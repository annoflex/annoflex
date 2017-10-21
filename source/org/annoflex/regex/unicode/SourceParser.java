/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.annoflex.util.LongBreak;
import org.annoflex.util.integer.MutableIntRangeSet;

/**
 * @author Stefan Czaska
 */
final class SourceParser {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final SourceScanner scanner;
    
    /**
     * 
     */
    private SourceType type;
    
    /**
     * 
     */
    private final Map<String,Map<String,Object>> mapMap = new HashMap<>();
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public SourceParser(Reader reader) {
        scanner = new SourceScanner(reader);
    }
    
    //================
    // Public Methods
    //================
    
    /**
     * 
     */
    public SourceType parseType() throws IOException {
        try {
            consume(SourceToken.AT);
            consumeIdentifier("type");
            consume(SourceToken.EQUAL);
            
            String optionValue = parseIdentifier(false);
            
            if (optionValue.equalsIgnoreCase("char")) {
                type = SourceType.CHAR;
            }
            
            else if (optionValue.equalsIgnoreCase("charSet")) {
                type = SourceType.CHAR_SET;
            }
            
            else if (optionValue.equalsIgnoreCase("string")) {
                type = SourceType.STRING;
            }
            
            else {
                throw createInternalError("syntax error");
            }
            
            return type;
        }
        
        catch(ParserLongBreak longBreak) {
            throw (IOException)longBreak.getCause();
        }
    }
    
    /**
     * 
     */
    public Map<String,Map<String,Object>> parseMap() throws IOException {
        try {
            String propertyName = parseIdentifier(false);
            
            while (propertyName != null) {
                SourceToken delimiter = parseColonOrLCB();
                
                if (delimiter == SourceToken.COLON) {
                    parseSingleValue(propertyName);
                }
                
                else {
                    parseMultiValue(propertyName);
                }
                
                propertyName = parseIdentifier(true);
            }
            
            return mapMap;
        }
        
        catch(ParserLongBreak longBreak) {
            throw (IOException)longBreak.getCause();
        }
    }
    
    //========================
    // Internal Parse Methods
    //========================
    
    /**
     * 
     */
    private void parseSingleValue(String propertyName) {
        String propertyValue = parseIdentifier(false);
        consume(SourceToken.LRB);
        parseValueContent(propertyName,propertyValue);
    }
    
    /**
     * 
     */
    private void parseMultiValue(String propertyName) {
        String propertyValue = parseIdentifier(false);
        
        do {
            consume(SourceToken.LRB);
            parseValueContent(propertyName,propertyValue);
            
            SourceToken delimiter = parseIdentifierOrRCB();
            propertyValue = delimiter == SourceToken.IDENTIFIER ?
                    scanner.getMatchText() : null;
        } while (propertyValue != null);
    }
    
    /**
     * 
     */
    private void parseValueContent(String propertyName, String propertyValue) {
        if (type == SourceType.CHAR) {
            int character = parseNumber();
            consume(SourceToken.RRB);
            
            if ((character < 0) || (character > Character.MAX_VALUE)) {
                throw createInternalError("invalid character value: "+character);
            }
            
            putProperty(propertyName,propertyValue,Character
                    .valueOf((char)character));
        }
        
        else if (type == SourceType.CHAR_SET) {
            MutableIntRangeSet rangeSet = new MutableIntRangeSet();
            
            do {
                int value1 = parseNumber();
                SourceToken delimiter = parseValueListDelimiter();
                
                if (delimiter == SourceToken.SEMICOLON) {
                    rangeSet.add(value1);
                }
                
                else if (delimiter == SourceToken.COLON) {
                    rangeSet.add(value1,parseNumber());
                    SourceToken delimiter2 = parseValueListDelimiter();
                    
                    if (delimiter2 == SourceToken.COLON) {
                        throw createInternalError("syntax error");
                    }
                    
                    else if (delimiter2 == SourceToken.RRB) {
                        break;
                    }
                }
                
                else {
                    rangeSet.add(value1);
                    break;
                }
            } while(true);
            
            putProperty(propertyName,propertyValue,rangeSet.toConstSet());
        }
        
        else if (type == SourceType.STRING) {
            String identifier = parseIdentifier(false);
            consume(SourceToken.RRB);
            
            putProperty(propertyName,propertyValue,identifier);
        }
        
        else {
            throw createInternalError("syntax error");
        }
    }
    
    /**
     * 
     */
    private void putProperty(String propertyName, String propertyValue,
            Object value) {
        
        Map<String,Object> map = mapMap.get(propertyName);
        
        if (map == null) {
            map = new HashMap<>();
            mapMap.put(propertyName,map);
        }
        
        if (map.containsKey(propertyValue)) {
            throw createInternalError("multiple property definition for "
                    + "property: "+propertyName+":"+propertyValue);
        }
        
        map.put(propertyValue,value);
    }
    
    //====================
    // Base Parse Methods
    //====================
    
    /**
     * 
     */
    private void consume(SourceToken expectedToken) {
        SourceToken token = getNextTokenSkipWS();
        
        if ((token == null) || (token != expectedToken)) {
            throw createInternalError("syntax error");
        }
    }
    
    /**
     * 
     */
    private void consumeIdentifier(String identifier) {
        if (!parseIdentifier(false).equalsIgnoreCase(identifier)) {
            throw createInternalError("syntax error");
        }
    }
    
    /**
     * 
     */
    private String parseIdentifier(boolean optional) {
        SourceToken token = getNextTokenSkipWS();
        
        if (token == null) {
            if (optional) {
                return null;
            }
            
            throw createInternalError("syntax error");
        }
        
        if (token != SourceToken.IDENTIFIER) {
            throw createInternalError("syntax error");
        }
        
        return scanner.getMatchText();
    }
    
    /**
     * 
     */
    private int parseNumber() {
        SourceToken token = getNextTokenSkipWS();
        
        if ((token == null) || (token != SourceToken.IDENTIFIER)) {
            throw createInternalError("syntax error");
        }
        
        return scanner.getMatchHexValue();
    }
    
    /**
     * 
     */
    private SourceToken parseColonOrLCB() {
        SourceToken token = getNextTokenSkipWS();
        
        if ((token == null) ||
            (token != SourceToken.COLON) &&
            (token != SourceToken.LCB)) {
            
            throw createInternalError("syntax error");
        }
        
        return token;
    }
    
    /**
     * 
     */
    private SourceToken parseValueListDelimiter() {
        SourceToken token = getNextTokenSkipWS();
        
        if ((token == null) ||
            (token != SourceToken.SEMICOLON) &&
            (token != SourceToken.COLON) &&
            (token != SourceToken.RRB)) {
            
            throw createInternalError("syntax error");
        }
        
        return token;
    }
    
    /**
     * 
     */
    private SourceToken parseIdentifierOrRCB() {
        SourceToken token = getNextTokenSkipWS();
        
        if ((token == null) ||
            (token != SourceToken.IDENTIFIER) &&
            (token != SourceToken.RCB)) {
            
            throw createInternalError("syntax error");
        }
        
        return token;
    }
    
    /**
     * 
     */
    private SourceToken getNextTokenSkipWS() {
        try {
            SourceToken token = scanner.getNextToken();
            
            while ((token != null) && (token == SourceToken.WHITESPACE)) {
                token = scanner.getNextToken();
            }
            
            return token;
        }
        
        catch(IOException e) {
            throw new ParserLongBreak(e);
        }
    }
    
    //===================
    // Exception Methods
    //===================
    
    /**
     * 
     */
    private PropertyException createInternalError(String message) {
        return new PropertyException(PropertyException.INTERNAL_ERROR,message);
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class ParserLongBreak extends LongBreak {
        
        /**
         * 
         */
        public ParserLongBreak(IOException e) {
            super(e);
        }
    }
}
