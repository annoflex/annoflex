/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.annoflex.jdt.dom.CommentType;
import org.annoflex.jdt.dom.CommentValue;
import org.annoflex.jdt.dom.JOMComment;
import org.annoflex.jdt.dom.JOMCommentList;
import org.annoflex.jdt.dom.JOMCompilationUnit;
import org.annoflex.jdt.dom.JOMImport;
import org.annoflex.jdt.dom.JOMMethodDeclaration;
import org.annoflex.jdt.dom.JOMModifier;
import org.annoflex.jdt.dom.JOMModifierList;
import org.annoflex.jdt.dom.JOMNamePart;
import org.annoflex.jdt.dom.JOMName;
import org.annoflex.jdt.dom.JOMNode;
import org.annoflex.jdt.dom.JOMPackage;
import org.annoflex.jdt.dom.JOMTag;
import org.annoflex.jdt.dom.JOMType;
import org.annoflex.jdt.dom.JOMTypeDeclaration;
import org.annoflex.jdt.dom.JOMTypeDeclarationBody;
import org.annoflex.jdt.dom.Modifier;
import org.annoflex.util.text.LineInfo;
import org.annoflex.util.text.Slice;
import org.annoflex.util.text.Span;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class JavaParser {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final JavaTokenHandler PARSER_INVISIBLE_TOKENS = new JavaTokenHandler(
            JavaTokenType.WHITESPACE,
            JavaTokenType.END_OF_LINE_COMMENT,
            JavaTokenType.TRADITIONAL_COMMENT,
            JavaTokenType.DOCUMENTATION_COMMENT);
    
    /**
     * 
     */
    private static final JavaTokenHandler JAVADOC_SKIP_TOKENS = new JavaTokenHandler(
            JavaTokenType.WHITESPACE,
            JavaTokenType.END_OF_LINE_COMMENT,
            JavaTokenType.TRADITIONAL_COMMENT);
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private String fileContent;
    
    /**
     * 
     */
    private JavaTokenList fullTokenList;
    
    /**
     * 
     */
    private JavaTokenList parserTokenList;
    
    /**
     * 
     */
    private JOMCompilationUnit compilationUnit;
    
    /**
     * 
     */
    private JOMTypeDeclaration lastTypeDeclaration;
    
    /**
     * 
     */
    private JOMCommentList commentList;
    
    /**
     * 
     */
    private HashMap<JavaToken,JOMComment> commentMap;
    
    //================
    // Public Methods
    //================
    
    /**
     * 
     */
    public JOMCompilationUnit parse(String fileContent) {
        
        // init
        this.fileContent = fileContent;
        fullTokenList = new JavaTokenList();
        parserTokenList = new JavaTokenList();
        compilationUnit = new JOMCompilationUnit();
        lastTypeDeclaration = null;
        commentList = new JOMCommentList();
        commentMap = new HashMap<>();
        
        // create tokens
        fullTokenList.addAll(new JavaScanner(fileContent));
        parserTokenList.addAll(fullTokenList,PARSER_INVISIBLE_TOKENS);
        
        // create tree
        appendComments();
        appendDeclarations();
        
        compilationUnit.appendChild(commentList);
        
        return compilationUnit;
    }
    
    //=================
    // Comment Methods
    //=================
    
    /**
     * 
     */
    private void appendComments() {
        int size = fullTokenList.size();
        
        for (int i=0;i<size;i++) {
            JavaToken curToken = fullTokenList.get(i);
            
            if (isComment(curToken)) {
                JOMComment commentNode = new JOMComment();
                setSourceRange(commentNode,curToken);
                commentNode.setType(getCommentType(curToken.type()));
                commentNode.setValue((CommentValue)curToken.value());
                
                if (curToken.type() == JavaTokenType.DOCUMENTATION_COMMENT) {
                    appendCommentTags(commentNode);
                }
                
                commentList.appendChild(commentNode);
                commentMap.put(curToken,commentNode);
            }
        }
    }
    
    /**
     * 
     */
    private void appendCommentTags(JOMComment commentNode) {
        // TODO: Review.
        CommentValue commentValue = commentNode.getValue();
        Span[] tagLines = getTagLines(commentValue);
        
        if ((tagLines != null) && (tagLines.length > 0)) {
            JOMTag curTag = null;
            int curTagStart = 0;
            ArrayList<Span> curContent = null;
            
            for (int i=0;i<tagLines.length;i++) {
                Span tagLine = tagLines[i];
                Span tagName = getTagName(tagLine);
                int lineStart = tagName != null ? tagName.end : tagLine.start;
                int lineEnd = tagLine.end;
                
                if ((tagName != null) || (i == 0)) {
                    
                    // finalize old tag
                    if (curTag != null) {
                        curTag.setSlice(new Slice(fileContent,curContent
                                .toArray(Span.EMPTY_ARRAY),0,' '));
                    }
                    
                    // create new tag
                    curTag = new JOMTag();
                    curTag.setSourceRange(tagLine.start,tagLine.end);
                    commentNode.appendChild(curTag);
                    curTagStart = lineStart;
                    
                    if (tagName != null) {
                        curTag.setName(fileContent.substring(tagName.start,tagName.end));
                    }
                    
                    if (curContent == null) {
                        curContent = new ArrayList<>();
                    }
                    
                    else {
                        curContent.clear();
                    }
                    
                    curContent.add(new Span(lineStart,lineEnd));
                }
                
                else {
                    curTag.setSourceRange(curTag.getSourceStart(),tagLine.end);
                    
                    curContent.add(new Span(lineStart,lineEnd));
                }
            }
            
            curTag.setSlice(new Slice(fileContent,curContent
                    .toArray(Span.EMPTY_ARRAY),0,' '));
        }
    }
    
    /**
     * 
     */
    private Span[] getTagLines(CommentValue commentValue) {
        ArrayList<Span> tagLines = null;
        int commentStart = commentValue.start();
        int commentEnd = commentValue.end();
        LineInfo lineInfo = new LineInfo(fileContent,commentStart,commentEnd);
        int lineCount = lineInfo.getLineCount();
        
        for (int i=0;i<lineCount;i++) {
            int contentStart = commentStart + lineInfo.lineStart(i);
            int contentEnd = commentStart + lineInfo.lineContentEnd(i);
            
            if (contentStart < contentEnd) {
                contentStart = StringToolkit.skip(fileContent,contentStart,
                        contentEnd,StringToolkit.WHITESPACE,false);
                contentStart = StringToolkit.skip(fileContent,contentStart,
                        contentEnd,'*',false);
                contentStart = StringToolkit.skip(fileContent,contentStart,
                        contentEnd,StringToolkit.WHITESPACE,false);
                
                // add only non-empty lines
                if (contentStart < contentEnd) {
                    // TODO: skip WS and asterisk at the end of the line.
                    if (tagLines == null) {
                        tagLines = new ArrayList<>();
                    }
                    
                    tagLines.add(new Span(contentStart,contentEnd));
                }
            }
        }
        
        return tagLines != null ? tagLines.toArray(Span.EMPTY_ARRAY) : null;
    }
    
    /**
     * 
     */
    private Span getTagName(Span line) {
        
        // skip initial at-character
        int index = line.start;
        int newIndex = StringToolkit.skip(fileContent,index,index+1,'@',false);
        
        if (newIndex == index) {
            return null;
        }
        
        // skip optional name characters
        index = newIndex;
        newIndex = StringToolkit.skip(fileContent,index,index+1,
                StringToolkit.JAVA_IDENTIFIER_START,false);
        
        if (newIndex > index) {
            newIndex = StringToolkit.skip(fileContent,newIndex,line.end,
                    StringToolkit.JAVA_IDENTIFIER_PART,false);
        }
        
        return new Span(line.start,newIndex);
    }
    
    /**
     * 
     */
    private CommentType getCommentType(JavaTokenType tokenType) {
        switch(tokenType) {
        case END_OF_LINE_COMMENT:   return CommentType.END_OF_LINE;
        case TRADITIONAL_COMMENT:   return CommentType.TRADITIONAL;
        case DOCUMENTATION_COMMENT: return CommentType.DOCUMENTATION;
        default:
            throw new IllegalArgumentException("invalid token type: "+tokenType);
        }
    }
    
    //=====================
    // Declaration Methods
    //=====================
    
    /**
     * 
     */
    private void appendDeclarations() {
        int size = parserTokenList.size();
        int i = 0;
        
        while (i < size) {
            JavaToken curToken = parserTokenList.get(i);
            
            if (lastTypeDeclaration == null) {
                switch(curToken.type()) {
                case PACKAGE:
                    int end = parsePackage(i);
                    
                    if (end != -1) {
                        compilationUnit.appendChild(createPackage(i,end));
                        i = end;
                        continue;
                    }
                    break;
                
                case IMPORT:
                    end = parseImport(i);
                    
                    if (end != -1) {
                        compilationUnit.appendChild(createImport(i,end));
                        i = end;
                        continue;
                    }
                    break;
                
                default:
                    break;
                }
            }
            
            int end = parseTypeDeclaration(i);
            
            if (end != -1) {
                lastTypeDeclaration = createTypeDeclaration(i,end);
                compilationUnit.appendChild(lastTypeDeclaration);
                i = end;
                continue;
            }
            
            if (lastTypeDeclaration != null) {
                end = parseMethodDeclaration(i);
                
                if (end != -1) {
                    lastTypeDeclaration.getTypeDeclarationBody().appendChild(
                            createMethodDeclaration(i,end));
                    i = end;
                    continue;
                }
            }
            
            i++;
        }
    }
    
    /**
     * 
     */
    private int parsePackage(int i) {
        JavaToken token = parserTokenList.get(i);
        
        if ((token == null) || (token.type() != JavaTokenType.PACKAGE)) {
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        if ((token == null) || (token.type() != JavaTokenType.IDENTIFIER)) {
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        while (true) {
            if (token == null) {
                return -1;
            }
            
            if (token.type() != JavaTokenType.DOT) {
                break;
            }
            
            token = parserTokenList.get(++i);
            
            if ((token == null) || (token.type() != JavaTokenType.IDENTIFIER)) {
                return -1;
            }
            
            token = parserTokenList.get(++i);
        }
        
        if ((token == null) || (token.type() != JavaTokenType.SEMICOLON)) {
            return -1;
        }
        
        return i + 1;
    }
    
    /**
     * 
     */
    private JOMPackage createPackage(int start, int end) {
        JOMPackage packageNode = new JOMPackage();
        setSourceRange(packageNode,parserTokenList.get(start),
                parserTokenList.get(end-1));
        
        JOMName name = new JOMName();
        packageNode.appendChild(name);
        
        int i = start + 1;
        end--;
        
        while (i < end) {
            JavaToken token = parserTokenList.get(i++);
            
            if (token.type() != JavaTokenType.IDENTIFIER) {
                break;
            }
            
            name.appendChild(createNamePart(token));
            
            token = parserTokenList.get(i++);
        }
        
        return packageNode;
    }
    
    /**
     * 
     */
    private int parseImport(int i) {
        JavaToken token = parserTokenList.get(i);
        
        if ((token == null) || (token.type() != JavaTokenType.IMPORT)) {
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        if (token == null) {
            return -1;
        }
        
        if (token.type() == JavaTokenType.STATIC) {
            token = parserTokenList.get(++i);
        }
        
        if ((token == null) || (token.type() != JavaTokenType.IDENTIFIER)) {
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        while (true) {
            if (token == null) {
                return -1;
            }
            
            if (token.type() != JavaTokenType.DOT) {
                break;
            }
            
            token = parserTokenList.get(++i);
            
            if (token == null) {
                return -1;
            }
            
            JavaTokenType tokenType = token.type();
            
            if (tokenType != JavaTokenType.IDENTIFIER) {
                if (tokenType == JavaTokenType.TIMES) {
                    token = parserTokenList.get(++i);
                    break;
                }
                
                return -1;
            }
            
            token = parserTokenList.get(++i);
        }
        
        if ((token == null) || (token.type() != JavaTokenType.SEMICOLON)) {
            return -1;
        }
        
        return i + 1;
    }
    
    /**
     * 
     */
    private JOMImport createImport(int start, int end) {
        JOMImport importNode = new JOMImport();
        setSourceRange(importNode,parserTokenList.get(start),
                parserTokenList.get(end-1));
        
        JOMName name = new JOMName();
        importNode.appendChild(name);
        
        int i = start + 1;
        end--;
        
        JavaToken token = parserTokenList.get(i);
        
        if (token.type() == JavaTokenType.STATIC) {
            importNode.setIsStatic(true);
            i++;
        }
        
        while (i < end) {
            token = parserTokenList.get(i++);
            JavaTokenType tokenType = token.type();
            
            if (tokenType == JavaTokenType.TIMES) {
                importNode.setOnDemand(true);
                break;
            }
            
            name.appendChild(createNamePart(token));
            
            token = parserTokenList.get(i++);
        }
        
        return importNode;
    }
    
    /**
     * 
     */
    private int parseTypeDeclaration(int i) {
        JavaToken token = parserTokenList.get(i);
        
        while ((token != null) && isClassModifier(token)) {
            token = parserTokenList.get(++i);
        }
        
        if (token == null) {
            return -1;
        }
        
        if ((token.type() != JavaTokenType.CLASS) &&
            (token.type() != JavaTokenType.INTERFACE) &&
            (token.type() != JavaTokenType.ENUM)) {
            
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        if ((token == null) || (token.type() != JavaTokenType.IDENTIFIER)) {
            return -1;
        }
        
        return i + 1;
    }
    
    /**
     * 
     */
    private int parseMethodDeclaration(int i) {
        JavaToken token = parserTokenList.get(i);
        
        while ((token != null) && isMethodModifier(token)) {
            token = parserTokenList.get(++i);
        }
        
        if ((token == null) || !isType(token)) {
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        if ((token == null) || (token.type() != JavaTokenType.IDENTIFIER)) {
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        if ((token == null) || (token.type() != JavaTokenType.LEFT_PARENTHESIS)) {
            return -1;
        }
        
        token = parserTokenList.get(++i);
        
        if ((token == null) || (token.type() != JavaTokenType.RIGHT_PARENTHESIS)) {
            return -1;
        }
        
        return i + 1;
    }
    
    /**
     * 
     */
    private JOMTypeDeclaration createTypeDeclaration(int start, int end) {
        JOMTypeDeclaration decl = new JOMTypeDeclaration();
        JOMModifierList modifierList = null;
        int i = start;
        
        while (i < end) {
            JavaToken token = parserTokenList.get(i);
            
            if (!isClassModifier(token)) {
                break;
            }
            
            if (modifierList == null) {
                modifierList = new JOMModifierList();
            }
            
            modifierList.appendChild(createModifierNode(token));
            i++;
        }
        
        if (modifierList != null) {
            decl.appendChild(modifierList);
        }
        
        decl.setIsInterface(isInterface(parserTokenList.get(i++)));
        decl.appendChild(createSimpleName(parserTokenList.get(i++)));
        decl.setJavaDoc(findPreceedingJavaDoc(parserTokenList.get(start)));
        decl.appendChild(new JOMTypeDeclarationBody());
        
        return decl;
    }
    
    /**
     * 
     */
    private boolean isInterface(JavaToken token) {
        switch(token.type()) {
        case CLASS:
        case ENUM:
            return false;
        case INTERFACE: return true;
        default:
            throw new IllegalArgumentException("invalid token type: "+token.type());
        }
    }
    
    /**
     * 
     */
    private JOMMethodDeclaration createMethodDeclaration(int start, int end) {
        JOMMethodDeclaration decl = new JOMMethodDeclaration();
        JOMModifierList modifierList = null;
        int i = start;
        
        while (i < end) {
            JavaToken token = parserTokenList.get(i);
            
            if (!isMethodModifier(token)) {
                break;
            }
            
            if (modifierList == null) {
                modifierList = new JOMModifierList();
            }
            
            modifierList.appendChild(createModifierNode(token));
            i++;
        }
        
        if (modifierList != null) {
            decl.appendChild(modifierList);
        }
        
        decl.appendChild(createType(parserTokenList.get(i++)));
        decl.appendChild(createSimpleName(parserTokenList.get(i++)));
        decl.setJavaDoc(findPreceedingJavaDoc(parserTokenList.get(start)));
        
        return decl;
    }
    
    /**
     * 
     */
    private JOMComment findPreceedingJavaDoc(JavaToken token) {
        JavaToken prevToken = fullTokenList.get(fullTokenList.indexOf(token),
                true,JAVADOC_SKIP_TOKENS);
        
        if ((prevToken != null) &&
            (prevToken.type() == JavaTokenType.DOCUMENTATION_COMMENT)) {
            
            return commentMap.get(prevToken);
        }
        
        return null;
    }
    
    //==============
    // Type Methods
    //==============
    
    /**
     * 
     */
    private JOMType createType(JavaToken token) {
        JOMType type = new JOMType();
        setSourceRange(type,token);
        type.setText(getTypeText(token));
        
        return type;
    }
    
    /**
     * 
     */
    private String getTypeText(JavaToken token) {
        switch(token.type()) {
        case VOID:       return "void";
        case BOOLEAN:    return "boolean";
        case BYTE:       return "byte";
        case SHORT:      return "short";
        case INT:        return "int";
        case LONG:       return "long";
        case FLOAT:      return "float";
        case DOUBLE:     return "double";
        case CHAR:       return "char";
        case IDENTIFIER: return (String)token.value();
        default:         return null;
        }
    }
    
    //==============
    // Name Methods
    //==============
    
    /**
     * 
     */
    private JOMName createSimpleName(JavaToken namePartToken) {
        JOMName name = new JOMName();
        name.appendChild(createNamePart(namePartToken));
        
        return name;
    }
    
    /**
     * 
     */
    private JOMNamePart createNamePart(JavaToken namePartToken) {
        JOMNamePart namePart = new JOMNamePart();
        namePart.setText((String)namePartToken.value());
        setSourceRange(namePart,namePartToken);
        
        return namePart;
    }
    
    //==================
    // Modifier Methods
    //==================
    
    /**
     * 
     */
    private JOMModifier createModifierNode(JavaToken token) {
        JOMModifier modifier = new JOMModifier();
        setSourceRange(modifier,token);
        modifier.setModifier(getModifier(token.type()));
        
        return modifier;
    }
    
    /**
     * 
     */
    private Modifier getModifier(JavaTokenType tokenType) {
        switch(tokenType) {
        case PUBLIC:       return Modifier.PUBLIC;
        case PROTECTED:    return Modifier.PROTECTED;
        case PRIVATE:      return Modifier.PRIVATE;
        case STATIC:       return Modifier.STATIC;
        case FINAL:        return Modifier.FINAL;
        case ABSTRACT:     return Modifier.ABSTRACT;
        case NATIVE:       return Modifier.NATIVE;
        case STRICTFP:     return Modifier.STRICTFP;
        case SYNCHRONIZED: return Modifier.SYNCHRONIZED;
        case TRANSIENT:    return Modifier.TRANSIENT;
        case VOLATILE:     return Modifier.VOLATILE;
        case CONST:        return Modifier.CONST;
        default:
            throw new IllegalArgumentException("invalid token type: "+tokenType);
        }
    }
    
    //======================
    // Token Helper Methods
    //======================
    
    /**
     * 
     */
    private void setSourceRange(JOMNode node, JavaToken token) {
        node.setSourceRange(token.start(),token.end());
    }
    
    /**
     * 
     */
    private void setSourceRange(JOMNode node, JavaToken startToken,
            JavaToken endToken) {
        
        node.setSourceRange(startToken.start(),endToken.end());
    }
    
    /**
     * 
     */
    private boolean isClassModifier(JavaToken token) {
        switch(token.type()) {
        case PUBLIC:
        case PROTECTED:
        case PRIVATE:
        case ABSTRACT:
        case STATIC:
        case FINAL:
        case STRICTFP:
            return true;
        default: return false;
        }
    }
    
    /**
     * 
     */
    private boolean isMethodModifier(JavaToken token) {
        switch(token.type()) {
        case PUBLIC:
        case PROTECTED:
        case PRIVATE:
        case ABSTRACT:
        case STATIC:
        case FINAL:
        case SYNCHRONIZED:
        case NATIVE:
        case STRICTFP:
            return true;
        default: return false;
        }
    }
    
    /**
     * 
     */
    private boolean isType(JavaToken token) {
        switch(token.type()) {
        case VOID:
        case BOOLEAN:
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
        case CHAR:
        case FLOAT:
        case DOUBLE:
        case IDENTIFIER:
            return true;
        default: return false;
        }
    }
    
    /**
     * 
     */
    private boolean isComment(JavaToken token) {
        switch(token.type()) {
        case END_OF_LINE_COMMENT:
        case TRADITIONAL_COMMENT:
        case DOCUMENTATION_COMMENT:
            return true;
        default: return false;
        }
    }
}
