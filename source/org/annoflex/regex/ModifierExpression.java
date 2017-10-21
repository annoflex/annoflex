/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class ModifierExpression extends Expression {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Modifier modifier;
    
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
    private Expression normalized;
    
    /**
     * 
     */
    private ModifierExpression reversed;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ModifierExpression(Modifier modifier, Expression expression) {
        super(ExpressionType.MODIFIER);
        
        if (modifier == null) {
            throw new IllegalArgumentException("modifier may not be null");
        }
        
        if (expression == null) {
            throw new IllegalArgumentException("expression may not be null");
        }
        
        int expressionTypeSet = expression.getTypeSet();
        
        if (ExpressionType.LOOKAHEAD.isContainedIn(expressionTypeSet)) {
            throw new IllegalArgumentException("lookahead expressions may not "
                    + "appear inside modifier expressions");
        }
        
        if (ExpressionType.CONDITION.isContainedIn(expressionTypeSet)) {
            throw new IllegalArgumentException("condition expressions may not "
                    + "appear inside modifier expressions");
        }
        
        if (!ExpressionType.CHAR_CLASS.isContainedIn(expressionTypeSet)) {
            throw new IllegalArgumentException("char class expression must be "
                    + "part of the sub expression");
        }
        
        this.modifier = modifier;
        this.expression = expression;
        this.typeSet = ExpressionType.MODIFIER.getTypeSetMask() |
                expression.getTypeSet();
        this.wordLength = computeWordLength();
    }
    
    //=============================
    // Modifier Expression Methods
    //=============================
    
    /**
     * 
     */
    public final Modifier getModifier() {
        return modifier;
    }
    
    /**
     * 
     */
    public final Expression getExpression() {
        return expression;
    }
    
    /**
     * 
     */
    public final boolean isNot() {
        return modifier == Modifier.NOT;
    }
    
    /**
     * 
     */
    public final boolean isUntil() {
        return modifier == Modifier.UNTIL;
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
        
        // Note: Due to "not" and "until" the resulting expression should in
        // nearly all cases be of variable length. If there are important cases
        // for which this is false then they should be detected here and return
        // the correct fix length.
        
        // detect important cases
        if ((modifier == Modifier.NOT) && expression.isModifier()) {
            
            // neutralize double negation
            ModifierExpression childModifierExpression =
                    (ModifierExpression)expression;
            
            if (childModifierExpression.isNot()) {
                return childModifierExpression.getExpression().getWordLength();
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
            switch(modifier) {
            case NOT:
                
                // neutralize double negation
                if (expression.isModifier()) {
                    ModifierExpression childModifierExpression =
                            (ModifierExpression)expression;
                    
                    if (childModifierExpression.isNot()) {
                        normalized = childModifierExpression.getExpression()
                                .normalize();
                        
                        return normalized;
                    }
                }
                
                ModifierExpression normalized = new ModifierExpression(
                        modifier,expression.normalize());
                normalized.normalized = normalized;
                
                this.normalized = normalized;
                
                return this.normalized;
            
            case UNTIL:
                this.normalized = resolveUntil().normalize();
                
                return this.normalized;
            
            default:
                throw new IllegalStateException("unknown modifier type");
            }
        }
        
        return normalized;
    }
    
    /**
     * 
     */
    public synchronized Expression reverse() {
        if (reversed == null) {
            reversed = new ModifierExpression(modifier,expression.reverse());
            reversed.reversed = this;
        }
        
        return reversed;
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * Transforms the expression into: <code>!([^]*{expr}[^]*){expr}</code>
     */
    private Expression resolveUntil() {
        return new Concatenation(
                new Not(new Concatenation(EVERYTHING,expression,EVERYTHING)),
                expression);
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
        buffer.append("[modifier=");
        buffer.append(modifier);
        buffer.append(",expression=");
        buffer.append(expression.getType());
        buffer.append("]");
        
        return buffer.toString();
    }
}
