/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import org.annoflex.regex.Expression;

/**
 * @author Stefan Czaska
 */
public class Rule<A> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Expression expression;
    
    /**
     * 
     */
    private final A action;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Rule(Expression expression, A action) {
        if (expression == null) {
            throw new IllegalArgumentException("expression may not be null");
        }
        
        if (action == null) {
            throw new IllegalArgumentException("action may not be null");
        }
        
        this.expression = expression;
        this.action = action;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final Expression getExpression() {
        return expression;
    }
    
    /**
     * 
     */
    public final A getAction() {
        return action;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return super.hashCode() ^ expression.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[expression=");
        buffer.append(expression);
        buffer.append(",action=");
        buffer.append(action);
        buffer.append("]");
        
        return buffer.toString();
    }
}
