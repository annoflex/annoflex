/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author Stefan Czaska
 */
public class CommandQueue {
    
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
    private TreeSet<Replacement> sortedList;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public CommandQueue(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        this.string = string;
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public final String getString() {
        return string;
    }
    
    //===================
    // Operation Methods
    //===================
    
    /**
     * 
     */
    public void insertion(int index, String string) {
        replacement(index,index,string);
    }
    
    /**
     * 
     */
    public void deletion(int start, int end) {
        replacement(start,end,"");
    }
    
    /**
     * 
     */
    public void replacement(int start, int end, String string) {
        checkParams(start,end,string);
        
        if ((start == end) && string.isEmpty()) {
            return;
        }
        
        addToList(new Replacement(nextId(),start,end,string,null,null));
    }
    
    /**
     * 
     */
    public void replacement(int start, int end, String string,
            Pattern pattern, String replacement) {
        
        checkParams(start,end,string);
        
        if (pattern == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        if (replacement == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        if ((start == end) && string.isEmpty()) {
            return;
        }
        
        addToList(new Replacement(nextId(),start,end,string,pattern,replacement));
    }
    
    /**
     * 
     */
    private void checkParams(int start, int end, String string) {
        if (start < 0) {
            throw new IllegalArgumentException("start may not be negative");
        }
        
        if (end > this.string.length()) {
            throw new IllegalArgumentException("end may not be greater than "
                    + "length of original string");
        }
        
        if (start > end) {
            throw new IllegalArgumentException("start may not be greater than end");
        }
        
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
    }
    
    /**
     * 
     */
    private int nextId() {
        return sortedList != null ? sortedList.size() : 0;
    }
    
    /**
     * 
     */
    private void addToList(Replacement replacement) {
        if (sortedList == null) {
            sortedList = new TreeSet<>();
        }
        
        else {
            Replacement prevReplacement = sortedList.floor(replacement);
            
            if ((prevReplacement != null) && (replacement.start < prevReplacement.end)) {
                throw new IllegalArgumentException("operations may not overlap");
            }
            
            Replacement nextReplacement = sortedList.ceiling(replacement);
            
            if ((nextReplacement != null) && (replacement.end > nextReplacement.start)) {
                throw new IllegalArgumentException("operations may not overlap");
            }
        }
        
        sortedList.add(replacement);
    }
    
    //=======================
    // String Change Methods
    //=======================
    
    /**
     * 
     */
    public String applyCommands() {
        if (sortedList == null) {
            return string;
        }
        
        Iterator<Replacement> iterator = sortedList.iterator();
        StringBuilder builder = new StringBuilder();
        int curPosition = 0;
        
        while (iterator.hasNext()) {
            Replacement replacement = iterator.next();
            
            builder.append(string,curPosition,replacement.start);
            
            if (replacement.pattern != null) {
                StringToolkit.appendReplacement(builder,replacement.string,
                        replacement.pattern,replacement.replacement);
            }
            
            else {
                builder.append(replacement.string);
            }
            
            curPosition = replacement.end;
        }
        
        builder.append(string,curPosition,string.length());
        
        return builder.toString();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class Replacement implements Comparable<Replacement> {
        
        public final int id;
        public final int start;
        public final int end;
        public final String string;
        public final Pattern pattern;
        public final String replacement;
        
        /**
         * 
         */
        public Replacement(int id, int start, int end, String string,
                Pattern pattern, String replacement) {
            
            this.id = id;
            this.start = start;
            this.end = end;
            this.string = string;
            this.pattern = pattern;
            this.replacement = replacement;
        }
        
        /**
         * {@inheritDoc}
         */
        public int compareTo(Replacement o) {
            if (o.start != start) {
                return start - o.start;
            }
            
            if (o.end != end) {
                return end - o.end;
            }
            
            return id - o.id;
        }
    }
}
