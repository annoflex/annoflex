/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.compiler;

import org.annoflex.regex.Expression;

/**
 * @author Stefan Czaska
 */
public interface MacroResolver {
    
    /**
     * 
     */
    public boolean containsMacro(String macro);
    
    /**
     * 
     */
    public Expression resolveMacro(String macro);
}
