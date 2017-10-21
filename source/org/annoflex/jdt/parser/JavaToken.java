/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.parser;

import org.annoflex.util.token.Token;

/**
 * @author Stefan Czaska
 */
public class JavaToken extends Token<JavaTokenType,Object> {
    
    /**
     * 
     */
    public JavaToken(JavaTokenType type, int start, int end, Object value) {
        super(type,start,end,value);
    }
}
