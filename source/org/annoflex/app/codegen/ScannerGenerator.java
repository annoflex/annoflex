/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.codegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.annoflex.app.dom.BooleanMap;
import org.annoflex.app.dom.BufferIncrement;
import org.annoflex.app.dom.BufferStrategy;
import org.annoflex.app.dom.Config;
import org.annoflex.app.dom.FunctionalityMap;
import org.annoflex.app.dom.InputMode;
import org.annoflex.app.dom.Member;
import org.annoflex.app.dom.MethodInfo;
import org.annoflex.app.dom.NoMatchAction;
import org.annoflex.app.dom.Options;
import org.annoflex.app.dom.TypeDescriptor;
import org.annoflex.app.dom.VisibilityMap;
import org.annoflex.jdt.codegen.JavaPrinter;
import org.annoflex.regex.Condition;
import org.annoflex.regex.ExpressionType;
import org.annoflex.regex.automaton.Action;
import org.annoflex.regex.automaton.ActionPool;
import org.annoflex.regex.automaton.Rule;
import org.annoflex.util.SystemToolkit;

/**
 * @author Stefan Czaska
 */
public class ScannerGenerator extends JavaPrinter {
    
    //===========
    // Constants
    //===========
    
    private static final TypeDescriptor TYPE_IO_EXCEPTION =
            TypeDescriptor.create("java.io.IOException");
    
    private static final TypeDescriptor TYPE_READER =
            TypeDescriptor.create("java.io.Reader");
    
    private static final TypeDescriptor TYPE_ILLEGAL_STATE_EXCEPTION =
            TypeDescriptor.create("java.lang.IllegalStateException");
    
    //==============
    // Input Fields
    //==============
    
    /**
     * 
     */
    private Config config;
    
    /**
     * 
     */
    private AutomatonInfo automatonInfo;
    
    //=====================
    // Cached Input Fields
    //=====================
    
    /**
     * 
     */
    private boolean isReaderMode;
    
    /**
     * 
     */
    private boolean isCurMatchStrategy;
    
    /**
     * 
     */
    private boolean isGoldenRatioIncrement;
    
    /**
     * 
     */
    private boolean hasLexicalStates;
    
    /**
     * 
     */
    private FunctionalityMap functionalityMap;
    
    /**
     * 
     */
    private VisibilityMap visibilityMap;
    
    /**
     * 
     */
    private BooleanMap internalMap;
    
    /**
     * 
     */
    private ArrayList<TypeDescriptor> methodThrows;
    
    /**
     * 
     */
    private NoMatchAction noMatchAction;
    
    //===============
    // Result Fields
    //===============
    
    /**
     * 
     */
    private HashSet<TypeDescriptor> imports;
    
    //====================
    // Generation Methods
    //====================
    
    /**
     * 
     */
    public String generate(Config config, AutomatonInfo automatonInfo) {
        this.config = config;
        this.automatonInfo = automatonInfo;
        
        Options options = config.getOptions();
        
        isReaderMode = options.getInputMode() == InputMode.READER;
        isCurMatchStrategy = isReaderMode && options.getBufferStrategy() ==
                BufferStrategy.CURRENT_MATCH;
        isGoldenRatioIncrement = options.getBufferIncrement() ==
                BufferIncrement.GOLDEN_RATIO;
        hasLexicalStates = automatonInfo.getNameMap().size() > 1;
        functionalityMap = options.getFunctionalityMap();
        visibilityMap = options.getVisibilityMap();
        internalMap = options.getInternalMap();
        methodThrows = options.getMethodThrows();
        noMatchAction = options.getNoMatchAction();
        
        generateStatistics();
        generateTableConstants();
        generateLexicalStateConstants();
        generateHelperConstants();
        generateFields();
        generateTableMethods();
        generatePropertyMethods();
        generateScanMethods();
        generateHelperMethods();
        ensureEmptyLine(1);
        
        if (isReaderMode) {
            addImport(TYPE_IO_EXCEPTION);
            addImport(TYPE_READER);
        }
        
        for (int i=0;i<methodThrows.size();i++) {
            TypeDescriptor curThrowable = methodThrows.get(i);
            
            if (curThrowable.getComponentCount() > 1) {
                addImport(curThrowable);
            }
        }
        
        return toString();
    }
    
    //================
    // Result Methods
    //================
    
    /**
     * 
     */
    public HashSet<TypeDescriptor> getImports() {
        return imports;
    }
    
    /**
     * 
     */
    private void addImport(TypeDescriptor type) {
        if (imports == null) {
            imports = new HashSet<>();
        }
        
        imports.add(type);
    }
    
    //====================
    // Statistics Methods
    //====================
    
    /**
     * 
     */
    private void generateStatistics() {
        if (config.getOptions().getLogo()) {
            ensureEmptyLine(1);
            
            line(1,"//================================================");
            line(1,"//     _                      _____ _             ");
            line(1,"//    / \\   _ __  _ __   ___ |  ___| | ___ _  __  ");
            line(1,"//   / _ \\ |  _ \\|  _ \\ / _ \\| |_  | |/ _ \\ \\/ /  ");
            line(1,"//  / ___ \\| | | | | | | (_) |  _| | |  __/>  <   ");
            line(1,"// /_/   \\_\\_| |_|_| |_|\\___/|_|   |_|\\___/_/\\_\\  ");
            line(1,"//                                                ");
            line(1,"//================================================");
        }
        
        if (config.getOptions().getStatistics()) {
            ensureEmptyLine(1);
            
            line(1,"/*************************************************");
            line(1," *             Generation Statistics             *");
            line(1," * * * * * * * * * * * * * * * * * * * * * * * * *");
            line(1," *                                               *");
            line(1," * Rules:           "+createValue(config.getRuleList().size())+"*");
            line(1," * Lookaheads:      "+createValue(determineLookaheadCount())+"*");
            line(1," * Alphabet length: "+createValue(automatonInfo.getAlphabetLength())+"*");
            line(1," * NFA states:      "+createValue(automatonInfo.getTotalNFAStateCount())+"*");
            line(1," * DFA states:      "+createValue(automatonInfo.getTotalDFAStateCount())+"*");
            line(1," * Static size:     "+createValue(computeStaticSize(automatonInfo))+"*");
            line(1," * Instance size:   "+createValue(computeInstanceSize(automatonInfo))+"*");
            line(1," *                                               *");
            line(1," ************************************************/");
        }
    }
    
    /**
     * 
     */
    private int determineLookaheadCount() {
        int lookaheadCount = 0;
        ArrayList<Rule<MethodInfo>> ruleList = config.getRuleList();
        
        for (int i=0;i<ruleList.size();i++) {
            lookaheadCount += ruleList.get(i).getExpression().getTypeCount(
                    ExpressionType.LOOKAHEAD);
        }
        
        return lookaheadCount;
    }
    
    /**
     * 
     */
    private String computeStaticSize(AutomatonInfo automatonInfo) {
        int sizeOfCharacterMap = SystemToolkit.SIZE_OF_REFERENCE +
                (automatonInfo.isSmallCharacterMap() ?
                        SystemToolkit.sizeOfByteArray(automatonInfo.getCharacterMapSize()) :
                        SystemToolkit.sizeOfShortArray(automatonInfo.getCharacterMapSize()));
        
        int stateCount = automatonInfo.getTotalDFAStateCount();
        int sizeOfTransitionTable = SystemToolkit.SIZE_OF_REFERENCE +
                SystemToolkit.sizeOfObjectArray(stateCount) +
                (stateCount * (automatonInfo.isSmallTransitionTable() ?
                        SystemToolkit.sizeOfByteArray(automatonInfo.getAlphabetLength()) :
                        SystemToolkit.sizeOfShortArray(automatonInfo.getAlphabetLength())));
        
        int sizeOfActionMap = SystemToolkit.SIZE_OF_REFERENCE +
                (automatonInfo.isSmallActionMap() ?
                        SystemToolkit.sizeOfByteArray(automatonInfo.getActionMapSize()) :
                        SystemToolkit.sizeOfShortArray(automatonInfo.getActionMapSize()));
        
        int sizeOfLexicalStateConstants = 0;
        
        if (hasLexicalStates) {
            sizeOfLexicalStateConstants += automatonInfo.getNameMap().size() *
                    SystemToolkit.SIZE_OF_INTEGER;
        }
        
        int sizeOfHelperConstants = 0;
        
        if (isReaderMode) {
            sizeOfHelperConstants += SystemToolkit.sizeOfCharArray(0);
        }
        
        return getSizeString(sizeOfCharacterMap + sizeOfTransitionTable +
                sizeOfActionMap + sizeOfLexicalStateConstants +
                sizeOfHelperConstants);
    }
    
    /**
     * 
     */
    private String computeInstanceSize(AutomatonInfo automatonInfo) {
        int sizeOfStringFields = 0;
        int sizeOfRegionFields = 0;
        int sizeOfReaderFields = 0;
        int sizeOfBufferFields = 0;
        
        if (isReaderMode) {
            sizeOfReaderFields += SystemToolkit.SIZE_OF_REFERENCE +
                    SystemToolkit.SIZE_OF_INTEGER;
            
            sizeOfBufferFields += SystemToolkit.SIZE_OF_REFERENCE +
                    SystemToolkit.SIZE_OF_INTEGER * 2;
        }
        
        else {
            sizeOfStringFields += SystemToolkit.SIZE_OF_REFERENCE;
            sizeOfRegionFields += SystemToolkit.SIZE_OF_INTEGER;
            
            if (functionalityMap.hasRegionStartFieldReadAccess()) {
                sizeOfRegionFields += SystemToolkit.SIZE_OF_INTEGER;
            }
        }
        
        int sizeOfDotFields = SystemToolkit.SIZE_OF_INTEGER;
        int sizeOfLexicalStateFields = 0;
        
        if (hasLexicalStates) {
            sizeOfLexicalStateFields += SystemToolkit.SIZE_OF_INTEGER;
        }
        
        int sizeOfMatchFields = 0;
        
        if (functionalityMap.hasMatchStartFieldReadAccess(isCurMatchStrategy)) {
            sizeOfMatchFields += SystemToolkit.SIZE_OF_INTEGER;
        }
        
        if (functionalityMap.hasMatchEndFieldReadAccess()) {
            sizeOfMatchFields += SystemToolkit.SIZE_OF_INTEGER;
        }
        
        if (functionalityMap.hasMatchLookaheadFieldReadAccess(isCurMatchStrategy)) {
            sizeOfMatchFields += SystemToolkit.SIZE_OF_INTEGER;
        }
        
        int sizeOfHelperFields = 0;
        
        if (hasLexicalStates) {
            sizeOfHelperFields += SystemToolkit.SIZE_OF_INTEGER;
        }
        
        if (automatonInfo.hasVariableLookaheads()) {
            sizeOfHelperFields += SystemToolkit.SIZE_OF_REFERENCE;
        }
        
        String sizeString = getSizeString(sizeOfReaderFields + sizeOfBufferFields +
                sizeOfStringFields + sizeOfRegionFields + sizeOfDotFields +
                sizeOfLexicalStateFields + sizeOfMatchFields + sizeOfHelperFields);
        
        return sizeString + getVariableSizeString(automatonInfo);
    }
    
    /**
     * 
     */
    private String getSizeString(int size) {
        if (size >= 4000) {
            return Integer.toString((int)Math.round((double)size / 1024))+" KB";
        }
        
        return Integer.toString(size)+" Bytes";
    }
    
    /**
     * 
     */
    private String getVariableSizeString(AutomatonInfo automatonInfo) {
        if (isReaderMode) {
            if (!isCurMatchStrategy) {
                return " + O(inputLength)";
            }
            
            return " + O(maxMatchLength)";
        }
        
        if (automatonInfo.hasVariableLookaheads()) {
            return " + O(maxMatchLength)";
        }
        
        return "";
    }
    
    /**
     * 
     */
    private String createValue(int value) {
        return createValue(Integer.toString(value));
    }
    
    /**
     * 
     */
    private String createValue(String value) {
        StringBuilder builder = new StringBuilder();
        builder.append(value);
        
        for (int i=29-builder.length();i>0;i--) {
            builder.append(" ");
        }
        
        return builder.toString();
    }
    
    //===================
    // Constants Methods
    //===================
    
    /**
     * 
     */
    private void generateTableConstants() {
        ensureEmptyLine(1);
        heading("Table Constants");
        
        javadoc(Member.CHARACTER_MAP,"Maps Unicode characters to DFA input symbols.");
        variableWithMethod(visibilityMap.getMemberName(Member.CHARACTER_MAP),
                "static final",automatonInfo.getCharacterMapType()+"[]","CHARACTER_MAP",
                "createCharacterMap",automatonInfo.getCharacterMapString());
        
        javadoc(Member.TRANSITION_TABLE,"The transition table of the DFA.");
        variableWithMethod(visibilityMap.getMemberName(Member.TRANSITION_TABLE),
                "static final",automatonInfo.getTransitionTableType()+"[][]","TRANSITION_TABLE",
                "createTransitionTable",automatonInfo.getTransitionTableString());
        
        javadoc(Member.ACTION_MAP,"Maps state numbers to action numbers.");
        variableWithMethod(visibilityMap.getMemberName(Member.ACTION_MAP),
                "static final",automatonInfo.getActionMapType()+"[]","ACTION_MAP",
                "createActionMap",automatonInfo.getActionMapString());
    }
    
    /**
     * 
     */
    private void generateLexicalStateConstants() {
        if (hasLexicalStates) {
            ensureEmptyLine(1);
            heading("Lexical State Constants");
            
            String[] names = automatonInfo.getNameMap().getNames();
            
            for (int i=0;i<names.length;i++) {
                javadoc(Member.LEXICAL_STATE_ENUM,false,"The ordinal number of "
                        + "the lexical state \""+names[i]+"\".");
                variable(visibilityMap.getMemberName(Member.LEXICAL_STATE_ENUM),
                        "static final","int","LEXICAL_STATE_"+names[i],""+i);
            }
        }
    }
    
    /**
     * 
     */
    private void generateHelperConstants() {
        if (isReaderMode) {
            ensureEmptyLine(1);
            heading("Helper Constants");
            
            javadoc(Member.EMPTY_CHAR_ARRAY,false,"An empty char array which is used to avoid null checks.");
            variable(visibilityMap.getMemberName(Member.EMPTY_CHAR_ARRAY),"static final","char[]",
                    "EMPTY_CHAR_ARRAY","new char[]{}");
        }
    }
    
    //================
    // Fields Methods
    //================
    
    /**
     * 
     */
    private void generateFields() {
        ensureEmptyLine(1);
        
        // input mode dependent fields
        if (isReaderMode) {
            heading("Reader Fields");
            
            javadoc(Member.READER,false,"A {@link Reader} from which the input characters are read.");
            variable(visibilityMap.getMemberName(Member.READER),"","Reader","reader",null);
            
            javadoc(Member.READER_START_CAPACITY,false,"The initial size of the character buffer.");
            variable(visibilityMap.getMemberName(Member.READER_START_CAPACITY),"","int","readerStartCapacity",null);
            
            heading("Buffer Fields");
            
            javadoc(Member.BUFFER,false,"A buffer which contains the characters of the reader.");
            variable(visibilityMap.getMemberName(Member.BUFFER),"","char[]","buffer","EMPTY_CHAR_ARRAY");
            
            if (isCurMatchStrategy) {
                javadoc(Member.BUFFER_START,false,"The position of the first available character.");
                variable(visibilityMap.getMemberName(Member.BUFFER_START),"","int","bufferStart",null);
            }
            
            javadoc(Member.BUFFER_END,false,"The position after the last available character.");
            variable(visibilityMap.getMemberName(Member.BUFFER_END),"","int","bufferEnd",null);
        }
        
        else {
            heading("String Fields");
            
            javadoc(Member.STRING,false,"The current string to be scanned.");
            variable(visibilityMap.getMemberName(Member.STRING),"","String","string","\"\"");
            
            heading("Region Fields");
            
            if (functionalityMap.hasRegionStartFieldReadAccess()) {
                javadoc(Member.REGION_START,false,"The start of the scan region.");
                variable(visibilityMap.getMemberName(Member.REGION_START),"","int","regionStart",null);
            }
            
            javadoc(Member.REGION_END,false,"The end of the scan region.");
            variable(visibilityMap.getMemberName(Member.REGION_END),"","int","regionEnd",null);
        }
        
        // input mode independent fields
        heading("Dot Fields");
        
        javadoc(Member.DOT,false,"The start position of the next scan.");
        variable(visibilityMap.getMemberName(Member.DOT),"","int","dot",null);
        
        if (hasLexicalStates && functionalityMap.hasLexicalStateFieldReadAccess()) {
            heading("Lexical State Fields");
            
            javadoc(Member.LEXICAL_STATE,false,"The current lexical state.");
            variable(visibilityMap.getMemberName(Member.LEXICAL_STATE),"","int","lexicalState","LEXICAL_STATE_INITIAL");
        }
        
        boolean hasMatchStartOwner = functionalityMap.hasMatchStartFieldReadAccess(isCurMatchStrategy);
        boolean hasMatchEndOwner = functionalityMap.hasMatchEndFieldReadAccess();
        boolean hasMatchLookaheadOwner = functionalityMap.hasMatchLookaheadFieldReadAccess(isCurMatchStrategy);
        
        if (hasMatchStartOwner || hasMatchEndOwner || hasMatchLookaheadOwner) {
            heading("Match Fields");
            
            if (hasMatchStartOwner) {
                javadoc(Member.MATCH_START,false,"The start of the last match.");
                variable(visibilityMap.getMemberName(Member.MATCH_START),"","int","matchStart",null);
            }
            
            if (hasMatchEndOwner) {
                javadoc(Member.MATCH_END,false,"The end of the last match.");
                variable(visibilityMap.getMemberName(Member.MATCH_END),"","int","matchEnd",null);
            }
            
            if (hasMatchLookaheadOwner) {
                javadoc(Member.MATCH_LOOKAHEAD,false,"The end of the last match including the lookahead.");
                variable(visibilityMap.getMemberName(Member.MATCH_LOOKAHEAD),"","int","matchLookahead",null);
            }
        }
        
        boolean hasVariableLookaheads = automatonInfo.hasVariableLookaheads();
        
        if (hasLexicalStates || hasVariableLookaheads) {
            heading("Helper Fields");
            
            if (hasLexicalStates) {
                javadoc(Member.START_STATE,false,"The start state of the DFA.");
                
                int startState = automatonInfo.getStartState(Condition.NAME_INITIAL);
                
                variable(visibilityMap.getMemberName(Member.START_STATE),"","int",
                        "startState",startState != 0 ? ""+startState : null);
            }
            
            if (hasVariableLookaheads) {
                javadoc(Member.POSITION_LIST,false,
                        "Contains expression end positions during the match end determination of",
                        "variable lookaheads.");
                variable(visibilityMap.getMemberName(Member.POSITION_LIST),"","boolean[]","positionList",null);
            }
        }
    }
    
    //================
    // Static Methods
    //================
    
    /**
     * 
     */
    private void generateTableMethods() {
        ensureEmptyLine(1);
        heading("Table Methods");
        
        javadoc(Member.CREATE_CHARACTER_MAP,
                "Creates the character map of the scanner.",
                "",
                "@param characterMapData The compressed data of the character map.",
                "@return The character map of the scanner.");
        
        decompressionMethod(Member.CREATE_CHARACTER_MAP,automatonInfo.getCharacterMapType(),
                "createCharacterMap","characterMapData","characterMap",
                ""+automatonInfo.getCharacterMapSize(),
                automatonInfo.getCharacterMapType(),
                getOffsetString(automatonInfo.getCharacterMapOffset()),null);
        
        javadoc(Member.CREATE_TRANSITION_TABLE,
                "Creates the transition table of the scanner.",
                "",
                "@param transitionTableData The compressed data of the transition table.",
                "@return The transition table of the scanner.");
        
        decompressionMethod(Member.CREATE_TRANSITION_TABLE,automatonInfo.getTransitionTableType(),
                "createTransitionTable","transitionTableData","transitionTable",
                ""+automatonInfo.getTotalDFAStateCount(),
                automatonInfo.getTransitionTableType(),
                getOffsetString(automatonInfo.getTransitionTableOffset()),
                ""+automatonInfo.getAlphabetLength());
        
        javadoc(Member.CREATE_ACTION_MAP,
                "Creates the action map of the scanner.",
                "",
                "@param actionMapData The compressed data of the action map.",
                "@return The action map of the scanner.");
        
        decompressionMethod(Member.CREATE_ACTION_MAP,automatonInfo.getActionMapType(),
                "createActionMap","actionMapData","actionMap",
                ""+automatonInfo.getTotalDFAStateCount(),
                automatonInfo.getActionMapType(),
                getOffsetString(automatonInfo.getActionMapOffset()),null);
    }
    
    /**
     * 
     */
    private String getOffsetString(int offset) {
        if (offset > 0) {
            return " - "+offset;
        }
        
        if (offset < 0) {
            return " + "+(-offset);
        }
        
        return null;
    }
    
    /**
     * 
     */
    private void decompressionMethod(Member member, String type,
            String methodName, String stringParamName, String localVarName,
            String arraySize, String cast, String offset, String secondArraySize) {
        
        methodHead(visibilityMap.getMemberName(member),"static",type+(secondArraySize != null ?
                "[][]" : "[]"),methodName,"String "+stringParamName);
        
        indent(2);
        append(type);
        
        if (secondArraySize != null) {
            append("[]");
        }
        
        append("[] ");
        append(localVarName);
        append(" = new ");
        append(type);
        append("[");
        append(arraySize);
        append("]");
        
        if (secondArraySize != null) {
            append("[");
            append(secondArraySize);
            append("]");
        }
        
        append(";");
        lineSeparator();
        
        indent(2);
        append("int length = ");
        append(stringParamName);
        append(".length();");
        lineSeparator();
        
        line(2,"int i = 0;");
        line(2,"int j = 0;");
        
        if (secondArraySize != null) {
            line(2,"int k = 0;");
        }
        
        line(2,"");
        line(2,"while (i < length) {");
        
        indent(3);
        append(type);
        append(" curValue = ");
        
        if ((cast != null) && !cast.equals("char")) {
            append("(");
            append(cast);
            append(")");
        }
        
        if (offset != null) {
            append("((short)");
        }
        
        append(stringParamName);
        append(".charAt(i++)");
        
        if (offset != null) {
            append(offset);
            append(")");
        }
        
        append(";");
        lineSeparator();
        
        line(3,"");
        
        indent(3);
        append("for (int x=");
        append(stringParamName);
        append(".charAt(i++);x>0;x--) {");
        lineSeparator();
        
        indent(4);
        append(localVarName);
        append(secondArraySize != null ? "[j][k++]" : "[j++]");
        append(" = curValue;");
        lineSeparator();
        
        line(3,"}");
        
        if (secondArraySize != null) {
            line(3,"");
            
            indent(3);
            append("if (k == ");
            append(secondArraySize);
            append(") {");
            lineSeparator();
            
            line(4,"k = 0;");
            line(4,"j++;");
            line(3,"}");
        }
        
        line(2,"}");
        line(2,"");
        
        indent(2);
        append("return ");
        append(localVarName);
        append(";");
        lineSeparator();
        
        line(1,"}");
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    private void generatePropertyMethods() {
        ensureEmptyLine(1);
        
        if (isReaderMode) {
            if (functionalityMap.hasReaderMethod()) {
                heading("Reader Methods");
                
                if (functionalityMap.has(Member.SET_READER)) {
                    javadoc(Member.SET_READER,
                            "Reinitializes the scanner by setting the {@link Reader} and the initial",
                            "buffer size to the specified values. All other values are set to their",
                            "default value.",
                            "",
                            "@param reader The new {@link Reader} from which the input characters are",
                            "read.",
                            "@param startCapacity The initial size of the character buffer.");
                    
                    methodHead(visibilityMap.getMemberName(Member.SET_READER),"",
                            "void",createMemberName(Member.SET_READER),"Reader reader, int startCapacity");
                    
                    line(2,"this.reader = reader;");
                    line(2,"this.readerStartCapacity = Math.max(startCapacity+1,2);");
                    line(2,"");
                    line(2,"buffer = EMPTY_CHAR_ARRAY;");
                    
                    if (isCurMatchStrategy) {
                        line(2,"bufferStart = 0;");
                    }
                    
                    line(2,"bufferEnd = 0;");
                    
                    appendResetForSharedFields();
                    
                    line(1,"}");
                }
                
                if (functionalityMap.has(Member.GET_READER)) {
                    javadoc(Member.GET_READER,
                            "Returns the {@link Reader} from which the input characters are read.",
                            "",
                            "@return The {@link Reader} from which the input characters are read.");
                    
                    getter(Member.GET_READER,"Reader","reader");
                }
                
                if (functionalityMap.has(Member.GET_READER_START_CAPACITY)) {
                    javadoc(Member.GET_READER_START_CAPACITY,
                            "Returns the initial size of the character buffer.",
                            "",
                            "@return The initial size of the character buffer.");
                    
                    getter(Member.GET_READER_START_CAPACITY,"int","readerStartCapacity");
                }
            }
            
            if (functionalityMap.hasBufferMethod()) {
                heading("Buffer Methods");
                
                if (functionalityMap.has(Member.GET_BUFFER)) {
                    javadoc(Member.GET_BUFFER,
                            "Returns the current character buffer. The returned reference may change",
                            "over time due to buffer reallocations.",
                            "",
                            "@return The current character buffer.");
                    
                    getter(Member.GET_BUFFER,"char[]","buffer");
                }
                
                if (isCurMatchStrategy &&
                    functionalityMap.has(Member.GET_BUFFER_START)) {
                    
                    javadoc(Member.GET_BUFFER_START,
                            "Returns the absolute position of the first character in the buffer.",
                            "",
                            "@return The absolute position of the first character in the buffer.");
                    
                    getter(Member.GET_BUFFER_START,"int","bufferStart");
                }
                
                if (functionalityMap.has(Member.GET_BUFFER_END)) {
                    javadoc(Member.GET_BUFFER_END,
                            "Returns the absolute position after the last character in the buffer.",
                            "",
                            "@return The absolute position after the last character in the buffer.");
                    
                    getter(Member.GET_BUFFER_END,"int","bufferEnd");
                }
            }
        }
        
        else {
            if (functionalityMap.hasStringMethod()) {
                heading("String Methods");
                
                if (functionalityMap.has(Member.SET_STRING)) {
                    javadoc(Member.SET_STRING,
                            "Sets the string to be scanned. The scan region is set to the entire",
                            "string.",
                            "",
                            "@param string The new string to be scanned.");
                    
                    methodHead(visibilityMap.getMemberName(Member.SET_STRING),"",
                            "void",createMemberName(Member.SET_STRING),"String string");
                    
                    line(2,"this.string = string != null ? string : \"\";");
                    line(2,"");
                    
                    if (functionalityMap.hasRegionStartFieldReadAccess()) {
                        line(2,"regionStart = 0;");
                    }
                    
                    line(2,"regionEnd = this.string.length();");
                    
                    appendResetForSharedFields();
                    
                    line(1,"}");
                }
                
                if (functionalityMap.has(Member.GET_STRING)) {
                    javadoc(Member.GET_STRING,
                            "Returns the current string to be scanned.",
                            "",
                            "@return The current string to be scanned.");
                    
                    getter(Member.GET_STRING,"String","string");
                }
            }
            
            if (functionalityMap.hasRegionMethod()) {
                heading("Region Methods");
                
                if (functionalityMap.has(Member.SET_REGION)) {
                    javadoc(Member.SET_REGION,
                            "Sets the scan region. The dot is clipped to the region if necessary.",
                            "",
                            "@param start The start of the scan region (inclusive).",
                            "@param end The end of the scan region (exclusive).",
                            "@throws IllegalArgumentException If the region is out of range");
                    
                    methodHead(visibilityMap.getMemberName(Member.SET_REGION),"",
                            "void",createMemberName(Member.SET_REGION),"int start, int end");
                    
                    line(2,"if ((start < 0) || (end > string.length()) || (start > end)) {");
                    line(3,"throw new IllegalArgumentException(\"region offsets out of range\");");
                    line(2,"}");
                    line(2,"");
                    
                    if (functionalityMap.hasRegionStartFieldReadAccess()) {
                        line(2,"regionStart = start;");
                    }
                    
                    line(2,"regionEnd = end;");
                    line(2,"");
                    line(2,"if (dot < start) {");
                    line(3,"dot = start;");
                    line(2,"}");
                    line(2,"");
                    line(2,"if (dot > end) {");
                    line(3,"dot = end;");
                    line(2,"}");
                    line(1,"}");
                }
                
                if (functionalityMap.has(Member.GET_REGION_START)) {
                    javadoc(Member.GET_REGION_START,
                            "Returns the start of the scan region.",
                            "",
                            "@return The start of the scan region.");
                    
                    getter(Member.GET_REGION_START,"int","regionStart");
                }
                
                if (functionalityMap.has(Member.GET_REGION_END)) {
                    javadoc(Member.GET_REGION_END,
                            "Returns the end of the scan region.",
                            "",
                            "@return The end of the scan region.");
                    
                    getter(Member.GET_REGION_END,"int","regionEnd");
                }
            }
        }
        
        if (functionalityMap.hasDotMethod()) {
            heading("Dot Methods");
            
            if (functionalityMap.has(Member.SET_DOT)) {
                javadoc(Member.SET_DOT,
                        "Sets the position at which the next scan starts.",
                        "",
                        "@param dot The new position at which the next scan starts.",
                        "@throws IllegalArgumentException If the position is out of range");
                
                setter(visibilityMap.getMemberName(Member.SET_DOT),createMemberName(Member.SET_DOT),"int dot");
                
                String dotMin = isReaderMode ? (isCurMatchStrategy ? "matchStart" : "0") : "regionStart";
                String dotMax = isReaderMode ? (isCurMatchStrategy ? "bufferEnd" : "bufferEnd") : "regionEnd";
                
                line(2,"if ((dot < "+dotMin+") || (dot > "+dotMax+")) {");
                line(3,"throw new IllegalArgumentException(\"dot out of range\");");
                line(2,"}");
                line(2,"");
                line(2,"this.dot = dot;");
                line(1,"}");
            }
            
            if (functionalityMap.has(Member.GET_DOT)) {
                javadoc(Member.GET_DOT,
                        "Returns the position at which the next scan starts.",
                        "",
                        "@return The position at which the next scan starts.");
                
                getter(Member.GET_DOT,"int","dot");
            }
        }
        
        if (hasLexicalStates) {
            heading("Lexical State Methods");
            
            javadoc(Member.SET_LEXICAL_STATE,
                    "Sets the current lexical state.",
                    "",
                    "@param lexicalState The new lexical state.",
                    "@throws IllegalArgumentException If the specified state is invalid");
            
            setter(visibilityMap.getMemberName(Member.SET_LEXICAL_STATE),
                    createMemberName(Member.SET_LEXICAL_STATE),"int lexicalState");
            
            line(2,"switch(lexicalState) {");
            String[] names = automatonInfo.getNameMap().getNames();
            
            for (int i=0;i<names.length;i++) {
                int startState = automatonInfo.getStartState(names[i]);
                
                line(2,"case LEXICAL_STATE_"+names[i]+": startState = "+startState+"; break;");
            }
            
            line(2,"default:");
            line(3,"throw new IllegalArgumentException(\"invalid lexical state\");");
            line(2,"}");
            
            if (functionalityMap.hasLexicalStateFieldReadAccess()) {
                line(2,"");
                line(2,"this.lexicalState = lexicalState;");
            }
            
            line(1,"}");
            
            if (functionalityMap.has(Member.GET_LEXICAL_STATE)) {
                javadoc(Member.GET_LEXICAL_STATE,
                        "Returns the current lexical state.",
                        "",
                        "@return The current lexical state.");
                
                getter(Member.GET_LEXICAL_STATE,"int","lexicalState");
            }
        }
        
        if (functionalityMap.hasMatchMethod()) {
            heading("Match Methods");
            
            if (functionalityMap.has(Member.GET_MATCH_START)) {
                javadoc(Member.GET_MATCH_START,
                        "Returns the start (inclusive) of the last match.",
                        "",
                        "@return The start (inclusive) of the last match.");
                
                getter(Member.GET_MATCH_START,"int","matchStart");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_END)) {
                javadoc(Member.GET_MATCH_END,
                        "Returns the end (exclusive) of the last match.",
                        "",
                        "@return The end (exclusive) of the last match.");
                
                getter(Member.GET_MATCH_END,"int","matchEnd");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_LOOKAHEAD)) {
                javadoc(Member.GET_MATCH_LOOKAHEAD,
                        "Returns the end of the lookahead (exclusive) of the last match.",
                        "",
                        "@return The end of the lookahead (exclusive) of the last match.");
                
                getter(Member.GET_MATCH_LOOKAHEAD,"int","matchLookahead");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_LENGTH)) {
                javadoc(Member.GET_MATCH_LENGTH,
                        "Returns the length of the last match.",
                        "",
                        "@return The length of the last match.");
                
                getter(Member.GET_MATCH_LENGTH,"int","matchEnd - matchStart");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_TOTAL_LENGTH)) {
                javadoc(Member.GET_MATCH_TOTAL_LENGTH,
                        "Returns the length of the last match including the lookahead.",
                        "",
                        "@return The length of the last match including the lookahead.");
                
                getter(Member.GET_MATCH_TOTAL_LENGTH,"int","matchLookahead - matchStart");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_LOOKAHEAD_LENGTH)) {
                javadoc(Member.GET_MATCH_LOOKAHEAD_LENGTH,
                        "Returns the length of the lookahead.",
                        "",
                        "@return The length of the lookahead.");
                
                getter(Member.GET_MATCH_LOOKAHEAD_LENGTH,"int","matchLookahead - matchEnd");
            }
            
            String curMatchStrategyDelta = isCurMatchStrategy ? "-bufferStart" : "";
            
            if (functionalityMap.has(Member.GET_MATCH_TEXT)) {
                javadoc(Member.GET_MATCH_TEXT,
                        "Returns the text of the last match.",
                        "",
                        "@return The text of the last match.");
                
                methodHead(visibilityMap.getMemberName(Member.GET_MATCH_TEXT),"",
                        "String",createMemberName(Member.GET_MATCH_TEXT),null);
                
                if (isReaderMode) {
                    line(2,"int count = matchEnd - matchStart;");
                    line(2,"");
                    line(2,"return count > 0 ? new String(buffer,matchStart"+curMatchStrategyDelta+",count) : \"\";");
                }
                
                else {
                    line(2,"return string.substring(matchStart,matchEnd);");
                }
                
                line(1,"}");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_TEXT_RANGE)) {
                javadoc(Member.GET_MATCH_TEXT_RANGE,
                        "Returns a substring relative to the last match.",
                        "",
                        "@param startOffset The forward-oriented start offset of the substring",
                        "relative to the start of the last match.",
                        "@param endOffset The backward-oriented end offset of the substring",
                        "relative to the end of the last match.",
                        "@return The substring at the specified indices.",
                        "@throws IndexOutOfBoundsException If the specified indices are invalid");
                
                // Note: Use GET_MATCH_TEXT as the member for createMemberName
                // as GET_MATCH_TEXT_RANGE overloads GET_MATCH_TEXT and does not
                // use the RANGE suffix in it's name.
                methodHead(visibilityMap.getMemberName(Member.GET_MATCH_TEXT_RANGE),"",
                        "String",createMemberName(Member.GET_MATCH_TEXT),"int startOffset, int endOffset");
                
                line(2,"int startIndex = matchStart + startOffset;");
                line(2,"int endIndex = matchEnd - endOffset;");
                line(2,"");
                
                String rangeMin = isReaderMode ? (isCurMatchStrategy ? "matchStart" : "0") : "regionStart";
                String rangeMax = isReaderMode ? (isCurMatchStrategy ? "bufferEnd" : "bufferEnd") : "regionEnd";
                
                line(2,"if ((startIndex < "+rangeMin+") || (endIndex > "+rangeMax+") ||");
                line(3,"(startIndex > endIndex)) {");
                line(3,"");
                line(3,"throw new IndexOutOfBoundsException(\"match text not available\");");
                line(2,"}");
                line(2,"");
                
                if (isReaderMode) {
                    line(2,"int count = endIndex - startIndex;");
                    line(2,"");
                    line(2,"return count > 0 ? new String(buffer,startIndex"+curMatchStrategyDelta+",count) : \"\";");
                }
                
                else {
                    line(2,"return string.substring(startIndex,endIndex);");
                }
                
                line(1,"}");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_TOTAL_TEXT)) {
                javadoc(Member.GET_MATCH_TOTAL_TEXT,
                        "Returns the text of the last match including the lookahead.",
                        "",
                        "@return The text of the last match including the lookahead.");
                
                methodHead(visibilityMap.getMemberName(Member.GET_MATCH_TOTAL_TEXT),"",
                        "String",createMemberName(Member.GET_MATCH_TOTAL_TEXT),null);
                
                if (isReaderMode) {
                    line(2,"int count = matchLookahead - matchStart;");
                    line(2,"");
                    line(2,"return count > 0 ? new String(buffer,matchStart"+curMatchStrategyDelta+",count) : \"\";");
                }
                
                else {
                    line(2,"return string.substring(matchStart,matchLookahead);");
                }
                
                line(1,"}");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_LOOKAHEAD_TEXT)) {
                javadoc(Member.GET_MATCH_LOOKAHEAD_TEXT,
                        "Returns the text of the lookahead of the last match.",
                        "",
                        "@return The text of the lookahead of the last match.");
                
                methodHead(visibilityMap.getMemberName(Member.GET_MATCH_LOOKAHEAD_TEXT),"",
                        "String",createMemberName(Member.GET_MATCH_LOOKAHEAD_TEXT),null);
                
                if (isReaderMode) {
                    line(2,"int count = matchLookahead - matchEnd;");
                    line(2,"");
                    line(2,"return count > 0 ? new String(buffer,matchEnd"+curMatchStrategyDelta+",count) : \"\";");
                }
                
                else {
                    line(2,"return string.substring(matchEnd,matchLookahead);");
                }
                
                line(1,"}");
            }
            
            if (functionalityMap.has(Member.GET_MATCH_CHAR)) {
                javadoc(Member.GET_MATCH_CHAR,
                        "Returns a character relative to the start of the last match.",
                        "",
                        "@param index The index of the character relative to the last match.",
                        "@return The character at the specified position.",
                        "@throws IndexOutOfBoundsException If the specified index is invalid");
                
                methodHead(visibilityMap.getMemberName(Member.GET_MATCH_CHAR),"",
                        "char",createMemberName(Member.GET_MATCH_CHAR),"int index");
                
                line(2,"int stringIndex = matchStart + index;");
                line(2,"");
                
                String dotMin = isReaderMode ? (isCurMatchStrategy ? "matchStart" : "0") : "regionStart";
                String dotMax = isReaderMode ? (isCurMatchStrategy ? "bufferEnd" : "bufferEnd") : "regionEnd";
                
                line(2,"if ((stringIndex < "+dotMin+") || (stringIndex >= "+dotMax+")) {");
                line(3,"throw new IndexOutOfBoundsException(\"match character not available\");");
                line(2,"}");
                line(2,"");
                
                line(2,isReaderMode ?
                        "return buffer[stringIndex"+curMatchStrategyDelta+"];" :
                        "return string.charAt(stringIndex);");
                
                line(1,"}");
            }
        }
    }
    
    //==============
    // Scan Methods
    //==============
    
    /**
     * 
     */
    private void generateScanMethods() {
        ensureEmptyLine(1);
        heading("Scan Methods");
        
        // create method throw types
        LinkedHashSet<TypeDescriptor> methodThrowsTypes = new LinkedHashSet<>();
        
        if (isReaderMode) {
            methodThrowsTypes.add(TYPE_IO_EXCEPTION);
        }
        
        methodThrowsTypes.addAll(methodThrows);
        
        // create Javadoc throw types
        LinkedHashSet<TypeDescriptor> javaDocThrowsTypes = new LinkedHashSet<>(methodThrowsTypes);
        
        if (noMatchAction == NoMatchAction.ERROR) {
            javaDocThrowsTypes.add(TYPE_ILLEGAL_STATE_EXCEPTION);
        }
        
        javadoc(Member.GET_NEXT_TOKEN,
                createDocComment(true,javaDocThrowsTypes,new String[]{
                "Performs at the current position the next step of the lexical analysis",
                "and returns the result."
                },"The result of the next step of the lexical analysis."));
        
        indent();
        append(visibilityMap.getMemberName(Member.GET_NEXT_TOKEN));
        ensureSpace();
        append(config.getUsedReturnType());
        append(" ");
        append(config.getOptions().getMethodName()+(internalMap
                .get(Member.GET_NEXT_TOKEN) ? "Internal" : ""));
        append("() ");
        
        if (!methodThrowsTypes.isEmpty()) {
            append("throws ");
            
            Iterator<TypeDescriptor> iterator = methodThrowsTypes.iterator();
            int throwCounter = 0;
            
            while (iterator.hasNext()) {
                append(iterator.next().getSimpleName());
                throwCounter++;
                
                if (iterator.hasNext()) {
                    append(",");
                    
                    if (((throwCounter - 1) % 2) == 0) {
                        lineSeparator();
                        indent(3);
                    }
                    
                    else {
                        append(" ");
                    }
                }
            }
            
            append(" ");
        }
        
        append("{");
        lineSeparator();
        
        if (methodThrowsTypes.size() > 1) {
            line(2,"");
        }
        
        boolean isLoop = (noMatchAction == NoMatchAction.CONTINUE) ||
                config.getHasAlsoVoidReturnType();
        String entranceCondition = isReaderMode ?
                "(reader != null) && hasNextChar(dot)" :
                "dot < regionEnd";
        
        if (isLoop) {
            line(2,"while ("+entranceCondition+") {");
        }
        
        else {
            line(2,"if ("+entranceCondition+") {");
        }
        
        line(3,"");
        line(3,"// find longest match");
        
        String startState = hasLexicalStates ? "startState" :
            ""+automatonInfo.getStartState(Condition.NAME_INITIAL);
        
        line(3,"int curState = "+startState+";");
        line(3,"int iterator = dot;");
        line(3,"int matchState = -1;");
        line(3,"int matchPosition = 0;");
        line(3,"");
        line(3,"do {");
        
        generateStateTransition(1,false);
        
        line(4,"");
        line(4,"if (curState == -1) {");
        line(5,"break;");
        line(4,"}");
        line(4,"");
        line(4,"if (ACTION_MAP[curState] != -1) {");
        line(5,"matchState = curState;");
        line(5,"matchPosition = iterator;");
        line(4,"}");
        
        String loopCondition = isReaderMode ? "hasNextChar(++iterator)" :
            "++iterator < regionEnd";
        
        line(3,"} while ("+loopCondition+");");
        
        line(3,"");
        line(3,"// match found, perform action");
        line(3,"if (matchState != -1) {");
        
        boolean hasMatchStartOwner = functionalityMap.hasMatchStartFieldReadAccess(isCurMatchStrategy);
        boolean hasMatchEndOwner = functionalityMap.hasMatchEndFieldReadAccess();
        boolean hasMatchLookaheadOwner = functionalityMap.hasMatchLookaheadFieldReadAccess(isCurMatchStrategy);
        boolean hasDotAssignmentOwner = automatonInfo.hasNormalActions();
        
        boolean hasMatchStartVarOwner = (automatonInfo.hasFixContentLookaheads() ||
                 automatonInfo.hasVariableLookaheads()) &&
                 hasDotAssignmentOwner && !hasMatchStartOwner;
        
        int endPositionOwner = 0;
        
        if (hasMatchEndOwner) {
            endPositionOwner++;
        }
        
        if (hasMatchLookaheadOwner) {
            endPositionOwner++;
        }
        
        if (hasDotAssignmentOwner) {
            endPositionOwner++;
        }
        
        String endPositionCode = endPositionOwner > 1 ?
                "endPosition" : "matchPosition + 1";
        
        boolean hasLocalVars = (endPositionOwner > 1) || hasMatchStartVarOwner;
        boolean hasMatchVars = hasMatchStartOwner || hasMatchEndOwner || hasMatchLookaheadOwner;
        boolean hasAssignments = hasMatchVars || hasDotAssignmentOwner;
        
        if (hasLocalVars) {
            if (endPositionOwner > 1) {
                line(4,"int endPosition = matchPosition + 1;");
            }
            
            if (hasMatchStartVarOwner) {
                line(4,"int matchStart = dot;");
            }
        }
        
        if (hasLocalVars && hasAssignments) {
            line(4,"");
        }
        
        if (hasAssignments) {
            generateMatchAssignment(4,hasMatchStartOwner,hasMatchEndOwner,
                    hasMatchLookaheadOwner,"dot",endPositionCode,endPositionCode);
            
            if (hasDotAssignmentOwner) {
                line(4,"dot = "+endPositionCode+";");
            }
            
            line(4,"");
        }
        
        String oldDotVar = hasDotAssignmentOwner ? "matchStart" : "dot";
        
        generateActionSwitch(oldDotVar);
        
        line(3,"}");
        
        switch(noMatchAction) {
        case CONTINUE:
            line(3,"");
            line(3,"// no match found, try next position");
            line(3,"dot++;");
            break;
        
        case RETURN:
            if (isLoop) {
                line(3,"");
                line(3,"// no match found, break loop");
                line(3,"break;");
            }
            break;
        
        case ERROR:
            line(3,"");
            line(3,"// no match found, set match values and report as error");
            
            if (hasMatchVars) {
                generateMatchAssignment(3,hasMatchStartOwner,hasMatchEndOwner,
                        hasMatchLookaheadOwner,"dot","dot","dot");
                line(3,"");
            }
            
            line(3,"throw new IllegalStateException(\"invalid input\");");
            break;
        
        default:
            throw new IllegalStateException("unknown action type");
        }
        
        line(2,"}");
        line(2,"");
        line(2,"// no match found, set match values and return to caller");
        
        if (hasMatchVars) {
            generateMatchAssignment(2,hasMatchStartOwner,hasMatchEndOwner,
                    hasMatchLookaheadOwner,"dot","dot","dot");
            line(2,"");
        }
        
        line(2,"return "+createDefaultReturnValue()+";");
        line(1,"}");
    }
    
    /**
     * 
     */
    private void generateStateTransition(int indent, boolean decreaseIterator) {
        int characterMapLastValue = automatonInfo.getCharacterMapLastValue();
        
        if (characterMapLastValue != -1) {
            String charAccess = isReaderMode ? (isCurMatchStrategy ?
                    "buffer[iterator-bufferStart]" : "buffer[iterator]") :
                    "string.charAt(iterator)";
            
            line(3+indent,"char curChar = "+charAccess+";");
            
            if (decreaseIterator) {
                line(3+indent,"iterator--;");
            }
            
            line(3+indent,"");
            
            indent(3+indent);
            append("curState = TRANSITION_TABLE[curState][curChar >= ");
            append(automatonInfo.getCharacterMapSize());
            append(" ?");
            lineSeparator();
            
            indent(5+indent);
            append("");
            append(characterMapLastValue);
            append(" : CHARACTER_MAP[curChar]];");
            lineSeparator();
        }
        
        else {
            String charAccess1 = isReaderMode ? "" :
                "string";
            String charAccess2 = isReaderMode ? (isCurMatchStrategy ?
                    "buffer[iterator-bufferStart]" : "buffer[iterator]") :
                    ".charAt(iterator)";
            
            line(3+indent,"curState = TRANSITION_TABLE[curState][CHARACTER_MAP["+charAccess1);
            line(5+indent,""+charAccess2+"]];");
            
            if (decreaseIterator) {
                line(3+indent,"iterator--;");
            }
        }
    }
    
    /**
     * 
     */
    private void generateActionSwitch(String oldDotVar) {
        ActionPool<MethodInfo> actionPool = automatonInfo.getActionPool();
        int[] actionReorderMap = automatonInfo.getActionReorderMap();
        int actionCount = actionPool.size();
        int lastLookaheadType = -1;
        
        line(4,"switch(ACTION_MAP[matchState]) {");
        
        for (int i=0;i<actionCount;i++) {
            Action<MethodInfo> curAction = actionPool.get(i);
            MethodInfo curMethod = curAction.getOwnerRule().getAction();
            int curLookaheadType = curAction.getLookaheadType();
            
            switch(lastLookaheadType) {
            case Action.LOOKAHEAD_FIX_CONTENT:
            case Action.LOOKAHEAD_FIX_CONDITION:
            case Action.LOOKAHEAD_VARIABLE:
                line(4,"");
            }
            
            switch(curLookaheadType) {
            case Action.LOOKAHEAD_NONE:
                line(4,"case "+actionReorderMap[i]+": "+generateActionCall(curMethod));
                break;
            
            case Action.LOOKAHEAD_FIX_CONDITION:
                line(4,"case "+actionReorderMap[i]+":");
                
                String dotIncrement = createIntIncrement("dot","matchPosition",
                        -(curAction.getLookaheadValue()-1));
                
                if (dotIncrement != null) {
                    line(5,""+dotIncrement);
                }
                
                if (functionalityMap.hasMatchEndFieldReadAccess()) {
                    line(5,"matchEnd = dot;");
                }
                
                line(5,""+generateActionCall(curMethod));
                break;
            
            case Action.LOOKAHEAD_FIX_CONTENT:
                line(4,"case "+actionReorderMap[i]+":");
                
                dotIncrement = createIntIncrement("dot",oldDotVar,
                        curAction.getLookaheadValue());
                
                if (dotIncrement != null) {
                    line(5,""+dotIncrement);
                }
                
                if (functionalityMap.hasMatchEndFieldReadAccess()) {
                    line(5,"matchEnd = dot;");
                }
                
                line(5,""+generateActionCall(curMethod));
                break;
            
            case Action.LOOKAHEAD_VARIABLE:
                int lookaheadValue = curAction.getLookaheadValue();
                
                line(4,"case "+actionReorderMap[i]+":");
                line(5,"dot = computeMatchEnd("+oldDotVar+",matchPosition,"+
                        automatonInfo.getStartState(lookaheadValue)+","+
                        automatonInfo.getStartState(lookaheadValue+1)+");");
                
                if (functionalityMap.hasMatchEndFieldReadAccess()) {
                    line(5,"matchEnd = dot;");
                }
                
                line(5,""+generateActionCall(curMethod));
                break;
            }
            
            lastLookaheadType = curLookaheadType;
        }
        
        line(4,"}");
    }
    
    /**
     * 
     */
    private String generateActionCall(MethodInfo method) {
        StringBuilder builder = new StringBuilder();
        String returnType = method.getReturnType();
        boolean isVoid = returnType == null || returnType.equals("void");
        
        if (!isVoid) {
            builder.append("return ");
        }
        
        builder.append(method.getName());
        builder.append("();");
        
        if (isVoid) {
            if (config.getHasAlsoVoidReturnType()) {
                builder.append(" continue;");
            }
            
            else {
                builder.append(" return true;");
            }
        }
        
        return builder.toString();
    }
    
    /**
     * 
     */
    private void generateMatchAssignment(int indent, boolean hasMatchStartOwner,
            boolean hasMatchEndOwner, boolean hasMatchLookaheadOwner,
            String matchStart, String matchEnd, String matchLookahead) {
        
        if (hasMatchStartOwner) {
            line(indent,"matchStart = "+matchStart+";");
        }
        
        if (hasMatchEndOwner) {
            line(indent,"matchEnd = "+matchEnd+";");
        }
        
        if (hasMatchLookaheadOwner) {
            line(indent,"matchLookahead = "+matchLookahead+";");
        }
    }
    
    /**
     * 
     */
    private String createDefaultReturnValue() {
        String defaultReturnValue = config.getOptions().getDefaultReturnValue();
        
        if ((defaultReturnValue != null) && !defaultReturnValue.trim().isEmpty()) {
            return defaultReturnValue;
        }
        
        return config.getReturnTypeDefaultValue();
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * 
     */
    private void generateHelperMethods() {
        boolean hasVariableLookaheads = automatonInfo.hasVariableLookaheads();
        
        if (isReaderMode || hasVariableLookaheads) {
            ensureEmptyLine(1);
            heading("Helper Methods");
            
            if (isReaderMode) {
                javadoc(Member.HAS_NEXT_CHAR,
                        "Checks whether for the specified position a following character exists.",
                        "This is always the case if the position lies inside the range of",
                        "available characters. If the position lies straight after the last",
                        "available character then the reader is used to check whether there are",
                        "further characters.",
                        "",
                        "@param position A position inside the range of available characters.",
                        "@return True if there is a next character, otherwise false.",
                        "@throws IOException If an IO error occurs");
                
                methodHead(visibilityMap.getMemberName(Member.HAS_NEXT_CHAR),"",
                        "boolean","hasNextChar","int position","IOException");
                
                line(2,"if (position < bufferEnd) {");
                line(3,"return true;");
                line(2,"}");
                line(2,"");
                line(2,"// if the buffer is empty then create a new one with the specified size");
                line(2,"if (buffer == EMPTY_CHAR_ARRAY) {");
                line(3,"buffer = new char[readerStartCapacity];");
                line(2,"}");
                line(2,"");
                line(2,"// otherwise check whether the buffer is full");
                
                if (isCurMatchStrategy) {
                    line(2,"else if ((bufferEnd - bufferStart) == buffer.length) {");
                    line(3,"int usedSpace = bufferEnd - dot;");
                    line(3,"");
                    line(3,"if (usedSpace > (buffer.length >> 1)) {");
                    line(4,"char[] newBuffer = new char["+createBufferIncrement("buffer.length")+"];");
                    line(4,"System.arraycopy(buffer,dot-bufferStart,newBuffer,0,usedSpace);");
                    line(4,"buffer = newBuffer;");
                    line(3,"}");
                    line(3,"");
                    line(3,"else {");
                    line(4,"System.arraycopy(buffer,dot-bufferStart,buffer,0,usedSpace);");
                    line(3,"}");
                    line(3,"");
                    line(3,"bufferStart = dot;");
                    line(2,"}");
                }
                
                else {
                    line(2,"else if (bufferEnd == buffer.length) {");
                    line(3,"char[] newBuffer = new char["+createBufferIncrement("buffer.length")+"];");
                    line(3,"System.arraycopy(buffer,0,newBuffer,0,buffer.length);");
                    line(3,"buffer = newBuffer;");
                    line(2,"}");
                }
                
                line(2,"");
                line(2,"// read further characters and check results");
                
                if (isCurMatchStrategy) {
                    line(2,"int usedSpace = bufferEnd - bufferStart;");
                    line(2,"int charsRead = reader.read(buffer,usedSpace,buffer.length-usedSpace);");
                }
                
                else {
                    line(2,"int charsRead = reader.read(buffer,bufferEnd,buffer.length-bufferEnd);");
                }
                
                line(2,"");
                line(2,"if (charsRead > 0) {");
                line(3,"bufferEnd += charsRead;");
                line(3,"");
                line(3,"return true;");
                line(2,"}");
                line(2,"");
                line(2,"return false;");
                line(1,"}");
            }
            
            if (hasVariableLookaheads) {
                javadoc(Member.COMPUTE_MATCH_END,
                        "Computes the match end position of a variable lookahead.",
                        "",
                        "@param start The start of the current match.",
                        "@param end The end of the current match.",
                        "@param forwardStartState The index of the start state of the forward pass.",
                        "@param backwardStartState The index of the start state of the backward pass.",
                        "@return The end position of the match.");
                
                indent();
                append(visibilityMap.getMemberName(Member.COMPUTE_MATCH_END));
                ensureSpace();
                append("int computeMatchEnd(int start, int end, int forwardStartState,");
                lineSeparator();
                indent(3);
                append("int backwardStartState) {");
                lineSeparator();
                
                line(2,"");
                line(2,"// initialize position list");
                line(2,"int length = end - start + 1;");
                line(2,"");
                line(2,"if (positionList == null) {");
                line(3,"positionList = new boolean[length];");
                line(2,"}");
                line(2,"");
                line(2,"else if (length > positionList.length) {");
                line(3,"positionList = new boolean["+createBufferIncrement("length")+"];");
                line(2,"}");
                line(2,"");
                line(2,"else {");
                line(3,"for (int i=0;i<length;i++) {");
                line(4,"positionList[i] = false;");
                line(3,"}");
                line(2,"}");
                line(2,"");
                line(2,"// determine content positions");
                line(2,"int curState = forwardStartState;");
                line(2,"int iterator = start;");
                line(2,"");
                line(2,"do {");
                
                generateStateTransition(0,false);
                
                line(3,"");
                line(3,"if (curState == -1) {");
                line(4,"break;");
                line(3,"}");
                line(3,"");
                line(3,"if (ACTION_MAP[curState] != -1) {");
                line(4,"positionList[iterator - start] = true;");
                line(3,"}");
                line(3,"");
                line(3,"if (iterator == end) {");
                line(4,"break;");
                line(3,"}");
                line(3,"");
                line(3,"iterator++;");
                line(2,"} while (true);");
                line(2,"");
                line(2,"// determine longest content match");
                line(2,"curState = backwardStartState;");
                line(2,"iterator = end;");
                line(2,"");
                line(2,"do {");
                
                generateStateTransition(0,true);
                
                line(3,"");
                line(3,"if ((ACTION_MAP[curState] != -1) && positionList[iterator-start]) {");
                line(4,"return iterator + 1;");
                line(3,"}");
                line(2,"} while (true);");
                line(1,"}");
            }
        }
    }
    
    /**
     * 
     */
    private String createBufferIncrement(String length) {
        if (isGoldenRatioIncrement) {
            return length + "+(" + length + ">>1)";
        }
        
        return length + "<<1";
    }
    
    /**
     * 
     */
    private void appendResetForSharedFields() {
        boolean hasMatchStartOwner = functionalityMap.hasMatchStartFieldReadAccess(isCurMatchStrategy);
        boolean hasMatchEndOwner = functionalityMap.hasMatchEndFieldReadAccess();
        boolean hasMatchLookaheadOwner = functionalityMap.hasMatchLookaheadFieldReadAccess(isCurMatchStrategy);
        
        line(2,"");
        line(2,"dot = 0;");
        
        if (hasLexicalStates && functionalityMap.hasLexicalStateFieldReadAccess()) {
            line(2,"lexicalState = LEXICAL_STATE_INITIAL;");
        }
        
        if (hasMatchStartOwner || hasMatchEndOwner || hasMatchLookaheadOwner) {
            line(2,"");
            
            if (hasMatchStartOwner) {
                line(2,"matchStart = 0;");
            }
            
            if (hasMatchEndOwner) {
                line(2,"matchEnd = 0;");
            }
            
            if (hasMatchLookaheadOwner) {
                line(2,"matchLookahead = 0;");
            }
        }
        
        boolean hasVariableLookaheads = automatonInfo.hasVariableLookaheads();
        
        if (hasLexicalStates || hasVariableLookaheads) {
            line(2,"");
            
            if (hasLexicalStates) {
                line(2,"startState = "+automatonInfo.getStartState(
                        Condition.NAME_INITIAL)+";");
            }
            
            if (hasVariableLookaheads) {
                line(2,"positionList = null;");
            }
        }
    }
    
    //=================
    // Heading Methods
    //=================
    
    /**
     * 
     */
    private void heading(String text) {
        switch(config.getOptions().getHeadings()) {
        case ENABLED:
        case LARGE:
            largeHeading(text);
            break;
        
        case SMALL:
            smallHeading(text);
            break;
        
        case MEDIUM:
            mediumHeading(text);
            break;
        
        case DISABLED:
            break;
        
        default:
            throw new IllegalStateException("invalid heading type");
        }
    }
    
    /**
     * 
     */
    private void largeHeading(String text) {
        int commentLength = text.length();
        
        ensureEmptyLine(1);
        
        indent();
        append("//");
        append("=",commentLength+2);
        lineSeparator();
        
        indent();
        append("// ");
        append(text);
        lineSeparator();
        
        indent();
        append("//");
        append("=",commentLength+2);
        lineSeparator();
        
        ensureEmptyLine(1);
    }
    
    /**
     * 
     */
    private void smallHeading(String text) {
        ensureEmptyLine(1);
        
        indent();
        append("// ");
        append(text);
        lineSeparator();
        
        ensureEmptyLine(1);
    }
    
    /**
     * 
     */
    private void mediumHeading(String text) {
        int commentLength = text.length();
        
        ensureEmptyLine(1);
        
        indent();
        append("// ");
        append(text);
        lineSeparator();
        
        indent();
        append("//");
        append("-",commentLength+2);
        lineSeparator();
        
        ensureEmptyLine(1);
    }
    
    //=================
    // Javadoc Methods
    //=================
    
    /**
     * 
     */
    public void javadoc(Member member, String... lines) {
        javadoc(member,true,lines);
    }
    
    /**
     * 
     */
    public void javadoc(Member member, boolean emptyLineIfAbsent,
            String... lines) {
        
        if (config.getOptions().getJavaDocMap().has(member)) {
            super.javadoc(lines);
        }
        
        else if (emptyLineIfAbsent) {
            ensureEmptyLine(1);
        }
    }
    
    /**
     * 
     */
    private String[] createDocComment(boolean hasReturnType,
            LinkedHashSet<TypeDescriptor> throwsTypes,
            String[] methodDescription, String returnTypeDescription) {
        
        ArrayList<String> lines = new ArrayList<>();
        
        if (methodDescription != null) {
            for (int i=0;i<methodDescription.length;i++) {
                lines.add(methodDescription[i]);
            }
        }
        
        boolean hasThrowTypes = !throwsTypes.isEmpty();
        
        if ((methodDescription != null) && (methodDescription.length > 0) &&
            (hasReturnType || hasThrowTypes)) {
            
            lines.add("");
        }
        
        if (hasReturnType) {
            lines.add("@return "+returnTypeDescription);
        }
        
        if (hasThrowTypes) {
            Iterator<TypeDescriptor> iterator = throwsTypes.iterator();
            
            while (iterator.hasNext()) {
                TypeDescriptor curType = iterator.next();
                String typeName = curType.getSimpleName();
                String typeDescription = getThrowableDescription(curType);
                
                lines.add("@throws "+typeName+" "+typeDescription);
            }
        }
        
        return lines.toArray(SystemToolkit.EMPTY_STRING_ARRAY);
    }
    
    /**
     * 
     */
    private String getThrowableDescription(TypeDescriptor type) {
        if (type.equals(TYPE_IO_EXCEPTION)) {
            return "If an IO error occurs";
        }
        
        return "If a lexical error occurs";
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * 
     */
    private String createMemberName(Member member) {
        if (internalMap.get(member)) {
            return member.getName() + "Internal";
        }
        
        return member.getName();
    }
    
    /**
     * 
     */
    private void getter(Member member, String returnType,
            String returnStatement) {
        
        super.getter(visibilityMap.getMemberName(member),createMemberName(member),
                returnType,returnStatement);
    }
}
