/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import org.annoflex.util.integer.ConstIntArray;
import org.annoflex.util.integer.IdSet;

/**
 * @author Stefan Czaska
 */
public class NFAState<A> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    final int id;
    
    /**
     * 
     */
    private Action<A> action;
    
    //===================
    // Transition Fields
    //===================
    
    /**
     * 
     */
    final IdSet[] conditionalTransitions;
    
    /**
     * 
     */
    IdSet epsilonTransitions;
    
    //==================
    // Iteration Fields
    //==================
    
    /**
     * 
     */
    int visitingId;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public NFAState(NFA<A> nfa, int id) {
        this.id = id;
        
        conditionalTransitions = new IdSet[nfa.getAlphabet().getLength()];
    }
    
    //===============
    // State Methods
    //===============
    
    /**
     * 
     */
    public int getId() {
        return id;
    }
    
    /**
     * 
     */
    public void setAction(Action<A> action) {
        this.action = action;
    }
    
    /**
     * 
     */
    public Action<A> getAction() {
        return action;
    }
    
    //=================================
    // Conditional Transitions Methods
    //=================================
    
    /**
     * 
     */
    public void putConditionalTransition(int index, NFAState<A> state) {
        IdSet  stateSet = conditionalTransitions[index];
        
        if (stateSet == null) {
            stateSet = new IdSet();
            conditionalTransitions[index] = stateSet;
        }
        
        stateSet.add(state.id);
    }
    
    /**
     * 
     */
    public void putConditionalTransition(ConstIntArray indices, NFAState<A> state) {
        for (int i=0;i<indices.size();i++) {
            putConditionalTransition(indices.get(i),state);
        }
    }
    
    /**
     * 
     */
    public IdSet[] getConditionalTransitions() {
        return conditionalTransitions;
    }
    
    //============================
    // Epsilon Transition Methods
    //============================
    
    /**
     * 
     */
    public void addEpsilonTransition(NFAState<A> state) {
        if (epsilonTransitions == null) {
            epsilonTransitions = new IdSet();
        }
        
        epsilonTransitions.add(state.id);
    }
    
    /**
     * 
     */
    public IdSet getEpsilonTransitions() {
        return epsilonTransitions;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = super.hashCode();
        
        if ((id & 1) != 0) {
            hash ^= 0x000f000f;
        }
        
        if ((id & 2) != 0) {
            hash ^= 0x00f000f0;
        }
        
        if ((id & 4) != 0) {
            hash ^= 0x0f000f00;
        }
        
        if ((id & 8) != 0) {
            hash ^= 0xf000f000;
        }
        
        return hash + (id * 31);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[id=");
        buffer.append(id);
        buffer.append(",action=");
        buffer.append(action);
        buffer.append("]");
        
        return buffer.toString();
    }
}
