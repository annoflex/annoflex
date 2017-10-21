/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.parser;

import org.annoflex.regex.dom.CharRef;
import org.annoflex.regex.dom.SequenceRef;
import org.annoflex.regex.dom.SequenceRefType;
import org.annoflex.regex.unicode.Property;
import org.annoflex.regex.unicode.PropertySelector;
import org.annoflex.util.text.StringToolkit;
import org.annoflex.util.token.TokenIterator;

/**
 * This is the scanner for the regular expression parser of AnnoFlex. It scans
 * an AnnoFlex-compatible regular expression and returns the tokens with their
 * corresponding token values.
 * 
 * @option internal = setString+
 * @option functionality = all- setString+ getMatchLength+ getMatchTotalLength+
 * getMatchText+ getMatchTextRange+ getMatchChar+ lexicalState+
 * @option visibility = all- scanMethods+
 * @macro CurlyBrackets = \{ [^\}]* \}?
 * @macro Hex = [[:xdigit:]]
 * 
 * @author Stefan Czaska
 */
public class RegExScanner implements TokenIterator<RegExToken> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int[] stateStack = new int[3];
    
    /**
     * 
     */
    private int stateStackSize;
    
    //=============
    // Constructor
    //=============
    
    /**
     * Constructs a {@link RegExScanner}.
     */
    public RegExScanner() {
    }
    
    /**
     * Constructs a {@link RegExScanner} and initializes it with a string to be
     * scanned.
     * 
     * @param regex The regular expression to be scanned.
     */
    public RegExScanner(String regex) {
        setString(regex);
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setString(String string) {
        setStringInternal(string);
        
        stateStackSize = 1;
        stateStack[0] = LEXICAL_STATE_INITIAL;
    }
    
    /**
     * 
     */
    private void enterLexicalState(int lexicalState) {
        stateStack[stateStackSize++] = lexicalState;
        setLexicalState(lexicalState);
    }
    
    /**
     * 
     */
    private void leaveLexicalState() {
        setLexicalState(stateStack[--stateStackSize-1]);
    }
    
    /**
     * 
     */
    public boolean isIgnoreWhitespace() {
        switch(lexicalState) {
        case LEXICAL_STATE_CC:
        case LEXICAL_STATE_SS:
            return false;
        default: return true;
        }
    }
    
    /**
     * 
     */
    private boolean isInsideCharacterClass() {
        return (stateStackSize > 1) &&
               (stateStack[1] == LEXICAL_STATE_CC);
    }
    
    //=========================
    // Escape Sequence Methods
    //=========================
    
    /**
     * @expr <Initial,AB,CB,SS,CC>\\ / [^]?
     **/
    void handleBackslash() {
        if (getMatchTotalLength() != 2) {
            throw invalid();
        }
        
        enterLexicalState(LEXICAL_STATE_ES);
    }
    
    //%%LEX-CONDITION-START%%ES%%
    
    // alarm bell character
    /** @expr a */ RegExToken handleSmallA() { return escapedChar('\u0007'); }
    
    // beginning of input, #### UNSUPPORTED ####
    /** @expr A */ RegExToken handleLargeA() { throw unsupported(); }
    
    /**
     * @expr b
     **/
    RegExToken handleSmallB() {
        
        // inside of character class: backspace character
        if (isInsideCharacterClass()) {
            return escapedChar('\u0008');
        }
        
        // outside of character class: word boundary, #### UNSUPPORTED ####
        throw unsupported();
    }
    
    // non-word boundary, #### UNSUPPORTED ####
    /** @expr B */ RegExToken handleLargeB() { throw unsupported(); }
    
    // control character
    /**
     * @expr c[^]?
     **/
    RegExToken handleSmallC() {
        leaveLexicalState();
        
        if (getMatchLength() != 2) {
            throw invalid();
        }
        
        char controlChar = getMatchChar(1);
        
        if (StringToolkit.isASCIILowercase(controlChar)) {
            controlChar -= 32;
        }
        
        controlChar ^= 0x40;
        
        if (controlChar > 127) {
            throw invalid();
        }
        
        return createCharRefToken(RegExTokenType.ESCAPED_CHAR,-1,
                new CharRef(controlChar));
    }
    
    // matches a data unit, #### UNSUPPORTED ####
    /** @expr C */ RegExToken handleLargeC() { throw unsupported(); }
    
    // an ASCII digit [0-9]
    /** @expr d */ RegExToken handleSmallD() { return escapedCharClass(Property.POSIX_DIGIT,false); }
    
    // all except an ASCII digit [0-9]
    /** @expr D */ RegExToken handleLargeD() { return escapedCharClass(Property.POSIX_DIGIT,true); }
    
    // escape character
    /** @expr e */ RegExToken handleSmallE() { return escapedChar('\u001b'); }
    
    // end of quote sequence, may not appear as a single escape sequence
    /** @expr E */ RegExToken handleLargeE() { throw new RegExScanException(
            "invalid end of quote sequence (start is missing)"); }
    
    // form feed character
    /** @expr f */ RegExToken handleSmallF() { return escapedChar('\u000c'); }
    
    // #### UNASSIGNED ####
    /** @expr F */ RegExToken handleLargeF() { throw unassigned(); }
    
    // capturing group, #### UNSUPPORTED ####
    /** @expr g */ RegExToken handleSmallG() { throw unsupported(); }
    
    // end of previous match, #### UNSUPPORTED ####
    /** @expr G */ RegExToken handleLargeG() { throw unsupported(); }
    
    // a Unicode horizontal white space character
    /** @expr h */ RegExToken handleSmallH() { return escapedCharClass(Property.HWHITE_SPACE,false); }
    
    // all except a Unicode horizontal white space character
    /** @expr H */ RegExToken handleLargeH() { return escapedCharClass(Property.HWHITE_SPACE,true); }
    
    // #### UNASSIGNED ####
    /** @expr i */ RegExToken handleSmallI() { throw unassigned(); }
    
    // #### UNASSIGNED ####
    /** @expr I */ RegExToken handleLargeI() { throw unassigned(); }
    
    // #### UNASSIGNED ####
    /** @expr j */ RegExToken handleSmallJ() { throw unassigned(); }
    
    // #### UNASSIGNED ####
    /** @expr J */ RegExToken handleLargeJ() { throw unassigned(); }
    
    // capturing group, #### UNSUPPORTED ####
    /** @expr k */ RegExToken handleSmallK() { throw unsupported(); }
    
    // restarts match region, #### UNSUPPORTED ####
    /** @expr K */ RegExToken handleLargeK() { throw unsupported(); }
    
    // lowercase operator, #### UNSUPPORTED ####
    /** @expr l */ RegExToken handleSmallL() { throw unsupported(); }
    
    // lowercase operator, #### UNSUPPORTED ####
    /** @expr L */ RegExToken handleLargeL() { throw unsupported(); }
    
    // #### UNASSIGNED ####
    /** @expr m */ RegExToken handleSmallM() { throw unassigned(); }
    
    // #### UNASSIGNED ####
    /** @expr M */ RegExToken handleLargeM() { throw unassigned(); }
    
    // line feed character
    /** @expr n */ RegExToken handleSmallN() { return escapedChar('\n'); }
    
    // all except a Unicode newline character
    /** @expr N */ RegExToken handleLargeN() { return escapedCharClass(Property.VWHITE_SPACE,true); }
    
    // named unicode character
    /**
     * @expr N{CurlyBrackets}
     **/
    RegExToken handleLargeNCurlyBrackets() {
        leaveLexicalState();
        
        return createCharRefToken(RegExTokenType.ESCAPED_CHAR,-1,
                new CharRef(extractCurlyBracketContent()));
    }
    
    // character with octal value of arbitrary length
    /** @expr o{CurlyBrackets}? */ RegExToken handleSmallO() { return escapedCharNumber(8); }
    
    // #### UNASSIGNED ####
    /** @expr O */ RegExToken handleLargeO() { throw unassigned(); }
    
    // all characters with the specified properties
    /** @expr p{CurlyBrackets}? */ RegExToken handleSmallP() { return escapedCharClassString(false); }
    
    // all characters except the ones with the specified properties
    /** @expr P{CurlyBrackets}? */ RegExToken handleLargeP() { return escapedCharClassString(true); }
    
    // Unicode literal cluster, #### UNSUPPORTED ####
    /** @expr q */ RegExToken handleSmallQ() { throw unsupported(); }
    
    // quote sequence which ends with \E
    /**
     * @expr Q !([^]*\\E[^]*) (\\E)?
     **/
    RegExToken handleLargeQ() {
        leaveLexicalState();
        
        int matchLength = getMatchLength();
        
        if ((matchLength < 4) || (getMatchChar(matchLength-1) != 'E') ||
            (getMatchChar(matchLength-2) != '\\')) {
            
            throw invalid();
        }
        
        return createSequenceRefToken(RegExTokenType.ESCAPED_CHAR_SEQUENCE,-1,
                new SequenceRef(getMatchText(1,2)));
    }
    
    // carriage return character
    /** @expr r */ RegExToken handleSmallR() { return escapedChar('\r'); }
    
    // newline character sequence
    /**
     * @expr R
     **/
    RegExToken handleLargeR() {
        leaveLexicalState();
        
        return createSequenceRefToken(RegExTokenType.ESCAPED_CHAR_SEQUENCE,-1,
                new SequenceRef(SequenceRefType.NEWLINE));
    }
    
    // an ASCII whitespace character
    /** @expr s */ RegExToken handleSmallS() { return escapedCharClass(Property.POSIX_SPACE,false); }
    
    // all except an ASCII whitespace character
    /** @expr S */ RegExToken handleLargeS() { return escapedCharClass(Property.POSIX_SPACE,true); }
    
    // tab character
    /** @expr t */ RegExToken handleSmallT() { return escapedChar('\u0009'); }
    
    // #### UNASSIGNED ####
    /** @expr T */ RegExToken handleLargeT() { throw unassigned(); }
    
    // 16-bit hex character
    /** @expr u{Hex}{,4} */ RegExToken handleSmallU() { return escapedCharNumber(1,4,16,65535); }
    
    // hex character of arbitrary length
    /** @expr u{CurlyBrackets}? */ RegExToken handleSmallUCurlyBrackets() { return escapedCharNumber(16); }
    
    // 24-bit hex character
    /** @expr U{Hex}{,6} */ RegExToken handleLargeU() { return escapedCharNumber(1,6,16,65535); }
    
    // a Unicode vertical whitespace character
    /** @expr v */ RegExToken handleSmallV() { return escapedCharClass(Property.VWHITE_SPACE,false); }
    
    // all except a Unicode vertical whitespace character
    /** @expr V */ RegExToken handleLargeV() { return escapedCharClass(Property.VWHITE_SPACE,true); }
    
    // an ASCII word character [a-zA-Z0-9_]
    /** @expr w */ RegExToken handleSmallW() { return escapedCharClass(Property.POSIX_WORD,false); }
    
    // all except an ASCII word character [a-zA-Z0-9_]
    /** @expr W */ RegExToken handleLargeW() { return escapedCharClass(Property.POSIX_WORD,true); }
    
    // 8-bit hex character
    /** @expr x{Hex}{,2} */ RegExToken handleSmallX() { return escapedCharNumber(1,2,16,65535); }
    
    // hex character of arbitrary length
    /** @expr x{CurlyBrackets}? */ RegExToken handleSmallXCurlyBrackets() { return escapedCharNumber(16); }
    
    // Unicode extended grapheme cluster, #### UNSUPPORTED ####
    /** @expr X */ RegExToken handleLargeX() { throw unsupported(); }
    
    // #### UNASSIGNED ####
    /** @expr y */ RegExToken handleSmallY() { throw unassigned(); }
    
    // #### UNASSIGNED ####
    /** @expr Y */ RegExToken handleLargeY() { throw unassigned(); }
    
    // end of input, #### UNSUPPORTED ####
    /** @expr z */ RegExToken handleSmallZ() { throw unsupported(); }
    
    // end of input, #### UNSUPPORTED ####
    /** @expr Z */ RegExToken handleLargeZ() { throw unsupported(); }
    
    // 8-bit octal character
    /** @expr [0-9]{1} */ RegExToken handleOctalCharacter1() { return escapedCharNumber(0,1,8,255); }
    /** @expr [0-9]{2} */ RegExToken handleOctalCharacter2() { return escapedCharNumber(0,2,8,255); }
    /** @expr [0-9]{3} */ RegExToken handleOctalCharacter3() { return escapedCharNumber(0,3,8,255); }
    
    // all other characters are used "as is"
    /** @expr [^a-zA-Z0-9] */ RegExToken handleCharacter() { return escapedChar(getMatchChar(0)); }
    
    //%%LEX-CONDITION-END%%
    
    //================================
    // Escape Sequence Helper Methods
    //================================
    
    /**
     * 
     */
    private String extractCurlyBracketContent() {
        int matchLength = getMatchLength();
        
        if ((matchLength < 4) || (getMatchChar(1) != '{') ||
            (getMatchChar(matchLength-1) != '}')) {
            
            throw invalid();
        }
        
        return getMatchText(2,1);
    }
    
    /**
     * 
     */
    private RegExToken escapedChar(char character) {
        leaveLexicalState();
        
        return createCharRefToken(RegExTokenType.ESCAPED_CHAR,-1,
                new CharRef(character));
    }
    
    /**
     * 
     */
    private RegExToken escapedCharNumber(int radix) {
        leaveLexicalState();
        
        String text = extractCurlyBracketContent();
        
        if (!StringToolkit.containsOnly(text,0,text.length(),
                StringToolkit.ASCII_LETTER_OR_DIGIT)) {
            
            throw invalid();
        }
        
        int charValue = parseInteger(text,radix,65535,"invalid escape sequence");
        
        return createCharRefToken(RegExTokenType.ESCAPED_CHAR,-1,
                new CharRef((char)charValue));
    }
    
    /**
     * 
     */
    private RegExToken escapedCharNumber(int prefixLength,
            int sequenceLength, int radix, int maxValue) {
        
        leaveLexicalState();
        
        if (getMatchLength() != (prefixLength + sequenceLength)) {
            throw invalid();
        }
        
        int charValue = parseInteger(getMatchText(prefixLength,0),radix,
                maxValue,"invalid escape sequence");
        
        return createCharRefToken(RegExTokenType.ESCAPED_CHAR,-1,
                new CharRef((char)charValue));
    }
    
    /**
     * 
     */
    private RegExToken escapedCharClass(Property property, boolean complement) {
        leaveLexicalState();
        
        return createClassRefToken(RegExTokenType.ESCAPED_CHAR_CLASS,-1,
                PropertySelector.forBinary(property,complement));
    }
    
    /**
     * 
     */
    private RegExToken escapedCharClassString(boolean invert) {
        leaveLexicalState();
        
        return createClassRefToken(RegExTokenType.ESCAPED_CHAR_CLASS,-1,
                new PropertySelector(extractCurlyBracketContent(),invert));
    }
    
    //===============================
    // Angle Bracket Context Methods
    //===============================
    
    /**
     * @expr <Initial>\<
     **/
    RegExToken enterAngleBracket() {
        enterLexicalState(LEXICAL_STATE_AB);
        
        return createCharacterToken(RegExTokenType.LT,'<');
    }
    
    /**
     * @expr <AB,CB,SS,CC>\<
     **/
    RegExToken handleLT() {
        return createCharacterToken(RegExTokenType.LT,'<');
    }
    
    /**
     * @expr <AB>\>
     **/
    RegExToken leaveAngleBracket() {
        leaveLexicalState();
        
        return createCharacterToken(RegExTokenType.GT,'>');
    }
    
    /**
     * @expr <Initial,CB,SS,CC>\>
     **/
    RegExToken handleGT() {
        return createCharacterToken(RegExTokenType.GT,'>');
    }
    
    //===============================
    // Curly Bracket Context Methods
    //===============================
    
    /**
     * @expr <Initial,CC>\{
     **/
    RegExToken enterCurlyBracket() {
        enterLexicalState(LEXICAL_STATE_CB);
        
        return createCharacterToken(RegExTokenType.LCB,'{');
    }
    
    /**
     * @expr <AB,CB,SS>\{
     **/
    RegExToken handleLCB() {
        return createCharacterToken(RegExTokenType.LCB,'{');
    }
    
    /**
     * @expr <CB>\}
     **/
    RegExToken leaveCurlyBracket() {
        leaveLexicalState();
        
        return createCharacterToken(RegExTokenType.RCB,'}');
    }
    
    /**
     * @expr <Initial,AB,SS,CC>\}
     **/
    RegExToken handleRCB() {
        return createCharacterToken(RegExTokenType.RCB,'}');
    }
    
    //=========================
    // String Sequence Methods
    //=========================
    
    /**
     * @expr <Initial>\"
     **/
    RegExToken enterStringSequence() {
        enterLexicalState(LEXICAL_STATE_SS);
        
        return createCharacterToken(RegExTokenType.QUOTE,'"');
    }
    
    /**
     * @expr <SS>\"
     **/
    RegExToken leaveStringSequence() {
        leaveLexicalState();
        
        return createCharacterToken(RegExTokenType.QUOTE,'"');
    }
    
    /**
     * @expr <AB,CB,CC>\"
     **/
    RegExToken createQuote() {
        return createCharacterToken(RegExTokenType.QUOTE,'"');
    }
    
    //=========================
    // Character Class Methods
    //=========================
    
    /**
     * @expr <Initial,CC>\[
     **/
    RegExToken enterCharacterClass() {
        enterLexicalState(LEXICAL_STATE_CC);
        
        return createCharacterToken(RegExTokenType.LSB,'[');
    }
    
    /**
     * @expr <AB,CB,SS>\[
     **/
    RegExToken handleLSB() {
        return createCharacterToken(RegExTokenType.LSB,'[');
    }
    
    /**
     * @expr <CC>\]
     **/
    RegExToken leaveCharacterClass() {
        leaveLexicalState();
        
        return createCharacterToken(RegExTokenType.RSB,']');
    }
    
    /**
     * @expr <Initial,AB,CB,SS>\]
     **/
    RegExToken handleRSB() {
        return createCharacterToken(RegExTokenType.RSB,']');
    }
    
    //%%LEX-CONDITION-START%%CC%%
    
    /** @expr "[:alnum:]"   */ RegExToken createAlnum()     { return namedCharClass(Property.POSIX_ALNUM,false); }
    /** @expr "[:^alnum:]"  */ RegExToken createAlnumInv()  { return namedCharClass(Property.POSIX_ALNUM,true); }
    /** @expr "[:alpha:]"   */ RegExToken createAlpha()     { return namedCharClass(Property.POSIX_ALPHA,false); }
    /** @expr "[:^alpha:]"  */ RegExToken createAlphaInv()  { return namedCharClass(Property.POSIX_ALPHA,true); }
    /** @expr "[:ascii:]"   */ RegExToken createAscii()     { return namedCharClass(Property.POSIX_ASCII,false); }
    /** @expr "[:^ascii:]"  */ RegExToken createAsciiInv()  { return namedCharClass(Property.POSIX_ASCII,true); }
    /** @expr "[:blank:]"   */ RegExToken createBlank()     { return namedCharClass(Property.POSIX_BLANK,false); }
    /** @expr "[:^blank:]"  */ RegExToken createBlankInv()  { return namedCharClass(Property.POSIX_BLANK,true); }
    /** @expr "[:cntrl:]"   */ RegExToken createCntrl()     { return namedCharClass(Property.POSIX_CNTRL,false); }
    /** @expr "[:^cntrl:]"  */ RegExToken createCntrlInv()  { return namedCharClass(Property.POSIX_CNTRL,true); }
    /** @expr "[:digit:]"   */ RegExToken createDigit()     { return namedCharClass(Property.POSIX_DIGIT,false); }
    /** @expr "[:^digit:]"  */ RegExToken createDigitInv()  { return namedCharClass(Property.POSIX_DIGIT,true); }
    /** @expr "[:graph:]"   */ RegExToken createGraph()     { return namedCharClass(Property.POSIX_GRAPH,false); }
    /** @expr "[:^graph:]"  */ RegExToken createGraphInv()  { return namedCharClass(Property.POSIX_GRAPH,true); }
    /** @expr "[:lower:]"   */ RegExToken createLower()     { return namedCharClass(Property.POSIX_LOWER,false); }
    /** @expr "[:^lower:]"  */ RegExToken createLowerInv()  { return namedCharClass(Property.POSIX_LOWER,true); }
    /** @expr "[:print:]"   */ RegExToken createPrint()     { return namedCharClass(Property.POSIX_PRINT,false); }
    /** @expr "[:^print:]"  */ RegExToken createPrintInv()  { return namedCharClass(Property.POSIX_PRINT,true); }
    /** @expr "[:punct:]"   */ RegExToken createPunct()     { return namedCharClass(Property.POSIX_PUNCT,false); }
    /** @expr "[:^punct:]"  */ RegExToken createPunctInv()  { return namedCharClass(Property.POSIX_PUNCT,true); }
    /** @expr "[:space:]"   */ RegExToken createSpace()     { return namedCharClass(Property.POSIX_SPACE,false); }
    /** @expr "[:^space:]"  */ RegExToken createSpaceInv()  { return namedCharClass(Property.POSIX_SPACE,true); }
    /** @expr "[:upper:]"   */ RegExToken createUpper()     { return namedCharClass(Property.POSIX_UPPER,false); }
    /** @expr "[:^upper:]"  */ RegExToken createUpperInv()  { return namedCharClass(Property.POSIX_UPPER,true); }
    /** @expr "[:word:]"    */ RegExToken createWord()      { return namedCharClass(Property.POSIX_WORD,false); }
    /** @expr "[:^word:]"   */ RegExToken createWordInv()   { return namedCharClass(Property.POSIX_WORD,true); }
    /** @expr "[:xdigit:]"  */ RegExToken createXdigit()    { return namedCharClass(Property.POSIX_XDIGIT,false); }
    /** @expr "[:^xdigit:]" */ RegExToken createXdigitInv() { return namedCharClass(Property.POSIX_XDIGIT,true); }
    
    /**
     * 
     */
    private RegExToken namedCharClass(Property property, boolean complement) {
        return createClassRefToken(RegExTokenType.NAMED_CHAR_CLASS,0,
                PropertySelector.forBinary(property,complement));
    }
    
    /** @expr "||" */ RegExToken createOrOr()       { return createToken(RegExTokenType.OR_OR); }
    /** @expr "~~" */ RegExToken createTildeTilde() { return createToken(RegExTokenType.TILDE_TILDE); }
    /** @expr "--" */ RegExToken createMinusMinus() { return createToken(RegExTokenType.MINUS_MINUS); }
    /** @expr "&&" */ RegExToken createAndAnd()     { return createToken(RegExTokenType.AND_AND); }
    
    //%%LEX-CONDITION-END%%
    
    //=======================
    // Special Token Methods
    //=======================
    
    /**
     * @expr <AB,CB>[a-zA-Z][a-zA-Z0-9_]*
     **/
    RegExToken handleName() {
        return createStringToken(RegExTokenType.NAME,getMatchText());
    }
    
    /**
     * @expr <AB,CB>[0-9]+
     **/
    RegExToken handleNumber() {
        int numberValue = parseInteger(getMatchText(),10,Integer.MAX_VALUE,
                "invalid number value");
        
        return createIntegerToken(RegExTokenType.NUMBER,numberValue);
    }
    
    //%%LEX-CONDITION-START%%Initial,AB,CB,SS,CC%%
    
    /** @expr \^ */ RegExToken createHat()    { return createCharacterToken(RegExTokenType.HAT,'^'); }
    /** @expr \| */ RegExToken createOr()     { return createCharacterToken(RegExTokenType.OR,'|'); }
    /** @expr \( */ RegExToken createLRB()    { return createCharacterToken(RegExTokenType.LRB,'('); }
    /** @expr \) */ RegExToken createRRB()    { return createCharacterToken(RegExTokenType.RRB,')'); }
    /** @expr \. */ RegExToken createDot()    { return createCharacterToken(RegExTokenType.DOT,'.'); }
    /** @expr \? */ RegExToken createQMark()  { return createCharacterToken(RegExTokenType.QMARK,'?'); }
    /** @expr \* */ RegExToken createStar()   { return createCharacterToken(RegExTokenType.STAR,'*'); }
    /** @expr \+ */ RegExToken createPlus()   { return createCharacterToken(RegExTokenType.PLUS,'+'); }
    /** @expr \/ */ RegExToken createSlash()  { return createCharacterToken(RegExTokenType.SLASH,'/'); }
    /** @expr \~ */ RegExToken createTilde()  { return createCharacterToken(RegExTokenType.TILDE,'~'); }
    /** @expr \! */ RegExToken createEMark()  { return createCharacterToken(RegExTokenType.EMARK,'!'); }
    /** @expr \$ */ RegExToken createDollar() { return createCharacterToken(RegExTokenType.DOLLAR,'$'); }
    /** @expr \, */ RegExToken createComma()  { return createCharacterToken(RegExTokenType.COMMA,','); }
    /** @expr \- */ RegExToken createMinus()  { return createCharacterToken(RegExTokenType.MINUS,'-'); }
    
    /**
     * @expr \p{whitespace}
     **/
    RegExToken createWS() {
        return createCharacterToken(RegExTokenType.WS,getMatchChar(0));
    }
    
    /**
     * @expr [^]
     **/
    RegExToken createCH() {
        return createCharacterToken(RegExTokenType.CH,getMatchChar(0));
    }
    
    //%%LEX-CONDITION-END%%
    
    //===============================
    // Token Creation Helper Methods
    //===============================
    
    /**
     * 
     */
    private RegExToken createToken(RegExTokenType type) {
        return new RegExToken(type,matchStart,matchEnd,null);
    }
    
    /**
     * 
     */
    private RegExToken createCharacterToken(RegExTokenType type, char character) {
        return new RegExToken(type,matchStart,matchEnd,Character.valueOf(character));
    }
    
    /**
     * 
     */
    private RegExToken createStringToken(RegExTokenType type, String string) {
        return new RegExToken(type,matchStart,matchEnd,string);
    }
    
    /**
     * 
     */
    private RegExToken createCharRefToken(RegExTokenType type, int matchStartOffset,
            CharRef charRef) {
        
        return new RegExToken(type,matchStart+matchStartOffset,matchEnd,charRef);
    }
    
    /**
     * 
     */
    private RegExToken createClassRefToken(RegExTokenType type, int matchStartOffset,
            PropertySelector selector) {
        
        return new RegExToken(type,matchStart+matchStartOffset,matchEnd,selector);
    }
    
    /**
     * 
     */
    private RegExToken createSequenceRefToken(RegExTokenType type, int matchStartOffset,
            SequenceRef sequenceRef) {
        
        return new RegExToken(type,matchStart+matchStartOffset,matchEnd,sequenceRef);
    }
    
    /**
     * 
     */
    private RegExToken createIntegerToken(RegExTokenType type, int integer) {
        return new RegExToken(type,matchStart,matchEnd,Integer.valueOf(integer));
    }
    
    //=======================
    // Integer Parse Methods
    //=======================
    
    /**
     * 
     */
    private int parseInteger(String text, int radix, int maxValue, String errorMessage) {
        try {
            int intValue = Integer.parseInt(text,radix);
            
            if (intValue > maxValue) {
                throw new RegExScanException(errorMessage);
            }
            
            return intValue;
        }
        
        catch(NumberFormatException e) {
            throw new RegExScanException(errorMessage);
        }
    }
    
    //===================
    // Exception Methods
    //===================
    
    /**
     * 
     */
    private RegExScanException invalid() {
        return new RegExScanException("invalid escape sequence");
    }
    
    /**
     * 
     */
    private RegExScanException unsupported() {
        return new RegExScanException("unsupported escape sequence: "+getMatchChar(0));
    }
    
    /**
     * 
     */
    private RegExScanException unassigned() {
        return new RegExScanException("unassigned escape sequence: "+getMatchChar(0));
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
     * Rules:           125                          *
     * Lookaheads:      1                            *
     * Alphabet length: 80                           *
     * NFA states:      740                          *
     * DFA states:      413                          *
     * Static size:     85 KB                        *
     * Instance size:   36 Bytes                     *
     *                                               *
     ************************************************/
    
    //=================
    // Table Constants
    //=================
    
    /**
     * Maps Unicode characters to DFA input symbols.
     */
    private static final byte[] CHARACTER_MAP = createCharacterMap(
    "\0\t\32\5\0\22\32\1\27\1\2\1\0\1\30\1\0\1\16\1\0\1\20\1\21\1" +
    "\24\1\25\1\31\1\r\1\22\1\26\1\33\n\t\1\0\1\5\1\0\1\6\1\23\1" +
    "\0\1\105\1\107\1\111\1\113\1\115\1\117\1\35\1\37\1\41\1\43\1" +
    "\45\1\'\1\51\1\53\1\55\1\57\1\61\1\63\1\65\1\67\1\71\1\73\1" +
    "\75\1\77\1\101\1\103\1\7\1\3\1\b\1\n\1\17\1\0\1\104\1\106\1" +
    "\110\1\112\1\114\1\116\1\34\1\36\1\40\1\"\1\44\1\46\1\50\1\52\1" +
    "\54\1\56\1\60\1\62\1\64\1\66\1\70\1\72\1\74\1\76\1\100\1\102\1" +
    "\4\1\13\1\1\1\f\1\0\6\32\1\0\32\32\1\0\u15df\32\1\0\u097f\32\13" +
    "\0\35\32\2\0\5\32\1\0\57\32\1\0\u0fa0\32\1");
    
    /**
     * The transition table of the DFA.
     */
    private static final short[][] TRANSITION_TABLE = createTransitionTable(
    "\31\1\6\1\7\1\2\1\5\1\3\1\4\1\b\1\t\1\31\1\n\1\13\1\23\1\27\1\31\2\f\1" +
    "\r\1\16\1\17\1\20\1\21\1\22\1\24\1\25\1\26\1\30\1\31\65\32\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\65\1" +
    "\40\1\41\1\34\1\37\1\35\1\36\1\"\1\43\1\65\1\46\1\'\1\57\1\63\1\65\2" +
    "\50\1\51\1\52\1\53\1\54\1\55\1\56\1\60\1\61\1\62\1\64\1\45\1\44\64\66\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\17\44\1\0\13\44\65\0\33" +
    "\45\1\0\64\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\121\1\74\1\75\1\70\1\73\1" +
    "\71\1\72\1\76\1\77\1\121\1\102\1\103\1\113\1\117\1\121\2\104\1\105\1" +
    "\106\1\107\1\110\1\111\1\112\1\114\1\115\1\116\1\120\1\101\1\100\64\122\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\17\100\1\0\13\100\65\0\33" +
    "\101\1\0\64\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\153\1\130\1\131\1\124\1\127\1" +
    "\125\1\126\1\132\1\133\1\153\1\\\1\135\1\145\1\151\1\153\2\136\1\137\1" +
    "\140\1\141\1\142\1\143\1\144\1\146\1\147\1\150\1\152\1\153\65\154\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\70\u0148\1\0\27\0\120\0\120\0\120\0\120\0\120\0\120\0\t\260\1\0\106" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\13\222\1" +
    "\0\104\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\f\223\1\0\103\0\120" +
    "\0\120\0\120\0\r\224\1\0\102\0\120\0\120\245\1\162\1\163\1\u0149\1\161\1" +
    "\157\1\160\1\164\1\165\1\245\1\226\1\227\1\237\1\243\1\247\1\245\1\230\1" +
    "\231\1\232\1\233\1\234\1\235\1\236\1\240\1\241\1\242\1\244\1\245\65\0\16" +
    "\225\1\0\101\0\b\221\1\0\107\0\t\250\1\0\106\0\66\251\1\0\31\0\40\252\1" +
    "\0\57\0\34\253\1\0\63\0\40\254\1\0\57\0\112\255\1\0\5\0\34\u0101\1\0\t" +
    "\365\1\0\7\337\1\0\5\323\1\0\3\307\1\0\3\274\1\0\1\256\1\0\5\u0131\1" +
    "\0\1\u0125\1\0\1\u0119\1\0\1\u010d\1\0\5\0\n\257\1\0\21\u0107\1\0\t\373\1" +
    "\0\7\345\1\0\5\331\1\0\3\315\1\0\3\301\1\0\1\267\1\0\5\u0137\1\0\1\u012b\1" +
    "\0\1\u011f\1\0\1\u0113\1\0\5\0\b\220\1\0\107\0\t\261\1\0\106\0\66\262\1" +
    "\0\31\0\40\263\1\0\57\0\34\264\1\0\63\0\40\265\1\0\57\0\112\266\1\0\5" +
    "\0\b\217\1\0\107\0\t\270\1\0\106\0\112\271\1\0\5\0\62\272\1\0\35\0\54" +
    "\273\1\0\43\0\b\216\1\0\107\0\t\275\1\0\106\0\112\276\1\0\5\0\62\277\1" +
    "\0\35\0\54\300\1\0\43\0\b\215\1\0\107\0\t\302\1\0\106\0\62\303\1\0\35" +
    "\0\114\304\1\0\3\0\56\305\1\0\41\0\56\306\1\0\41\0\b\214\1\0\107\0\t" +
    "\310\1\0\106\0\62\311\1\0\35\0\114\312\1\0\3\0\56\313\1\0\41\0\56\314\1" +
    "\0\41\0\b\213\1\0\107\0\t\316\1\0\106\0\114\317\1\0\3\0\110\320\1\0\7" +
    "\0\104\321\1\0\13\0\56\322\1\0\41\0\b\212\1\0\107\0\t\324\1\0\106\0\114" +
    "\325\1\0\3\0\110\326\1\0\7\0\104\327\1\0\13\0\56\330\1\0\41\0\b\211\1" +
    "\0\107\0\t\332\1\0\106\0\66\333\1\0\31\0\110\334\1\0\7\0\52\335\1\0\45" +
    "\0\62\352\1\0\5\336\1\0\27\0\b\210\1\0\107\0\t\340\1\0\106\0\66\341\1" +
    "\0\31\0\110\342\1\0\7\0\52\343\1\0\45\0\62\357\1\0\5\344\1\0\27\0\b\207\1" +
    "\0\107\0\t\346\1\0\106\0\66\347\1\0\31\0\52\350\1\0\45\0\40\351\1\0\57" +
    "\0\b\206\1\0\107\0\t\353\1\0\106\0\66\354\1\0\31\0\52\355\1\0\45\0\40" +
    "\356\1\0\57\0\b\205\1\0\107\0\t\360\1\0\106\0\62\361\1\0\35\0\114\362\1" +
    "\0\3\0\74\363\1\0\23\0\54\364\1\0\43\0\b\204\1\0\107\0\t\366\1\0\106" +
    "\0\62\367\1\0\35\0\114\370\1\0\3\0\74\371\1\0\23\0\54\372\1\0\43\0\b" +
    "\203\1\0\107\0\t\374\1\0\106\0\36\375\1\0\61\0\56\376\1\0\41\0\104\377\1" +
    "\0\13\0\62\u0100\1\0\35\0\b\202\1\0\107\0\t\u0102\1\0\106\0\36\u0103\1" +
    "\0\61\0\56\u0104\1\0\41\0\104\u0105\1\0\13\0\62\u0106\1\0\35\0\b\201\1" +
    "\0\107\0\t\u0108\1\0\106\0\66\u0109\1\0\31\0\40\u010a\1\0\57\0\34\u010b\1" +
    "\0\63\0\40\u010c\1\0\57\0\b\200\1\0\107\0\t\u010e\1\0\106\0\66\u010f\1" +
    "\0\31\0\40\u0110\1\0\57\0\34\u0111\1\0\63\0\40\u0112\1\0\57\0\b\177\1" +
    "\0\107\0\t\u0114\1\0\106\0\46\u0115\1\0\51\0\62\u0116\1\0\35\0\66\u0117\1" +
    "\0\31\0\52\u0118\1\0\45\0\b\176\1\0\107\0\t\u011a\1\0\106\0\46\u011b\1" +
    "\0\51\0\62\u011c\1\0\35\0\66\u011d\1\0\31\0\52\u011e\1\0\45\0\b\175\1" +
    "\0\107\0\t\u0120\1\0\106\0\44\u0121\1\0\53\0\52\u0122\1\0\45\0\104\u0123\1" +
    "\0\13\0\46\u0124\1\0\51\0\b\174\1\0\107\0\t\u0126\1\0\106\0\44\u0127\1" +
    "\0\53\0\52\u0128\1\0\45\0\104\u0129\1\0\13\0\46\u012a\1\0\51\0\b\173\1" +
    "\0\107\0\t\u012c\1\0\106\0\40\u012d\1\0\57\0\40\u012e\1\0\57\0\110\u012f\1" +
    "\0\7\0\46\u013c\1\0\r\u0130\1\0\33\0\b\172\1\0\107\0\t\u0132\1\0\106" +
    "\0\40\u0133\1\0\57\0\40\u0134\1\0\57\0\110\u0135\1\0\7\0\46\u0141\1\0\r" +
    "\u0136\1\0\33\0\b\171\1\0\107\0\t\u0138\1\0\106\0\104\u0139\1\0\13\0\36" +
    "\u013a\1\0\61\0\52\u0145\1\0\3\u013b\1\0\41\0\b\170\1\0\107\0\t\u013d\1" +
    "\0\106\0\104\u013e\1\0\13\0\36\u013f\1\0\61\0\52\155\1\0\3\u0140\1\0\41" +
    "\0\b\167\1\0\107\0\t\u0142\1\0\106\0\50\u0143\1\0\'\0\70\u0144\1\0\27" +
    "\0\b\166\1\0\107\0\t\u0146\1\0\106\0\50\u0147\1\0\'\156\120\u0185\33" +
    "\u0182\1\u0157\1\u0158\1\u0159\1\u015a\1\u015b\1\u015c\1\u015d\1\u015e\1" +
    "\u015f\1\u0160\1\u0161\1\u0162\1\u0163\1\u0164\1\u0165\1\u0166\1\u0195\1" +
    "\u0169\1\u0193\1\u0191\1\u016c\1\u018f\1\u016e\1\u016f\1\u0170\1\u0171\1" +
    "\u0172\1\u0173\1\u018a\1\u0176\1\u0177\1\u0178\1\u0179\1\u017a\1\u0187\1" +
    "\u017d\1\u017e\1\u017f\1\u0180\1\u0181\1\u014b\1\u014c\1\u014d\1\u014e\1" +
    "\u0197\1\u0150\1\u0151\1\u0152\1\u0153\1\u0154\1\u0155\1\u0156\1\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\4\u0196\1\0\113\0\120\0\120\0\120\0\120\0\120\0\120\0\120" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\33\u019d\1\0\50\u019d\f" +
    "\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\120\0\33" +
    "\u0183\1\0\64\0\33\u0184\1\0\64\0\120\0\120\u0186\1\u017c\1\u0186\116" +
    "\0\4\u0186\1\0\26\u0188\1\0\50\u0188\f\0\33\u017b\1\0\50\u017b\f\u0189\1" +
    "\u0175\1\u0189\116\0\4\u0189\1\0\26\u018b\1\0\50\u018b\f\0\33\u018d\1" +
    "\0\50\u018d\f\0\33\u0174\1\0\50\u0174\f\0\33\u018c\1\0\50\u018c\f\u018f\3" +
    "\u018e\1\u018f\111\u016d\1\u018f\2\u018f\3\u018e\1\u018f\114\u0190\1" +
    "\u016b\1\u0190\116\0\4\u0190\1\0\113\u0192\1\u016a\1\u0192\116\0\4\u0192\1" +
    "\0\113\u0194\1\u0168\1\u0194\116\0\4\u0194\1\0\113\u0196\1\u0167\1\u0196\116" +
    "\u014f\120\0\120\0\33\u0198\1\0\50\u0198\f\0\33\u0199\1\0\50\u0199\f" +
    "\0\33\u019a\1\0\50\u019a\f\0\33\u019b\1\0\50\u019b\f\0\33\u019c\1\0\50" +
    "\u019c\f");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\75\1\100\1\101\1\104\1\105\1\110\1\113\1\156\1" +
    "\157\1\160\1\161\1\162\1\163\1\164\1\165\1\166\1\167\1" +
    "\170\1\171\1\172\1\173\1\174\1\175\1\1\1\0\1\1\1\76\1" +
    "\77\1\102\1\104\1\107\1\111\1\113\1\154\1\155\1\156\1" +
    "\157\1\160\1\161\1\162\1\163\1\164\1\165\1\166\1\167\1" +
    "\170\1\171\1\172\1\173\1\174\1\175\1\1\1\0\1\1\1\76\1" +
    "\100\1\102\1\103\1\107\1\111\1\113\1\154\1\155\1\156\1" +
    "\157\1\160\1\161\1\162\1\163\1\164\1\165\1\166\1\167\1" +
    "\170\1\171\1\172\1\173\1\174\1\175\1\1\1\0\1\1\1\76\1" +
    "\100\1\102\1\104\1\106\1\111\1\113\1\156\1\157\1\160\1" +
    "\161\1\162\1\163\1\164\1\165\1\166\1\167\1\170\1\171\1" +
    "\172\1\173\1\174\1\175\1\1\1\0\1\1\1\76\1\100\1\101\1" +
    "\104\1\107\1\110\1\112\1\114\1\115\1\116\1\117\1\120\1" +
    "\121\1\122\1\123\1\124\1\125\1\126\1\127\1\130\1\131\1" +
    "\132\1\133\1\\\1\135\1\136\1\137\1\140\1\141\1\142\1\143\1" +
    "\144\1\145\1\146\1\147\1\150\1\151\1\152\1\153\1\156\1" +
    "\157\1\160\1\161\1\162\1\163\1\164\1\165\1\166\1\167\1" +
    "\170\1\171\1\172\1\173\1\174\1\175\1\0\1\175\1\0\241\1\1" +
    "\0\1\2\1\3\1\4\1\5\1\6\1\7\1\b\1\t\1\n\1\13\1\f\1\r\1" +
    "\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1" +
    "\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\"\1\43\1" +
    "\44\1\45\1\46\1\'\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1" +
    "\57\1\60\1\61\1\62\1\63\1\64\1\65\1\66\1\67\1\70\1\71\1" +
    "\72\1\73\1\74\1\63\1\62\2\54\1\53\4\44\2\"\2\41\2\37\2" +
    "\36\1\6\1\55\6");
    
    //=========================
    // Lexical State Constants
    //=========================
    
    /**
     * The ordinal number of the lexical state "INITIAL".
     */
    private static final int LEXICAL_STATE_INITIAL = 0;
    
    /**
     * The ordinal number of the lexical state "AB".
     */
    private static final int LEXICAL_STATE_AB = 1;
    
    /**
     * The ordinal number of the lexical state "CB".
     */
    private static final int LEXICAL_STATE_CB = 2;
    
    /**
     * The ordinal number of the lexical state "SS".
     */
    private static final int LEXICAL_STATE_SS = 3;
    
    /**
     * The ordinal number of the lexical state "CC".
     */
    private static final int LEXICAL_STATE_CC = 4;
    
    /**
     * The ordinal number of the lexical state "ES".
     */
    private static final int LEXICAL_STATE_ES = 5;
    
    //===============
    // String Fields
    //===============
    
    /**
     * The current string to be scanned.
     */
    private String string = "";
    
    //===============
    // Region Fields
    //===============
    
    /**
     * The start of the scan region.
     */
    private int regionStart;
    
    /**
     * The end of the scan region.
     */
    private int regionEnd;
    
    //============
    // Dot Fields
    //============
    
    /**
     * The start position of the next scan.
     */
    private int dot;
    
    //======================
    // Lexical State Fields
    //======================
    
    /**
     * The current lexical state.
     */
    private int lexicalState = LEXICAL_STATE_INITIAL;
    
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
    
    /**
     * The end of the last match including the lookahead.
     */
    private int matchLookahead;
    
    //===============
    // Helper Fields
    //===============
    
    /**
     * The start state of the DFA.
     */
    private int startState;
    
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
    private static short[][] createTransitionTable(String transitionTableData) {
        short[][] transitionTable = new short[413][80];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            short curValue = (short)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 80) {
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
        byte[] actionMap = new byte[413];
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
    // String Methods
    //================
    
    /**
     * Sets the string to be scanned. The scan region is set to the entire
     * string.
     * 
     * @param string The new string to be scanned.
     */
    private void setStringInternal(String string) {
        this.string = string != null ? string : "";
        
        regionStart = 0;
        regionEnd = this.string.length();
        
        dot = 0;
        lexicalState = LEXICAL_STATE_INITIAL;
        
        matchStart = 0;
        matchEnd = 0;
        matchLookahead = 0;
        
        startState = 0;
    }
    
    //=======================
    // Lexical State Methods
    //=======================
    
    /**
     * Sets the current lexical state.
     * 
     * @param lexicalState The new lexical state.
     * @throws IllegalArgumentException If the specified state is invalid
     */
    private void setLexicalState(int lexicalState) {
        switch(lexicalState) {
        case LEXICAL_STATE_INITIAL: startState = 0; break;
        case LEXICAL_STATE_AB: startState = 26; break;
        case LEXICAL_STATE_CB: startState = 54; break;
        case LEXICAL_STATE_SS: startState = 82; break;
        case LEXICAL_STATE_CC: startState = 165; break;
        case LEXICAL_STATE_ES: startState = 329; break;
        default:
            throw new IllegalArgumentException("invalid lexical state");
        }
        
        this.lexicalState = lexicalState;
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
     * Returns the length of the last match including the lookahead.
     * 
     * @return The length of the last match including the lookahead.
     */
    private int getMatchTotalLength() {
        return matchLookahead - matchStart;
    }
    
    /**
     * Returns the text of the last match.
     * 
     * @return The text of the last match.
     */
    private String getMatchText() {
        return string.substring(matchStart,matchEnd);
    }
    
    /**
     * Returns a substring relative to the last match.
     * 
     * @param startOffset The forward-oriented start offset of the substring
     * relative to the start of the last match.
     * @param endOffset The backward-oriented end offset of the substring
     * relative to the end of the last match.
     * @return The substring at the specified indices.
     * @throws IndexOutOfBoundsException If the specified indices are invalid
     */
    private String getMatchText(int startOffset, int endOffset) {
        int startIndex = matchStart + startOffset;
        int endIndex = matchEnd - endOffset;
        
        if ((startIndex < regionStart) || (endIndex > regionEnd) ||
            (startIndex > endIndex)) {
            
            throw new IndexOutOfBoundsException("match text not available");
        }
        
        return string.substring(startIndex,endIndex);
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
        
        if ((stringIndex < regionStart) || (stringIndex >= regionEnd)) {
            throw new IndexOutOfBoundsException("match character not available");
        }
        
        return string.charAt(stringIndex);
    }
    
    //==============
    // Scan Methods
    //==============
    
    /**
     * Performs at the current position the next step of the lexical analysis
     * and returns the result.
     * 
     * @return The result of the next step of the lexical analysis.
     * @throws IllegalStateException If a lexical error occurs
     */
    public RegExToken getNextToken() {
        while (dot < regionEnd) {
            
            // find longest match
            int curState = startState;
            int iterator = dot;
            int matchState = -1;
            int matchPosition = 0;
            
            do {
                char curChar = string.charAt(iterator);
                
                curState = TRANSITION_TABLE[curState][curChar >= 12289 ?
                        0 : CHARACTER_MAP[curChar]];
                
                if (curState == -1) {
                    break;
                }
                
                if (ACTION_MAP[curState] != -1) {
                    matchState = curState;
                    matchPosition = iterator;
                }
            } while (++iterator < regionEnd);
            
            // match found, perform action
            if (matchState != -1) {
                int endPosition = matchPosition + 1;
                
                matchStart = dot;
                matchEnd = endPosition;
                matchLookahead = endPosition;
                dot = endPosition;
                
                switch(ACTION_MAP[matchState]) {
                case 0:
                    dot = matchStart + 1;
                    matchEnd = dot;
                    handleBackslash(); continue;
                
                case 1: return handleSmallA();
                case 2: return handleLargeA();
                case 3: return handleSmallB();
                case 4: return handleLargeB();
                case 5: return handleSmallC();
                case 6: return handleLargeC();
                case 7: return handleSmallD();
                case 8: return handleLargeD();
                case 9: return handleSmallE();
                case 10: return handleLargeE();
                case 11: return handleSmallF();
                case 12: return handleLargeF();
                case 13: return handleSmallG();
                case 14: return handleLargeG();
                case 15: return handleSmallH();
                case 16: return handleLargeH();
                case 17: return handleSmallI();
                case 18: return handleLargeI();
                case 19: return handleSmallJ();
                case 20: return handleLargeJ();
                case 21: return handleSmallK();
                case 22: return handleLargeK();
                case 23: return handleSmallL();
                case 24: return handleLargeL();
                case 25: return handleSmallM();
                case 26: return handleLargeM();
                case 27: return handleSmallN();
                case 28: return handleLargeN();
                case 29: return handleLargeNCurlyBrackets();
                case 30: return handleSmallO();
                case 31: return handleLargeO();
                case 32: return handleSmallP();
                case 33: return handleLargeP();
                case 34: return handleSmallQ();
                case 35: return handleLargeQ();
                case 36: return handleSmallR();
                case 37: return handleLargeR();
                case 38: return handleSmallS();
                case 39: return handleLargeS();
                case 40: return handleSmallT();
                case 41: return handleLargeT();
                case 42: return handleSmallU();
                case 43: return handleSmallUCurlyBrackets();
                case 44: return handleLargeU();
                case 45: return handleSmallV();
                case 46: return handleLargeV();
                case 47: return handleSmallW();
                case 48: return handleLargeW();
                case 49: return handleSmallX();
                case 50: return handleSmallXCurlyBrackets();
                case 51: return handleLargeX();
                case 52: return handleSmallY();
                case 53: return handleLargeY();
                case 54: return handleSmallZ();
                case 55: return handleLargeZ();
                case 56: return handleOctalCharacter1();
                case 57: return handleOctalCharacter2();
                case 58: return handleOctalCharacter3();
                case 59: return handleCharacter();
                case 60: return enterAngleBracket();
                case 61: return handleLT();
                case 62: return leaveAngleBracket();
                case 63: return handleGT();
                case 64: return enterCurlyBracket();
                case 65: return handleLCB();
                case 66: return leaveCurlyBracket();
                case 67: return handleRCB();
                case 68: return enterStringSequence();
                case 69: return leaveStringSequence();
                case 70: return createQuote();
                case 71: return enterCharacterClass();
                case 72: return handleLSB();
                case 73: return leaveCharacterClass();
                case 74: return handleRSB();
                case 75: return createAlnum();
                case 76: return createAlnumInv();
                case 77: return createAlpha();
                case 78: return createAlphaInv();
                case 79: return createAscii();
                case 80: return createAsciiInv();
                case 81: return createBlank();
                case 82: return createBlankInv();
                case 83: return createCntrl();
                case 84: return createCntrlInv();
                case 85: return createDigit();
                case 86: return createDigitInv();
                case 87: return createGraph();
                case 88: return createGraphInv();
                case 89: return createLower();
                case 90: return createLowerInv();
                case 91: return createPrint();
                case 92: return createPrintInv();
                case 93: return createPunct();
                case 94: return createPunctInv();
                case 95: return createSpace();
                case 96: return createSpaceInv();
                case 97: return createUpper();
                case 98: return createUpperInv();
                case 99: return createWord();
                case 100: return createWordInv();
                case 101: return createXdigit();
                case 102: return createXdigitInv();
                case 103: return createOrOr();
                case 104: return createTildeTilde();
                case 105: return createMinusMinus();
                case 106: return createAndAnd();
                case 107: return handleName();
                case 108: return handleNumber();
                case 109: return createHat();
                case 110: return createOr();
                case 111: return createLRB();
                case 112: return createRRB();
                case 113: return createDot();
                case 114: return createQMark();
                case 115: return createStar();
                case 116: return createPlus();
                case 117: return createSlash();
                case 118: return createTilde();
                case 119: return createEMark();
                case 120: return createDollar();
                case 121: return createComma();
                case 122: return createMinus();
                case 123: return createWS();
                case 124: return createCH();
                }
            }
            
            // no match found, set match values and report as error
            matchStart = dot;
            matchEnd = dot;
            matchLookahead = dot;
            
            throw new IllegalStateException("invalid input");
        }
        
        // no match found, set match values and return to caller
        matchStart = dot;
        matchEnd = dot;
        matchLookahead = dot;
        
        return null;
    }
    
    //%%LEX-MAIN-END%%
}
