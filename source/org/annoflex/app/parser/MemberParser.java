/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.parser;

import org.annoflex.app.Problems;
import org.annoflex.app.dom.Member;
import org.annoflex.app.dom.MemberGroup;
import org.annoflex.app.dom.MemberMap;
import org.annoflex.jdt.dom.JOMTag;
import org.annoflex.util.problem.ErrorHandler;
import org.annoflex.util.text.Span;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class MemberParser<V> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final char[] characters;
    
    /**
     * 
     */
    private final V[] values;
    
    /**
     * 
     */
    private ErrorHandler<Span> errorHandler;
    
    /**
     * 
     */
    private boolean errors;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public MemberParser(char[] characters, V[] values) {
        this.characters = characters;
        this.values = values;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setErrorHandler(ErrorHandler<Span> errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    /**
     * 
     */
    public ErrorHandler<Span> getErrorHandler() {
        return errorHandler;
    }
    
    /**
     * 
     */
    public boolean hasErrors() {
        return errors;
    }
    
    /**
     * 
     */
    public void putValues(MemberMap<V> map, JOMTag tag, String value,
            Span valueSpan) {
        
        errors = false;
        
        Span[] subSpans = StringToolkit.split(value,StringToolkit.WHITESPACE);
        
        for (int i=0;i<subSpans.length;i++) {
            putValue(map,subSpans[i],value,tag,valueSpan);
        }
    }
    
    /**
     * 
     */
    private void putValue(MemberMap<V> map, Span subSpan, String valueText, JOMTag tag,
            Span valueSpan) {
        
        String text = subSpan.substring(valueText);
        
        if (text.length() < 2) {
            errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                    tag.getSliceSourceRange(subSpan.makeAbsoluteTo(valueSpan)));
        }
        
        else {
            V value = getValue(text.charAt(text.length()-1));
            
            if (value == null) {
                errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                        tag.getSliceSourceRange(subSpan.makeAbsoluteTo(valueSpan)));
            }
            
            else {
                String name = text.substring(0,text.length()-1);
                MemberGroup memberGroup = MemberGroup.forName(name);
                
                if (memberGroup != null) {
                    map.putGroup(memberGroup,value);
                }
                
                else {
                    Member member = Member.forName(name);
                    
                    if (member != null) {
                        map.put(member,value);
                    }
                    
                    else {
                        errors |= Problems.INVALID_OPTION_VALUE.report(errorHandler,
                                tag.getSliceSourceRange(subSpan.makeAbsoluteTo(valueSpan)));
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    private V getValue(char character) {
        for (int i=0;i<characters.length;i++) {
            if (characters[i] == character) {
                return values[i];
            }
        }
        
        return null;
    }
}
