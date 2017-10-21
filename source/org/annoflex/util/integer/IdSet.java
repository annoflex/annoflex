/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.integer;

import org.annoflex.util.SystemToolkit;

/**
 * <p>This class is a container for arbitrary sets of non-negative integers.
 * The memory consumption depends only on the amount of actual values in the set
 * and not on the numerical value of the individual integers. A set with the
 * single values "1" and "100" consumes the same memory as a set with the values
 * "1" and "100,000,000".</p>
 * <p>The implementation uses a red-black tree for the storage of the values.
 * Each node of the tree has a 32-bit index and 64-bit bit-set variable. Both
 * together are used to store the integers as single bits.</p>
 * 
 * @author Stefan Czaska
 */
public class IdSet implements IntCollection, Comparable<IdSet> {
    
    //========
    // Fields
    //========
    
    /**
     * <p>Specifies the number of ids in the set. The value has different
     * meanings:</p>
     * <ul>
     * <li>A value of zero indicates that the set is empty.</li>
     * <li>A negative value specifies that the set has exactly one id and the
     * value of this id is stored in the size variable as a negative value.</li>
     * <li>A positive value specifies the number of ids in the set but in
     * contrast to a negative value it also specifies that the ids are stored in
     * nodes of a red-black tree.</li>
     * </ul>
     */
    private int size;
    
    /**
     * The root node of the red-black tree.
     */
    private Node root;
    
    //==============
    // Cache Fields
    //==============
    
    /**
     * Points to the node last used by an iteration method.
     */
    private Node lastUsedNode;
    
    /**
     * The hash value of the tree or zero if it is not computed. 
     */
    private int hash;
    
    //==============
    // Constructors
    //==============
    
    /**
     * Constructs an empty {@link IdSet}.
     */
    public IdSet() {
    }
    
    /**
     * Creates a clone of an {@link IdSet}.
     * 
     * @param set An {@link IdSet} which should be cloned.
     */
    public IdSet(IdSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set may not be null");
        }
        
        root = set.root != null ? set.cloneNode(set.root,null) : null;
        size = set.size;
        hash = set.hash;
    }
    
    //==============
    // Size Methods
    //==============
    
    /**
     * Returns the number of ids in the set.
     * 
     * @return The number of ids in the set.
     */
    public int size() {
        return size < 0 ? 1 : size;
    }
    
    /**
     * Returns whether the set is empty.
     * 
     * @return True if the set is empty, otherwise false.
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Returns whether the set is not empty.
     * 
     * @return True if the set is not empty, otherwise false.
     */
    public boolean hasContent() {
        return size != 0;
    }
    
    //=============
    // Add Methods
    //=============
    
    /**
     * Adds the ids of an {@link IdSet} to this {@link IdSet}.
     * 
     * @param set An {@link IdSet} whose ids should be added to this {@link IdSet}.
     * @return True if this set has been modified due to the addition, false
     * otherwise.
     */
    public boolean add(IdSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set may not be null");
        }
        
        if (set != this) {
            boolean modified = false;
            int iterator = set.first();
            
            while (iterator != -1) {
                modified |= add(iterator);
                
                iterator = set.next(iterator);
            }
            
            return modified;
        }
        
        return false;
    }
    
    /**
     * Adds an id to this {@link IdSet}.
     * 
     * @param id An id which should be added.
     * @return True if this set has been modified due to the addition, false
     * otherwise.
     * @throws IllegalArgumentException if the specified id is negative.
     */
    public boolean add(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id may not be negative");
        }
        
        if (size == 0) {
            if (root != null) {
                root.index = id >> 6;
                root.bitSet = 1L << id;
                size = 1;
            }
            
            else {
                size = -id - 1;
            }
            
            hash = 0;
            
            return true;
        }
        
        if (size < 0) {
            int curId = -(size + 1);
            
            if (id == curId) {
                return false;
            }
            
            root = createNode();
            root.index = id >> 6;
            root.bitSet = 1L << id;
            
            addToTree(curId);
            
            size = 2;
            hash = 0;
            
            return true;
        }
        
        if (addToTree(id)) {
            size++;
            hash = 0;
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Adds the specified id to the tree. This method assumes that a root node
     * already exists and that the id can thus be either added as a new node or
     * to an existing one.
     * 
     * @param id An id to be added.
     * @return True if the addition has modified the set, false otherwise.
     */
    private boolean addToTree(int id) {
        Node iterator = root;
        int index = id >> 6;
        
        do {
            
            // Check whether the current node is a flip candidate. If it is then
            // flip it.
            if (!iterator.isRed) {
                Node leftChild = iterator.leftChild;
                Node rightChild = iterator.rightChild;
                
                if ((leftChild != null) && (rightChild != null) &&
                    leftChild.isRed && rightChild.isRed) {
                    
                    // perform the flip
                    leftChild.isRed = false;
                    rightChild.isRed = false;
                    
                    if (iterator != root) {
                        iterator.isRed = true;
                        
                        if (iterator.parent.isRed) {
                            redRedCleanup(iterator);
                        }
                    }
                }
            }
            
            // find right node
            if (index == iterator.index) {
                long bit = 1L << id;
                
                if ((iterator.bitSet & bit) == 0) {
                    iterator.bitSet |= bit;
                    
                    return true;
                }
                
                return false;
            }
            
            if (index < iterator.index) {
                Node leftChild = iterator.leftChild;
                
                if (leftChild == null) {
                    Node newNode = createNode();
                    newNode.isRed = true;
                    newNode.index = index;
                    newNode.bitSet = 1L << id;
                    
                    iterator.leftChild = newNode;
                    newNode.parent = iterator;
                    
                    if (iterator.isRed) {
                        redRedCleanup(newNode);
                    }
                    
                    return true;
                }
                
                iterator = leftChild;
            }
            
            else {
                Node rightChild = iterator.rightChild;
                
                if (rightChild == null) {
                    Node newNode = createNode();
                    newNode.isRed = true;
                    newNode.index = index;
                    newNode.bitSet = 1L << id;
                    
                    iterator.rightChild = newNode;
                    newNode.parent = iterator;
                    
                    if (iterator.isRed) {
                        redRedCleanup(newNode);
                    }
                    
                    return true;
                }
                
                iterator = rightChild;
            }
        } while (true);
    }
    
    /**
     * Cleans up an insertion denormalization which consists of a red node with a
     * red parent. Such a situation can easily be cleaned by performing one or
     * two rotations depending on the tree structure.
     * 
     * @param node A red node which has a red parent.
     */
    private void redRedCleanup(Node node) {
        Node parent = node.parent;
        Node grandParent = parent.parent;
        grandParent.isRed = true;
        
        // parent is right child
        if (grandParent.rightChild == parent) {
            
            // right outer grandchild
            if (parent.rightChild == node) {
                parent.isRed = false;
            }
            
            // right inner grandchild
            else {
                node.isRed = false;
                ror(parent);
            }
            
            rol(grandParent);
        }
        
        // parent is left child
        else {
            
            // left inner grandchild
            if (parent.rightChild == node) {
                node.isRed = false;
                rol(parent);
            }
            
            // left outer grandchild
            else {
                parent.isRed = false;
            }
            
            ror(grandParent);
        }
    }
    
    //================
    // Remove Methods
    //================
    
    /**
     * Removes the ids of the specified {@link IdSet} from this {@link IdSet}.
     * 
     * @param set An {@link IdSet} whose ids should be removed from this
     * {@link IdSet}.
     * @return True if the removal has modified the {@link IdSet}, otherwise
     * false.
     */
    public boolean remove(IdSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set may not be null");
        }
        
        if (set == this) {
            return clear();
        }
        
        boolean modified = false;
        int iterator = set.first();
        
        while ((iterator != -1) && (size != 0)) {
            modified |= remove(iterator);
            
            iterator = set.next(iterator);
        }
        
        return modified;
    }
    
    /**
     * Removes an id from the set.
     * 
     * @param id An id to be removed from the set.
     * @return True if the removal has modified the set, otherwise false.
     */
    public boolean remove(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id may not be negative");
        }
        
        if (size != 0) {
            if (size < 0) {
                if (id == -(size+1)) {
                    size = 0;
                    hash = 0;
                    
                    return true;
                }
                
                return false;
            }
            
            // remove id from tree if it is part of the set
            Node iterator = root;
            int index = id >> 6;
            
            do {
                if (index == iterator.index) {
                    long bit = 1L << id;
                    
                    if ((iterator.bitSet & bit) != 0) {
                        iterator.bitSet &= ~bit;
                        size--;
                        hash = 0;
                        
                        // if the current node is empty then remove it from the tree
                        if (iterator.bitSet == 0) {
                            if ((iterator.leftChild != null) && (iterator.rightChild != null)) {
                                Node nextInOrderNode = findNextInOrderNode(iterator);
                                
                                iterator.index = nextInOrderNode.index;
                                iterator.bitSet = nextInOrderNode.bitSet;
                                iterator = nextInOrderNode;
                            }
                            
                            if (iterator.leftChild != null) {
                                removeNode(iterator,iterator.leftChild);
                            }
                            
                            else if (iterator.rightChild != null) {
                                removeNode(iterator,iterator.rightChild);
                            }
                            
                            else if (iterator.isRed) {
                                removeNode(iterator,null);
                            }
                            
                            else {
                                increaseBlackDepth(iterator);
                                removeNode(iterator,null);
                            }
                        }
                        
                        return true;
                    }
                    
                    return false;
                }
                
                iterator = index < iterator.index ? iterator.leftChild :
                    iterator.rightChild;
            } while (iterator != null);
        }
        
        return false;
    }
    
    /**
     * Removes a node with one or no child from the tree. This method assumes
     * that after the removal the tree will have balanced black depth.
     * 
     * @param node A node to be removed.
     * @param child The child of the node or null if it has no child.
     */
    private void removeNode(Node node, Node child) {
        Node parent = node.parent;
        
        // root node
        if (node == root) {
            if (child != null) {
                root = child;
                
                // clear parent in order to get cacheNode working
                child.parent = null;
                
                cacheNode(node);
            }
            
            else {
                
                // clear parent in order to have only one cached
                // node and not two nodes
                node.parent = null;
            }
        }
        
        // node below root node
        else {
            if (parent.leftChild == node) {
                parent.leftChild = child;
            }
            
            else {
                parent.rightChild = child;
            }
            
            cacheNode(node);
        }
        
        if (child != null) {
            
            // If node was the root node then the child node will be the new
            // root node and the node itself will be the cached node. Thus a
            // parent adjustment is only necessary if the node was not the root
            // node.
            if (child != root) {
                child.parent = parent;
            }
            
            // in all situations the child must be black after the removal
            child.isRed = false;
        }
        
        // clear iteration cache if necessary
        if (node == lastUsedNode) {
            lastUsedNode = null;
        }
    }
    
    /**
     * Increases the black depth of a black node without children. This is
     * always possible but the necessary operations depend on the tree
     * structure. This method must first be called before a black node without
     * children can be removed from the tree. As it first increases the black
     * depth of the node the node can easily be removed afterwards without
     * changing the black depth of the tree.
     * 
     * @param node A black node without children whose black depth should be
     * increased.
     */
    private void increaseBlackDepth(Node node) {
        Node parent = node.parent;
        
        // root node
        if (parent == null) {
            return;
        }
        
        // red parent
        if (parent.isRed) {
            increaseBlackDepthRedParent(node);
        }
        
        // black parent
        else {
            
            // node is left child
            if (parent.leftChild == node) {
                Node sibling = parent.rightChild;
                
                // case 2
                if (sibling.isRed) {
                    parent.isRed = true;
                    sibling.isRed = false;
                    rol(parent);
                    
                    increaseBlackDepthRedParent(node);
                }
                
                // at least one child of right sibling is red
                else if (((sibling.leftChild != null) && sibling.leftChild.isRed) ||
                         ((sibling.rightChild != null) && sibling.rightChild.isRed)) {
                    
                    increaseBlackDepthRightSiblingRedChildren(sibling,parent);
                }
                
                // Node, sibling and parent are black. Decrease black depth of
                // sibling and delegate to parent node.
                else {
                    sibling.isRed = true;
                    increaseBlackDepth(parent);
                }
            }
            
            // node is right child
            else {
                Node sibling = parent.leftChild;
                
                // case 2
                if (sibling.isRed) {
                    parent.isRed = true;
                    sibling.isRed = false;
                    ror(parent);
                    
                    increaseBlackDepthRedParent(node);
                }
                
                // at least one child of left sibling is red
                else if (((sibling.leftChild != null) && sibling.leftChild.isRed) ||
                         ((sibling.rightChild != null) && sibling.rightChild.isRed)) {
                    
                    increaseBlackDepthLeftSiblingRedChildren(sibling,parent);
                }
                
                // Node, sibling and parent are black. Decrease black depth of
                // sibling and delegate to parent node.
                else {
                    sibling.isRed = true;
                    increaseBlackDepth(parent);
                }
            }
        }
    }
    
    /**
     * Increases the black depth of a black node without children and a red
     * parent.
     * 
     * @param node A node whose black depth should be increased.
     */
    private void increaseBlackDepthRedParent(Node node) {
        Node parent = node.parent;
        
        // node is left child
        if (parent.leftChild == node) {
            Node sibling = parent.rightChild;
            
            // at least one child of right sibling is red
            if (((sibling.leftChild != null) && sibling.leftChild.isRed) ||
                ((sibling.rightChild != null) && sibling.rightChild.isRed)) {
                
                increaseBlackDepthRightSiblingRedChildren(sibling,parent);
            }
            
            // switch colors of parent and sibling
            else {
                parent.isRed = false;
                sibling.isRed = true;
            }
        }
        
        // node is right child
        else {
            Node sibling = parent.leftChild;
            
            // at least one child of left sibling is red
            if (((sibling.leftChild != null) && sibling.leftChild.isRed) ||
                ((sibling.rightChild != null) && sibling.rightChild.isRed)) {
                
                increaseBlackDepthLeftSiblingRedChildren(sibling,parent);
            }
            
            // switch colors of parent and sibling
            else {
                parent.isRed = false;
                sibling.isRed = true;
            }
        }
    }
    
    /**
     * Increases the black depth of a black node without children and a right
     * sibling with at least one red child.
     * 
     * @param sibling The sibling of the node whose black depth should be
     * increased.
     * @param parent The parent of the node.
     */
    private void increaseBlackDepthRightSiblingRedChildren(Node sibling,
            Node parent) {
        
        // normalize
        if ((sibling.rightChild == null) || !sibling.rightChild.isRed) {
            ror(sibling);
            sibling.isRed = true;
            sibling.parent.isRed = false;
            sibling = sibling.parent;
        }
        
        // increase black depth
        rol(parent);
        
        boolean parentIsRed = parent.isRed;
        parent.isRed = parent.parent.isRed;
        parent.parent.isRed = parentIsRed;
        
        sibling.rightChild.isRed = false;
    }
    
    /**
     * Increases the black depth of a black node without children and a left
     * sibling with at least one red child.
     * 
     * @param sibling The sibling of the node whose black depth should be
     * increased.
     * @param parent The parent of the node.
     */
    private void increaseBlackDepthLeftSiblingRedChildren(Node sibling,
            Node parent) {
        
        // normalize
        if ((sibling.leftChild == null) || !sibling.leftChild.isRed) {
            rol(sibling);
            sibling.isRed = true;
            sibling.parent.isRed = false;
            sibling = sibling.parent;
        }
        
        // increase black depth
        ror(parent);
        
        boolean parentIsRed = parent.isRed;
        parent.isRed = parent.parent.isRed;
        parent.parent.isRed = parentIsRed;
        
        sibling.leftChild.isRed = false;
    }
    
    //===============
    // Clear Methods
    //===============
    
    /**
     * Removes all ids from the set.
     * 
     * @return True if the set was not empty and the method actually removed ids
     * from the set, false if the set was already empty and not changed.
     */
    public boolean clear() {
        if (size != 0) {
            if (size > 0) {
                root.parent = null;
                root.leftChild = null;
                root.rightChild = null;
                
                lastUsedNode = null;
            }
            
            size = 0;
            hash = 0;
            
            return true;
        }
        
        return false;
    }
    
    //==============
    // Node Methods
    //==============
    
    /**
     * Creates a new node. If the node cache is not empty then the returned node
     * is taken from the cache, otherwise a new instance is created. 
     * 
     * @return A new node which is either taken from the node cache or a new
     * instance. 
     */
    private Node createNode() {
        if (root != null) {
            Node node = root.parent;
            
            if (node != null) {
                node.leftChild = null;
                root.parent = null;
                
                return node;
            }
        }
        
        return new Node();
    }
    
    /**
     * Adds a node to the node cache.
     * 
     * @param node A node to be added to the node cache.
     */
    private void cacheNode(Node node) {
        if (root.parent == null) {
            root.parent = node;
            
            node.parent = null;
            node.leftChild = root;
            node.rightChild = null;
        }
    }
    
    //==================
    // Contains Methods
    //==================
    
    /**
     * Checks whether this set contains all ids of another {@link IdSet}.
     * 
     * @param set A set whose ids are checked.
     * @return True if all ids of the specified set are contained in this set,
     * otherwise false.
     */
    public boolean contains(IdSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set may not be null");
        }
        
        if (!set.isEmpty()) {
            if (set == this) {
                return true;
            }
            
            int iterator = set.first();
            
            while (iterator != -1) {
                if (!contains(iterator)) {
                    return false;
                }
                
                iterator = set.next(iterator);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Returns whether a specified id is contained in this set.
     * 
     * @param id An id to be checked.
     * @return True if the specified id is contained in this set, otherwise
     * false.
     */
    public boolean contains(int id) {
        
        // Note: First check for size greater than one as this should be the
        // common case.
        if (size > 0) {
            Node iterator = root;
            int index = id >> 6;
            
            do {
                if (index == iterator.index) {
                    return (iterator.bitSet & (1L << id)) != 0;
                }
                
                iterator = index < iterator.index ? iterator.leftChild :
                    iterator.rightChild;
            } while (iterator != null);
            
            return false;
        }
        
        return (size < 0) && (id == -(size + 1));
    }
    
    //======================
    // Intersection Methods
    //======================
    
    /**
     * Returns whether the specified {@link IdSet} intersects with this set.
     * 
     * @param set An {@link IdSet} to be checked for intersection with this set.
     * @return True if the specified set intersects with this set, otherwise
     * false.
     */
    public boolean intersects(IdSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set may not be null");
        }
        
        int size1 = size();
        int size2 = set.size();
        
        if (size1 <= size2) {
            int iterator = first();
            
            while (iterator != -1) {
                if (set.contains(iterator)) {
                    return true;
                }
                
                iterator = next(iterator);
            }
        }
        
        else {
            int iterator = set.first();
            
            while (iterator != -1) {
                if (contains(iterator)) {
                    return true;
                }
                
                iterator = set.next(iterator);
            }
        }
        
        return false;
    }
    
    /**
     * Creates the intersection between this set and another set.
     * 
     * @param set An {@link IdSet} for which the intersection with this set
     * should be created.
     * @return True if this set has been modified due to the creation of the
     * intersection, otherwise false.
     */
    public boolean intersection(IdSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set may not be null");
        }
        
        if (set != this) {
            if (set.isEmpty()) {
                return clear();
            }
            
            boolean modified = false;
            int iterator = first();
            
            while (iterator != -1) {
                int next = next(iterator);
                
                if (!set.contains(iterator)) {
                    remove(iterator);
                    
                    modified = true;
                    
                    if (size == 0) {
                        break;
                    }
                }
                
                iterator = next;
            }
            
            return modified;
        }
        
        return false;
    }
    
    //===================
    // Iteration Methods
    //===================
    
    /**
     * Returns the first id of this {@link IdSet}. As the set is sorted this is
     * the id with the lowest value.
     * 
     * @return The first id of this set or -1 if the set is empty and there is
     * no id.
     */
    public int first() {
        if (size > 0) {
            Node iterator = root;
            
            while (iterator.leftChild != null) {
                iterator = iterator.leftChild;
            }
            
            lastUsedNode = iterator;
            
            return (iterator.index << 6) + Long.numberOfTrailingZeros(Long
                    .lowestOneBit(iterator.bitSet));
        }
        
        return -(size+1);
    }
    
    /**
     * Returns the last id of this {@link IdSet}. As the set is sorted this is
     * the id with the highest value.
     * 
     * @return The last id of this set or -1 if the set is empty and there is
     * no id.
     */
    public int last() {
        if (size > 0) {
            Node iterator = root;
            
            while (iterator.rightChild != null) {
                iterator = iterator.rightChild;
            }
            
            lastUsedNode = iterator;
            
            return (iterator.index << 6) + Long.numberOfTrailingZeros(Long
                    .highestOneBit(iterator.bitSet));
        }
        
        return -(size+1);
    }
    
    /**
     * Returns the id with the next higher value as a specified id.
     * 
     * @param id An id for which the next id should be returned.
     * @return The id with the next higher value as the specified id or -1 if
     * there is no next id.
     * @throws IllegalArgumentException if the specified id is not part of the
     * set.
     */
    public int next(int id) {
        Node curNode = getNodeByIndex(id >> 6);
        
        if (curNode == null) {
            return -1;
        }
        
        long curBlock = curNode.bitSet;
        long bit = 1L << id;
        
        if ((curBlock & bit) == 0) {
            throw new IllegalArgumentException("the specified id is not part of the set");
        }
        
        // clear all lower bits and the id bit
        curBlock &= ~(bit | (bit - 1));
        
        // go to the next value if the current id is the last one of its block
        if (curBlock == 0) {
            curNode = findNextInOrderNode(curNode);
            
            if (curNode == null) {
                return -1;
            }
            
            curBlock = curNode.bitSet;
        }
        
        lastUsedNode = curNode;
        
        return (curNode.index << 6) + Long.numberOfTrailingZeros(Long
                .lowestOneBit(curBlock));
    }
    
    /**
     * Returns the id with the next lower value as a specified id.
     * 
     * @param id An id for which the previous id should be returned.
     * @return The id with the next lower value as the specified id or -1 if
     * there is no previous id.
     * @throws IllegalArgumentException if the specified id is not part of the
     * set.
     */
    public int previous(int id) {
        Node curNode = getNodeByIndex(id >> 6);
        
        if (curNode == null) {
            return -1;
        }
        
        long curBlock = curNode.bitSet;
        long bit = 1L << id;
        
        if ((curBlock & bit) == 0) {
            throw new IllegalArgumentException("the specified id is not part of the set");
        }
        
        // clear all higher bits and the id bit
        curBlock &= bit - 1;
        
        // go to the previous value if the current id is the last one of its block
        if (curBlock == 0) {
            curNode = findPreviousInOrderNode(curNode);
            
            if (curNode == null) {
                return -1;
            }
            
            curBlock = curNode.bitSet;
        }
        
        lastUsedNode = curNode;
        
        return (curNode.index << 6) + Long.numberOfTrailingZeros(Long
                .highestOneBit(curBlock));
    }
    
    /**
     * Returns the node with the specified index.
     * 
     * @param index An index for which the node should be returned.
     * @return The node with the specified index.
     * @throws IllegalArgumentException if no node exists for the specified
     * index.
     */
    private Node getNodeByIndex(int index) {
        Node node = lastUsedNode;
        
        if ((node == null) || (node.index != index)) {
            node = root;
            
            if (node == null) {
                return null;
            }
            
            do {
                if (node.index == index) {
                    break;
                }
                
                node = index < node.index ? node.leftChild : node.rightChild;
                
                if (node == null) {
                    throw new IllegalArgumentException("the specified id is not part of the set");
                }
            } while (true);
        }
        
        return node;
    }
    
    //==============
    // Tree Methods
    //==============
    
    /**
     * Performs a right rotation for a specified node.
     * 
     * @param node A node for which the right rotation should be performed.
     */
    private void ror(Node node) {
        Node nodeLeftChild = node.leftChild;
        node.leftChild = nodeLeftChild.rightChild;
        
        if (nodeLeftChild.rightChild != null) {
            nodeLeftChild.rightChild.parent = node;
        }
        
        if (node == root) {
            root = nodeLeftChild;
            
            // transfer cached node to new root
            if (node.parent != null) {
                node.parent.leftChild = nodeLeftChild;
            }
        }
        
        else if (node.parent.rightChild == node) {
            node.parent.rightChild = nodeLeftChild;
        }
        
        else {
            node.parent.leftChild = nodeLeftChild;
        }
        
        nodeLeftChild.parent = node.parent;
        nodeLeftChild.rightChild = node;
        node.parent = nodeLeftChild;
    }
    
    /**
     * Performs a left rotation for a specified node.
     * 
     * @param node A node for which the left rotation should be performed.
     */
    private void rol(Node node) {
        Node nodeRightChild = node.rightChild;
        node.rightChild = nodeRightChild.leftChild;
        
        if (nodeRightChild.leftChild != null) {
            nodeRightChild.leftChild.parent = node;
        }
        
        if (node == root) {
            root = nodeRightChild;
            
            // transfer cached node to new root
            if (node.parent != null) {
                node.parent.leftChild = nodeRightChild;
            }
        }
        
        else if (node.parent.leftChild == node) {
            node.parent.leftChild = nodeRightChild;
        }
        
        else {
            node.parent.rightChild = nodeRightChild;
        }
        
        nodeRightChild.parent = node.parent;
        nodeRightChild.leftChild = node;
        node.parent = nodeRightChild;
    }
    
    /**
     * Returns the next in-order node of a specified node.
     * 
     * @param node A node whose next in-order node should be returned.
     * @return The next in-order anode of the specified node.
     */
    private Node findNextInOrderNode(Node node) {
        Node iterator = node.rightChild;
        
        if (iterator != null) {
            Node leftChild = iterator.leftChild;
            
            while (leftChild != null) {
                iterator = leftChild;
                leftChild = iterator.leftChild;
            }
            
            return iterator;
        }
        
        if ((node.parent == null) || (node == root)) {
            return null;
        }
        
        if (node.parent.rightChild == node) {
            iterator = node.parent;
            
            do {
                Node parent = iterator.parent;
                
                if ((parent == null) || (iterator == root)) {
                    return null;
                }
                
                if (parent.leftChild == iterator) {
                    return parent;
                }
                
                iterator = parent;
            } while(true);
        }
        
        return node.parent;
    }
    
    /**
     * Returns the previous in-order node of a specified node.
     * 
     * @param node A node whose previous in-order node should be returned.
     * @return The previous in-order anode of the specified node.
     */
    private Node findPreviousInOrderNode(Node node) {
        Node iterator = node.leftChild;
        
        if (iterator != null) {
            Node rightChild = iterator.rightChild;
            
            while (rightChild != null) {
                iterator = rightChild;
                rightChild = iterator.rightChild;
            }
            
            return iterator;
        }
        
        if ((node.parent == null) || (node == root)) {
            return null;
        }
        
        if (node.parent.leftChild == node) {
            iterator = node.parent;
            
            do {
                Node parent = iterator.parent;
                
                if ((parent == null) || (iterator == root)) {
                    return null;
                }
                
                if (parent.rightChild == iterator) {
                    return parent;
                }
                
                iterator = parent;
            } while (true);
        }
        
        return node.parent;
    }
    
    //====================
    // Comparable Methods
    //====================
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(IdSet o) {
        int size1 = size();
        int size2 = o.size();
        
        if (size1 != size2) {
            return size1 - size2;
        }
        
        if (size1 <= 1) {
            return first() - o.first();
        }
        
        if (o == this) {
            return 0;
        }
        
        Node iterator1 = root;
        
        while (iterator1.rightChild != null) {
            iterator1 = iterator1.rightChild;
        }
        
        Node iterator2 = o.root;
        
        while (iterator2.rightChild != null) {
            iterator2 = iterator2.rightChild;
        }
        
        do {
            if (iterator1.index != iterator2.index) {
                return iterator1.index - iterator2.index;
            }
            
            if (iterator1.bitSet != iterator2.bitSet) {
                return iterator1.bitSet > iterator2.bitSet ? 1 : -1;
            }
            
            iterator1 = findPreviousInOrderNode(iterator1);
            iterator2 = findPreviousInOrderNode(iterator2);
            
            // check whether both nodes are null
            if (iterator1 == iterator2) {
                return 0;
            }
            
            if (iterator1 == null) {
                return -1;
            }
            
            if (iterator2 == null) {
                return 1;
            }
        } while (true);
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * Returns an array which contains all ids of this set. The ids are sorted
     * in ascending order.
     * 
     * @return An array which contains all ids of this set.
     */
    public int[] toArray() {
        if (hasContent()) {
            int[] array = new int[size()];
            int iterator = first();
            int i = 0;
            
            do {
                array[i++] = iterator;
                iterator = next(iterator);
            } while (iterator != -1);
            
            return array;
        }
        
        return SystemToolkit.EMPTY_INT_ARRAY;
    }
    
    /**
     * Returns a {@link ConstIntArray} which contains all ids of this set. The
     * ids are sorted in ascending order.
     * 
     * @return A {@link ConstIntArray} which contains all ids of this set.
     */
    public ConstIntArray toConstIntArray() {
        if (hasContent()) {
            return new ConstIntArray(this);
        }
        
        return ConstIntArray.EMPTY_ARRAY;
    }
    
    /**
     * Returns a {@link MutableIntArray} which contains all ids of this set. The
     * ids are sorted in ascending order.
     * 
     * @return A {@link MutableIntArray} which contains all ids of this set.
     */
    public MutableIntArray toMutableIntArray() {
        return new MutableIntArray(this);
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
        
        if (obj instanceof IdSet) {
            IdSet set = (IdSet)obj;
            int size1 = size();
            int size2 = set.size();
            
            if (size1 == size2) {
                if (size1 <= 1) {
                    return first() == set.first();
                }
                
                if ((hash != 0) && (set.hash != 0) && (hash != set.hash)) {
                    return false;
                }
                
                Node iterator1 = root;
                
                while (iterator1.leftChild != null) {
                    iterator1 = iterator1.leftChild;
                }
                
                Node iterator2 = set.root;
                
                while (iterator2.leftChild != null) {
                    iterator2 = iterator2.leftChild;
                }
                
                do {
                    if ((iterator1.index != iterator2.index) ||
                        (iterator1.bitSet != iterator2.bitSet)) {
                        
                        return false;
                    }
                    
                    iterator1 = findNextInOrderNode(iterator1);
                    iterator2 = findNextInOrderNode(iterator2);
                    
                    // Note: This is a double null check as only null values can
                    // be equal at this stage.
                    if (iterator1 == iterator2) {
                        return true;
                    }
                    
                    if ((iterator1 == null) || (iterator2 == null)) {
                        return false;
                    }
                } while (true);
            }
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (hash == 0) {
            int hash = 0;
            int size = size();
            
            if (size > 0) {
                if (size == 1) {
                    hash = first() * 31;
                }
                
                else {
                    hash = size * 31;
                    Node iterator = root;
                    
                    while (iterator.leftChild != null) {
                        iterator = iterator.leftChild;
                    }
                    
                    do {
                        long value = iterator.bitSet;
                        
                        hash = (hash * (iterator.index + 1)) ^ (int)value ^ (int)(value >> 32);
                        
                        iterator = findNextInOrderNode(iterator);
                    } while (iterator != null);
                }
            }
            
            this.hash = hash == 0 ? 1 : hash;
        }
        
        return hash;
    }
    
    /**
     * {@inheritDoc}
     */
    public IdSet clone() {
        return new IdSet(this);
    }
    
    /**
     * Clones a specified node and all its descendants.
     * 
     * @param node A node which should be cloned.
     * @param parent The parent of the cloned node.
     * @return A clone of the specified node with the specified parent as its
     * parent.
     */
    private Node cloneNode(Node node, Node parent) {
        Node newNode = createNode();
        
        newNode.parent = parent;
        newNode.leftChild = node.leftChild != null ?
                cloneNode(node.leftChild,newNode) : null;
        newNode.rightChild = node.rightChild != null ?
                cloneNode(node.rightChild,newNode) : null;
        newNode.isRed = node.isRed;
        newNode.index = node.index;
        newNode.bitSet = node.bitSet;
        
        return newNode;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[size=");
        buffer.append(size());
        buffer.append(", values=[");
        
        int iterator = first();
        
        while (iterator != -1) {
            buffer.append(iterator);
            
            iterator = next(iterator);
            
            if (iterator != -1) {
                buffer.append(", ");
            }
        }
        
        buffer.append("]]");
        
        return buffer.toString();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * This class represents a node of the red-black tree used by {@link IdSet}.
     * Besides the usual red-black tree properties parent, leftChild, rightChild
     * and the color it has also an index and a bit-set in order to be able to
     * store 64 ids of the set. The index specifies the position of the ids and
     * the bit-set stores the next 64 ids at this index via bits.
     */
    static final class Node {
        
        /**
         * The parent of the node.
         */
        Node parent;
        
        /**
         * The left child of the node.
         */
        Node leftChild;
        
        /**
         * The right child of the node.
         */
        Node rightChild;
        
        /**
         * Specifies whether the node is red or black.
         */
        boolean isRed;
        
        /**
         * The index at which ids are stored. 
         */
        int index;
        
        /**
         * A bit-set which specifies for the next 64 ids whether they are in
         * the bit-set. A set bit indicates that the id is in the set and a
         * cleared bit indicates that the id is not in the set.
         */
        long bitSet;
    }
}
