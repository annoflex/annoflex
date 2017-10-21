/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.token;

/**
 * @author Stefan Czaska
 */
public interface TokenIterator<T extends Token<?,?>> {
    
    /**
     * 
     */
    public T getNextToken();
}
