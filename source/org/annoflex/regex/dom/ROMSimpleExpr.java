/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public abstract class ROMSimpleExpr extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMSimpleExpr(ROMNodeType nodeType) {
        super(nodeType);
    }
}
