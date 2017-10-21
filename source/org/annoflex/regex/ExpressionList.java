/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Stefan Czaska
 */
public abstract class ExpressionList extends Expression {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Expression[] expressionList;
    
    /**
     * 
     */
    private final int typeSet;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    ExpressionList(ExpressionType type, Expression... expressionList) {
        super(type);
        
        ValidationResult result = validateArray(expressionList,true);
        
        this.expressionList = result.array;
        this.typeSet = result.typeSet | type.getTypeSetMask();
    }
    
    /**
     * 
     */
    ExpressionList(ExpressionType type,
            Collection<? extends Expression> expressionList) {
        
        super(type);
        
        ValidationResult result = validateArray(toArray(expressionList),false);
        
        this.expressionList = result.array;
        this.typeSet = result.typeSet | type.getTypeSetMask();
    }
    
    //=========================
    // Expression Tree Methods
    //=========================
    
    /**
     * 
     */
    public final int getChildCount() {
        return expressionList.length;
    }
    
    /**
     * 
     */
    public final Expression getChild(int index) {
        return expressionList[index];
    }
    
    /**
     * 
     */
    public final int getTypeSet() {
        return typeSet;
    }
    
    //====================
    // Validation Methods
    //====================
    
    /**
     * 
     */
    private ValidationResult validateArray(Expression[] array, boolean clone) {
        if (array == null) {
            throw new IllegalArgumentException("expression list may not be null");
        }
        
        if (array.length == 0) {
            throw new IllegalArgumentException("expression list may not be empty");
        }
        
        if (clone) {
            array = array.clone();
        }
        
        int typeSet = 0;
        
        // validate the array
        for (int i=0;i<array.length;i++) {
            Expression expression = array[i];
            
            if (expression == null) {
                throw new IllegalArgumentException("expression list may not " +
                        "contain null entries");
            }
            
            int expressionTypeSet = expression.getTypeSet();
            
            if (ExpressionType.LOOKAHEAD.isContainedIn(expressionTypeSet) &&
                !isAlternation()) {
                
                throw new IllegalArgumentException("lookahead expressions may "
                        + "only appear inside expression lists which are alternations");
            }
            
            if (ExpressionType.CONDITION.isContainedIn(expressionTypeSet) &&
                !isAlternation()) {
                
                throw new IllegalArgumentException("condition expressions may "
                        + "only appear inside expression lists which are alternations");
            }
            
            if (!ExpressionType.CHAR_CLASS.isContainedIn(expressionTypeSet)) {
                throw new IllegalArgumentException("char class expression must be "
                        + "part of the sub expression");
            }
            
            typeSet |= expressionTypeSet;
        }
        
        return new ValidationResult(array,typeSet);
    }
    
    /**
     * 
     */
    private Expression[] toArray(Collection<? extends Expression> expressionList) {
        
        // NOTE: This method exists in order to ensure that the array is
        // actually immutable. toArray of the collection could return a
        // non-constant array.
        
        if (expressionList != null) {
            int size = expressionList.size();
            Expression[] result = new Expression[size];
            
            if (size > 0) {
                Iterator<? extends Expression> iterator  = expressionList.iterator();
                int i = 0;
                
                while (iterator.hasNext()) {
                    result[i++] = iterator.next();
                }
            }
            
            return result;
        }
        
        return null;
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
        buffer.append("[childCount=");
        
        int childCount = getChildCount();
        
        buffer.append(childCount);
        
        buffer.append(",children=[");
        
        for (int i=0;i<childCount;i++) {
            buffer.append(getChild(i).getType());
            
            if (i != (childCount - 1)) {
                buffer.append(",");
            }
        }
        
        buffer.append("]]");
        
        return buffer.toString();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class ValidationResult {
        
        /**
         * 
         */
        final Expression[] array;
        
        /**
         * 
         */
        final int typeSet;
        
        /**
         * 
         */
        ValidationResult(Expression[] array, int typeSet) {
            this.array = array;
            this.typeSet = typeSet;
        }
    }
}
