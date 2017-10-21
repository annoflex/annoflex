/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

import org.annoflex.util.Node;
import org.annoflex.util.text.Span;

/**
 * @author Stefan Czaska
 */
public class JOMNode extends Node<JOMNode> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final JOMNodeType nodeType;
    
    /**
     * 
     */
    private int sourceStart = -1;
    
    /**
     * 
     */
    private int sourceEnd = -1;
    
    /**
     * 
     */
    private boolean sourceRangeAuto;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMNode(JOMNodeType nodeType) {
        if (nodeType == null) {
            throw new IllegalArgumentException("node type may not be null");
        }
        
        this.nodeType = nodeType;
    }
    
    //==============
    // Type Methods
    //==============
    
    /**
     * 
     */
    public final JOMNodeType getNodeType() {
        return nodeType;
    }
    
    /**
     * 
     */
    public final boolean isCompilationUnit() {
        return nodeType == JOMNodeType.COMPILATION_UNIT;
    }
    
    /**
     * 
     */
    public final boolean isPackage() {
        return nodeType == JOMNodeType.PACKAGE;
    }
    
    /**
     * 
     */
    public final boolean isImport() {
        return nodeType == JOMNodeType.IMPORT;
    }
    
    /**
     * 
     */
    public final boolean isTypeDeclaration() {
        return nodeType == JOMNodeType.TYPE_DECLARATION;
    }
    
    /**
     * 
     */
    public final boolean isTypeDeclarationBody() {
        return nodeType == JOMNodeType.TYPE_DECLARATION_BODY;
    }
    
    /**
     * 
     */
    public final boolean isMethodDeclaration() {
        return nodeType == JOMNodeType.METHOD_DECLARATION;
    }
    
    /**
     * 
     */
    public final boolean isCommentList() {
        return nodeType == JOMNodeType.COMMENT_LIST;
    }
    
    /**
     * 
     */
    public final boolean isComment() {
        return nodeType == JOMNodeType.COMMENT;
    }
    
    /**
     * 
     */
    public final boolean isTag() {
        return nodeType == JOMNodeType.TAG;
    }
    
    /**
     * 
     */
    public final boolean isModifierList() {
        return nodeType == JOMNodeType.MODIFIER_LIST;
    }
    
    /**
     * 
     */
    public final boolean isModifier() {
        return nodeType == JOMNodeType.MODIFIER;
    }
    
    /**
     * 
     */
    public final boolean isType() {
        return nodeType == JOMNodeType.TYPE;
    }
    
    /**
     * 
     */
    public final boolean isName() {
        return nodeType == JOMNodeType.NAME;
    }
    
    /**
     * 
     */
    public final boolean isNamePart() {
        return nodeType == JOMNodeType.NAME_PART;
    }
    
    //======================
    // Source Range Methods
    //======================
    
    /**
     * 
     */
    public void setSourceRange(int start, int end) {
        if (sourceRangeAuto) {
            throw new IllegalArgumentException("source range is auto");
        }
        
        if (!(((start == -1) && (end == -1)) ||
             ((start != -1) && (end != -1) && (start < end)))) {
            
            throw new IllegalArgumentException("invalid source range: "
                    + "start="+start+", end="+end);
        }
        
        sourceStart = start;
        sourceEnd = end;
    }
    
    /**
     * 
     */
    public final Span getSourceRange() {
        return getSourceRange(0,0);
    }
    
    /**
     * 
     */
    public final Span getSourceRange(int startDelta, int endDelta) {
        int sourceStart = getSourceStart();
        
        if (sourceStart != -1) {
            int sourceEnd = getSourceEnd();
            
            if (sourceEnd != -1) {
                return new Span(sourceStart+startDelta,sourceEnd-endDelta);
            }
        }
        
        return null;
    }
    
    /**
     * 
     */
    public final int getSourceStart() {
        if (sourceRangeAuto) {
            return hasChildren() ? getFirstChild().getSourceStart() : -1;
        }
        
        return sourceStart;
    }
    
    /**
     * 
     */
    public final int getSourceEnd() {
        if (sourceRangeAuto) {
            return hasChildren() ? getLastChild().getSourceEnd() : -1;
        }
        
        return sourceEnd;
    }
    
    /**
     * 
     */
    public final int getSourceLength() {
        int sourceStart = getSourceStart();
        
        if (sourceStart != -1) {
            int sourceEnd = getSourceEnd();
            
            if (sourceEnd != -1) {
                return sourceEnd - sourceStart;
            }
        }
        
        return 0;
    }
    
    /**
     * 
     */
    public void setSourceRangeAuto(boolean enabled) {
        sourceRangeAuto = enabled;
        sourceStart = -1;
        sourceEnd = -1;
    }
    
    /**
     * 
     */
    public final boolean isSourceRangeAuto() {
        return sourceRangeAuto;
    }
    
    //========================
    // Type Iteration Methods
    //========================
    
    /**
     * 
     */
    public JOMNode getChildByType(JOMNodeType type, boolean last) {
        JOMNode iterator = getChild(last);
        
        while (iterator != null) {
            if (iterator.getNodeType() == type) {
                return iterator;
            }
            
            iterator = iterator.getSibling(last);
        }
        
        return null;
    }
    
    /**
     * 
     */
    public JOMNode getSiblingByType(JOMNodeType type, boolean previous) {
        JOMNode iterator = getSibling(previous);
        
        while (iterator != null) {
            if (iterator.getNodeType() == type) {
                return iterator;
            }
            
            iterator = iterator.getSibling(previous);
        }
        
        return null;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(getClass().getSimpleName());
        
        String description = toStringInternal();
        
        if ((description != null) && !description.isEmpty()) {
            builder.append('[');
            builder.append(description);
            builder.append(']');
        }
        
        return builder.toString();
    }
    
    /**
     * 
     */
    protected String toStringInternal() {
        return null;
    }
}
