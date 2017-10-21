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
public enum Visibility {
    
    PUBLIC("public"),
    PROTECTED("protected"),
    PRIVATE("private"),
    PACKAGE_PRIVATE("");
    
    /**
     * 
     */
    private static final HashMap<String,Visibility> NAME_MAP = createNameMap();
    
    /**
     * 
     */
    private final String name;
    
    /**
     * 
     */
    private Visibility(String name) {
        this.name = name;
    }
    
    /**
     * 
     */
    public final String getName() {
        return name;
    }
    
    /**
     * 
     */
    public static Visibility forName(String name) {
        return NAME_MAP.get(name);
    }
    
    /**
     * 
     */
    private static HashMap<String,Visibility> createNameMap() {
        HashMap<String,Visibility> map = new HashMap<>();
        Visibility[] values = Visibility.values();
        
        for (int i=0;i<values.length;i++) {
            Visibility curValue = values[i];
            map.put(curValue.name,curValue);
        }
        
        return map;
    }
}
