/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

import java.util.Iterator;

/**
 * @author Stefan Czaska
 */
public class MutableIntRangeMultiMap<V,C> extends IntRangeMultiMap<V,C> {
    
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
    public MutableIntRangeMultiMap(MultiMapController<V,C> controller) {
        super(controller);
    }
    
    /**
     * 
     */
    public MutableIntRangeMultiMap(IntRangeMultiMap<V,C> map) {
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
    @SuppressWarnings("unchecked")
    public boolean put(int start, int end, V value) {
        // TODO: Try to minimize code of this method, especially flag evaluation.
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        // validate value
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }
        
        // preprocess start
        int startLocation = locationOf(start);
        boolean startInside = false;
        boolean startOutside = false;
        
        // start lies inside an interval
        if (startLocation >= 0) {
            
            // check whether start lies not at the start of the interval
            startInside = start != intervalList[startLocation].start;
        }
        
        // start lies not inside an interval
        else {
            
            // determine non-interval index
            startLocation = -(startLocation + 1);
            
            // save that start lies not inside an interval
            startOutside = true;
        }
        
        // preprocess end
        int endLocation = locationOf(end);
        boolean endInside = false;
        boolean endOutside = false;
        
        // end lies inside an interval
        if (endLocation >= 0) {
            
            // check whether end lies not at the end of the interval
            endInside = end != intervalList[endLocation].end;
        }
        
        // end lies not inside an interval
        else {
            
            // determine non-interval position
            endLocation = -(endLocation + 1);
            
            // save that end lies not inside an interval
            endOutside = true;
        }
        
        // commit new span
        if (startOutside) {
            
            // start and end outside
            if (endOutside) {
                int count = getNonEmptyGapCount(startLocation,endLocation-1) +
                        (startLocation == endLocation ? 1 : 2);
                
                insertInternal(endLocation,count);
                
                distributeOldIntervals(startLocation,endLocation-1,count,true);
                
                endLocation += count - 1;
                
                // update start and end
                if (count == 1) {
                    
                    // create new interval
                    intervalList[startLocation].start = start;
                    intervalList[startLocation].end = end;
                }
                
                else {
                    
                    // create new start interval based on old start interval
                    intervalList[startLocation].start = start;
                    intervalList[startLocation].end = intervalList[startLocation+1].start - 1;
                    
                    // create new end interval based on old end interval
                    intervalList[endLocation].start = intervalList[endLocation-1].end + 1;
                    intervalList[endLocation].end = end;
                }
                
                // add new value to all intervals
                addNewValue(startLocation,endLocation+1,value);
                
                return true;
            }
            
            // start outside and end inside
            int count = getNonEmptyGapCount(startLocation,endLocation) +
                    (endInside ? 2 : 1);
            
            insertInternal(endLocation+1,count);
            
            distributeOldIntervals(startLocation,endLocation,count,endInside);
            
            // create new start interval based on old start interval
            intervalList[startLocation].start = start;
            intervalList[startLocation].end = intervalList[startLocation+1].start - 1;
            
            endLocation += count;
            
            if (endInside) {
                
                // create new end interval based on old end interval
                intervalList[endLocation].start = end + 1;
                intervalList[endLocation].end = intervalList[endLocation-1].end;
                intervalList[endLocation].value = controller.cloneCollection(
                        (C)intervalList[endLocation-1].value);
                
                // make old last interval smaller
                intervalList[endLocation-1].end = end;
            }
            
            addNewValue(startLocation,endLocation+1-(endInside ? 1 : 0),value);
            
            return true;
        }
        
        // start inside and end outside
        if (endOutside) {
            int count = getNonEmptyGapCount(startLocation,endLocation-1) +
                    (startInside ? 2 : 1);
            
            insertInternal(endLocation,count);
            
            distributeOldIntervals(startLocation,endLocation-1,count,true);
            
            endLocation += count - 1;
            
            // create new end interval based on old end interval
            intervalList[endLocation].start = intervalList[endLocation-1].end + 1;
            intervalList[endLocation].end = end;
            
            if (startInside) {
                
                // adjust first interval
                intervalList[startLocation].end = start - 1;
                intervalList[startLocation].value = controller.cloneCollection(
                        (C)intervalList[startLocation+1].value);
                
                // adjust second interval
                intervalList[startLocation+1].start = start;
            }
            
            addNewValue(startLocation+(startInside ? 1 : 0),endLocation+1,value);
            
            return true;
        }
        
        // start and end inside
        int count = getNonEmptyGapCount(startLocation,endLocation);
        
        if (startInside) {
            count++;
        }
        
        if (endInside) {
            count++;
        }
        
        if (count > 0) {
            insertInternal(endLocation+1,count);
            
            distributeOldIntervals(startLocation,endLocation,count,endInside);
            
            if (startInside) {
                
                // adjust first interval
                intervalList[startLocation].end = start - 1;
                intervalList[startLocation].value = controller.cloneCollection(
                        (C)intervalList[startLocation+1].value);
                
                // adjust second interval
                intervalList[startLocation+1].start = start;
            }
            
            endLocation += count;
            
            if (endInside) {
                
                // create new last interval based on old last
                intervalList[endLocation].start = end + 1;
                intervalList[endLocation].end = intervalList[endLocation-1].end;
                intervalList[endLocation].value = controller.cloneCollection(
                        (C)intervalList[endLocation-1].value);
                
                // make old last interval smaller
                intervalList[endLocation-1].end = end;
            }
            
            addNewValue(startLocation+(startInside ? 1 : 0),
                    endLocation+1-(endInside ? 1 : 0),value);
            
            return true;
        }
        
        return addNewValue(startLocation,endLocation+1,value);
    }
    
    /**
     * 
     */
    private int getNonEmptyGapCount(int firstIntervalIndex,
            int lastIntervalIndex) {
        
        int count = 0;
        
        for (int i=firstIntervalIndex;i<lastIntervalIndex;i++) {
            if ((intervalList[i+1].start - intervalList[i].end) > 1) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * 
     */
    private void distributeOldIntervals(int firstIntervalIndex,
            int lastIntervalIndex, int newIntervalCount, boolean skipLast) {
        
        int n = lastIntervalIndex + newIntervalCount;
        
        if (skipLast) {
            n--;
        }
        
        boolean lastHadGap = false;
        
        for (int o=lastIntervalIndex;o>=firstIntervalIndex;o--) {
            if (o != n) {
                intervalList[n].start = intervalList[o].start;
                intervalList[n].end = intervalList[o].end;
                intervalList[n].value = intervalList[o].value;
                intervalList[o].value = null;
            }
            
            if (lastHadGap) {
                intervalList[n+1].start = intervalList[n].end + 1;
                intervalList[n+1].end = intervalList[n+2].start - 1;
                intervalList[n+1].value = null;
            }
            
            lastHadGap = (o != firstIntervalIndex) &&
                    ((intervalList[o].start-intervalList[o-1].end) > 1);
            
            if (lastHadGap) {
                n -= 2;
            }
            
            else {
                n--;
            }
        }
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private boolean addNewValue(int start, int end, V value) {
        boolean modified = false;
        
        for (int i=start;i<end;i++) {
            C curCollection = (C)intervalList[i].value;
            
            if (curCollection == null) {
                curCollection = controller.createCollection();
                intervalList[i].value = curCollection;
            }
            
            if (controller.addValue(curCollection,value)) {
                modified = true;
            }
        }
        
        return modified;
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
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public boolean put(IntRangeMultiMap<? extends V, ? extends Iterable<? extends V>> multiTable) {
        if (multiTable == null) {
            throw new IllegalArgumentException("multi table may not be null");
        }
        
        boolean modified = false;
        int intervalCount = multiTable.intervalCount;
        
        for (int i=0;i<intervalCount;i++) {
            int start = multiTable.intervalList[i].start;
            int end = multiTable.intervalList[i].end;
            Iterable<? extends V> iterable = (Iterable<? extends V>)multiTable.intervalList[i].value;
            
            if (iterable == null) {
                throw new IllegalArgumentException("interval value may " +
                        "not be null");
            }
            
            Iterator<? extends V> iterator = iterable.iterator();
            
            if (iterator == null) {
                throw new IllegalArgumentException("iterator of interval " +
                        "value may not be null");
            }
            
            if (!iterator.hasNext()) {
                throw new IllegalArgumentException("iterable may not be " +
                        "empty");
            }
            
            do {
                if (put(start,end,iterator.next())) {
                    modified = true;
                }
            } while (iterator.hasNext());
        }
        
        return modified;
    }
    
    //======================
    // Value Remove Methods
    //======================
    
    /**
     * 
     */
    public boolean removeValue(int index, V value) {
        return removeValue(index,index,value);
    }
    
    /**
     * 
     */
    public boolean removeValue(int start, int end, V value) {
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater then end");
        }
        
        int location1 = locationOf(start);
        
        if (location1 < 0) {
            location1 = -(location1 + 1);
            
            if (location1 >= intervalCount) {
                return false;
            }
            
            start = intervalList[location1].start;
        }
        
        int location2 = locationOf(end);
        
        if (location2 < 0) {
            location2 = -(location2 + 1);
            
            if (location2 <= 0) {
                return false;
            }
            
            end = intervalList[location2-1].end;
        }
        
        return (location2 > location1) &&
               removeValueInternal(start,location1,end,location2,value);
    }
    
    /**
     * 
     */
    private boolean removeValueInternal(int start, int startIndex, int end,
            int endIndex, V value) {
        
        // TODO: Implement.
        throw new UnsupportedOperationException("method not implemented");
    }
    
    /**
     * 
     */
    public boolean removeValue(int[] indices, V value) {
        if (indices == null) {
            throw new IllegalArgumentException("indices may not be null");
        }
        
        if (hasContent()) {
            boolean modified = false;
            
            for (int i=0;i<indices.length;i++) {
                if (removeValue(indices[i],value)) {
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
    public boolean removeValue(int[][] spanList, V value) {
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
                    if (removeValue(span[0],value)) {
                        if (isEmpty()) {
                            return true;
                        }
                        
                        modified = true;
                    }
                }
                
                else if (span.length == 2) {
                    if (removeValue(span[0],span[1],value)) {
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
    public boolean removeValue(IntRangeCollection collection, V value) {
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
                if (removeValue(collection.intervalList[i].start,
                        collection.intervalList[i].end,value)) {
                    
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
    @SuppressWarnings("unchecked")
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
                            intervalList[insertionIndex].value = controller.cloneCollection(
                                    (C)intervalList[firstIndex].value);
                            
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
    
    //===============================
    // Interval Value Remove Methods
    //===============================
    
    /**
     * 
     */
    public boolean removeValueAt(int index, V value) {
        return removeValueBetween(index,index+1,value);
    }
    
    /**
     * 
     */
    public boolean removeValueBetween(int startIndex, int endIndex, V value) {
        if ((startIndex < 0) || (endIndex > intervalCount) ||
            (endIndex <= startIndex)) {
            
            throw new IllegalArgumentException("indices are out of range");
        }
        
        return removeValueInternal(intervalList[startIndex].start,startIndex,
                intervalList[endIndex-1].end,endIndex,value);
    }
    
    /**
     * 
     */
    public boolean clear(V value) {
        if (intervalCount > 0) {
            return removeValueBetween(0,intervalCount,value);
        }
        
        return false;
    }
    
    //=========================
    // Interval Remove Methods
    //=========================
    
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
    public ConstIntRangeMultiMap<V,C> toConstMultiMap() {
        if (intervalCount == 0) {
            return new ConstIntRangeMultiMap<>(controller);
        }
        
        return new ConstIntRangeMultiMap<>(this);
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
    public MutableIntRangeMultiMap<V,C> clone() {
        return new MutableIntRangeMultiMap<>(this);
    }
}
