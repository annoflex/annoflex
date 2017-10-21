/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class Exactly extends QuantifierExpression {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Exactly(Expression expression, int number) {
        super(expression,Quantifier.createExactly(number));
    }
}
