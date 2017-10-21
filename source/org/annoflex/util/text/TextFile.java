/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

import org.annoflex.util.SystemToolkit;

/**
 * @author Stefan Czaska
 */
public class TextFile {
    
    //===========================
    // Default Charset Constants
    //===========================
    
    /**
     * 
     */
    public static final String DEFAULT_CHAR_SET = "UTF-8";
    
    //===============
    // BOM Constants
    //===============
    
    /**
     * 
     */
    private static final byte[] BOM_UTF8 = new byte[] {(byte)0xEF,(byte)0xBB,(byte)0xBF};
    
    /**
     * 
     */
    private static final byte[] BOM_UTF16_BE = new byte[] {(byte)0xFE,(byte)0xFF};
    
    /**
     * 
     */
    private static final byte[] BOM_UTF16_LE = new byte[] {(byte)0xFF,(byte)0xFE};
    
    /**
     * 
     */
    private static final byte[] BOM_UTF32_BE = new byte[] {(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0xFF};
    
    /**
     * 
     */
    private static final byte[] BOM_UTF32_LE = new byte[] {(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00};
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private String content;
    
    /**
     * 
     */
    private String charSet;
    
    /**
     * 
     */
    private boolean hasBOM;
    
    /**
     * 
     */
    private String lineSeparator = SystemToolkit.LINE_SEPARATOR;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public TextFile() {
    }
    
    /**
     * 
     */
    public TextFile(String content) {
        this.content = content;
    }
    
    /**
     * 
     */
    public TextFile(String content, String charSet) {
        this.content = content;
        this.charSet = charSet;
    }
    
    /**
     * 
     */
    public TextFile(String content, String charSet, boolean hasBom) {
        this.content = content;
        this.charSet = charSet;
        this.hasBOM = hasBom;
    }
    
    /**
     * 
     */
    public TextFile(String content, String charSet, boolean hasBom,
            String lineSeparator) {
        
        this.content = content;
        this.charSet = charSet;
        this.hasBOM = hasBom;
        this.lineSeparator = lineSeparator;
    }
    
    /**
     * 
     */
    public TextFile(File file) throws IOException {
        load(file);
    }
    
    /**
     * 
     */
    public TextFile(File file, String defaultCharSet) throws IOException {
        load(file,defaultCharSet);
    }
    
    /**
     * 
     */
    public TextFile(URL url) throws IOException {
        load(url);
    }
    
    /**
     * 
     */
    public TextFile(URL url, String defaultCharSet) throws IOException {
        load(url,defaultCharSet);
    }
    
    /**
     * 
     */
    public TextFile(InputStream stream) throws IOException {
        load(stream);
    }
    
    /**
     * 
     */
    public TextFile(InputStream stream, String defaultCharSet) throws IOException {
        load(stream,defaultCharSet);
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * 
     */
    public String getContent() {
        return content;
    }
    
    /**
     * 
     */
    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }
    
    /**
     * 
     */
    public String getCharSet() {
        return charSet;
    }
    
    /**
     * 
     */
    public void setHasBOM(boolean hasBOM) {
        this.hasBOM = hasBOM;
    }
    
    /**
     * 
     */
    public boolean getHasBOM() {
        return hasBOM;
    }
    
    /**
     * 
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    /**
     * 
     */
    public String getLineSeparator() {
        return lineSeparator;
    }
    
    //==============
    // Load Methods
    //==============
    
    /**
     * 
     */
    public void loadFromFile(String file) throws IOException {
        loadFromFile(file,DEFAULT_CHAR_SET);
    }
    
    /**
     * 
     */
    public void loadFromFile(String file, String defaultCharSet) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file may not be null");
        }
        
        load(new File(file),defaultCharSet);
    }
    
    /**
     * 
     */
    public void load(File file) throws IOException {
        load(file,DEFAULT_CHAR_SET);
    }
    
    /**
     * 
     */
    public void load(File file, String defaultCharSet) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file may not be null");
        }
        
        setData(SystemToolkit.readBytes(file),defaultCharSet);
    }
    
    /**
     * 
     */
    public void loadFromURL(String url) throws IOException {
        loadFromURL(url,DEFAULT_CHAR_SET);
    }
    
    /**
     * 
     */
    public void loadFromURL(String url, String defaultCharSet) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url may not be null");
        }
        
        load(new URL(url),defaultCharSet);
    }
    
    /**
     * 
     */
    public void load(URL url) throws IOException {
        load(url,DEFAULT_CHAR_SET);
    }
    
    /**
     * 
     */
    public void load(URL url, String defaultCharSet) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url may not be null");
        }
        
        setData(SystemToolkit.readBytes(url),defaultCharSet);
    }
    
    /**
     * 
     */
    public void load(InputStream stream) throws IOException {
        load(stream,DEFAULT_CHAR_SET);
    }
    
    /**
     * 
     */
    public void load(InputStream stream, String defaultCharSet) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("stream may not be null");
        }
        
        setData(SystemToolkit.readBytes(stream),defaultCharSet);
    }
    
    /**
     * 
     */
    private void setData(byte[] bytes, String defaultCharSet)
            throws UnsupportedEncodingException {
        
        int offset;
        String charSet;
        boolean hasBOM;
        ByteOrderMark bom = ByteOrderMark.lookup(bytes);
        
        if (bom != null) {
            offset = bom.getOffset();
            charSet = bom.getCharSet();
            hasBOM = true;
        }
        
        else {
            offset = 0;
            charSet = defaultCharSet != null ? defaultCharSet : DEFAULT_CHAR_SET;
            hasBOM = false;
        }
        
        // Note: Update properties only if everything could be determined
        // without exceptions. This ensures that the TextFile properties are
        // always valid.
        content = bytes != null ? new String(bytes,offset,bytes.length-offset,
                charSet) : null;
        this.charSet = charSet;
        this.hasBOM = hasBOM;
    }
    
    //==============
    // Save Methods
    //==============
    
    /**
     * 
     */
    public void saveToFile(String file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file may not be null");
        }
        
        save(new File(file));
    }
    
    /**
     * 
     */
    public void save(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file may not be null");
        }
        
        byte[][] data = getWriteData();
        
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            writeData(data,outputStream);
        }
    }
    
    /**
     * 
     */
    public void saveToURL(String url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url may not be null");
        }
        
        save(new URL(url));
    }
    
    /**
     * 
     */
    public void save(URL url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url may not be null");
        }
        
        byte[][] data = getWriteData();
        
        try (OutputStream outputStream = url.openConnection().getOutputStream()) {
            writeData(data,outputStream);
        }
    }
    
    /**
     * 
     */
    public void save(OutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("stream may not be null");
        }
        
        byte[][] data = getWriteData();
        
        try (OutputStream outputStream = stream) {
            writeData(data,outputStream);
        }
    }
    
    /**
     * 
     */
    private byte[][] getWriteData() throws IOException {
        Charset charset = Charset.forName(charSet != null ?
                charSet : DEFAULT_CHAR_SET);
        
        byte[] bomBytes = getBOMArray(charset);
        byte[] contentBytes = convertLineSeparators(content != null ?
                content : "").getBytes(charset);
        
        if (bomBytes != null) {
            return new byte[][] { bomBytes, contentBytes };
        }
        
        return new byte[][] { contentBytes };
    }
    
    /**
     * 
     */
    private byte[] getBOMArray(Charset charset) {
        if (hasBOM) {
            switch(charset.name().toUpperCase()) {
            case "UTF-32BE": return BOM_UTF32_BE;
            case "UTF-32LE": return BOM_UTF32_LE;
            case "UTF-32":   return SystemToolkit.IS_BIG_ENDIAN_SYSTEM ? BOM_UTF32_BE : BOM_UTF32_LE;
            case "UTF-8":    return BOM_UTF8;
            case "UTF-16BE": return BOM_UTF16_BE;
            case "UTF-16LE": return BOM_UTF16_LE;
            case "UTF-16":   return SystemToolkit.IS_BIG_ENDIAN_SYSTEM ? BOM_UTF16_BE : BOM_UTF16_LE;
            }
        }
        
        return null;
    }
    
    /**
     * 
     */
    private String convertLineSeparators(String string) {
        if (lineSeparator != null) {
            StringBuilder builder = StringToolkit.appendReplacement(null,string,
                    StringToolkit.LINE_TERMINATOR,lineSeparator);
            
            return builder != null ? builder.toString() : string;
        }
        
        return string;
    }
    
    /**
     * 
     */
    private void writeData(byte[][] data, OutputStream stream) throws IOException {
        for (int i=0;i<data.length;i++) {
            stream.write(data[i]);
        }
    }
}
