/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.HashMap;

import org.annoflex.util.integer.IdSet;

/**
 * @author Stefan Czaska
 */
public class NFAContext<A> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final NFA<A> nfa;
    
    /**
     * 
     */
    private final HashMap<IdSet,DFAState<A>> stateSetToStateMap;
    
    /**
     * 
     */
    private final HashMap<DFAState<A>,IdSet> stateToStateSetMap;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public NFAContext(NFA<A> nfa,
            HashMap<IdSet,DFAState<A>> stateSetToStateMap,
            HashMap<DFAState<A>,IdSet> stateToStateSetMap) {
        
        this.nfa = nfa;
        this.stateSetToStateMap = stateSetToStateMap;
        this.stateToStateSetMap = stateToStateSetMap;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public NFA<A> getNFA() {
        return nfa;
    }
    
    /**
     * 
     */
    public HashMap<IdSet,DFAState<A>> getStateSetToStateMap() {
        return stateSetToStateMap;
    }
    
    /**
     * 
     */
    public HashMap<DFAState<A>,IdSet> getStateToStateSetMap() {
        return stateToStateSetMap;
    }
}
