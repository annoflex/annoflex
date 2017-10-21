/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class JOMImport extends JOMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private boolean isStatic;
    
    /**
     * 
     */
    private boolean onDemand;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMImport() {
        super(JOMNodeType.IMPORT);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
    
    /**
     * 
     */
    public boolean getIsStatic() {
        return isStatic;
    }
    
    /**
     * 
     */
    public void setOnDemand(boolean onDemand) {
        this.onDemand = onDemand;
    }
    
    /**
     * 
     */
    public boolean getOnDemand() {
        return onDemand;
    }
    
    /**
     * 
     */
    public String getNameText() {
        JOMName name = (JOMName)getChildByType(JOMNodeType.NAME,false);
        
        return name != null ? name.getText() : null;
    }
    
    /**
     * 
     */
    public String[] getNameTextParts() {
        JOMName name = (JOMName)getChildByType(JOMNodeType.NAME,false);
        
        return name != null ? name.getTextParts() : null;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        StringBuilder builder = new StringBuilder();
        
        if (isStatic) {
            builder.append("static");
        }
        
        if (onDemand) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            
            builder.append("onDemand");
        }
        
        return builder.toString();
    }
}
