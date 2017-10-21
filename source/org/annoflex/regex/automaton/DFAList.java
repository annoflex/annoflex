/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.ArrayList;

/**
 * @author Stefan Czaska
 */
public class DFAList<A> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final ArrayList<DFA<A>> list = new ArrayList<>();
    
    //==============
    // List Methods
    //==============
    
    /**
     * 
     */
    public int size() {
        return list.size();
    }
    
    /**
     * 
     */
    public DFA<A> get(int index) {
        return list.get(index);
    }
    
    /**
     * 
     */
    public int add(DFA<A> state) {
        int index = list.size();
        
        list.add(state);
        
        return index;
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public int getTotalStateCount() {
        int stateCount = 0;
        int size = list.size();
        
        for (int i=0;i<size;i++) {
            stateCount += list.get(i).getStateCount();
        }
        
        return stateCount;
    }
}
