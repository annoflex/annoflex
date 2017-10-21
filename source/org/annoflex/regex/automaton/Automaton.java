/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

/**
 * @author Stefan Czaska
 */
public class Automaton<A> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Alphabet alphabet;
    
    /**
     * 
     */
    private final NameMap nameMap;
    
    /**
     * 
     */
    private final ActionPool<A> actionPool;
    
    /**
     * 
     */
    private final int totalNFAStateCount;
    
    /**
     * 
     */
    private final DFAList<A> dfaList;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Automaton(Alphabet alphabet, NameMap nameMap,
            ActionPool<A> actionPool, int totalNFAStateCount,
            DFAList<A> dfaList) {
        
        this.alphabet = alphabet;
        this.nameMap = nameMap;
        this.actionPool = actionPool;
        this.totalNFAStateCount = totalNFAStateCount;
        this.dfaList = dfaList;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final Alphabet getAlphabet() {
        return alphabet;
    }
    
    /**
     * 
     */
    public final NameMap getNameMap() {
        return nameMap;
    }
    
    /**
     * 
     */
    public final ActionPool<A> getActionPool() {
        return actionPool;
    }
    
    /**
     * 
     */
    public final int getTotalNFAStateCount() {
        return totalNFAStateCount;
    }
    
    /**
     * 
     */
    public final DFAList<A> getDFAList() {
        return dfaList;
    }
}
