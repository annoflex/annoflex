/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import org.annoflex.util.problem.Problem;

/**
 * @author Stefan Czaska
 */
public class Problems {
    
    //=============================
    // Scanner Definition Problems
    //=============================
    
    public static final Problem UNKNOWN_INSTRUCTION    = new Problem("s",Problems.class,"unknownInstruction");
    public static final Problem NO_CODE_AREA_DEFINED   = new Problem("s",Problems.class,"noCodeAreaDefined");
    public static final Problem INVALID_CODE_AREA      = new Problem("s",Problems.class,"invalidCodeArea");
    public static final Problem INSIDE_CODE_AREA       = new Problem("s",Problems.class,"insideCodeArea");
    public static final Problem NO_CLASS_DEFINED       = new Problem("s",Problems.class,"noClassDefined");
    public static final Problem INVALID_CONDITION_AREA = new Problem("s",Problems.class,"invalidConditionArea");
    public static final Problem INVALID_OPTION         = new Problem("s",Problems.class,"invalidOption");
    public static final Problem UNKNOWN_OPTION         = new Problem("w",Problems.class,"unknownOption");
    public static final Problem INVALID_OPTION_VALUE   = new Problem("s",Problems.class,"invalidOptionValue");
    public static final Problem INVALID_MACRO          = new Problem("s",Problems.class,"invalidMacro");
    public static final Problem INVALID_MACRO_NAME     = new Problem("s",Problems.class,"invalidMacroName");
    public static final Problem INVALID_MACRO_VALUE    = new Problem("s",Problems.class,"invalidMacroValue");
    public static final Problem NO_RULES_DEFINED       = new Problem("s",Problems.class,"noRulesDefined");
    public static final Problem INVALID_EXPRESSION     = new Problem("s",Problems.class,"invalidExpression");
    public static final Problem TOO_MANY_EXPR_TAGS     = new Problem("s",Problems.class,"tooManyExprTags");
    public static final Problem AMBIGUOUS_RETURN_TYPES = new Problem("s",Problems.class,"ambiguousReturnType");
    public static final Problem UNUSED_TAG             = new Problem("w",Problems.class,"unusedTag");
    public static final Problem TOO_MANY_DFA_STATES    = new Problem("s",Problems.class,"tooManyDFAStates");
}
