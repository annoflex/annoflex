/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.token;

/**
 * @author Stefan Czaska
 */
public class Token<T,V> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final T type;
    
    /**
     * 
     */
    private final int start;
    
    /**
     * 
     */
    private final int end;
    
    /**
     * 
     */
    private final V value;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Token(T type, int start, int end, V value) {
        if (type == null) {
            throw new IllegalArgumentException("type may not be null");
        }
        
        if (start < 0) {
            throw new IllegalArgumentException("start may not be negative: "+start);
        }
        
        if (end <= start) {
            throw new IllegalArgumentException("end may not be lower or equal "
                    + "than start: end="+end+", start="+start);
        }
        
        this.type = type;
        this.start = start;
        this.end = end;
        this.value = value;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final T type() {
        return type;
    }
    
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
    
    /**
     * 
     */
    public final V value() {
        return value;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return ((start * 2031647) + ((end-start) * 257)) ^ super.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[type=");
        buffer.append(type);
        buffer.append(",start=");
        buffer.append(start);
        buffer.append(",end=");
        buffer.append(end);
        buffer.append(",value=");
        buffer.append(value);
        buffer.append("]");
        
        return buffer.toString();
    }
}
