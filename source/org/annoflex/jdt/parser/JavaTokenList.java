/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.parser;

import org.annoflex.util.token.TokenIterator;
import org.annoflex.util.token.TokenList;

/**
 * @author Stefan Czaska
 */
public class JavaTokenList extends TokenList<JavaToken> {
    
    /**
     * 
     */
    public JavaTokenList() {
    }
    
    /**
     * 
     */
    public JavaTokenList(TokenIterator<JavaToken> iterator) {
        addAll(iterator);
    }
}
