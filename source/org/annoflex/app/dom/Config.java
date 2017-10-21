/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

import java.util.ArrayList;

import org.annoflex.app.TextInfo;
import org.annoflex.regex.automaton.Rule;
import org.annoflex.util.text.Section;
import org.annoflex.util.text.Span;

/**
 * @author Stefan Czaska
 */
public class Config {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final TextInfo textInfo;
    
    /**
     * 
     */
    private Span prologComment;
    
    /**
     * 
     */
    private Span packageDeclaration;
    
    /**
     * 
     */
    private final ArrayList<ImportInfo> imports = new ArrayList<>();
    
    /**
     * 
     */
    private Span typeDeclaration;
    
    /**
     * 
     */
    private Section codeArea;
    
    /**
     * 
     */
    private final ArrayList<ConditionArea> conditionAreas = new ArrayList<>();
    
    /**
     * 
     */
    private final Options options = new Options();
    
    /**
     * 
     */
    private final ArrayList<Rule<MethodInfo>> ruleList = new ArrayList<>();
    
    /**
     * 
     */
    private String returnType = "void";
    
    /**
     * 
     */
    private boolean hasAlsoVoidReturnType;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Config(TextInfo textInfo) {
        if (textInfo == null) {
            throw new IllegalArgumentException("text info may not be null");
        }
        
        this.textInfo = textInfo;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public TextInfo getTextInfo() {
        return textInfo;
    }
    
    /**
     * 
     */
    public void setPrologComment(Span prologComment) {
        this.prologComment = prologComment;
    }
    
    /**
     * 
     */
    public Span getPrologComment() {
        return prologComment;
    }
    
    /**
     * 
     */
    public void setPackageDeclaration(Span packageDeclaration) {
        this.packageDeclaration = packageDeclaration;
    }
    
    /**
     * 
     */
    public Span getPackageDeclaration() {
        return packageDeclaration;
    }
    
    /**
     * 
     */
    public ArrayList<ImportInfo> getImports() {
        return imports;
    }
    
    /**
     * 
     */
    public void setTypeDeclaration(Span typeDeclaration) {
        this.typeDeclaration = typeDeclaration;
    }
    
    /**
     * 
     */
    public Span getTypeDeclaration() {
        return typeDeclaration;
    }
    
    /**
     * 
     */
    public void setCodeArea(Section codeArea) {
        this.codeArea = codeArea;
    }
    
    /**
     * 
     */
    public Section getCodeArea() {
        return codeArea;
    }
    
    /**
     * 
     */
    public ArrayList<ConditionArea> getConditionAreas() {
        return conditionAreas;
    }
    
    /**
     * 
     */
    public Options getOptions() {
        return options;
    }
    
    /**
     * 
     */
    public ArrayList<Rule<MethodInfo>> getRuleList() {
        return ruleList;
    }
    
    //=====================
    // Return Type Methods
    //=====================
    
    /**
     * 
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    
    /**
     * 
     */
    public String getReturnType() {
        return returnType;
    }
    
    /**
     * 
     */
    public String getUsedReturnType() {
        if ((returnType == null) || returnType.equals("void")) {
            return "boolean";
        }
        
        return returnType;
    }
    
    /**
     * 
     */
    public String getReturnTypeDefaultValue() {
        if ((returnType == null) || returnType.equals("void")) {
            return "false";
        }
        
        switch(returnType) {
        case "byte":
        case "short":
        case "int":
        case "long":
        case "float":
        case "double":
            return "-1";
        
        case "char":
            return "0";
        
        case "boolean":
            return "false";
        }
        
        return "null";
    }
    
    /**
     * 
     */
    public void setHasAlsoVoidReturnType(boolean enabled) {
        hasAlsoVoidReturnType = enabled;
    }
    
    /**
     * 
     */
    public boolean getHasAlsoVoidReturnType() {
        return hasAlsoVoidReturnType;
    }
}
