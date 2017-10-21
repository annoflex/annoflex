/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Stefan Czaska
 */
public class Alternation extends ExpressionList {
    
    //==============
    // Cache Fields
    //==============
    
    /**
     * 
     */
    private final int wordLength;
    
    /**
     * 
     */
    private Alternation normalized;
    
    /**
     * 
     */
    private Alternation reversed;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Alternation(Expression... expressionList) {
        super(ExpressionType.ALTERNATION,expressionList);
        
        this.wordLength = computeWordLength();
    }
    
    /**
     * 
     */
    public Alternation(Collection<? extends Expression> expressionList) {
        super(ExpressionType.ALTERNATION,expressionList);
        
        this.wordLength = computeWordLength();
    }
    
    //=========================
    // Expression Tree Methods
    //=========================
    
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
        int refLength = getChild(0).getWordLength();
        
        if (refLength > 0) {
            int childCount = getChildCount();
            
            for (int i=1;i<childCount;i++) {
                if (getChild(i).getWordLength() != refLength) {
                    return -1;
                }
            }
            
            return refLength;
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
            ArrayList<Expression> expressionList = new ArrayList<>();
            
            int childCount = getChildCount();
            
            for (int i=0;i<childCount;i++) {
                expressionList.add(getChild(i).normalize());
            }
            
            normalized = new Alternation(expressionList);
            normalized.normalized = normalized;
        }
        
        return normalized;
    }
    
    /**
     * 
     */
    public synchronized Expression reverse() {
        if (reversed == null) {
            ArrayList<Expression> expressionList = new ArrayList<>();
            
            int childCount = getChildCount();
            
            for (int i=0;i<childCount;i++) {
                expressionList.add(getChild(i).reverse());
            }
            
            reversed = new Alternation(expressionList);
            reversed.reversed = this;
        }
        
        return reversed;
    }
}
