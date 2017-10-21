/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import java.util.Arrays;

import org.annoflex.util.integer.MutableIntArray;

/**
 * @author Stefan Czaska
 */
public class LineInfo {
    
    //===========
    // Constants
    //===========
    
    private static final int LINE_TERMINATOR_NONE = 0;
    private static final int LINE_TERMINATOR_LF   = 1;
    private static final int LINE_TERMINATOR_CR   = 2;
    private static final int LINE_TERMINATOR_CRLF = 3;
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int lineCount;
    
    /**
     * 
     */
    private final int lfCount;
    
    /**
     * 
     */
    private final int crCount;
    
    /**
     * 
     */
    private final int crlfCount;
    
    /**
     * 
     */
    private final String primaryLineTerminator;
    
    /**
     * 
     */
    private final byte[] byteLineMap;
    
    /**
     * 
     */
    private final short[] shortLineMap;
    
    /**
     * 
     */
    private final int[] intLineMap;
    
    /**
     * 
     */
    private final byte[] byteLineStart;
    
    /**
     * 
     */
    private final short[] shortLineStart;
    
    /**
     * 
     */
    private final int[] intLineStart;
    
    /**
     * 
     */
    private final byte[] byteLineContentLength;
    
    /**
     * 
     */
    private final short[] shortLineContentLength;
    
    /**
     * 
     */
    private final int[] intLineContentLength;
    
    /**
     * 
     */
    private final byte[] lineTerminator;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public LineInfo(String string) {
        this(string,0,string.length());
    }
    
    /**
     * 
     */
    public LineInfo(String string, int startIndex, int endIndex) {
        if (string == null) {
            throw new IllegalArgumentException("sequence may not be null");
        }
        
        if (startIndex < 0) {
            throw new IllegalArgumentException("start index may not be negative");
        }
        
        if (endIndex > string.length()) {
            throw new IllegalArgumentException("end index may not be greater "
                    + "than sequence length");
        }
        
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("start index may not e greater "
                    + "than end index");
        }
        
        MutableIntArray lineMap = new MutableIntArray();
        MutableIntArray lineStart = new MutableIntArray();
        MutableIntArray lineContentLength = new MutableIntArray();
        MutableIntArray lineTerminator = new MutableIntArray();
        
        int curIndex = 0;
        int lineCount = 0;
        int lfCount = 0;
        int crCount = 0;
        int crlfCount = 0;
        int lfLineIndex = -1;
        int crLineIndex = -1;
        int crlfLineIndex = -1;
        int maxLineContentLength = 0;
        int nextTerminatorStart = nextTerminatorStart(string,startIndex,endIndex);
        
        while (nextTerminatorStart != -1) {
            int nextLineStart = nextLineStart(string,nextTerminatorStart,endIndex);
            int nextLineStartRelative = nextLineStart - startIndex;
            int curLineLength = nextLineStartRelative - curIndex;
            int curLineTerminatorLength = nextLineStart - nextTerminatorStart;
            int curLineContentLength = curLineLength - curLineTerminatorLength;
            
            lineStart.add(curIndex);
            lineContentLength.add(curLineContentLength);
            
            if (curLineContentLength > maxLineContentLength) {
                maxLineContentLength = curLineContentLength;
            }
            
            lineMap.add(lineCount,curLineLength);
            curIndex = nextLineStartRelative;
            
            if (curLineTerminatorLength == 2) {
                crlfCount++;
                lineTerminator.add(LINE_TERMINATOR_CRLF);
                
                if (crlfLineIndex == -1) {
                    crlfLineIndex = lineCount;
                }
            }
            
            else if (string.charAt(nextLineStart-1) == '\r') {
                crCount++;
                lineTerminator.add(LINE_TERMINATOR_CR);
                
                if (crLineIndex == -1) {
                    crLineIndex = lineCount;
                }
            }
            
            else {
                lfCount++;
                lineTerminator.add(LINE_TERMINATOR_LF);
                
                if (lfLineIndex == -1) {
                    lfLineIndex = lineCount;
                }
            }
            
            lineCount++;
            nextTerminatorStart = nextTerminatorStart(string,nextLineStart,endIndex);
        }
        
        int rangeLength = endIndex - startIndex;
        int lastLineContentLength = rangeLength - curIndex;
        
        lineStart.add(curIndex);
        lineContentLength.add(lastLineContentLength);
        lineTerminator.add(LINE_TERMINATOR_NONE);
        
        if (lastLineContentLength > maxLineContentLength) {
            maxLineContentLength = lastLineContentLength;
        }
        
        lineMap.add(lineCount,lastLineContentLength+1);
        lineCount++;
        
        this.lineCount = lineCount;
        this.lfCount = lfCount;
        this.crCount = crCount;
        this.crlfCount = crlfCount;
        this.primaryLineTerminator = determinePrimaryLineTerminator(lfCount,
                crCount,crlfCount,lfLineIndex,crLineIndex,crlfLineIndex);
        this.byteLineMap = createByteArray(lineMap,lineCount-1);
        this.shortLineMap = createShortArray(lineMap,lineCount-1);
        this.intLineMap = createIntArray(lineMap,lineCount-1);
        this.byteLineStart = createByteArray(lineStart,rangeLength);
        this.shortLineStart = createShortArray(lineStart,rangeLength);
        this.intLineStart = createIntArray(lineStart,rangeLength);
        this.byteLineContentLength = createByteArray(lineContentLength,maxLineContentLength);
        this.shortLineContentLength = createShortArray(lineContentLength,maxLineContentLength);
        this.intLineContentLength = createIntArray(lineContentLength,maxLineContentLength);
        this.lineTerminator = createByteArray(lineTerminator,LINE_TERMINATOR_CRLF);
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public final int getLineCount() {
        return lineCount;
    }
    
    /**
     * 
     */
    public final int getLFCount() {
        return lfCount;
    }
    
    /**
     * 
     */
    public final int getCRCount() {
        return crCount;
    }
    
    /**
     * 
     */
    public final int getCRLFCount() {
        return crlfCount;
    }
    
    /**
     * 
     */
    public final String getPrimaryLineTerminator() {
        return primaryLineTerminator;
    }
    
    //=======================
    // Line Property Methods
    //=======================
    
    /**
     * 
     */
    public int lineContentLength(int lineIndex) {
        
        // Note: Prefer the byte array as this should be the common case.
        if (byteLineContentLength != null) {
            
            // 0xff is used to convert to unsigned byte
            return byteLineContentLength[lineIndex] & 0xff;
        }
        
        if (shortLineContentLength != null) {
            
            // 0xffff is used to convert to unsigned short
            return shortLineContentLength[lineIndex] & 0xffff;
        }
        
        return intLineContentLength[lineIndex];
    }
    
    /**
     * 
     */
    public int lineTerminatorLength(int lineIndex) {
        switch(lineTerminator[lineIndex]) {
        case LINE_TERMINATOR_NONE: return 0;
        case LINE_TERMINATOR_LF:   return 1;
        case LINE_TERMINATOR_CR:   return 1;
        case LINE_TERMINATOR_CRLF: return 2;
        default: throw new IllegalArgumentException("invalid line terminator");
        }
    }
    
    /**
     * 
     */
    public int lineLength(int lineIndex) {
        return lineContentLength(lineIndex) + lineTerminatorLength(lineIndex);
    }
    
    /**
     * 
     */
    public int lineStart(int lineIndex) {
        
        // Note: Prefer the short array as this should be the common case.
        if (shortLineStart != null) {
            
            // 0xffff is used to convert to unsigned short
            return shortLineStart[lineIndex] & 0xffff;
        }
        
        if (byteLineStart != null) {
            
            // 0xff is used to convert to unsigned byte
            return byteLineStart[lineIndex] & 0xff;
        }
        
        return intLineStart[lineIndex];
    }
    
    /**
     * 
     */
    public int lineContentEnd(int lineIndex) {
        return lineStart(lineIndex) + lineContentLength(lineIndex);
    }
    
    /**
     * 
     */
    public int lineEnd(int lineIndex) {
        return lineStart(lineIndex) + lineLength(lineIndex);
    }
    
    /**
     * 
     */
    public String lineTerminator(int lineIndex) {
        switch(lineTerminator[lineIndex]) {
        case LINE_TERMINATOR_NONE: return null;
        case LINE_TERMINATOR_LF:   return "\n";
        case LINE_TERMINATOR_CR:   return "\r";
        case LINE_TERMINATOR_CRLF: return "\r\n";
        default: throw new IllegalArgumentException("invalid line terminator");
        }
    }
    
    //============================
    // Character Property Methods
    //============================
    
    /**
     * 
     */
    public int lineAt(int charIndex) {
        
        // Note: Prefer the short array as this should be the common case.
        if (shortLineMap != null) {
            
            // 0xffff is used to convert to unsigned short
            return shortLineMap[charIndex] & 0xffff;
        }
        
        if (byteLineMap != null) {
            
            // 0xff is used to convert to unsigned byte
            return byteLineMap[charIndex] & 0xff;
        }
        
        return intLineMap[charIndex];
    }
    
    /**
     * 
     */
    public int lineStartAt(int charIndex) {
        return lineStart(lineAt(charIndex));
    }
    
    /**
     * 
     */
    public int lineLengthAt(int charIndex) {
        return lineLength(lineAt(charIndex));
    }
    
    /**
     * 
     */
    public int lineEndAt(int charIndex) {
        return lineEnd(lineAt(charIndex));
    }
    
    /**
     * 
     */
    public int columnAt(int charIndex) {
        return charIndex - lineStartAt(charIndex);
    }
    
    //========================
    // Initialization Methods
    //========================
    
    /**
     * 
     */
    private int nextTerminatorStart(String string, int startIndex,
            int endIndex) {
        
        while (startIndex < endIndex) {
            char curChar = string.charAt(startIndex);
            
            if ((curChar == '\r') || (curChar == '\n')) {
                return startIndex;
            }
            
            startIndex++;
        }
        
        return -1;
    }
    
    /**
     * 
     */
    private int nextLineStart(String string, int startIndex,
            int endIndex) {
        
        char char1 = string.charAt(startIndex++);
        
        if (char1 == '\n') {
            return startIndex;
        }
        
        if ((startIndex < endIndex) && (string.charAt(startIndex) == '\n')) {
            return startIndex + 1;
        }
        
        return startIndex;
    }
    
    /**
     * 
     */
    private String determinePrimaryLineTerminator(int lfCount, int crCount,
            int crlfCount, int lfLineIndex, int crLineIndex, int crlfLineIndex) {
        
        if ((lfCount == 0) && (crCount == 0) && (crlfCount == 0)) {
            return null;
        }
        
        LineTerminatorInfo[] array = new LineTerminatorInfo[3];
        array[0] = new LineTerminatorInfo("\n",lfCount,lfLineIndex);
        array[1] = new LineTerminatorInfo("\r",crCount,crLineIndex);
        array[2] = new LineTerminatorInfo("\r\n",crlfCount,crlfLineIndex);
        
        Arrays.sort(array);
        
        return array[0].lineTerminator;
    }
    
    /**
     * 
     */
    private byte[] createByteArray(MutableIntArray array, int maxValue) {
        return maxValue <= 255 ? array.toByteArray() : null;
    }
    
    /**
     * 
     */
    private short[] createShortArray(MutableIntArray array, int maxValue) {
        return (maxValue >= 256) && (maxValue <= 65535) ?
                array.toShortArray() : null;
    }
    
    /**
     * 
     */
    private int[] createIntArray(MutableIntArray array, int maxValue) {
        return maxValue >= 65536 ? array.toArray() : null;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[lineCount=");
        buffer.append(lineCount);
        buffer.append(",CRLF=");
        buffer.append(crlfCount);
        buffer.append(",LF=");
        buffer.append(lfCount);
        buffer.append(",CR=");
        buffer.append(crCount);
        buffer.append(",primaryLineTerminator=");
        
        if (primaryLineTerminator != null) {
            if (primaryLineTerminator.length() == 2) {
                buffer.append("CRLF");
            }
            
            else if (primaryLineTerminator.charAt(0) == '\r') {
                buffer.append("CR");
            }
            
            else {
                buffer.append("LF");
            }
        }
        
        else {
            buffer.append("null");
        }
        
        buffer.append("]");
        
        return buffer.toString();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class LineTerminatorInfo implements Comparable<LineTerminatorInfo> {
        
        public final String lineTerminator;
        public final int count;
        public final int index;
        
        /**
         * 
         */
        public LineTerminatorInfo(String lineTerminator, int count, int index) {
            this.lineTerminator = lineTerminator;
            this.count = count;
            this.index = index;
        }
        
        /**
         * 
         */
        public int compareTo(LineTerminatorInfo o) {
            int countDelta = o.count - count;
            
            return countDelta != 0 ? countDelta : index - o.index;
        }
    }
}
