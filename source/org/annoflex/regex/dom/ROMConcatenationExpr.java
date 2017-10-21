/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMConcatenationExpr extends ROMSimpleExpr {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMConcatenationExpr(ROMNode node1, ROMNode node2) {
        super(ROMNodeType.CONCATENATION_EXPR);
        
        appendChild(node1);
        appendChild(node2);
    }
}
