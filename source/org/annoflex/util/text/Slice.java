/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

/**
 * @author Stefan Czaska
 */
public class Slice {
    
    // TODO: Validate slice list.
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final Interval[] EMPTY_ARRAY = new Interval[0];
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String string;
    
    /**
     * 
     */
    private final Interval[] intervalList;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Slice(String src, Span[] sliceList, int origin, char delimiter) {
        if (src == null) {
            throw new IllegalArgumentException("src may not be null");
        }
        
        if (sliceList == null) {
            throw new IllegalArgumentException("slice list may not be null");
        }
        
        int srcLength = src.length();
        
        if (srcLength == 0) {
            string = "";
            intervalList = EMPTY_ARRAY;
        }
        
        else {
            string = createDestString(src,sliceList,delimiter);
            intervalList = createIntervalList(sliceList,origin);
        }
    }
    
    //==================
    // Creation Methods
    //==================
    
    /**
     * 
     */
    private Interval[] createIntervalList(Span[] sliceList, int origin) {
        Interval[] list = new Interval[sliceList.length];
        int destStart = 0;
        
        for (int i=0;i<sliceList.length;i++) {
            Span curSpan = sliceList[i];
            Interval curInterval = new Interval(curSpan.start-origin,
                    destStart,curSpan.length());
            destStart += curSpan.length();
            
            if (i != (sliceList.length - 1)) {
                destStart++;
            }
            
            list[i] = curInterval;
        }
        
        return list;
    }
    
    /**
     * 
     */
    private String createDestString(String src, Span[] sliceList, char delimiter) {
        StringBuilder buffer = new StringBuilder();
        
        for (int i=0;i<sliceList.length;i++) {
            Span curSpan = sliceList[i];
            buffer.append(src,curSpan.start,curSpan.end);
            
            if (i != (sliceList.length - 1)) {
                buffer.append(delimiter);
            }
        }
        
        return buffer.toString();
    }
    
    //===================
    // DestToSrc Methods
    //===================
    
    /**
     * 
     */
    public Span srcSpan(Span span) {
        return new Span(srcIndexBefore(span.start),srcIndexAfter(span.end-1));
    }
    
    /**
     * 
     */
    public int srcIndexBefore(int sliceIndex) {
        return srcIndexInternal(sliceIndex,false);
    }
    
    /**
     * 
     */
    public int srcIndexAfter(int sliceIndex) {
        return srcIndexInternal(sliceIndex,true);
    }
    
    /**
     * 
     */
    private int srcIndexInternal(int sliceIndex, boolean after) {
        int location = locationOfSliceIndex(sliceIndex);
        
        if (location >= 0) {
            Interval interval = intervalList[location];
            
            return interval.srcStart + sliceIndex - interval.destStart +
                    (after ? 1 : 0);
        }
        
        location = -(location + 1);
        
        if (location == 0) {
            return intervalList[0].srcStart;
        }
        
        if (location == intervalList.length) {
            return intervalList[location-1].srcEnd + 1;
        }
        
        return after ? intervalList[location].srcStart :
            intervalList[location-1].srcEnd + 1;
    }
    
    /**
     * 
     */
    private int locationOfSliceIndex(int sliceIndex) {
        Interval[] intervalList = this.intervalList;
        int start = 0;
        int end = intervalList.length - 1;
        
        while (start <= end) {
            int curIndex = (start + end) >>> 1;
            
            if (sliceIndex < intervalList[curIndex].destStart) {
                end = curIndex - 1;
            }
            
            else if (sliceIndex > intervalList[curIndex].destEnd) {
                start = curIndex + 1;
            }
            
            else {
                return curIndex;
            }
        }
        
        return -start - 1;
    }
    
    //================
    // String Methods
    //================
    
    /**
     * 
     */
    public final String getText() {
        return string;
    }
    
    /**
     * 
     */
    public Span trim() {
        return trim(0,string.length());
    }
    
    /**
     * 
     */
    public Span trim(int start, int end) {
        return trim(new Span(start,end));
    }
    
    /**
     * 
     */
    public Span trim(Span span) {
        return span != null ? span.trim(string) : null;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return string;
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class Interval {
        
        public final int srcStart;
        public final int srcEnd;
        public final int destStart;
        public final int destEnd;
        
        /**
         * 
         */
        public Interval(int srcStart, int destStart, int length) {
            this.srcStart = srcStart;
            this.srcEnd = srcStart + length - 1;
            this.destStart = destStart;
            this.destEnd = destStart + length - 1;
        }
    }
}
