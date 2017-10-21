/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

import org.annoflex.util.SystemToolkit;

/**
 * @author Stefan Czaska
 */
public class IntRangeMultiMap<V,C> extends IntRangeCollection {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    final MultiMapController<V,C> controller;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    IntRangeMultiMap(MultiMapController<V,C> controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller may not be null");
        }
        
        this.controller = controller;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    IntRangeMultiMap(IntRangeMultiMap<V,C> map) {
        if (map == null) {
            throw new IllegalArgumentException("map may not be null");
        }
        
        MultiMapController<V,C> controller = map.controller;
        this.controller = controller;
        int count = map.intervalCount;
        
        if (count > 0) {
            Interval[] intervalList = new Interval[count];
            Interval[] mapIntervalList = map.intervalList;
            
            for (int i=0;i<count;i++) {
                intervalList[i] = new Interval(mapIntervalList[i].start,
                        mapIntervalList[i].end,controller.cloneCollection(
                        (C)mapIntervalList[i].value));
            }
            
            intervalCount = count;
            this.intervalList = intervalList;
        }
    }
    
    //===============
    // Value Methods
    //===============
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public final C getValues(int index) {
        if ((index < 0) || (index >= intervalCount)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        return (C)intervalList[index].value;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        
        if (object instanceof IntRangeMultiMap) {
            IntRangeMultiMap<V,C> rangeMap = (IntRangeMultiMap<V,C>)object;
            int intervalCount = this.intervalCount;
            
            if (rangeMap.intervalCount == intervalCount) {
                Interval[] intervalList = this.intervalList;
                Interval[] rangeMapIntervalList = rangeMap.intervalList;
                
                for (int i=0;i<intervalCount;i++) {
                    if ((rangeMapIntervalList[i].start != intervalList[i].start) ||
                        (rangeMapIntervalList[i].end != intervalList[i].end) ||
                        (!SystemToolkit.equals(rangeMapIntervalList[i].value,
                                intervalList[i].value))) {
                        
                        return false;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int hashCode() {
        int intervalCount = this.intervalCount;
        int hash = intervalCount;
        Interval[] intervalList = this.intervalList;
        
        for (int i=0;i<intervalCount;i++) {
            hash = hash * 8191 + intervalList[i].start * 31;
            hash = hash * 8191 + intervalList[i].end * 31;
            
            C curValue = (C)intervalList[i].value;
            
            if (curValue != null) {
                hash ^= curValue.hashCode();
            }
        }
        
        return hash;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[intervalCount=");
        
        buffer.append(intervalCount);
        buffer.append(",intervalList=[");
        
        for (int i=0;i<intervalCount;i++) {
            buffer.append("[");
            buffer.append(intervalList[i].start);
            buffer.append("..");
            buffer.append(intervalList[i].end);
            buffer.append(",");
            buffer.append(intervalList[i].value);
            buffer.append("]");
            
            if (i < (intervalCount-1)) {
                buffer.append(",");
            }
        }
        
        buffer.append("]]");
        
        return buffer.toString();
    }
}
