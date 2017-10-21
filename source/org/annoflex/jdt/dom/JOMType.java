/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class JOMType extends JOMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private String text;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMType() {
        super(JOMNodeType.TYPE);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * 
     */
    public String getText() {
        return text;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return text;
    }
}
