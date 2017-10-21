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
public class Concatenation extends ExpressionList {
    
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
    private Concatenation normalized;
    
    /**
     * 
     */
    private Concatenation reversed;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Concatenation(Expression... expressionList) {
        super(ExpressionType.CONCATENATION,expressionList);
        
        this.wordLength = computeWordLength();
    }
    
    /**
     * 
     */
    public Concatenation(Collection<? extends Expression> expressionList) {
        super(ExpressionType.CONCATENATION,expressionList);
        
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
        int length = 0;
        int childCount = getChildCount();
        
        for (int i=0;i<childCount;i++) {
            int childLength = getChild(i).getWordLength();
            
            if (childLength < 0) {
                return -1;
            }
            
            length += childLength;
        }
        
        return length;
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
            
            normalized = new Concatenation(expressionList);
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
            
            for (int i=childCount-1;i>=0;i--) {
                expressionList.add(getChild(i).reverse());
            }
            
            reversed = new Concatenation(expressionList);
            reversed.reversed = this;
        }
        
        return reversed;
    }
}
