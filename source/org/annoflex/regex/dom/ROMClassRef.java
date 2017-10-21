/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

import org.annoflex.regex.unicode.PropertySelector;

/**
 * @author Stefan Czaska
 */
public class ROMClassRef extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final PropertySelector propertySelector;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMClassRef(PropertySelector propertySelector) {
        super(ROMNodeType.CLASS_REF);
        
        if (propertySelector == null) {
            throw new IllegalArgumentException("property selector may not be null");
        }
        
        this.propertySelector = propertySelector;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final PropertySelector getPropertySelector() {
        return propertySelector;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return propertySelector.toString();
    }
}
