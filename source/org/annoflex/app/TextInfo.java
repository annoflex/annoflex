/*
 * AnnoFlex - A code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import org.annoflex.util.SystemToolkit;
import org.annoflex.util.text.LineInfo;

/**
 * @author Stefan Czaska
 */
public class TextInfo {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String text;
    
    /**
     * 
     */
    private final LineSeparatorMode lineSeparatorMode;
    
    //==============
    // Cache Fields
    //==============
    
    /**
     * 
     */
    private String lineSeparator;
    
    /**
     * 
     */
    private LineInfo lineInfo;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public TextInfo(String text, LineSeparatorMode lineSeparatorMode) {
        if (text == null) {
            throw new IllegalArgumentException("text may not be null");
        }
        
        if (lineSeparatorMode == null) {
            throw new IllegalArgumentException("line separator mode may not be null");
        }
        
        this.text = text;
        this.lineSeparatorMode = lineSeparatorMode;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final String getText() {
        return text;
    }
    
    /**
     * 
     */
    public final LineSeparatorMode getLineSeparatorMode() {
        return lineSeparatorMode;
    }
    
    /**
     * 
     */
    public final String getLineSeparator() {
        
        // Note: Determine line separator lazy as the line info is slow. It
        // should only be created if really necessary.
        if (lineSeparator == null) {
            lineSeparator = determineLineSeparator();
        }
        
        return lineSeparator;
    }
    
    /**
     * 
     */
    private String determineLineSeparator() {
        switch(lineSeparatorMode) {
        case LF: return "\n";
        case CR: return "\r";
        case CRLF: return "\r\n";
        case SYSTEM: return SystemToolkit.LINE_SEPARATOR;
        case AUTO:
            String lineTerminator = getLineInfo().getPrimaryLineTerminator();
            
            return lineTerminator != null ? lineTerminator : SystemToolkit.LINE_SEPARATOR;
        
        default:
            throw new IllegalStateException("invalid line separator mode");
        }
    }
    
    /**
     * 
     */
    public final LineInfo getLineInfo() {
        
        // Note: Create lazy as the creation is slow. It should only be
        // created if really necessary.
        if (lineInfo == null) {
            lineInfo = new LineInfo(text);
        }
        
        return lineInfo;
    }
}
