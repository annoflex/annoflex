/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.problem;

/**
 * @author Stefan Czaska
 */
public abstract class DefaultErrorHandler<S,D> implements ErrorHandler<S> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    protected final ErrorHandler<D> handler;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public DefaultErrorHandler(ErrorHandler<D> handler) {
        this.handler = handler;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public Handling lookupHandling(Problem problem) {
        return handler != null ? handler.lookupHandling(problem) : null;
    }
    
    /**
     * 
     */
    public void handleError(Problem problem, S context) {
        if (handler != null) {
            handler.handleError(problem,convertContext(context));
        }
    }
    
    /**
     * 
     */
    public void handleWarning(Problem problem, S context) {
        if (handler != null) {
            handler.handleWarning(problem,convertContext(context));
        }
    }
    
    /**
     * 
     */
    protected abstract D convertContext(S context);
}
