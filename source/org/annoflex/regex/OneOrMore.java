/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class OneOrMore extends QuantifierExpression {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public OneOrMore(Expression expression) {
        super(expression,Quantifier.ONE_OR_MORE);
    }
}
