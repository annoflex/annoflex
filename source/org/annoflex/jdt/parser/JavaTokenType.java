/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.parser;

/**
 * @author Stefan Czaska
 */
public enum JavaTokenType {
    
    /* import and export */
    
    PACKAGE,
    IMPORT,
    
    /* types */
    
    VOID,
    BOOLEAN,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    CHAR,
    CLASS,
    INTERFACE,
    ENUM,
    
    /* modifiers */
    
    PUBLIC,
    PROTECTED,
    PRIVATE,
    STATIC,
    FINAL,
    ABSTRACT,
    NATIVE,
    STRICTFP,
    SYNCHRONIZED,
    TRANSIENT,
    VOLATILE,
    CONST,
    
    /* class extensions */
    
    EXTENDS,
    IMPLEMENTS,
    
    /* flow control */
    
    IF,
    ELSE,
    SWITCH,
    CASE,
    DEFAULT,
    FOR,
    WHILE,
    DO,
    BREAK,
    CONTINUE,
    RETURN,
    GOTO,
    
    /* instance management */
    
    NEW,
    THIS,
    SUPER,
    INSTANCEOF,
    NULL,
    
    /* exception handling */
    
    TRY,
    CATCH,
    FINALLY,
    THROW,
    THROWS,
    ASSERT,
    
    /* boolean literals */
    
    FALSE,
    TRUE,
    
    /* delimiters */
    
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    LEFT_BRACE,
    RIGHT_BRACE,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    DOT,
    COMMA,
    COLON,
    SEMICOLON,
    QUESTION_MARK,
    
    /* special characters */
    
    GREATER,
    LOWER,
    EQUAL,
    NOT,
    AND,
    OR,
    PLUS,
    MINUS,
    TIMES,
    DIVIDED,
    XOR,
    MODULO,
    TILDE,
    
    /* literals */
    
    STRING_LITERAL,
    CHARACTER_LITERAL,
    INTEGER_LITERAL,
    LONG_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,
    
    /* comments */
    
    END_OF_LINE_COMMENT,
    TRADITIONAL_COMMENT,
    DOCUMENTATION_COMMENT,
    
    /* identifier */
    
    IDENTIFIER,
    
    /* whitespace */
    
    WHITESPACE,
    
    /* unknown */
    
    UNKNOWN;
}
