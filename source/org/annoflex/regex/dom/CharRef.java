/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class CharRef {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final CharRefType type;
    
    /**
     * 
     */
    private final char charValue;
    
    /**
     * 
     */
    private final String charName;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public CharRef(char charValue) {
        this.type = CharRefType.VALUE;
        this.charValue = charValue;
        charName = null;
    }
    
    /**
     * 
     */
    public CharRef(String charName) {
        if (charName == null) {
            throw new IllegalArgumentException("char name may not be null");
        }
        // TODO: Validate char name according to Unicode specification.
        if (charName.isEmpty()) {
            throw new IllegalArgumentException("char name may not be empty");
        }
        
        this.type = CharRefType.NAME;
        this.charName = charName;
        charValue = 0;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final CharRefType type() {
        return type;
    }
    
    /**
     * 
     */
    public final char charValue() {
        return charValue;
    }
    
    /**
     * 
     */
    public final String charName() {
        return charName;
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
        builder.append("[");
        
        switch(type) {
        case VALUE:
            builder.append("value=");
            builder.append((int)charValue);
            builder.append(" (hex: ");
            builder.append(Integer.toHexString(charValue));
            builder.append(", octal: ");
            builder.append(Integer.toOctalString(charValue));
            builder.append("), char=");
            builder.append(charValue);
            break;
        
        case NAME:
            builder.append("name=");
            builder.append(charName);
            break;
        
        default:
            throw new IllegalStateException("invalid type: "+type);
        }
        
        builder.append("]");
        
        return builder.toString();
    }
}
