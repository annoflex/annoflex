/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * @author Stefan Czaska
 */
public class IntRangeSet extends IntRangeCollection {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    IntRangeSet() {
    }
    
    /**
     * 
     */
    IntRangeSet(int index) {
        this(index,index);
    }
    
    /**
     * 
     */
    IntRangeSet(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be " +
                    "greater than end");
        }
        
        intervalCount = 1;
        intervalList = new Interval[] {new Interval(start,end,null)};
    }
    
    /**
     * 
     */
    IntRangeSet(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values may not be null");
        }
        
        if (values.length > 0) {
            Interval[] intervalList = new Interval[values.length];
            int lastValue = Integer.MIN_VALUE;
            
            for (int i=0;i<values.length;i++) {
                int curValue = values[i];
                
                if ((curValue <= lastValue) && (i != 0)) {
                    throw new IllegalArgumentException("invalid interval " +
                            "value of interval "+i);
                }
                
                intervalList[i] = new Interval(curValue,curValue,null);
                lastValue = curValue;
            }
            
            intervalCount = values.length;
            this.intervalList = intervalList;
        }
    }
    
    /**
     * 
     */
    IntRangeSet(int[][] spanList) {
        if (spanList == null) {
            throw new IllegalArgumentException("span list may not be null");
        }
        
        int count = spanList.length;
        
        if (count > 0) {
            Interval[] intervalList = new Interval[count];
            int lastEndValue = Integer.MIN_VALUE;
            
            for (int i=0;i<count;i++) {
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
                
                intervalList[i] = new Interval(curStart,curEnd,null);
                lastEndValue = curEnd;
            }
            
            intervalCount = count;
            this.intervalList = intervalList;
        }
    }
    
    /**
     * 
     */
    IntRangeSet(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        int count = collection.intervalCount;
        
        if (count > 0) {
            Interval[] intervalList = new Interval[count];
            Interval[] collectionIntervalList = collection.intervalList;
            
            for (int i=0;i<count;i++) {
                intervalList[i] = new Interval(collectionIntervalList[i].start,
                        collectionIntervalList[i].end,null);
            }
            
            intervalCount = count;
            this.intervalList = intervalList;
        }
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        
        if (object instanceof IntRangeSet) {
            IntRangeSet rangeSet = (IntRangeSet)object;
            int intervalCount = this.intervalCount;
            
            if (rangeSet.intervalCount == intervalCount) {
                Interval[] intervalList = this.intervalList;
                Interval[] rangeSetIntervalList = rangeSet.intervalList;
                
                for (int i=0;i<intervalCount;i++) {
                    if ((rangeSetIntervalList[i].start != intervalList[i].start) ||
                        (rangeSetIntervalList[i].end != intervalList[i].end)) {
                        
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
    public int hashCode() {
        int intervalCount = this.intervalCount;
        int hash = intervalCount;
        Interval[] intervalList = this.intervalList;
        
        for (int i=0;i<intervalCount;i++) {
            hash = hash * 8191 + intervalList[i].start * 31;
            hash = hash * 8191 + intervalList[i].end * 31;
        }
        
        return hash == 0 ? 1 : hash;
    }
}
