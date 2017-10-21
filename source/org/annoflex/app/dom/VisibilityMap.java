/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

/**
 * @author Stefan Czaska
 */
public class VisibilityMap extends MemberMap<Visibility> {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public VisibilityMap() {
        super(Visibility.PRIVATE);
        
        putReaderMethods(Visibility.PUBLIC);
        putStringMethods(Visibility.PUBLIC);
        putRegionMethods(Visibility.PUBLIC);
        putDotMethods(Visibility.PUBLIC);
        putMatchMethods(Visibility.PUBLIC);
        putScanMethods(Visibility.PUBLIC);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public String getMemberName(Member member) {
        return get(member).getName();
    }
}
