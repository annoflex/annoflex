/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.parser;

import org.annoflex.util.token.TokenIterator;
import org.annoflex.util.token.TokenList;

/**
 * @author Stefan Czaska
 */
public class RegExTokenList extends TokenList<RegExToken> {
    
    /**
     * 
     */
    public RegExTokenList() {
    }
    
    /**
     * 
     */
    public RegExTokenList(TokenIterator<RegExToken> iterator) {
        addAll(iterator);
    }
}
