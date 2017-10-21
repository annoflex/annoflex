/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class FromTo extends QuantifierExpression {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public FromTo(Expression expression, int min, int max) {
        super(expression,Quantifier.create(min,max));
    }
}
