/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

/**
 * @author Stefan Czaska
 */
public class Section {
    
    //========
    // Fields
    //========
    
    /**
     * The start of the section.
     */
    private final Span start;
    
    /**
     * The end of the section.
     */
    private final Span end;
    
    //==============
    // Constructors
    //==============
    
    /**
     * Constructs a new {@link Section}.
     * 
     * @param start The start of the section.
     * @param end The end of the section.
     */
    public Section(Span start, Span end) {
        if (start == null) {
            throw new IllegalArgumentException("start may not be null");
        }
        
        if (end == null) {
            throw new IllegalArgumentException("end may not be null");
        }
        
        if (start.end > end.start) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        this.start = start;
        this.end = end;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * Returns the start of the section.
     * 
     * @return The start of the section.
     */
    public final Span getStart() {
        return start;
    }
    
    /**
     * Returns the end of the section.
     * 
     * @return The end of the section.
     */
    public final Span getEnd() {
        return end;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof Section) {
            Section section = (Section)obj;
            
            return section.start.equals(start) && section.end.equals(end);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return start.hashCode() ^ end.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[start=");
        buffer.append(start);
        buffer.append(",end=");
        buffer.append(end);
        buffer.append("]");
        
        return buffer.toString();
    }
}
