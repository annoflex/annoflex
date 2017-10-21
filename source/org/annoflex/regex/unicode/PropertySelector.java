/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

import java.util.EnumMap;

/**
 * @author Stefan Czaska
 */
public class PropertySelector {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final EnumMap<Property,PropertySelector> BINARY_SELECTORS =
            new EnumMap<>(Property.class);
    
    /**
     * 
     */
    private static final EnumMap<Property,PropertySelector> BINARY_COMP_SELECTORS =
            new EnumMap<>(Property.class);
    
    /**
     * 
     */
    static {
        Property[] values = Property.values();
        
        for (int i=0;i<values.length;i++) {
            Property property = values[i];
            
            if (property.isBinary()) {
                BINARY_SELECTORS.put(property,new PropertySelector(property.getLongName(),false));
                BINARY_COMP_SELECTORS.put(property,new PropertySelector(property.getLongName(),true));
            }
        }
    }
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String text;
    
    /**
     * 
     */
    private final boolean invert;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public PropertySelector(String text, boolean invert) {
        this.text = text;
        this.invert = invert;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final String getText() {
        return text;
    }
    
    /**
     * 
     */
    public final boolean getInvert() {
        return invert;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(getClass().getSimpleName());
        builder.append("[text=");
        builder.append(text);
        builder.append(",invert=");
        builder.append(invert);
        builder.append("]");
        
        return builder.toString();
    }
    
    //=================
    // Factory Methods
    //=================
    
    /**
     * 
     */
    public static PropertySelector forBinary(Property property, boolean complement) {
        if (property == null) {
            throw new IllegalArgumentException("property may not be null");
        }
        
        if (!property.isBinary()) {
            throw new IllegalArgumentException("property is not a binary property: "+property);
        }
        
        return complement ? BINARY_COMP_SELECTORS.get(property) :
            BINARY_SELECTORS.get(property);
    }
}
