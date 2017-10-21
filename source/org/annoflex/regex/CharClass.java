/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

import org.annoflex.util.integer.ConstIntRangeSet;
import org.annoflex.util.integer.MutableIntRangeSet;

/**
 * @author Stefan Czaska
 */
public class CharClass extends Expression {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    public final ConstIntRangeSet charSet;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public CharClass(int character) {
        this(new ConstIntRangeSet(character));
    }
    
    /**
     * 
     */
    public CharClass(int startCharacter, int endCharacter) {
        this(new ConstIntRangeSet(startCharacter,endCharacter));
    }
    
    /**
     * 
     */
    public CharClass(int[] characters) {
        this(characters,false);
    }
    
    /**
     * 
     */
    public CharClass(int[] characters, boolean invert) {
        this(createConstIntRangeSet(characters,invert));
    }
    
    /**
     * 
     */
    public CharClass(int[][] spanList) {
        this(createConstIntRangeSet(spanList));
    }
    
    /**
     * 
     */
    public CharClass(ConstIntRangeSet charSet) {
        super(ExpressionType.CHAR_CLASS);
        
        if (charSet == null) {
            throw new IllegalArgumentException("char set may not be null");
        }
        
        if (charSet.isEmpty()) {
            throw new IllegalArgumentException("char set may not be empty");
        }
        
        if (charSet.getStart(0) < Character.MIN_VALUE) {
            throw new IllegalArgumentException("char set may not " +
                    "contain negative values");
        }
        
        if (charSet.getEnd(charSet.size()-1) > Character.MAX_VALUE) {
            throw new IllegalArgumentException("char set may not " +
                    "contain values greater than "+Character.MAX_VALUE);
        }
        
        this.charSet = charSet;
    }
    
    //===============================
    // Symbol Set Expression Methods
    //===============================
    
    /**
     * 
     */
    public final ConstIntRangeSet getCharSet() {
        return charSet;
    }
    
    //=========================
    // Expression Tree Methods
    //=========================
    
    /**
     * 
     */
    public final int getChildCount() {
        return 0;
    }
    
    /**
     * 
     */
    public final Expression getChild(int index) {
        return null;
    }
    
    /**
     * 
     */
    public final int getTypeSet() {
        return ExpressionType.CHAR_CLASS.getTypeSetMask();
    }
    
    /**
     * 
     */
    public final int getWordLength() {
        return 1;
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public Expression normalize() {
        return this;
    }
    
    /**
     * 
     */
    public Expression reverse() {
        return this;
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
        buffer.append("[");
        buffer.append(charSet);
        buffer.append("]");
        
        return buffer.toString();
    }
    
    //==========================
    // ConstIntRangeSet Methods
    //==========================
    
    /**
     * 
     */
    private static ConstIntRangeSet createConstIntRangeSet(int[] symbols,
            boolean invert) {
        
        MutableIntRangeSet set = new MutableIntRangeSet();
        
        if (invert) {
            set.add(Character.MIN_VALUE,Character.MAX_VALUE);
            set.remove(symbols);
        }
        
        else {
            set.add(symbols);
        }
        
        return set.toConstSet();
    }
    
    /**
     * 
     */
    private static ConstIntRangeSet createConstIntRangeSet(int[][] spanList) {
        MutableIntRangeSet set = new MutableIntRangeSet();
        set.add(spanList);
        
        return set.toConstSet();
    }
}
