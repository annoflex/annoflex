/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.LinkedHashMap;

import org.annoflex.util.SystemToolkit;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class NameMap {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public int size() {
        return map.size();
    }
    
    /**
     * 
     */
    public void put(String name, int index) {
        if (name == null) {
            throw new IllegalArgumentException("name may not be null");
        }
        
        if (!StringToolkit.isASCIIIdentifier(name)) {
            throw new IllegalArgumentException("invalid name \""+name+"\"");
        }
        
        map.put(name,index);
    }
    
    /**
     * 
     */
    public int get(String name) {
        Integer index = map.get(name);
        
        if (index == null) {
            throw new IllegalArgumentException("unknown name \""+name+"\"");
        }
        
        return index.intValue();
    }
    
    /**
     * 
     */
    public String[] getNames() {
        return map.keySet().toArray(SystemToolkit.EMPTY_STRING_ARRAY);
    }
}
