/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.annoflex.app.Problems;
import org.annoflex.app.TextInfo;
import org.annoflex.app.dom.BufferIncrement;
import org.annoflex.app.dom.BufferStrategy;
import org.annoflex.app.dom.ConditionArea;
import org.annoflex.app.dom.Config;
import org.annoflex.app.dom.HeadingType;
import org.annoflex.app.dom.ImportInfo;
import org.annoflex.app.dom.InputMode;
import org.annoflex.app.dom.Member;
import org.annoflex.app.dom.MethodInfo;
import org.annoflex.app.dom.NoMatchAction;
import org.annoflex.app.dom.Options;
import org.annoflex.app.dom.TypeDescriptor;
import org.annoflex.app.dom.TypeException;
import org.annoflex.app.dom.Visibility;
import org.annoflex.jdt.dom.CommentValue;
import org.annoflex.jdt.dom.JOMComment;
import org.annoflex.jdt.dom.JOMCommentList;
import org.annoflex.jdt.dom.JOMCompilationUnit;
import org.annoflex.jdt.dom.JOMImport;
import org.annoflex.jdt.dom.JOMMethodDeclaration;
import org.annoflex.jdt.dom.JOMNode;
import org.annoflex.jdt.dom.JOMNodeType;
import org.annoflex.jdt.dom.JOMPackage;
import org.annoflex.jdt.dom.JOMTag;
import org.annoflex.jdt.dom.JOMTypeDeclaration;
import org.annoflex.jdt.dom.JOMTypeDeclarationBody;
import org.annoflex.jdt.parser.JavaParser;
import org.annoflex.regex.Condition;
import org.annoflex.regex.ConditionException;
import org.annoflex.regex.ConditionExpression;
import org.annoflex.regex.Expression;
import org.annoflex.regex.automaton.Rule;
import org.annoflex.regex.compiler.DefaultMacroResolver;
import org.annoflex.regex.compiler.RegExCompileException;
import org.annoflex.regex.compiler.RegExCompiler;
import org.annoflex.util.integer.IntHandler;
import org.annoflex.util.problem.ErrorHandler;
import org.annoflex.util.problem.Problem;
import org.annoflex.util.text.Section;
import org.annoflex.util.text.Slice;
import org.annoflex.util.text.Span;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class ConfigParser {
    
    //==================
    // Marker Constants
    //==================
    
    public static final char NAMESPACE_SYMBOL = '%';
    public static final String NAMESPACE_NAME = "LEX";
    
    public static final String LEX_MAIN_START = "%%LEX-MAIN-START%%";
    public static final String LEX_MAIN_END   = "%%LEX-MAIN-END%%";
    
    public static final String LEX_CONDITION_START = "%%LEX-CONDITION-START%%";
    public static final String LEX_CONDITION_END   = "%%LEX-CONDITION-END%%";
    
    //===============
    // Tag Constants
    //===============
    
    public static final String OPTION_TAG = "@option";
    public static final String MACRO_TAG = "@macro";
    public static final String EXPR_TAG = "@expr";
    
    //====================
    // Internal Constants
    //====================
    
    private static final IntHandler COMMA_HANDLER = StringToolkit
            .createHandler(',',false);
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private ErrorHandler<Span> errorHandler;
    
    /**
     * 
     */
    private boolean errors;
    
    /**
     * 
     */
    private final DefaultMacroResolver globalMacroResolver = new DefaultMacroResolver();
    
    /**
     * 
     */
    private final HashSet<JOMTag> usedTags = new HashSet<>();
    
    /**
     * 
     */
    private final MemberParser<Boolean> booleanParser = new MemberParser<>(
            new char[]{'+','-'},new Boolean[]{Boolean.TRUE,Boolean.FALSE});
    
    /**
     * 
     */
    private final MemberParser<Visibility> visibilityParser = new MemberParser<>(
            new char[]{'+','-','$','&'},new Visibility[]{Visibility.PUBLIC,
                    Visibility.PRIVATE,Visibility.PROTECTED,Visibility.PACKAGE_PRIVATE});
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setErrorHandler(ErrorHandler<Span> errorHandler) {
        this.errorHandler = errorHandler;
        
        booleanParser.setErrorHandler(errorHandler);
        visibilityParser.setErrorHandler(errorHandler);
    }
    
    /**
     * 
     */
    public ErrorHandler<Span> getErrorHandler() {
        return errorHandler;
    }
    
    /**
     * 
     */
    public boolean hasErrors() {
        return errors;
    }
    
    //===============
    // Parse Methods
    //===============
    
    /**
     * 
     */
    public Config parse(TextInfo textInfo) {
        errors = false;
        
        Config config = new Config(textInfo);
        String fileContent = textInfo.getText();
        
        // create AST for the source file
        JOMCompilationUnit compilationUnit = new JavaParser().parse(fileContent);
        JOMCommentList commentList = compilationUnit.getCommentList();
        
        // validate all comments
        validateComments(fileContent,commentList);
        
        if (errors) {
            return config;
        }
        
        // find the code area
        Section codeArea = findCodeArea(fileContent,commentList);
        
        if (codeArea == null) {
            return config;
        }
        
        config.setCodeArea(codeArea);
        
        // get the first type declaration
        JOMTypeDeclaration typeDecl = (JOMTypeDeclaration)compilationUnit
                .getChildByType(JOMNodeType.TYPE_DECLARATION,false);
        
        if (typeDecl == null) {
            errors |= Problems.NO_CLASS_DEFINED.report(errorHandler,
                    Span.EMPTY_SPAN);
            
            return config;
        }
        
        config.setTypeDeclaration(typeDecl.getName().getSourceRange());
        
        // collect condition areas
        collectConditionAreas(config,commentList);
        
        if (errors) {
            return config;
        }
        
        // Note: The following settings are always processed even if an error is
        // reported as they are considered less critical than the previous ones.
        
        // set prolog comment and package declaration
        config.setPrologComment(determinePrologComment(commentList,typeDecl));
        config.setPackageDeclaration(determinePackageDeclaration(compilationUnit));
        
        // collect imports
        collectImports(config,compilationUnit);
        
        // handle options
        setOptions(config.getOptions(),typeDecl,config);
        
        // handle global macros
        List<TagInfo> globalMacros = getTagInfoList(typeDecl.getJavaDoc(),
                MACRO_TAG,true,Problems.INVALID_MACRO,config);
        
        if (globalMacros != null) {
            RegExCompiler expressionCompiler = new RegExCompiler();
            expressionCompiler.setExcludeConditions(true);
            expressionCompiler.setMacroResolver(globalMacroResolver);
            
            setMacros(globalMacros,expressionCompiler,globalMacroResolver);
        }
        
        // handle rules
        setRules(config,typeDecl);
        
        // handle return type
        setReturnType(config);
        
        // report unused tags
        reportUnusedTags(commentList,config);
        
        return config;
    }
    
    //============================
    // Comment Validation Methods
    //============================
    
    /**
     * 
     */
    private void validateComments(String fileContent, JOMCommentList commentList) {
        JOMComment iterator = (JOMComment)commentList.getFirstChild();
        
        while (iterator != null) {
            if (iterator.isEndOfLineComment()) {
                CommentValue commentValue = iterator.getValue();
                
                if (hasLexNamespacePrefix(fileContent,commentValue) &&
                    !(fileContent.startsWith(LEX_MAIN_START,commentValue.start()) ||
                      fileContent.startsWith(LEX_MAIN_END,commentValue.start()) ||
                      fileContent.startsWith(LEX_CONDITION_START,commentValue.start()) ||
                      fileContent.startsWith(LEX_CONDITION_END,commentValue.start()))) {
                    
                    errors |= Problems.UNKNOWN_INSTRUCTION.report(errorHandler,
                            iterator.getSourceRange(true));
                }
            }
            
            iterator = (JOMComment)iterator.getNextSibling();
        }
    }
    
    /**
     * 
     */
    private boolean hasLexNamespacePrefix(String fileContent,
            CommentValue commentValue) {
        
        int iterator = commentValue.start();
        int length = fileContent.length();
        
        iterator = StringToolkit.skip(fileContent,iterator,length,
                StringToolkit.WHITESPACE,false);
        
        int newIterator = StringToolkit.skip(fileContent,iterator,
                length,NAMESPACE_SYMBOL,false);
        
        if (newIterator == iterator) {
            return false;
        }
        
        iterator = StringToolkit.skip(fileContent,newIterator,length,
                StringToolkit.WHITESPACE,false);
        
        newIterator = StringToolkit.skip(fileContent,iterator,
                NAMESPACE_NAME,true);
        
        return newIterator != iterator;
    }
    
    //===================
    // Code Area Methods
    //===================
    
    /**
     * 
     */
    @SuppressWarnings("null")
    private Section findCodeArea(String fileContent, JOMCommentList commentList) {
        JOMComment startComment = null;
        JOMComment endComment = null;
        JOMComment iterator = (JOMComment)commentList.getFirstChild();
        
        while (iterator != null) {
            if (iterator.isEndOfLineComment()) {
                CommentValue commentValue = iterator.getValue();
                
                if (fileContent.startsWith(LEX_MAIN_START,commentValue.start())) {
                    int suffixStart = commentValue.start() + LEX_MAIN_START.length();
                    
                    if ((commentValue.end() > suffixStart) || (startComment != null)) {
                        errors |= Problems.INVALID_CODE_AREA.report(errorHandler,
                                iterator.getSourceRange(true));
                        
                        return null;
                    }
                    
                    // Note: An end marker can not be present at this location
                    // as this end marker would have been already detected as an
                    // invalid marker as it occurs without a leading start
                    // marker.
                    
                    startComment = iterator;
                }
                
                else if (fileContent.startsWith(LEX_MAIN_END,commentValue.start())) {
                    int suffixStart = commentValue.start() + LEX_MAIN_END.length();
                    
                    if ((commentValue.end() > suffixStart) || (startComment == null) ||
                        (endComment != null)) {
                        
                        errors |= Problems.INVALID_CODE_AREA.report(errorHandler,
                                iterator.getSourceRange(true));
                        
                        return null;
                    }
                    
                    endComment = iterator;
                }
            }
            
            iterator = (JOMComment)iterator.getNextSibling();
        }
        
        if ((startComment == null) && (endComment == null)) {
            errors |= Problems.NO_CODE_AREA_DEFINED.report(errorHandler,
                    Span.EMPTY_SPAN);
            
            return null;
        }
        
        if ((startComment == null) || (endComment == null)) {
            errors |= Problems.INVALID_CODE_AREA.report(errorHandler,
                    startComment != null ? startComment.getSourceRange(true) :
                        endComment.getSourceRange(true));
            
            return null;
        }
        
        return new Section(startComment.getSourceRange(),endComment.getSourceRange());
    }
    
    /**
     * 
     */
    private boolean insideCodeArea(int position, Config config) {
        Section codeArea = config.getCodeArea();
        
        return (position >= codeArea.getStart().end) &&
               (position <= codeArea.getEnd().start);
    }
    
    //========================
    // Condition Area Methods
    //========================
    
    /**
     * 
     */
    private void collectConditionAreas(Config config, JOMCommentList commentList) {
        String fileContent = config.getTextInfo().getText();
        JOMComment iterator = (JOMComment)commentList.getFirstChild();
        JOMComment startComment = null;
        Condition condition = null;
        
        while (iterator != null) {
            if (iterator.isEndOfLineComment()) {
                CommentValue commentValue = iterator.getValue();
                
                if (fileContent.startsWith(LEX_CONDITION_START,commentValue.start())) {
                    if (insideCodeArea(commentValue.start(),config)) {
                        errors |= Problems.INSIDE_CODE_AREA.report(errorHandler,
                                iterator.getSourceRange(true));
                        return;
                    }
                    
                    Condition curCondition = extractCondition(fileContent,commentValue);
                    
                    if ((curCondition == null) || (startComment != null)) {
                        errors |= Problems.INVALID_CONDITION_AREA.report(errorHandler,
                                iterator.getSourceRange(true));
                        return;
                    }
                    
                    startComment = iterator;
                    condition = curCondition;
                }
                
                else if (fileContent.startsWith(LEX_CONDITION_END,commentValue.start())) {
                    if (insideCodeArea(commentValue.start(),config)) {
                        errors |= Problems.INSIDE_CODE_AREA.report(errorHandler,
                                iterator.getSourceRange(true));
                        return;
                    }
                    
                    int suffixStart = commentValue.start() + LEX_CONDITION_END.length();
                    
                    if ((commentValue.end() > suffixStart) || (startComment == null)) {
                        errors |= Problems.INVALID_CONDITION_AREA.report(errorHandler,
                                iterator.getSourceRange(true));
                        return;
                    }
                    
                    Span sectionStart = startComment.getSourceRange();
                    Span sectionEnd = iterator.getSourceRange();
                    Section section = new Section(sectionStart,sectionEnd);
                    config.getConditionAreas().add(new ConditionArea(section,condition));
                    
                    startComment = null;
                    condition = null;
                }
            }
            
            iterator = (JOMComment)iterator.getNextSibling();
        }
        
        if (startComment != null) {
            errors |= Problems.INVALID_CONDITION_AREA.report(errorHandler,
                    startComment.getSourceRange(true));
        }
    }
    
    /**
     * 
     */
    private Condition extractCondition(String fileContent,
            CommentValue commentValue) {
        
        int start = commentValue.start() + LEX_CONDITION_START.length();
        int end = commentValue.end();
        
        if (((end - start) > 2) &&
            StringToolkit.equals(fileContent,end-2,end,"%%")) {
            
            String[] nameList = StringToolkit.split(fileContent,start,end-2,',');
            
            try {
                return Condition.create(nameList);
            }
            
            catch(ConditionException e) {
            }
        }
        
        return null;
    }
    
    /**
     * 
     */
    private ConditionArea findConditionArea(Config config, int position) {
        ArrayList<ConditionArea> conditionAreas = config.getConditionAreas();
        int size = conditionAreas.size();
        int low = 0;
        int high = size - 1;
        
        while (low <= high) {
            int mid = (low + high) / 2;
            
            ConditionArea conditionArea = conditionAreas.get(mid);
            Section section = conditionArea.getSection();
            
            if (position < section.getStart().start) {
                high = mid - 1;
            }
            
            else if (position >= section.getEnd().end) {
                low = mid + 1;
            }
            
            else {
                return conditionArea;
            }
        }
        
        return null;
    }
    
    //========================
    // Prolog Comment Methods
    //========================
    
    /**
     * 
     */
    private Span determinePrologComment(JOMCommentList commentList,
            JOMTypeDeclaration typeDecl) {
        
        JOMComment firstComment = (JOMComment)commentList.getFirstChild();
        
        if ((firstComment != null) && (firstComment.getSourceStart() == 0) &&
            (firstComment.isTraditionalComment() ||
             firstComment.isDocumentationComment()) &&
             (firstComment != typeDecl.getJavaDoc())) {
            
            return firstComment.getSourceRange();
        }
        
        return null;
    }
    
    //=============================
    // Package Declaration Methods
    //=============================
    
    /**
     * 
     */
    private Span determinePackageDeclaration(JOMCompilationUnit compilationUnit) {
        JOMPackage packageNode = compilationUnit.getPackage();
        
        return packageNode != null ? packageNode.getSourceRange() : null;
    }
    
    //================
    // Import Methods
    //================
    
    /**
     * 
     */
    private void collectImports(Config config, JOMCompilationUnit compilationUnit) {
        ArrayList<JOMImport> fileImportList = compilationUnit.collectImports();
        
        if (fileImportList != null) {
            int size = fileImportList.size();
            
            for (int i=0;i<size;i++) {
                JOMImport curImport = fileImportList.get(i);
                
                TypeDescriptor typeDescriptor = TypeDescriptor.create(
                        curImport.getNameTextParts());
                boolean isStatic = curImport.getIsStatic();
                boolean onDemand = curImport.getOnDemand();
                Span sourceRange = curImport.getSourceRange();
                ImportInfo importInfo = new ImportInfo(typeDescriptor,isStatic,onDemand,sourceRange);
                
                config.getImports().add(importInfo);
            }
        }
    }
    
    //================
    // Option Methods
    //================
    
    /**
     * 
     */
    private void setOptions(Options options, JOMTypeDeclaration typeDecl,
            Config config) {
        
        List<TagInfo> optionTags =  getTagInfoList(typeDecl.getJavaDoc(),
                OPTION_TAG,true,Problems.INVALID_OPTION,config);
        
        if (optionTags != null) {
            
            // first pass
            for (int i=0;i<optionTags.size();i++) {
                TagInfo curOptionInfo = optionTags.get(i);
                
                if (curOptionInfo.name.equals(Options.INTERNAL)) {
                    putOption(options,curOptionInfo);
                }
            }
            
            // handle options of first pass
            handleInternalOption(options);
            
            // second pass
            for (int i=0;i<optionTags.size();i++) {
                TagInfo curOptionInfo = optionTags.get(i);
                
                if (!curOptionInfo.name.equals(Options.INTERNAL)) {
                    putOption(options,curOptionInfo);
                }
            }
        }
    }
    
    /**
     * 
     */
    private void putOption(Options options, TagInfo optionInfo) {
        JOMTag optionTag = optionInfo.tag;
        String name = optionInfo.name;
        String value = optionInfo.value;
        Span valueSpan = optionInfo.valueSpan;
        
        switch(name) {
        case Options.LOGO:
            Boolean logoValue = parseBooleanOption(optionTag,value,valueSpan);
            
            if (logoValue != null) {
                options.setLogo(logoValue);
            }
            break;
        
        case Options.STATISTICS:
            Boolean statisticsValue = parseBooleanOption(optionTag,value,valueSpan);
            
            if (statisticsValue != null) {
                options.setStatistics(statisticsValue);
            }
            break;
        
        case Options.HEADINGS:
            options.setHeadings(parseHeadingType(optionTag,value,valueSpan));
            break;
        
        case Options.METHOD_NAME:
            options.setMethodName(parseMethodName(optionTag,value,valueSpan));
            break;
        
        case Options.METHOD_THROWS:
            parseMethodThrows(options,optionTag,value,valueSpan);
            break;
        
        case Options.DEFAULT_RETURN_VALUE:
            options.setDefaultReturnValue(parseDefaultReturnValue(optionTag,value,valueSpan));
            break;
        
        case Options.INPUT_MODE:
            options.setInputMode(parseInputMode(optionTag,value,valueSpan));
            break;
        
        case Options.BUFFER_STRATEGY:
            options.setBufferStrategy(parseBufferStrategy(optionTag,value,valueSpan));
            break;
        
        case Options.BUFFER_INCREMENT:
            options.setBufferIncrement(parseBufferIncrement(optionTag,value,valueSpan));
            break;
        
        case Options.FUNCTIONALITY:
            booleanParser.putValues(options.getFunctionalityMap(),optionTag,value,valueSpan);
            errors |= booleanParser.hasErrors();
            break;
        
        case Options.JAVADOC:
            booleanParser.putValues(options.getJavaDocMap(),optionTag,value,valueSpan);
            errors |= booleanParser.hasErrors();
            break;
        
        case Options.VISIBILITY:
            visibilityParser.putValues(options.getVisibilityMap(),optionTag,value,valueSpan);
            errors |= visibilityParser.hasErrors();
            break;
        
        case Options.INTERNAL:
            booleanParser.putValues(options.getInternalMap(),optionTag,value,valueSpan);
            errors |= booleanParser.hasErrors();
            break;
        
        case Options.NO_MATCH_ACION:
            options.setNoMatchAction(parseNoMatchAction(optionTag,value,valueSpan));
            break;
        
        default:
            errors |= Problems.UNKNOWN_OPTION.report(errorHandler,
                    optionTag.getSliceSourceRange(optionInfo.nameSpan));
        }
    }
    
    /**
     * 
     */
    private Boolean parseBooleanOption(JOMTag optionTag, String value, Span valueSpan) {
        Boolean booleanValue = parseBooleanValue(value);
        
        if (booleanValue == null) {
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    optionTag.getSliceSourceRange(valueSpan));
        }
        
        return booleanValue;
    }
    
    /**
     * 
     */
    private Boolean parseBooleanValue(String value) {
        switch(value) {
        case "enabled": return Boolean.TRUE;
        case "disabled": return Boolean.FALSE;
        }
        
        return null;
    }
    
    /**
     * 
     */
    private HeadingType parseHeadingType(JOMTag optionTag, String value, Span valueSpan) {
        HeadingType headingType = HeadingType.forName(value);
        
        if (headingType == null) {
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    optionTag.getSliceSourceRange(valueSpan));
        }
        
        return headingType;
    }
    
    /**
     * 
     */
    private String parseMethodName(JOMTag optionTag, String value,
            Span valueSpan) {
        
        if (StringToolkit.isJavaIdentifier(value)) {
            return value;
        }
        
        errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                optionTag.getSliceSourceRange(valueSpan));
        
        return null;
    }
    
    /**
     * 
     */
    private void parseMethodThrows(Options options,
            JOMTag optionTag, String value, Span valueSpan) {
        
        Span[] subSpans = StringToolkit.split(value,COMMA_HANDLER);
        
        for (int i=0;i<subSpans.length;i++) {
            Span curSpan = subSpans[i].trim(value);
            String curValue = curSpan.substring(value);
            
            try {
                String content = extractLink(curValue,optionTag,curSpan);
                
                if (content != null) {
                    options.getMethodThrows().add(TypeDescriptor.create(content));
                }
            }
            
            catch(TypeException e) {
                errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                        optionTag.getSliceSourceRange(curSpan.makeAbsoluteTo(valueSpan)));
                break;
            }
        }
    }
    
    /**
     * 
     */
    private String parseDefaultReturnValue(JOMTag optionTag, String value,
            Span valueSpan) {
        
        String content = extractLink(value,optionTag,valueSpan);
        
        if (content == null) {
            return null;
        }
        
        if (content != value) {
            int index = content.indexOf('#');
            
            if (index != -1) {
                return content.substring(0,index)+"."+content.substring(index+1);
            }
        }
        
        return content;
    }
    
    /**
     * 
     */
    private String extractLink(String value, JOMTag optionTag, Span optionSpan) {
        String newValue = extractSingleLink("{@link",value,optionTag,optionSpan);
        
        if (newValue != value) {
            return newValue;
        }
        
        return extractSingleLink("{@linkplain",value,optionTag,optionSpan);
    }
    
    /**
     * 
     */
    private String extractSingleLink(String linkName, String value,
            JOMTag optionTag, Span optionSpan) {
        
        if (value.startsWith(linkName)) {
            if (value.endsWith("}")) {
                return value.substring(linkName.length(),value.length()-1).trim();
            }
            
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    optionTag.getSliceSourceRange(optionSpan));
            
            return null;
        }
        
        return value;
    }
    
    /**
     * 
     */
    private InputMode parseInputMode(JOMTag optionTag, String value, Span valueSpan) {
        InputMode inputMode = InputMode.forName(value);
        
        if (inputMode == null) {
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    optionTag.getSliceSourceRange(valueSpan));
        }
        
        return inputMode;
    }
    
    /**
     * 
     */
    private BufferStrategy parseBufferStrategy(JOMTag optionTag, String value, Span valueSpan) {
        BufferStrategy bufferStrategy = BufferStrategy.forName(value);
        
        if (bufferStrategy == null) {
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    optionTag.getSliceSourceRange(valueSpan));
        }
        
        return bufferStrategy;
    }
    
    /**
     * 
     */
    private BufferIncrement parseBufferIncrement(JOMTag optionTag, String value, Span valueSpan) {
        BufferIncrement bufferIncrement = BufferIncrement.forName(value);
        
        if (bufferIncrement == null) {
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    optionTag.getSliceSourceRange(valueSpan));
        }
        
        return bufferIncrement;
    }
    
    /**
     * 
     */
    private void handleInternalOption(Options options) {
        List<Member> internalMembers = options.getInternalMap().getAllKeys(Boolean.TRUE);
        
        if (internalMembers != null) {
            int size = internalMembers.size();
            
            for (int i=0;i<size;i++) {
                options.getVisibilityMap().put(internalMembers.get(i),Visibility.PRIVATE);
            }
        }
    }
    
    /**
     * 
     */
    private NoMatchAction parseNoMatchAction(JOMTag optionTag, String value, Span valueSpan) {
        NoMatchAction noMatchAction = NoMatchAction.forName(value);
        
        if (noMatchAction == null) {
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    optionTag.getSliceSourceRange(valueSpan));
        }
        
        return noMatchAction;
    }
    
    //===============
    // Macro Methods
    //===============
    
    /**
     * 
     */
    private void setMacros(List<TagInfo> macroTags, RegExCompiler expressionCompiler,
            DefaultMacroResolver macroResolver) {
        
        int size = macroTags.size();
        
        for (int i=0;i<size;i++) {
            appendMacro(macroTags.get(i),expressionCompiler,macroResolver);
        }
    }
    
    /**
     * 
     */
    private void appendMacro(TagInfo macroTagInfo, RegExCompiler expressionCompiler,
            DefaultMacroResolver macroResolver) {
        
        if (!StringToolkit.isASCIIIdentifier(macroTagInfo.name)) {
            errors |= Problems.INVALID_MACRO_NAME.report(errorHandler,
                    macroTagInfo.tag.getSliceSourceRange(macroTagInfo.nameSpan));
            return;
        }
        
        // compile expression
        Expression expression = null;
        
        try {
            expression = expressionCompiler.compile(macroTagInfo.value);
        }
        
        catch(RegExCompileException e) {
            
            // the usage of invalid macros may only be skipped if the invalid
            // macro has been reported as an error
            if ((e.getType() != RegExCompileException.INVALID_MACRO) || !errors) {
                errors |= Problems.INVALID_MACRO_VALUE.report(errorHandler,
                        macroTagInfo.tag.getSliceSourceRange(macroTagInfo.valueSpan));
            }
        }
        
        // Put expression even if null in order to know that the macro
        // has been defined. This is necessary in order to be able to
        // distinguish between undefined macros and defined but invalid
        // macros.
        macroResolver.put(macroTagInfo.name,expression);
    }
    
    //==============
    // Rule Methods
    //==============
    
    /**
     * 
     */
    private void setRules(Config config, JOMTypeDeclaration typeDecl) {
        JOMTypeDeclarationBody typeDeclarationBody = typeDecl.getTypeDeclarationBody();
        JOMNode iterator = typeDeclarationBody.getFirstChild();
        
        while (iterator != null) {
            if (iterator.isMethodDeclaration()) {
                JOMMethodDeclaration methodDecl = (JOMMethodDeclaration)iterator;
                List<TagInfo> exprTags = getTagInfoList(methodDecl.getJavaDoc(),
                        EXPR_TAG,false,Problems.INVALID_EXPRESSION,config);
                
                if (exprTags != null) {
                    addRule(methodDecl,exprTags,config);
                }
            }
            
            iterator = iterator.getNextSibling();
        }
        
        if (!errors && config.getRuleList().isEmpty()) {
            errors |= Problems.NO_RULES_DEFINED.report(errorHandler,
                    typeDecl.getName().getSourceRange());
        }
    }
    
    /**
     * 
     */
    private void addRule(JOMMethodDeclaration methodDecl,
            List<TagInfo> exprTags, Config config) {
        
        // check for multiple expr tags
        if (exprTags.size() != 1) {
            for (int j=1;j<exprTags.size();j++) {
                errors |= Problems.TOO_MANY_EXPR_TAGS.report(errorHandler,
                        exprTags.get(j).tag.getSourceRange());
            }
            
            return;
        }
        
        // process local macros
        DefaultMacroResolver localMacroResolver = new DefaultMacroResolver(
                globalMacroResolver);
        
        RegExCompiler expressionCompiler = new RegExCompiler();
        expressionCompiler.setExcludeConditions(true);
        expressionCompiler.setMacroResolver(localMacroResolver);
        
        List<TagInfo> localMacros = getTagInfoList(methodDecl.getJavaDoc(),
                MACRO_TAG,true,Problems.INVALID_MACRO,config);
        
        if (localMacros != null) {
            setMacros(localMacros,expressionCompiler,localMacroResolver);
        }
        
        // compile expression and create rule
        TagInfo exprInfo = exprTags.get(0);
        Slice exprSlice = exprInfo.tag.getSlice();
        
        if (exprSlice != null) {
            
            // compile expression
            Expression expression = null;
            
            try {
                expressionCompiler.setExcludeConditions(false);
                
                expression = expressionCompiler.compile(exprInfo.value);
            }
            
            catch(RegExCompileException e) {
                
                // the usage of invalid macros may only be skipped if the invalid
                // macro has been reported as an error
                if ((e.getType() != RegExCompileException.INVALID_MACRO) || !errors) {
                    errors |= Problems.INVALID_EXPRESSION.report(errorHandler,
                            exprInfo.tag.getSliceSourceRange(exprSlice.trim()));
                }
                
                // Processing must always be stopped even if the macro is
                // invalid. Invalid macros are not reported only for
                // usability reasons.
                return;
            }
            
            // report empty word expression
            if (expression == null) {
                errors |= Problems.INVALID_EXPRESSION.report(errorHandler,
                        exprInfo.tag.getSourceRange());
                return;
            }
            
            // add condition of surrounding condition area
            ConditionArea conditionArea = findConditionArea(config,exprInfo.tag
                    .getSourceStart());
            
            if (conditionArea != null) {
                expression = new ConditionExpression(conditionArea
                        .getCondition(),expression);
            }
            
            // create method info
            MethodInfo methodInfo = new MethodInfo(
                    methodDecl.getName().getText(),
                    methodDecl.getReturnType().getText(),
                    methodDecl.getReturnType().getSourceRange(),
                    exprInfo.tag.getSliceSourceRange(exprSlice.trim()));
            
            // add rule
            config.getRuleList().add(new Rule<>(expression,methodInfo));
        }
    }
    
    //=====================
    // Return Type Methods
    //=====================
    
    /**
     * 
     */
    private void setReturnType(Config config) {
        ArrayList<Rule<MethodInfo>> ruleList = config.getRuleList();
        int ruleListSize = ruleList.size();
        
        if (ruleListSize > 0) {
            
            // create sorted return type info list
            LinkedHashMap<String,ReturnTypeInfo> infoMap = new LinkedHashMap<>();
            
            for (int i=0;i<ruleListSize;i++) {
                Rule<MethodInfo> curRule = ruleList.get(i);
                String curReturnType = curRule.getAction().getReturnType();
                ReturnTypeInfo curReturnTypeInfo = infoMap.get(curReturnType);
                
                if (curReturnTypeInfo == null) {
                    curReturnTypeInfo = new ReturnTypeInfo(curReturnType);
                    infoMap.put(curReturnType,curReturnTypeInfo);
                }
                
                curReturnTypeInfo.count++;
            }
            
            ArrayList<ReturnTypeInfo> sortedInfoList = new ArrayList<>(infoMap.values());
            Collections.sort(sortedInfoList);
            
            // determine return type and the flag for additional void methods
            int returnTypeCount = sortedInfoList.size();
            
            if (returnTypeCount == 1) {
                config.setReturnType(sortedInfoList.get(0).returnType);
            }
            
            else if (returnTypeCount >= 2) {
                ReturnTypeInfo firstInfo = sortedInfoList.get(0);
                ReturnTypeInfo secondInfo = sortedInfoList.get(1);
                
                // determine return type
                String returnType = isVoidType(firstInfo.returnType) ?
                        secondInfo.returnType : firstInfo.returnType;
                
                config.setReturnType(returnType);
                
                // determine flag for additional void type
                for (int i=0;i<returnTypeCount;i++) {
                    if (isVoidType(sortedInfoList.get(i).returnType)) {
                        config.setHasAlsoVoidReturnType(true);
                        break;
                    }
                }
                
                // report multiple non-void return types
                for (int i=0;i<ruleListSize;i++) {
                    Rule<MethodInfo> curRule = ruleList.get(i);
                    String curReturnType = curRule.getAction().getReturnType();
                    
                    if (!curReturnType.equals(returnType) && !isVoidType(curReturnType)) {
                        errors |= Problems.AMBIGUOUS_RETURN_TYPES.report(errorHandler,
                                curRule.getAction().getReturnTypeSpan());
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    private boolean isVoidType(String type) {
        return (type != null) && type.equals("void");
    }
    
    //=============
    // Tag Methods
    //=============
    
    /**
     * 
     */
    private List<TagInfo> getTagInfoList(JOMComment comment, String tagName,
            boolean hasName, Problem problem, Config config) {
        
        if (comment != null) {
            List<JOMTag> tagList = comment.getTagsByName(tagName);
            
            if (tagList != null) {
                usedTags.addAll(tagList);
                
                int tagListSize = tagList.size();
                List<TagInfo> infoList = new ArrayList<>();
                
                for (int i=0;i<tagListSize;i++) {
                    putTagInfo(infoList,tagList.get(i),hasName,problem,config);
                }
                
                return infoList;
            }
        }
        
        return null;
    }
    
    /**
     * 
     */
    private void putTagInfo(List<TagInfo> infoList, JOMTag tag, boolean hasName,
            Problem problem, Config config) {
        
        Slice slice = tag.getSlice();
        String sliceText = slice.getText();
        
        if (insideCodeArea(tag.getSourceStart(),config)) {
            errors |= Problems.INSIDE_CODE_AREA.report(errorHandler,
                    tag.getSourceRange());
            return;
        }
        
        // tags of type "@tag name = value"
        if (hasName) {
            int index = sliceText.indexOf('=');
            
            if (index == -1) {
                errors |= problem.report(errorHandler,
                        tag.getSliceSourceRange(slice.trim()));
                return;
            }
            
            Span nameSpan = slice.trim(0,index);
            Span valueSpan = slice.trim(index+1,slice.getText().length());
            
            if ((nameSpan.length() == 0) || (valueSpan.length() == 0)) {
                errors |= problem.report(errorHandler,
                        tag.getSliceSourceRange(slice.trim()));
                return;
            }
            
            String name = nameSpan.substring(sliceText);
            String value = valueSpan.substring(sliceText);
            
            infoList.add(new TagInfo(tag,nameSpan,name,valueSpan,value));
        }
        
        // tags of type "@tag value"
        else {
            Span valueSpan = slice.trim(0,slice.getText().length());
            
            if (valueSpan.length() == 0) {
                errors |= problem.report(errorHandler,tag.getSourceRange());
                return;
            }
            
            String value = valueSpan.substring(sliceText);
            
            infoList.add(new TagInfo(tag,null,null,valueSpan,value));
        }
    }
    
    //========================
    // Tag Validation Methods
    //========================
    
    /**
     * 
     */
    private void reportUnusedTags(JOMCommentList commentList, Config config) {
        JOMComment curComment = (JOMComment)commentList.getFirstChild();
        
        while (curComment != null) {
            JOMTag curTag = (JOMTag)curComment.getFirstChild();
            
            while (curTag != null) {
                if (isRelevantTag(curTag) && !usedTags.contains(curTag)) {
                    if (insideCodeArea(curTag.getSourceStart(),config)) {
                        errors |= Problems.INSIDE_CODE_AREA.report(errorHandler,
                                curTag.getSourceRange());
                    }
                    
                    else {
                        errors |= Problems.UNUSED_TAG.report(errorHandler,
                                curTag.getSourceRange());
                    }
                }
                
                curTag = (JOMTag)curTag.getNextSibling();
            }
            
            curComment = (JOMComment)curComment.getNextSibling();
        }
    }
    
    /**
     * 
     */
    private boolean isRelevantTag(JOMTag tag) {
        if (tag != null) {
            String name = tag.getName();
            
            if (name != null) {
                switch(name) {
                case OPTION_TAG:
                case MACRO_TAG:
                case EXPR_TAG:
                    return true;
                }
            }
        }
        
        return false;
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class TagInfo {
        
        final JOMTag tag;
        final Span nameSpan;
        final String name;
        final Span valueSpan;
        final String value;
        
        /**
         * 
         */
        public TagInfo(JOMTag optionTag, Span nameSpan, String name,
                Span valueSpan, String value) {
            
            this.tag = optionTag;
            this.nameSpan = nameSpan;
            this.name = name;
            this.valueSpan = valueSpan;
            this.value = value;
        }
    }
    
    /**
     * 
     */
    static final class ReturnTypeInfo implements Comparable<ReturnTypeInfo> {
        
        final String returnType;
        int count;
        
        /**
         * 
         */
        public ReturnTypeInfo(String returnType) {
            this.returnType = returnType;
        }
        
        /**
         * 
         */
        public int compareTo(ReturnTypeInfo o) {
            return o.count - count;
        }
    }
}
