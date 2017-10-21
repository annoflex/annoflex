/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

/**
 * @author Stefan Czaska
 */
public class MutableIntArray extends IntArray {
    
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
    public MutableIntArray() {
    }
    
    /**
     * 
     */
    public MutableIntArray(int[] array) {
        super(array);
    }
    
    /**
     * 
     */
    public MutableIntArray(int[] array, int start, int end) {
        super(array,start,end);
    }
    
    /**
     * 
     */
    public MutableIntArray(IntArray array) {
        super(array);
    }
    
    /**
     * 
     */
    public MutableIntArray(IdSet set) {
        super(set);
    }
    
    //=============
    // Add Methods
    //=============
    
    /**
     * 
     */
    public void add(int value) {
        int oldLength = length;
        
        insertInternal(oldLength,1);
        
        array[oldLength] = value;
    }
    
    /**
     * 
     */
    public void add(int value, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count may not be negative");
        }
        
        if (count > 0) {
            int oldLength = length;
            
            insertInternal(oldLength,count);
            
            for (int i=0;i<count;i++) {
                array[oldLength+i] = value;
            }
        }
    }
    
    /**
     * 
     */
    public void add(int[] values) {
        insert(length,values);
    }
    
    /**
     * 
     */
    public void add(int[] values, int start, int end) {
        insert(length,values,start,end);
    }
    
    /**
     * 
     */
    public void add(IntArray array) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        insert(length,array.array);
    }
    
    /**
     * 
     */
    public void add(IntArray array, int start, int end) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        insert(length,array.array,start,end);
    }
    
    //================
    // Insert Methods
    //================
    
    /**
     * 
     */
    public void insert(int index, int value) {
        if ((index < 0) || (index > length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        insertInternal(index,1);
        
        array[index] = value;
    }
    
    /**
     * 
     */
    public void insert(int index, int[] values) {
        if ((index < 0) || (index > length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        if (values == null) {
            throw new IllegalArgumentException("values may not be null");
        }
        
        if (values.length != 0) {
            insertInternal(index,values.length);
            
            System.arraycopy(values,0,array,index,values.length);
        }
    }
    
    /**
     * 
     */
    public void insert(int index, int[] values, int start, int end) {
        if ((index < 0) || (index > length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        if (values == null) {
            throw new IllegalArgumentException("values may not be null");
        }
        
        if (start < 0) {
            throw new IllegalArgumentException("start out of bounds: "+start);
        }
        
        if (end > values.length) {
            throw new IllegalArgumentException("end out of bounds: "+end);
        }
        
        if (end < start) {
            throw new IllegalArgumentException("end may not be lower than start");
        }
        
        int length = end - start;
        
        if (length != 0) {
            insertInternal(index,length);
            
            System.arraycopy(values,start,array,index,length);
        }
    }
    
    /**
     * 
     */
    public void insert(int index, IntArray array) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        insert(index,array.array);
    }
    
    /**
     * 
     */
    public void insert(int index, IntArray array, int start, int end) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        insert(index,array.array,start,end);
    }
    
    /**
     * 
     */
    private void insertInternal(int index, int count) {
        int newIntervalCount = length + count;
        
        // empty or full array -> reallocate and copy if necessary
        if ((array == null) || (newIntervalCount > array.length)) {
            int newSize = computeNewListSize(newIntervalCount);
            int[] newArray = new int[newSize];
            
            if (length > 0) {
                if (index == length) {
                    System.arraycopy(array,0,newArray,0,length);
                }
                
                else if (index == 0) {
                    System.arraycopy(array,0,newArray,count,length);
                }
                
                else {
                    System.arraycopy(array,0,newArray,0,index);
                    System.arraycopy(array,index,newArray,index+count,
                            length-index);
                }
            }
            
            // set new array
            array = newArray;
        }
        
        // new array is not necessary -> just copy the data
        else if (index < length) {
            
            // copy array content
            System.arraycopy(array,index,array,index+count,length-index);
        }
        
        length += count;
    }
    
    //================
    // Remove Methods
    //================
    
    /**
     * 
     */
    public void removeAt(int index) {
        if ((index < 0) || (index >= length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        removeInternal(index,index+1);
    }
    
    /**
     * 
     */
    public void removeRange(int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException("start out of bounds: "+start);
        }
        
        if (end > length) {
            throw new IllegalArgumentException("end out of bounds: "+end);
        }
        
        if (end > start) {
            removeInternal(start,end);
        }
    }
    
    /**
     * 
     */
    private void removeInternal(int index1, int index2) {
        
        // partial deletion at the start or in the middle
        if (index2 < length) {
            int delta = index2 - index1;
            int newSize = length - delta;
            int refSize = array.length / SHRINK_RELATION;
            
            // unused area is large -> reallocate new array
            if ((newSize < refSize) && (array.length > MAX_REUSE_SIZE)) {
                
                // compute new list size
                int newListSize = computeNewListSize(newSize);
                
                // reallocate new array
                int[] newArray = new int[newListSize];
                
                // copy first part
                System.arraycopy(array,0,newArray,0,index1);
                
                // copy second part
                System.arraycopy(array,index2,newArray,index1,length-index2);
                
                // set new array
                array = newArray;
            }
            
            // unused area is small -> delete normally
            else {
                
                // copy array content
                System.arraycopy(array,index2,array,index1,length-index2);
            }
        }
        
        // full deletion
        else if (index1 == 0) {
            
            // Large arrays are thrown away in order not to waste memory.
            // Small arrays are reused in order to avoid expensive memory
            // allocations.
            if (array.length > MAX_REUSE_SIZE) {
                array = null;
            }
        }
        
        length -= index2 - index1;
    }
    
    /**
     * 
     */
    public boolean clear() {
        if (length > 0) {
            removeInternal(0,length);
            
            return true;
        }
        
        return false;
    }
    
    //=======================
    // Value Removal Methods
    //=======================
    
    /**
     * 
     */
    public boolean removeFirst(int value) {
        int index = indexOf(value);
        
        if (index != -1) {
            removeInternal(index,index+1);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean removeLast(int value) {
        int index = lastIndexOf(value);
        
        if (index != -1) {
            removeInternal(index,index+1);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     */
    public boolean removeAll(int value) {
        int end = lastIndexOf(value,length-1);
        
        if (end != -1) {
            int start = end;
            int next = lastIndexOf(value,start-1);
            
            while (next != -1) {
                if (next != (start-1)) {
                    removeInternal(start,end+1);
                    
                    end = next;
                }
                
                start = next;
                next = lastIndexOf(value,start-1);
            }
            
            removeInternal(start,end+1);
            
            return true;
        }
        
        return false;
    }
    
    //===================
    // Value Set Methods
    //===================
    
    /**
     * 
     */
    public void set(int index, int value) {
        if ((index < 0) || (index >= length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        array[index] = value;
    }
    
    /**
     * 
     */
    public void set(int index, int[] values) {
        if ((index < 0) || (index >= length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        if (values == null) {
            throw new IllegalArgumentException("values may not be null");
        }
        
        System.arraycopy(values,0,array,index,values.length);
    }
    
    /**
     * 
     */
    public void set(int index, int[] values, int start, int end) {
        if ((index < 0) || (index >= length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        if (values == null) {
            throw new IllegalArgumentException("values may not be null");
        }
        
        if (start < 0) {
            throw new IllegalArgumentException("start out of bounds: "+start);
        }
        
        if (end > values.length) {
            throw new IllegalArgumentException("end out of bounds: "+end);
        }
        
        System.arraycopy(values,start,array,index,end-start);
    }
    
    /**
     * 
     */
    public void set(int index, IntArray array) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        set(index,array.array);
    }
    
    /**
     * 
     */
    public void set(int index, IntArray array, int start, int end) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        set(index,array.array,start,end);
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public ConstIntArray toConstArray() {
        if (length == 0) {
            return ConstIntArray.EMPTY_ARRAY;
        }
        
        return new ConstIntArray(this);
    }
    
    //===========================
    // Memory Management Methods
    //===========================
    
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
    public MutableIntArray clone() {
        return new MutableIntArray(this);
    }
}
