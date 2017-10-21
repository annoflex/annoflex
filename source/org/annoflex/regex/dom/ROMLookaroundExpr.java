/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMLookaroundExpr extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMLookaroundExpr(ROMNode node) {
        super(ROMNodeType.LOOKAROUND_EXPR);
        
        appendChild(node);
    }
    
    /**
     * 
     */
    public ROMLookaroundExpr(ROMNode node1, ROMNode node2) {
        super(ROMNodeType.LOOKAROUND_EXPR);
        
        appendChild(node1);
        appendChild(node2);
    }
}
