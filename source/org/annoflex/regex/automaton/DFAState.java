/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

/**
 * @author Stefan Czaska
 */
public class DFAState<A> {
    
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
    final DFAState<A>[] conditionalTransitions;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public DFAState(DFA<A> dfa, int id) {
        this.id = id;
        
        conditionalTransitions = new DFAState[dfa.getAlphabet().getLength()];
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
    public void putConditionalTransition(int symbol, DFAState<A> state) {
        conditionalTransitions[symbol] = state;
    }
    
    /**
     * 
     */
    public void putConditionalTransitionAll(DFAState<A> state) {
        for (int i=0;i<conditionalTransitions.length;i++) {
            conditionalTransitions[i] = state;
        }
    }
    
    /**
     * 
     */
    public DFAState<A> getConditionalTransition(int symbol) {
        return conditionalTransitions[symbol];
    }
    
    /**
     * 
     */
    public void removeConditionalTransition(int symbol) {
        conditionalTransitions[symbol] = null;
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
