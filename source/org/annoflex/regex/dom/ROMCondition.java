/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMCondition extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMCondition() {
        super(ROMNodeType.CONDITION);
    }
    
    /**
     * 
     */
    public ROMCondition(ROMNode nameList) {
        super(ROMNodeType.CONDITION);
        
        appendChild(nameList);
    }
}
