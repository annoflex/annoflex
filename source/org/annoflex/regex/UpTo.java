/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class UpTo extends QuantifierExpression {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public UpTo(Expression expression, int max) {
        super(expression,Quantifier.createUpTo(max));
    }
}
