/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.parser;

import org.annoflex.util.token.Token;

/**
 * @author Stefan Czaska
 */
public class RegExToken extends Token<RegExTokenType,Object> {
    
    /**
     * 
     */
    public RegExToken(RegExTokenType type, int start, int end, Object value) {
        super(type,start,end,value);
    }
}
