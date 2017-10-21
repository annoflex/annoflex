/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.problem;

/**
 * @author Stefan Czaska
 */
public interface ErrorHandler<C> {
    
    /**
     * 
     */
    public Handling lookupHandling(Problem problem);
    
    /**
     * 
     */
    public void handleError(Problem problem, C context);
    
    /**
     * 
     */
    public void handleWarning(Problem problem, C context);
}
