/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

import org.annoflex.util.text.Slice;
import org.annoflex.util.text.Span;

/**
 * @author Stefan Czaska
 */
public class JOMTag extends JOMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private String name;
    
    /**
     * 
     */
    private Slice slice;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMTag() {
        super(JOMNodeType.TAG);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setName(String newName) {
        name = newName;
    }
    
    /**
     * 
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     */
    public void setSlice(Slice newSlice) {
        slice = newSlice;
    }
    
    /**
     * 
     */
    public Slice getSlice() {
        return slice;
    }
    
    /**
     * 
     */
    public Span getSliceSourceRange(Span span) {
        return slice != null ? slice.srcSpan(span) : null;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return name != null ? ("name=" + name) : null;
    }
}
