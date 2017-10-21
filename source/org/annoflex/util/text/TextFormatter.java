/*
 * AnnoFlex - A code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import org.annoflex.util.SystemToolkit;

/**
 * @author Stefan Czaska
 */
public class TextFormatter {
    
    //=================
    // Property Fields
    //=================
    
    /**
     * 
     */
    private String text = "";
    
    /**
     * 
     */
    private Locale locale = Locale.getDefault();
    
    /**
     * 
     */
    private int lineLength = 80;
    
    //==============
    // State Fields
    //==============
    
    /**
     * 
     */
    private ArrayList<Line> lineList;
    
    /**
     * 
     */
    private BreakIterator breakIterator;
    
    /**
     * 
     */
    private Locale breakIteratorLocale;
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text may not be null");
        }
        
        this.text = text;
    }
    
    /**
     * 
     */
    public final String getText() {
        return text;
    }
    
    /**
     * 
     */
    public void setLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale may not be null");
        }
        
        this.locale = locale;
    }
    
    /**
     * 
     */
    public final Locale getLocale() {
        return locale;
    }
    
    /**
     * 
     */
    public void setLineLength(int lineLength) {
        if (lineLength < 1) {
            throw new IllegalArgumentException("line length may not be lower than one");
        }
        
        this.lineLength = lineLength;
    }
    
    /**
     * 
     */
    public final int getLineLength() {
        return lineLength;
    }
    
    //================
    // Layout Methods
    //================
    
    /**
     * 
     */
    public String[] computeLines() {
        lineList = new ArrayList<>();
        
        layoutFragments();
        trimLineEnds();
        
        return extractLines();
    }
    
    /**
     * 
     */
    private void layoutFragments() {
        BreakIterator breakIterator = createBreakIterator();
        breakIterator.setText(text);
        
        int fragmentStart = 0;
        int fragmentEnd = breakIterator.first();
        
        if (fragmentEnd == 0) {
            fragmentEnd = breakIterator.next();
        }
        
        while (fragmentEnd != BreakIterator.DONE) {
            layoutFragment(fragmentStart,fragmentEnd);
            fragmentStart = fragmentEnd;
            fragmentEnd = breakIterator.next();
        }
        
        if (fragmentStart < text.length()) {
            layoutFragment(fragmentStart,text.length());
        }
    }
    
    /**
     * 
     */
    private void layoutFragment(int start, int end) {
        int lineCount = lineList.size();
        
        if (lineCount == 0) {
            layoutIntoNewLine(start,end);
        }
        
        else {
            Line lastLine = lineList.get(lineCount-1);
            int lastLineLength = lastLine.end - lastLine.start;
            
            if (lastLineLength >= lineLength) {
                layoutIntoNewLine(start,end);
            }
            
            else {
                int fragmentLength = end - start;
                int totalLength = lastLineLength + fragmentLength;
                
                if (totalLength <= lineLength) {
                    lastLine.end = end;
                }
                
                else {
                    int maxEnd = end - (totalLength - lineLength);
                    int newEnd = StringToolkit.skip(text,end,maxEnd,StringToolkit.WHITESPACE,true);
                    
                    if (newEnd == maxEnd) {
                        lastLine.end = newEnd;
                    }
                    
                    else {
                        layoutIntoNewLine(start,end);
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    private void layoutIntoNewLine(int start, int end) {
        start = StringToolkit.skip(text,start,end,StringToolkit.WHITESPACE,false);
        
        if (start < end) {
            int fragmentLength = end - start;
            
            while (fragmentLength > lineLength) {
                Line newLine = new Line();
                newLine.start = start;
                newLine.end = start + lineLength;
                
                lineList.add(newLine);
                
                start += lineLength;
                fragmentLength -= lineLength;
            }
            
            int newEnd = StringToolkit.skip(text,end,start,StringToolkit.WHITESPACE,true);
            
            if (newEnd != start) {
                Line newLine = new Line();
                newLine.start = start;
                newLine.end = end;
                
                lineList.add(newLine);
            }
        }
    }
    
    /**
     * 
     */
    private void trimLineEnds() {
        int size = lineList.size();
        
        for (int i=0;i<size;i++) {
            Line curLine = lineList.get(i);
            
            curLine.end = StringToolkit.skip(text,curLine.end,curLine.start,
                    StringToolkit.WHITESPACE,true);
        }
    }
    
    /**
     * 
     */
    private String[] extractLines() {
        ArrayList<String> lineStrings = new ArrayList<>();
        int size = lineList.size();
        
        for (int i=0;i<size;i++) {
            Line curLine = lineList.get(i);
            lineStrings.add(text.substring(curLine.start,curLine.end));
        }
        
        return lineStrings.toArray(SystemToolkit.EMPTY_STRING_ARRAY);
    }
    
    //========================
    // Break Iterator Methods
    //========================
    
    /**
     * 
     */
    private BreakIterator createBreakIterator() {
        if ((breakIterator == null) || (breakIteratorLocale != locale)) {
            breakIterator = BreakIterator.getLineInstance(locale);
            breakIteratorLocale = locale;
        }
        
        return breakIterator;
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class Line {
        
        public int start;
        public int end;
    }
}
