/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMQuantifierExpr extends ROMSimpleExpr {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMQuantifierExpr(ROMNode simpleExpr, ROMNode quantifier) {
        super(ROMNodeType.QUANTIFIER_EXPR);
        
        appendChild(simpleExpr);
        appendChild(quantifier);
    }
}
