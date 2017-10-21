/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util;

/**
 * @author Stefan Czaska
 */
@SuppressWarnings("serial")
public abstract class LongBreak extends RuntimeException {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public LongBreak() {
        super(null,null,false,false);
    }
    
    /**
     * 
     */
    public LongBreak(Throwable cause) {
        super(null,cause,false,false);
    }
}
