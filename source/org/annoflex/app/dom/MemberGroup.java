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
public enum MemberGroup {
    
    ALL("all"),
    TABLE_CONSTANTS("tableConstants"),
    LEXICAL_STATE_CONSTANTS("lexicalStateConstants"),
    HELPER_CONSTANTS("helperConstants"),
    
    // input mode dependent
    READER_FIELDS("readerFields"),
    BUFFER_FIELDS("bufferFields"),
    STRING_FIELDS("stringFields"),
    REGION_FIELDS("regionFields"),
    
    // input mode independent
    DOT_FIELDS("dotFields"),
    LEXICAL_STATE_FIELDS("lexicalStateFields"),
    MATCH_FIELDS("matchFields"),
    HELPER_FIELDS("helperFields"),
    TABLE_METHODS("tableMethods"),
    
    // input mode dependent
    READER_METHODS("readerMethods"),
    BUFFER_METHODS("bufferMethods"),
    STRING_METHODS("stringMethods"),
    REGION_METHODS("regionMethods"),
    
    // input mode independent
    DOT_METHODS("dotMethods"),
    LEXICAL_STATE_METHODS("lexicalStateMethods"),
    MATCH_METHODS("matchMethods"),
    SCAN_METHODS("scanMethods"),
    HELPER_METHODS("helperMethods");
    
    /**
     * 
     */
    private static final HashMap<String,MemberGroup> NAME_MAP = createNameMap();
    
    /**
     * 
     */
    private final String name;
    
    /**
     * 
     */
    private MemberGroup(String name) {
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
    public static MemberGroup forName(String name) {
        return NAME_MAP.get(name);
    }
    
    /**
     * 
     */
    private static HashMap<String,MemberGroup> createNameMap() {
        HashMap<String,MemberGroup> map = new HashMap<>();
        MemberGroup[] values = MemberGroup.values();
        
        for (int i=0;i<values.length;i++) {
            MemberGroup curValue = values[i];
            map.put(curValue.name,curValue);
        }
        
        return map;
    }
}
