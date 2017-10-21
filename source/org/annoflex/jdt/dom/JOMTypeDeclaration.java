/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class JOMTypeDeclaration extends JOMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private boolean isInterface;
    
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
    public JOMTypeDeclaration() {
        super(JOMNodeType.TYPE_DECLARATION);
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
    public void setIsInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }
    
    /**
     * 
     */
    public boolean getIsInterface() {
        return isInterface;
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
    
    /**
     * 
     */
    public JOMTypeDeclarationBody getTypeDeclarationBody() {
        return (JOMTypeDeclarationBody)getChildByType(JOMNodeType.TYPE_DECLARATION_BODY,false);
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return isInterface ? "interface" : null;
    }
}
