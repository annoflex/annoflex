/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.compiler;

import java.util.HashMap;

import org.annoflex.regex.Expression;

/**
 * @author Stefan Czaska
 */
@SuppressWarnings("serial")
public class DefaultMacroResolver extends HashMap<String,Expression>
        implements MacroResolver {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final MacroResolver parentResolver;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public DefaultMacroResolver() {
        parentResolver = null;
    }
    
    /**
     * 
     */
    public DefaultMacroResolver(MacroResolver parentResolver) {
        this.parentResolver = parentResolver;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public boolean containsMacro(String macro) {
        if (containsKey(macro)) {
            return true;
        }
        
        return parentResolver != null ? parentResolver.containsMacro(macro) : false;
    }
    
    /**
     * 
     */
    public Expression resolveMacro(String macro) {
        if (containsKey(macro)) {
            return get(macro);
        }
        
        return parentResolver != null ? parentResolver.resolveMacro(macro) : null;
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
        buffer.append("[this=");
        buffer.append(this);
        buffer.append(",parent=");
        buffer.append(parentResolver);
        buffer.append("]");
        
        return buffer.toString();
    }
}
