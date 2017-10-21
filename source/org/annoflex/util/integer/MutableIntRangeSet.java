/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * @author Stefan Czaska
 */
public class MutableIntRangeSet extends IntRangeSet {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final int MAX_REUSE_SIZE = 16;
    
    /**
     * 
     */
    private static final int SHRINK_RELATION = 8;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public MutableIntRangeSet() {
    }
    
    /**
     * 
     */
    public MutableIntRangeSet(int value) {
        super(value);
    }
    
    /**
     * 
     */
    public MutableIntRangeSet(int start, int end) {
        super(start,end);
    }
    
    /**
     * 
     */
    public MutableIntRangeSet(int[] values) {
        super(values);
    }
    
    /**
     * 
     */
    public MutableIntRangeSet(int[][] spanList) {
        super(spanList);
    }
    
    /**
     * 
     */
    public MutableIntRangeSet(IntRangeCollection collection) {
        super(collection);
    }
    
    /**
     * 
     */
    MutableIntRangeSet(int intervalCount, Interval[] intervalList) {
        this.intervalCount = intervalCount;
        this.intervalList = intervalList;
    }
    
    //==================
    // Mode Add Methods
    //==================
    
    /**
     * 
     */
    public boolean add(int value) {
        return add(value,value);
    }
    
    /**
     * 
     */
    public boolean add(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        int firstLocation = locationOf(start);
        int lastLocation = locationOf(end);
        
        // first position lies inside an interval
        if (firstLocation >= 0) {
            
            // last position lies inside an interval
            if (lastLocation >= 0) {
                
                // if the addition interval intersect with more than one
                // interval then adjust the first one and remove all others,
                // otherwise the interval list is not changed by the addition
                if (lastLocation > firstLocation) {
                    int endOfLastInterval = intervalList[lastLocation].end;
                    
                    removeInternal(firstLocation+1,lastLocation+1);
                    
                    // adjust end of first interval
                    intervalList[firstLocation].end = endOfLastInterval;
                    
                    return true;
                }
            }
            
            // last position lies not inside an interval
            else {
                
                // determine non-interval position
                lastLocation = -(lastLocation + 1);
                
                // Check whether there is a following interval and the end
                // lies straight before this interval. If this is the case
                // then extend the addition interval by this interval.
                if ((lastLocation < intervalCount) &&
                    (end == (intervalList[lastLocation].start - 1))) {
                    
                    int endOfLastInterval = intervalList[lastLocation].end;
                    
                    removeInternal(firstLocation+1,lastLocation+1);
                    
                    // adjust end of first interval
                    intervalList[firstLocation].end = endOfLastInterval;
                }
                
                // otherwise adjust first interval and remove all intervals
                // between first and last position
                else {
                    removeInternal(firstLocation+1,lastLocation);
                    
                    // adjust end of first interval
                    intervalList[firstLocation].end = end;
                }
                
                return true;
            }
        }
        
        // first position lies not inside an interval
        else {
            
            // determine non-interval positions
            firstLocation = -(firstLocation + 1);
            
            // last position lies inside an interval
            if (lastLocation >= 0) {
                
                // Check whether there is a preceding interval and the start
                // lies straight after this interval. If this is the case
                // then extend the addition interval by this interval.
                if ((firstLocation > 0) &&
                    (start == (intervalList[firstLocation-1].end + 1))) {
                    
                    int endOfLastInterval = intervalList[lastLocation].end;
                    
                    removeInternal(firstLocation,lastLocation+1);
                    
                    // adjust end of first interval
                    intervalList[firstLocation-1].end = endOfLastInterval;
                }
                
                // otherwise adjust first interval and remove all intervals
                // between first and last position
                else {
                    int endOfLastInterval = intervalList[lastLocation].end;
                    
                    removeInternal(firstLocation+1,lastLocation+1);
                    
                    // adjust first interval
                    intervalList[firstLocation].start = start;
                    intervalList[firstLocation].end = endOfLastInterval;
                }
                
                return true;
            }
            
            // last position lies not inside an interval
                
            // determine non-interval positions
            lastLocation = -(lastLocation + 1);
            
            // Check whether there is a preceding interval and the start
            // lies straight after this interval.
            if ((firstLocation > 0) &&
                (start == (intervalList[firstLocation-1].end + 1))) {
                
                // Check whether there is a following interval and the end
                // lies straight before this interval. If this is the case
                // then extend the addition interval by both intervals.
                if ((lastLocation < intervalCount) &&
                    (end == (intervalList[lastLocation].start - 1))) {
                    
                    int endOfLastInterval = intervalList[lastLocation].end;
                    
                    removeInternal(firstLocation,lastLocation+1);
                    
                    // adjust end of first interval
                    intervalList[firstLocation-1].end = endOfLastInterval;
                }
                
                // If the end lies not straight before a following
                // interval then extend the addition interval by the
                // preceding interval.
                else {
                    removeInternal(firstLocation,lastLocation);
                    
                    // adjust end of first interval
                    intervalList[firstLocation-1].end = end;
                }
            }
            
            // start lies not straight after a preceding interval
            else {
                
                // Check whether there is a following interval and the end
                // lies straight before this interval. If this is the case
                // then extend the addition interval by this interval.
                if ((lastLocation < intervalCount) &&
                    (end == (intervalList[lastLocation].start - 1))) {
                    
                    int endOfLastInterval = intervalList[lastLocation].end;
                    
                    removeInternal(firstLocation+1,lastLocation+1);
                    
                    // adjust first interval
                    intervalList[firstLocation].start = start;
                    intervalList[firstLocation].end = endOfLastInterval;
                }
                
                // If the end lies not straight before a following
                // interval then adjust the first interval by start and
                // end and remove all intervals between the first
                // interval and the last position.
                
                // End lies not straight before a following interval, so
                // start and end points just to positions between intervals.
                else {
                    
                    // Check whether there is at least one interval between
                    // start and end position.
                    if (lastLocation > firstLocation) {
                        removeInternal(firstLocation+1,lastLocation);
                        
                        // adjust first interval
                        intervalList[firstLocation].start = start;
                        intervalList[firstLocation].end = end;
                    }
                    
                    // No interval between start and end. Insert new
                    // interval.
                    else {
                        insertInternal(firstLocation,1);
                        
                        intervalList[firstLocation].start = start;
                        intervalList[firstLocation].end = end;
                    }
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean add(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values may not be null");
        }
        
        boolean modified = false;
        
        for (int i=0;i<values.length;i++) {
            if (add(values[i])) {
                modified = true;
            }
        }
        
        return modified;
    }
    
    /**
     * 
     */
    public boolean add(int[][] spanList) {
        if (spanList == null) {
            throw new IllegalArgumentException("span list may not be null");
        }
        
        boolean modified = false;
        
        for (int i=0;i<spanList.length;i++) {
            int[] span = spanList[i];
            
            if (span == null) {
                throw new IllegalArgumentException("span may not be null");
            }
            
            if (span.length == 1) {
                if (add(span[0])) {
                    modified = true;
                }
            }
            
            else if (span.length == 2) {
                if (add(span[0],span[1])) {
                    modified = true;
                }
            }
            
            else {
                throw new IllegalArgumentException("span array must "
                        + "either have a length of one or two");
            }
        }
        
        return modified;
    }
    
    /**
     * 
     */
    public boolean add(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (collection != this) {
            boolean modified = false;
            int intervalCount = collection.intervalCount;
            
            for (int i=0;i<intervalCount;i++) {
                if (add(collection.intervalList[i].start,
                        collection.intervalList[i].end)) {
                    
                    modified = true;
                }
            }
            
            return modified;
        }
        
        return false;
    }
    
    //================
    // Remove Methods
    //================
    
    /**
     * 
     */
    public boolean remove(int value) {
        return remove(value,value);
    }
    
    /**
     * 
     */
    public boolean remove(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater then end");
        }
        
        int firstIndex = indexOf(start,false);
        
        if (firstIndex >= 0) {
            int lastIndex = indexOf(end,true);
            
            if (lastIndex >= 0) {
                int delta = lastIndex - firstIndex;
                
                // one range
                if (delta == 0) {
                    
                    // start of subtraction interval is greater than start
                    // of current interval
                    if (start > intervalList[firstIndex].start) {
                        
                        // end of subtraction interval is lower than end of
                        // current interval
                        if (end < intervalList[firstIndex].end) {
                            int insertionIndex = firstIndex + 1;
                            
                            insertInternal(insertionIndex,1);
                            
                            intervalList[insertionIndex].start = end + 1;
                            intervalList[insertionIndex].end = intervalList[firstIndex].end;
                            
                            intervalList[firstIndex].end = start - 1;
                        }
                        
                        // end of subtraction interval is greater or equal
                        // than end of current interval
                        else {
                            intervalList[firstIndex].end = start - 1;
                        }
                    }
                    
                    // start of subtraction interval is lower or equal than
                    // start of current interval
                    else {
                        
                        // end of subtraction interval is greater or equal than
                        // end of current interval
                        if (end >= intervalList[firstIndex].end) {
                            removeInternal(firstIndex,firstIndex+1);
                        }
                        
                        // end of subtraction interval is lower than end of
                        // current interval
                        else {
                            
                            // adjust interval
                            intervalList[firstIndex].start = end + 1;
                        }
                    }
                    
                    return true;
                }
                
                // more than one range
                if (delta > 0) {
                    boolean adjustStart = start > intervalList[firstIndex].start;
                    boolean adjustEnd = end < intervalList[lastIndex].end;
                    
                    removeInternal(firstIndex + (adjustStart ? 1 : 0),
                            lastIndex + (adjustEnd ? 0 : 1));
                    
                    if (adjustStart) {
                        intervalList[firstIndex].end = start - 1;
                    }
                    
                    if (adjustEnd) {
                        intervalList[lastIndex].start = end + 1;
                    }
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean remove(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values may not be null");
        }
        
        if (hasContent()) {
            boolean modified = false;
            
            for (int i=0;i<values.length;i++) {
                if (remove(values[i])) {
                    if (isEmpty()) {
                        return true;
                    }
                    
                    modified = true;
                }
            }
            
            return modified;
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean remove(int[][] spanList) {
        if (spanList == null) {
            throw new IllegalArgumentException("span list may not be null");
        }
        
        if (hasContent()) {
            boolean modified = false;
            
            for (int i=0;i<spanList.length;i++) {
                int[] span = spanList[i];
                
                if (span == null) {
                    throw new IllegalArgumentException("span may not be null");
                }
                
                if (span.length == 1) {
                    if (remove(span[0])) {
                        if (isEmpty()) {
                            return true;
                        }
                        
                        modified = true;
                    }
                }
                
                else if (span.length == 2) {
                    if (remove(span[0],span[1])) {
                        if (isEmpty()) {
                            return true;
                        }
                        
                        modified = true;
                    }
                }
                
                else {
                    throw new IllegalArgumentException("span array must "
                            + "either have a length of one or two");
                }
            }
            
            return modified;
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean remove(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (hasContent()) {
            if (collection == this) {
                return clear();
            }
            
            boolean modified = false;
            int intervalCount = collection.intervalCount;
            
            for (int i=0;i<intervalCount;i++) {
                if (remove(collection.intervalList[i].start,
                        collection.intervalList[i].end)) {
                    
                    if (isEmpty()) {
                        return true;
                    }
                    
                    modified = true;
                }
            }
            
            return modified;
        }
        
        return false;
    }
    
    //================
    // Invert Methods
    //================
    
    /**
     * 
     */
    public void invert() {
        if (intervalCount == 0) {
            insertInternal(0,1);
            
            intervalList[0].start = Integer.MIN_VALUE;
            intervalList[0].end = Integer.MAX_VALUE;
        }
        
        else {
            
            // Note: Iterate always from start to end in order to ensure that
            // the final array copy modifications are performed at the end of
            // the array.
            int firstStart = intervalList[0].start;
            int lastEnd = intervalList[intervalCount-1].end;
            int i;
            int j = 0;
            int start;
            
            // skip first interval if necessary
            if (firstStart == Integer.MIN_VALUE) {
                i = 1;
                start = intervalList[0].end + 1;
            }
            
            else {
                i = 0;
                start = Integer.MIN_VALUE;
            }
            
            // adjust intervals in the mid
            while (i < intervalCount) {
                
                // skip adjacent intervals
                if (intervalList[i].start == start) {
                    start = intervalList[i].end + 1;
                }
                
                // otherwise create an inverted interval between the current and
                // previous interval
                else {
                    int end = intervalList[i].start - 1;
                    intervalList[j].start = start;
                    start = intervalList[i].end + 1;
                    intervalList[j].end = end;
                    j++;
                }
                
                i++;
            }
            
            // adjust last interval if necessary
            if (lastEnd != Integer.MAX_VALUE) {
                if (j == intervalCount) {
                    insertInternal(j,1);
                }
                
                intervalList[j].start = lastEnd + 1;
                intervalList[j].end = Integer.MAX_VALUE;
                j++;
            }
            
            removeInternal(j,intervalCount);
        }
    }
    
    /**
     * 
     */
    public void invert(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater then end");
        }
        
        if (intervalCount == 0) {
            insertInternal(0,1);
            
            intervalList[0].start = start;
            intervalList[0].end = end;
        }
        
        else {
            MutableIntRangeSet list = new MutableIntRangeSet();
            int startInterval = indexOf(start,false);
            int endInterval = indexOf(end,true);
            
            for (int i=startInterval;i<=endInterval;i++) {
                list.add(intervalList[i].start,intervalList[i].end);
            }
            
            list.invert();
            
            if (end < Integer.MAX_VALUE) {
                list.remove(end+1,Integer.MAX_VALUE);
            }
            
            if (start > Integer.MIN_VALUE) {
                list.remove(Integer.MIN_VALUE,start-1);
            }
            
            remove(start,end);
            add(list);
        }
    }
    
    //======================
    // Intersection Methods
    //======================
    
    /**
     * 
     */
    public boolean intersection(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if ((collection != this) && hasContent()) {
            int intervalCount = collection.intervalCount;
            
            if (intervalCount == 0) {
                return clear();
            }
            
            boolean modified = false;
            
            int minValue = collection.intervalList[0].start;
            
            if ((minValue > Integer.MIN_VALUE) &&
                remove(Integer.MIN_VALUE,minValue-1)) {
                
                if (isEmpty()) {
                    return true;
                }
                
                modified = true;
            }
            
            int maxValue = collection.intervalList[intervalCount-1].end;
            
            if ((maxValue < Integer.MAX_VALUE) &&
                remove(maxValue+1,Integer.MAX_VALUE)) {
                
                if (isEmpty()) {
                    return true;
                }
                
                modified = true;
            }
            
            for (int i=1;i<intervalCount;i++) {
                if (remove(collection.intervalList[i-1].end+1,
                        collection.intervalList[i].start-1)) {
                    
                    if (isEmpty()) {
                        return true;
                    }
                    
                    modified = true;
                }
            }
            
            return modified;
        }
        
        return false;
    }
    
    //==============================
    // Symmetric Difference Methods
    //==============================
    
    /**
     * 
     */
    public boolean symmetricDifference(IntRangeCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        if (collection == this) {
            return clear();
        }
        
        if (collection.isEmpty()) {
            return false;
        }
        
        if (isEmpty()) {
            return add(collection);
        }
        
        MutableIntRangeSet collectionWithoutThis = new MutableIntRangeSet(collection);
        collectionWithoutThis.remove(this);
        
        return remove(collection) | add(collectionWithoutThis);
    }
    
    //==========================
    // Interval Removal Methods
    //==========================
    
    /**
     * 
     */
    public boolean removeAt(int index) {
        return removeBetween(index,index+1);
    }
    
    /**
     * 
     */
    public boolean removeBetween(int startIndex, int endIndex) {
        if ((startIndex < 0) || (endIndex > intervalCount) ||
            (endIndex <= startIndex)) {
            
            throw new IllegalArgumentException("indices are out of range");
        }
        
        removeInternal(startIndex,endIndex);
        
        return true;
    }
    
    /**
     * 
     */
    public boolean clear() {
        if (intervalCount > 0) {
            removeInternal(0,intervalCount);
            
            return true;
        }
        
        return false;
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public ConstIntRangeSet toConstSet() {
        if (intervalCount == 0) {
            return ConstIntRangeSet.EMPTY_SET;
        }
        
        return new ConstIntRangeSet(this);
    }
    
    //====================
    // Management Methods
    //====================
    
    /**
     * 
     */
    private void insertInternal(int index, int count) {
        int newIntervalCount = intervalCount + count;
        
        // empty or full array -> reallocate and copy if necessary
        if ((intervalList == null) || (newIntervalCount > intervalList.length)) {
            int newSize = computeNewListSize(newIntervalCount);
            Interval[] newIntervalList = new Interval[newSize];
            
            if (intervalCount > 0) {
                if (index == intervalCount) {
                    System.arraycopy(intervalList,0,newIntervalList,0,
                            intervalCount);
                }
                
                else if (index == 0) {
                    System.arraycopy(intervalList,0,newIntervalList,count,
                            intervalCount);
                }
                
                else {
                    System.arraycopy(intervalList,0,newIntervalList,0,index);
                    System.arraycopy(intervalList,index,newIntervalList,
                                    index+count,intervalCount-index);
                }
            }
            
            // set new array
            intervalList = newIntervalList;
        }
        
        // new array is not necessary -> just copy the data
        else if (index < intervalCount) {
            
            // copy array content
            System.arraycopy(intervalList,index,intervalList,index+count,
                    intervalCount-index);
        }
        
        // create new intervals and increase size
        for (int i=0;i<count;i++) {
            intervalList[index+i] = new Interval();
        }
        
        intervalCount += count;
    }
    
    /**
     * 
     */
    private void removeInternal(int index1, int index2) {
        if (index2 > index1) {
            
            // partial deletion at the start or in the middle
            if (index2 < intervalCount) {
                int delta = index2 - index1;
                int newSize = intervalCount - delta;
                int refSize = intervalList.length / SHRINK_RELATION;
                
                // unused area is large -> reallocate new array
                if ((newSize < refSize) && (intervalList.length > MAX_REUSE_SIZE)) {
                    
                    // compute new list size
                    int newListSize = computeNewListSize(newSize);
                    
                    // reallocate new array
                    Interval[] newIntervalList = new Interval[newListSize];
                    
                    // copy first part
                    System.arraycopy(intervalList,0,newIntervalList,0,index1);
                    
                    // copy second part
                    System.arraycopy(intervalList,index2,newIntervalList,index1,
                            intervalCount-index2);
                    
                    // set new array
                    intervalList = newIntervalList;
                }
                
                // unused area is small -> delete normally
                else {
                    
                    // copy array content
                    System.arraycopy(intervalList,index2,intervalList,index1,
                            intervalCount-index2);
                    
                    // remove references
                    for (int i=intervalCount-delta;i<intervalCount;i++) {
                        intervalList[i] = null;
                    }
                }
            }
            
            // full deletion
            else if (index1 == 0) {
                
                // large arrays are thrown away in order not to waste memory
                if (intervalList.length > MAX_REUSE_SIZE) {
                    intervalList = null;
                }
                
                // small arrays are reused in order to avoid expensive memory
                // allocations
                else {
                    
                    // remove references
                    for (int i=index1;i<index2;i++) {
                        intervalList[i] = null;
                    }
                }
            }
            
            // partial deletion at the end
            else {
                
                // remove references
                for (int i=index1;i<index2;i++) {
                    intervalList[i] = null;
                }
            }
            
            intervalCount -= index2 - index1;
        }
    }
    
    /**
     * 
     */
    private int computeNewListSize(int curSize) {
        return (curSize * 3) / 2 + 1;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public MutableIntRangeSet clone() {
        return new MutableIntRangeSet(this);
    }
}
