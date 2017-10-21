/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMMacroExpr extends ROMSimpleExpr {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMMacroExpr(ROMNode macro) {
        super(ROMNodeType.MACRO_EXPR);
        
        appendChild(macro);
    }
}
