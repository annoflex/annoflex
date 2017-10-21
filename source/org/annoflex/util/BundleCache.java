/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.annoflex.util.text.ByteOrderMark;

/**
 * @author Stefan Czaska
 */
public class BundleCache {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final ControlExt control;
    
    /**
     * 
     */
    private final HashSet<ResourceBundle> bundleRefHolder = new HashSet<>();
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public BundleCache() {
        this("ISO-8859-1");
    }
    
    /**
     * 
     */
    public BundleCache(String defaultCharSet) {
        if (defaultCharSet == null) {
            throw new IllegalArgumentException("default char set may not be null");
        }
        
        control = new ControlExt(defaultCharSet);
    }
    
    //================
    // Bundle Methods
    //================
    
    /**
     * 
     */
    public String getString(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message may not be null");
        }
        
        ResourceBundle bundle = ResourceBundle.getBundle(message
                .getBundleName(),control);
        
        bundleRefHolder.add(bundle);
        
        return bundle.getString(message.getId());
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class ControlExt extends Control {
        
        /**
         * 
         */
        private final String defaultCharSet;
        
        /**
         * 
         */
        public ControlExt(String defaultCharSet) {
            this.defaultCharSet = defaultCharSet;
        }
        
        /**
         * 
         */
        public ResourceBundle newBundle(String baseName, Locale locale,
                String format, ClassLoader loader, boolean reload)
                        throws IllegalAccessException, InstantiationException,
                        IOException {
            
            try {
                if (format.equals("java.properties")) {
                    String bundleName = toBundleName(baseName,locale);
                    String resourceName = toResourceName(bundleName,"properties");
                    
                    if (resourceName == null) {
                        return null;
                    }
                    
                    try (InputStream stream = getInputStream(loader,resourceName,
                            reload)) {
                        
                        if (stream != null) {
                            try (Reader reader = getReader(stream)) {
                                return new PropertyResourceBundle(reader);
                            }
                        }
                    }
                    
                    return null;
                }
            }
            
            // convert to Error as ResourceBundle has special handling for
            // exceptions which makes them invisible
            catch(Exception e) {
                throw new Error(e);
            }
            
            return super.newBundle(baseName,locale,format,loader,reload);
        }
        
        /**
         * 
         */
        private InputStream getInputStream(ClassLoader loader,
                String resourceName, boolean reload) throws IOException {
            
            URL url = loader.getResource(resourceName);
            
            if (url != null) {
                URLConnection connection = url.openConnection();
                
                if (connection != null) {
                    if (reload) {
                        connection.setUseCaches(false);
                    }
                    
                    return connection.getInputStream();
                }
            }
            
            return null;
        }
        
        /**
         * 
         */
        private Reader getReader(InputStream stream) throws IOException {
            byte[] bytes = SystemToolkit.readBytes(stream);
            
            ByteOrderMark bom = ByteOrderMark.lookup(bytes);
            int offset = 0;
            String charSet = null;
            
            if (bom != null) {
                offset = bom.getOffset();
                charSet = bom.getCharSet();
            }
            
            else {
                offset = 0;
                charSet = defaultCharSet;
            }
            
            return new InputStreamReader(new ByteArrayInputStream(bytes,offset,
                    bytes.length-offset),charSet);
        }
    }
}
