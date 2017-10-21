/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

import org.annoflex.util.text.TextFile;

/**
 * @author Stefan Czaska
 */
public class BuildInfo {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String name;
    
    /**
     * 
     */
    private final String major;
    
    /**
     * 
     */
    private final String minor;
    
    /**
     * 
     */
    private final String build;
    
    /**
     * 
     */
    private final String year;
    
    /**
     * 
     */
    private final String month;
    
    /**
     * 
     */
    private final String day;
    
    /**
     * 
     */
    private final String revision;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public BuildInfo(Class<?> anchorClass) {
        this(anchorClass,"build.properties");
    }
    
    /**
     * 
     */
    public BuildInfo(Class<?> anchorClass, String fileName) {
        if (anchorClass == null) {
            throw new IllegalArgumentException("anchor class may not be null");
        }
        
        if (fileName == null) {
            throw new IllegalArgumentException("file name may not be null");
        }
        
        Properties properties = loadProperties(anchorClass,fileName);
        
        name = getValue(properties,"name","<name>");
        major = getValue(properties,"major","0");
        minor = getValue(properties,"minor","0");
        build = getValue(properties,"build","0");
        year = getValue(properties,"year","0");
        month = getValue(properties,"month","0");
        day = getValue(properties,"day","0");
        revision = getValue(properties,"revision","0");
    }
    
    //=================
    // Base Properties
    //=================
    
    /**
     * 
     */
    public final String name() {
        return name;
    }
    
    /**
     * 
     */
    public final String major() {
        return major;
    }
    
    /**
     * 
     */
    public final String minor() {
        return minor;
    }
    
    /**
     * 
     */
    public final String build() {
        return build;
    }
    
    /**
     * 
     */
    public final String year() {
        return year;
    }
    
    /**
     * 
     */
    public final String month() {
        return month;
    }
    
    /**
     * 
     */
    public final String day() {
        return day;
    }
    
    /**
     * 
     */
    public final String revision() {
        return revision;
    }
    
    //========================
    // Initialization Methods
    //========================
    
    /**
     * 
     */
    private Properties loadProperties(Class<?> anchorClass, String fileName) {
        URL url = anchorClass.getResource(fileName);
        
        if (url != null) {
            try {
                Properties properties = new Properties();
                properties.load(new StringReader(new TextFile(url).getContent()));
                
                return properties;
            }
            
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        return null;
    }
    
    /**
     * 
     */
    private String getValue(Properties properties, String name,
            String defaultValue) {
        
        String value = properties != null ? properties.getProperty(name) : null;
        
        return value != null ? value : defaultValue;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(name);
        builder.append('-');
        builder.append(major);
        builder.append('.');
        builder.append(minor);
        builder.append('-');
        builder.append(build);
        builder.append('-');
        builder.append(year);
        builder.append('.');
        builder.append(month);
        builder.append('.');
        builder.append(day);
        builder.append('-');
        builder.append(revision);
        
        return builder.toString();
    }
}
