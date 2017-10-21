/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public enum ExpressionType {
    
    // Note: Types are sorted by priority.
    
    CHAR_CLASS,
    MODIFIER,
    QUANTIFIER,
    CONCATENATION,
    ALTERNATION,
    LOOKAHEAD,
    CONDITION;
    
    /**
     * 
     */
    public final int getTypeSetMask() {
        return 1 << ordinal();
    }
    
    /**
     * 
     */
    public final boolean isContainedIn(int typeSet) {
        return (typeSet & getTypeSetMask()) != 0;
    }
}
