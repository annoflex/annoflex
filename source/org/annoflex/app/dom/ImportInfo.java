/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

import org.annoflex.util.text.Span;

/**
 * @author Stefan Czaska
 */
public class ImportInfo {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final TypeDescriptor type;
    
    /**
     * 
     */
    private final boolean isStatic;
    
    /**
     * 
     */
    private final boolean onDemand;
    
    /**
     * 
     */
    private final Span sourceRange;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ImportInfo(TypeDescriptor type, boolean isStatic,
            boolean onDemand, Span sourceRange) {
        
        if (type == null) {
            throw new IllegalArgumentException("type may not be null");
        }
        
        if (sourceRange == null) {
            throw new IllegalArgumentException("source range may not be null");
        }
        
        this.type = type;
        this.isStatic = isStatic;
        this.onDemand = onDemand;
        this.sourceRange = sourceRange;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public TypeDescriptor getType() {
        return type;
    }
    
    /**
     * 
     */
    public boolean getIsStatic() {
        return isStatic;
    }
    
    /**
     * 
     */
    public boolean getOnDemand() {
        return onDemand;
    }
    
    /**
     * 
     */
    public Span getSourceRange() {
        return sourceRange;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        if (isStatic) {
            builder.append("static ");
        }
        
        builder.append(type.getQualifiedName());
        
        if (onDemand) {
            builder.append(".*");
        }
        
        return builder.toString();
    }
}
