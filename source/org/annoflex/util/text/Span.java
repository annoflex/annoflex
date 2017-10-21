/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import org.annoflex.util.integer.IntHandler;

/**
 * This class can be used to define content ranges inside strings or similar
 * index-based data structures.
 * 
 * @author Stefan Czaska
 */
public class Span {
    
    //===========
    // Constants
    //===========
    
    /**
     * The empty span which starts at the origin and has a length of zero.
     */
    public static final Span EMPTY_SPAN = new Span(0,0);
    
    /**
     * The empty array for spans.
     */
    public static final Span[] EMPTY_ARRAY = new Span[]{};
    
    //========
    // Fields
    //========
    
    /**
     * The start index (inclusive) of the span.
     */
    public final int start;
    
    /**
     * The end index (exclusive) of the span.
     */
    public final int end;
    
    //==============
    // Constructors
    //==============
    
    /**
     * Constructs a {@link Span}.
     * 
     * @param start The start index (inclusive) of the span.
     * @param end The end index (exclusive) of the span.
     */
    public Span(int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException("start may not be negative");
        }
        
        if (end < 0) {
            throw new IllegalArgumentException("end may not be negative");
        }
        
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        this.start = start;
        this.end = end;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * Returns the length of this span.
     * 
     * @return The length of this span.
     */
    public final int length() {
        return end - start;
    }
    
    /**
     * Returns whether the content values of this span equal specified values.
     * 
     * @param start The start value to be checked.
     * @param end The end value to be checked.
     * @return True if the values of this span equal the specified values,
     * otherwise false.
     */
    public boolean equals(int start, int end) {
        return (start == this.start) && (end == this.end);
    }
    
    /**
     * Makes the indices of this span absolute to a parent span.
     * 
     * @param parent A parent span to which this span should be made absolute.
     * @return A new span which is absolute to the specified parent span.
     */
    public Span makeAbsoluteTo(Span parent) {
        return new Span(parent.start+start,parent.start+end);
    }
    
    //================
    // String Methods
    //================
    
    /**
     * Creates a substring of a specified string by using the indices of this
     * span.
     * 
     * @param string A string for which a substring should be created.
     * @return The substring of the specifies string.
     */
    public String substring(String string) {
        return string.substring(start,end);
    }
    
    /**
     * Performs a whitespace trim of this span against a specified string.
     * 
     * @param string A string whose content is used to trim this span.
     * @return The trimmed span.
     */
    public Span trim(String string) {
        return trim(string,StringToolkit.WHITESPACE);
    }
    
    /**
     * Trims this span against a specified string using a specified character
     * handler.
     * 
     * @param string A string whose content is used to trim this span.
     * @param handler An {@link IntHandler} which is used to skip characters.
     * @return The trimmed span.
     */
    public Span trim(String string, IntHandler handler) {
        if ((string != null) && (handler != null)) {
            int spanStart = start;
            int spanEnd = end;
            
            spanStart = StringToolkit.skip(string,spanStart,spanEnd,handler,false);
            spanEnd = StringToolkit.skip(string,spanEnd,spanStart,handler,true);
            
            if ((spanStart == start) && (spanEnd == end)) {
                return this;
            }
            
            return new Span(spanStart,spanEnd);
        }
        
        return null;
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
        
        if (obj instanceof Span) {
            Span span = (Span)obj;
            
            return (span.start == start) && (span.end == end);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (start * 2031647) + ((end - start) * 257);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[start=");
        buffer.append(start);
        buffer.append(",length=");
        buffer.append(length());
        buffer.append("]");
        
        return buffer.toString();
    }
}
