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
public enum NoMatchAction {
    
    ERROR("error"),
    CONTINUE("continue"),
    RETURN("return");
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final HashMap<String,NoMatchAction> NAME_MAP = new HashMap<>();
    
    /**
     * 
     */
    static {
        NoMatchAction[] values = NoMatchAction.values();
        
        for (int i=0;i<values.length;i++) {
            NoMatchAction value = values[i];
            
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
    private NoMatchAction(String name) {
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
    public static NoMatchAction forName(String name) {
        return NAME_MAP.get(name);
    }
}
