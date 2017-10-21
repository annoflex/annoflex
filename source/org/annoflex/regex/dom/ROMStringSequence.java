/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMStringSequence extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMStringSequence(ROMNode stringElement) {
        super(ROMNodeType.STRING_SEQUENCE);
        
        appendChild(stringElement);
    }
}
