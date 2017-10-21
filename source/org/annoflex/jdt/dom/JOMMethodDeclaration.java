/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class JOMMethodDeclaration extends JOMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private JOMComment javaDoc;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMMethodDeclaration() {
        super(JOMNodeType.METHOD_DECLARATION);
        
        setSourceRangeAuto(true);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public JOMModifierList getModifierList() {
        return (JOMModifierList)getChildByType(JOMNodeType.MODIFIER_LIST,false);
    }
    
    /**
     * 
     */
    public JOMType getReturnType() {
        return (JOMType)getChildByType(JOMNodeType.TYPE,false);
    }
    
    /**
     * 
     */
    public JOMName getName() {
        return (JOMName)getChildByType(JOMNodeType.NAME,false);
    }
    
    /**
     * 
     */
    public void setJavaDoc(JOMComment newJavaDoc) {
        javaDoc = newJavaDoc;
    }
    
    /**
     * 
     */
    public JOMComment getJavaDoc() {
        return javaDoc;
    }
}
