/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Stefan Czaska
 */
public class MemberMap<V> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final EnumMap<Member,V> map = new EnumMap<>(Member.class);
    
    /**
     * 
     */
    private final V defaultValue;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public MemberMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void put(Member member, V value) {
        if ((member != null) && (value != null)) {
            map.put(member,value);
        }
    }
    
    /**
     * 
     */
    public V get(Member member) {
        return getValue(map.get(member));
    }
    
    /**
     * 
     */
    private V getValue(V value) {
        return value != null ? value : defaultValue;
    }
    
    /**
     * 
     */
    public void putAll(V value) {
        putTableConstants(value);
        putLexicalStateConstants(value);
        putHelperConstants(value);
        putReaderFields(value);
        putBufferFields(value);
        putStringFields(value);
        putRegionFields(value);
        putDotFields(value);
        putLexicalStateFields(value);
        putMatchFields(value);
        putHelperFields(value);
        putTableMethods(value);
        putReaderMethods(value);
        putBufferMethods(value);
        putStringMethods(value);
        putRegionMethods(value);
        putDotMethods(value);
        putLexicalStateMethods(value);
        putMatchMethods(value);
        putScanMethods(value);
        putHelperMethods(value);
    }
    
    /**
     * 
     */
    public void putGroup(MemberGroup group, V value) {
        switch(group) {
        case ALL:                     putAll(value); break;
        case TABLE_CONSTANTS:         putTableConstants(value); break;
        case LEXICAL_STATE_CONSTANTS: putLexicalStateConstants(value); break;
        case HELPER_CONSTANTS:        putHelperConstants(value); break;
        case READER_FIELDS:           putReaderFields(value); break;
        case BUFFER_FIELDS:           putBufferFields(value); break;
        case STRING_FIELDS:           putStringFields(value); break;
        case REGION_FIELDS:           putRegionFields(value); break;
        case DOT_FIELDS:              putDotFields(value); break;
        case LEXICAL_STATE_FIELDS:    putLexicalStateFields(value); break;
        case MATCH_FIELDS:            putMatchFields(value); break;
        case HELPER_FIELDS:           putHelperFields(value); break;
        case TABLE_METHODS:           putTableMethods(value); break;
        case READER_METHODS:          putReaderMethods(value); break;
        case BUFFER_METHODS:          putBufferMethods(value); break;
        case STRING_METHODS:          putStringMethods(value); break;
        case REGION_METHODS:          putRegionMethods(value); break;
        case DOT_METHODS:             putDotMethods(value); break;
        case LEXICAL_STATE_METHODS:   putLexicalStateMethods(value); break;
        case MATCH_METHODS:           putMatchMethods(value); break;
        case SCAN_METHODS:            putScanMethods(value); break;
        case HELPER_METHODS:          putHelperMethods(value); break;
        }
    }
    
    /**
     * 
     */
    public void putTableConstants(V value) {
        put(Member.CHARACTER_MAP,value);
        put(Member.TRANSITION_TABLE,value);
        put(Member.ACTION_MAP,value);
    }
    
    /**
     * 
     */
    public void putLexicalStateConstants(V value) {
        put(Member.LEXICAL_STATE_ENUM,value);
    }
    
    /**
     * 
     */
    public void putHelperConstants(V value) {
        put(Member.EMPTY_CHAR_ARRAY,value);
    }
    
    /**
     * 
     */
    public void putReaderFields(V value) {
        put(Member.READER,value);
        put(Member.READER_START_CAPACITY,value);
    }
    
    /**
     * 
     */
    public void putBufferFields(V value) {
        put(Member.BUFFER,value);
        put(Member.BUFFER_START,value);
        put(Member.BUFFER_END,value);
    }
    
    /**
     * 
     */
    public void putStringFields(V value) {
        put(Member.STRING,value);
    }
    
    /**
     * 
     */
    public void putRegionFields(V value) {
        put(Member.REGION_START,value);
        put(Member.REGION_END,value);
    }
    
    /**
     * 
     */
    public void putDotFields(V value) {
        put(Member.DOT,value);
    }
    
    /**
     * 
     */
    public void putLexicalStateFields(V value) {
        put(Member.LEXICAL_STATE,value);
    }
    
    /**
     * 
     */
    public void putMatchFields(V value) {
        put(Member.MATCH_START,value);
        put(Member.MATCH_END,value);
        put(Member.MATCH_LOOKAHEAD,value);
    }
    
    /**
     * 
     */
    public void putHelperFields(V value) {
        put(Member.START_STATE,value);
        put(Member.POSITION_LIST,value);
    }
    
    /**
     * 
     */
    public void putTableMethods(V value) {
        put(Member.CREATE_CHARACTER_MAP,value);
        put(Member.CREATE_TRANSITION_TABLE,value);
        put(Member.CREATE_ACTION_MAP,value);
    }
    
    /**
     * 
     */
    public void putReaderMethods(V value) {
        put(Member.SET_READER,value);
        put(Member.GET_READER,value);
        put(Member.GET_READER_START_CAPACITY,value);
    }
    
    /**
     * 
     */
    public void putBufferMethods(V value) {
        put(Member.GET_BUFFER,value);
        put(Member.GET_BUFFER_START,value);
        put(Member.GET_BUFFER_END,value);
    }
    
    /**
     * 
     */
    public void putStringMethods(V value) {
        put(Member.SET_STRING,value);
        put(Member.GET_STRING,value);
    }
    
    /**
     * 
     */
    public void putRegionMethods(V value) {
        put(Member.SET_REGION,value);
        put(Member.GET_REGION_START,value);
        put(Member.GET_REGION_END,value);
    }
    
    /**
     * 
     */
    public void putDotMethods(V value) {
        put(Member.SET_DOT,value);
        put(Member.GET_DOT,value);
    }
    
    /**
     * 
     */
    public void putLexicalStateMethods(V value) {
        put(Member.SET_LEXICAL_STATE,value);
        put(Member.GET_LEXICAL_STATE,value);
    }
    
    /**
     * 
     */
    public void putMatchMethods(V value) {
        put(Member.GET_MATCH_START,value);
        put(Member.GET_MATCH_END,value);
        put(Member.GET_MATCH_LOOKAHEAD,value);
        put(Member.GET_MATCH_LENGTH,value);
        put(Member.GET_MATCH_TOTAL_LENGTH,value);
        put(Member.GET_MATCH_LOOKAHEAD_LENGTH,value);
        put(Member.GET_MATCH_TEXT,value);
        put(Member.GET_MATCH_TEXT_RANGE,value);
        put(Member.GET_MATCH_TOTAL_TEXT,value);
        put(Member.GET_MATCH_LOOKAHEAD_TEXT,value);
        put(Member.GET_MATCH_CHAR,value);
    }
    
    /**
     * 
     */
    public void putScanMethods(V value) {
        put(Member.GET_NEXT_TOKEN,value);
    }
    
    /**
     * 
     */
    public void putHelperMethods(V value) {
        put(Member.HAS_NEXT_CHAR,value);
        put(Member.COMPUTE_MATCH_END,value);
    }
    
    /**
     * 
     */
    public List<Member> getAllKeys(V value) {
        Iterator<Entry<Member,V>> iterator = map.entrySet().iterator();
        List<Member> list = null;
        
        while (iterator.hasNext()) {
            Entry<Member,V> curEntry = iterator.next();
            
            if (getValue(curEntry.getValue()).equals(value)) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                
                list.add(curEntry.getKey());
            }
        }
        
        return list;
    }
}
