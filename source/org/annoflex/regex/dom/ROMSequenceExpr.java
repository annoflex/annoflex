/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMSequenceExpr extends ROMSimpleExpr {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMSequenceExpr(ROMNode node) {
        super(ROMNodeType.SEQUENCE_EXPR);
        
        appendChild(node);
    }
}
