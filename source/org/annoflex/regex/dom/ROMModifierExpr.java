/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMModifierExpr extends ROMSimpleExpr {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMModifierExpr(ROMNode modifier, ROMNode simpleExpr) {
        super(ROMNodeType.MODIFIER_EXPR);
        
        appendChild(modifier);
        appendChild(simpleExpr);
    }
}
