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
public class MethodInfo {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String name;
    
    /**
     * 
     */
    private final String returnType;
    
    /**
     * 
     */
    private final Span returnTypeSpan;
    
    /**
     * 
     */
    private final Span exprSpan;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public MethodInfo(String name, String returnType, Span returnTypeSpan,
            Span exprSpan) {
        
        this.name = name;
        this.returnType = returnType;
        this.returnTypeSpan = returnTypeSpan;
        this.exprSpan = exprSpan;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final String getName() {
        return name;
    }
    
    /**
     * 
     */
    public final String getReturnType() {
        return returnType;
    }
    
    /**
     * 
     */
    public final Span getReturnTypeSpan() {
        return returnTypeSpan;
    }
    
    /**
     * 
     */
    public final Span getExprSpan() {
        return exprSpan;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[methodName=");
        buffer.append(name);
        buffer.append(",returnType=");
        buffer.append(returnType);
        buffer.append(",returnTypeSpan=");
        buffer.append(returnTypeSpan);
        buffer.append("]");
        
        return buffer.toString();
    }
}
