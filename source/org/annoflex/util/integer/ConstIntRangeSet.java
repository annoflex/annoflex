/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * @author Stefan Czaska
 */
public class ConstIntRangeSet extends IntRangeSet {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    public static final ConstIntRangeSet EMPTY_SET = new ConstIntRangeSet();
    
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
    public ConstIntRangeSet() {
    }
    
    /**
     * 
     */
    public ConstIntRangeSet(int value) {
        super(value,value);
    }
    
    /**
     * 
     */
    public ConstIntRangeSet(int start, int end) {
        super(start,end);
    }
    
    /**
     * 
     */
    public ConstIntRangeSet(int[] values) {
        super(values);
    }
    
    /**
     * 
     */
    public ConstIntRangeSet(int[][] spanList) {
        super(spanList);
    }
    
    /**
     * 
     */
    public ConstIntRangeSet(IntRangeCollection collection) {
        super(collection);
    }
    
    //================
    // Invert Methods
    //================
    
    /**
     * 
     */
    public ConstIntRangeSet invert() {
        
        // increase array size by one in order to avoid double array allocation
        // if the inversion needs one additional interval
        Interval[] newIntervalList = new Interval[intervalCount+1];
        
        for (int i=0;i<intervalCount;i++) {
            newIntervalList[i] = new Interval(intervalList[i].start,
                    intervalList[i].end,null);
        }
        
        MutableIntRangeSet set = new MutableIntRangeSet(intervalCount,
                newIntervalList);
        
        set.invert();
        
        return set.toConstSet();
    }
    
    /**
     * 
     */
    public ConstIntRangeSet invert(int start, int end) {
        MutableIntRangeSet set = new MutableIntRangeSet(this);
        
        set.invert(start,end);
        
        return set.toConstSet();
    }
    
    //======================
    // Intersection Methods
    //======================
    
    /**
     * 
     */
    public ConstIntRangeSet intersection(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (collection == this) {
            return this;
        }
        
        // This is an optimization which removes the object creation
        // overhead for non-intersecting cases. This is especially useful
        // for small lists.
        if (!intersects(collection)) {
            return EMPTY_SET;
        }
        
        // create intersection
        MutableIntRangeSet mutableSet = toMutableSet();
        mutableSet.intersection(collection);
        
        return mutableSet.toConstSet();
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public MutableIntRangeSet toMutableSet() {
        return new MutableIntRangeSet(this);
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object) {
        
        // If the hash of this interval is known and the compare object is also
        // a ConstIntervalList and has also a computed hash value then use them
        // as a short cut.
        if ((hash != 0) && (object instanceof ConstIntRangeSet)) {
            ConstIntRangeSet intervalList = (ConstIntRangeSet)object;
            
            // Note: The hash of the other interval must also be not zero as
            // this indicates that it has been already computed. Otherwise it
            // could be different but not computed. 
            if ((intervalList.hash != 0) && (hash != intervalList.hash)) {
                return false;
            }
        }
        
        return super.equals(object);
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
