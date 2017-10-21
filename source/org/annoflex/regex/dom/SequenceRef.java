/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class SequenceRef {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final SequenceRefType type;
    
    /**
     * 
     */
    private final String quoteValue;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public SequenceRef(SequenceRefType type) {
        if (type == null) {
            throw new IllegalArgumentException("type may not be null");
        }
        
        if (type != SequenceRefType.NEWLINE) {
            throw new IllegalArgumentException("invalid sequence type: "+type);
        }
        
        this.type = type;
        quoteValue = null;
    }
    
    /**
     * 
     */
    public SequenceRef(String quoteValue) {
        if (quoteValue == null) {
            throw new IllegalArgumentException("quote value may not be null");
        }
        
        if (quoteValue.isEmpty()) {
            throw new IllegalArgumentException("quote value may not be empty");
        }
        
        this.type = SequenceRefType.QUOTE;
        this.quoteValue = quoteValue;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final SequenceRefType type() {
        return type;
    }
    
    /**
     * 
     */
    public final String quoteValue() {
        return quoteValue;
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
        case NEWLINE:
            builder.append("newline sequence");
            break;
        
        case QUOTE:
            builder.append("quote=");
            builder.append(quoteValue);
            break;
        
        default:
            throw new IllegalStateException("invalid type: "+type);
        }
        
        builder.append("]");
        
        return builder.toString();
    }
}
