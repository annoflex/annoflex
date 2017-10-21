/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util;

/**
 * @author Stefan Czaska
 */
public class Message {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String id;
    
    /**
     * 
     */
    private final Class<?> anchorClass;
    
    /**
     * 
     */
    private final String bundleName;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Message(Class<?> anchorClass, String id) {
        this(anchorClass,id,"messages");
    }
    
    /**
     * 
     */
    public Message(Class<?> anchorClass, String id, String bundleSuffix) {
        if (anchorClass == null) {
            throw new IllegalArgumentException("anchor class may not be null");
        }
        
        this.anchorClass = anchorClass;
        
        if (id == null) {
            throw new IllegalArgumentException("id may not be null");
        }
        
        this.id = id;
        
        if (bundleSuffix == null) {
            throw new IllegalArgumentException("bundle suffix may not be null");
        }
        
        this.bundleName = anchorClass.getPackage().getName() + "." + bundleSuffix;
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public final String getId() {
        return id;
    }
    
    /**
     * 
     */
    public final Class<?> getAnchorClass() {
        return anchorClass;
    }
    
    /**
     * 
     */
    public final String getBundleName() {
        return bundleName;
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
        builder.append(anchorClass.getPackage().getName());
        builder.append(".");
        builder.append(id);
        builder.append("]");
        
        return builder.toString();
    }
}
