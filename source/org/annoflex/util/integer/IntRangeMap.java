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
public class IntRangeMap<V> extends IntRangeCollection {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    IntRangeMap() {
    }
    
    /**
     * 
     */
    IntRangeMap(int index, V value) {
        this(index,index,value);
    }
    
    /**
     * 
     */
    IntRangeMap(int start, int end, V value) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be " +
                    "greater than end");
        }
        
        intervalCount = 1;
        intervalList = new Interval[] {new Interval(start,end,value)};
    }
    
    /**
     * 
     */
    IntRangeMap(int[] indices, V value) {
        if (indices == null) {
            throw new IllegalArgumentException("indices may not be null");
        }
        
        if (indices.length > 0) {
            Interval[] intervalList = new Interval[indices.length];
            int lastValue = Integer.MIN_VALUE;
            
            for (int i=0;i<indices.length;i++) {
                int curValue = indices[i];
                
                if ((curValue <= lastValue) && (i != 0)) {
                    throw new IllegalArgumentException("invalid interval " +
                            "value of interval "+i);
                }
                
                intervalList[i] = new Interval(curValue,curValue,value);
                lastValue = curValue;
            }
            
            intervalCount = indices.length;
            this.intervalList = intervalList;
        }
    }
    
    /**
     * 
     */
    IntRangeMap(int[][] spanList, V value) {
        if (spanList == null) {
            throw new IllegalArgumentException("span list may not be null");
        }
        
        if (spanList.length > 0) {
            Interval[] intervalList = new Interval[spanList.length];
            int lastEndValue = Integer.MIN_VALUE;
            
            for (int i=0;i<spanList.length;i++) {
                if (spanList[i] == null) {
                    throw new IllegalArgumentException("span list may not " +
                            "contain null entries");
                }
                
                int curStart = spanList[i][0];
                
                if ((curStart <= lastEndValue) && (i != 0)) {
                    throw new IllegalArgumentException("invalid interval " +
                            "start of interval "+i);
                }
                
                int curEnd = spanList[i][1];
                
                if (curEnd < curStart) {
                    throw new IllegalArgumentException("invalid interval " +
                            "end of interval "+i);
                }
                
                intervalList[i] = new Interval(curStart,curEnd,value);
                lastEndValue = curEnd;
            }
            
            intervalCount = spanList.length;
            this.intervalList = intervalList;
        }
    }
    
    /**
     * 
     */
    IntRangeMap(int[][] spanList, V[] valueList) {
        if (spanList == null) {
            throw new IllegalArgumentException("span list may not be null");
        }
        
        if (valueList == null) {
            throw new IllegalArgumentException("value list may not be null");
        }
        
        if (spanList.length != valueList.length) {
            throw new IllegalArgumentException("length of span list and " +
                    "value list must be equal");
        }
        
        if (spanList.length > 0) {
            Interval[] intervalList = new Interval[spanList.length];
            int lastEndValue = Integer.MIN_VALUE;
            
            for (int i=0;i<spanList.length;i++) {
                if (spanList[i] == null) {
                    throw new IllegalArgumentException("span list may not " +
                            "contain null entries");
                }
                
                int curStart = spanList[i][0];
                
                if ((curStart <= lastEndValue) && (i != 0)) {
                    throw new IllegalArgumentException("invalid interval " +
                            "start of interval "+i);
                }
                
                int curEnd = spanList[i][1];
                
                if (curEnd < curStart) {
                    throw new IllegalArgumentException("invalid interval " +
                            "end of interval "+i);
                }
                
                intervalList[i] = new Interval(curStart,curEnd,valueList[i]);
                lastEndValue = curEnd;
            }
            
            intervalCount = spanList.length;
            this.intervalList = intervalList;
        }
    }
    
    /**
     * 
     */
    IntRangeMap(IntRangeCollection collection, V value) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        int count = collection.intervalCount;
        
        if (count > 0) {
            Interval[] intervalList = new Interval[count];
            Interval[] collectionIntervalList = collection.intervalList;
            
            for (int i=0;i<count;i++) {
                intervalList[i] = new Interval(collectionIntervalList[i].start,
                        collectionIntervalList[i].end,value);
            }
            
            intervalCount = count;
            this.intervalList = intervalList;
        }
    }
    
    /**
     * 
     */
    IntRangeMap(IntRangeMap<? extends V> map) {
        if (map == null) {
            throw new IllegalArgumentException("map may not be null");
        }
        
        int count = map.intervalCount;
        
        if (count > 0) {
            Interval[] intervalList = new Interval[count];
            Interval[] mapIntervalList = map.intervalList;
            
            for (int i=0;i<count;i++) {
                intervalList[i] = new Interval(mapIntervalList[i].start,
                        mapIntervalList[i].end,mapIntervalList[i].value);
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
    public final V getValue(int index) {
        if ((index < 0) || (index >= intervalCount)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        return (V)intervalList[index].value;
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
        
        if (object instanceof IntRangeMap) {
            IntRangeMap<V> rangeMap = (IntRangeMap<V>)object;
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
            
            V curValue = (V)intervalList[i].value;
            
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
