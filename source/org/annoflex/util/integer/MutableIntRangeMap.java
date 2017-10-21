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
public class MutableIntRangeMap<V> extends IntRangeMap<V> {
    
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
    public MutableIntRangeMap() {
    }
    
    /**
     * 
     */
    public MutableIntRangeMap(int index, V value) {
        super(index,value);
    }
    
    /**
     * 
     */
    public MutableIntRangeMap(int start, int end, V value) {
        super(start,end,value);
    }
    
    /**
     * 
     */
    public MutableIntRangeMap(int[] indices, V value) {
        super(indices,value);
    }
    
    /**
     * 
     */
    public MutableIntRangeMap(int[][] spanList, V value) {
        super(spanList,value);
    }
    
    /**
     * 
     */
    public MutableIntRangeMap(int[][] spanList, V[] valueList) {
        super(spanList,valueList);
    }
    
    /**
     * 
     */
    public MutableIntRangeMap(IntRangeCollection collection, V value) {
        super(collection,value);
    }
    
    /**
     * 
     */
    public MutableIntRangeMap(IntRangeMap<? extends V> map) {
        super(map);
    }
    
    //=============
    // Put Methods
    //=============
    
    /**
     * 
     */
    public boolean put(int index, V value) {
        return put(index,index,value);
    }
    
    /**
     * 
     */
    public boolean put(int start, int end, V value) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        // preprocess start
        int startLocation = locationOf(start);
        boolean replaceStart = false;
        boolean adjustStart = false;
        
        // start lies inside an interval
        if (startLocation >= 0) {
            
            // start lies at the start of the interval
            if (start == intervalList[startLocation].start) {
                
                // check whether there is a preceding adjacent interval and
                // the values are equal -> if yes then extend start by this
                // interval
                if ((startLocation > 0) &&
                    (intervalList[startLocation-1].end == (start - 1)) &&
                    SystemToolkit.equals(value,intervalList[startLocation-1].value)) {
                    
                    startLocation--;
                }
                
                replaceStart = true;
            }
            
            // Start lies not at the start of the interval.
            // Check values. If equal extend, otherwise reduce.
            else {
                if (SystemToolkit.equals(value,intervalList[startLocation].value)) {
                    replaceStart = true;
                }
                
                else {
                    adjustStart = true;
                }
            }
        }
        
        // start lies not inside an interval
        else {
            
            // determine non-interval index
            startLocation = -(startLocation + 1);
            
            // Start lies straight after a preceding interval.
            // Check values. If equal then extend by preceding interval.
            if ((startLocation > 0) &&
                (start == (intervalList[startLocation-1].end + 1)) &&
                SystemToolkit.equals(value,intervalList[startLocation-1].value)) {
                
                startLocation--;
                replaceStart = true;
            }
        }
        
        // preprocess end
        int endLocation = locationOf(end);
        boolean replaceEnd = false;
        boolean adjustEnd = false;
        
        // end lies inside an interval
        if (endLocation >= 0) {
            
            // end lies at the end of the interval
            if (end == intervalList[endLocation].end) {
                
                // check whether there is a following adjacent interval and
                // the values are equal -> if yes then extend end by this
                // interval
                if ((endLocation < (intervalCount - 1)) &&
                    (intervalList[endLocation+1].start == (end + 1)) &&
                    SystemToolkit.equals(value,intervalList[endLocation+1].value)) {
                    
                    endLocation++;
                }
                
                replaceEnd = true;
            }
            
            // End lies not at the end of the interval.
            // Check values. If equal extend, otherwise reduce.
            else {
                if (SystemToolkit.equals(value,intervalList[endLocation].value)) {
                    replaceEnd = true;
                }
                
                else {
                    adjustEnd = true;
                }
            }
        }
        
        // end lies not inside an interval
        else {
            
            // determine non-interval position
            endLocation = -(endLocation + 1);
            
            // End lies straight before a following interval.
            // Check values. If equal then extend by following interval.
            if ((endLocation < intervalCount) &&
                (end == (intervalList[endLocation].start - 1)) &&
                SystemToolkit.equals(value,intervalList[endLocation].value)) {
                
                replaceEnd = true;
            }
        }
        
        // commit new span
        if (replaceStart) {
            
            // replace start and end
            if (replaceEnd) {
                int delta = endLocation - startLocation;
                
                // start and end lie both inside same interval and fully
                // select this interval
                if (delta == 0) {
                    if ((start == intervalList[startLocation].start) &&
                        (end == intervalList[startLocation].end) &&
                        !SystemToolkit.equals(value,intervalList[startLocation].value)) {
                        
                        // adjust value
                        intervalList[startLocation].value = value;
                        
                        return true;
                    }
                }
                
                // start and end lie in different intervals and fully select
                // these intervals -> remove intervals which are no longer
                // needed and adjust start interval
                else {
                    int endOfLastInterval = intervalList[endLocation].end;
                    
                    removeInternal(startLocation+1,endLocation+1);
                    
                    // adjust start interval
                    intervalList[startLocation].end = endOfLastInterval;
                    intervalList[startLocation].value = value;
                    
                    return true;
                }
            }
            
            // replace start and adjust end
            else {
                
                // start and end lie inside same interval
                if (startLocation == endLocation) {
                    
                    insertInternal(startLocation,1);
                    
                    // adjust start interval
                    intervalList[startLocation].start = intervalList[endLocation+1].start;
                    intervalList[startLocation].end = end;
                    intervalList[startLocation].value = value;
                    
                    // adjust end interval
                    intervalList[endLocation+1].start = end + 1;
                }
                
                // start and end lie in different intervals
                else {
                    
                    removeInternal(startLocation+1,endLocation);
                    
                    // adjust start interval
                    intervalList[startLocation].end = end;
                    intervalList[startLocation].value = value;
                    
                    // adjust end interval
                    if (adjustEnd) {
                        intervalList[startLocation+1].start = end + 1;
                    }
                }
                
                return true;
            }
        }
        
        else {
            
            // replace end and adjust start
            if (replaceEnd) {
                
                // start and end lies inside same interval
                if ((startLocation == endLocation) && adjustStart) {
                    insertInternal(++startLocation,1);
                    
                    // adjust end interval
                    intervalList[startLocation].start = start;
                    intervalList[startLocation].end = intervalList[endLocation].end;
                    intervalList[startLocation].value = value;
                    
                    // adjust start interval
                    intervalList[endLocation].end = start - 1;
                }
                
                // start and end lies inside different intervals
                else {
                    removeInternal(startLocation+1,endLocation);
                    
                    // adjust start interval
                    if (adjustStart) {
                        intervalList[startLocation].end = start - 1;
                    }
                    
                    // adjust end interval
                    intervalList[startLocation+1].start = start;
                    intervalList[startLocation+1].value = value;
                }
                
                return true;
            }
            
            // adjust start and end
            
            // start and end lies inside same interval
            // -> create two new intervals
            if (adjustStart && adjustEnd && (startLocation == endLocation)) {
                insertInternal(startLocation+1,2);
                
                // adjust third interval
                intervalList[startLocation+2].start = end + 1;
                intervalList[startLocation+2].end = intervalList[startLocation].end;
                intervalList[startLocation+2].value = intervalList[startLocation].value;
                
                // adjust second interval
                intervalList[startLocation+1].start = start;
                intervalList[startLocation+1].end = end;
                intervalList[startLocation+1].value = value;
                
                // adjust first interval
                intervalList[startLocation].end = start - 1;
                
                return true;
            }
            
            // start and end lies inside different intervals
            // -> adjust start and end and ensure that exactly one new span
            // lies between start and end 
            if (adjustStart) {
                startLocation++;
            }
            
            if (startLocation == endLocation) {
                insertInternal(startLocation,1);
            }
            
            else {
                removeInternal(startLocation+1,endLocation);
            }
            
            if (adjustStart) {
                intervalList[startLocation-1].end = start - 1;
            }
            
            if (adjustEnd) {
                intervalList[startLocation+1].start = end + 1;
            }
            
            intervalList[startLocation].start = start;
            intervalList[startLocation].end = end;
            intervalList[startLocation].value = value;
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean put(int[] indices, V value) {
        if (indices == null) {
            throw new IllegalArgumentException("indices may not be null");
        }
        
        boolean modified = false;
        
        for (int i=0;i<indices.length;i++) {
            if (put(indices[i],value)) {
                modified = true;
            }
        }
        
        return modified;
    }
    
    /**
     * 
     */
    public boolean put(int[][] spanList, V value) {
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
                if (put(span[0],value)) {
                    modified = true;
                }
            }
            
            else if (span.length == 2) {
                if (put(span[0],span[1],value)) {
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
    public boolean put(int[][] spanList, V[] valueList) {
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
        
        boolean modified = false;
        
        for (int i=0;i<spanList.length;i++) {
            int[] span = spanList[i];
            
            if (span == null) {
                throw new IllegalArgumentException("span may not be null");
            }
            
            if (span.length == 1) {
                if (put(span[0],valueList[i])) {
                    modified = true;
                }
            }
            
            else if (span.length == 2) {
                if (put(span[0],span[1],valueList[i])) {
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
    public boolean put(IntRangeCollection collection, V value) {
        if (collection == null) {
            throw new IllegalArgumentException("collection may not be null");
        }
        
        boolean modified = false;
        int intervalCount = collection.intervalCount;
        
        for (int i=0;i<intervalCount;i++) {
            if (put(collection.intervalList[i].start,
                    collection.intervalList[i].end,value)) {
                
                modified = true;
            }
        }
        
        return modified;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public boolean put(IntRangeMap<? extends V> intervalTable) {
        if (intervalTable == null) {
            throw new IllegalArgumentException("interval table may not be null");
        }
        
        if (intervalTable != this) {
            boolean modified = false;
            int intervalCount = intervalTable.intervalCount;
            
            for (int i=0;i<intervalCount;i++) {
                if (put(intervalTable.intervalList[i].start,
                        intervalTable.intervalList[i].end,
                        (V)intervalTable.intervalList[i].value)) {
                    
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
                            intervalList[insertionIndex].value = intervalList[firstIndex].value;
                            
                            intervalList[firstIndex].end = start - 1;
                        }
                        
                        // end of subtraction interval is greater or equal
                        // than end of current interval
                        else {
                            
                            // adjust first interval
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
        
        if (hasContent() && (collection != this)) {
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
    
    //==========================
    // Interval Removal Methods
    //==========================
    
    /**
     * 
     */
    public boolean removeAt(int index) {
        return removeRange(index,index+1);
    }
    
    /**
     * 
     */
    public boolean removeRange(int startIndex, int endIndex) {
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
    @SuppressWarnings("unchecked")
    public ConstIntRangeMap<V> toConstMap() {
        if (intervalCount == 0) {
            return ConstIntRangeMap.EMPTY_MAP;
        }
        
        return new ConstIntRangeMap<>(this);
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
                
                // unused area is small -> delete normally and remove references
                else {
                    
                    // delete array content
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
    public MutableIntRangeMap<V> clone() {
        return new MutableIntRangeMap<>(this);
    }
}
