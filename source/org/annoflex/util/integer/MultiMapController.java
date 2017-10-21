/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * @author Stefan Czaska
 */
public interface MultiMapController<V,C> {
    
    //=================================
    // Predefined Controller Constants
    //=================================
    
    /**
     * 
     */
    public static final IdSetController ID_SET_CONTROLLER =
            new IdSetController();
    
    //========================
    // Map Controller Methods
    //========================
    
    /**
     * 
     */
    public C createCollection();
    
    /**
     * 
     */
    public C cloneCollection(C collection);
    
    /**
     * 
     */
    public boolean addValue(C collection, V value);
    
    //===============================
    // Predefined Controller Classes
    //===============================
    
    /**
     * 
     */
    public static class ArrayListController<V>
            implements MultiMapController<V,ArrayList<V>> {
        
        /**
         * 
         */
        public ArrayList<V> createCollection() {
            return new ArrayList<>(1);
        }
        
        /**
         * 
         */
        @SuppressWarnings("unchecked")
        public ArrayList<V> cloneCollection(ArrayList<V> collection) {
            return (ArrayList<V>)collection.clone();
        }
        
        /**
         * 
         */
        public boolean addValue(ArrayList<V> collection, V value) {
            return collection.add(value);
        }
    }
    
    /**
     * 
     */
    public static class LinkedListController<V>
            implements MultiMapController<V,LinkedList<V>> {
        
        /**
         * 
         */
        public LinkedList<V> createCollection() {
            return new LinkedList<>();
        }
        
        /**
         * 
         */
        @SuppressWarnings("unchecked")
        public LinkedList<V> cloneCollection(LinkedList<V> collection) {
            return (LinkedList<V>)collection.clone();
        }
        
        /**
         * 
         */
        public boolean addValue(LinkedList<V> collection, V value) {
            return collection.add(value);
        }
    }
    
    /**
     * 
     */
    public static class HashSetController<V>
            implements MultiMapController<V,HashSet<V>> {
        
        /**
         * 
         */
        public HashSet<V> createCollection() {
            return new HashSet<>(1);
        }
        
        /**
         * 
         */
        @SuppressWarnings("unchecked")
        public HashSet<V> cloneCollection(HashSet<V> collection) {
            return (HashSet<V>)collection.clone();
        }
        
        /**
         * 
         */
        public boolean addValue(HashSet<V> collection, V value) {
            return collection.add(value);
        }
    }
    
    /**
     * 
     */
    public static class LinkedHashSetController<V>
            implements MultiMapController<V,LinkedHashSet<V>> {
        
        /**
         * 
         */
        public LinkedHashSet<V> createCollection() {
            return new LinkedHashSet<>(1);
        }
        
        /**
         * 
         */
        @SuppressWarnings("unchecked")
        public LinkedHashSet<V> cloneCollection(LinkedHashSet<V> collection) {
            return (LinkedHashSet<V>)collection.clone();
        }
        
        /**
         * 
         */
        public boolean addValue(LinkedHashSet<V> collection, V value) {
            return collection.add(value);
        }
    }
    
    /**
     * 
     */
    public static class IdSetController
            implements MultiMapController<Integer,IdSet> {
        
        /**
         * 
         */
        public IdSet createCollection() {
            return new IdSet();
        }
        
        /**
         * 
         */
        public IdSet cloneCollection(IdSet collection) {
            return collection.clone();
        }
        
        /**
         * 
         */
        public boolean addValue(IdSet collection, Integer value) {
            return collection.add(value.intValue());
        }
    }
}
