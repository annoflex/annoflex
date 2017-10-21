/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public enum JOMNodeType {
    
    COMPILATION_UNIT,
    PACKAGE,
    IMPORT,
    
    TYPE_DECLARATION,
    TYPE_DECLARATION_BODY,
    METHOD_DECLARATION,
    
    COMMENT_LIST,
    COMMENT,
    TAG,
    
    MODIFIER_LIST,
    MODIFIER,
    TYPE,
    NAME,
    NAME_PART,
}
