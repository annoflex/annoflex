/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class ConditionExpression extends Expression {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Condition condition;
    
    /**
     * 
     */
    private final Expression expression;
    
    //==============
    // Cache Fields
    //==============
    
    /**
     * 
     */
    private final int typeSet;
    
    /**
     * 
     */
    private final int wordLength;
    
    /**
     * 
     */
    private ConditionExpression normalized;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ConditionExpression(Condition condition, Expression expression) {
        super(ExpressionType.CONDITION);
        
        if (condition == null) {
            throw new IllegalArgumentException("condition may not be null");
        }
        
        if (expression == null) {
            throw new IllegalArgumentException("expression may not be null");
        }
        
        if (!ExpressionType.CHAR_CLASS.isContainedIn(expression.getTypeSet())) {
            throw new IllegalArgumentException("char class expression must be "
                    + "part of the sub expression");
        }
        
        this.condition = condition;
        this.expression = expression;
        this.typeSet = ExpressionType.CONDITION.getTypeSetMask() |
                expression.getTypeSet();
        this.wordLength = expression.getWordLength();
    }
    
    //==============================
    // Condition Expression Methods
    //==============================
    
    /**
     * 
     */
    public final Condition getCondition() {
        return condition;
    }
    
    /**
     * 
     */
    public final Expression getExpression() {
        return expression;
    }
    
    //=========================
    // Expression Tree Methods
    //=========================
    
    /**
     * 
     */
    public final int getChildCount() {
        return 1;
    }
    
    /**
     * 
     */
    public final Expression getChild(int index) {
        return index == 0 ? expression : null;
    }
    
    /**
     * 
     */
    public final int getTypeSet() {
        return typeSet;
    }
    
    /**
     * 
     */
    public final int getWordLength() {
        return wordLength;
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public synchronized Expression normalize() {
        if (normalized == null) {
            normalized = new ConditionExpression(condition,expression.normalize());
            normalized.normalized = normalized;
        }
        
        return normalized;
    }
    
    /**
     * 
     */
    public synchronized Expression reverse() {
        throw new UnsupportedOperationException("condition expressions may " +
                "not be reversed");
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[condition=");
        buffer.append(condition);
        buffer.append(",expression=");
        buffer.append(expression.getType());
        buffer.append("]");
        
        return buffer.toString();
    }
}
