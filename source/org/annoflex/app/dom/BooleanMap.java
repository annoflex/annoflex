/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

/**
 * @author Stefan Czaska
 */
public class BooleanMap extends MemberMap<Boolean> {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public BooleanMap(boolean defaultValue) {
        super(defaultValue);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public boolean has(Member member) {
        return get(member);
    }
}
