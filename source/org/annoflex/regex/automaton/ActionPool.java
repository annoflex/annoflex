/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Stefan Czaska
 */
public class ActionPool<A> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final HashMap<Action<A>,Action<A>> cache = new HashMap<>();
    
    /**
     * 
     */
    private final ArrayList<Action<A>> list = new ArrayList<>();
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public int size() {
        return list.size();
    }
    
    /**
     * 
     */
    public Action<A> get(int index) {
        return list.get(index);
    }
    
    /**
     * 
     */
    public Action<A> createAction(Rule<A> rule, int lookaheadType,
            int lookaheadValue) {
        
        Action<A> newAction = new Action<>(list.size(),rule,
                lookaheadType,lookaheadValue);
        
        Action<A> curAction = cache.get(newAction);
        
        if (curAction != null) {
            return curAction;
        }
        
        cache.put(newAction,newAction);
        list.add(newAction);
        
        return newAction;
    }
}
