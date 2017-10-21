/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.annoflex.util.SystemToolkit;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class Condition {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    public static final String NAME_INITIAL = "INITIAL";
    
    /**
     * 
     */
    public static final String NAME_ALL = "*";
    
    /**
     * 
     */
    public static final Condition ALL = new Condition(new String[]{NAME_ALL});
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String[] nameList;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    private Condition(String[] nameList) {
        this.nameList = nameList;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final int getNameCount() {
        return nameList.length;
    }
    
    /**
     * 
     */
    public final String getName(int index) {
        return nameList[index];
    }
    
    /**
     * 
     */
    public String[] toArray() {
        return nameList.clone();
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof Condition) {
            return Arrays.equals(((Condition)obj).nameList,nameList);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return Arrays.hashCode(nameList);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("<");
        
        for (int i=0;i<nameList.length;i++) {
            builder.append(nameList[i]);
            
            if (i < (nameList.length - 1)) {
                builder.append(",");
            }
        }
        
        builder.append(">");
        
        return builder.toString();
    }
    
    //=================
    // Factory Methods
    //=================
    
    /**
     * 
     */
    public static Condition create(String[] nameList) throws ConditionException {
        if (nameList == null) {
            throw new ConditionException("name list may not be null");
        }
        
        if (nameList.length == 0) {
            throw new ConditionException("name list may not be empty");
        }
        
        if (nameList.length == 1) {
            return new Condition(new String[]{normalize(checkName(nameList[0]))});
        }
        
        LinkedHashSet<String> nameSet = new LinkedHashSet<>();
        
        for (int i=0;i<nameList.length;i++) {
            nameSet.add(normalize(checkName(nameList[i])));
        }
        
        return new Condition(nameSet.toArray(SystemToolkit.EMPTY_STRING_ARRAY));
    }
    
    /**
     * 
     */
    private static String checkName(String name) {
        if (name == null) {
            throw new ConditionException("name may not be null");
        }
        
        if (!name.equals(NAME_ALL) &&
            !StringToolkit.isASCIIIdentifier(name)) {
            
            throw new ConditionException("invalid condition name "+ "\""+name+"\"");
        }
        
        return name;
    }
    
    /**
     * 
     */
    private static String normalize(String name) {
        StringBuilder builder = new StringBuilder();
        boolean containsLowercase = StringToolkit.indexOf(name,
                StringToolkit.LOWERCASE) != -1;
        int prevChar = -1;
        
        for (int i=0;i<name.length();i++) {
            char curChar = name.charAt(i);
            
            if (containsLowercase && (prevChar != -1) &&
                requiresUnderscore(prevChar,curChar)) {
                
                builder.append('_');
            }
            
            builder.append(Character.toUpperCase(curChar));
            prevChar = curChar;
        }
        
        return builder.toString();
    }
    
    /**
     * 
     */
    private static boolean requiresUnderscore(int prevChar, int nextChar) {
        return (Character.isLowerCase(prevChar) && Character.isUpperCase(nextChar)) ||
               ((Character.isLetter(prevChar) && Character.isDigit(nextChar)) ||
                (Character.isDigit(prevChar) && Character.isLetter(nextChar)));
    }
}
