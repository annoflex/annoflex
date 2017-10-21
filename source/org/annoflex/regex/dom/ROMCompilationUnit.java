/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMCompilationUnit extends ROMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMCompilationUnit() {
        super(ROMNodeType.COMPILATION_UNIT);
    }
    
    /**
     * 
     */
    public ROMCompilationUnit(ROMNode rootAlternation) {
        super(ROMNodeType.COMPILATION_UNIT);
        
        appendChild(rootAlternation);
    }
    
    /**
     * 
     */
    public ROMCompilationUnit(ROMNode condition, ROMNode rootAlternation) {
        super(ROMNodeType.COMPILATION_UNIT);
        
        appendChild(condition);
        appendChild(rootAlternation);
    }
}
