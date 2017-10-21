/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;


/**
 * @author Stefan Czaska
 */
public class ROMCCOperator extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final CharClassOperator operator;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMCCOperator(CharClassOperator operator) {
        super(ROMNodeType.CC_OPERATOR);
        
        if (operator == null) {
            throw new IllegalArgumentException("operator may not be null");
        }
        
        this.operator = operator;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final CharClassOperator getCharClassOperator() {
        return operator;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return operator.toString()+","+operator.alias();
    }
}
