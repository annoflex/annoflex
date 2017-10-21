/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

import java.io.IOException;
import java.io.Reader;

/**
 * @option inputMode = reader
 * @option functionality = all- setReader+ getMatchLength+ getMatchText+
 * getMatchChar+
 * @option visibility = all- getNextToken+ getMatchText+
 * 
 * @author Stefan Czaska
 */
final class SourceScanner {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public SourceScanner(Reader reader) {
        setReader(reader,4096);
    }
    
    //========================
    // Token Creation Methods
    //========================
    
    /** @expr @ */  SourceToken createAt()        { return SourceToken.AT; }
    /** @expr = */  SourceToken createEqual()     { return SourceToken.EQUAL; }
    /** @expr : */  SourceToken createColon()     { return SourceToken.COLON; }
    /** @expr \{ */ SourceToken createLCB()       { return SourceToken.LCB; }
    /** @expr \} */ SourceToken createRCB()       { return SourceToken.RCB; }
    /** @expr \( */ SourceToken createLRB()       { return SourceToken.LRB; }
    /** @expr \) */ SourceToken createRRB()       { return SourceToken.RRB; }
    /** @expr ; */  SourceToken createSemicolon() { return SourceToken.SEMICOLON; }
    
    /**
     * @expr \p{whitespace}+
     **/
    SourceToken createWhitespace() {
        return SourceToken.WHITESPACE;
    }
    
    /**
     * @expr [a-zA-Z0-9_.-]+
     **/
    SourceToken createIdentifier() {
        return SourceToken.IDENTIFIER;
    }
    
    //===============
    // Match Methods
    //===============
    
    /**
     * 
     */
    public int getMatchHexValue() {
        int result = 0;
        int base = 1;
        
        for (int i=getMatchLength()-1;i>=0;i--) {
            result += Character.digit(getMatchChar(i),16)*base;
            base *= 16;
        }
        
        return result;
    }
    
    //%%LEX-MAIN-START%%
    
    //================================================
    //     _                      _____ _             
    //    / \   _ __  _ __   ___ |  ___| | ___ _  __  
    //   / _ \ |  _ \|  _ \ / _ \| |_  | |/ _ \ \/ /  
    //  / ___ \| | | | | | | (_) |  _| | |  __/>  <   
    // /_/   \_\_| |_|_| |_|\___/|_|   |_|\___/_/\_\  
    //                                                
    //================================================
    
    /*************************************************
     *             Generation Statistics             *
     * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                               *
     * Rules:           10                           *
     * Lookaheads:      0                            *
     * Alphabet length: 11                           *
     * NFA states:      25                           *
     * DFA states:      11                           *
     * Static size:     12 KB                        *
     * Instance size:   32 Bytes + O(maxMatchLength) *
     *                                               *
     ************************************************/
    
    //=================
    // Table Constants
    //=================
    
    /**
     * Maps Unicode characters to DFA input symbols.
     */
    private static final byte[] CHARACTER_MAP = createCharacterMap(
    "\0\t\t\5\0\22\t\1\0\7\6\1\7\1\0\3\n\2\0\1\n\n\3\1\b\1\0\1\2\1" +
    "\0\2\1\1\n\32\0\4\n\1\0\1\n\32\4\1\0\1\5\1\0\7\t\1\0\32\t\1" +
    "\0\u15df\t\1\0\u097f\t\13\0\35\t\2\0\5\t\1\0\57\t\1\0\u0fa0" +
    "\t\1");
    
    /**
     * The transition table of the DFA.
     */
    private static final byte[][] TRANSITION_TABLE = createTransitionTable(
    "\0\1\2\1\3\1\4\1\5\1\6\1\7\1\b\1\t\1\n\1\13\1\0\13\0\13\0\13\0\13\0\13" +
    "\0\13\0\13\0\13\0\t\n\1\0\1\0\n\13\1");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\b\1\t\1\n\1");
    
    //==================
    // Helper Constants
    //==================
    
    /**
     * An empty char array which is used to avoid null checks.
     */
    private static final char[] EMPTY_CHAR_ARRAY = new char[]{};
    
    //===============
    // Reader Fields
    //===============
    
    /**
     * A {@link Reader} from which the input characters are read.
     */
    private Reader reader;
    
    /**
     * The initial size of the character buffer.
     */
    private int readerStartCapacity;
    
    //===============
    // Buffer Fields
    //===============
    
    /**
     * A buffer which contains the characters of the reader.
     */
    private char[] buffer = EMPTY_CHAR_ARRAY;
    
    /**
     * The position of the first available character.
     */
    private int bufferStart;
    
    /**
     * The position after the last available character.
     */
    private int bufferEnd;
    
    //============
    // Dot Fields
    //============
    
    /**
     * The start position of the next scan.
     */
    private int dot;
    
    //==============
    // Match Fields
    //==============
    
    /**
     * The start of the last match.
     */
    private int matchStart;
    
    /**
     * The end of the last match.
     */
    private int matchEnd;
    
    //===============
    // Table Methods
    //===============
    
    /**
     * Creates the character map of the scanner.
     * 
     * @param characterMapData The compressed data of the character map.
     * @return The character map of the scanner.
     */
    private static byte[] createCharacterMap(String characterMapData) {
        byte[] characterMap = new byte[12289];
        int length = characterMapData.length();
        int i = 0;
        int j = 0;
        
        while (i < length) {
            byte curValue = (byte)characterMapData.charAt(i++);
            
            for (int x=characterMapData.charAt(i++);x>0;x--) {
                characterMap[j++] = curValue;
            }
        }
        
        return characterMap;
    }
    
    /**
     * Creates the transition table of the scanner.
     * 
     * @param transitionTableData The compressed data of the transition table.
     * @return The transition table of the scanner.
     */
    private static byte[][] createTransitionTable(String transitionTableData) {
        byte[][] transitionTable = new byte[11][11];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 11) {
                k = 0;
                j++;
            }
        }
        
        return transitionTable;
    }
    
    /**
     * Creates the action map of the scanner.
     * 
     * @param actionMapData The compressed data of the action map.
     * @return The action map of the scanner.
     */
    private static byte[] createActionMap(String actionMapData) {
        byte[] actionMap = new byte[11];
        int length = actionMapData.length();
        int i = 0;
        int j = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)actionMapData.charAt(i++) - 1);
            
            for (int x=actionMapData.charAt(i++);x>0;x--) {
                actionMap[j++] = curValue;
            }
        }
        
        return actionMap;
    }
    
    //================
    // Reader Methods
    //================
    
    /**
     * Reinitializes the scanner by setting the {@link Reader} and the initial
     * buffer size to the specified values. All other values are set to their
     * default value.
     * 
     * @param reader The new {@link Reader} from which the input characters are
     * read.
     * @param startCapacity The initial size of the character buffer.
     */
    private void setReader(Reader reader, int startCapacity) {
        this.reader = reader;
        this.readerStartCapacity = Math.max(startCapacity+1,2);
        
        buffer = EMPTY_CHAR_ARRAY;
        bufferStart = 0;
        bufferEnd = 0;
        
        dot = 0;
        
        matchStart = 0;
        matchEnd = 0;
    }
    
    //===============
    // Match Methods
    //===============
    
    /**
     * Returns the length of the last match.
     * 
     * @return The length of the last match.
     */
    private int getMatchLength() {
        return matchEnd - matchStart;
    }
    
    /**
     * Returns the text of the last match.
     * 
     * @return The text of the last match.
     */
    public String getMatchText() {
        int count = matchEnd - matchStart;
        
        return count > 0 ? new String(buffer,matchStart-bufferStart,count) : "";
    }
    
    /**
     * Returns a character relative to the start of the last match.
     * 
     * @param index The index of the character relative to the last match.
     * @return The character at the specified position.
     * @throws IndexOutOfBoundsException If the specified index is invalid
     */
    private char getMatchChar(int index) {
        int stringIndex = matchStart + index;
        
        if ((stringIndex < matchStart) || (stringIndex >= bufferEnd)) {
            throw new IndexOutOfBoundsException("match character not available");
        }
        
        return buffer[stringIndex-bufferStart];
    }
    
    //==============
    // Scan Methods
    //==============
    
    /**
     * Performs at the current position the next step of the lexical analysis
     * and returns the result.
     * 
     * @return The result of the next step of the lexical analysis.
     * @throws IOException If an IO error occurs
     * @throws IllegalStateException If a lexical error occurs
     */
    public SourceToken getNextToken() throws IOException {
        if ((reader != null) && hasNextChar(dot)) {
            
            // find longest match
            int curState = 0;
            int iterator = dot;
            int matchState = -1;
            int matchPosition = 0;
            
            do {
                char curChar = buffer[iterator-bufferStart];
                
                curState = TRANSITION_TABLE[curState][curChar >= 12289 ?
                        0 : CHARACTER_MAP[curChar]];
                
                if (curState == -1) {
                    break;
                }
                
                if (ACTION_MAP[curState] != -1) {
                    matchState = curState;
                    matchPosition = iterator;
                }
            } while (hasNextChar(++iterator));
            
            // match found, perform action
            if (matchState != -1) {
                int endPosition = matchPosition + 1;
                
                matchStart = dot;
                matchEnd = endPosition;
                dot = endPosition;
                
                switch(ACTION_MAP[matchState]) {
                case 0: return createAt();
                case 1: return createEqual();
                case 2: return createColon();
                case 3: return createLCB();
                case 4: return createRCB();
                case 5: return createLRB();
                case 6: return createRRB();
                case 7: return createSemicolon();
                case 8: return createWhitespace();
                case 9: return createIdentifier();
                }
            }
            
            // no match found, set match values and report as error
            matchStart = dot;
            matchEnd = dot;
            
            throw new IllegalStateException("invalid input");
        }
        
        // no match found, set match values and return to caller
        matchStart = dot;
        matchEnd = dot;
        
        return null;
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * Checks whether for the specified position a following character exists.
     * This is always the case if the position lies inside the range of
     * available characters. If the position lies straight after the last
     * available character then the reader is used to check whether there are
     * further characters.
     * 
     * @param position A position inside the range of available characters.
     * @return True if there is a next character, otherwise false.
     * @throws IOException If an IO error occurs
     */
    private boolean hasNextChar(int position) throws IOException {
        if (position < bufferEnd) {
            return true;
        }
        
        // if the buffer is empty then create a new one with the specified size
        if (buffer == EMPTY_CHAR_ARRAY) {
            buffer = new char[readerStartCapacity];
        }
        
        // otherwise check whether the buffer is full
        else if ((bufferEnd - bufferStart) == buffer.length) {
            int usedSpace = bufferEnd - dot;
            
            if (usedSpace > (buffer.length >> 1)) {
                char[] newBuffer = new char[buffer.length+(buffer.length>>1)];
                System.arraycopy(buffer,dot-bufferStart,newBuffer,0,usedSpace);
                buffer = newBuffer;
            }
            
            else {
                System.arraycopy(buffer,dot-bufferStart,buffer,0,usedSpace);
            }
            
            bufferStart = dot;
        }
        
        // read further characters and check results
        int usedSpace = bufferEnd - bufferStart;
        int charsRead = reader.read(buffer,usedSpace,buffer.length-usedSpace);
        
        if (charsRead > 0) {
            bufferEnd += charsRead;
            
            return true;
        }
        
        return false;
    }
    
    //%%LEX-MAIN-END%%
}
