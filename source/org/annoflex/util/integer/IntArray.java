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
public class IntArray implements IntCollection, Comparable<IntArray> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    int length;
    
    /**
     * 
     */
    int[] array;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    IntArray() {
    }
    
    /**
     * 
     */
    IntArray(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        length = array.length;
        
        if (length != 0) {
            this.array = array.clone();
        }
    }
    
    /**
     * 
     */
    IntArray(int[] array, int start, int end) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        if (start < 0) {
            throw new IllegalArgumentException("start out of bounds: "+start);
        }
        
        if (end > array.length) {
            throw new IllegalArgumentException("end out of bounds: "+end);
        }
        
        if (end < start) {
            throw new IllegalArgumentException("end may not be lower than start");
        }
        
        length = end - start;
        
        if (length != 0) {
            this.array = new int[length];
            
            System.arraycopy(array,start,this.array,0,length);
        }
    }
    
    /**
     * 
     */
    IntArray(IntArray array) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }
        
        length = array.length;
        
        if (length != 0) {
            this.array = new int[length];
            
            System.arraycopy(array.array,0,this.array,0,length);
        }
    }
    
    /**
     * 
     */
    IntArray(IdSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set may not be null");
        }
        
        if (set.hasContent()) {
            int[] array = new int[set.size()];
            int iterator = set.first();
            int i = 0;
            
            do {
                array[i++] = iterator;
                iterator = set.next(iterator);
            } while (iterator != -1);
            
            this.array = array;
            this.length = array.length;
        }
    }
    
    //================
    // Length Methods
    //================
    
    /**
     * 
     */
    public final int size() {
        return length;
    }
    
    /**
     * 
     */
    public final boolean isEmpty() {
        return length == 0;
    }
    
    /**
     * 
     */
    public final boolean hasContent() {
        return length != 0;
    }
    
    //===============
    // Value Methods
    //===============
    
    /**
     * 
     */
    public final int get(int index) {
        if ((index < 0) || (index >= length)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        return array[index];
    }
    
    //===============
    // Index Methods
    //===============
    
    /**
     * 
     */
    public int indexOf(int value) {
        for (int i=0;i<length;i++) {
            if (array[i] == value) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     */
    public int indexOf(int value, int index) {
        if (index < 0) {
            index = 0;
        }
        
        for (int i=index;i<length;i++) {
            if (array[i] == value) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     */
    public int lastIndexOf(int value) {
        for (int i=length-1;i>=0;i--) {
            if (array[i] == value) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     */
    public int lastIndexOf(int value, int index) {
        if (index >= length) {
            index = length - 1;
        }
        
        for (int i=index;i>=0;i--) {
            if (array[i] == value) {
                return i;
            }
        }
        
        return -1;
    }
    
    //==================
    // Contains Methods
    //==================
    
    /**
     * 
     */
    public boolean contains(int value) {
        for (int i=0;i<length;i++) {
            if (array[i] == value) {
                return true;
            }
        }
        
        return false;
    }
    
    //====================
    // Comparable Methods
    //====================
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(IntArray o) {
        if (o.length != length) {
            return length - o.length;
        }
        
        for (int i=0;i<length;i++) {
            if (o.array[i] != array[i]) {
                return array[i] - o.array[i];
            }
        }
        
        return 0;
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public int[] toArray() {
        if (length != 0) {
            int[] newArray = new int[length];
            
            System.arraycopy(array,0,newArray,0,length);
            
            return newArray;
        }
        
        return SystemToolkit.EMPTY_INT_ARRAY;
    }
    
    /**
     * 
     */
    public byte[] toByteArray() {
        if (length != 0) {
            byte[] newArray = new byte[length];
            
            for (int i=0;i<newArray.length;i++) {
                newArray[i] = (byte)array[i];
            }
            
            return newArray;
        }
        
        return SystemToolkit.EMPTY_BYTE_ARRAY;
    }
    
    /**
     * 
     */
    public short[] toShortArray() {
        if (length != 0) {
            short[] newArray = new short[length];
            
            for (int i=0;i<newArray.length;i++) {
                newArray[i] = (short)array[i];
            }
            
            return newArray;
        }
        
        return SystemToolkit.EMPTY_SHORT_ARRAY;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof IntArray) {
            IntArray array = (IntArray)obj;
            
            if (array.length != length) {
                return false;
            }
            
            for (int i=0;i<length;i++) {
                if (array.array[i] != this.array[i]) {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = length;
        
        for (int i=0;i<length;i++) {
            hash = hash * 31 + array[i];
        }
        
        return hash == 0 ? 1 : hash;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[length=");
        buffer.append(length);
        buffer.append(",values=[");
        
        for (int i=0;i<length;i++) {
            buffer.append(array[i]);
            
            if (i < (length - 1)) {
                buffer.append(",");
            }
        }
        
        buffer.append("]]");
        
        return buffer.toString();
    }
}
