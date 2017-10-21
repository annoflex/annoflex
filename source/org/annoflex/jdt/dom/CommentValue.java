/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class CommentValue {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int start;
    
    /**
     * 
     */
    private final int end;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public CommentValue(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final int start() {
        return start;
    }
    
    /**
     * 
     */
    public final int end() {
        return end;
    }
    
    /**
     * 
     */
    public final int length() {
        return end - start;
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
        builder.append("[start=");
        builder.append(start);
        builder.append(",end=");
        builder.append(end);
        builder.append("]");
        
        return builder.toString();
    }
}
