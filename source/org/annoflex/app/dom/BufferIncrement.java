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
public enum BufferIncrement {
    
    GOLDEN_RATIO("goldenRatio"),
    DOUBLE("double");
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final HashMap<String,BufferIncrement> NAME_MAP = new HashMap<>();
    
    /**
     * 
     */
    static {
        BufferIncrement[] values = BufferIncrement.values();
        
        for (int i=0;i<values.length;i++) {
            BufferIncrement value = values[i];
            
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
    private BufferIncrement(String name) {
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
    public static BufferIncrement forName(String name) {
        return NAME_MAP.get(name);
    }
}
