/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class ZeroOrMore extends QuantifierExpression {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ZeroOrMore(Expression expression) {
        super(expression,Quantifier.ZERO_OR_MORE);
    }
}
