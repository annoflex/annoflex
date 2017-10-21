/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.problem;

import org.annoflex.util.Message;

/**
 * @author Stefan Czaska
 */
public class Problem extends Message {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final boolean strictError;
    
    /**
     * 
     */
    private final Handling defaultHandling;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Problem(String mode, Class<?> anchorClass, String id) {
        this(mode,anchorClass,id,"problem");
    }
    
    /**
     * 
     */
    public Problem(String mode, Class<?> anchorClass, String id,
            String bundleSuffix) {
        
        super(anchorClass,id,bundleSuffix);
        
        if (mode == null) {
            throw new IllegalArgumentException("mode may not be null");
        }
        
        switch(mode) {
        case "e":
            strictError = false;
            defaultHandling = Handling.ERROR;
            break;
        
        case "w":
            strictError = false;
            defaultHandling = Handling.WARNING;
            break;
        
        case "i":
            strictError = false;
            defaultHandling = Handling.IGNORE;
            break;
        
        case "s":
            strictError = true;
            defaultHandling = Handling.ERROR;
            break;
        
        default:
            throw new IllegalArgumentException("invalid mode");
        }
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public final boolean isStrictError() {
        return strictError;
    }
    
    /**
     * 
     */
    public final Handling getDefaultHandling() {
        return defaultHandling;
    }
    
    //==================
    // Handling Methods
    //==================
    
    /**
     * 
     */
    public <C> boolean report(ErrorHandler<C> handler) {
        return report(handler,null);
    }
    
    /**
     * 
     */
    public <C> boolean report(ErrorHandler<C> handler, C context) {
        Handling handling;
        
        if (strictError || (handler == null)) {
            handling = defaultHandling;
        }
        
        else {
            handling = handler.lookupHandling(this);
            
            if (handling == null) {
                handling = defaultHandling;
            }
        }
        
        switch(handling) {
        case ERROR:
            if (handler != null) {
                handler.handleError(this,context);
            }
            return true;
        
        case WARNING:
            if (handler != null) {
                handler.handleWarning(this,context);
            }
            return false;
        
        default:
            return false;
        }
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
        builder.append("[");
        builder.append(getAnchorClass().getPackage().getName());
        builder.append(".");
        builder.append(getId());
        builder.append(", strict=");
        builder.append(strictError);
        builder.append(", handling=");
        builder.append(defaultHandling);
        builder.append("]");
        
        return builder.toString();
    }
}
