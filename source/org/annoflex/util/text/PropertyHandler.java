/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import org.annoflex.util.integer.IntHandler;

/**
 * @author Stefan Czaska
 */
final class PropertyHandler implements IntHandler {
    
    //===========
    // Constants
    //===========
    
    static final int ASCII_DIGIT             = 0;
    static final int ASCII_LETTER            = 1;
    static final int ASCII_LETTER_OR_DIGIT   = 2;
    static final int ASCII_IDENTIFIER_START  = 3;
    static final int ASCII_IDENTIFIER_PART   = 4;
    static final int DIGIT                   = 5;
    static final int LETTER                  = 6;
    static final int LETTER_OR_DIGIT         = 7;
    static final int LOWERCASE               = 8;
    static final int UPPERCASE               = 9;
    static final int WHITESPACE              = 10;
    static final int JAVA_IDENTIFIER_START   = 11;
    static final int JAVA_IDENTIFIER_PART    = 12;
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int type;
    
    /**
     * 
     */
    private final boolean invert;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    PropertyHandler(int type, boolean invert) {
        this.type = type;
        this.invert = invert;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public boolean handleInteger(int codePoint) {
        switch(type) {
        case ASCII_DIGIT:            return StringToolkit.isASCIIDigit(codePoint) ^ invert;
        case ASCII_LETTER:           return StringToolkit.isASCIILetter(codePoint) ^ invert;
        case ASCII_LETTER_OR_DIGIT:  return StringToolkit.isASCIILetterOrDigit(codePoint) ^ invert;
        case ASCII_IDENTIFIER_START: return StringToolkit.isASCIIIdentifierStart(codePoint) ^ invert;
        case ASCII_IDENTIFIER_PART:  return StringToolkit.isASCIIIdentifierPart(codePoint) ^ invert;
        case DIGIT:                  return Character.isDigit(codePoint) ^ invert;
        case LETTER:                 return Character.isLetter(codePoint) ^ invert;
        case LETTER_OR_DIGIT:        return Character.isLetterOrDigit(codePoint) ^ invert;
        case LOWERCASE:              return Character.isLowerCase(codePoint) ^ invert;
        case UPPERCASE:              return Character.isUpperCase(codePoint) ^ invert;
        case WHITESPACE:             return Character.isWhitespace(codePoint) ^ invert;
        case JAVA_IDENTIFIER_START:  return Character.isJavaIdentifierStart(codePoint) ^ invert;
        case JAVA_IDENTIFIER_PART:   return Character.isJavaIdentifierPart(codePoint) ^ invert;
        }
        
        return false;
    }
    
    /**
     * 
     */
    private String getTypeName() {
        switch(type) {
        case ASCII_DIGIT:            return "ASCIIDigit";
        case ASCII_LETTER:           return "ASCIILetter";
        case ASCII_LETTER_OR_DIGIT:  return "ASCIILetterOrDigit";
        case ASCII_IDENTIFIER_START: return "ASCIIIdentifierStart";
        case ASCII_IDENTIFIER_PART:  return "ASCIIIdentifierPart";
        case DIGIT:                  return "Digit";
        case LETTER:                 return "Letter";
        case LETTER_OR_DIGIT:        return "LetterOrDigit";
        case LOWERCASE:              return "Lowercase";
        case UPPERCASE:              return "Upercase";
        case WHITESPACE:             return "Whitespace";
        case JAVA_IDENTIFIER_START:  return "JavaIdentifierStart";
        case JAVA_IDENTIFIER_PART:   return "JavaIdentifierPart";
        }
        
        return null;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(getClass().getSimpleName());
        builder.append("[type=");
        builder.append(getTypeName());
        builder.append(",invert=");
        builder.append(invert);
        builder.append("]");
        
        return builder.toString();
    }
}
