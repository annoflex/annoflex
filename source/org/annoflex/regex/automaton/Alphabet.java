/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.annoflex.regex.CharClass;
import org.annoflex.regex.Expression;
import org.annoflex.util.integer.ConstIntArray;
import org.annoflex.util.integer.ConstIntRangeSet;
import org.annoflex.util.integer.IdSet;
import org.annoflex.util.integer.MultiMapController;
import org.annoflex.util.integer.MutableIntRangeMap;
import org.annoflex.util.integer.MutableIntRangeMultiMap;
import org.annoflex.util.integer.MutableIntRangeSet;

/**
 * @author Stefan Czaska
 */
public class Alphabet {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final ConstIntRangeSet[] charClassList;
    
    /**
     * 
     */
    private final MutableIntRangeMap<Integer> charClassIndex =
            new MutableIntRangeMap<>();
    
    //==============
    // Cache Fields
    //==============
    
    /**
     * 
     */
    private final HashMap<ConstIntRangeSet,ConstIntArray> charClassCache =
            new HashMap<>();
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Alphabet(Rule<?>[] ruleList) {
        charClassList = createCharClassList(ruleList);
        
        for (int i=0;i<charClassList.length;i++) {
            charClassIndex.put(charClassList[i],i);
        }
    }
    
    //==================
    // Alphabet Methods
    //==================
    
    /**
     * 
     */
    public int getLength() {
        return charClassList.length;
    }
    
    /**
     * 
     */
    public ConstIntRangeSet getCharClass(int index) {
        return charClassList[index];
    }
    
    /**
     * 
     */
    public ConstIntArray getSymbols(ConstIntRangeSet charSet) {
        ConstIntArray symbols = charClassCache.get(charSet);
        
        if (symbols == null) {
            IdSet symbolSet = new IdSet();
            int intervalCount = charSet.size();
            
            for (int i=0;i<intervalCount;i++) {
                int startIndex = charClassIndex.indexOf(charSet.getStart(i));
                int endIndex = charClassIndex.indexOf(charSet.getEnd(i));
                
                for (int j=startIndex;j<=endIndex;j++) {
                    symbolSet.add(charClassIndex.getValue(j));
                }
            }
            
            symbols = symbolSet.toConstIntArray();
            charClassCache.put(charSet,symbols);
        }
        
        return symbols;
    }
    
    //========================
    // Initialization Methods
    //========================
    
    /**
     * 
     */
    private ConstIntRangeSet[] createCharClassList(Rule<?>[] ruleList) {
        
        // create the char class table
        CharClassTable charClassTable = new CharClassTable(ruleList);
        
        // sort the intervals of the char class table by their id sets 
        Interval[] intervalList = createSortedIntervalList(charClassTable);
        
        // create for each id set group an individual char class
        ArrayList<ConstIntRangeSet> charClassList = new ArrayList<>();
        MutableIntRangeSet curCharClass = new MutableIntRangeSet();
        Interval refInterval = intervalList[0];
        
        curCharClass.add(charClassTable.getStart(refInterval.index),
                charClassTable.getEnd(refInterval.index));
        
        for (int i=1;i<intervalList.length;i++) {
            Interval curInterval = intervalList[i];
            
            if (!curInterval.idSet.equals(refInterval.idSet)) {
                charClassList.add(curCharClass.toConstSet());
                curCharClass.clear();
                
                refInterval = curInterval;
            }
            
            curCharClass.add(charClassTable.getStart(curInterval.index),
                    charClassTable.getEnd(curInterval.index));
        }
        
        charClassList.add(curCharClass.toConstSet());
        
        // convert to array 
        return charClassList.toArray(new ConstIntRangeSet[] {});
    }
    
    /**
     * 
     */
    private Interval[] createSortedIntervalList(CharClassTable charClassTable) {
        int tableSize = charClassTable.size();
        Interval[] array = new Interval[tableSize];
        
        for (int i=0;i<tableSize;i++) {
            array[i] = new Interval(i,charClassTable.getValues(i));
        }
        
        Arrays.sort(array);
        
        return array;
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public char[] toSymbolMap() {
        char[] symbolMap = new char[0x10000];
        int size = getLength();
        
        for (int i=0;i<size;i++) {
            ConstIntRangeSet curCharClass = getCharClass(i);
            int curIntervalCount = curCharClass.size();
            
            for (int j=0;j<curIntervalCount;j++) {
                int start = curCharClass.getStart(j);
                int end = curCharClass.getEnd(j);
                
                for (int k=start;k<=end;k++) {
                    symbolMap[k] = (char)i;
                }
            }
        }
        
        return symbolMap;
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
        buffer.append("[length=");
        buffer.append(getLength());
        buffer.append(",characterIntervals=");
        buffer.append(charClassIndex.size());
        buffer.append("]");
        
        return buffer.toString();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class CharClassTable extends MutableIntRangeMultiMap<Integer,IdSet> {
        
        /**
         * 
         */
        private int symbolSetCounter;
        
        /**
         * 
         */
        private final HashSet<ConstIntRangeSet> symbolSetCache = new HashSet<>();
        
        /**
         * 
         */
        public CharClassTable(Rule<?>[] ruleList) {
            super(MultiMapController.ID_SET_CONTROLLER);
            
            put(Character.MIN_VALUE,Character.MAX_VALUE,symbolSetCounter++);
            
            for (int i=0;i<ruleList.length;i++) {
                commitSymbolSets(ruleList[i].getExpression().normalize());
            }
        }
        
        /**
         * 
         */
        private void commitSymbolSets(Expression expression) {
            if (expression.isCharClass()) {
                ConstIntRangeSet symbolSet = ((CharClass)expression).getCharSet();
                
                // put only new symbol sets in order to minimize the number of ids
                if (symbolSetCache.add(symbolSet)) {
                    put(symbolSet,symbolSetCounter++);
                }
            }
            
            else {
                int childCount = expression.getChildCount();
                
                for (int i=0;i<childCount;i++) {
                    commitSymbolSets(expression.getChild(i));
                }
            }
        }
    }
    
    /**
     * 
     */
    static final class Interval implements Comparable<Interval> {
        
        /**
         * 
         */
        public final int index;
        
        /**
         * 
         */
        public final IdSet idSet;
        
        /**
         * 
         */
        public Interval(int index, IdSet idSet) {
            this.index = index;
            this.idSet = idSet;
        }
        
        /**
         * 
         */
        public int compareTo(Interval o) {
            return idSet.compareTo(o.idSet);
        }
    }
}
