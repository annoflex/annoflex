/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class QuantifierExpression extends Expression {
    
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
    private final Quantifier quantifier;
    
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
    private QuantifierExpression normalized;
    
    /**
     * 
     */
    private QuantifierExpression reversed;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public QuantifierExpression(Expression expression, int min, int max) {
        this(expression,Quantifier.create(min,max));
    }
    
    /**
     * 
     */
    public QuantifierExpression(Expression expression, Quantifier quantifier) {
        super(ExpressionType.QUANTIFIER);
        
        if (expression == null) {
            throw new IllegalArgumentException("expression may not be null");
        }
        
        int expressionTypeSet = expression.getTypeSet();
        
        if (ExpressionType.LOOKAHEAD.isContainedIn(expressionTypeSet)) {
            throw new IllegalArgumentException("lookahead expression may not "
                    + "appear inside quantifier expressions");
        }
        
        if (ExpressionType.CONDITION.isContainedIn(expressionTypeSet)) {
            throw new IllegalArgumentException("condition expression may not "
                    + "appear inside quantifier expressions");
        }
        
        if (!ExpressionType.CHAR_CLASS.isContainedIn(expressionTypeSet)) {
            throw new IllegalArgumentException("char class expression must be "
                    + "part of the sub expression");
        }
        
        if (quantifier == null) {
            throw new IllegalArgumentException("quantifier may not be null");
        }
        
        this.expression = expression;
        this.quantifier = quantifier;
        this.typeSet = ExpressionType.QUANTIFIER.getTypeSetMask() |
                expression.getTypeSet();
        this.wordLength = computeWordLength();
    }
    
    //===============================
    // Quantifier Expression Methods
    //===============================
    
    /**
     * 
     */
    public final Expression getExpression() {
        return expression;
    }
    
    /**
     * 
     */
    public final Quantifier getQuantifier() {
        return quantifier;
    }
    
    /**
     * 
     */
    public final int getMin() {
        return quantifier.min;
    }
    
    /**
     * 
     */
    public final int getMax() {
        return quantifier.max;
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
    
    /**
     * 
     */
    private int computeWordLength() {
        int min  = quantifier.min;
        
        if (quantifier.max == min) {
            int expressionLength = expression.getWordLength();
            
            if (expressionLength > 0) {
                return expressionLength * min;
            }
        }
        
        return -1;
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public synchronized Expression normalize() {
        if (normalized == null) {
            normalized = new QuantifierExpression(expression.normalize(),quantifier);
            normalized.normalized = normalized;
        }
        
        return normalized;
    }
    
    /**
     * 
     */
    public synchronized Expression reverse() {
        if (reversed == null) {
            reversed = new QuantifierExpression(expression.reverse(),quantifier);
            reversed.reversed = this;
        }
        
        return reversed;
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
        buffer.append("[quantifier=");
        buffer.append(quantifier);
        buffer.append(",expression=");
        buffer.append(expression.getType());
        buffer.append("]");
        
        return buffer.toString();
    }
}
