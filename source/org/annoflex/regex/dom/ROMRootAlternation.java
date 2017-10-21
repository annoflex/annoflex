/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMRootAlternation extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMRootAlternation(ROMNode node) {
        super(ROMNodeType.ROOT_ALTERNATION);
        
        appendChild(node);
    }
}
