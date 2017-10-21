/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * Interval and position indices in the context of this interval collection are
 * as following:
 * <pre>
 * 
 *       0      1      2      3       (interval indices)
 * "    ---    ---    ---    ---    "
 *   -1     -2     -3     -4     -5   (position indices)
 * </pre>
 * and normalized they are:
 * <pre>
 *      0     1     2     3      (interval indices)
 * "   ---   ---   ---   ---   "
 *   0     1     2     3     4   (position indices)
 * </pre>
 * 
 * @author Stefan Czaska
 */
public class IntRangeCollection implements IntCollection {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    int intervalCount;
    
    /**
     * 
     */
    Interval[] intervalList;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    IntRangeCollection() {
    }
    
    //==================
    // Interval Methods
    //==================
    
    /**
     * 
     */
    public final int size() {
        return intervalCount;
    }
    
    /**
     * 
     */
    public final int getStart(int index) {
        if ((index < 0) || (index >= intervalCount)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        return intervalList[index].start;
    }
    
    /**
     * 
     */
    public final int getEnd(int index) {
        if ((index < 0) || (index >= intervalCount)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        return intervalList[index].end;
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public final boolean isEmpty() {
        return intervalCount == 0;
    }
    
    /**
     * 
     */
    public final boolean hasContent() {
        return intervalCount != 0;
    }
    
    //======================
    // Intersection Methods
    //======================
    
    /**
     * 
     */
    public boolean intersects(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        int startLocation = locationOf(start);
        
        // check for direct hit at the start
        if (startLocation >= 0) {
            return true;
        }
        
        int endLocation = locationOf(end);
        
        // check for direct hit at the end
        if (endLocation >= 0) {
            return true;
        }
        
        // as the values are negative check for lower which means at least
        // one interval between start and end
        return endLocation < startLocation;
    }
    
    /**
     * 
     */
    public boolean intersects(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (collection == this) {
            return true;
        }
        
        int intervalCount = this.intervalCount;
        
        if (intervalCount > 0) {
            int collectionIntervalCount = collection.intervalCount;
            
            if (collectionIntervalCount > 0) {
                Interval[] intervalList = this.intervalList;
                Interval[] collectionIntervalList = collection.intervalList;
                
                if ((intervalList[0].start <= collectionIntervalList[collectionIntervalCount-1].end) &&
                    (intervalList[intervalCount-1].end >= collectionIntervalList[0].start)) {
                    
                    if (collectionIntervalCount <= intervalCount) {
                        for (int i=0;i<collectionIntervalCount;i++) {
                            if (intersects(collectionIntervalList[i].start,
                                    collectionIntervalList[i].end)) {
                                
                                return true;
                            }
                        }
                    }
                    
                    else {
                        for (int i=0;i<intervalCount;i++) {
                            if (collection.intersects(intervalList[i].start,
                                    intervalList[i].end)) {
                                
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    //==================
    // Contains Methods
    //==================
    
    /**
     * 
     */
    public boolean contains(int value) {
        return locationOf(value) >= 0;
    }
    
    /**
     * 
     */
    public boolean contains(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        // Note: This method assumes that intervals are usually not
        // adjacent. Thus this method first checks the interval after the
        // start interval in order to avoid the index lookup for end. Only
        // if the next interval is actually adjacent the end index is
        // determined and used as a further check. Only if this also fails the
        // iteration over all intervals between start and end is started.
        // As this is the only part which can have a runtime of O(n) it
        // should be performed at the very end.
        
        int startLocation = locationOf(start);
        
        // start must lie inside an interval
        if (startLocation >= 0) {
            Interval[] intervalList = this.intervalList;
            int startIntervalEnd = intervalList[startLocation].end;
            
            // end lies also inside the start interval
            if (end <= startIntervalEnd) {
                return true;
            }
            
            int nextIndex = startLocation + 1;
            
            // next interval exists and is adjacent to start interval
            if ((nextIndex < intervalCount) &&
                (intervalList[nextIndex].start == (startIntervalEnd + 1))) {
                
                // end lies inside next interval
                if (end <= intervalList[nextIndex].end) {
                    return true;
                }
                
                // end lies not inside next interval
                // -> get end index
                int endLocation = locationOf(end);
                
                // end lies inside an interval
                if (endLocation > nextIndex) {
                    
                    // check whether all intervals after next up to end are
                    // adjacent
                    for (int i=endLocation;i>nextIndex;i--) {
                        
                        // current interval is not adjacent
                        if (intervalList[i].start != (intervalList[i-1].end+1)) {
                            return false;
                        }
                    }
                    
                    // all intervals are adjacent
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean contains(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (collection == this) {
            return true;
        }
        
        int intervalCount = collection.intervalCount;
        
        if (intervalCount > 0) {
            Interval[] collectionIntervalList = collection.intervalList;
            
            for (int i=0;i<intervalCount;i++) {
                if (!contains(collectionIntervalList[i].start,
                        collectionIntervalList[i].end)) {
                    
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    //==================
    // Location Methods
    //==================
    
    /**
     * 
     */
    public int locationOf(int value) {
        int start = 0;
        int end = intervalCount - 1;
        Interval[] intervalList = this.intervalList;
        
        while (start <= end) {
            int curIndex = (start + end) >>> 1;
            
            if (value < intervalList[curIndex].start) {
                end = curIndex - 1;
            }
            
            else if (value > intervalList[curIndex].end) {
                start = curIndex + 1;
            }
            
            else {
                return curIndex;
            }
        }
        
        return -start - 1;
    }
    
    /**
     * 
     */
    public int indexOf(int value) {
        int location = locationOf(value);
        
        return location >= 0 ? location : -1;
    }
    
    /**
     * 
     */
    public int indexOf(int value, boolean previous) {
        int location = locationOf(value);
        
        if (location >= 0) {
            return location;
        }
        
        if (previous) {
            return -(location + 2);
        }
        
        location = -(location + 1);
        
        return location < intervalCount ? location : -1;
    }
    
    //==========================
    // Content Equality Methods
    //==========================
    
    /**
     * 
     */
    public boolean equalsCollection(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (collection == this) {
            return true;
        }
        
        // Note: Two collections are equal either if both are empty or both
        // are not empty and cover the same set of integers.
        
        // first check whether both collections are empty
        int intervalCount = this.intervalCount;
        
        if (intervalCount == 0) {
            return collection.intervalCount == 0;
        }
        
        int collectionIntervalCount = collection.intervalCount;
        
        if (collectionIntervalCount == 0) {
            return false;
        }
        
        // both collections are not empty
        // -> check whether they cover the same set of integers
        
        // Note: The following code checks for sequences of adjacent
        // intervals whether they are equal to the corresponding sequences
        // of the other collection.
        int i = 0;
        int j = 0;
        Interval[] intervalList = this.intervalList;
        Interval[] collectionIntervalList = collection.intervalList;
        
        OuterLoop:
        do {
            
            // start of both current sequences must be equal
            if (intervalList[i].start != collectionIntervalList[j].start) {
                return false;
            }
            
            // now check the content of both sequences
            int end1 = intervalList[i].end;
            int end2 = collectionIntervalList[j].end;
            
            while (true) {
                
                // if the ends are equal then both sequences are equal and
                // the next both sequences can be checked
                if (end1 == end2) {
                    i++;
                    j++;
                    
                    // check whether the end has been reached
                    if (i >= intervalCount) {
                        return j == collectionIntervalCount;
                    }
                    
                    // check whether the end has been reached
                    if (j >= collectionIntervalCount) {
                        return i == intervalCount;
                    }
                    
                    continue OuterLoop;
                }
                
                // if the current interval of the first sequence is greater
                // then check the next interval of the second sequence
                if (end1 > end2) {
                    j++;
                    
                    // check whether the end has been reached and whether the
                    // interval is not adjacent
                    if ((j == collectionIntervalCount) ||
                        (collectionIntervalList[j].start > (end2 + 1))) {
                        
                        return false;
                    }
                    
                    end2 = collectionIntervalList[j].end;
                }
                
                // if the current interval of the second sequence is greater
                // then check the next interval of the first sequence
                else {
                    i++;
                    
                    // check whether the end has been reached and whether the
                    // interval is not adjacent
                    if ((i == intervalCount) ||
                        (intervalList[i].start > (end1 + 1))) {
                        
                        return false;
                    }
                    
                    end1 = collectionIntervalList[i].end;
                }
            }
        } while (true);
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
        buffer.append("[intervalCount=");
        
        buffer.append(intervalCount);
        buffer.append(",intervalList=[");
        
        for (int i=0;i<intervalCount;i++) {
            buffer.append(intervalList[i].start);
            buffer.append("..");
            buffer.append(intervalList[i].end);
            
            if (i < (intervalCount-1)) {
                buffer.append(",");
            }
        }
        
        buffer.append("]]");
        
        return buffer.toString();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class Interval {
        
        /**
         * 
         */
        int start;
        
        /**
         * 
         */
        int end;
        
        /**
         * 
         */
        Object value;
        
        /**
         * 
         */
        Interval() {
        }
        
        /**
         * 
         */
        Interval(int start, int end, Object value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            
            buffer.append(getClass().getSimpleName());
            buffer.append("[start=");
            buffer.append(start);
            buffer.append(",end=");
            buffer.append(end);
            buffer.append(",value=");
            buffer.append(value);
            buffer.append("]");
            
            return buffer.toString();
        }
    }
}
