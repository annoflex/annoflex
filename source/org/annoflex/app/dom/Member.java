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
public enum Member {
    
    /* table constants */
    CHARACTER_MAP("characterMap"),
    TRANSITION_TABLE("transitionTable"),
    ACTION_MAP("actionMap"),
    
    /* lexical state constants */
    LEXICAL_STATE_ENUM("lexicalStateEnum"),
    
    /* helper constants */
    EMPTY_CHAR_ARRAY("emptyCharArray"),
    
    /* reader fields */
    READER("reader"),
    READER_START_CAPACITY("readerStartCapacity"),
    
    /* buffer fields */
    BUFFER("buffer"),
    BUFFER_START("bufferStart"),
    BUFFER_END("bufferEnd"),
    
    /* string fields */
    STRING("string"),
    
    /* region fields */
    REGION_START("regionStart"),
    REGION_END("regionEnd"),
    
    /* dot fields */
    DOT("dot"),
    
    /* lexical state fields */
    LEXICAL_STATE("lexicalState"),
    
    /* match fields */
    MATCH_START("matchStart"),
    MATCH_END("matchEnd"),
    MATCH_LOOKAHEAD("matchLookahead"),
    
    /* helper fields */
    START_STATE("startState"),
    POSITION_LIST("positionList"),
    
    /* table methods */
    CREATE_CHARACTER_MAP("createCharacterMap"),
    CREATE_TRANSITION_TABLE("createTransitionTable"),
    CREATE_ACTION_MAP("createActionMap"),
    
    /* reader methods */
    SET_READER("setReader"),
    GET_READER("getReader"),
    GET_READER_START_CAPACITY("getReaderStartCapacity"),
    
    /* buffer methods */
    GET_BUFFER("getBuffer"),
    GET_BUFFER_START("getBufferStart"),
    GET_BUFFER_END("getBufferEnd"),
    
    /* string methods */
    SET_STRING("setString"),
    GET_STRING("getString"),
    
    /* region methods */
    SET_REGION("setRegion"),
    GET_REGION_START("getRegionStart"),
    GET_REGION_END("getRegionEnd"),
    
    /* dot methods */
    SET_DOT("setDot"),
    GET_DOT("getDot"),
    
    /* lexical state methods */
    SET_LEXICAL_STATE("setLexicalState"),
    GET_LEXICAL_STATE("getLexicalState"),
    
    /* match methods */
    GET_MATCH_START("getMatchStart"),
    GET_MATCH_END("getMatchEnd"),
    GET_MATCH_LOOKAHEAD("getMatchLookahead"),
    GET_MATCH_LENGTH("getMatchLength"),
    GET_MATCH_TOTAL_LENGTH("getMatchTotalLength"),
    GET_MATCH_LOOKAHEAD_LENGTH("getMatchLookaheadLength"),
    GET_MATCH_TEXT("getMatchText"),
    GET_MATCH_TEXT_RANGE("getMatchTextRange"),
    GET_MATCH_TOTAL_TEXT("getMatchTotalText"),
    GET_MATCH_LOOKAHEAD_TEXT("getMatchLookaheadText"),
    GET_MATCH_CHAR("getMatchChar"),
    
    /* scan methods */
    GET_NEXT_TOKEN("getNextToken"),
    
    /* helper methods */
    HAS_NEXT_CHAR("hasNextChar"),
    COMPUTE_MATCH_END("computeMatchEnd");
    
    /**
     * 
     */
    private static final HashMap<String,Member> NAME_MAP = createNameMap();
    
    /**
     * 
     */
    private final String name;
    
    /**
     * 
     */
    private Member(String name) {
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
    public static Member forName(String name) {
        return NAME_MAP.get(name);
    }
    
    /**
     * 
     */
    private static HashMap<String,Member> createNameMap() {
        HashMap<String,Member> map = new HashMap<>();
        Member[] values = Member.values();
        
        for (int i=0;i<values.length;i++) {
            Member curValue = values[i];
            map.put(curValue.name,curValue);
        }
        
        return map;
    }
}
