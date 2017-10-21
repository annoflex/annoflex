/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.parser;

import java.util.EnumSet;

import org.annoflex.util.token.TokenHandler;

/**
 * @author Stefan Czaska
 */
public class JavaTokenHandler implements TokenHandler<JavaToken> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final EnumSet<JavaTokenType> set;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JavaTokenHandler(JavaTokenType... elements) {
        set = EnumSet.noneOf(JavaTokenType.class);
        
        for (int i=0;i<elements.length;i++) {
            set.add(elements[i]);
        }
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public boolean handleToken(JavaToken token) {
        return set.contains(token.type());
    }
}
