/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMCCRange extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMCCRange(ROMNode char1, ROMNode char2) {
        super(ROMNodeType.CC_RANGE);
        
        appendChild(char1);
        appendChild(char2);
    }
}
