/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

import java.util.ArrayList;

import org.annoflex.util.SystemToolkit;

/**
 * @author Stefan Czaska
 */
public class JOMName extends JOMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMName() {
        super(JOMNodeType.NAME);
        
        setSourceRangeAuto(true);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public String getText() {
        StringBuilder builder = new StringBuilder();
        JOMNode iterator = getFirstChild();
        
        while (iterator != null) {
            if (!iterator.isNamePart()) {
                throw new IllegalArgumentException("name list has invalid children");
            }
            
            builder.append(((JOMNamePart)iterator).getText());
            
            if (iterator.hasNextSibling()) {
                builder.append(".");
            }
            
            iterator = iterator.getNextSibling();
        }
        
        return builder.toString();
    }
    
    /**
     * 
     */
    public String[] getTextParts() {
        ArrayList<String> nameParts = new ArrayList<>();
        JOMNode iterator = getFirstChild();
        
        while (iterator != null) {
            if (!iterator.isNamePart()) {
                throw new IllegalArgumentException("name list has invalid children");
            }
            
            nameParts.add(((JOMNamePart)iterator).getText());
            iterator = iterator.getNextSibling();
        }
        
        return nameParts.toArray(SystemToolkit.EMPTY_STRING_ARRAY);
    }
}
