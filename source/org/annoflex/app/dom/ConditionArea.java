/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

import org.annoflex.regex.Condition;
import org.annoflex.util.text.Section;

/**
 * @author Stefan Czaska
 */
public class ConditionArea {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Section section;
    
    /**
     * 
     */
    private final Condition condition;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ConditionArea(Section section, Condition condition) {
        if (section == null) {
            throw new IllegalArgumentException("section may not be null");
        }
        
        if (condition == null) {
            throw new IllegalArgumentException("condition may not be null");
        }
        
        this.section = section;
        this.condition = condition;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final Section getSection() {
        return section;
    }
    
    /**
     * 
     */
    public final Condition getCondition() {
        return condition;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[");
        buffer.append(section.getStart().start);
        buffer.append("-");
        buffer.append(section.getEnd().end);
        buffer.append(";");
        buffer.append(condition);
        buffer.append("]");
        
        return buffer.toString();
    }
}
