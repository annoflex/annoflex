/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * @author Stefan Czaska
 */
public class ConstIntArray extends IntArray {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    public static final ConstIntArray EMPTY_ARRAY = new ConstIntArray();
    
    //==============
    // Cache Fields
    //==============
    
    /**
     * 
     */
    private int hash;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ConstIntArray() {
    }
    
    /**
     * 
     */
    public ConstIntArray(int[] array) {
        super(array);
    }
    
    /**
     * 
     */
    public ConstIntArray(int[] array, int start, int end) {
        super(array,start,end);
    }
    
    /**
     * 
     */
    public ConstIntArray(IntArray array) {
        super(array);
    }
    
    /**
     * 
     */
    public ConstIntArray(IdSet set) {
        super(set);
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public MutableIntArray toMutableArray() {
        return new MutableIntArray(this);
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if ((hash != 0) && (obj instanceof ConstIntArray)) {
            ConstIntArray array = (ConstIntArray)obj;
            
            if ((array.hash != 0) && (hash != array.hash)) {
                return false;
            }
        }
        
        return super.equals(obj);
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = this.hash;
        
        if (hash == 0) {
            hash = super.hashCode();
            
            this.hash = hash;
        }
        
        return hash;
    }
}
