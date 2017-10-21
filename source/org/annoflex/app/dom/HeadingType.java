/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

import java.util.HashMap;

/**
 * @author Stefan Czaska
 */
public enum HeadingType {
    
    DISABLED("disabled"),
    ENABLED("enabled"),
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large");
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final HashMap<String,HeadingType> NAME_MAP = new HashMap<>();
    
    /**
     * 
     */
    static {
        HeadingType[] values = HeadingType.values();
        
        for (int i=0;i<values.length;i++) {
            HeadingType value = values[i];
            
            NAME_MAP.put(value.getName(),value);
        }
    }
    
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
    private HeadingType(String name) {
        this.name = name;
    }
    
    /**
     * 
     */
    public final String getName() {
        return name;
    }
    
    //================
    // Static Methods
    //================
    
    /**
     * 
     */
    public static HeadingType forName(String name) {
        return NAME_MAP.get(name);
    }
}
