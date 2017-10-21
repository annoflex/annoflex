/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import org.annoflex.util.problem.Problem;

/**
 * @author Stefan Czaska
 */
public class Problems {
    
    //====================
    // Automaton Problems
    //====================
    
    public static final Problem NO_INITIAL_LEX_STATE = new Problem("s",Problems.class,"noInitialLexState");
    public static final Problem EMPTY_WORD_EXPR      = new Problem("s",Problems.class,"emptyWordExpr");
    public static final Problem REDUNDANT_EXPR       = new Problem("w",Problems.class,"redundantExpr");
}
