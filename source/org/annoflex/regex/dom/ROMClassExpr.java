/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMClassExpr extends ROMSimpleExpr {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMClassExpr(ROMNode node) {
        super(ROMNodeType.CLASS_EXPR);
        
        appendChild(node);
    }
}
