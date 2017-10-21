/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

import java.util.ArrayList;
import java.util.List;

import org.annoflex.util.SystemToolkit;
import org.annoflex.util.text.Span;

/**
 * @author Stefan Czaska
 */
public class JOMComment extends JOMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private CommentType type;
    
    /**
     * 
     */
    private CommentValue value;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMComment() {
        super(JOMNodeType.COMMENT);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setType(CommentType type) {
        this.type = type;
    }
    
    /**
     * 
     */
    public CommentType getType() {
        return type;
    }
    
    /**
     * 
     */
    public boolean isEndOfLineComment() {
        return type == CommentType.END_OF_LINE;
    }
    
    /**
     * 
     */
    public boolean isTraditionalComment() {
        return type == CommentType.TRADITIONAL;
    }
    
    /**
     * 
     */
    public boolean isDocumentationComment() {
        return type == CommentType.DOCUMENTATION;
    }
    
    /**
     * 
     */
    public void setValue(CommentValue value) {
        this.value = value;
    }
    
    /**
     * 
     */
    public CommentValue getValue() {
        return value;
    }
    
    /**
     * 
     */
    public List<JOMTag> getTagsByName(String tagName) {
        List<JOMTag> result = null;
        JOMTag curTag = (JOMTag)getFirstChild();
        
        while (curTag != null) {
            String curName = curTag.getName();
            
            if (SystemToolkit.equals(curName,tagName)) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                
                result.add(curTag);
            }
            
            curTag = (JOMTag)curTag.getNextSibling();
        }
        
        return result;
    }
    
    /**
     * 
     */
    public Span getSourceRange(boolean content) {
        if (content && (value != null)) {
            return getSourceRange(value.start()-getSourceStart(),
                    value.end()-getSourceEnd());
        }
        
        return getSourceRange();
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        if (type != null) {
            switch(type) {
            case END_OF_LINE:   return "endOfLine";
            case TRADITIONAL:   return "traditional";
            case DOCUMENTATION: return "documentation";
            default:
                throw new IllegalStateException("invalid comment type");
            }
        }
        
        return null;
    }
}
