/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.ArrayDeque;
import java.util.HashMap;

import org.annoflex.util.integer.IdSet;

/**
 * @author Stefan Czaska
 */
public class NFA<A> {
    
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
    NFAState<A>[] states;
    
    /**
     * 
     */
    private int stateCount;
    
    /**
     * 
     */
    private NFAState<A> startState;
    
    //===============
    // Helper Fields
    //===============
    
    /**
     * 
     */
    private int visitingId;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public NFA(Alphabet alphabet) {
        this.alphabet = alphabet;
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
    public NFAState<A> getState(int index) {
        return states[index];
    }
    
    //=====================
    // Start State Methods
    //=====================
    
    /**
     * 
     */
    public void setStartState(NFAState<A> startState) {
        this.startState = startState;
    }
    
    /**
     * 
     */
    public NFAState<A> getStartState() {
        return startState;
    }
    
    //========================
    // State Creation Methods
    //========================
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public NFAState<A> createState() {
        NFAState<A> newState = new NFAState<>(this,stateCount);
        
        int newSize = stateCount + 1;
        
        if ((states == null) || (newSize > states.length)) {
            NFAState<A>[] newArray = new NFAState[(newSize*3)/2+1];
            
            if (stateCount > 0) {
                System.arraycopy(states,0,newArray,0,stateCount);
            }
            
            states = newArray;
        }
        
        states[stateCount] = newState;
        stateCount = newSize;
        
        return newState;
    }
    
    //=============
    // DFA Methods
    //=============
    
    /**
     * 
     */
    public DFA<A> toDFA(boolean provideNFAContext) {
        
        // initialize
        ArrayDeque<IdSet> queue = new ArrayDeque<>();
        HashMap<IdSet,DFAState<A>> stateSetToStateMap = new HashMap<>();
        HashMap<DFAState<A>,IdSet> stateToStateSetMap = new HashMap<>();
        
        // create DFA
        DFA<A> dfa = new DFA<>(alphabet,provideNFAContext ?
                new NFAContext<>(this,stateSetToStateMap,stateToStateSetMap) :
                    null);
        
        // create DFA start state
        IdSet epsilonClosure = new IdSet();
        
        prepareEpsilonClosure();
        determineEpsilonClosure(startState,epsilonClosure);
        
        DFAState<A> dfaStartState = dfa.createState();
        dfa.setStartState(dfaStartState);
        
        stateSetToStateMap.put(epsilonClosure,dfaStartState);
        stateToStateSetMap.put(dfaStartState,epsilonClosure);
        queue.add(epsilonClosure);
        
        epsilonClosure = null;
        
        // find all other DFA states
        int alphabetLength = alphabet.getLength();
        
        while (!queue.isEmpty()) {
            IdSet curNFAStateSet = queue.remove();
            DFAState<A> curDFAState = stateSetToStateMap.get(curNFAStateSet);
            
            // Note: This is the main loop of the conversion. The number of
            // iterations is extremely high. Thus try to avoid to add code at
            // the top level of the loop as this has a high impact on the
            // execution time.
            for (int i=0;i<alphabetLength;i++) {
                if (epsilonClosure == null) {
                    epsilonClosure = new IdSet();
                }
                
                else {
                    epsilonClosure.clear();
                }
                
                prepareEpsilonClosure();
                
                int curNFAStateSetIterator = curNFAStateSet.first();
                
                while (curNFAStateSetIterator != -1) {
                    IdSet stateSet = states[curNFAStateSetIterator].conditionalTransitions[i];
                    
                    if (stateSet != null) {
                        int stateSetIterator = stateSet.first();
                        
                        while (stateSetIterator != -1) {
                            determineEpsilonClosure(states[stateSetIterator],epsilonClosure);
                            
                            stateSetIterator = stateSet.next(stateSetIterator);
                        }
                    }
                    
                    curNFAStateSetIterator = curNFAStateSet.next(curNFAStateSetIterator);
                }
                
                if (epsilonClosure.hasContent()) {
                    DFAState<A> nextDFAState = stateSetToStateMap.get(epsilonClosure);
                    
                    if (nextDFAState == null) {
                        nextDFAState = dfa.createState();
                        
                        stateSetToStateMap.put(epsilonClosure,nextDFAState);
                        stateToStateSetMap.put(nextDFAState,epsilonClosure);
                        queue.add(epsilonClosure);
                        
                        epsilonClosure = null;
                    }
                    
                    curDFAState.putConditionalTransition(i,nextDFAState);
                }
            }
        }
        
        // update actions
        int dfaStateCount = dfa.getStateCount();
        
        for (int i=0;i<dfaStateCount;i++) {
            DFAState<A> curDFAState = dfa.getState(i);
            IdSet curNFAStateSet = stateToStateSetMap.get(curDFAState);
            curDFAState.setAction(determineAction(curNFAStateSet));
        }
        
        return dfa;
    }
    
    /**
     * 
     */
    private Action<A> determineAction(IdSet stateSet) {
        NFAState<A> state = null;
        int iterator = stateSet.first();
        
        while (iterator != -1) {
            NFAState<A> curState = states[iterator];
            Action<A> curAction = curState.getAction();
            
            if (curAction != null) {
                if (state == null) {
                    state = curState;
                }
                
                else if (curAction.getId() < state.getAction().getId()) {
                    state = curState;
                }
            }
            
            iterator = stateSet.next(iterator);
        }
        
        return state != null ? state.getAction() : null;
    }
    
    //=====================
    // Integration Methods
    //=====================
    
    /**
     * 
     */
    public void integrateDFA(DFA<A> dfa, IdSet livingStates, IdSet dfaEndStates,
            NFAState<A> destStartState, NFAState<A> destEndState) {
        
        // initialize
        int alphabetLength = alphabet.getLength();
        int dfaStateCount = dfa.getStateCount();
        HashMap<DFAState<A>,NFAState<A>> stateMap = new HashMap<>();
        
        // first create a copy of all living states
        for (int i=0;i<dfaStateCount;i++) {
            DFAState<A> curDFAState = dfa.getState(i);
            
            if (livingStates.contains(curDFAState.id)) {
                stateMap.put(curDFAState,createState());
            }
        }
        
        // then update all conditional transitions
        for (int i=0;i<dfaStateCount;i++) {
            DFAState<A> dfaState = dfa.getState(i);
            NFAState<A> nfaState = stateMap.get(dfaState);
            
            if (nfaState != null) {
                for (int j=0;j<alphabetLength;j++) {
                    DFAState<A> destDFAState = dfaState.conditionalTransitions[j];
                    
                    if (destDFAState != null) {
                        nfaState.putConditionalTransition(j,stateMap.get(destDFAState));
                    }
                }
            }
        }
        
        // connect start state
        destStartState.addEpsilonTransition(stateMap.get(dfa.getStartState()));
        
        // connect end states
        int iterator = dfaEndStates.first();
        
        while (iterator != -1) {
            NFAState<A> curNFAEndState = stateMap.get(dfa.getState(iterator));
            
            if (curNFAEndState != null) {
                curNFAEndState.addEpsilonTransition(destEndState);
            }
            
            iterator = dfaEndStates.next(iterator);
        }
    }
    
    //=========================
    // Epsilon Closure Methods
    //=========================
    
    /**
     * 
     */
    public boolean isEpsilonReachable(NFAState<A> startState,
            NFAState<A> endState) {
        
        if (startState == endState) {
            return true;
        }
        
        prepareEpsilonClosure();
        
        return isEpsilonReachable(startState,endState.id);
    }
    
    /**
     * 
     */
    private boolean isEpsilonReachable(NFAState<A> state, int endStateId) {
        if (state.visitingId != visitingId) {
            if (state.id == endStateId) {
                return true;
            }
            
            state.visitingId = visitingId;
            
            IdSet epsilonTransitions = state.epsilonTransitions;
            
            if (epsilonTransitions != null) {
                int iterator = epsilonTransitions.first();
                
                while (iterator != -1) {
                    if (isEpsilonReachable(states[iterator],endStateId)) {
                        return true;
                    }
                    
                    iterator = epsilonTransitions.next(iterator);
                }
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    private void prepareEpsilonClosure() {
        if (++visitingId == 0) {
            
            // Note: This should almost never happen as it only happens at every
            // 2^32nd epsilon closure determination.
            for (int i=0;i<stateCount;i++) {
                states[i].visitingId = 0;
            }
            
            visitingId = 1;
        }
    }
    
    /**
     * 
     */
    private void determineEpsilonClosure(NFAState<A> state, IdSet epsilonClosure) {
        if (state.visitingId != visitingId) {
            state.visitingId = visitingId;
            
            epsilonClosure.add(state.id);
            
            IdSet epsilonTransitions = state.epsilonTransitions;
            
            if (epsilonTransitions != null) {
                int iterator = epsilonTransitions.first();
                
                while (iterator != -1) {
                    determineEpsilonClosure(states[iterator],epsilonClosure);
                    
                    iterator = epsilonTransitions.next(iterator);
                }
            }
        }
    }
}
