/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class JOMModifierList extends JOMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMModifierList() {
        super(JOMNodeType.MODIFIER_LIST);
        
        setSourceRangeAuto(true);
    }
}
