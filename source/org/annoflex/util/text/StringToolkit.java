/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.annoflex.util.SystemToolkit;
import org.annoflex.util.integer.IntHandler;

/**
 * @author Stefan Czaska
 */
public final class StringToolkit {
    
    //=========================
    // ASCII Handler Constants
    //=========================
    
    public static final IntHandler ASCII_DIGIT = new PropertyHandler(
            PropertyHandler.ASCII_DIGIT,false);
    
    public static final IntHandler ASCII_DIGIT_INV = new PropertyHandler(
            PropertyHandler.ASCII_DIGIT,true);
    
    public static final IntHandler ASCII_LETTER = new PropertyHandler(
            PropertyHandler.ASCII_LETTER,false);
    
    public static final IntHandler ASCII_LETTER_INV = new PropertyHandler(
            PropertyHandler.ASCII_LETTER,true);
    
    public static final IntHandler ASCII_LETTER_OR_DIGIT = new PropertyHandler(
            PropertyHandler.ASCII_LETTER_OR_DIGIT,false);
    
    public static final IntHandler ASCII_LETTER_OR_DIGIT_INV = new PropertyHandler(
            PropertyHandler.ASCII_LETTER_OR_DIGIT,true);
    
    public static final IntHandler ASCII_IDENTIFIER_START = new PropertyHandler(
            PropertyHandler.ASCII_IDENTIFIER_START,false);
    
    public static final IntHandler ASCII_IDENTIFIER_START_INV = new PropertyHandler(
            PropertyHandler.ASCII_IDENTIFIER_START,true);
    
    public static final IntHandler ASCII_IDENTIFIER_PART = new PropertyHandler(
            PropertyHandler.ASCII_IDENTIFIER_PART,false);
    
    public static final IntHandler ASCII_IDENTIFIER_PART_INV = new PropertyHandler(
            PropertyHandler.ASCII_IDENTIFIER_PART,true);
    
    //===========================
    // Unicode Handler Constants
    //===========================
    
    public static final IntHandler DIGIT = new PropertyHandler(
            PropertyHandler.DIGIT,false);
    
    public static final IntHandler DIGIT_INV = new PropertyHandler(
            PropertyHandler.DIGIT,true);
    
    public static final IntHandler LETTER = new PropertyHandler(
            PropertyHandler.LETTER,false);
    
    public static final IntHandler LETTER_INV = new PropertyHandler(
            PropertyHandler.LETTER,true);
    
    public static final IntHandler LETTER_OR_DIGIT = new PropertyHandler(
            PropertyHandler.LETTER_OR_DIGIT,false);
    
    public static final IntHandler LETTER_OR_DIGIT_INV = new PropertyHandler(
            PropertyHandler.LETTER_OR_DIGIT,true);
    
    public static final IntHandler LOWERCASE = new PropertyHandler(
            PropertyHandler.LOWERCASE,false);
    
    public static final IntHandler LOWERCASE_INV = new PropertyHandler(
            PropertyHandler.LOWERCASE,true);
    
    public static final IntHandler UPPERCASE = new PropertyHandler(
            PropertyHandler.UPPERCASE,false);
    
    public static final IntHandler UPPERCASE_INV = new PropertyHandler(
            PropertyHandler.UPPERCASE,true);
    
    public static final IntHandler WHITESPACE = new PropertyHandler(
            PropertyHandler.WHITESPACE,false);
    
    public static final IntHandler WHITESPACE_INV = new PropertyHandler(
            PropertyHandler.WHITESPACE,true);
    
    //========================
    // Java Handler Constants
    //========================
    
    public static final IntHandler JAVA_IDENTIFIER_START = new PropertyHandler(
            PropertyHandler.JAVA_IDENTIFIER_START,false);
    
    public static final IntHandler JAVA_IDENTIFIER_START_INV = new PropertyHandler(
            PropertyHandler.JAVA_IDENTIFIER_START,true);
    
    public static final IntHandler JAVA_IDENTIFIER_PART = new PropertyHandler(
            PropertyHandler.JAVA_IDENTIFIER_PART,false);
    
    public static final IntHandler JAVA_IDENTIFIER_PART_INV = new PropertyHandler(
            PropertyHandler.JAVA_IDENTIFIER_PART,true);
    
    //==========================
    // Line Separator Constants
    //==========================
    
    /**
     * 
     */
    public static final Pattern LINE_TERMINATOR = Pattern.compile("\\r\\n|\\r|\\n");
    
    //========================
    // String Builder Methods
    //========================
    
    /**
     * 
     */
    public static StringBuilder append(StringBuilder builder, String string, int count) {
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        if (count < 0) {
            throw new IllegalArgumentException("count may not be negative");
        }
        
        if ((count > 0) && !string.isEmpty()) {
            if (builder == null) {
                builder = new StringBuilder();
            }
            
            do {
                builder.append(string);
            } while (--count > 0);
        }
        
        return builder;
    }
    
    /**
     * 
     */
    public static StringBuilder appendReplacement(StringBuilder builder,
            String string, Pattern pattern, String replacement) {
        
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        if (pattern == null) {
            throw new IllegalArgumentException("pattern may not be null");
        }
        
        if (replacement == null) {
            throw new IllegalArgumentException("replacement may not be null");
        }
        
        Matcher matcher = pattern.matcher(string);
        
        if (matcher.find()) {
            int lastStart = 0;
            
            do {
                int matchStart = matcher.start();
                int matchEnd = matcher.end();
                
                // TODO: Enable this check also for initial non-null builders.
                if ((builder == null) &&
                    (((matchEnd - matchStart) != replacement.length()) ||
                     !string.regionMatches(matchStart,replacement,0,replacement.length()))) {
                    
                    builder = new StringBuilder();
                }
                
                if (builder != null) {
                    builder.append(string,lastStart,matchStart);
                    builder.append(replacement);
                    
                    lastStart = matchEnd;
                }
            } while(matcher.find());
            
            if (builder != null) {
                builder.append(string,lastStart,string.length());
                
                return builder;
            }
        }
        
        if (builder != null) {
            builder.append(string);
        }
        
        return builder;
    }
    
    //=========================
    // String Creation Methods
    //=========================
    
    /**
     * 
     */
    public static String createString(char character, int count) {
        StringBuilder builder = new StringBuilder();
        
        for (int i=0;i<count;i++) {
            builder.append(character);
        }
        
        return builder.toString();
    }
    
    //=====================
    // String Skip Methods
    //=====================
    
    /**
     * Skips one or more characters in a string. The specified indices are
     * positions between characters. A skip "jumps" over the character to the
     * next position between characters.
     * 
     * @param string A string in which characters should be skipped.
     * @param startIndex The position before/after the first character to be
     * skipped. If the skip goes forwards the start index is the index of the
     * first character (inclusive). If the skip goes backwards the start index
     * is the index of the character after the first character (exclusive).
     * @param endIndex The position after/before the last character to be
     * skipped. If the skip goes forwards the end index is the index of the
     * character after the last character (exclusive). If the skip goes
     * backwards the end index is the index of the last character (inclusive).
     * @param skipChar The character which should be skipped if present at the
     * skip positions.
     * @param backwards True if the skip should go backwards, false if it should
     * go forwards.
     * @return The position after the skip. If the skip goes forwards it is the
     * index after the last skipped character. If the skip goes backwards it is
     * the index of the last skipped character.
     */
    public static int skip(String string, int startIndex, int endIndex,
            char skipChar, boolean backwards) {
        
        int length = checkIndex(string,startIndex);
        
        if (backwards) {
            if (endIndex < 0) {
                endIndex = 0;
            }
            
            while ((startIndex > endIndex) &&
                   (string.charAt(startIndex-1) == skipChar)) {
                
                startIndex--;
            }
        }
        
        else {
            if (endIndex > length) {
                endIndex = length;
            }
            
            while ((startIndex < endIndex) &&
                   (string.charAt(startIndex) == skipChar)) {
                
                startIndex++;
            }
        }
        
        return startIndex;
    }
    
    /**
     * Skips one or more characters in a string. The specified indices are
     * positions between characters. A skip "jumps" over the character to the
     * next position between characters.
     * 
     * @param string A string in which characters should be skipped.
     * @param startIndex The position before/after the first character to be
     * skipped. If the skip goes forwards the start index is the index of the
     * first character (inclusive). If the skip goes backwards the start index
     * is the index of the character after the first character (exclusive).
     * @param endIndex The position after/before the last character to be
     * skipped. If the skip goes forwards the end index is the index of the
     * character after the last character (exclusive). If the skip goes
     * backwards the end index is the index of the last character (inclusive).
     * @param skipHandler An {@link IntHandler} which determines whether a
     * character is a skippable character or not. If the handler returns true,
     * the character is a skippable character and if it returns false, the
     * character is not a skippable character.
     * @param backwards True if the skip should go backwards, false if it should
     * go forwards.
     * @return The position after the skip. If the skip goes forwards it is the
     * index after the last skipped character. If the skip goes backwards it is
     * the index of the last skipped character.
     */
    public static int skip(String string, int startIndex, int endIndex,
            IntHandler skipHandler, boolean backwards) {
        
        int length = checkIndex(string,startIndex);
        
        if (skipHandler == null) {
            throw new IllegalArgumentException("skip handler may not be null");
        }
        
        if (backwards) {
            if (endIndex < 0) {
                endIndex = 0;
            }
            
            while ((startIndex > endIndex) &&
                   skipHandler.handleInteger(string.charAt(startIndex-1))) {
                 
                startIndex--;
            }
        }
        
        else {
            if (endIndex > length) {
                endIndex = length;
            }
            
            while ((startIndex < endIndex) &&
                   skipHandler.handleInteger(string.charAt(startIndex))) {
                
                startIndex++;
            }
        }
        
        return startIndex;
    }
    
    /**
     * 
     */
    public static int skip(String string, int startIndex, String skipString,
            boolean ignoreCase) {
        
        int length = checkIndex(string,startIndex);
        
        if (skipString == null) {
            throw new IllegalArgumentException("skip string may not be null");
        }
        
        int skipLength = skipString.length();
        int endIndex = startIndex + skipLength;
        
        if (endIndex > length) {
            return startIndex;
        }
        
        if (ignoreCase) {
            for (int i=0;i<skipLength;i++) {
                if (Character.toLowerCase(string.charAt(startIndex+i)) !=
                        Character.toLowerCase(skipString.charAt(i))) {
                    
                    return startIndex;
                }
            }
        }
        
        else {
            for (int i=0;i<skipLength;i++) {
                if (string.charAt(startIndex+i) != skipString.charAt(i)) {
                    return startIndex;
                }
            }
        }
        
        return endIndex;
    }
    
    //======================
    // String Split Methods
    //======================
    
    /**
     * 
     */
    public static Span[] split(String string, IntHandler delimiterSet) {
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        if (delimiterSet == null) {
            throw new IllegalArgumentException("delimiter set may not be null");
        }
        
        ArrayList<Span> list = null;
        int start = 0;
        int end = string.length();
        
        OuterLoop:
        while (start < end) {
            while (delimiterSet.handleInteger(string.charAt(start))) {
                if (++start == end) {
                    break OuterLoop;
                }
            }
            
            int contentEnd = start;
            
            while (!delimiterSet.handleInteger(string.charAt(contentEnd))) {
                if (++contentEnd == end) {
                    break;
                }
            }
            
            if (contentEnd == start) {
                break;
            }
            
            if (list == null) {
                list = new ArrayList<>();
            }
            
            list.add(new Span(start,contentEnd));
            start = contentEnd;
        }
        
        return list != null ? list.toArray(Span.EMPTY_ARRAY) : Span.EMPTY_ARRAY;
    }
    
    /**
     * 
     */
    public static String[] split(String string, char character) {
        return split(string,0,string.length(),character);
    }
    
    /**
     * 
     */
    public static String[] split(String string, int startIndex, int endIndex,
            char character) {
        
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        int index = indexOf(string,startIndex,endIndex,character);
        
        if (index == -1) {
            return new String[]{string.substring(startIndex,endIndex)};
        }
        
        ArrayList<String> list = new ArrayList<>();
        
        do {
            list.add(string.substring(startIndex,index));
            
            startIndex = index + 1;
            index = indexOf(string,startIndex,endIndex,character);
        } while (index != -1);
        
        list.add(string.substring(startIndex,endIndex));
        
        return list.toArray(SystemToolkit.EMPTY_STRING_ARRAY);
    }
    
    //=========================
    // String Property Methods
    //=========================
    
    /**
     * 
     */
    public static boolean equals(String string, int startIndex,
            int endIndex, String subString) {
        
        return equals(string,startIndex,endIndex,subString,0,subString.length());
    }
    
    /**
     * 
     */
    public static boolean equals(String string1, int startIndex1,
            int endIndex1, String string2, int startIndex2,
            int endIndex2) {
        
        checkRange(string1,startIndex1,endIndex1);
        checkRange(string2,startIndex2,endIndex2);        
        
        int length1 = endIndex1 - startIndex1;
        int length2 = endIndex2 - startIndex2;
        
        if (length1 != length2) {
            return false;
        }
        
        for (int i=0;i<length1;i++,startIndex1++,startIndex2++) {
            if (string1.charAt(startIndex1) != string2.charAt(startIndex2)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 
     */
    public static int indexOf(String string, int startIndex, int endIndex,
            char character) {
        
        checkRange(string,startIndex,endIndex);
        
        for (int i=startIndex;i<endIndex;i++) {
            if (string.charAt(i) == character) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     */
    public static int indexOf(String string, IntHandler handler) {
        return indexOf(string,0,string.length(),handler);
    }
    
    /**
     * 
     */
    public static int indexOf(String string, int startIndex, int endIndex,
            IntHandler handler) {
        
        checkRange(string,startIndex,endIndex);
        
        if (handler == null) {
            throw new IllegalArgumentException("handler may not be null");
        }
        
        for (int i=startIndex;i<endIndex;i++) {
            if (handler.handleInteger(string.charAt(i))) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     */
    public static boolean containsOnly(String string, Span span,
            IntHandler handler) {
        
        if (span == null) {
            throw new IllegalArgumentException("span may not be null");
        }
        
        return containsOnly(string,span.start,span.end,handler);
    }
    
    /**
     * 
     */
    public static boolean containsOnly(String string, int startIndex,
            int endIndex, IntHandler handler) {
        
        checkRange(string,startIndex,endIndex);
        
        if (handler == null) {
            throw new IllegalArgumentException("handler may not be null");
        }
        
        if (startIndex == endIndex) {
            return false;
        }
        
        for (int i=startIndex;i<endIndex;i++) {
            if (!handler.handleInteger(string.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 
     */
    public static boolean isASCIIIdentifier(String string) {
        return isIdentifier(string,ASCII_IDENTIFIER_START,ASCII_IDENTIFIER_PART);
    }
    
    /**
     * 
     */
    public static boolean isJavaIdentifier(String string) {
        return isIdentifier(string,JAVA_IDENTIFIER_START,JAVA_IDENTIFIER_PART);
    }
    
    /**
     * 
     */
    private static boolean isIdentifier(String string,
            IntHandler startHandler, IntHandler partHandler) {
        
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        int length = string.length();
        
        if ((length > 0) && startHandler.handleInteger(string.charAt(0))) {
            for (int i=1;i<length;i++) {
                if (!partHandler.handleInteger(string.charAt(i))) {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    //============================
    // Character Property Methods
    //============================
    
    /**
     * 
     */
    public static boolean isASCIIDigit(int codePoint) {
        return (codePoint >= '0') && (codePoint <= '9');
    }
    
    /**
     * 
     */
    public static boolean isASCIIOctalDigit(int codePoint) {
        return (codePoint >= '0') && (codePoint <= '7');
    }
    
    /**
     * 
     */
    public static boolean isASCIIHexDigit(int codePoint) {
        return (codePoint >= '0') && (codePoint <= '9') ||
               (codePoint >= 'a') && (codePoint <= 'f') ||
               (codePoint >= 'A') && (codePoint <= 'F');
    }
    
    /**
     * 
     */
    public static boolean isASCIILetter(int codePoint) {
        return (codePoint >= 'a') && (codePoint <= 'z') ||
               (codePoint >= 'A') && (codePoint <= 'Z');
    }
    
    /**
     * 
     */
    public static boolean isASCIILowercase(int codePoint) {
        return (codePoint >= 'a') && (codePoint <= 'z');
    }
    
    /**
     * 
     */
    public static boolean isASCIIUppercase(int codePoint) {
        return (codePoint >= 'A') && (codePoint <= 'Z');
    }
    
    /**
     * 
     */
    public static boolean isASCIILetterOrDigit(int codePoint) {
        return isASCIILetter(codePoint) || isASCIIDigit(codePoint);
    }
    
    /**
     * 
     */
    public static boolean isASCIIIdentifierStart(int codePoint) {
        return isASCIILetter(codePoint);
    }
    
    /**
     * 
     */
    public static boolean isASCIIIdentifierPart(int codePoint) {
        return isASCIILetterOrDigit(codePoint) || (codePoint == '_');
    }
    
    //==============================
    // Char Handler Factory Methods
    //==============================
    
    /**
     * 
     */
    public static IntHandler createHandler(int codePoint, boolean invert) {
        return new ValueHandler(codePoint,invert);
    }
    
    /**
     * 
     */
    public static IntHandler createHandler(char[] characterSet, boolean invert) {
        return new IdSetHandler(characterSet,invert);
    }
    
    /**
     * 
     */
    public static IntHandler createHandler(int[] codePointSet, boolean invert) {
        return new IdSetHandler(codePointSet,invert);
    }
    
    //===================
    // Exception Methods
    //===================
    
    /**
     * 
     */
    private static int checkRange(String string, int startIndex,
            int endIndex) {
        
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        if (startIndex < 0) {
            throw new IllegalArgumentException("start index may not be negative");
        }
        
        int length = string.length();
        
        if (endIndex > length) {
            throw new IllegalArgumentException("end index may not be greater than string length");
        }
        
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("start index may not be greater than end index");
        }
        
        return length;
    }
    
    /**
     * 
     */
    private static int checkIndex(String string, int index) {
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        if (index < 0) {
            throw new IllegalArgumentException("start index may not be negative");
        }
        
        int stringLength = string.length();
        
        if (index > stringLength) {
            throw new IllegalArgumentException("start index may not be "
                    + "greater than maximum index");
        }
        
        return stringLength;
    }
}
