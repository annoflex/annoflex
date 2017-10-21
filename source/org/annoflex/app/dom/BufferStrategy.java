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
public enum BufferStrategy {
    
    CURRENT_MATCH("currentMatch"),
    ALL_CHARACTERS("allCharacters");
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final HashMap<String,BufferStrategy> NAME_MAP = new HashMap<>();
    
    /**
     * 
     */
    static {
        BufferStrategy[] values = BufferStrategy.values();
        
        for (int i=0;i<values.length;i++) {
            BufferStrategy value = values[i];
            
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
    private BufferStrategy(String name) {
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
    public static BufferStrategy forName(String name) {
        return NAME_MAP.get(name);
    }
}
