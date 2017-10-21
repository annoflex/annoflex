/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class Until extends ModifierExpression {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Until(Expression expression) {
        super(Modifier.UNTIL,expression);
    }
}
