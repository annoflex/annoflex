/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.annoflex.util.integer.ConstIntRangeSet;

/**
 * @author Stefan Czaska
 */
public class PropertyResolver {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final Pattern PROPERTY_DELIMITER = Pattern.compile("!=|:|=");
    
    /**
     * 
     */
    private static final String PROPERTY_VALUE_ALIASES_FILE = "valueAlias.txt";
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final HashMap<String,Source> sourceCache = new HashMap<>();
    
    //==================
    // Char Set Methods
    //==================
    
    /**
     * 
     */
    public ConstIntRangeSet resolveToCharSet(PropertySelector selector) {
        if (selector == null) {
            throw new IllegalArgumentException("selector may not be null");
        }
        
        String selectorText = selector.getText();
        boolean invert = selector.getInvert();
        Matcher matcher = PROPERTY_DELIMITER.matcher(selectorText);
        
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            
            // handle property name
            String propertyName = PropertyToolkit.normalizeSymbolicValue(
                    selectorText.substring(0,start));
            
            if (propertyName.isEmpty()) {
                throw createSyntaxError("property name may not be empty");
            }
            
            Property property = Property.forName(propertyName);
            
            if (property == null) {
                throw createSyntaxError("unknown property: "+propertyName);
            }
            
            // handle delimiter
            String delimiterText = selectorText.substring(start,end);
            
            if (delimiterText.equals("!=")) {
                invert = !invert;
            }
            
            // handle property value
            String propertyValue = property.normalizeValue(
                    selectorText.substring(end));
            
            if (propertyValue.isEmpty()) {
                throw createSyntaxError("property value may not be empty");
            }
            
            return resolveToCharSetInternal(property,propertyValue,invert);
        }
        
        // normalize text as a symbolic value
        String normalizedText = PropertyToolkit.normalizeSymbolicValue(selectorText);
        Property property = Property.forName(normalizedText);
        
        if ((property != null) && property.isBinary()) {
            return resolveToCharSetInternal(property,PropertyToolkit.BINARY_YES,invert);
        }
        
        String scriptValue = lookUpValue(Property.SCRIPT,normalizedText);
        
        if (scriptValue != null) {
            return resolveToCharSetInternal(Property.SCRIPT,scriptValue,invert);
        }
        
        String categoryValue = lookUpValue(Property.GENERAL_CATEGORY,normalizedText);
        
        if (categoryValue != null) {
            return resolveToCharSetInternal(Property.GENERAL_CATEGORY,categoryValue,invert);
        }
        
        throw createSyntaxError("unknown property: \""+selectorText+"\"");
    }
    
    /**
     * 
     */
    private String lookUpValue(Property property, String normalizedValue) {
        String longValue = resolveValueAlias(property,normalizedValue);
        
        if (getSource(property).hasEntry(property.getLongName(),longValue)) {
            return longValue;
        }
        
        return null;
    }
    
    /**
     * 
     */
    private ConstIntRangeSet resolveToCharSetInternal(Property property,
            String normalizedValue, boolean invert) {
        
        String longValue = resolveValueAlias(property,normalizedValue);
        
        if (property.isBinary() &&
            PropertyToolkit.isBinaryFalse(longValue)) {
            
            longValue = PropertyToolkit.BINARY_YES;
            invert = !invert;
        }
        
        Source source = getSource(property);
        
        switch(source.getType()) {
        case CHAR:
            Character character = resolveToCharInternal(property,longValue);
            
            return character != null ? createCharSet(character,invert) : null;
        
        case CHAR_SET:
            String propertyName = property.getLongName();
            ConstIntRangeSet rangeSet = source.getCharSet(propertyName,longValue);
            
            if (rangeSet != null) {
                return invert ? rangeSet.invert(Character.MIN_VALUE,Character.MAX_VALUE) :
                    rangeSet;
            }
            
            return null;
        
        default:
            throw createInternalError("char set can not be requested for property: "+property);
        }
    }
    
    //==============
    // Char Methods
    //==============
    
    /**
     * 
     */
    public Character resolveToChar(Property property, String value) {
        if (property == null) {
            throw new IllegalArgumentException("property may not be null");
        }
        
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }
        
        value = resolveValueAlias(property,property.normalizeValue(value));
        
        return resolveToCharInternal(property,value);
    }
    
    /**
     * 
     */
    private Character resolveToCharInternal(Property property,
            String longValue) {
        
        String propertyName = property.getLongName();
        Character character = getSource(property).getChar(propertyName,longValue);
        
        if (character != null) {
            return character;
        }
        
        if (property == Property.NAME) {
            return getSource(Property.NAME_ALIAS).getChar(
                    Property.NAME_ALIAS.getLongName(),longValue);
        }
        
        return null;
    }
    
    //===============
    // Alias Methods
    //===============
    
    /**
     * 
     */
    private String resolveValueAlias(Property property, String normalizedValue) {
        if (property.isBinary()) {
            return PropertyToolkit.resolveBinaryAlias(normalizedValue);
        }
        
        if (property == Property.SCRIPT_EXTENSIONS) {
            property = Property.SCRIPT;
        }
        
        String longName = getSource(PROPERTY_VALUE_ALIASES_FILE).getString(
                property.getLongName(),normalizedValue);
        
        return longName != null ? longName : normalizedValue;
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * 
     */
    private ConstIntRangeSet createCharSet(char character, boolean invert) {
        if (invert) {
            if (character == Character.MIN_VALUE) {
                return new ConstIntRangeSet(Character.MIN_VALUE+1,Character.MAX_VALUE);
            }
            
            if (character == Character.MAX_VALUE) {
                return new ConstIntRangeSet(Character.MIN_VALUE,Character.MAX_VALUE-1);
            }
            
            return new ConstIntRangeSet(new int[][]{
                    new int[] {Character.MIN_VALUE,character-1},
                    new int[] {character+1,Character.MAX_VALUE}});
        }
        
        return new ConstIntRangeSet(character);
    }
    
    //======================
    // Source Cache Methods
    //======================
    
    /**
     * 
     */
    private Source getSource(Property property) {
        String sourceFile = property.getSourceFile();
        
        if (sourceFile == null) {
            throw createSyntaxError("unsupported property: "+property.getLongName());
        }
        
        return getSource(sourceFile);
    }
    
    /**
     * 
     */
    private Source getSource(String sourceFile) {
        Source source = sourceCache.get(sourceFile);
        
        if (source == null) {
            source = Source.getSource(sourceFile);
            sourceCache.put(sourceFile,source);
        }
        
        return source;
    }
    
    //===================
    // Exception Methods
    //===================
    
    /**
     * 
     */
    private PropertyException createSyntaxError(String message) {
        return new PropertyException(PropertyException.SYNTAX_ERROR,message);
    }
    
    /**
     * 
     */
    private PropertyException createInternalError(String message) {
        return new PropertyException(PropertyException.INTERNAL_ERROR,message);
    }
}
