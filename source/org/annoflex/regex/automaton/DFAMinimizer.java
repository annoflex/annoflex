/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.automaton;

import java.util.Arrays;

import org.annoflex.util.integer.IdSet;

/**
 * This class computes the minimal DFA of a specified DFA by using Hopcroft's
 * DFA minimization algorithm.
 * 
 * @author Stefan Czaska
 */
final class DFAMinimizer<A> {
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public DFA<A> minimize(DFA<A> dfa) {
        
        // get alphabet length
        Alphabet alphabet = dfa.getAlphabet();
        int alphabetLength = alphabet.getLength();
        
        // increase state count by one for a virtual idle state which exists
        // only during the minimization
        int idleStateId = dfa.getStateCount();
        int stateCount = dfa.getStateCount() + 1;
        
        // create the inversion of all transitions
        IdSet[][] invertedTransitions = createInvertedTransitions(dfa,alphabet,stateCount);
        
        // create state list
        State<A>[] stateList = createStateList(dfa,stateCount,idleStateId);
        
        // create block list
        Block<A>[] blockList = new Block[stateCount];
        
        // fill block list
        int blockListSize = fillBlockList(blockList,stateList,idleStateId);
        
        // get idle block
        Block<A> idleBlock = blockList[0];
        
        // create pair list and inside pair list
        int[] pairList = new int[stateCount*alphabetLength];
        boolean[] insidePairList = new boolean[stateCount*alphabetLength];
        
        // fill pair list
        int pairListSize = fillPairList(alphabet,blockList,blockListSize,
                pairList,insidePairList);
        
        // create source states list
        int[] sourceStatesList = new int[stateCount];
        
        // create split block list
        Block<A>[] splitBlockList = new Block[stateCount];
        
        // process pair list
        while (pairListSize != 0) {
            
            // pick one pair from list
            int pair = pairList[--pairListSize];
            Block<A> pairBlock = blockList[pair/alphabetLength];
            int pairSymbol = pair % alphabetLength;
            insidePairList[pair] = false;
            
            // determine all states which point for the current symbol to the
            // states of the current block
            int sourceStateListCount = 0;
            State<A> iterator = pairBlock.firstState;
            
            while (iterator != null) {
                IdSet sourceStates = invertedTransitions[iterator.dfaState != null ?
                        iterator.dfaState.id : idleStateId][pairSymbol];
                
                if (sourceStates != null) {
                    int destIterator = sourceStates.first();
                    
                    while (destIterator != -1) {
                        sourceStatesList[sourceStateListCount++] = destIterator;
                        
                        destIterator = sourceStates.next(destIterator);
                    }
                }
                
                iterator = iterator.nextState;
            }
            
            // reset block state and twin field
            for (int i=0;i<sourceStateListCount;i++) {
                Block<A> curBlock = stateList[sourceStatesList[i]].block;
                
                curBlock.blockState = Block.CHECK_FOR_SPLIT;
                curBlock.twinBlock = null;
            }
            
            // determine states to move
            for (int i=0;i<sourceStateListCount;i++) {
                State<A> curState = stateList[sourceStatesList[i]];
                Block<A> curBlock = curState.block;
                
                // handle each block only one time even if it contains more than
                // one source state 
                if (curBlock.blockState == Block.CHECK_FOR_SPLIT) {
                    
                    // iterate all states of the current block
                    State<A> stateIterator = curBlock.firstState;
                    int newBlockState = Block.DO_NOT_SPLIT;
                    
                    while (stateIterator != null) {
                        
                        // determine destination block of current state
                        Block<A> destBlock;
                        
                        if (stateIterator.dfaState == null) {
                            destBlock = idleBlock;
                        }
                        
                        else {
                            DFAState<A> destState = stateIterator.dfaState
                                    .conditionalTransitions[pairSymbol];
                            
                            if (destState == null) {
                                destBlock = idleBlock;
                            }
                            
                            else {
                                destBlock = stateList[destState.id].block;
                            }
                        }
                        
                        // if the destination block is not the block of the
                        // current pair then the search can be skipped as it is
                        // clear that multiple blocks are affected
                        if (destBlock != pairBlock) {
                            newBlockState = Block.SPLIT;
                            break;
                        }
                        
                        // go to next state
                        stateIterator = stateIterator.nextState;
                    }
                    
                    // mark block as processed and save whether states must be
                    // moved or not
                    curBlock.blockState = newBlockState;
                }
            }
            
            // reset split block list
            int splitBlockListSize = 0;
            
            // split each block
            for (int i=0;i<sourceStateListCount;i++) {
                State<A> curState = stateList[sourceStatesList[i]];
                Block<A> curBlock = curState.block;
                
                if (curBlock.blockState == Block.SPLIT) {
                    if (curBlock.twinBlock == null) {
                        Block<A> twin = new Block<>(blockListSize);
                        
                        curBlock.twinBlock = twin;
                        splitBlockList[splitBlockListSize++] = curBlock;
                        blockList[blockListSize++] = twin;
                    }
                    
                    curBlock.moveToTwin(curState);
                }
            }
            
            // fix list according to splits that occurred
            for (int i=0;i<splitBlockListSize;i++) {
                Block<A> curSplitBlock = splitBlockList[i];
                Block<A> curSplitBlockTwin = curSplitBlock.twinBlock;
                int curIndex = curSplitBlock.id * alphabetLength;
                
                for (int j=0;j<alphabetLength;j++) {
                    Block<A> insertionBlock = ((insidePairList[curIndex+j]) ||
                            (curSplitBlock.stateCount > curSplitBlockTwin.stateCount)) ?
                                    curSplitBlockTwin : curSplitBlock;
                    
                    int index = insertionBlock.id * alphabetLength + j;
                    
                    pairList[pairListSize++] = index;
                    insidePairList[index] = true;
                }
            }
        }
        
        // create minimum DFA
        DFA<A> minimizedDFA = new DFA<>(alphabet);
        
        // create and initialize states
        for (int i=1;i<blockListSize;i++) {
            minimizedDFA.createState().setAction(blockList[i].firstState.dfaState
                    .getAction());
        }
        
        // register start state
        minimizedDFA.setStartState(minimizedDFA.getState(
                stateList[dfa.getStartState().id].block.id-1));
        
        // adopt transitions
        for (int i=1;i<blockListSize;i++) {
            Block<A> curBlock = blockList[i];
            DFAState<A> curState = curBlock.firstState.dfaState;
            
            for (int j=0;j<alphabetLength;j++) {
                DFAState<A> destState = curState.conditionalTransitions[j];
                
                if (destState != null) {
                    int curBlockMinId = curBlock.id - 1;
                    int destBlockMinId = stateList[destState.id].block.id - 1;
                    
                    minimizedDFA.getState(curBlockMinId).putConditionalTransition(j,
                            minimizedDFA.getState(destBlockMinId));
                }
            }
        }
        
        return minimizedDFA;
    }
    
    /**
     * 
     */
    private IdSet[][] createInvertedTransitions(DFA<A> dfa, Alphabet alphabet,
            int stateCount) {
        
        int alphabetLength = alphabet.getLength();
        int idleStateId = stateCount - 1;
        
        IdSet[][] inversion = new IdSet[stateCount][alphabetLength];
        
        // Create the incoming transitions for the idle state. For each
        // character leads the idle state to the idle state. Additional
        // transitions are added later by the individual states.
        for (int i=0;i<alphabetLength;i++) {
            IdSet inversionStateSet = new IdSet();
            inversionStateSet.add(idleStateId);
            inversion[idleStateId][i] = inversionStateSet;
        }
        
        // create the incoming transitions for all other states
        for (int i=0;i<idleStateId;i++) {
            DFAState<A> curState = dfa.getState(i);
            
            for (int j=0;j<alphabetLength;j++) {
                
                // Determine the destination state of the current state. This is
                // either the real destination state or the idle state if there
                // is no real destination state.
                DFAState<A> curDestState = curState.conditionalTransitions[j];
                int destStateId = curDestState != null ?
                        curDestState.id : idleStateId;
                
                // create the incoming transition for the destination state
                IdSet inversionStateSet = inversion[destStateId][j];
                
                if (inversionStateSet == null) {
                    inversionStateSet = new IdSet();
                    inversion[destStateId][j] = inversionStateSet;
                }
                
                inversionStateSet.add(curState.id);
            }
        }
        
        return inversion;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private State<A>[] createStateList(DFA<A> dfa, int stateCount,
            int idleStateId) {
        
        State<A>[] stateList = new State[stateCount];
        
        // create states for all DFA states
        for (int i=0;i<idleStateId;i++) {
            stateList[i] = new State<>(dfa.getState(i));
        }
        
        // create state for virtual idle state
        stateList[idleStateId] = new State<>(null);
        
        return stateList;
    }
    
    /**
     * 
     */
    private int fillBlockList(Block<A>[] blockList, State<A>[] stateList,
            int idleStateId) {
        
        int blockListSize = 0;
        
        // Let the idle block be the first one in order not to have a gap
        // between the ids of the blocks of the states of the DFA. This
        // simplifies the creation of the states of the minimized DFA at the
        // end of the minimization.
        Block<A> idleBlock = new Block<>(0);
        idleBlock.addState(stateList[idleStateId]);
        blockList[blockListSize++] = idleBlock;
        
        // add all DFA states to blocks
        State<A>[] sortedStateList = stateList.clone();
        Arrays.sort(sortedStateList,0,idleStateId);
        
        Block<A> refBlock = new Block<>(blockListSize);
        refBlock.addState(sortedStateList[0]);
        blockList[blockListSize++] = refBlock;
        Action<A> refStateAction = refBlock.firstState.dfaState.getAction();
        
        for (int i=1;i<idleStateId;i++) {
            State<A> curState = sortedStateList[i];
            Action<A> curStateAction = curState.dfaState.getAction();
            
            if (curStateAction != refStateAction) {
                Block<A> newBlock = new Block<>(blockListSize);
                blockList[blockListSize++] = newBlock;
                
                refBlock = newBlock;
                refStateAction = curStateAction;
            }
            
            refBlock.addState(curState);
        }
        
        return blockListSize;
    }
    
    /**
     * 
     */
    private int fillPairList(Alphabet alphabet, Block<A>[] blockList,
            int blockListSize, int[] pairList, boolean[] insidePairList) {
        
        int pairListSize = 0;
        int alphabetLength = alphabet.getLength();
        
        Block<A> largestBlock = getLargestBlock(blockList,blockListSize);
        
        for (int i=0;i<blockListSize;i++) {
            Block<A> curBlock = blockList[i];
            
            if (curBlock != largestBlock) {
                for (int j=0;j<alphabetLength;j++) {
                    int index = curBlock.id * alphabetLength + j;
                    
                    pairList[pairListSize++] = index;
                    insidePairList[index] = true;
                }
            }
        }
        
        return pairListSize;
    }
    
    /**
     * 
     */
    private Block<A> getLargestBlock(Block<A>[] blockList, int blockListSize) {
        Block<A> refBlock = blockList[0];
        
        for (int i=1;i<blockListSize;i++) {
            Block<A> curBlock = blockList[i];
            
            if (curBlock.stateCount > refBlock.stateCount) {
                refBlock = curBlock;
            }
        }
        
        return refBlock;
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class State<A> implements Comparable<State<A>> {
        
        /**
         * 
         */
        public final DFAState<A> dfaState;
        
        /**
         * 
         */
        public Block<A> block;
        
        /**
         * 
         */
        public State<A> nextState;
        
        /**
         * 
         */
        public State<A> previousState;
        
        /**
         * 
         */
        public State(DFAState<A> state) {
            this.dfaState = state;
        }
        
        /**
         * 
         */
        public int compareTo(State<A> state) {
            Action<A> thisAction = dfaState.getAction();
            Action<A> otherAction = state.dfaState.getAction();
            
            if (thisAction == null) {
                return otherAction == null ? 0 : -1;
            }
            
            if (otherAction == null) {
                return 1;
            }
            
            return thisAction.getId() - otherAction.getId();
        }
    }
    
    /**
     * 
     */
    static final class Block<A> {
        
        /**
         * 
         */
        public static final int CHECK_FOR_SPLIT = 0;
        
        /**
         * 
         */
        public static final int DO_NOT_SPLIT = 1;
        
        /**
         * 
         */
        public static final int SPLIT = 2;
        
        /**
         * 
         */
        public final int id;
        
        /**
         * 
         */
        public State<A> firstState;
        
        /**
         * 
         */
        public State<A> lastState;
        
        /**
         * 
         */
        public int stateCount;
        
        /**
         * 
         */
        public int blockState;
        
        /**
         * 
         */
        public Block<A> twinBlock;
        
        /**
         * 
         */
        public Block(int id) {
            this.id = id;
        }
        
        /**
         * 
         */
        public void addState(State<A> state) {
            state.nextState = null;
            state.previousState = lastState;
            
            if (firstState == null) {
                firstState = state;
            }
            
            else {
                lastState.nextState = state;
            }
            
            lastState = state;
            
            stateCount++;
            state.block = this;
        }
        
        /**
         * 
         */
        public void moveToTwin(State<A> state) {
            
            // remove from this block
            if (state == firstState) {
                state.nextState.previousState = null;
                firstState = state.nextState;
            }
            
            else if (state == lastState) {
                state.previousState.nextState = null;
                lastState = state.previousState;
            }
            
            else {
                state.previousState.nextState = state.nextState;
                state.nextState.previousState = state.previousState;
            }
            
            stateCount--;
            
            // add to twin block
            twinBlock.addState(state);
        }
    }
}
