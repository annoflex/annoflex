/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public enum ROMNodeType {
    
    COMPILATION_UNIT,
    ROOT_ALTERNATION,
    ROOT_ELEMENT,
    
    LOOKAROUND_EXPR,
    LOOKBEFORE,
    LOOKAFTER,
    
    ALTERNATION_EXPR,
    CONCATENATION_EXPR,
    MODIFIER_EXPR,
    QUANTIFIER_EXPR,
    CHAR_EXPR,
    CLASS_EXPR,
    SEQUENCE_EXPR,
    MACRO_EXPR,
    
    CHARACTER_CLASS,
    CC_SEQUENCE,
    CC_OPERATOR,
    CC_RANGE,
    
    STRING_SEQUENCE,
    
    CONDITION,
    NAME_LIST,
    MACRO,
    
    CHAR_REF,
    CLASS_REF,
    SEQUENCE_REF,
    NAME,
    MODIFIER,
    QUANTIFIER,
}
