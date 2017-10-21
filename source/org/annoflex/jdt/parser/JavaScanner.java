/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.parser;

import org.annoflex.jdt.dom.CommentValue;
import org.annoflex.util.token.TokenIterator;

/**
 * This is a scanner for Java source code files.
 * 
 * @option functionality = all- setString+
 * getMatchLength+ getMatchText+ getMatchTextRange+ getMatchChar+
 * 
 * @option visibility = all- stringMethods+ scanMethods+
 * 
 * @macro LineTerminator = \r | \n | \r\n
 * @macro DecimalInteger = 0 | [1-9][0-9]* 
 * @macro HexInteger     = 0 [xX] [0-9a-fA-F]* 
 * @macro OctalInteger   = 0 [0-7]+
 * @macro BinaryInteger  = 0 [bB] [01]* 
 * @macro FloatNumber    = (([0-9]+\.[0-9]*) | (\.?[0-9]+)) ([eE][+-]?[0-9]+)?
 * @macro CommentClosure = !([^]* [*][/] [^]*) ([*][/])?
 * 
 * @author Stefan Czaska
 */
public class JavaScanner implements TokenIterator<JavaToken> {
    
    //==============
    // Constructors
    //==============
    
    /**
     * Constructs a {@link JavaScanner}.
     */
    public JavaScanner() {
    }
    
    /**
     * Constructs a {@link JavaScanner} and initializes it with a string to be
     * scanned.
     * 
     * @param javaFile The content of a Java source code file.
     */
    public JavaScanner(String javaFile) {
        setString(javaFile);
    }
    
    //========================
    // Token Creation Methods
    //========================
    
    /* import and export */
    
    /** @expr package */ JavaToken createPackage() { return createToken(JavaTokenType.PACKAGE); }
    /** @expr import  */ JavaToken createImport()  { return createToken(JavaTokenType.IMPORT); }
    
    /* types */
    
    /** @expr void      */ JavaToken createVoid()      { return createToken(JavaTokenType.VOID); }
    /** @expr boolean   */ JavaToken createBoolean()   { return createToken(JavaTokenType.BOOLEAN); }
    /** @expr byte      */ JavaToken createByte()      { return createToken(JavaTokenType.BYTE); }
    /** @expr short     */ JavaToken createShort()     { return createToken(JavaTokenType.SHORT); }
    /** @expr int       */ JavaToken createInt()       { return createToken(JavaTokenType.INT); }
    /** @expr long      */ JavaToken createLong()      { return createToken(JavaTokenType.LONG); }
    /** @expr float     */ JavaToken createFloat()     { return createToken(JavaTokenType.FLOAT); }
    /** @expr double    */ JavaToken createDouble()    { return createToken(JavaTokenType.DOUBLE); }
    /** @expr char      */ JavaToken createChar()      { return createToken(JavaTokenType.CHAR); }
    /** @expr class     */ JavaToken createClass()     { return createToken(JavaTokenType.CLASS); }
    /** @expr interface */ JavaToken createInterface() { return createToken(JavaTokenType.INTERFACE); }
    /** @expr enum      */ JavaToken createEnum()      { return createToken(JavaTokenType.ENUM); }
    
    /* modifiers */
    
    /** @expr public       */ JavaToken createPublic()       { return createToken(JavaTokenType.PUBLIC); }
    /** @expr protected    */ JavaToken createProtected()    { return createToken(JavaTokenType.PROTECTED); }
    /** @expr private      */ JavaToken createPrivate()      { return createToken(JavaTokenType.PRIVATE); }
    /** @expr static       */ JavaToken createStatic()       { return createToken(JavaTokenType.STATIC); }
    /** @expr final        */ JavaToken createFinal()        { return createToken(JavaTokenType.FINAL); }
    /** @expr abstract     */ JavaToken createAbstract()     { return createToken(JavaTokenType.ABSTRACT); }
    /** @expr native       */ JavaToken createNative()       { return createToken(JavaTokenType.NATIVE); }
    /** @expr strictfp     */ JavaToken createStrictfp()     { return createToken(JavaTokenType.STRICTFP); }
    /** @expr synchronized */ JavaToken createSynchronized() { return createToken(JavaTokenType.SYNCHRONIZED); }
    /** @expr transient    */ JavaToken createTransient()    { return createToken(JavaTokenType.TRANSIENT); }
    /** @expr volatile     */ JavaToken createVolatile()     { return createToken(JavaTokenType.VOLATILE); }
    /** @expr const        */ JavaToken createConst()        { return createToken(JavaTokenType.CONST); }
    
    /* class extensions */
    
    /** @expr extends    */ JavaToken createExtends()    { return createToken(JavaTokenType.EXTENDS); }
    /** @expr implements */ JavaToken createImplements() { return createToken(JavaTokenType.IMPLEMENTS); }
    
    /* flow control */
    
    /** @expr if       */ JavaToken createIf()       { return createToken(JavaTokenType.IF); }
    /** @expr else     */ JavaToken createElse()     { return createToken(JavaTokenType.ELSE); }
    /** @expr switch   */ JavaToken createSwitch()   { return createToken(JavaTokenType.SWITCH); }
    /** @expr case     */ JavaToken createCase()     { return createToken(JavaTokenType.CASE); }
    /** @expr default  */ JavaToken createDefault()  { return createToken(JavaTokenType.DEFAULT); }
    /** @expr for      */ JavaToken createFor()      { return createToken(JavaTokenType.FOR); }
    /** @expr while    */ JavaToken createWhile()    { return createToken(JavaTokenType.WHILE); }
    /** @expr do       */ JavaToken createDo()       { return createToken(JavaTokenType.DO); }
    /** @expr break    */ JavaToken createBreak()    { return createToken(JavaTokenType.BREAK); }
    /** @expr continue */ JavaToken createContinue() { return createToken(JavaTokenType.CONTINUE); }
    /** @expr return   */ JavaToken createReturn()   { return createToken(JavaTokenType.RETURN); }
    /** @expr goto     */ JavaToken createGoto()     { return createToken(JavaTokenType.GOTO); }
    
    /* instance management */
    
    /** @expr new        */ JavaToken createNew()        { return createToken(JavaTokenType.NEW); }
    /** @expr this       */ JavaToken createThis()       { return createToken(JavaTokenType.THIS); }
    /** @expr super      */ JavaToken createSuper()      { return createToken(JavaTokenType.SUPER); }
    /** @expr instanceof */ JavaToken createInstanceof() { return createToken(JavaTokenType.INSTANCEOF); }
    /** @expr null       */ JavaToken createNull()       { return createToken(JavaTokenType.NULL); }
    
    /* exception handling */
    
    /** @expr try     */ JavaToken createTry()     { return createToken(JavaTokenType.TRY); }
    /** @expr catch   */ JavaToken createCatch()   { return createToken(JavaTokenType.CATCH); }
    /** @expr finally */ JavaToken createFinally() { return createToken(JavaTokenType.FINALLY); }
    /** @expr throw   */ JavaToken createThrow()   { return createToken(JavaTokenType.THROW); }
    /** @expr throws  */ JavaToken createThrows()  { return createToken(JavaTokenType.THROWS); }
    /** @expr assert  */ JavaToken createAssert()  { return createToken(JavaTokenType.ASSERT); }
    
    /* boolean literals */
    
    /** @expr false */ JavaToken createFalse() { return createToken(JavaTokenType.FALSE); }
    /** @expr true  */ JavaToken createTrue()  { return createToken(JavaTokenType.TRUE); }
    
    /* delimiters */
    
    /** @expr \( */ JavaToken createLP()        { return createToken(JavaTokenType.LEFT_PARENTHESIS); }
    /** @expr \) */ JavaToken createRP()        { return createToken(JavaTokenType.RIGHT_PARENTHESIS); }
    /** @expr \{ */ JavaToken createLBrace()    { return createToken(JavaTokenType.LEFT_BRACE); }
    /** @expr \} */ JavaToken createRBrace()    { return createToken(JavaTokenType.RIGHT_BRACE); }
    /** @expr \[ */ JavaToken createLBracket()  { return createToken(JavaTokenType.LEFT_BRACKET); }
    /** @expr \] */ JavaToken createRBracket()  { return createToken(JavaTokenType.RIGHT_BRACKET); }
    /** @expr \. */ JavaToken createDot()       { return createToken(JavaTokenType.DOT); }
    /** @expr \, */ JavaToken createComma()     { return createToken(JavaTokenType.COMMA); }
    /** @expr \: */ JavaToken createColon()     { return createToken(JavaTokenType.COLON); }
    /** @expr \; */ JavaToken createSemicolon() { return createToken(JavaTokenType.SEMICOLON); }
    /** @expr \? */ JavaToken createQM()        { return createToken(JavaTokenType.QUESTION_MARK); }
    
    /* special characters */
    
    /** @expr \> */ JavaToken createGreater() { return createToken(JavaTokenType.GREATER); }
    /** @expr \< */ JavaToken createLower()   { return createToken(JavaTokenType.LOWER); }
    /** @expr \= */ JavaToken createEqual()   { return createToken(JavaTokenType.EQUAL); }
    /** @expr \! */ JavaToken createNot()     { return createToken(JavaTokenType.NOT); }
    /** @expr \& */ JavaToken createAnd()     { return createToken(JavaTokenType.AND); }
    /** @expr \| */ JavaToken createOr()      { return createToken(JavaTokenType.OR); }
    /** @expr \+ */ JavaToken createPlus()    { return createToken(JavaTokenType.PLUS); }
    /** @expr \- */ JavaToken createMinus()   { return createToken(JavaTokenType.MINUS); }
    /** @expr \* */ JavaToken createTimes()   { return createToken(JavaTokenType.TIMES); }
    /** @expr \/ */ JavaToken createDivided() { return createToken(JavaTokenType.DIVIDED); }
    /** @expr \^ */ JavaToken createXor()     { return createToken(JavaTokenType.XOR); }
    /** @expr \% */ JavaToken createModulo()  { return createToken(JavaTokenType.MODULO); }
    /** @expr \~ */ JavaToken createTilde()   { return createToken(JavaTokenType.TILDE); }
    
    /* string literal */
    
    /**
     * @macro StringContent = (\\\" | [^\r\n"])* 
     * @expr \" {StringContent} \"? | (\" {StringContent} / {LineTerminator})
     */
    JavaToken createStringLiteral() {
        int length = getMatchLength();
        String value = length > 1 ? getMatchText(1,getMatchChar(length-1) == '"' ?
                1 : 0) : null;
        
        return createToken(JavaTokenType.STRING_LITERAL,value);
    }
    
    /* character literal */
    
    /**
     * A character literal can either be empty, line terminated, unclosed or valid.
     * 
     * @expr ' [^\r\n']? '?
     */
    JavaToken createCharacterLiteral() {
        Character value = getMatchLength() == 3 ? getMatchChar(1) : null;
        
        return createToken(JavaTokenType.CHARACTER_LITERAL,value);
    }
    
    /* integer literals */
    
    /** @expr {DecimalInteger} */
    JavaToken createDecimalInteger() {
        return createToken(JavaTokenType.INTEGER_LITERAL,createIntegerValue(10));
    }
    
    /** @expr {HexInteger} */
    JavaToken createHexInteger() {
        return createToken(JavaTokenType.INTEGER_LITERAL,createIntegerValue(16));
    }
    
    /** @expr {OctalInteger} */
    JavaToken createOctalInteger() {
        return createToken(JavaTokenType.INTEGER_LITERAL,createIntegerValue(8));
    }
    
    /** @expr {BinaryInteger} */
    JavaToken createBinaryInteger() {
        return createToken(JavaTokenType.INTEGER_LITERAL,createIntegerValue(2));
    }
    
    /* long literals */
    
    /** @expr {DecimalInteger}[lL] */
    JavaToken createDecimalLong() {
        return createToken(JavaTokenType.LONG_LITERAL,createLongValue(10));
    }
    
    /** @expr {HexInteger}[lL] */
    JavaToken createHexLong() {
        return createToken(JavaTokenType.LONG_LITERAL,createLongValue(16));
    }
    
    /** @expr {OctalInteger}[lL] */
    JavaToken createOctalLong() {
        return createToken(JavaTokenType.LONG_LITERAL,createLongValue(8));
    }
    
    /** @expr {BinaryInteger}[lL] */
    JavaToken createBinaryLong() {
        return createToken(JavaTokenType.LONG_LITERAL,createLongValue(2));
    }
    
    /* float literal */
    
    /** @expr {FloatNumber}[fF] */
    JavaToken createFloatLiteral() {
        return createToken(JavaTokenType.FLOAT_LITERAL,createFloatValue());
    }
    
    /* double literal */
    
    /** @expr {FloatNumber}[dD]? */
    JavaToken createDoubleLiteral() {
        return createToken(JavaTokenType.DOUBLE_LITERAL,createDoubleValue());
    }
    
    /* comments */
    
    /** @expr [/][/] [^\r\n]* */
    JavaToken createEOLComment() {
        return createToken(JavaTokenType.END_OF_LINE_COMMENT,
                createCommentValue(2,0));
    }
    
    /** @expr [/][*] | [/][*][*][/] | [/][*] [^*] {CommentClosure} */
    JavaToken createTradComment() {
        int length = getMatchLength();
        int endOffset = 0;
        
        if ((getMatchChar(length-1) == '/') &&
            (getMatchChar(length-2) == '*')) {
            
            endOffset = 2;
        }
        
        return createToken(JavaTokenType.TRADITIONAL_COMMENT,
                createCommentValue(2,endOffset));
    }
    
    /** @expr [/][*][*] ([*]* | [*]+[/] | [*]+ [^/*] {CommentClosure} | [^/*] {CommentClosure}) */
    JavaToken createDocComment() {
        int length = getMatchLength();
        int endOffset = 0;
        
        if ((getMatchChar(length-1) == '/') &&
            (getMatchChar(length-2) == '*')) {
            
            endOffset = 2;
        }
        
        return createToken(JavaTokenType.DOCUMENTATION_COMMENT,
                createCommentValue(3,endOffset));
    }
    
    /* identifier */
    
    /** @expr \p{JavaIdentifierStart}\p{JavaIdentifierPart}* */
    JavaToken createIdentifier() {
        return createToken(JavaTokenType.IDENTIFIER,getMatchText());
    }
    
    /* whitespace */
    
    /** @expr \p{whitespace}+ */
    JavaToken createWhitespace() {
        return createToken(JavaTokenType.WHITESPACE);
    }
    
    /* unknown token */
    
    /** @expr [^] */
    JavaToken createUnknownToken() {
        return createToken(JavaTokenType.UNKNOWN);
    }
    
    //===============================
    // Token Creation Helper Methods
    //===============================
    
    /**
     * 
     */
    JavaToken createToken(JavaTokenType type) {
        return createToken(type,null);
    }
    
    /**
     * 
     */
    JavaToken createToken(JavaTokenType type, Object value) {
        return new JavaToken(type,matchStart,matchEnd,value);
    }
    
    /**
     * 
     */
    private Integer createIntegerValue(int radix) {
        try {
            return Integer.valueOf(getMatchText(),radix);
        }
        
        catch(NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 
     */
    private Long createLongValue(int radix) {
        try {
            return Long.valueOf(getMatchText(),radix);
        }
        
        catch(NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 
     */
    private Float createFloatValue() {
        try {
            return Float.valueOf(getMatchText());
        }
        
        catch(NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 
     */
    private Double createDoubleValue() {
        try {
            return Double.valueOf(getMatchText());
        }
        
        catch(NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 
     */
    private CommentValue createCommentValue(int startOffset, int endOffset) {
        return new CommentValue(matchStart+startOffset,matchEnd-endOffset);
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
     * Rules:           95                           *
     * Lookaheads:      1                            *
     * Alphabet length: 69                           *
     * NFA states:      565                          *
     * DFA states:      304                          *
     * Static size:     111 KB                       *
     * Instance size:   24 Bytes                     *
     *                                               *
     ************************************************/
    
    //=================
    // Table Constants
    //=================
    
    /**
     * Maps Unicode characters to DFA input symbols.
     */
    private static final byte[] CHARACTER_MAP = createCharacterMap(
    "\34\t\35\1\2\1\35\2\1\1\34\16\6\4\35\1\25\1\5\1\6\1\40\1\31\1" +
    "\26\1\4\1\7\1\b\1\0\1\36\1\16\1\37\1\r\1\3\1\103\1\104\1\76\6" +
    "\65\2\17\1\20\1\23\1\24\1\22\1\21\1\6\1\63\1\71\1\63\1\75\1" +
    "\73\1\74\1\40\5\64\1\40\13\62\1\40\2\13\1\33\1\f\1\30\1\40\1" +
    "\6\1\67\1\77\1\70\1\102\1\100\1\101\1\44\1\56\1\45\1\40\1\43\1" +
    "\72\1\46\1\53\1\'\1\"\1\40\1\50\1\55\1\51\1\57\1\52\1\61\1\66\1" +
    "\54\1\60\1\t\1\27\1\n\1\32\1\34\6\41\1\34\32\35\1\6\1\40\4\6\4" +
    "\40\1\6\2\34\1\6\7\40\1\6\4\40\1\6\5\40\27\6\1\40\37\6\1\40\u01ca" +
    "\6\4\40\f\6\16\40\5\6\7\40\1\6\1\40\1\6\21\34\160\40\5\6\1\40\2" +
    "\6\2\40\4\6\b\40\1\6\1\40\3\6\1\40\1\6\1\40\24\6\1\40\123\6\1" +
    "\40\213\6\1\34\5\6\2\40\236\6\t\40\46\6\2\40\1\6\7\40\'\6\7" +
    "\40\1\6\1\34\55\6\1\34\1\6\1\34\2\6\1\34\2\6\1\34\1\6\b\40\33" +
    "\6\5\40\3\6\r\34\5\6\6\40\1\6\4\34\13\6\5\40\53\34\37\6\4\40\2" +
    "\34\1\40\143\6\1\40\1\34\b\6\1\34\6\40\2\34\2\6\1\34\4\40\2" +
    "\34\n\40\3\6\2\40\1\6\17\34\1\40\1\34\1\40\36\34\33\6\2\40\131" +
    "\34\13\40\1\6\16\34\n\40\41\34\t\40\2\6\4\40\1\6\5\40\26\34\4" +
    "\40\1\34\t\40\1\34\3\40\1\34\5\6\22\40\31\34\3\6\104\40\1\6\1" +
    "\40\13\6\67\34\33\6\1\34\4\40\66\34\3\40\1\34\22\40\1\34\7\40\n" +
    "\34\2\6\2\34\n\6\1\40\7\6\1\40\7\6\1\34\3\6\1\40\b\6\2\40\2" +
    "\6\2\40\26\6\1\40\7\6\1\40\1\6\3\40\4\6\2\34\1\40\1\34\7\6\2" +
    "\34\2\6\2\34\3\40\1\6\b\34\1\6\4\40\2\6\1\40\3\34\2\6\2\34\n" +
    "\40\4\6\7\40\1\6\5\34\3\6\1\40\6\6\4\40\2\6\2\40\26\6\1\40\7" +
    "\6\1\40\2\6\1\40\2\6\1\40\2\6\2\34\1\6\1\34\5\6\4\34\2\6\2\34\3" +
    "\6\3\34\1\6\7\40\4\6\1\40\1\6\7\34\f\40\3\34\1\6\13\34\3\6\1" +
    "\40\t\6\1\40\3\6\1\40\26\6\1\40\7\6\1\40\2\6\1\40\5\6\2\34\1" +
    "\40\1\34\b\6\1\34\3\6\1\34\3\6\2\40\1\6\17\40\2\34\2\6\2\34\n" +
    "\6\1\40\1\6\17\34\3\6\1\40\b\6\2\40\2\6\2\40\26\6\1\40\7\6\1" +
    "\40\2\6\1\40\5\6\2\34\1\40\1\34\7\6\2\34\2\6\2\34\3\6\b\34\2" +
    "\6\4\40\2\6\1\40\3\34\2\6\2\34\n\6\1\40\1\6\20\34\1\40\1\6\1" +
    "\40\6\6\3\40\3\6\1\40\4\6\3\40\2\6\1\40\1\6\1\40\2\6\3\40\2" +
    "\6\3\40\3\6\3\40\f\6\4\34\5\6\3\34\3\6\1\34\4\6\2\40\1\6\6\34\1" +
    "\6\16\34\n\6\t\40\1\6\7\34\3\6\1\40\b\6\1\40\3\6\1\40\27\6\1" +
    "\40\n\6\1\40\5\6\3\40\1\34\7\6\1\34\3\6\1\34\4\6\7\34\2\6\1" +
    "\40\2\6\6\40\2\34\2\6\2\34\n\6\22\34\2\6\1\40\b\6\1\40\3\6\1" +
    "\40\27\6\1\40\n\6\1\40\5\6\2\34\1\40\1\34\7\6\1\34\3\6\1\34\4" +
    "\6\7\34\2\6\7\40\1\6\1\40\2\34\2\6\2\34\n\6\1\40\2\6\17\34\2" +
    "\6\1\40\b\6\1\40\3\6\1\40\51\6\2\40\1\34\7\6\1\34\3\6\1\34\4" +
    "\40\1\6\b\34\1\6\b\40\2\34\2\6\2\34\n\6\n\40\6\6\2\34\2\6\1" +
    "\40\22\6\3\40\30\6\1\40\t\6\1\40\1\6\2\40\7\6\3\34\1\6\4\34\6" +
    "\6\1\34\1\6\1\34\b\6\22\34\2\6\r\40\60\34\1\40\2\34\7\6\4\40\b" +
    "\34\b\6\1\34\n\6\'\40\2\6\1\40\1\6\2\40\2\6\1\40\1\6\2\40\1" +
    "\6\6\40\4\6\1\40\7\6\1\40\3\6\1\40\1\6\1\40\1\6\2\40\2\6\1\40\4" +
    "\34\1\40\2\34\6\6\1\34\2\40\1\6\2\40\5\6\1\40\1\6\1\34\6\6\2" +
    "\34\n\6\2\40\4\6\40\40\1\6\27\34\2\6\6\34\n\6\13\34\1\6\1\34\1" +
    "\6\1\34\1\6\4\34\2\40\b\6\1\40\44\6\4\34\24\6\1\34\2\40\5\34\13" +
    "\6\1\34\44\6\t\34\1\6\71\40\53\34\24\40\1\34\n\6\6\40\6\34\4" +
    "\40\4\34\3\40\1\34\3\40\2\34\7\40\3\34\4\40\r\34\f\40\1\34\17" +
    "\6\2\40\46\6\1\40\1\6\5\40\1\6\2\40\53\6\1\40\u014d\6\1\40\4" +
    "\6\2\40\7\6\1\40\1\6\1\40\4\6\2\40\51\6\1\40\4\6\2\40\41\6\1" +
    "\40\4\6\2\40\7\6\1\40\1\6\1\40\4\6\2\40\17\6\1\40\71\6\1\40\4" +
    "\6\2\40\103\6\2\34\3\6\40\40\20\6\20\40\125\6\f\40\u026c\6\2" +
    "\40\21\35\1\40\32\6\5\40\113\6\3\40\3\6\17\40\r\6\1\40\4\34\3" +
    "\6\13\40\22\34\3\6\13\40\22\34\2\6\f\40\r\6\1\40\3\6\1\34\2" +
    "\6\f\40\64\34\40\6\3\40\1\6\3\40\2\34\1\6\2\34\n\6\41\34\3\6\2" +
    "\34\n\6\6\40\130\6\b\40\51\34\1\40\1\6\5\40\106\6\n\40\35\6\3" +
    "\34\f\6\4\34\f\6\n\34\n\40\36\6\2\40\5\6\13\40\54\6\4\34\21" +
    "\40\7\34\2\6\6\34\n\6\46\40\27\34\5\6\4\40\65\34\n\6\1\34\35" +
    "\6\2\34\13\6\6\34\n\6\r\40\1\6\130\34\5\40\57\34\21\40\7\6\4" +
    "\34\n\6\21\34\t\6\f\34\3\40\36\34\r\40\2\34\n\40\54\34\16\6\f" +
    "\40\44\34\24\6\b\34\n\6\3\40\3\34\n\40\44\6\122\34\3\6\1\34\25" +
    "\40\4\34\1\40\4\34\3\40\2\6\t\40\300\34\'\6\25\34\4\40\u0116" +
    "\6\2\40\6\6\2\40\46\6\2\40\6\6\2\40\b\6\1\40\1\6\1\40\1\6\1" +
    "\40\1\6\1\40\37\6\2\40\65\6\1\40\7\6\1\40\1\6\3\40\3\6\1\40\7" +
    "\6\3\40\4\6\2\40\6\6\4\40\r\6\5\40\3\6\1\40\7\6\3\35\13\34\5" +
    "\6\30\35\2\34\5\35\1\6\17\40\2\6\23\40\1\6\n\35\1\34\5\6\5\34\6" +
    "\6\1\40\1\6\r\40\1\6\20\40\r\6\3\40\33\6\25\34\r\6\4\34\1\6\3" +
    "\34\f\6\21\40\1\6\4\40\1\6\2\40\n\6\1\40\1\6\3\40\5\6\6\40\1" +
    "\6\1\40\1\6\1\40\1\6\1\40\4\6\1\40\13\6\2\40\4\6\5\40\5\6\4" +
    "\40\1\6\21\40\51\6\u0a77\40\57\6\1\40\57\6\1\40\205\6\6\40\4" +
    "\34\3\40\2\6\f\40\46\6\1\40\1\6\5\40\1\6\2\40\70\6\7\40\1\6\17" +
    "\34\1\40\27\6\t\40\7\6\1\40\7\6\1\40\7\6\1\40\7\6\1\40\7\6\1" +
    "\40\7\6\1\40\7\6\1\40\7\6\1\34\40\6\57\40\1\6\u01d0\35\1\6\4" +
    "\40\3\6\31\40\t\34\6\6\1\40\5\6\2\40\5\6\4\40\126\6\2\34\2\6\2" +
    "\40\3\6\1\40\132\6\1\40\4\6\5\40\51\6\3\40\136\6\21\40\33\6\65" +
    "\40\20\6\u0200\40\u19b6\6\112\40\u51cd\6\63\40\u048d\6\103\40\56" +
    "\6\2\40\u010d\6\3\40\20\34\n\40\2\6\24\40\57\34\1\6\4\34\n\6\1" +
    "\40\31\6\7\34\1\40\120\34\2\6\45\40\t\6\2\40\147\6\2\40\4\6\1" +
    "\40\4\6\f\40\13\6\115\40\n\34\1\40\3\34\1\40\4\34\1\40\27\34\5" +
    "\6\20\40\1\6\7\40\64\6\f\34\2\40\62\34\21\6\13\34\n\6\6\34\22" +
    "\40\6\6\3\40\1\6\4\34\n\40\34\34\b\6\2\40\27\34\r\6\f\40\35" +
    "\6\3\34\4\40\57\34\16\6\16\40\1\34\n\6\46\40\51\34\16\6\t\40\3" +
    "\34\1\40\b\34\2\6\2\34\n\6\6\40\27\6\3\40\1\34\1\6\4\40\60\34\1" +
    "\40\1\34\3\40\2\34\2\40\5\34\2\40\1\34\1\40\1\6\30\40\3\6\2" +
    "\40\13\34\5\6\2\40\3\34\2\6\n\40\6\6\2\40\6\6\2\40\6\6\t\40\7" +
    "\6\1\40\7\6\221\40\43\34\b\6\1\34\2\6\2\34\n\6\6\40\u2ba4\6\f" +
    "\40\27\6\4\40\61\6\u2104\40\u016e\6\2\40\152\6\46\40\7\6\f\40\5" +
    "\6\5\40\1\34\1\40\n\6\1\40\r\6\1\40\5\6\1\40\1\6\1\40\2\6\1" +
    "\40\2\6\1\40\154\6\41\40\u016b\6\22\40\100\6\2\40\66\6\50\40\r" +
    "\6\3\34\20\6\20\34\7\6\f\40\2\6\30\40\3\6\31\40\1\6\6\40\5\6\1" +
    "\40\207\6\2\34\1\6\4\40\1\6\13\34\n\6\7\40\32\6\4\40\1\6\1\40\32" +
    "\6\13\40\131\6\3\40\6\6\2\40\6\6\2\40\6\6\2\40\3\6\3\40\2\6\3" +
    "\40\2\6\22\34\3\6\4");
    
    /**
     * The transition table of the DFA.
     */
    private static final short[][] TRANSITION_TABLE = createTransitionTable(
    "\0\65\152\1\0\b\152\1\0\4\152\2\0\34\140\1\0\3\140\45\0\34\140\1\0\3" +
    "\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45" +
    "\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\40\u010e\1\140\4\0\34\140\1" +
    "\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3" +
    "\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45" +
    "\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34" +
    "\140\1\0\3\140\45\0\34\140\1\0\3\140\32\201\1\140\n\0\34\140\1\0\3\140\45" +
    "\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34" +
    "\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1" +
    "\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3" +
    "\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45" +
    "\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\17\u0116\1" +
    "\140\25\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45" +
    "\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34" +
    "\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1" +
    "\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1\0\3" +
    "\140\r\63\1\140\27\0\34\140\1\0\3\140\45\0\34\140\1\0\3\140\45\0\34\140\1" +
    "\0\3\140\45\0\34\140\1\0\3\140\45\0\105\0\105\0\105\0\105\0\105\0\105" +
    "\0\65\u012f\1\0\b\u012f\1\0\4\u012f\2\0\105\0\105\0\105\0\105\0\105\0\105" +
    "\0\105\0\105\0\105\0\105\0\105\0\105\0\105\146\1\0\2\135\1\0\101\0\105" +
    "\0\105\0\105\0\105\0\105\0\105\0\105\0\r\u012f\1\0\46\127\1\123\1\0\4" +
    "\127\1\u0130\1\133\1\\\1\123\1\0\1\u0130\1\133\1\\\1\123\2\0\63\124\1" +
    "\130\1\124\1\0\1\124\3\130\1\124\n\0\r\u012f\1\0\46\131\1\154\1\0\4\131\1" +
    "\u0130\1\133\1\\\1\125\1\0\1\u0130\1\133\1\\\1\125\2\0\64\132\1\0\5\132\1" +
    "\0\b\126\2\0\105\0\105\0\105\0\105\0\105\0\105\135\1\0\2\135\102\0\105" +
    "\0\105\0\34\140\1\0\3\140\45\0\1\141\2\0\32\141\1\0\3\141\1\0\43\0\105" +
    "\112\1\141\2\113\1\155\1\160\1\142\1\67\1\70\1\71\1\72\1\73\1\74\1\75\1" +
    "\76\1\77\1\100\1\101\1\102\1\103\1\104\1\105\1\106\1\107\1\114\1\115\1" +
    "\116\1\142\2\141\1\110\1\111\1\140\1\141\1\375\1\140\1\232\1\221\1\140\2" +
    "\237\1\164\1\321\1\210\1\140\1\225\1\140\3\256\1\140\3\123\1\140\1\175\1" +
    "\205\1\140\1\u011c\1\140\3\123\1\251\1\274\1\170\1\252\1\153\1\123\1" +
    "\147\1\145\2\136\1\145\101\147\1\145\104\144\1\151\104\147\1\145\2\137\1" +
    "\145\101\150\1\151\2\136\1\151\101\150\1\151\104\0\65\152\1\0\6\133\1" +
    "\\\1\152\1\0\2\133\1\\\1\152\2\0\r\u012f\1\0\44\124\1\0\1\127\1\154\1" +
    "\124\1\0\2\126\1\127\1\u0130\1\133\1\\\1\125\1\126\1\u0130\1\133\1\\\1" +
    "\125\2\0\r\u012f\1\0\'\154\1\0\5\u0130\1\133\1\\\1\154\1\0\1\u0130\1" +
    "\133\1\\\1\154\2\156\1\0\2\156\1\122\1\156\100\0\4\122\1\0\100\0\2\121\1" +
    "\0\102\160\1\157\1\120\1\160\2\117\1\160\25\161\1\160\51\160\1\157\1" +
    "\120\1\160\30\161\1\160\51\0\34\140\1\0\3\140\40\66\1\140\4\0\34\140\1" +
    "\0\3\140\f\57\1\140\2\162\1\140\7\327\1\140\r\0\34\140\1\0\3\140\b\163\1" +
    "\140\5\200\1\140\26\0\34\140\1\0\3\140\40\65\1\140\4\0\34\140\1\0\3\140\r" +
    "\165\1\140\27\0\34\140\1\0\3\140\32\166\1\140\n\0\34\140\1\0\3\140\5" +
    "\364\1\140\1\257\1\140\17\167\1\140\2\u0119\1\140\n\0\34\140\1\0\3\140\t" +
    "\64\1\140\33\0\34\140\1\0\3\140\b\171\1\140\34\0\34\140\1\0\3\140\40" +
    "\172\1\140\4\0\34\140\1\0\3\140\r\173\1\140\27\0\34\140\1\0\3\140\r\174\1" +
    "\140\21\361\1\140\5\0\34\140\1\0\3\140\21\62\1\140\23\0\34\140\1\0\3" +
    "\140\7\176\1\140\35\0\34\140\1\0\3\140\5\226\1\140\2\177\1\140\34\0\34" +
    "\140\1\0\3\140\f\61\1\140\30\0\34\140\1\0\3\140\16\60\1\140\26\0\34\140\1" +
    "\0\3\140\30\202\1\140\f\0\34\140\1\0\3\140\t\203\1\140\3\265\1\140\27" +
    "\0\34\140\1\0\3\140\7\245\1\140\6\u0113\1\140\b\204\1\140\2\u0111\1\140\n" +
    "\0\34\140\1\0\3\140\32\56\1\140\n\0\34\140\1\0\3\140\32\206\1\140\n\0\34" +
    "\140\1\0\3\140\17\207\1\140\7\353\1\140\b\227\1\140\4\0\34\140\1\0\3" +
    "\140\41\55\1\140\3\0\34\140\1\0\3\140\7\211\1\140\35\0\34\140\1\0\3\140\40" +
    "\212\1\140\4\0\34\140\1\0\3\140\30\213\1\140\f\0\34\140\1\0\3\140\13" +
    "\214\1\140\31\0\34\140\1\0\3\140\27\215\1\140\r\0\34\140\1\0\3\140\t" +
    "\216\1\140\33\0\34\140\1\0\3\140\t\b\1\140\3\217\1\140\27\0\34\140\1" +
    "\0\3\140\6\304\1\140\4\220\1\140\25\36\1\140\3\0\34\140\1\0\3\140\b\54\1" +
    "\140\34\0\34\140\1\0\3\140\40\222\1\140\4\0\34\140\1\0\3\140\2\223\1" +
    "\140\"\0\34\140\1\0\3\140\t\347\1\140\2\341\1\140\1\u011f\1\224\1\140\1" +
    "\271\1\140\23\0\34\140\1\0\3\140\r\53\1\140\27\0\34\140\1\0\3\140\21" +
    "\52\1\140\23\0\34\140\1\0\3\140\7\51\1\140\35\0\34\140\1\0\3\140\t\230\1" +
    "\140\33\0\34\140\1\0\3\140\7\231\1\140\35\0\34\140\1\0\3\140\13\50\1" +
    "\140\31\0\34\140\1\0\3\140\b\233\1\140\34\0\34\140\1\0\3\140\17\234\1" +
    "\140\25\0\34\140\1\0\3\140\t\235\1\140\33\0\34\140\1\0\3\140\40\236\1" +
    "\140\4\0\34\140\1\0\3\140\40\'\1\140\4\0\34\140\1\0\3\140\17\240\1\140\25" +
    "\0\34\140\1\0\3\140\13\241\1\140\31\0\34\140\1\0\3\140\5\242\1\140\37" +
    "\0\34\140\1\0\3\140\t\243\1\140\3\312\1\140\27\0\34\140\1\0\3\140\13" +
    "\244\1\140\31\0\34\140\1\0\3\140\3\46\1\140\41\0\34\140\1\0\3\140\27" +
    "\246\1\140\r\0\34\140\1\0\3\140\40\247\1\140\4\0\34\140\1\0\3\140\7\u0126\1" +
    "\250\1\140\3\u0121\1\140\30\0\34\140\1\0\3\140\7\45\1\140\30\264\1\140\4" +
    "\0\34\140\1\0\3\140\40\44\1\140\4\0\34\140\1\0\3\140\32\253\1\140\n\0\34" +
    "\140\1\0\3\140\5\254\1\140\37\0\34\140\1\0\3\140\16\255\1\140\26\0\34" +
    "\140\1\0\3\140\b\43\1\140\34\0\34\140\1\0\3\140\t\"\1\140\33\0\34\140\1" +
    "\0\3\140\32\260\1\140\n\0\34\140\1\0\3\140\17\261\1\140\25\0\34\140\1" +
    "\0\3\140\27\262\1\140\r\0\34\140\1\0\3\140\41\263\1\140\3\0\34\140\1" +
    "\0\3\140\40\41\1\140\4\0\34\140\1\0\3\140\16\40\1\140\26\0\34\140\1\0\3" +
    "\140\30\266\1\140\f\0\34\140\1\0\3\140\t\267\1\140\33\0\34\140\1\0\3" +
    "\140\5\270\1\140\37\0\34\140\1\0\3\140\40\37\1\140\4\0\34\140\1\0\3\140\r" +
    "\272\1\140\27\0\34\140\1\0\3\140\13\u0109\1\140\n\311\1\140\3\273\1\140\n" +
    "\0\34\140\1\0\3\140\r\35\1\140\27\0\34\140\1\0\3\140\t\275\1\140\33\0\34" +
    "\140\1\0\3\140\13\276\1\140\31\0\34\140\1\0\3\140\40\277\1\140\4\0\34" +
    "\140\1\0\3\140\6\300\1\140\36\0\34\140\1\0\3\140\40\301\1\140\4\0\34" +
    "\140\1\0\3\140\7\u0129\1\140\22\302\1\140\n\0\34\140\1\0\3\140\2\303\1" +
    "\140\"\0\34\140\1\0\3\140\r\34\1\140\27\0\34\140\1\0\3\140\"\305\1\140\2" +
    "\0\34\140\1\0\3\140\13\306\1\140\31\0\34\140\1\0\3\140\40\307\1\140\4" +
    "\0\34\140\1\0\3\140\t\310\1\140\33\0\34\140\1\0\3\140\t\33\1\140\33\0\34" +
    "\140\1\0\3\140\40\32\1\140\4\0\34\140\1\0\3\140\32\313\1\140\n\0\34\140\1" +
    "\0\3\140\5\314\1\140\37\0\34\140\1\0\3\140\t\315\1\140\33\0\34\140\1" +
    "\0\3\140\27\316\1\140\r\0\34\140\1\0\3\140\5\u0127\1\140\24\317\1\140\n" +
    "\0\34\140\1\0\3\140\7\320\1\140\35\0\34\140\1\0\3\140\t\31\1\140\33\0\34" +
    "\140\1\0\3\140\13\322\1\140\31\0\34\140\1\0\3\140\40\323\1\140\4\0\34" +
    "\140\1\0\3\140\5\324\1\140\37\0\34\140\1\0\3\140\r\325\1\140\27\0\34" +
    "\140\1\0\3\140\13\326\1\140\31\0\34\140\1\0\3\140\"\30\1\140\2\0\34\140\1" +
    "\0\3\140\40\330\1\140\4\0\34\140\1\0\3\140\20\331\1\140\24\0\34\140\1" +
    "\0\3\140\5\332\1\140\37\0\34\140\1\0\3\140\13\333\1\140\31\0\34\140\1" +
    "\0\3\140\7\334\1\140\35\0\34\140\1\0\3\140\b\335\1\140\34\0\34\140\1" +
    "\0\3\140\16\336\1\140\26\0\34\140\1\0\3\140\30\337\1\140\f\0\34\140\1" +
    "\0\3\140\13\340\1\140\31\0\34\140\1\0\3\140\2\27\1\140\"\0\34\140\1\0\3" +
    "\140\41\342\1\140\3\0\34\140\1\0\3\140\t\343\1\140\33\0\34\140\1\0\3" +
    "\140\30\344\1\140\f\0\34\140\1\0\3\140\5\345\1\140\37\0\34\140\1\0\3" +
    "\140\b\346\1\140\16\367\1\140\r\0\34\140\1\0\3\140\40\26\1\140\4\0\34" +
    "\140\1\0\3\140\n\350\1\140\32\0\34\140\1\0\3\140\5\351\1\140\37\0\34" +
    "\140\1\0\3\140\t\352\1\140\33\0\34\140\1\0\3\140\t\25\1\140\33\0\34\140\1" +
    "\0\3\140\30\354\1\140\f\0\34\140\1\0\3\140\27\355\1\140\r\0\34\140\1" +
    "\0\3\140\b\356\1\140\34\0\34\140\1\0\3\140\t\357\1\140\33\0\34\140\1" +
    "\0\3\140\r\360\1\140\27\0\34\140\1\0\3\140\32\24\1\140\n\0\34\140\1\0\3" +
    "\140\27\362\1\140\r\0\34\140\1\0\3\140\13\363\1\140\31\0\34\140\1\0\3" +
    "\140\30\23\1\140\f\0\34\140\1\0\3\140\5\365\1\140\37\0\34\140\1\0\3\140\t" +
    "\366\1\140\33\0\34\140\1\0\3\140\40\22\1\140\4\0\34\140\1\0\3\140\t\370\1" +
    "\140\33\0\34\140\1\0\3\140\27\371\1\140\r\0\34\140\1\0\3\140\n\372\1" +
    "\140\32\0\34\140\1\0\3\140\5\373\1\140\1\u0103\1\140\35\0\34\140\1\0\3" +
    "\140\b\374\1\140\6\u0107\1\140\7\u012e\1\140\r\0\34\140\1\0\3\140\"\21\1" +
    "\140\2\0\34\140\1\0\3\140\40\376\1\140\4\0\34\140\1\0\3\140\t\377\1\140\33" +
    "\0\34\140\1\0\3\140\30\u0100\1\140\f\0\34\140\1\0\3\140\40\u0101\1\140\4" +
    "\0\34\140\1\0\3\140\t\u0102\1\140\33\0\34\140\1\0\3\140\30\20\1\140\f" +
    "\0\34\140\1\0\3\140\5\u0104\1\140\37\0\34\140\1\0\3\140\32\u0105\1\140\n" +
    "\0\34\140\1\0\3\140\37\u0106\1\140\5\0\34\140\1\0\3\140\6\17\1\140\36" +
    "\0\34\140\1\0\3\140\17\u0108\1\140\25\0\34\140\1\0\3\140\40\16\1\140\4" +
    "\0\34\140\1\0\3\140\30\u010a\1\140\f\0\34\140\1\0\3\140\27\u010b\1\140\r" +
    "\0\34\140\1\0\3\140\41\u010c\1\140\3\0\34\140\1\0\3\140\b\u010d\1\140\34" +
    "\0\34\140\1\0\3\140\r\r\1\140\27\0\34\140\1\0\3\140\r\u010f\1\140\27" +
    "\0\34\140\1\0\3\140\27\u0110\1\140\r\0\34\140\1\0\3\140\b\f\1\140\34" +
    "\0\34\140\1\0\3\140\27\u0112\1\140\r\0\34\140\1\0\3\140\40\13\1\140\4" +
    "\0\34\140\1\0\3\140\32\u0114\1\140\n\0\34\140\1\0\3\140\37\u0115\1\140\5" +
    "\0\34\140\1\0\3\140\t\n\1\140\33\0\34\140\1\0\3\140\27\u0117\1\140\r" +
    "\0\34\140\1\0\3\140\7\u0118\1\140\35\0\34\140\1\0\3\140\4\t\1\140\40" +
    "\0\34\140\1\0\3\140\13\u011a\1\140\31\0\34\140\1\0\3\140\7\u011b\1\140\35" +
    "\0\34\140\1\0\3\140\t\7\1\140\33\0\34\140\1\0\3\140\b\u011d\1\140\34" +
    "\0\34\140\1\0\3\140\7\u011e\1\140\35\0\34\140\1\0\3\140\40\6\1\140\4" +
    "\0\34\140\1\0\3\140\t\u0120\1\140\33\0\34\140\1\0\3\140\13\5\1\140\31" +
    "\0\34\140\1\0\3\140\27\u0122\1\140\r\0\34\140\1\0\3\140\40\u0123\1\140\4" +
    "\0\34\140\1\0\3\140\32\u0124\1\140\n\0\34\140\1\0\3\140\7\u0125\1\140\35" +
    "\0\34\140\1\0\3\140\"\4\1\140\2\0\34\140\1\0\3\140\t\3\1\140\33\0\34" +
    "\140\1\0\3\140\b\u0128\1\140\34\0\34\140\1\0\3\140\40\2\1\140\4\0\34" +
    "\140\1\0\3\140\4\u012a\1\140\40\0\34\140\1\0\3\140\27\u012b\1\140\r\0\34" +
    "\140\1\0\3\140\3\u012c\1\140\41\0\34\140\1\0\3\140\30\u012d\1\140\f\0\65" +
    "\u012f\1\0\5\u0130\1\133\1\\\1\u012f\1\0\1\u0130\1\133\1\\\1\u012f\2" +
    "\0\36\1\2\0\25\152\1\0\b\152\1\0\4\152\2");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\b\1\t\1\n\1\13\1\f\1" +
    "\r\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1" +
    "\30\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\"\1" +
    "\43\1\44\1\45\1\46\1\'\1\50\1\51\1\52\1\53\1\54\1\55\1" +
    "\56\1\57\1\60\1\61\1\62\1\63\1\64\1\65\1\66\1\67\1\70\1" +
    "\71\1\72\1\73\1\74\1\75\1\76\1\77\1\100\1\101\1\102\1" +
    "\103\1\104\1\105\1\106\1\107\1\110\1\111\1\112\1\113\1" +
    "\114\1\115\1\116\1\117\1\120\1\121\1\122\1\123\1\124\1" +
    "\125\1\126\1\127\1\130\1\131\1\132\1\133\1\\\1\135\1\136\1" +
    "\137\1\140\1\141\1\0\1\136\2\135\1\136\1\135\2\133\1\122\1" +
    "\133\1\121\2\117\1\116\2\137\275\133\1\0\1");
    
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
        byte[] characterMap = new byte[65536];
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
        short[][] transitionTable = new short[304][69];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            short curValue = (short)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 69) {
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
        byte[] actionMap = new byte[304];
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
    public void setString(String string) {
        this.string = string != null ? string : "";
        
        regionStart = 0;
        regionEnd = this.string.length();
        
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
    public JavaToken getNextToken() {
        if (dot < regionEnd) {
            
            // find longest match
            int curState = 98;
            int iterator = dot;
            int matchState = -1;
            int matchPosition = 0;
            
            do {
                curState = TRANSITION_TABLE[curState][CHARACTER_MAP[string
                        .charAt(iterator)]];
                
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
                dot = endPosition;
                
                switch(ACTION_MAP[matchState]) {
                case 0: return createPackage();
                case 1: return createImport();
                case 2: return createVoid();
                case 3: return createBoolean();
                case 4: return createByte();
                case 5: return createShort();
                case 6: return createInt();
                case 7: return createLong();
                case 8: return createFloat();
                case 9: return createDouble();
                case 10: return createChar();
                case 11: return createClass();
                case 12: return createInterface();
                case 13: return createEnum();
                case 14: return createPublic();
                case 15: return createProtected();
                case 16: return createPrivate();
                case 17: return createStatic();
                case 18: return createFinal();
                case 19: return createAbstract();
                case 20: return createNative();
                case 21: return createStrictfp();
                case 22: return createSynchronized();
                case 23: return createTransient();
                case 24: return createVolatile();
                case 25: return createConst();
                case 26: return createExtends();
                case 27: return createImplements();
                case 28: return createIf();
                case 29: return createElse();
                case 30: return createSwitch();
                case 31: return createCase();
                case 32: return createDefault();
                case 33: return createFor();
                case 34: return createWhile();
                case 35: return createDo();
                case 36: return createBreak();
                case 37: return createContinue();
                case 38: return createReturn();
                case 39: return createGoto();
                case 40: return createNew();
                case 41: return createThis();
                case 42: return createSuper();
                case 43: return createInstanceof();
                case 44: return createNull();
                case 45: return createTry();
                case 46: return createCatch();
                case 47: return createFinally();
                case 48: return createThrow();
                case 49: return createThrows();
                case 50: return createAssert();
                case 51: return createFalse();
                case 52: return createTrue();
                case 53: return createLP();
                case 54: return createRP();
                case 55: return createLBrace();
                case 56: return createRBrace();
                case 57: return createLBracket();
                case 58: return createRBracket();
                case 59: return createDot();
                case 60: return createComma();
                case 61: return createColon();
                case 62: return createSemicolon();
                case 63: return createQM();
                case 64: return createGreater();
                case 65: return createLower();
                case 66: return createEqual();
                case 67: return createNot();
                case 68: return createAnd();
                case 69: return createOr();
                case 70: return createPlus();
                case 71: return createMinus();
                case 72: return createTimes();
                case 73: return createDivided();
                case 74: return createXor();
                case 75: return createModulo();
                case 76: return createTilde();
                case 77: return createStringLiteral();
                case 78:
                    dot = matchPosition;
                    matchEnd = dot;
                    return createStringLiteral();
                
                case 79:
                    dot = matchPosition - 1;
                    matchEnd = dot;
                    return createStringLiteral();
                
                case 80: return createCharacterLiteral();
                case 81: return createDecimalInteger();
                case 82: return createHexInteger();
                case 83: return createOctalInteger();
                case 84: return createBinaryInteger();
                case 85: return createDecimalLong();
                case 86: return createHexLong();
                case 87: return createOctalLong();
                case 88: return createBinaryLong();
                case 89: return createFloatLiteral();
                case 90: return createDoubleLiteral();
                case 91: return createEOLComment();
                case 92: return createTradComment();
                case 93: return createDocComment();
                case 94: return createIdentifier();
                case 95: return createWhitespace();
                case 96: return createUnknownToken();
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
    
    //%%LEX-MAIN-END%%
}
