/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMCCSequence extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMCCSequence(ROMNode node) {
        super(ROMNodeType.CC_SEQUENCE);
        
        appendChild(node);
    }
    
    /**
     * 
     */
    public ROMCCSequence(ROMNode node1, ROMNode node2) {
        super(ROMNodeType.CC_SEQUENCE);
        
        appendChild(node1);
        appendChild(node2);
    }
}
