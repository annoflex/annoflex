/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.token;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * @author Stefan Czaska
 */
public class TokenList<T extends Token<?,?>> implements ListModel<T> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private Object[] list;
    
    /**
     * 
     */
    private int size;
    
    //==============
    // List Methods
    //==============
    
    /**
     * 
     */
    public final int size() {
        return size;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public final T get(int index) {
        if ((index < 0) || (index >= size)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        return (T)list[index];
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public final T get(int index, boolean prev, TokenHandler<T> skipFilter) {
        if ((index < 0) || (index >= size)) {
            throw new IllegalArgumentException("index out of bounds: "+index);
        }
        
        if (skipFilter == null) {
            throw new IllegalArgumentException("filter may not be null");
        }
        
        if (prev) {
            for (int i=index-1;i>=0;i--) {
                T token = (T)list[i];
                
                if (!skipFilter.handleToken(token)) {
                    return token;
                }
            }
        }
        
        else {
            int size = this.size;
            
            for (int i=index+1;i<size;i++) {
                T token = (T)list[i];
                
                if (!skipFilter.handleToken(token)) {
                    return token;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public final T findToken(int textOffset) {
        int index = indexOf(textOffset);
        
        return index != -1 ? (T)list[index] : null;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public final int indexOf(int textOffset) {
        if (textOffset < 0) {
            throw new IllegalArgumentException("offset may not be negative: "+textOffset);
        }
        
        int low = 0;
        int high = size - 1;
        
        while (low <= high) {
            int mid = (low + high) / 2;
            
            T token = (T)list[mid];
            
            if (textOffset < token.start()) {
                high = mid - 1;
            }
            
            else if (textOffset >= token.end()) {
                low = mid + 1;
            }
            
            else {
                return mid;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     */
    public final int indexOf(T token) {
        if (token == null) {
            throw new IllegalArgumentException("token may not be null");
        }
        
        int index = indexOf(token.start());
        
        if ((index != -1) && (token == list[index])) {
            return index;
        }
        
        throw new IllegalArgumentException("token is not part of the list: "+token);
    }
    
    //======================
    // Modification Methods
    //======================
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public void add(T token) {
        if (token == null) {
            throw new IllegalArgumentException("token may not be null");
        }
        
        if (size > 0) {
            T lastToken = (T)list[size-1];
            
            if (token.start() < lastToken.end()) {
                throw new IllegalArgumentException("token start may not be "
                        + "lower then end of last token: "
                        + "start="+token.start()+", last end="+lastToken.end());
            }
        }
        
        if (list == null) {
            list = new Object[10];
        }
        
        else if (size == list.length) {
            Object[] newList = new Object[((size*3)/2)+1];
            System.arraycopy(list,0,newList,0,size);
            list = newList;
        }
        
        list[size++] = token;
    }
    
    /**
     * 
     */
    public void addAll(TokenIterator<T> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator may not be null");
        }
        
        T curToken = iterator.getNextToken();
        
        while (curToken != null) {
            add(curToken);
            curToken = iterator.getNextToken();
        }
    }
    
    /**
     * 
     */
    public void addAll(TokenIterator<T> iterator, TokenHandler<T> filter) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator may not be null");
        }
        
        if (filter == null) {
            throw new IllegalArgumentException("filter may not be null");
        }
        
        T curToken = iterator.getNextToken();
        
        while (curToken != null) {
            if (!filter.handleToken(curToken)) {
                add(curToken);
            }
            
            curToken = iterator.getNextToken();
        }
    }
    
    /**
     * 
     */
    public void addAll(TokenList<T> tokenList, TokenHandler<T> filter) {
        if (tokenList == null) {
            throw new IllegalArgumentException("token list may not be null");
        }
        
        if (filter == null) {
            throw new IllegalArgumentException("filter may not be null");
        }
        
        int size = tokenList.size();
        
        for (int i=0;i<size;i++) {
            T curToken = tokenList.get(i);
            
            if (!filter.handleToken(curToken)) {
                add(curToken);
            }
        }
    }
    
    //====================
    // List Model Methods
    //====================
    
    /**
     * 
     */
    public final int getSize() {
        return size();
    }
    
    /**
     * 
     */
    public final T getElementAt(int index) {
        return get(index);
    }
    
    /**
     * 
     */
    public void addListDataListener(ListDataListener l) {
    }
    
    /**
     * 
     */
    public void removeListDataListener(ListDataListener l) {
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
        buffer.append("[list=");
        buffer.append(list);
        buffer.append("]");
        
        return buffer.toString();
    }
}
