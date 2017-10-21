/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.compiler;

import java.util.ArrayList;

import org.annoflex.regex.Alternation;
import org.annoflex.regex.CharClass;
import org.annoflex.regex.Concatenation;
import org.annoflex.regex.Condition;
import org.annoflex.regex.ConditionExpression;
import org.annoflex.regex.Expression;
import org.annoflex.regex.ExpressionType;
import org.annoflex.regex.LookafterType;
import org.annoflex.regex.Lookahead;
import org.annoflex.regex.ModifierExpression;
import org.annoflex.regex.QuantifierExpression;
import org.annoflex.regex.dom.CharRef;
import org.annoflex.regex.dom.ROMCCOperator;
import org.annoflex.regex.dom.ROMCCRange;
import org.annoflex.regex.dom.ROMCCSequence;
import org.annoflex.regex.dom.ROMCharExpr;
import org.annoflex.regex.dom.ROMCharRef;
import org.annoflex.regex.dom.ROMCharacterClass;
import org.annoflex.regex.dom.ROMClassExpr;
import org.annoflex.regex.dom.ROMClassRef;
import org.annoflex.regex.dom.ROMCompilationUnit;
import org.annoflex.regex.dom.ROMCondition;
import org.annoflex.regex.dom.ROMLookafter;
import org.annoflex.regex.dom.ROMLookaroundExpr;
import org.annoflex.regex.dom.ROMLookbefore;
import org.annoflex.regex.dom.ROMMacro;
import org.annoflex.regex.dom.ROMMacroExpr;
import org.annoflex.regex.dom.ROMModifier;
import org.annoflex.regex.dom.ROMModifierExpr;
import org.annoflex.regex.dom.ROMName;
import org.annoflex.regex.dom.ROMNameList;
import org.annoflex.regex.dom.ROMNode;
import org.annoflex.regex.dom.ROMQuantifier;
import org.annoflex.regex.dom.ROMQuantifierExpr;
import org.annoflex.regex.dom.ROMRootAlternation;
import org.annoflex.regex.dom.ROMRootElement;
import org.annoflex.regex.dom.ROMSequenceExpr;
import org.annoflex.regex.dom.ROMSequenceRef;
import org.annoflex.regex.dom.ROMSimpleExpr;
import org.annoflex.regex.dom.ROMStringSequence;
import org.annoflex.regex.dom.SequenceRef;
import org.annoflex.regex.parser.RegExParseException;
import org.annoflex.regex.parser.RegExParser;
import org.annoflex.regex.unicode.Property;
import org.annoflex.regex.unicode.PropertyException;
import org.annoflex.regex.unicode.PropertyResolver;
import org.annoflex.regex.unicode.PropertySelector;
import org.annoflex.util.SystemToolkit;
import org.annoflex.util.integer.ConstIntRangeSet;
import org.annoflex.util.integer.MutableIntRangeSet;

/**
 * @author Stefan Czaska
 */
public class RegExCompiler {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final RegExParser parser = new RegExParser();
    
    /**
     * 
     */
    private boolean excludeConditions;
    
    /**
     * 
     */
    private MacroResolver macroResolver;
    
    /**
     * 
     */
    private PropertyResolver propertyResolver;
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setExcludeConditions(boolean excludeConditions) {
        this.excludeConditions = excludeConditions;
    }
    
    /**
     * 
     */
    public boolean getExcludeConditions() {
        return excludeConditions;
    }
    
    /**
     * 
     */
    public void setMacroResolver(MacroResolver macroResolver) {
        this.macroResolver = macroResolver;
    }
    
    /**
     * 
     */
    public MacroResolver getMacroResolver() {
        return macroResolver;
    }
    
    //=================
    // Compile Methods
    //=================
    
    /**
     * 
     */
    public Expression compile(String string) throws RegExCompileException {
        try {
            return extractCompilationUnit(parser.parse(string));
        }
        
        catch(RegExParseException e) {
            throw new RegExCompileException(RegExCompileException.SYNTAX_ERROR,
                    "syntax error");
        }
    }
    
    /**
     * 
     */
    private Expression extractCompilationUnit(ROMCompilationUnit compilationUnit) {
        if (compilationUnit != null) {
            int childCount = compilationUnit.getChildCount();
            
            // an empty list represents the empty word and is returned as a null
            // expression
            if (childCount > 0) {
                
                // check for more than two children which are always wrong
                if (childCount > 2) {
                    throw createInternalError("compilation unit has more than two children");
                }
                
                // determine root condition and root alternation and extract
                // expression
                if (childCount == 2) {
                    ROMNode child1 = compilationUnit.getChild(0);
                    ROMNode child2 = compilationUnit.getChild(1);
                    
                    if (!child1.isCondition() || !child2.isRootAlternation()) {
                        throw createInternalError("compilation unit has invalid children");
                    }
                    
                    Condition condition = extractCondition((ROMCondition)child1);
                    Expression rootAlternation = extractRootAlternation((ROMRootAlternation)child2);
                    
                    if (excludeConditions) {
                        throw createError(RegExCompileException.CONDITIONS_NOT_ALLOWED,
                                "conditions are not supported here");
                    }
                    
                    return new ConditionExpression(condition,rootAlternation);
                }
                
                ROMNode child1 = compilationUnit.getChild(0);
                
                if (!child1.isRootAlternation()) {
                    throw createInternalError("compilation unit has invalid children");
                }
                
                return extractRootAlternation((ROMRootAlternation)child1);
            }
        }
        
        return null;
    }
    
    //==========================
    // Root Alternation Methods
    //==========================
    
    /**
     * 
     */
    private Expression extractRootAlternation(ROMRootAlternation rootAlternation) {
        int rootChildCount = rootAlternation.getChildCount();
        
        if (rootChildCount == 0) {
            throw createInternalError("root alternation has no children");
        }
        
        ArrayList<Expression> list = new ArrayList<>(rootChildCount);
        ROMNode iterator = rootAlternation.getFirstChild();
        
        do {
            if (!iterator.isRootElement()) {
                throw createInternalError("root alternation has invalid children");
            }
            
            list.add(extractRootElement((ROMRootElement)iterator));
            
            iterator = iterator.getNextSibling();
        } while (iterator != null);
        
        return list.size() == 1 ? list.get(0) :
            new Alternation(list);
    }
    
    /**
     * 
     */
    private Expression extractRootElement(ROMRootElement rootElement) {
        int childCount = rootElement.getChildCount();
        
        if (childCount == 0) {
            throw createInternalError("root element has no children");
        }
        
        if (childCount > 2) {
            throw createInternalError("root element has too many children");
        }
        
        if (childCount == 2) {
            ROMNode child1 = rootElement.getChild(0);
            ROMNode child2 = rootElement.getChild(1);
            
            if (!child1.isCondition() ||
                (!child2.isSimpleExpr() && !child2.isLookaroundExpr())) {
                
                throw createInternalError("root element has invalid children");
            }
            
            Condition condition = extractCondition((ROMCondition)child1);
            Expression rootElementContent = extractRootElementContent(child2);
            
            if (excludeConditions) {
                throw createError(RegExCompileException.CONDITIONS_NOT_ALLOWED,
                        "conditions are not supported here");
            }
            
            return new ConditionExpression(condition,rootElementContent);
        }
        
        ROMNode child1 = rootElement.getChild(0);
        
        if (!child1.isSimpleExpr() && !child1.isLookaroundExpr()) {
            throw createInternalError("root element has invalid children");
        }
        
        return extractRootElementContent(child1);
    }
    
    /**
     * 
     */
    private Expression extractRootElementContent(ROMNode node) {
        if (node.isLookaroundExpr()) {
            return extractLookaroundExpression((ROMLookaroundExpr)node);
        }
        
        if (node.isSimpleExpr()) {
            return extractSimpleExpression((ROMSimpleExpr)node);
        }
        
        throw createInternalError("invalid node");
    }
    
    //===============================
    // Lookaround Expression Methods
    //===============================
    
    /**
     * 
     */
    private Expression extractLookaroundExpression(ROMLookaroundExpr lookaround) {
        // Note: For the time being only the lookafter type "expression" is
        // supported. Thus reject all other configurations as unsupported
        // features.
        int childCount = lookaround.getChildCount();
        
        if ((childCount < 2) || (childCount > 4)) {
            throw createInternalError("lookaround expression has invalid number of children");
        }
        
        // data to collect
        ROMLookbefore lookbefore = null;
        ROMSimpleExpr mainExpression = null;
        ROMLookafter lookafter = null;
        ROMSimpleExpr lookafterExpression = null;
        
        // collect data
        ROMNode child1 = lookaround.getChild(0);
        ROMNode child2 = lookaround.getChild(1);
        
        if (child1.isLookbefore()) {
            lookbefore = (ROMLookbefore)child1;
            mainExpression = (ROMSimpleExpr)child2;
            
            if (childCount >= 3) {
                ROMNode child3 = lookaround.getChild(2);
                
                if (!child3.isLookafter()) {
                    throw createInternalError("lookaround expression has invalid children");
                }
                
                lookafter = (ROMLookafter)child3;
                lookafterExpression = extractLookafterExpression(lookafter,lookaround,4);
            }
        }
        
        else {
            if (!child1.isSimpleExpr() || !child2.isLookafter()) {
                throw createInternalError("lookaround expression has invalid children");
            }
            
            mainExpression = (ROMSimpleExpr)child1;
            lookafter = (ROMLookafter)child2;
            
            if (childCount == 4) {
                throw createInternalError("lookaround expression has invalid number of children");
            }
            
            lookafterExpression = extractLookafterExpression(lookafter,lookaround,3);
        }
        
        // reject unsupported lookarounds
        if ((lookbefore != null) || (lookafterExpression == null)) {
            // TODO: Implement unsupported lookaround types.
            throw createError(RegExCompileException.UNSUPPORTED_FEATURE,
                    "unsupported lookaround type");
        }
        
        return new Lookahead(extractSimpleExpression(mainExpression),
                extractSimpleExpression(lookafterExpression));
    }
    
    /**
     * 
     */
    private ROMSimpleExpr extractLookafterExpression(ROMLookafter lookafter,
            ROMLookaroundExpr lookaround, int maxChildCount) {
        
        if (lookafter.getLookafterType() == LookafterType.EXPRESSION) {
            if (lookaround.getChildCount() != maxChildCount) {
                throw createInternalError("lookaround expression has invalid number of children");
            }
            
            ROMNode child = lookaround.getChild(maxChildCount-1);
            
            if (!child.isSimpleExpr()) {
                throw createInternalError("lookaround expression has invalid children");
            }
            
            return (ROMSimpleExpr)child;
        }
        
        if (lookaround.getChildCount() == maxChildCount) {
            throw createInternalError("lookaround expression has invalid number of children");
        }
        
        return null;
    }
    
    //===========================
    // Simple Expression Methods
    //===========================
    
    /**
     * 
     */
    private Expression extractSimpleExpression(ROMSimpleExpr simpleExpr) {
        switch(simpleExpr.getNodeType()) {
        case ALTERNATION_EXPR:
            return new Alternation(extractSimpleExpressionList(simpleExpr));
        
        case CONCATENATION_EXPR:
            return new Concatenation(extractSimpleExpressionList(simpleExpr));
        
        case MODIFIER_EXPR:
            return extractModifierExpression((ROMModifierExpr)simpleExpr);
        
        case QUANTIFIER_EXPR:
            return extractQuantifierExpression((ROMQuantifierExpr)simpleExpr);
        
        case CHAR_EXPR:
            return extractCharExpression((ROMCharExpr)simpleExpr);
        
        case CLASS_EXPR:
            return extractClassExpression((ROMClassExpr)simpleExpr);
        
        case SEQUENCE_EXPR:
            return extractSequenceExpression((ROMSequenceExpr)simpleExpr);
        
        case MACRO_EXPR:
            return extractMacroExpression((ROMMacroExpr)simpleExpr);
        
        default:
            throw createInternalError("invalid node type");
        }
    }
    
    /**
     * 
     */
    private ArrayList<Expression> extractSimpleExpressionList(ROMSimpleExpr root) {
        int rootChildCount = root.getChildCount();
        
        if (rootChildCount == 0) {
            throw createInternalError("expression list has no children");
        }
        
        ArrayList<Expression> list = new ArrayList<>(rootChildCount);
        ROMNode iterator = root.getFirstChild();
        
        do {
            if (!iterator.isSimpleExpr()) {
                throw createInternalError("expression list has invalid children");
            }
            
            list.add(extractSimpleExpression((ROMSimpleExpr)iterator));
            
            iterator = iterator.getNextSibling();
        } while (iterator != null);
        
        return list;
    }
    
    /**
     * 
     */
    private Expression extractModifierExpression(ROMModifierExpr modifierExpr) {
        if (modifierExpr.getChildCount() != 2) {
            throw createInternalError("modifier expression has invalid number of children");
        }
        
        ROMNode child1 = modifierExpr.getChild(0);
        ROMNode child2 = modifierExpr.getChild(1);
        
        if (!child1.isModifier() || !child2.isSimpleExpr()) {
            throw createInternalError("modifier expression has invalid children");
        }
        
        return new ModifierExpression(((ROMModifier)child1).getModifier(),
                extractSimpleExpression((ROMSimpleExpr)child2));
    }
    
    /**
     * 
     */
    private Expression extractQuantifierExpression(ROMQuantifierExpr quantifierExpr) {
        if (quantifierExpr.getChildCount() != 2) {
            throw createInternalError("quantifier expression has invalid number of children");
        }
        
        ROMNode child1 = quantifierExpr.getChild(0);
        ROMNode child2 = quantifierExpr.getChild(1);
        
        if (!child1.isSimpleExpr() || !child2.isQuantifier()) {
            throw createInternalError("quantifier expression has invalid children");
        }
        
        return new QuantifierExpression(extractSimpleExpression((ROMSimpleExpr)child1),
                ((ROMQuantifier)child2).getQuantifier());
    }
    
    /**
     * 
     */
    private Expression extractCharExpression(ROMCharExpr charExpr) {
        if (charExpr.getChildCount() != 1) {
            throw createInternalError("char expression has invalid number of children");
        }
        
        ROMNode child1 = charExpr.getFirstChild();
        
        if (!child1.isCharRef()) {
            throw createInternalError("char expression has invalid children");
        }
        
        return Expression.forChar(extractCharRef((ROMCharRef)child1));
    }
    
    /**
     * 
     */
    private Expression extractClassExpression(ROMClassExpr classExpr) {
        if (classExpr.getChildCount() != 1) {
            throw createInternalError("class expression has invalid number of children");
        }
        
        ROMNode child1 = classExpr.getFirstChild();
        
        if (child1.isCharacterClass()) {
            return Expression.forCharRangeSet(extractCharacterClass(
                    (ROMCharacterClass)child1).toConstSet());
        }
        
        if (child1.isClassRef()) {
            return Expression.forCharRangeSet(extractClassRef(
                    (ROMClassRef)child1));
        }
        
        throw createInternalError("class expression has invalid children");
    }
    
    /**
     * 
     */
    private Expression extractSequenceExpression(ROMSequenceExpr sequenceExpr) {
        if (sequenceExpr.getChildCount() != 1) {
            throw createInternalError("sequence expression has invalid number of children");
        }
        
        ROMNode child1 = sequenceExpr.getFirstChild();
        
        if (child1.isStringSequence()) {
            return extractStringSequence((ROMStringSequence)child1);
        }
        
        if (child1.isSequenceRef()) {
            return extractSequenceRef((ROMSequenceRef)child1);
        }
        
        throw createInternalError("sequence expression has invalid children");
    }
    
    /**
     * 
     */
    private Expression extractMacroExpression(ROMMacroExpr macroExpr) {
        if (macroExpr.getChildCount() != 1) {
            throw createInternalError("macro expression has invalid number of children");
        }
        
        ROMNode macroExprChild1 = macroExpr.getFirstChild();
        
        if (!macroExprChild1.isMacro()) {
            throw createInternalError("macro expression has invalid children");
        }
        
        String macroName = extractMacro((ROMMacro)macroExprChild1);
        Expression macroExpression = resolveMacro(macroName);
        
        // inside simple expressions conditions and lookarounds are not
        // supported and must be reported as an error
        int macroTypeSet = macroExpression.getTypeSet();
        
        // conditions are never allowed inside macros and thus rejected with an
        // error
        if (ExpressionType.CONDITION.isContainedIn(macroTypeSet)) {
            throw createError(RegExCompileException.INVALID_MACRO_USAGE,"invalid macro usage");
        }
        
        // The usage of lookarounds is context dependent. If the ancestors up to
        // the root element are only alternations or the direct parent is the
        // root element then they are allowed. Otherwise not as they would appear
        // as "simple expression content" which is not allowed. Lookarounds are
        // always "top level expressions" which can be alternated with other
        // top level expressions only.
        if (ExpressionType.LOOKAHEAD.isContainedIn(macroTypeSet) &&
            !contextAllowsLookarounds(macroExpr)) {
            
            throw createError(RegExCompileException.INVALID_MACRO_USAGE,"invalid macro usage");
        }
        
        return macroExpression;
    }
    
    /**
     * 
     */
    private boolean contextAllowsLookarounds(ROMMacroExpr macroExpr) {
        switch(macroExpr.getParent().getNodeType()) {
        case ROOT_ELEMENT:
            return true;
        
        case ALTERNATION_EXPR:
            ROMNode iterator = macroExpr.getParent();
            
            while (iterator.isAlternationExpr()) {
                iterator = iterator.getParent();
            }
            
            return iterator.isRootElement();
        
        default:
            return false;
        }
    }
    
    //=========================
    // Character Class Methods
    //=========================
    
    /**
     * 
     */
    private MutableIntRangeSet extractCharacterClass(ROMCharacterClass characterClass) {
        ROMNode iterator = characterClass.getFirstChild();
        MutableIntRangeSet charSet;
        
        if (iterator == null) {
            charSet = new MutableIntRangeSet();
        }
        
        else {
            if (!iterator.isCCSequence()) {
                throw createInternalError("character class has invalid children");
            }
            
            charSet = extractCCSequence((ROMCCSequence)iterator);
            
            ROMNode nextSibling1 = iterator.getNextSibling();
            
            while (nextSibling1 != null) {
                ROMNode nextSibling2 = nextSibling1.getNextSibling();
                
                if (nextSibling2 == null) {
                    throw createInternalError("character class has invalid number of children");
                }
                
                if (!nextSibling1.isCCOperator() || !nextSibling2.isCCSequence()) {
                    throw createInternalError("character class has invalid children");
                }
                
                MutableIntRangeSet nextCharSet = extractCCSequence((ROMCCSequence)nextSibling2);
                
                switch(((ROMCCOperator)nextSibling1).getCharClassOperator()) {
                case UNION:
                    charSet.add(nextCharSet);
                    break;
                
                case INTERSECTION:
                    charSet.intersection(nextCharSet);
                    break;
                
                case SET_DIFFERENCE:
                    charSet.remove(nextCharSet);
                    break;
                
                case SYMMETRIC_DIFFERENCE:
                    charSet.symmetricDifference(nextCharSet);
                    break;
                }
                
                nextSibling1 = nextSibling2.getNextSibling();
            }
        }
        
        if (characterClass.getInvert()) {
            charSet.invert(Character.MIN_VALUE,Character.MAX_VALUE);
        }
        
        if (charSet.isEmpty()) {
            throw createError(RegExCompileException.EMPTY_CHAR_CLASS,"empty char class");
        }
        
        return charSet;
    }
    
    /**
     * 
     */
    private MutableIntRangeSet extractCCSequence(ROMCCSequence sequence) {
        if (sequence.getChildCount() == 0) {
            throw createInternalError("character class sequence has invalid number of children");
        }
        
        MutableIntRangeSet charSet = new MutableIntRangeSet();
        ROMNode iterator = sequence.getFirstChild();
        
        do {
            switch(iterator.getNodeType()) {
            case CHAR_REF:
                charSet.add(extractCharRef((ROMCharRef)iterator));
                break;
            
            case CC_RANGE:
                charSet.add(extractCCRange((ROMCCRange)iterator));
                break;
            
            case CLASS_REF:
                charSet.add(extractClassRef((ROMClassRef)iterator));
                break;
            
            case SEQUENCE_REF:
                Expression sequenceExpression = extractSequenceRef((ROMSequenceRef)iterator);
                
                charSet.add(computeCharSet(sequenceExpression,RegExCompileException
                        .INTERNAL_ERROR,"internal error"));
                break;
            
            case MACRO:
                String macroName = extractMacro((ROMMacro)iterator);
                Expression macroExpression = resolveMacro(macroName);
                
                charSet.add(computeCharSet(macroExpression,RegExCompileException
                        .INVALID_MACRO_USAGE,"invalid macro usage"));
                break;
            
            case CHARACTER_CLASS:
                charSet.add(extractCharacterClass((ROMCharacterClass)iterator));
                break;
            
            default:
                throw createInternalError("character class sequence has invalid children");
            }
            
            iterator = iterator.getNextSibling();
        } while (iterator != null);
        
        return charSet;
    }
    
    /**
     * 
     */
    private int[][] extractCCRange(ROMCCRange range) {
        if (range.getChildCount() != 2) {
            throw createInternalError("character class range has invalid number of children");
        }
        
        ROMNode child1 = range.getChild(0);
        ROMNode child2 = range.getChild(1);
        
        if (!child1.isCharRef() || !child2.isCharRef()) {
            throw createInternalError("character class range has invalid children");
        }
        
        char char1 = extractCharRef((ROMCharRef)child1);
        char char2 = extractCharRef((ROMCharRef)child2);
        
        if (char1 > char2) {
            throw createError(RegExCompileException.INVALID_CHAR_RANGE,"invalid char range");
        }
        
        return new int[][]{new int[]{char1,char2}};
    }
    
    /**
     * 
     */
    private MutableIntRangeSet computeCharSet(Expression expression,
            int errorType, String errorMessage) {
        
        int typeSet = expression.getTypeSet();
        
        if (ExpressionType.CONDITION.isContainedIn(typeSet) ||
            ExpressionType.LOOKAHEAD.isContainedIn(typeSet) ||
            ExpressionType.QUANTIFIER.isContainedIn(typeSet) ||
            ExpressionType.MODIFIER.isContainedIn(typeSet)) {
            
            throw createError(errorType,errorMessage);
        }
        
        MutableIntRangeSet charSet = new MutableIntRangeSet();
        
        computeCharSetRecursive(expression,charSet);
        
        return charSet;
    }
    
    /**
     * 
     */
    private void computeCharSetRecursive(Expression expression,
            MutableIntRangeSet charSet) {
        
        switch(expression.getType()) {
        case CHAR_CLASS:
            charSet.add(((CharClass)expression).getCharSet());
            break;
        
        case CONCATENATION:
        case ALTERNATION:
            // these expressions can simply be skipped as only their character
            // class descendants are relevant which are processed by the child
            // loop below
            break;
        
        case MODIFIER:
        case QUANTIFIER:
        case LOOKAHEAD:
        case CONDITION:
        default:
            throw createInternalError("invalid expression type");
        }
        
        int childCount = expression.getChildCount();
        
        for (int i=0;i<childCount;i++) {
            computeCharSetRecursive(expression.getChild(i),charSet);
        }
    }
    
    //=========================
    // String Sequence Methods
    //=========================
    
    /**
     * 
     */
    private Expression extractStringSequence(ROMStringSequence stringSequence) {
        int sequenceChildCount = stringSequence.getChildCount();
        
        if (sequenceChildCount == 0) {
            throw createInternalError("string sequence has no children");
        }
        
        ArrayList<Expression> list = new ArrayList<>(sequenceChildCount);
        ROMNode iterator = stringSequence.getFirstChild();
        
        do {
            if (iterator.isCharRef()) {
                list.add(Expression.forChar(extractCharRef((ROMCharRef)iterator)));
            }
            
            else if (iterator.isClassRef()) {
                list.add(Expression.forCharRangeSet(extractClassRef((ROMClassRef)iterator)));
            }
            
            else if (iterator.isSequenceRef()) {
                list.add(extractSequenceRef((ROMSequenceRef)iterator));
            }
            
            else {
                throw createInternalError("string sequence has invalid children");
            }
            
            iterator = iterator.getNextSibling();
        } while(iterator != null);
        
        return new Concatenation(list);
    }
    
    //==============================
    // Simple Compound Type Methods
    //==============================
    
    /**
     * 
     */
    private Condition extractCondition(ROMCondition condition) {
        if (condition.getChildCount() == 0) {
            return Condition.ALL;
        }
        
        if (condition.getChildCount() > 1) {
            throw createInternalError("condition has too many children");
        }
        
        ROMNode firstChild = condition.getFirstChild();
        
        if (!firstChild.isNameList()) {
            throw createInternalError("condition has invalid children");
        }
        
        return Condition.create(extractNameList((ROMNameList)firstChild));
    }
    
    /**
     * 
     */
    private String[] extractNameList(ROMNameList nameList) {
        int nameListChildCount = nameList.getChildCount();
        
        if (nameListChildCount == 0) {
            throw createInternalError("name list has no children");
        }
        
        ArrayList<String> list = new ArrayList<>(nameListChildCount);
        ROMNode iterator = nameList.getFirstChild();
        
        do {
            if (!iterator.isName()) {
                throw createInternalError("name list has invalid children");
            }
            
            list.add(((ROMName)iterator).getName());
            iterator = iterator.getNextSibling();
        } while (iterator != null);
        
        return list.toArray(SystemToolkit.EMPTY_STRING_ARRAY);
    }
    
    /**
     * 
     */
    private String extractMacro(ROMMacro macro) {
        if (macro.getChildCount() != 1) {
            throw createInternalError("macro has invalid number of children");
        }
        
        ROMNode firstChild = macro.getFirstChild();
        
        if (!firstChild.isName()) {
            throw createInternalError("macro has invalid children");
        }
        
        return ((ROMName)firstChild).getName();
    }
    
    //===================
    // Base Type Methods
    //===================
    
    /**
     * 
     */
    private char extractCharRef(ROMCharRef charRefNode) {
        CharRef charRef = charRefNode.getCharRef();
        
        switch(charRef.type()) {
        case VALUE:
            return charRef.charValue();
        
        case NAME:
            String name = charRef.charName();
            
            try {
                ensurePropertyResolver();
                
                Character character = propertyResolver.resolveToChar(
                        Property.NAME,name);
                
                if (character != null) {
                    return character;
                }
                
                throw createError(RegExCompileException.UNKNOWN_CHAR_NAME,
                        "unknown character name \""+name+"\"");
            }
            
            catch(PropertyException e) {
                if (e.getType() == PropertyException.INTERNAL_ERROR) {
                    throw new RegExCompileException("internal error",e);
                }
                
                throw createError(RegExCompileException.INVALID_CHAR_NAME,
                        "invalid character name \""+name+"\" ("+e.getMessage()+")");
            }
        
        default:
            throw createInternalError("invalid character reference type");
        }
    }
    
    /**
     * 
     */
    private ConstIntRangeSet extractClassRef(ROMClassRef classRefNode) {
        PropertySelector selector = classRefNode.getPropertySelector();
        
        try {
            ensurePropertyResolver();
            
            ConstIntRangeSet charSet = propertyResolver.resolveToCharSet(selector);
            
            if (charSet != null) {
                return charSet;
            }
            
            throw createError(RegExCompileException.UNKNOWN_CHAR_PROPERTY,
                    "unknown property \""+selector.getText()+"\"");
        }
        
        catch(PropertyException e) {
            if (e.getType() == PropertyException.INTERNAL_ERROR) {
                throw new RegExCompileException("internal error",e);
            }
            
            throw createError(RegExCompileException.INVALID_CHAR_PROPERTY,
                    "invalid property \""+selector.getText()+"\" ("+e.getMessage()+")");
        }
    }
    
    /**
     * 
     */
    private Expression extractSequenceRef(ROMSequenceRef sequenceRefNode) {
        SequenceRef sequenceRef = sequenceRefNode.getSequenceRef();
        
        switch(sequenceRef.type()) {
        case NEWLINE:
            return Expression.UNICODE_LINE_TERMINATOR;
        
        case QUOTE:
            return Expression.forString(sequenceRef.quoteValue());
        
        default:
            throw createInternalError("invalid sequence reference type");
        }
    }
    
    //========================
    // Macro Resolver Methods
    //========================
    
    /**
     * 
     */
    private Expression resolveMacro(String macroName) {
        boolean unknownExpression = macroResolver == null ||
                !macroResolver.containsMacro(macroName);
        
        if (unknownExpression) {
            throw createError(RegExCompileException.UNKNOWN_MACRO,"unknown macro");
        }
        
        Expression expression = macroResolver.resolveMacro(macroName);
        
        if (expression == null) {
            throw createError(RegExCompileException.INVALID_MACRO,"invalid macro");
        }
        
        return expression;
    }
    
    //===========================
    // Property Resolver Methods
    //===========================
    
    /**
     * 
     */
    private void ensurePropertyResolver() {
        if (propertyResolver == null) {
            propertyResolver = new PropertyResolver();
        }
    }
    
    //===================
    // Exception Methods
    //===================
    
    /**
     * 
     */
    private RegExCompileException createError(int type, String message) {
        return new RegExCompileException(type,message);
    }
    
    /**
     * 
     */
    private RegExCompileException createInternalError(String message) {
        return new RegExCompileException(RegExCompileException.INTERNAL_ERROR,
                message);
    }
}
