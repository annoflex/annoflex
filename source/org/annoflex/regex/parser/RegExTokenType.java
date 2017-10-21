/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.parser;

/**
 * @author Stefan Czaska
 */
public enum RegExTokenType {
    
    /* single characters */
    
    LSB,    // [
    RSB,    // ]
    HAT,    // ^
    LCB,    // {
    RCB,    // }
    OR,     // |
    LRB,    // (
    RRB,    // )
    LT,     // <
    GT,     // >
    QUOTE,  // "
    DOT,    // .
    QMARK,  // ?
    STAR,   // *
    PLUS,   // +
    SLASH,  // /
    TILDE,  // ~
    EMARK,  // !
    DOLLAR, // $
    COMMA,  // ,
    MINUS,  // -
    
    /* whitespace and miscellaneous characters */
    
    WS, // \p{whitespace}
    CH, // [^]
    
    /* escape sequences */
    
    ESCAPED_CHAR,            // \...
    ESCAPED_CHAR_CLASS,      // \...
    ESCAPED_CHAR_SEQUENCE,   // \...
    
    /* special character class tokens */
    
    OR_OR,            // ||
    AND_AND,          // &&
    MINUS_MINUS,      // --
    TILDE_TILDE,      // ~~
    NAMED_CHAR_CLASS, // [:name:]
    
    /* angle and curly bracket context tokens */
    
    NAME,   // [a-zA-Z][a-zA-Z0-9_]*
    NUMBER, // [0-9]+
}
