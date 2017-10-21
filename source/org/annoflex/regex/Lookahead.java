/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class Lookahead extends Expression {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Expression content;
    
    /**
     * 
     */
    private final Expression condition;
    
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
    private Lookahead normalized;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Lookahead(Expression content, Expression condition) {
        super(ExpressionType.LOOKAHEAD);
        
        if (content == null) {
            throw new IllegalArgumentException("content expression may not be null");
        }
        
        if (condition == null) {
            throw new IllegalArgumentException("condition expression may not be null");
        }
        
        validateExpression(content);
        validateExpression(condition);
        
        this.content = content;
        this.condition = condition;
        this.typeSet = ExpressionType.LOOKAHEAD.getTypeSetMask() |
                content.getTypeSet() | condition.getTypeSet();
        this.wordLength = computeWordLength();
    }
    
    //==============================
    // Lookahead Expression Methods
    //==============================
    
    /**
     * 
     */
    public final Expression getContent() {
        return content;
    }
    
    /**
     * 
     */
    public final Expression getCondition() {
        return condition;
    }
    
    //=========================
    // Expression Tree Methods
    //=========================
    
    /**
     * 
     */
    public final int getChildCount() {
        return 2;
    }
    
    /**
     * 
     */
    public final Expression getChild(int index) {
        switch(index) {
        case 0: return content;
        case 1: return condition;
        }
        
        return null;
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
        int contentLength = content.getWordLength();
        
        if (contentLength > 0) {
            int conditionLength = condition.getWordLength();
            
            if (conditionLength > 0) {
                return contentLength + conditionLength;
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
            normalized = new Lookahead(content.normalize(),condition.normalize());
            normalized.normalized = normalized;
        }
        
        return normalized;
    }
    
    /**
     * 
     */
    public Expression reverse() {
        throw new UnsupportedOperationException("lookahead expressions may " +
                "not be reversed");
    }
    
    //====================
    // Validation Methods
    //====================
    
    /**
     * 
     */
    private void validateExpression(Expression expression) {
        int typeSet = expression.getTypeSet();
        
        if (ExpressionType.LOOKAHEAD.isContainedIn(typeSet)) {
            throw new IllegalArgumentException("lookahead expressions may not "
                    + "be nested");
        }
        
        if (ExpressionType.CONDITION.isContainedIn(typeSet)) {
            throw new IllegalArgumentException("condition expressions may " +
                    "not appear inside lookahead expressions");
        }
        
        if (!ExpressionType.CHAR_CLASS.isContainedIn(typeSet)) {
            throw new IllegalArgumentException("char class expression must be "
                    + "part of the sub expression");
        }
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
        buffer.append("[content=");
        buffer.append(content.getType());
        buffer.append(",condition=");
        buffer.append(condition.getType());
        buffer.append("]");
        
        return buffer.toString();
    }
}
