/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import org.annoflex.util.integer.IdSet;

/**
 * @author Stefan Czaska
 */
public class ComplementResult {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final IdSet livingStates;
    
    /**
     * 
     */
    private final IdSet endStates;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ComplementResult(IdSet livingStates, IdSet endStates) {
        this.livingStates = livingStates;
        this.endStates = endStates;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final IdSet getLivingStates() {
        return livingStates;
    }
    
    /**
     * 
     */
    public final IdSet getEndStates() {
        return endStates;
    }
}
