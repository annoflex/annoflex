/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class TypeDescriptor {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String qualifiedName;
    
    /**
     * 
     */
    private final String[] components;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    private TypeDescriptor(String qualifiedName, String[] components) {
        this.qualifiedName = qualifiedName;
        this.components = components;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public int getComponentCount() {
        return components.length;
    }
    
    /**
     * 
     */
    public String getComponent(int index) {
        return components[index];
    }
    
    /**
     * 
     */
    public String getQualifiedName() {
        return qualifiedName;
    }
    
    /**
     * 
     */
    public String getSimpleName() {
        return getComponent(getComponentCount()-1);
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
        
        if (obj instanceof TypeDescriptor) {
            return ((TypeDescriptor)obj).qualifiedName.equals(qualifiedName);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return qualifiedName.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[qualifiedName=");
        buffer.append(qualifiedName);
        buffer.append(",simpleName=");
        buffer.append(getSimpleName());
        buffer.append("]");
        
        return buffer.toString();
    }
    
    //=================
    // Factory Methods
    //=================
    
    /**
     * 
     */
    public static TypeDescriptor create(String qualifiedName) throws TypeException {
        if (qualifiedName == null) {
            throw new TypeException("qualified name may not be null");
        }
        
        return create(StringToolkit.split(qualifiedName,'.'));
    }
    
    /**
     * 
     */
    public static TypeDescriptor create(String[] components) throws TypeException {
        if (components == null) {
            throw new TypeException("components may not be null");
        }
        
        if (components.length == 0) {
            throw new TypeException("components may not be empty");
        }
        
        String[] clone = new String[components.length];
        StringBuilder builder = new StringBuilder();
        
        for (int i=0;i<components.length;i++) {
            String curComponent = components[i];
            
            if (!StringToolkit.isJavaIdentifier(curComponent)) {
                throw new TypeException("component is not a valid Java type: \""+curComponent+"\"");
            }
            
            clone[i] = curComponent;
            builder.append(curComponent);
            
            if (i < (components.length - 1)) {
                builder.append(".");
            }
        }
        
        return new TypeDescriptor(builder.toString(),clone);
    }
}
