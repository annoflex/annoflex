/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

/**
 * @author Stefan Czaska
 */
public class Action<A> {
    
    //===========
    // Constants
    //===========
    
    public static final int LOOKAHEAD_NONE          = 0;
    public static final int LOOKAHEAD_FIX_CONDITION = 1;
    public static final int LOOKAHEAD_FIX_CONTENT   = 2;
    public static final int LOOKAHEAD_VARIABLE      = 3;
    public static final int LOOKAHEAD_FORWARD_PASS  = 4;
    public static final int LOOKAHEAD_BACKWARD_PASS = 5;
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int id;
    
    /**
     * 
     */
    private final Rule<A> ownerRule;
    
    /**
     * 
     */
    private final int lookaheadType;
    
    /**
     * 
     */
    private final int lookaheadValue;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    Action(int id, Rule<A> ownerRule, int lookaheadType, int lookaheadValue) {
        this.id = id;
        this.ownerRule = ownerRule;
        this.lookaheadType = lookaheadType;
        this.lookaheadValue = lookaheadValue;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final int getId() {
        return id;
    }
    
    /**
     * 
     */
    public final Rule<A> getOwnerRule() {
        return ownerRule;
    }
    
    /**
     * 
     */
    public final int getLookaheadType() {
        return lookaheadType;
    }
    
    /**
     * 
     */
    public final int getLookaheadValue() {
        return lookaheadValue;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof Action) {
            Action action = (Action)obj;
            
            return (action.ownerRule == ownerRule) &&
                   (action.lookaheadType == lookaheadType) &&
                   (action.lookaheadValue == lookaheadValue);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (ownerRule.hashCode() * 31 + lookaheadType) * 31 + lookaheadValue;
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
        buffer.append(ownerRule.getAction());
        buffer.append(",lookaheadType=");
        buffer.append(lookaheadType);
        buffer.append(",lookaheadValue=");
        buffer.append(lookaheadValue);
        buffer.append("]");
        
        return buffer.toString();
    }
}
