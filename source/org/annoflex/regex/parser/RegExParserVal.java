/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.parser;

import org.annoflex.regex.dom.ROMNode;

/**
 * @author Stefan Czaska
 */
final class RegExParserVal {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    public final RegExToken t;
    
    /**
     * 
     */
    public final ROMNode n;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public RegExParserVal() {
        t = null;
        n = null;
    }
    
    /**
     * 
     */
    public RegExParserVal(RegExToken t) {
        this.t = t;
        n = null;
    }
    
    /**
     * 
     */
    public RegExParserVal(ROMNode n) {
        t = null;
        this.n = n;
    }
}
