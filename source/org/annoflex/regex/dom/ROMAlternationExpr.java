/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMAlternationExpr extends ROMSimpleExpr {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMAlternationExpr(ROMNode node1, ROMNode node2) {
        super(ROMNodeType.ALTERNATION_EXPR);
        
        appendChild(node1);
        appendChild(node2);
    }
}
