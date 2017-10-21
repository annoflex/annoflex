/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Stefan Czaska
 */
public final class SystemToolkit {
    
    //===================
    // System Properties
    //===================
    
    public static final String LINE_SEPARATOR = System.lineSeparator();
    
    public static final boolean IS_64_BIT_SYSTEM = is64BitSystemInternal();
    public static final boolean IS_BIG_ENDIAN_SYSTEM = isBigEndianSystemInternal();
    
    //=================
    // Array Constants
    //=================
    
    public static final byte[]   EMPTY_BYTE_ARRAY = new byte[]{};
    public static final short[]  EMPTY_SHORT_ARRAY = new short[]{};
    public static final int[]    EMPTY_INT_ARRAY = new int[]{};
    public static final long[]   EMPTY_LONG_ARRAY = new long[]{};
    public static final float[]  EMPTY_FLOAT_ARRAY = new float[]{};
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[]{};
    public static final char[]   EMPTY_CHAR_ARRAY = new char[]{};
    public static final String[] EMPTY_STRING_ARRAY = new String[]{};
    
    //================
    // Size Constants
    //================
    
    public static final int SIZE_OF_BYTE = 1;
    public static final int SIZE_OF_BOOLEAN = 1;
    public static final int SIZE_OF_SHORT = 2;
    public static final int SIZE_OF_CHAR = 2;
    public static final int SIZE_OF_INTEGER = 4;
    public static final int SIZE_OF_LONG = 8;
    public static final int SIZE_OF_FLOAT = 4;
    public static final int SIZE_OF_DOUBLE = 8;
    public static final int SIZE_OF_REFERENCE = 4;
    public static final int SIZE_OF_OBJECT_HEADER = 12;
    public static final int SIZE_OF_ARRAY_HEADER = 16;
    public static final int SIZE_OF_MEMORY_PAGE = 4096;
    
    //==================
    // Buffer Constants
    //==================
    
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final int BUFFER_THRESHOLD = 1048576;
    
    //================
    // Equals Methods
    //================
    
    /**
     * 
     */
    public static boolean equals(Object object1, Object object2) {
        return (object1 == object2) ||
               ((object1 != null) && (object2 != null) &&
                object1.equals(object2));
    }
    
    /**
     * 
     */
    public static boolean equals(int[][] array1, int[][] array2) {
        if (array1 == array2) {
            return true;
        }
        
        if ((array1 == null) || (array2 == null) ||
            (array1.length != array2.length)) {
            
            return false;
        }
        
        for (int i=0;i<array1.length;i++) {
            if (!Arrays.equals(array1[i],array2[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    //===============
    // Clone Methods
    //===============
    
    /**
     * 
     */
    public static int[][] clone(int[][] array) {
        if (array != null) {
            int length = array.length;
            int[][] newArray = new int[length][];
            
            for (int i=0;i<length;i++) {
                int[] subArray = array[i];
                
                newArray[i] = subArray != null ? subArray.clone() : null;
            }
            
            return newArray;
        }
        
        return null;
    }
    
    //============
    // IO Methods
    //============
    
    /**
     * 
     */
    public static byte[] readBytes(File file) throws IOException {
        if (file != null) {
            long fileSize = file.length();
            
            if (fileSize > MAX_ARRAY_SIZE) {
                throw new IOException("file is too large");
            }
            
            try (SeekableByteChannel byteChannel = Files.newByteChannel(file.toPath());
                    InputStream inputStream = Channels.newInputStream(byteChannel)) {
                
                int available = inputStream.available();
                
                if (available > 0) {
                    return readBytes(inputStream,available > fileSize ?
                            available : (int)fileSize);
                }
            }
        }
        
        return EMPTY_BYTE_ARRAY;
    }
    
    /**
     * 
     */
    public static byte[] readBytes(URL url) throws IOException {
        if (url != null) {
            URLConnection connection = url.openConnection();
            connection.connect();
            
            long contentLength = connection.getContentLengthLong();
            
            if (contentLength > MAX_ARRAY_SIZE) {
                throw new IOException("stream length is too large");
            }
            
            try (InputStream inputStream = connection.getInputStream()) {
                int available = inputStream.available();
                
                if (available > 0) {
                    return readBytes(inputStream,available > contentLength ?
                            available : (int)contentLength);
                }
            }
        }
        
        return EMPTY_BYTE_ARRAY;
    }
    
    /**
     * 
     */
    public static byte[] readBytes(InputStream stream) throws IOException {
        if (stream != null) {
            try (InputStream inputStream = stream) {
                int available = inputStream.available();
                
                if (available > 0) {
                    return readBytes(inputStream,available);
                }
            }
        }
        
        return EMPTY_BYTE_ARRAY;
    }
    
    /**
     * 
     */
    @SuppressWarnings("null")
    private static byte[] readBytes(InputStream stream,
            int initialBufferSize) throws IOException {
        
        LinkedList<byte[]> bufferList = null;
        int totalBytesRead = 0;
        byte[] buffer = new byte[initialBufferSize];
        
        while (true) {
            int bytesRead = 0;
            boolean endReached = false;
            
            while (true) {
                int curBytesRead = stream.read(buffer,bytesRead,buffer.length-bytesRead);
                
                if (curBytesRead <= 0) {
                    endReached = true;
                    break;
                }
                
                if (curBytesRead > (MAX_ARRAY_SIZE - totalBytesRead)) {
                    throw new IOException("input stream is too large");
                }
                
                totalBytesRead += curBytesRead;
                bytesRead += curBytesRead;
                
                if (bytesRead >= buffer.length) {
                    break;
                }
            }
            
            int available = stream.available();
            
            if ((available <= 0) || endReached) {
                if (bufferList != null) {
                    bufferList.add(buffer);
                }
                
                break;
            }
            
            if (bufferList == null) {
                bufferList = new LinkedList<>();
            }
            
            bufferList.add(buffer);
            
            int newBufferSize = (buffer.length < BUFFER_THRESHOLD / 2) ?
                    buffer.length * 2 : Math.max(BUFFER_THRESHOLD,totalBytesRead>>6);
            
            buffer = new byte[available > newBufferSize ?
                    available : newBufferSize];
        }
        
        if (totalBytesRead > 0) {
            int size = bufferList != null ? bufferList.size() : 0;
            
            if (size <= 1) {
                byte[] bytes = bufferList != null ? bufferList.getFirst() : buffer;
                
                if (totalBytesRead == bytes.length) {
                    return bytes;
                }
            }
            
            byte[] result = new byte[totalBytesRead];
            
            if (size <= 1) {
                byte[] curArray = bufferList != null ? bufferList.getFirst() : buffer;
                System.arraycopy(curArray,0,result,0,totalBytesRead);
            }
            
            else {
                Iterator<byte[]> iterator = bufferList.iterator();
                int position = 0;
                
                while (iterator.hasNext()) {
                    byte[] curArray = iterator.next();
                    System.arraycopy(curArray,0,result,position,
                            Math.min(curArray.length,totalBytesRead-position));
                    position += curArray.length;
                }
            }
            
            return result;
        }
        
        return EMPTY_BYTE_ARRAY;
    }
    
    //========================
    // Class Location Methods
    //========================
    
    /**
     * 
     */
    public static URL getClassLocation(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("class may not be null");
        }
        
        String className = clazz.getName().replace('.','/') + ".class";
        
        URL location = clazz.getClassLoader().getResource(className);
        
        if (location == null) {
            throw new IllegalArgumentException("class could not be found");
        }
        
        String protocol = location.getProtocol();
        
        if (protocol == null) {
            throw new IllegalArgumentException("invalid protocol");
        }
        
        try {
            if (protocol.equalsIgnoreCase("file")) {
                String string = location.toString();
                int index = string.lastIndexOf(className);
                
                if (index != -1) {
                    return new URL(string.substring(0,index));
                }
            }
            
            else if (protocol.equalsIgnoreCase("jar")) {
                String path = location.getPath();
                int index = path.lastIndexOf("!/"+className);
                
                if (index != -1) {
                    return new URL(path.substring(0,index));
                }
            }
            
            throw new IllegalArgumentException("unknown URL protocol \""+protocol+"\"");
        }
        
        catch(MalformedURLException e) {
            throw new IllegalArgumentException("class location could not be " +
                    "determined",e);
        }
    }
    
    /**
     * 
     */
    public static URL getClassLocation(Class<?> clazz, String context) {
        try {
            return new URL(getClassLocation(clazz),context);
        }
        
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("class location could not be " +
                    "determined",e);
        }
    }
    
    /**
     * 
     */
    public static URL getFileLocation(Class<?> clazz, String relativePath,
            String fileName) {
        
        try {
            return new URL(getClassLocation(clazz),relativePath+"/"+clazz
                    .getPackage().getName().replace('.','/')+"/"+fileName);
        }
        
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("file location could not be "
                    + "determined",e);
        }
    }
    
    //=========================
    // System Property Methods
    //=========================
    
    /**
     * 
     */
    private static boolean is64BitSystemInternal() {
        String dataModel = System.getProperty("sun.arch.data.model");
        
        if ((dataModel != null) && (dataModel.indexOf("64") != -1)) {
            return true;
        }
        
        String osArch = System.getProperty("os.arch");
        
        if ((osArch != null) && (osArch.indexOf("64") != -1)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     */
    private static boolean isBigEndianSystemInternal() {
        return ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
    }
    
    //==============
    // Size Methods
    //==============
    
    /**
     * 
     */
    public static int sizeOfByteArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_BYTE * length;
    }
    
    /**
     * 
     */
    public static int sizeOfBooleanArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_BOOLEAN * length;
    }
    
    /**
     * 
     */
    public static int sizeOfShortArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_SHORT * length;
    }
    
    /**
     * 
     */
    public static int sizeOfCharArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_CHAR * length;
    }
    
    /**
     * 
     */
    public static int sizeOfIntArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_INTEGER * length;
    }
    
    /**
     * 
     */
    public static int sizeOfLongArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_LONG * length;
    }
    
    /**
     * 
     */
    public static int sizeOfFloatArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_FLOAT * length;
    }
    
    /**
     * 
     */
    public static int sizeOfDoubleArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_DOUBLE * length;
    }
    
    /**
     * 
     */
    public static int sizeOfObjectArray(int length) {
        return SIZE_OF_ARRAY_HEADER + SIZE_OF_REFERENCE * length;
    }
}
