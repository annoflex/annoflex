/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

import org.annoflex.util.Node;
import org.annoflex.util.text.Span;

/**
 * @author Stefan Czaska
 */
public class ROMNode extends Node<ROMNode> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final ROMNodeType nodeType;
    
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
    public ROMNode(ROMNodeType nodeType) {
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
    public final ROMNodeType getNodeType() {
        return nodeType;
    }
    
    /**
     * 
     */
    public final boolean isCompilationUnit() {
        return nodeType == ROMNodeType.COMPILATION_UNIT;
    }
    
    /**
     * 
     */
    public final boolean isRootAlternation() {
        return nodeType == ROMNodeType.ROOT_ALTERNATION;
    }
    
    /**
     * 
     */
    public final boolean isRootElement() {
        return nodeType == ROMNodeType.ROOT_ELEMENT;
    }
    
    /**
     * 
     */
    public final boolean isLookaroundExpr() {
        return nodeType == ROMNodeType.LOOKAROUND_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isLookbefore() {
        return nodeType == ROMNodeType.LOOKBEFORE;
    }
    
    /**
     * 
     */
    public final boolean isLookafter() {
        return nodeType == ROMNodeType.LOOKAFTER;
    }
    
    /**
     * 
     */
    public boolean isAlternationExpr() {
        return nodeType == ROMNodeType.ALTERNATION_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isConcatenationExpr() {
        return nodeType == ROMNodeType.CONCATENATION_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isModifierExpr() {
        return nodeType == ROMNodeType.MODIFIER_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isQuantifierExpr() {
        return nodeType == ROMNodeType.QUANTIFIER_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isCharExpr() {
        return nodeType == ROMNodeType.CHAR_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isClassExpr() {
        return nodeType == ROMNodeType.CLASS_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isSequenceExpr() {
        return nodeType == ROMNodeType.SEQUENCE_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isMacroExpr() {
        return nodeType == ROMNodeType.MACRO_EXPR;
    }
    
    /**
     * 
     */
    public final boolean isCharacterClass() {
        return nodeType == ROMNodeType.CHARACTER_CLASS;
    }
    
    /**
     * 
     */
    public final boolean isCCSequence() {
        return nodeType == ROMNodeType.CC_SEQUENCE;
    }
    
    /**
     * 
     */
    public final boolean isCCOperator() {
        return nodeType == ROMNodeType.CC_OPERATOR;
    }
    
    /**
     * 
     */
    public final boolean isCCRange() {
        return nodeType == ROMNodeType.CC_RANGE;
    }
    
    /**
     * 
     */
    public final boolean isStringSequence() {
        return nodeType == ROMNodeType.STRING_SEQUENCE;
    }
    
    /**
     * 
     */
    public final boolean isCondition() {
        return nodeType == ROMNodeType.CONDITION;
    }
    
    /**
     * 
     */
    public final boolean isNameList() {
        return nodeType == ROMNodeType.NAME_LIST;
    }
    
    /**
     * 
     */
    public final boolean isMacro() {
        return nodeType == ROMNodeType.MACRO;
    }
    
    /**
     * 
     */
    public final boolean isCharRef() {
        return nodeType == ROMNodeType.CHAR_REF;
    }
    
    /**
     * 
     */
    public final boolean isClassRef() {
        return nodeType == ROMNodeType.CLASS_REF;
    }
    
    /**
     * 
     */
    public final boolean isSequenceRef() {
        return nodeType == ROMNodeType.SEQUENCE_REF;
    }
    
    /**
     * 
     */
    public final boolean isName() {
        return nodeType == ROMNodeType.NAME;
    }
    
    /**
     * 
     */
    public final boolean isModifier() {
        return nodeType == ROMNodeType.MODIFIER;
    }
    
    /**
     * 
     */
    public final boolean isQuantifier() {
        return nodeType == ROMNodeType.QUANTIFIER;
    }
    
    /**
     * 
     */
    public final boolean isSimpleExpr() {
        switch(nodeType) {
        case ALTERNATION_EXPR:
        case CONCATENATION_EXPR:
        case MODIFIER_EXPR:
        case QUANTIFIER_EXPR:
        case CHAR_EXPR:
        case CLASS_EXPR:
        case SEQUENCE_EXPR:
        case MACRO_EXPR:
            return true;
        
        default:
            return false;
        }
    }
    
    //======================
    // Source Range Methods
    //======================
    
    /**
     * 
     */
    public void setSourceRange(int start, int end) {
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
        int sourceStart = getSourceStart();
        
        if (sourceStart != -1) {
            int sourceEnd = getSourceEnd();
            
            if (sourceEnd != -1) {
                return new Span(sourceStart,sourceEnd);
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
    }
    
    /**
     * 
     */
    public final boolean isSourceRangeAuto() {
        return sourceRangeAuto;
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
