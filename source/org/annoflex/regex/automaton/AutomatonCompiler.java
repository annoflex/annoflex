/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.annoflex.regex.Alternation;
import org.annoflex.regex.CharClass;
import org.annoflex.regex.Concatenation;
import org.annoflex.regex.Condition;
import org.annoflex.regex.ConditionExpression;
import org.annoflex.regex.Expression;
import org.annoflex.regex.ExpressionType;
import org.annoflex.regex.Lookahead;
import org.annoflex.regex.ModifierExpression;
import org.annoflex.regex.QuantifierExpression;
import org.annoflex.util.LongBreak;
import org.annoflex.util.SystemToolkit;
import org.annoflex.util.integer.IdSet;
import org.annoflex.util.problem.ErrorHandler;

/**
 * @author Stefan Czaska
 */
public class AutomatonCompiler<A> {
    
    //=================
    // Property Fields
    //=================
    
    /**
     * 
     */
    private ErrorHandler<Rule<A>> errorHandler;
    
    /**
     * 
     */
    private boolean errors;
    
    //==============
    // State Fields
    //==============
    
    /**
     * 
     */
    private Alphabet alphabet;
    
    /**
     * 
     */
    private final LinkedHashSet<String> conditionNameSet = new LinkedHashSet<>();
    
    /**
     * 
     */
    private String[] conditionNameArray;
    
    /**
     * 
     */
    private final ArrayList<NFAInfo<A>> nfaInfoList = new ArrayList<>();
    
    /**
     * 
     */
    private NameMap nameMap;
    
    /**
     * 
     */
    private ActionPool<A> actionPool;
    
    /**
     * 
     */
    private Rule<A> curRule;
    
    /**
     * 
     */
    private final ArrayList<Condition> conditionStack = new ArrayList<>();
    
    /**
     * 
     */
    private final IdSet conditionIds = new IdSet();
    
    /**
     * 
     */
    private final ArrayList<NFAInfo<A>> endStateClearList = new ArrayList<>();
    
    /**
     * 
     */
    private final HashMap<Lookahead,Integer> endPositionNFACache = new HashMap<>();
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setErrorHandler(ErrorHandler<Rule<A>> errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    /**
     * 
     */
    public ErrorHandler<Rule<A>> getErrorHandler() {
        return errorHandler;
    }
    
    /**
     * 
     */
    public boolean hasErrors() {
        return errors;
    }
    
    //=================
    // Compile Methods
    //=================
    
    /**
     * 
     */
    public Automaton<A> compile(ArrayList<Rule<A>> ruleList) {
        errors = false;
        alphabet = null;
        conditionNameSet.clear();
        conditionNameArray = null;
        nfaInfoList.clear();
        nameMap = null;
        actionPool = null;
        curRule = null;
        conditionStack.clear();
        conditionIds.clear();
        endStateClearList.clear();
        
        // validate rule list
        Rule<A>[] ruleArray = validateRuleList(ruleList);
        
        // initialize
        alphabet = new Alphabet(ruleArray);
        nameMap = new NameMap();
        actionPool = new ActionPool<>();
        
        // determine condition names
        determineConditionNames(ruleArray);
        
        if (!conditionNameSet.contains(Condition.NAME_INITIAL)) {
            errors |= Problems.NO_INITIAL_LEX_STATE.report(errorHandler);
            return null;
        }
        
        conditionNameArray = conditionNameSet.toArray(SystemToolkit
                .EMPTY_STRING_ARRAY);
        
        // create NFA for all condition names
        for (int i=0;i<conditionNameArray.length;i++) {
            String conditionName = conditionNameArray[i];
            
            NFA<A> nfa = new NFA<>(alphabet);
            nfa.setStartState(nfa.createState());
            
            NFAInfo<A> nfaInfo = new NFAInfo<>();
            nfaInfo.nfa = nfa;
            nfaInfo.conditionName = conditionName;
            nfaInfoList.add(nfaInfo);
            
            nameMap.put(conditionName,nfaInfoList.size()-1);
        }
        
        // process rules
        for (int i=0;i<ruleArray.length;i++) {
            curRule = ruleArray[i];
            
            int size = endStateClearList.size();
            
            for (int j=0;j<size;j++) {
                NFAInfo<A> curInfo = endStateClearList.get(j);
                curInfo.defaultEndState = null;
                
                if (curInfo.fixConditionEndStates != null) {
                    curInfo.fixConditionEndStates.clear();
                }
                
                if (curInfo.fixContentEndStates != null) {
                    curInfo.fixContentEndStates.clear();
                }
            }
            
            endStateClearList.clear();
            conditionStack.clear();
            
            try {
                appendTopLevelExpression(curRule.getExpression().normalize());
            }
            
            catch(SkipRule e) {
            }
        }
        
        if (errors) {
            return null;
        }
        
        // transform all NFAs into minimal DFAs
        DFAList<A> dfaList = new DFAList<>();
        int nfaInfoListSize = nfaInfoList.size();
        int totalNFAStateCount = 0;
        
        for (int i=0;i<nfaInfoListSize;i++) {
            NFA<A> curNFA = nfaInfoList.get(i).nfa;
            
            dfaList.add(curNFA.toDFA(false).toMinimumDFA());
            totalNFAStateCount += curNFA.getStateCount();
        }
        
        // detect unused actions
        detectUnusedActions(dfaList);
        
        return new Automaton<>(alphabet,nameMap,actionPool,
                totalNFAStateCount,dfaList);
    }
    
    //==============================
    // Rule List Validation Methods
    //==============================
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private Rule<A>[] validateRuleList(ArrayList<Rule<A>> ruleList) {
        if (ruleList == null) {
            throw new IllegalArgumentException("rule list may not be null");
        }
        
        if (ruleList.isEmpty()) {
            throw new IllegalArgumentException("rule list may not be empty");
        }
        
        int ruleListSize = ruleList.size();
        Rule<A>[] ruleArray = new Rule[ruleListSize];
        
        for (int i=0;i<ruleListSize;i++) {
            Rule<A> curRule = ruleList.get(i);
            
            if (curRule == null) {
                throw new IllegalArgumentException("rule list may not " +
                        "contain null rules");
            }
            
            ruleArray[i] = curRule;
        }
        
        return ruleArray;
    }
    
    //========================
    // Condition Name Methods
    //========================
    
    /**
     * 
     */
    private void determineConditionNames(Rule<A>[] ruleArray) {
        for (int i=0;i<ruleArray.length;i++) {
            Expression expression = ruleArray[i].getExpression().normalize();
            
            // only expressions which actually have condition expressions
            // require a recursive search
            if (ExpressionType.CONDITION.isContainedIn(expression.getTypeSet())) {
                determineConditionNamesRecursive(expression,false);
            }
            
            // otherwise use "initial" as default
            else {
                conditionNameSet.add(Condition.NAME_INITIAL);
            }
        }
    }
    
    /**
     * 
     */
    private void determineConditionNamesRecursive(Expression expression,
            boolean insideCondition) {
        
        switch(expression.getType()) {
        case CHAR_CLASS:
        case MODIFIER:
        case QUANTIFIER:
        case CONCATENATION:
        case LOOKAHEAD:
            
            // leaf expressions which are not contained in a condition
            // expression have "initial" as their condition
            if (!insideCondition) {
                conditionNameSet.add(Condition.NAME_INITIAL);
            }
            break;
        
        case ALTERNATION:
            int childCount = expression.getChildCount();
            
            for (int i=0;i<childCount;i++) {
                determineConditionNamesRecursive(expression.getChild(i),
                        insideCondition);
            }
            break;
        
        case CONDITION:
            ConditionExpression conditionExpression = (ConditionExpression)expression;
            Condition condition = conditionExpression.getCondition();
            int nameCount = condition.getNameCount();
            
            for (int i=0;i<nameCount;i++) {
                String curName = condition.getName(i);
                
                conditionNameSet.add(curName.equals(Condition.NAME_ALL) ?
                        Condition.NAME_INITIAL : curName);
            }
            
            determineConditionNamesRecursive(conditionExpression.getExpression(),true);
            break;
        
        default:
            throw new IllegalStateException("unknown expression type");
        }
    }
    
    //==========================
    // Top Level Append Methods
    //==========================
    
    /**
     * 
     */
    private void appendTopLevelExpression(Expression expression) {
        switch(expression.getType()) {
        case CHAR_CLASS:
        case MODIFIER:
        case QUANTIFIER:
        case CONCATENATION:
        case LOOKAHEAD:
            determineConditionsIds();
            int iterator = conditionIds.first();
            
            while (iterator != -1) {
                if (expression.isLookahead()) {
                    appendLookaheadExpression((Lookahead)expression,iterator);
                }
                
                else {
                    appendSimpleExpression(expression,iterator);
                }
                
                iterator = conditionIds.next(iterator);
            }
            break;
        
        case ALTERNATION:
            int childCount = expression.getChildCount();
            
            for (int i=0;i<childCount;i++) {
                appendTopLevelExpression(expression.getChild(i));
            }
            break;
        
        case CONDITION:
            ConditionExpression conditionExpression = (ConditionExpression)expression;
            conditionStack.add(conditionExpression.getCondition());
            
            appendTopLevelExpression(conditionExpression.getExpression());
            
            conditionStack.remove(conditionStack.size()-1);
            break;
        
        default:
            throw new IllegalStateException("unknown expression type");
        }
    }
    
    /**
     * 
     */
    private void determineConditionsIds() {
        conditionIds.clear();
        int size = conditionStack.size();
        
        if (size == 0) {
            conditionIds.add(nameMap.get(Condition.NAME_INITIAL));
        }
        
        else {
            ConditionLoop:
            for (int i=0;i<size;i++) {
                Condition curCondition = conditionStack.get(i);
                int nameCount = curCondition.getNameCount();
                
                for (int j=0;j<nameCount;j++) {
                    String curName = curCondition.getName(j);
                    
                    if (curName.equals(Condition.NAME_ALL)) {
                        for (int k=0;k<conditionNameArray.length;k++) {
                            conditionIds.add(nameMap.get(conditionNameArray[k]));
                        }
                        break ConditionLoop;
                    }
                    
                    conditionIds.add(nameMap.get(curName));
                }
            }
        }
    }
    
    /**
     * 
     */
    private void appendSimpleExpression(Expression expression, int nfaIndex) {
        NFAInfo<A> nfaInfo = nfaInfoList.get(nfaIndex);
        NFA<A> nfa = nfaInfo.nfa;
        NFAState<A> startState = nfa.createState();
        NFAState<A> endState = createDefaultEndState(nfaInfo);
        
        appendExpression(expression,startState,endState,nfa);
        
        if (nfa.isEpsilonReachable(startState,endState)) {
            errors |= Problems.EMPTY_WORD_EXPR.report(errorHandler,curRule);
            throw new SkipRule();
        }
        
        nfa.getStartState().addEpsilonTransition(startState);
    }
    
    //==============================
    // Lookahead Expression Methods
    //==============================
    
    /**
     * 
     */
    private void appendLookaheadExpression(Lookahead expression, int nfaIndex) {
        NFAInfo<A> nfaInfo = nfaInfoList.get(nfaIndex);
        ArrayList<Expression> conditionList = getFixLengthExpressionList(
                expression.getCondition());
        
        // Case 1: fix length condition expressions
        if (conditionList != null) {
            appendFixConditionLookaheadExpression(expression,nfaInfo,conditionList);
        }
        
        else {
            ArrayList<Expression> contentList = getFixLengthExpressionList(
                    expression.getContent());
            
            // Case 2: fix length content expressions
            if (contentList != null) {
                appendFixContentLookaheadExpression(expression,nfaInfo,contentList);
            }
            
            // Case 3: variable content and condition expressions
            else {
                appendVariableLookaheadExpression(expression,nfaInfo.nfa);
            }
        }
    }
    
    /**
     * 
     */
    private ArrayList<Expression> getFixLengthExpressionList(Expression expression) {
        if (expression.isAlternation()) {
            return getFixLengthExpressionListRecursive((Alternation)expression,
                    new ArrayList<Expression>());
        }
        
        if (expression.getWordLength() > 0) {
            ArrayList<Expression> list = new ArrayList<>(1);
            list.add(expression);
            
            return list;
        }
        
        return null;
    }
    
    /**
     * 
     */
    private ArrayList<Expression> getFixLengthExpressionListRecursive(
            Alternation alternation, ArrayList<Expression> list) {
        
        int childCount = alternation.getChildCount();
        
        for (int i=0;i<childCount;i++) {
            Expression childExpression = alternation.getChild(i);
            
            if (childExpression.isAlternation()) {
                list = getFixLengthExpressionListRecursive(
                        (Alternation)childExpression,list);
                
                if (list == null) {
                    return null;
                }
            }
            
            else {
                if (childExpression.getWordLength() < 0) {
                    return null;
                }
                
                list.add(childExpression);
            }
        }
        
        return list;
    }
    
    /**
     * 
     */
    private void appendFixConditionLookaheadExpression(Lookahead expression,
            NFAInfo<A> nfaInfo, ArrayList<Expression> conditionList) {
        
        // Append content expression. Use custom start state in order to be able
        // to properly check for the empty word on a variable content expression.
        NFA<A> nfa = nfaInfo.nfa;
        NFAState<A> startState = nfa.createState();
        NFAState<A> midState = nfa.createState();
        Expression contentExpression = expression.getContent();
        
        appendExpression(contentExpression,startState,midState,nfa);
        
        // check for empty word
        if ((contentExpression.getWordLength() < 0) &&
            nfa.isEpsilonReachable(startState,midState)) {
            
            errors |= Problems.EMPTY_WORD_EXPR.report(errorHandler,curRule);
            throw new SkipRule();
        }
        
        // sort condition expressions by their length
        Collections.sort(conditionList,Expression.LENGTH_COMPARATOR);
        
        // create for each length an end state and append the corresponding
        // expressions
        int size = conditionList.size();
        NFAState<A> lastEndState = null;
        int lastLength = -1;
        
        // Important: Iterate forwards in order or prioritize smaller condition
        // expressions. This is necessary in order to get always the longest
        // content match.
        for (int i=0;i<size;i++) {
            Expression curExpression = conditionList.get(i);
            int length = curExpression.getWordLength();
            
            if (lastLength != length) {
                lastEndState = createFixConditionEndState(nfaInfo,length);
                lastLength = length;
            }
            
            appendExpression(curExpression,midState,lastEndState,nfa);
        }
        
        // integrate into NFA via epsilon transition
        nfa.getStartState().addEpsilonTransition(startState);
    }
    
    /**
     * 
     */
    private void appendFixContentLookaheadExpression(Lookahead expression,
            NFAInfo<A> nfaInfo, ArrayList<Expression> contentList) {
        
        // sort content expressions by their length
        Collections.sort(contentList,Expression.LENGTH_COMPARATOR);
        
        NFA<A> nfa = nfaInfo.nfa;
        int size = contentList.size();
        NFAState<A> lastMidState = null;
        NFAState<A> lastEndState = null;
        NFAState<A> startState = nfa.getStartState();
        Expression conditionExpression = expression.getCondition();
        int lastLength = -1;
        
        // Important: Iterate backwards in order or prioritize larger content
        // expressions. This is necessary in order to get always the longest
        // content match.
        for (int i=size-1;i>=0;i--) {
            Expression curContentExpression = contentList.get(i);
            int length = curContentExpression.getWordLength();
            
            if (lastLength != length) {
                lastMidState = nfa.createState();
                lastEndState = createFixContentEndState(nfaInfo,length);
                lastLength = length;
            }
            
            // Append content expression. Checking for the empty word is not
            // necessary as expressions with a fix length of zero make now
            // sense and thus never occur. This is ensured by the expression
            // object model.
            appendExpression(curContentExpression,startState,lastMidState,nfa);
            
            // append condition expression
            appendExpression(conditionExpression,lastMidState,lastEndState,nfa);
        }
    }
    
    /**
     * 
     */
    private void appendVariableLookaheadExpression(Lookahead expression,
            NFA<A> nfa) {
        
        // get expressions
        Expression contentExpression = expression.getContent();
        Expression conditionExpression = expression.getCondition();
        
        // create states
        NFAState<A> startState = nfa.createState();
        NFAState<A> midState = nfa.createState();
        NFAState<A> endState = nfa.createState();
        
        // append content expression
        appendExpression(contentExpression,startState,midState,nfa);
        
        // check for empty word
        if (nfa.isEpsilonReachable(startState,midState)) {
            errors |= Problems.EMPTY_WORD_EXPR.report(errorHandler,curRule);
            throw new SkipRule();
        }
        
        // append condition expression
        appendExpression(conditionExpression,midState,endState,nfa);
        
        // integrate into main NFA via epsilon transition
        nfa.getStartState().addEpsilonTransition(startState);
        
        // create end position NFAs
        Integer contentNFAIndex = endPositionNFACache.get(expression);
        
        if (contentNFAIndex == null) {
            contentNFAIndex = createEndPositionNFAs(expression);
            endPositionNFACache.put(expression,contentNFAIndex);
        }
        
        // set action of end state
        endState.setAction(createAction(Action.LOOKAHEAD_VARIABLE,
                contentNFAIndex));
    }
    
    /**
     * 
     */
    private int createEndPositionNFAs(Lookahead lookahead) {
        
        // create an additional NFA for the content expression which is used to
        // determine the position list
        NFA<A> contentNFA = new NFA<>(alphabet);
        
        NFAInfo<A> contentNFAInfo = new NFAInfo<>();
        contentNFAInfo.nfa = contentNFA;
        nfaInfoList.add(contentNFAInfo);
        
        int contentNFAIndex = nfaInfoList.size() - 1;
        
        NFAState<A> contentStartState = contentNFA.createState();
        contentNFA.setStartState(contentStartState);
        
        NFAState<A> contentEndState = contentNFA.createState();
        contentEndState.setAction(createAction(Action.LOOKAHEAD_FORWARD_PASS,
                contentNFAIndex));
        
        appendExpression(lookahead.getContent(),contentStartState,
                contentEndState,contentNFA);
        
        // create an additional NFA for the condition expression which is used
        // to determine the longest content match based on the position list
        NFA<A> conditionNFA = new NFA<>(alphabet);
        
        NFAInfo<A> conditionNFAInfo = new NFAInfo<>();
        conditionNFAInfo.nfa = conditionNFA;
        nfaInfoList.add(conditionNFAInfo);
        
        NFAState<A> conditionStartState = conditionNFA.createState();
        conditionNFA.setStartState(conditionStartState);
        
        NFAState<A> conditionEndState = conditionNFA.createState();
        conditionEndState.setAction(createAction(Action.LOOKAHEAD_BACKWARD_PASS,
                contentNFAIndex));
        
        appendExpression(lookahead.getCondition().reverse(),conditionStartState,
                conditionEndState,conditionNFA);
        
        return contentNFAIndex;
    }
    
    //============================
    // End State Creation Methods
    //============================
    
    /**
     * 
     */
    private NFAState<A> createDefaultEndState(NFAInfo<A> nfaInfo) {
        NFAState<A> endState = nfaInfo.defaultEndState;
        
        if (endState == null) {
            endState = nfaInfo.nfa.createState();
            endState.setAction(createAction(Action.LOOKAHEAD_NONE,0));
            nfaInfo.defaultEndState = endState;
            endStateClearList.add(nfaInfo);
        }
        
        return endState;
    }
    
    /**
     * 
     */
    private NFAState<A> createFixConditionEndState(NFAInfo<A> nfaInfo,
            int length) {
        
        if (nfaInfo.fixConditionEndStates == null) {
            nfaInfo.fixConditionEndStates = new HashMap<>();
        }
        
        NFAState<A> endState = nfaInfo.fixConditionEndStates.get(length);
        
        if (endState == null) {
            endState = nfaInfo.nfa.createState();
            endState.setAction(createAction(Action.LOOKAHEAD_FIX_CONDITION,length));
            nfaInfo.fixConditionEndStates.put(length,endState);
            endStateClearList.add(nfaInfo);
        }
        
        return endState;
    }
    
    /**
     * 
     */
    private NFAState<A> createFixContentEndState(NFAInfo<A> nfaInfo,
            int length) {
        
        if (nfaInfo.fixContentEndStates == null) {
            nfaInfo.fixContentEndStates = new HashMap<>();
        }
        
        NFAState<A> endState = nfaInfo.fixContentEndStates.get(length);
        
        if (endState == null) {
            endState = nfaInfo.nfa.createState();
            endState.setAction(createAction(Action.LOOKAHEAD_FIX_CONTENT,length));
            nfaInfo.fixContentEndStates.put(length,endState);
            endStateClearList.add(nfaInfo);
        }
        
        return endState;
    }
    
    //=========================
    // Base Expression Methods
    //=========================
    
    /**
     * 
     */
    private void appendExpression(Expression expression, NFAState<A> startState,
                    NFAState<A> endState, NFA<A> nfa) {
        
        switch(expression.getType()) {
        case CHAR_CLASS:
            appendCharClass((CharClass)expression,startState,endState);
            break;
        
        case MODIFIER:
            appendModifierExpression((ModifierExpression)expression,
                    startState,endState,nfa);
            break;
        
        case QUANTIFIER:
            appendQuantifierExpression((QuantifierExpression)expression,
                    startState,endState,nfa);
            break;
        
        case CONCATENATION:
            appendConcatenation((Concatenation)expression,startState,endState,
                    nfa);
            break;
        
        case ALTERNATION:
            appendAlternation((Alternation)expression,startState,endState,
                    nfa);
            break;
        
        case LOOKAHEAD:
        case CONDITION:
            throw new IllegalStateException("invalid expression type");
        
        default:
            throw new IllegalStateException("unknown expression type");
        }
    }
    
    /**
     * 
     */
    private void appendCharClass(CharClass charClass, NFAState<A> startState,
            NFAState<A> endState) {
        
        startState.putConditionalTransition(alphabet.getSymbols(charClass
                .getCharSet()),endState);
    }
    
    /**
     * 
     */
    private void appendModifierExpression(ModifierExpression modifier,
            NFAState<A> startState, NFAState<A> endState, NFA<A> nfa) {
        
        switch(modifier.getModifier()) {
        case NOT:
            
            // create a NFA for the content expression
            NFA<A> contentNFA = new NFA<>(nfa.getAlphabet());
            contentNFA.setStartState(contentNFA.createState());
            NFAState<A> contentNFAEndState = contentNFA.createState();
            
            appendExpression(modifier.getExpression(),contentNFA
                    .getStartState(),contentNFAEndState,contentNFA);
            
            // transform the NFA into a DFA
            DFA<A> dfa = contentNFA.toDFA(true);
            
            // create the complement of the DFA
            ComplementResult result = dfa.complement(contentNFAEndState);
            
            // release NFA context
            dfa.releaseNFAContext();
            
            // integrate the complement into the parent NFA
            nfa.integrateDFA(dfa,result.getLivingStates(),
                    result.getEndStates(),startState,endState);
            break;
        
        case UNTIL:
            throw new IllegalStateException("expression tree is not normalized");
        
        default:
            throw new IllegalStateException("unknown modifier type");
        }
    }
    
    /**
     * 
     */
    private void appendQuantifierExpression(QuantifierExpression quantifier,
            NFAState<A> startState, NFAState<A> endState, NFA<A> nfa) {
        
        Expression expression = quantifier.getExpression();
        int min = quantifier.getMin();
        int max = quantifier.getMax();
        
        // exactly n times
        if (max == min) {
            for (int i=0;i<min-1;i++) {
                NFAState<A> newState = nfa.createState();
                
                appendExpression(expression,startState,newState,nfa);
                
                startState = newState;
            }
            
            appendExpression(expression,startState,endState,nfa);
        }
        
        // unlimited maximum
        else if (max == Integer.MAX_VALUE) {
            NFAState<A> state1 = nfa.createState();
            NFAState<A> state2 = nfa.createState();
            
            appendExpression(expression,state1,state2,nfa);
            state2.addEpsilonTransition(state1);
            state2.addEpsilonTransition(endState);
            
            if (min <= 1) {
                startState.addEpsilonTransition(state1);
                
                if (min == 0) {
                    state1.addEpsilonTransition(state2);
                }
            }
            
            else {
                int numberOfStates = min - 2;
                
                for (int i=0;i<numberOfStates;i++) {
                    NFAState<A> newState = nfa.createState();
                    
                    appendExpression(expression,startState,newState,nfa);
                    
                    startState = newState;
                }
                
                appendExpression(expression,startState,state1,nfa);
            }
        }
        
        // fix maximum
        else {
            for (int i=0;i<min;i++) {
                NFAState<A> newState = nfa.createState();
                
                appendExpression(expression,startState,newState,nfa);
                
                startState = newState;
            }
            
            int numberOfStates = max - min - 1;
            
            for (int i=0;i<numberOfStates;i++) {
                NFAState<A> newState = nfa.createState();
                
                appendExpression(expression,startState,newState,nfa);
                startState.addEpsilonTransition(endState);
                
                startState = newState;
            }
            
            appendExpression(expression,startState,endState,nfa);
            startState.addEpsilonTransition(endState);
        }
    }
    
    /**
     * 
     */
    private void appendConcatenation(Concatenation concatenation,
            NFAState<A> startState, NFAState<A> endState, NFA<A> nfa) {
        
        int size = concatenation.getChildCount();
        NFAState<A> curStartState = startState;
        
        for (int i=0;i<size-1;i++) {
            NFAState<A> state = nfa.createState();
            
            appendExpression(concatenation.getChild(i),curStartState,state,
                    nfa);
            
            curStartState = state;
        }
        
        appendExpression(concatenation.getChild(size-1),curStartState,
                        endState,nfa);
    }
    
    /**
     * 
     */
    private void appendAlternation(Alternation alternation, NFAState<A> startState,
            NFAState<A> endState, NFA<A> nfa) {
        
        int size = alternation.getChildCount();
        
        for (int i=0;i<size;i++) {
            appendExpression(alternation.getChild(i),startState,endState,nfa);
        }
    }
    
    //================
    // Action Methods
    //================
    
    /**
     * 
     */
    private Action<A> createAction(int lookaheadType, int lookaheadValue) {
        return actionPool.createAction(curRule,lookaheadType,lookaheadValue);
    }
    
    /**
     * 
     */
    private void detectUnusedActions(DFAList<A> dfaList) {
        IdSet actionIds = getAllActionIds(dfaList);
        
        for (int i=0;i<actionPool.size();i++) {
            Action<A> curAction = actionPool.get(i);
            
            if (!actionIds.contains(curAction.getId())) {
                errors |= Problems.REDUNDANT_EXPR.report(errorHandler,
                        curAction.getOwnerRule());
            }
        }
    }
    
    /**
     * 
     */
    private IdSet getAllActionIds(DFAList<A> dfaList) {
        IdSet actionSet = new IdSet();
        int size = dfaList.size();
        
        for (int i=0;i<size;i++) {
            DFA<A> curDFA = dfaList.get(i);
            int stateCount = curDFA.getStateCount();
            
            for (int j=0;j<stateCount;j++) {
                Action<A> curAction = curDFA.getState(j).getAction();
                
                if (curAction != null) {
                    actionSet.add(curAction.getId());
                }
            }
        }
        
        return actionSet;
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class NFAInfo<A> {
        
        /**
         * 
         */
        public NFA<A> nfa;
        
        /**
         * 
         */
        public String conditionName;
        
        /**
         * 
         */
        public NFAState<A> defaultEndState;
        
        /**
         * 
         */
        public HashMap<Integer,NFAState<A>> fixConditionEndStates;
        
        /**
         * 
         */
        public HashMap<Integer,NFAState<A>> fixContentEndStates;
    }
    
    /**
     * 
     */
    static final class SkipRule extends LongBreak {
    }
}
