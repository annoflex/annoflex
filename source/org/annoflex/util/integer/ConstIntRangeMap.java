/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * @author Stefan Czaska
 */
public class ConstIntRangeMap<V> extends IntRangeMap<V> {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    @SuppressWarnings("rawtypes")
    public static final ConstIntRangeMap EMPTY_MAP = new ConstIntRangeMap();
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ConstIntRangeMap() {
    }
    
    /**
     * 
     */
    public ConstIntRangeMap(int index, V value) {
        super(index,value);
    }
    
    /**
     * 
     */
    public ConstIntRangeMap(int start, int end, V value) {
        super(start,end,value);
    }
    
    /**
     * 
     */
    public ConstIntRangeMap(int[] indices, V value) {
        super(indices,value);
    }
    
    /**
     * 
     */
    public ConstIntRangeMap(int[][] spanList, V value) {
        super(spanList,value);
    }
    
    /**
     * 
     */
    public ConstIntRangeMap(int[][] spanList, V[] valueList) {
        super(spanList,valueList);
    }
    
    /**
     * 
     */
    public ConstIntRangeMap(IntRangeCollection collection, V value) {
        super(collection,value);
    }
    
    /**
     * 
     */
    public ConstIntRangeMap(IntRangeMap<? extends V> map) {
        super(map);
    }
    
    //======================
    // Intersection Methods
    //======================
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public ConstIntRangeMap<V> intersection(IntRangeCollection collection) {
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
            return EMPTY_MAP;
        }
        
        // create intersection
        MutableIntRangeMap<V> mutableMap = toMutableMap();
        mutableMap.intersection(collection);
        
        return mutableMap.toConstMap();
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public MutableIntRangeMap<V> toMutableMap() {
        return new MutableIntRangeMap<>(this);
    }
}
