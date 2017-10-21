/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.codegen;

import java.util.Arrays;

import org.annoflex.app.dom.MethodInfo;
import org.annoflex.regex.automaton.Action;
import org.annoflex.regex.automaton.ActionPool;
import org.annoflex.regex.automaton.Automaton;
import org.annoflex.regex.automaton.DFA;
import org.annoflex.regex.automaton.DFAList;
import org.annoflex.regex.automaton.DFAState;
import org.annoflex.regex.automaton.NameMap;
import org.annoflex.util.SystemToolkit;

/**
 * @author Stefan Czaska
 */
public class AutomatonInfo {
    
    //=================
    // Alphabet Fields
    //=================
    
    /**
     * 
     */
    private final int alphabetLength;
    
    /**
     * 
     */
    private String characterMapString;
    
    /**
     * 
     */
    private int characterMapSize;
    
    /**
     * 
     */
    private int characterMapLastValue;
    
    //==============
    // State Fields
    //==============
    
    /**
     * 
     */
    private final NameMap nameMap;
    
    /**
     * 
     */
    private int totalNFAStateCount;
    
    /**
     * 
     */
    private int totalDFAStateCount;
    
    /**
     * 
     */
    private int[] actionIdList;
    
    /**
     * 
     */
    private int[] startStateList;
    
    /**
     * 
     */
    private String transitionTableString;
    
    //===============
    // Action Fields
    //===============
    
    /**
     * 
     */
    private final ActionPool<MethodInfo> actionPool;
    
    /**
     * 
     */
    private int[] actionReorderMap;
    
    /**
     * 
     */
    private String actionMapString;
    
    /**
     * 
     */
    private int actionMapSize;
    
    /**
     * 
     */
    private boolean hasNormalActions;
    
    /**
     * 
     */
    private boolean hasFixConditionLookaheads;
    
    /**
     * 
     */
    private boolean hasFixContentLookaheads;
    
    /**
     * 
     */
    private boolean hasVariableLookaheads;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public AutomatonInfo(Automaton<MethodInfo> automaton) {
        alphabetLength = automaton.getAlphabet().getLength();
        nameMap = automaton.getNameMap();
        actionPool = automaton.getActionPool();
        
        computeStateData(automaton);
        computeCharacterData(automaton);
        computeActionReorderMap();
        computeActionMap();
        computeActionProperties();
    }
    
    //========================
    // Initialization Methods
    //========================
    
    /**
     * 
     */
    private void computeStateData(Automaton<MethodInfo> automaton) {
        DFAList<MethodInfo> dfaList = automaton.getDFAList();
        int dfaListSize = dfaList.size();
        int totalDFAStateCount = dfaList.getTotalStateCount();
        int[][] stateList = new int[totalDFAStateCount][];
        int[] actionIdList = new int[totalDFAStateCount];
        int[] startStateList = new int[dfaListSize];
        int stateCounter = 0;
        int stateIdBase = 0;
        
        for (int i=0;i<dfaListSize;i++) {
            DFA<MethodInfo> curDFA = dfaList.get(i);
            int stateCount = curDFA.getStateCount();
            
            startStateList[i] = stateIdBase + curDFA.getStartState().getId();
            
            for (int j=0;j<stateCount;j++) {
                DFAState<MethodInfo> curState = curDFA.getState(j);
                
                int[] transitions = new int[alphabetLength];
                stateList[stateCounter] = transitions;
                
                for (int k=0;k<alphabetLength;k++) {
                    DFAState<MethodInfo> destState = curState.getConditionalTransition(k);
                    
                    transitions[k] = destState != null ? stateIdBase + destState.getId() : -1;
                }
                
                Action<MethodInfo> stateAction = curState.getAction();
                actionIdList[stateCounter] = stateAction != null ? stateAction.getId() : -1;
                
                stateCounter++;
            }
            
            stateIdBase += stateCount;
        }
        
        this.totalNFAStateCount = automaton.getTotalNFAStateCount();
        this.totalDFAStateCount = totalDFAStateCount;
        this.actionIdList = actionIdList;
        this.startStateList = startStateList;
        
        // create transition table string
        StringBuilder builder = new StringBuilder();
        
        for (int i=0;i<stateList.length;i++) {
            appendCompressedArray(stateList[i],getTransitionTableOffset(),builder);
        }
        
        transitionTableString = builder.toString();
    }
    
    /**
     * 
     */
    private void computeCharacterData(Automaton<MethodInfo> automaton) {
        char[] symbolMap = automaton.getAlphabet().toSymbolMap();
        char[] compressedSymbolMap = compress(symbolMap,getCharacterMapOffset());
        
        // enable last counter removal optimization if at least one page of
        // memory can be saved
        int characterMapSize = 65536;
        int characterMapLastValue = -1;
        int lastCharacterCounter = compressedSymbolMap[compressedSymbolMap.length-1];
        int charactersPerPage = SystemToolkit.SIZE_OF_MEMORY_PAGE / getCharacterMapElementSize();
        
        if (lastCharacterCounter >= charactersPerPage) {
            characterMapSize -= lastCharacterCounter;
            characterMapLastValue = compressedSymbolMap[compressedSymbolMap.length-2];
            compressedSymbolMap = Arrays.copyOf(compressedSymbolMap,compressedSymbolMap.length-2);
        }
        
        this.characterMapSize = characterMapSize;
        this.characterMapLastValue = characterMapLastValue;
        characterMapString = new String(compressedSymbolMap);
    }
    
    /**
     * 
     */
    private void computeActionReorderMap() {
        int actionCount = actionPool.size();
        int[] actionReorderMap = new int[actionCount];
        int lastId = actionCount - 1;
        
        for (int i=0,j=0;i<actionCount;i++) {
            
            // Note: Move all forward and backward pass actions to the end of
            // the list in order to have continuous action numbers for all
            // "visible" actions in the action switch.
            switch(actionPool.get(i).getLookaheadType()) {
            case Action.LOOKAHEAD_FORWARD_PASS:
            case Action.LOOKAHEAD_BACKWARD_PASS:
                actionReorderMap[i] = lastId--;
                break;
            
            default:
                actionReorderMap[i] = j++;
            }
        }
        
        this.actionReorderMap = actionReorderMap;
    }
    
    /**
     * 
     */
    private void computeActionMap() {
        int[] map = new int[totalDFAStateCount];
        
        for (int i=0;i<totalDFAStateCount;i++) {
            int actionId = getStateActionId(i);
            
            map[i] = actionId != -1 ? actionReorderMap[actionId] : -1;
        }
        
        actionMapSize = map.length;
        actionMapString = appendCompressedArray(map,getActionMapOffset(),
                new StringBuilder()).toString();
    }
    
    /**
     * 
     */
    private void computeActionProperties() {
        int actionCount = actionPool.size();
        
        for (int i=0;i<actionCount;i++) {
            switch(actionPool.get(i).getLookaheadType()) {
            case Action.LOOKAHEAD_NONE:
                hasNormalActions = true;
                break;
            
            case Action.LOOKAHEAD_FIX_CONDITION:
                hasFixConditionLookaheads = true;
                break;
            
            case Action.LOOKAHEAD_FIX_CONTENT:
                hasFixContentLookaheads = true;
                break;
            
            case Action.LOOKAHEAD_VARIABLE:
                hasVariableLookaheads = true;
                break;
            }
        }
    }
    
    //==================
    // Alphabet Methods
    //==================
    
    /**
     * 
     */
    public int getAlphabetLength() {
        return alphabetLength;
    }
    
    /**
     * 
     */
    public String getCharacterMapString() {
        return characterMapString;
    }
    
    /**
     * 
     */
    public int getCharacterMapSize() {
        return characterMapSize;
    }
    
    /**
     * 
     */
    public int getCharacterMapLastValue() {
        return characterMapLastValue;
    }
    
    /**
     * 
     */
    public String getCharacterMapType() {
        return isSmallCharacterMap() ? "byte" : "char";
    }
    
    /**
     * 
     */
    public int getCharacterMapElementSize() {
        return isSmallCharacterMap() ? SystemToolkit.SIZE_OF_BYTE :
            SystemToolkit.SIZE_OF_SHORT;
    }
    
    /**
     * 
     */
    public int getCharacterMapOffset() {
        return 0;
    }
    
    /**
     * 
     */
    public boolean isSmallCharacterMap() {
        return alphabetLength <= 128;
    }
    
    //===============
    // State Methods
    //===============
    
    /**
     * 
     */
    public NameMap getNameMap() {
        return nameMap;
    }
    
    /**
     * 
     */
    public int getTotalNFAStateCount() {
        return totalNFAStateCount;
    }
    
    /**
     * 
     */
    public int getTotalDFAStateCount() {
        return totalDFAStateCount;
    }
    
    /**
     * 
     */
    private int getStateActionId(int stateIndex) {
        return actionIdList[stateIndex];
    }
    
    /**
     * 
     */
    public int getStartState(int dfaIndex) {
        return startStateList[dfaIndex];
    }
    
    /**
     * 
     */
    public int getStartState(String name) {
        return getStartState(nameMap.get(name));
    }
    
    /**
     * 
     */
    public String getTransitionTableString() {
        return transitionTableString;
    }
    
    /**
     * 
     */
    public String getTransitionTableType() {
        return isSmallTransitionTable() ? "byte" : "short";
    }
    
    /**
     * 
     */
    public int getTransitionTableOffset() {
        return 1;
    }
    
    /**
     * 
     */
    public boolean isSmallTransitionTable() {
        return totalDFAStateCount <= 128;
    }
    
    //================
    // Action Methods
    //================
    
    /**
     * 
     */
    public ActionPool<MethodInfo> getActionPool() {
        return actionPool;
    }
    
    /**
     * 
     */
    public int[] getActionReorderMap() {
        return actionReorderMap;
    }
    
    /**
     * 
     */
    public String getActionMapString() {
        return actionMapString;
    }
    
    /**
     * 
     */
    public int getActionMapSize() {
        return actionMapSize;
    }
    
    /**
     * 
     */
    public String getActionMapType() {
        return isSmallActionMap() ? "byte" : "short";
    }
    
    /**
     * 
     */
    public int getActionMapOffset() {
        return 1;
    }
    
    /**
     * 
     */
    public boolean isSmallActionMap() {
        return actionPool.size() <= 128;
    }
    
    /**
     * 
     */
    public boolean hasNormalActions() {
        return hasNormalActions;
    }
    
    /**
     * 
     */
    public boolean hasFixConditionLookaheads() {
        return hasFixConditionLookaheads;
    }
    
    /**
     * 
     */
    public boolean hasFixContentLookaheads() {
        return hasFixContentLookaheads;
    }
    
    /**
     * 
     */
    public boolean hasVariableLookaheads() {
        return hasVariableLookaheads;
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * 
     */
    private char[] compress(char[] array, int offset) {
        StringBuilder builder = new StringBuilder();
        
        char refValue = array[0];
        int refCounter = 1;
        
        for (int i=1;i<array.length;i++) {
            char curValue = array[i];
            
            if ((curValue != refValue) || (refCounter == 0xffff)) {
                builder.append((char)(refValue+offset));
                builder.append((char)refCounter);
                
                refValue = curValue;
                refCounter = 1;
            }
            
            else {
                refCounter++;
            }
        }
        
        builder.append((char)(refValue+offset));
        builder.append((char)refCounter);
        
        int length = builder.length();
        char[] result = new char[length];
        builder.getChars(0,length,result,0);
        
        return result;
    }
    
    /**
     * 
     */
    private StringBuilder appendCompressedArray(int[] array, int offset,
            StringBuilder builder) {
        
        int refValue = array[0];
        int refCounter = 1;
        
        for (int i=1;i<array.length;i++) {
            int curValue = array[i];
            
            if ((curValue != refValue) || (refCounter == 0xffff)) {
                builder.append((char)(refValue+offset));
                builder.append((char)refCounter);
                
                refValue = curValue;
                refCounter = 1;
            }
            
            else {
                refCounter++;
            }
        }
        
        builder.append((char)(refValue+offset));
        builder.append((char)refCounter);
        
        return builder;
    }
}
