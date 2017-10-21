/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * @author Stefan Czaska
 */
public class ConstIntRangeMultiMap<V,C> extends IntRangeMultiMap<V,C> {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ConstIntRangeMultiMap(MultiMapController<V,C> controller) {
        super(controller);
    }
    
    /**
     * 
     */
    public ConstIntRangeMultiMap(IntRangeMultiMap<V,C> map) {
        super(map);
    }
    
    //======================
    // Intersection Methods
    //======================
    
    /**
     * 
     */
    public ConstIntRangeMultiMap<V,C> intersection(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (collection == this) {
            return this;
        }
        
        // create intersection
        MutableIntRangeMultiMap<V,C> mutableMap = toMutableMultiMap();
        mutableMap.intersection(collection);
        
        return mutableMap.toConstMultiMap();
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public MutableIntRangeMultiMap<V,C> toMutableMultiMap() {
        return new MutableIntRangeMultiMap<>(this);
    }
}
