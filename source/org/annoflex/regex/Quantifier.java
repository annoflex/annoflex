/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class Quantifier {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    public static final Quantifier ZERO_OR_ONE = new Quantifier(0,1);
    
    /**
     * 
     */
    public static final Quantifier ZERO_OR_MORE = new Quantifier(0,
            Integer.MAX_VALUE);
    
    /**
     * 
     */
    public static final Quantifier ONE_OR_MORE = new Quantifier(1,
            Integer.MAX_VALUE);
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    public final int min;
    
    /**
     * 
     */
    public final int max;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    private Quantifier(int min, int max) {
        this.min = min;
        this.max = max;
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
        
        if (obj instanceof Quantifier) {
            Quantifier quantifier = (Quantifier)obj;
            
            return (quantifier.min == min) && (quantifier.max == max);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return max * 31 + min;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append('{');
        builder.append(min);
        builder.append(',');
        
        if (max != Integer.MAX_VALUE) {
            builder.append(max);
        }
        
        builder.append('}');
        
        return builder.toString();
    }
    
    //=================
    // Factory Methods
    //=================
    
    /**
     * 
     */
    public static Quantifier create(int min, int max) throws QuantifierException {
        if (min < 0) {
            throw new QuantifierException("minimum may not be lower then zero");
        }
        
        if (max < 1) {
            throw new QuantifierException("maximum may not be lower then one");
        }
        
        if (max < min) {
            throw new QuantifierException("maximum may not be lower then minimum");
        }
        
        if (min == Integer.MAX_VALUE) {
            throw new QuantifierException("minimum may not be infinity");
        }
        
        if (min == 0) {
            if (max == 1) {
                return ZERO_OR_ONE;
            }
            
            if (max == Integer.MAX_VALUE) {
                return ZERO_OR_MORE;
            }
        }
        
        else if (min == 1) {
            if (max == Integer.MAX_VALUE) {
                return ONE_OR_MORE;
            }
        }
        
        return new Quantifier(min,max);
    }
    
    /**
     * 
     */
    public static Quantifier createAtLeast(int min) throws QuantifierException {
        return create(min,Integer.MAX_VALUE);
    }
    
    /**
     * 
     */
    public static Quantifier createUpTo(int max) throws QuantifierException {
        return create(0,max);
    }
    
    /**
     * 
     */
    public static Quantifier createExactly(int number) throws QuantifierException {
        return create(number,number);
    }
}
