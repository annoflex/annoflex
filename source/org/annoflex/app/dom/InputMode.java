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
public enum InputMode {
    
    STRING("string"),
    READER("reader");
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final HashMap<String,InputMode> NAME_MAP = new HashMap<>();
    
    /**
     * 
     */
    static {
        InputMode[] values = InputMode.values();
        
        for (int i=0;i<values.length;i++) {
            InputMode value = values[i];
            
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
    private InputMode(String name) {
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
    public static InputMode forName(String name) {
        return NAME_MAP.get(name);
    }
}
