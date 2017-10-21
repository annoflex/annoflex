/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

/**
 * @author Stefan Czaska
 */
public class FunctionalityMap extends BooleanMap {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public FunctionalityMap() {
        super(false);
        
        putReaderMethods(true);
        putStringMethods(true);
        putRegionMethods(true);
        putDotMethods(true);
        put(Member.GET_MATCH_START,true);
        put(Member.GET_MATCH_END,true);
        put(Member.GET_MATCH_LENGTH,true);
        put(Member.GET_MATCH_TEXT,true);
        put(Member.GET_MATCH_CHAR,true);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public boolean hasReaderMethod() {
        return has(Member.SET_READER) ||
               has(Member.GET_READER) ||
               has(Member.GET_READER_START_CAPACITY);
    }
    
    /**
     * 
     */
    public boolean hasBufferMethod() {
        return has(Member.GET_BUFFER) ||
               has(Member.GET_BUFFER_START) ||
               has(Member.GET_BUFFER_END);
    }
    
    /**
     * 
     */
    public boolean hasStringMethod() {
        return has(Member.SET_STRING) ||
               has(Member.GET_STRING);
    }
    
    /**
     * 
     */
    public boolean hasRegionMethod() {
        return has(Member.SET_REGION) ||
               has(Member.GET_REGION_START) ||
               has(Member.GET_REGION_END);
    }
    
    /**
     * 
     */
    public boolean hasDotMethod() {
        return has(Member.SET_DOT) ||
               has(Member.GET_DOT);
    }
    
    /**
     * 
     */
    public boolean hasMatchMethod() {
        return has(Member.GET_MATCH_START) ||
               has(Member.GET_MATCH_END) ||
               has(Member.GET_MATCH_LOOKAHEAD) ||
               has(Member.GET_MATCH_LENGTH) ||
               has(Member.GET_MATCH_TOTAL_LENGTH) ||
               has(Member.GET_MATCH_LOOKAHEAD_LENGTH) ||
               has(Member.GET_MATCH_TEXT) ||
               has(Member.GET_MATCH_TEXT_RANGE) ||
               has(Member.GET_MATCH_TOTAL_TEXT) ||
               has(Member.GET_MATCH_LOOKAHEAD_TEXT) ||
               has(Member.GET_MATCH_CHAR);
    }
    
    //=====================
    // Read Access Methods
    //=====================
    
    /**
     * 
     */
    public boolean hasRegionStartFieldReadAccess() {
        return has(Member.REGION_START) ||
               has(Member.GET_REGION_START) ||
               has(Member.SET_DOT) ||
               has(Member.GET_MATCH_TEXT_RANGE) ||
               has(Member.GET_MATCH_CHAR);
    }
    
    /**
     * 
     */
    public boolean hasLexicalStateFieldReadAccess() {
        return has(Member.LEXICAL_STATE) ||
               has(Member.GET_LEXICAL_STATE);
    }
    
    /**
     * 
     */
    public boolean hasMatchStartFieldReadAccess(boolean isReaderCurMatch) {
        return has(Member.MATCH_START) ||
               has(Member.GET_MATCH_START) ||
               has(Member.GET_MATCH_LENGTH) ||
               has(Member.GET_MATCH_TOTAL_LENGTH) ||
               has(Member.GET_MATCH_TEXT) ||
               has(Member.GET_MATCH_TEXT_RANGE) ||
               has(Member.GET_MATCH_TOTAL_TEXT) ||
               has(Member.GET_MATCH_CHAR) ||
               (isReaderCurMatch && has(Member.SET_DOT));
    }
    
    /**
     * 
     */
    public boolean hasMatchEndFieldReadAccess() {
        return has(Member.MATCH_END) ||
               has(Member.GET_MATCH_END) ||
               has(Member.GET_MATCH_LENGTH) ||
               has(Member.GET_MATCH_LOOKAHEAD_LENGTH) ||
               has(Member.GET_MATCH_TEXT) ||
               has(Member.GET_MATCH_TEXT_RANGE) ||
               has(Member.GET_MATCH_LOOKAHEAD_TEXT);
    }
    
    /**
     * 
     */
    public boolean hasMatchLookaheadFieldReadAccess(boolean isReaderCurMatch) {
        return has(Member.MATCH_LOOKAHEAD) ||
               has(Member.GET_MATCH_LOOKAHEAD) ||
               has(Member.GET_MATCH_TOTAL_LENGTH) ||
               has(Member.GET_MATCH_LOOKAHEAD_LENGTH) ||
               has(Member.GET_MATCH_TOTAL_TEXT) ||
               has(Member.GET_MATCH_LOOKAHEAD_TEXT);
    }
}
