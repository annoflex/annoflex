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
public class DFA<A> {
    
    //=================
    // Property Fields
    //=================
    
    /**
     * 
     */
    private final Alphabet alphabet;
    
    /**
     * 
     */
    private NFAContext<A> nfaContext;
    
    /**
     * 
     */
    private DFAState<A>[] states;
    
    /**
     * 
     */
    private int stateCount;
    
    /**
     * 
     */
    private DFAState<A> startState;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public DFA(Alphabet alphabet) {
        this.alphabet = alphabet;
        this.nfaContext = null;
    }
    
    /**
     * 
     */
    public DFA(Alphabet alphabet, NFAContext<A> context) {
        this.alphabet = alphabet;
        this.nfaContext = context;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public Alphabet getAlphabet() {
        return alphabet;
    }
    
    /**
     * 
     */
    public NFAContext<A> getNFAContext() {
        return nfaContext;
    }
    
    /**
     * 
     */
    public void releaseNFAContext() {
        nfaContext = null;
    }
    
    //===============
    // State Methods
    //===============
    
    /**
     * 
     */
    public int getStateCount() {
        return stateCount;
    }
    
    /**
     * 
     */
    public DFAState<A> getState(int index) {
        return states[index];
    }
    
    //=====================
    // Start State Methods
    //=====================
    
    /**
     * 
     */
    public void setStartState(DFAState<A> startState) {
        this.startState = startState;
    }
    
    /**
     * 
     */
    public DFAState<A> getStartState() {
        return startState;
    }
    
    //========================
    // State Creation Methods
    //========================
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public DFAState<A> createState() {
        DFAState<A> newState = new DFAState<>(this,stateCount);
        
        int newSize = stateCount + 1;
        
        if ((states == null) || (newSize > states.length)) {
            DFAState<A>[] newArray = new DFAState[(newSize*3)/2+1];
            
            if (stateCount > 0) {
                System.arraycopy(states,0,newArray,0,stateCount);
            }
            
            states = newArray;
        }
        
        states[stateCount] = newState;
        stateCount = newSize;
        
        return newState;
    }
    
    //====================
    // Complement Methods
    //====================
    
    /**
     * 
     */
    public ComplementResult complement(NFAState<A> nfaEndState) {
        if (nfaContext == null) {
            throw new IllegalStateException("DFA must have a NFA context");
        }
        
        int alphabetLength = alphabet.getLength();
        int oldStateCount = stateCount;
        HashMap<DFAState<A>,IdSet> stateToStateSetMap = nfaContext
                .getStateToStateSetMap();
        
        IdSet dfaEndStates = new IdSet();
        DFAState<A> dfaIdleState = null;
        
        for (int i=0;i<oldStateCount;i++) {
            DFAState<A> curState = states[i];
            
            // invert end-state property
            if (!stateToStateSetMap.get(curState).contains(nfaEndState.id)) {
                dfaEndStates.add(curState.id);
            }
            
            for (int j=0;j<alphabetLength;j++) {
                if (curState.conditionalTransitions[j] == null) {
                    
                    // create idle state
                    if (dfaIdleState == null) {
                        dfaIdleState = createState();
                        dfaIdleState.putConditionalTransitionAll(dfaIdleState);
                        dfaEndStates.add(dfaIdleState.id);
                    }
                    
                    // create transition to idle state
                    curState.putConditionalTransition(j,dfaIdleState);
                }
            }
        }
        
        IdSet livingStates = new IdSet();
        IdSet visitedStates = new IdSet();
        
        removeDeadStates(startState,livingStates,visitedStates,dfaEndStates);
        
        return new ComplementResult(livingStates,dfaEndStates);
    }
    
    /**
     * 
     */
    private void removeDeadStates(DFAState<A> state, IdSet livingStates,
            IdSet visitedStates, IdSet endStates) {
        
        if (visitedStates.add(state.id)) {
            if (endStates.contains(state.id)) {
                livingStates.add(state.id);
            }
            
            int alphabetLength = alphabet.getLength();
            
            for (int i=0;i<alphabetLength;i++) {
                DFAState<A> nextState = state.conditionalTransitions[i];
                
                if ((nextState != null) && (nextState != state)) {
                    removeDeadStates(nextState,livingStates,visitedStates,endStates);
                    
                    if (livingStates.contains(nextState.id)) {
                        livingStates.add(state.id);
                    }
                    
                    else {
                        state.removeConditionalTransition(i);
                    }
                }
            }
        }
    }
    
    //======================
    // Minimization Methods
    //======================
    
    /**
     * 
     */
    public DFA<A> toMinimumDFA() {
        return new DFAMinimizer<A>().minimize(this);
    }
}
