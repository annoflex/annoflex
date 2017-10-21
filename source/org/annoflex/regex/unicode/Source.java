/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.annoflex.regex.unicode.files.UCDAnchor;
import org.annoflex.util.integer.ConstIntRangeSet;

/**
 * @author Stefan Czaska
 */
final class Source {
    
    //================
    // File Constants
    //================
    
    static final String BINARY      = "binary.txt";
    static final String NON_BINARY  = "nonBinary.txt";
    static final String CHAR_NAME   = "charName.txt";
    static final String VALUE_ALIAS = "valueAlias.txt";
    
    //=================
    // Cache Constants
    //=================
    
    /**
     * 
     */
    private static final HashMap<String,Source> SOURCE_CACHE = new HashMap<>();
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final SourceType type;
    
    /**
     * 
     */
    private final Map<String,Map<String,Object>> mapMap;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    private Source(String sourceFile) {
        URL url = UCDAnchor.class.getResource(sourceFile);
        
        if (url == null) {
            throw new PropertyException(PropertyException.INTERNAL_ERROR,
                    "source file could not be loaded: "+sourceFile);
        }
        
        try (InputStreamReader reader = new InputStreamReader(
                url.openStream(),"US-ASCII")) {
            
            SourceParser parser = new SourceParser(reader);
            
            type = parser.parseType();
            mapMap = parser.parseMap();
        }
        
        catch(IOException e) {
            throw new PropertyException(PropertyException.INTERNAL_ERROR,
                    "source could not be loaded: "+url);
        }
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public SourceType getType() {
        return type;
    }
    
    /**
     * 
     */
    public Character getChar(String propertyName, String propertyValue) {
        return (Character)getValue(propertyName,propertyValue,
                SourceType.CHAR);
    }
    
    /**
     * 
     */
    public ConstIntRangeSet getCharSet(String propertyName,
            String propertyValue) {
        
        return (ConstIntRangeSet)getValue(propertyName,propertyValue,
                SourceType.CHAR_SET);
    }
    
    /**
     * 
     */
    public String getString(String propertyName, String propertyValue) {
        return (String)getValue(propertyName,propertyValue,SourceType.STRING);
    }
    
    /**
     * 
     */
    public boolean hasEntry(String propertyName) {
        return mapMap.get(propertyName) != null;
    }
    
    /**
     * 
     */
    public boolean hasEntry(String propertyName, String propertyValue) {
        Map<String,?> map = mapMap.get(propertyName);
        
        return (map != null) && (map.get(propertyValue) != null);
    }
    
    /**
     * 
     */
    private Object getValue(String propertyName, String propertyValue,
            SourceType sourceType) {
        
        if (this.type != sourceType) {
            throw new PropertyException(PropertyException.INTERNAL_ERROR,
                    "invalid source type: "+sourceType);
        }
        
        Map<String,?> map = mapMap.get(propertyName);
        
        return map != null ? map.get(propertyValue) : null;
    }
    
    //=================
    // Factory Methods
    //=================
    
    /**
     * 
     */
    public static Source getSource(String sourceName) {
        if (sourceName == null) {
            throw new IllegalArgumentException("source name may not be null");
        }
        
        synchronized (SOURCE_CACHE) {
            Source source = SOURCE_CACHE.get(sourceName);
            
            if (source == null) {
                source = new Source(sourceName);
                SOURCE_CACHE.put(sourceName,source);
            }
            
            return source;
        }
    }
}
