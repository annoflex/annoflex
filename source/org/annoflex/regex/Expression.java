/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.annoflex.regex.compiler.RegExCompileException;
import org.annoflex.regex.compiler.RegExCompiler;
import org.annoflex.util.integer.ConstIntRangeSet;

/**
 * @author Stefan Czaska
 */
public abstract class Expression {
    
    //============================
    // Char Class Cache Constants
    //============================
    
    /**
     * 
     */
    private static final CharClass[] CHAR_CLASS_CACHE = new CharClass[128];
    
    /**
     * 
     */
    static {
        for (int i=0;i<=127;i++) {
            CHAR_CLASS_CACHE[i] = new CharClass(i);
        }
    }
    
    //======================
    // Expression Constants
    //======================
    
    /**
     * ANY_SYBMOL := [^]
     */
    public static final CharClass ANY_SYMBOL = new CharClass(
            Character.MIN_VALUE,Character.MAX_VALUE);
    
    /**
     * EVERYTHING := [^]*
     */
    public static final ZeroOrMore EVERYTHING = new ZeroOrMore(ANY_SYMBOL);
    
    /**
     * ASCII_LINE_TERMINATOR := \x0D | \x0A | \x0D\x0A
     */
    public static final Alternation ASCII_LINE_TERMINATOR =
            new Alternation(forChar('\r'),forChar('\n'),
                    new Concatenation(forChar('\r'),forChar('\n')));
    
    /**
     * UNICODE_LINE_TERMINATOR := \x0D\x0A | [\x0A\x0B\x0C\x0D\x85\u2028\u2029]
     */
    public static final Alternation UNICODE_LINE_TERMINATOR =
            new Alternation(new Concatenation(forChar('\r'),forChar('\n')),
                    new Concatenation(forChar('\n'),forChar('\u000B'),
                            forChar('\u000C'),forChar('\r'),forChar('\u0085'),
                            forChar('\u2028'),forChar('\u2029')));
    
    //======================
    // Comparator Constants
    //======================
    
    /**
     * 
     */
    public static final Comparator<Expression> TYPE_COMPARATOR =
            new TypeComparator();
    
    /**
     * 
     */
    public static final Comparator<Expression> LENGTH_COMPARATOR =
            new LengthComparator();
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final ExpressionType type;
    
    /**
     * 
     */
    private int hash;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    Expression(ExpressionType type) {
        if (type == null) {
            throw new IllegalArgumentException("type may not be null");
        }
        
        this.type = type;
    }
    
    //==============
    // Type Methods
    //==============
    
    /**
     * 
     */
    public final ExpressionType getType() {
        return type;
    }
    
    /**
     * 
     */
    public final boolean isCharClass() {
        return type == ExpressionType.CHAR_CLASS;
    }
    
    /**
     * 
     */
    public final boolean isModifier() {
        return type == ExpressionType.MODIFIER;
    }
    
    /**
     * 
     */
    public final boolean isQuantifier() {
        return type == ExpressionType.QUANTIFIER;
    }
    
    /**
     * 
     */
    public final boolean isConcatenation() {
        return type == ExpressionType.CONCATENATION;
    }
    
    /**
     * 
     */
    public final boolean isLookahead() {
        return type == ExpressionType.LOOKAHEAD;
    }
    
    /**
     * 
     */
    public final boolean isCondition() {
        return type == ExpressionType.CONDITION;
    }
    
    /**
     * 
     */
    public final boolean isAlternation() {
        return type == ExpressionType.ALTERNATION;
    }
    
    /**
     * 
     */
    public int getTypeCount(ExpressionType type) {
        if (type == null) {
            throw new IllegalArgumentException("type may not be null");
        }
        
        if (type.isContainedIn(getTypeSet())) {
            int count = this.type == type ? 1 : 0;
            int childCount = getChildCount();
            
            for (int i=0;i<childCount;i++) {
                count += getChild(i).getTypeCount(type);
            }
            
            return count;
        }
        
        return 0;
    }
    
    //=========================
    // Expression Tree Methods
    //=========================
    
    /**
     * 
     */
    public abstract int getChildCount();
    
    /**
     * 
     */
    public abstract Expression getChild(int index);
    
    /**
     * 
     */
    public abstract int getTypeSet();
    
    /**
     * 
     */
    public abstract int getWordLength();
    
    //===============================
    // Expression Conversion Methods
    //===============================
    
    /**
     * 
     */
    public abstract Expression normalize();
    
    /**
     * 
     */
    public abstract Expression reverse();
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public TreeModel toTreeModel() {
        return new ExpressionTreeModel(this);
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof Expression) {
            Expression expression = (Expression)obj;
            
            if (expression.getTypeSet() == getTypeSet()) {
                if ((expression.hash != 0) && (hash != 0) &&
                    (expression.hash != hash)) {
                    
                    return false;
                }
                
                int childCount = getChildCount();
                
                if ((expression.getChildCount() == childCount) &&
                    hasSameData(expression)) {
                    
                    for (int i=0;i<childCount;i++) {
                        if (!getChild(i).equals(expression.getChild(i))) {
                            return false;
                        }
                    }
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    private boolean hasSameData(Expression expression) {
        switch(expression.getType()) {
        case CHAR_CLASS:
            return ((CharClass)expression).getCharSet()
                    .equals(((CharClass)this).getCharSet());
        
        case QUANTIFIER:
            return ((QuantifierExpression)expression).getQuantifier()
                    .equals(((QuantifierExpression)this).getQuantifier());
        
        case MODIFIER:
            return ((ModifierExpression)expression).getModifier()
                    .equals(((ModifierExpression)this).getModifier());
        
        case ALTERNATION: return true;
        case CONCATENATION: return true;
        case LOOKAHEAD: return true;
        
        case CONDITION:
            return ((ConditionExpression)expression).getCondition()
                    .equals(((ConditionExpression)this).getCondition());
        
        default:
            throw new IllegalStateException("unknown expression type");
        }
    }
    
    /**
     * 
     */
    public int hashCode() {
        int hash = this.hash;
        
        if (hash == 0) {
            hash = computeHashCode();
            
            this.hash = hash;
        }
        
        return hash;
    }
    
    /**
     * 
     */
    private int computeHashCode() {
        int hash = type.hashCode() * 31 + computeDataHashCode(this);
        int childCount = getChildCount();
        
        for (int i=0;i<childCount;i++) {
            hash = hash * 31 + getChild(i).hashCode();
        }
        
        return hash == 0 ? 1 : hash;
    }
    
    /**
     * 
     */
    private int computeDataHashCode(Expression expression) {
        switch(expression.type) {
        case CHAR_CLASS:
            return ((CharClass)expression).getCharSet().hashCode();
        
        case QUANTIFIER:
            return ((QuantifierExpression)expression).getQuantifier().hashCode();
        
        case MODIFIER:
            return ((ModifierExpression)expression).getModifier().hashCode();
        
        case ALTERNATION: return 0;
        case CONCATENATION: return 0;
        case LOOKAHEAD: return 0;
        
        case CONDITION:
            return ((ConditionExpression)expression).getCondition().hashCode();
        
        default:
            throw new IllegalStateException("unknown expression type");
        }
    }
    
    //============================
    // Expression Factory Methods
    //============================
    
    /**
     * 
     */
    public static CharClass forChar(int character) {
        return (character >= 0) && (character <= 127) ?
                CHAR_CLASS_CACHE[character] : new CharClass(character);
    }
    
    /**
     * 
     */
    public static CharClass forCharSet(int[] array) {
        return new CharClass(array);
    }
    
    /**
     * 
     */
    public static CharClass forCharSet(int[] array, boolean invert) {
        return new CharClass(array,invert);
    }
    
    /**
     * 
     */
    public static CharClass forCharRange(int startCharacter, int endCharacter) {
        return startCharacter == endCharacter ?
                forChar(startCharacter) :
                new CharClass(startCharacter,endCharacter);
    }
    
    /**
     * 
     */
    public static CharClass forCharRangeSet(int[][] spanList) {
        return new CharClass(spanList);
    }
    
    /**
     * 
     */
    public static CharClass forCharRangeSet(ConstIntRangeSet charSet) {
        return new CharClass(charSet);
    }
    
    /**
     * 
     */
    public static Concatenation forString(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string may not be null");
        }
        
        int length = string.length();
        
        if (length == 0) {
            throw new IllegalArgumentException("string may not be empty");
        }
        
        ArrayList<CharClass> charClassList = new ArrayList<>(length);
        
        for (int i=0;i<length;i++) {
            charClassList.add(forChar(string.charAt(i)));
        }
        
        return new Concatenation(charClassList);
    }
    
    /**
     * 
     */
    public static Expression compile(String expression) throws RegExCompileException {
        return new RegExCompiler().compile(expression);
    }
    
    //====================
    // Comparator Classes
    //====================
    
    /**
     * 
     */
    static final class TypeComparator implements Comparator<Expression> {
        
        /**
         * 
         */
        public int compare(Expression o1, Expression o2) {
            return o1.getType().compareTo(o2.getType());
        }
    }
    
    /**
     * 
     */
    static final class LengthComparator implements Comparator<Expression> {
        
        /**
         * 
         */
        public int compare(Expression o1, Expression o2) {
            return o1.getWordLength() - o2.getWordLength();
        }
    }
    
    //====================
    // Tree Model Classes
    //====================
    
    /**
     * 
     */
    static final class ExpressionTreeModel implements TreeModel {
        
        // TODO: Use unique wrapper objects to avoid the equality issue on same parent.
        
        /**
         * 
         */
        private final Expression root;
        
        /**
         * 
         */
        ExpressionTreeModel(Expression root) {
            this.root = root;
        }
        
        /**
         * 
         */
        public Object getRoot() {
            return root;
        }
        
        /**
         * 
         */
        public int getChildCount(Object parent) {
            if (parent != null) {
                return ((Expression)parent).getChildCount();
            }
            
            return 0;
        }
        
        /**
         * 
         */
        public Object getChild(Object parent, int index) {
            if (parent != null) {
                return ((Expression)parent).getChild(index);
            }
            
            return null;
        }
        
        /**
         * 
         */
        public boolean isLeaf(Object node) {
            return (node != null) && (((Expression)node).getChildCount() == 0);
        }
        
        /**
         * 
         */
        public int getIndexOfChild(Object parent, Object child) {
            if ((parent != null) && (child != null)) {
                Expression parentExpression = (Expression)parent;
                Expression childExpression = (Expression)child;
                
                int childCount = parentExpression.getChildCount();
                
                for (int i=0;i<childCount;i++) {
                    if (parentExpression.getChild(i) == childExpression) {
                        return i;
                    }
                }
            }
            
            return -1;
        }
        
        /**
         * 
         */
        public void addTreeModelListener(TreeModelListener l) {
        }
        
        /**
         * 
         */
        public void removeTreeModelListener(TreeModelListener l) {
        }
        
        /**
         * 
         */
        public void valueForPathChanged(TreePath path, Object newValue) {
        }
    }
}
