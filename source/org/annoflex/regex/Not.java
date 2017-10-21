/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class Not extends ModifierExpression {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Not(Expression expression) {
        super(Modifier.NOT,expression);
    }
}
