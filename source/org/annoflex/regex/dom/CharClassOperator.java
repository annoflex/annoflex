/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public enum CharClassOperator {
    
    UNION("||"),
    INTERSECTION("&&"),
    SET_DIFFERENCE("--"),
    SYMMETRIC_DIFFERENCE("~~");
    
    /**
     * 
     */
    private final String alias;
    
    /**
     * 
     */
    private CharClassOperator(String alias) {
        this.alias = alias;
    }
    
    /**
     * 
     */
    public final String alias() {
        return alias;
    }
}
