/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;


/**
 * @author Stefan Czaska
 */
public class ROMSequenceRef extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final SequenceRef sequenceRef;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMSequenceRef(SequenceRef sequenceRef) {
        super(ROMNodeType.SEQUENCE_REF);
        
        if (sequenceRef == null) {
            throw new IllegalArgumentException("sequence ref may not be null");
        }
        
        this.sequenceRef = sequenceRef;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final SequenceRef getSequenceRef() {
        return sequenceRef;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return sequenceRef.toString();
    }
}
