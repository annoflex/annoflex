/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMRootElement extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMRootElement(ROMNode node) {
        super(ROMNodeType.ROOT_ELEMENT);
        
        appendChild(node);
    }
    
    /**
     * 
     */
    public ROMRootElement(ROMNode node1, ROMNode node2) {
        super(ROMNodeType.ROOT_ELEMENT);
        
        appendChild(node1);
        appendChild(node2);
    }
}
