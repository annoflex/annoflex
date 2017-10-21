/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import org.annoflex.util.BundleCache;

/**
 * @author Stefan Czaska
 */
final class Console {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final BundleCache bundleCache;
    
    /**
     * 
     */
    private int errors;
    
    /**
     * 
     */
    private int warnings;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Console(BundleCache bundleCache) {
        this.bundleCache = bundleCache;
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public int getErrorCount() {
        return errors;
    }
    
    /**
     * 
     */
    public int getWarningCount() {
        return warnings;
    }
    
    //===============
    // Print Methods
    //===============
    
    /**
     * 
     */
    public void print(String text) {
        System.out.println(text);
    }
    
    /**
     * 
     */
    public void printInfo(String text) {
        System.out.println("["+bundleCache.getString(Messages.INFO)+"] "+text);
    }
    
    /**
     * 
     */
    public void printWarning(String text) {
        warnings++;
        System.out.println("["+bundleCache.getString(Messages.WARNING)+"] "+text);
    }
    
    /**
     * 
     */
    public void printError(String text) {
        errors++;
        System.out.println("["+bundleCache.getString(Messages.ERROR)+"] "+text);
    }
    
    //=================
    // Summary Methods
    //=================
    
    /**
     * 
     */
    public void printSummary() {
        printInfo(createSummary());
    }
    
    /**
     * 
     */
    private String createSummary() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(errors);
        builder.append(" ");
        builder.append(bundleCache.getString(errors == 1 ? Messages.ERROR : Messages.ERRORS));
        builder.append(", ");
        builder.append(warnings);
        builder.append(" ");
        builder.append(bundleCache.getString(warnings == 1 ? Messages.WARNING : Messages.WARNINGS));
        
        return builder.toString();
    }
}
