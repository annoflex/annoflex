/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Stefan Czaska
 */
@SuppressWarnings("rawtypes")
public class Node<N extends Node> {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    public static final int NOT_RELATED = Integer.MIN_VALUE;
    
    //=============
    // Tree Fields
    //=============
    
    /**
     * 
     */
    N parent;
    
    /**
     * 
     */
    N nextSibling;
    
    /**
     * 
     */
    N previousSibling;

    /**
     * 
     */
    N firstChild;
    
    /**
     * 
     */
    N lastChild;
    
    /**
     * 
     */
    int childCount;
    
    /**
     * 
     */
    int index = -1;
    
    //==============
    // Tree Methods
    //==============
    
    /**
     * 
     */
    public final N getParent() {
        return parent;
    }
    
    /**
     * 
     */
    public final N getNextSibling() {
        return nextSibling;
    }
    
    /**
     * 
     */
    public final N getPreviousSibling() {
        return previousSibling;
    }
    
    /**
     * 
     */
    public final N getSibling(boolean previous) {
        return previous ? previousSibling : nextSibling;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public N getSibling(int delta) {
        if (delta > 0) {
            Node iterator = nextSibling;
            
            while ((delta > 1) && (iterator != null)) {
                iterator = iterator.nextSibling;
                delta--;
            }
            
            return (N)iterator;
        }
        
        if (delta < 0) {
            Node iterator = previousSibling;
            
            while ((delta < -1) && (iterator != null)) {
                iterator = iterator.previousSibling;
                delta++;
            }
            
            return (N)iterator;
        }
        
        return null;
    }
    
    /**
     * 
     */
    public final N getFirstChild() {
        return firstChild;
    }
    
    /**
     * 
     */
    public final N getLastChild() {
        return lastChild;
    }
    
    /**
     * 
     */
    public final N getChild(boolean last) {
        return last ? lastChild : firstChild;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public N getChild(int index) {
        if ((index >= 0) && (index < childCount)) {
            if (index < (childCount / 2)) {
                Node iterator = firstChild;
                
                while (index > 0) {
                    iterator = iterator.nextSibling;
                    index--;
                }
                
                return (N)iterator;
            }
            
            Node iterator = lastChild;
            int end = childCount - 1;
            
            while (index < end) {
                iterator = iterator.previousSibling;
                index++;
            }
            
            return (N)iterator;
        }
        
        return null;
    }
    
    /**
     * 
     */
    public final int getChildCount() {
        return childCount;
    }
    
    /**
     * 
     */
    public final boolean hasParent() {
        return parent != null;
    }
    
    /**
     * 
     */
    public final boolean hasSibling(boolean previous) {
        return previous ? previousSibling != null : nextSibling != null;
    }
    
    /**
     * 
     */
    public final boolean hasNextSibling() {
        return nextSibling != null;
    }
    
    /**
     * 
     */
    public final boolean hasPreviousSibling() {
        return previousSibling != null;
    }
    
    /**
     * 
     */
    public final boolean isLeaf() {
        return childCount == 0;
    }
    
    /**
     * 
     */
    public final boolean hasChildren() {
        return childCount != 0;
    }
    
    /**
     * 
     */
    public final boolean isFirstChild() {
        return (parent != null) && (previousSibling == null);
    }
    
    /**
     * 
     */
    public final boolean isLastChild() {
        return (parent != null) && (nextSibling == null);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public N getRoot() {
        Node parent = this;
        Node lastParent;
        
        do {
            lastParent = parent;
            parent = parent.parent;
        } while (parent != null);
        
        return (N)lastParent;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public final N getFirstSibling() {
        return parent != null ? (N)parent.firstChild : null;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public final N getLastSibling() {
        return parent != null ? (N)parent.lastChild : null;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public N getDeepestDescendant(boolean last, boolean includeNode) {
        if (last) {
            if (lastChild != null) {
                Node iterator = lastChild;
                
                while (iterator.lastChild != null) {
                    iterator = iterator.lastChild;
                }
                
                return (N)iterator;
            }
        }
        
        else {
            if (firstChild != null) {
                Node iterator = firstChild;
                
                while (iterator.firstChild != null) {
                    iterator = iterator.firstChild;
                }
                
                return (N)iterator;
            }
        }
        
        if (includeNode) {
            return (N)this;
        }
        
        return null;
    }
    
    /**
     * 
     */
    public int getLevel() {
        if (parent != null) {
            int level = 1;
            Node iterator = parent.parent;
            
            while (iterator != null) {
                iterator = iterator.parent;
                level++;
            }
            
            return level;
        }
        
        return 0;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public int getIndex() {
        if (index == -1) {
            if (parent == null) {
                index = 0;
            }
            
            else {
                Node endNode = nextSibling;
                Node startNode = previousSibling;
                
                while ((startNode != null) && (startNode.index == -1)) {
                    startNode = startNode.previousSibling;
                }
                
                int i;
                
                if (startNode == null) {
                    i = -1;
                    startNode = parent.firstChild;
                }
                
                else {
                    i = startNode.index;
                    startNode = startNode.nextSibling;
                }
                
                while (startNode != endNode) {
                    startNode.index = ++i;
                    startNode = startNode.nextSibling;
                }
            }
        }
        
        return index;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private void resetIndex() {
        index = -1;
        
        Node iterator = nextSibling;
        
        while ((iterator != null) && (iterator.index != -1)) {
            iterator.index = -1;
            iterator = iterator.nextSibling;
        }
    }
    
    //========================
    // Reference Node Methods
    //========================
    
    /**
     * 
     */
    public boolean hasAncestor(N node) {
        if ((node != null) && (node.childCount != 0)) {
            Node iterator = parent;
            
            while (iterator != null) {
                if (iterator == node) {
                    return true;
                }
                
                iterator = iterator.parent;
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    public final boolean hasParent(N node) {
        return (node != null) && (node == parent);
    }
    
    /**
     * 
     */
    public final boolean hasSibling(N node) {
        return (node != null) && (parent != null) && (node.parent == parent);
    }
    
    /**
     * 
     */
    public final boolean hasChild(N node) {
        return (node != null) && (node.parent == this);
    }
    
    /**
     * 
     */
    public boolean hasDescendant(N node) {
        if ((node != null) && (childCount != 0)) {
            Node iterator = node.parent;
            
            while (iterator != null) {
                if (iterator == this) {
                    return true;
                }
                
                iterator = iterator.parent;
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    public int compareToSibling(N sibling) {
        if (hasSibling(sibling)) {
            if (sibling == this) {
                return 0;
            }
            
            if ((nextSibling == null) ||
                (previousSibling == sibling) ||
                (sibling.previousSibling == null)) {
                
                return 1;
            }
            
            if ((nextSibling == sibling) ||
                (previousSibling == null) ||
                (sibling.nextSibling == null)) {
                
                return -1;
            }
            
            return getIndex() < sibling.getIndex() ? -1 : 1;
        }
        
        return NOT_RELATED;
    }
    
    //===================
    // Insertion Methods
    //===================
    
    /**
     * 
     */
    public void appendChild(N newChild) {
        insertChild(newChild,null,false,false);
    }
    
    /**
     * 
     */
    public void appendChild(N newChild, boolean start) {
        insertChild(newChild,null,start,start);
    }
    
    /**
     * 
     */
    public void insertAfter(N newNode, N refNode) {
        insertChild(newNode,refNode,false,true);
    }
    
    /**
     * 
     */
    public void insertBefore(N newNode, N refNode) {
        insertChild(newNode,refNode,true,false);
    }
    
    /**
     * 
     */
    public void insertChild(N newNode, N refNode, boolean insertBefore) {
        insertChild(newNode,refNode,insertBefore,!insertBefore);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public void insertChild(N newNode, N refNode, boolean insertBefore,
            boolean appendStart) {
        
        if ((newNode != null) && (newNode != this) && !hasAncestor(newNode)) {
            if (newNode.hasParent()) {
                throw new IllegalArgumentException("new node may not have a " +
                        "parent");
            }
            
            // insert newNode as first/last child
            if (refNode == null) {
                if (appendStart) {
                    
                    // new node may not be already the first child of the new
                    // parent
                    if ((newNode.parent != this) || (newNode.previousSibling != null)) {
                        
                        // this node has no children
                        if (childCount == 0) {
                            newNode.parent = this;
                            firstChild = newNode;
                            lastChild = newNode;
                        }
                        
                        // this node has children
                        else {
                            newNode.parent = this;
                            newNode.nextSibling = firstChild;
                            firstChild.previousSibling = newNode;
                            firstChild = newNode;
                        }
                        
                        childCount++;
                        
                        resetIndex();
                    }
                }
                
                // new node may not be already the last child of the new parent
                else if ((newNode.parent != this) || (newNode.nextSibling != null)) {
                    
                    // this node has no children
                    if (childCount == 0) {
                        newNode.parent = this;
                        firstChild = newNode;
                        lastChild = newNode;
                    }
                    
                    // this node has children
                    else {
                        newNode.parent = this;
                        newNode.previousSibling = lastChild;
                        lastChild.nextSibling = newNode;
                        lastChild = newNode;
                    }
                    
                    childCount++;
                    
                    // Note: resetIndex is not necessary as the index of the
                    // inserted node is already invalid and there are no
                    // following siblings.
                }
            }
            
            // insert newNode if refNode is child of this node
            else if (refNode.parent == this) {
                if (insertBefore) {
                    
                    // new node may not be already the previous sibling of the
                    // reference node
                    if ((newNode.parent != this) || (newNode.nextSibling != refNode)) {
                        
                        // set new node
                        newNode.parent = this;
                        newNode.previousSibling = refNode.previousSibling;
                        newNode.nextSibling = refNode;
                        
                        // there is no previous sibling of refNode
                        if (refNode == firstChild) {
                            refNode.previousSibling = newNode;
                            firstChild = newNode;
                        }
                        
                        // there is a previous sibling of refNode
                        else {
                            refNode.previousSibling.nextSibling = newNode;
                            refNode.previousSibling = newNode;
                        }
                        
                        childCount++;
                        
                        resetIndex();
                    }
                }
                
                // new node may not be already the next sibling of the reference
                // node
                else if ((newNode.parent != this) || (newNode.previousSibling != refNode)) {
                    
                    // set newNode
                    newNode.parent = this;
                    newNode.nextSibling = refNode.nextSibling;
                    newNode.previousSibling = refNode;
                    
                    // there is no next sibling of refNode
                    if (refNode == lastChild) {
                        refNode.nextSibling = newNode;
                        lastChild = newNode;
                    }
                    
                    // there is a next sibling of refNode
                    else {
                        refNode.nextSibling.previousSibling = newNode;
                        refNode.nextSibling = newNode;
                    }
                    
                    childCount++;
                    
                    resetIndex();
                }
            }
        }
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public void appendChildren(N node) {
        if (node != null) {
            Node iterator = node.getFirstChild();
            
            while (iterator != null) {
                Node nextSibling = iterator.getNextSibling();
                
                iterator.remove();
                
                appendChild((N)iterator);
                
                iterator = nextSibling;
            }
        }
    }
    
    //=================
    // Removal Methods
    //=================
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public void remove() {
        if (hasParent()) {
            
            // reset index of removed node and its following siblings with a
            // valid index
            resetIndex();
            
            // this node is the only one child
            if (parent.childCount == 1) {
                parent.firstChild = null;
                parent.lastChild = null;
            }
            
            // this node is the last child
            else if (nextSibling == null) {
                parent.lastChild = previousSibling;
                previousSibling.nextSibling = null;
                previousSibling = null;
            }
            
            // this node is the first child
            else if (previousSibling == null) {
                parent.firstChild = nextSibling;
                nextSibling.previousSibling = null;
                nextSibling = null;
            }
            
            // this node is in the middle of the children
            else {
                nextSibling.previousSibling = previousSibling;
                previousSibling.nextSibling = nextSibling;
                nextSibling = null;
                previousSibling = null;
            }
            
            // remove from parent
            parent.childCount--;
            parent = null;
        }
    }
    
    /**
     * 
     */
    public void removeAllChildren() {
        Node iterator = getLastChild();
        
        while (iterator != null) {
            Node sibling = iterator.previousSibling;
            
            iterator.remove();
            
            iterator = sibling;
        }
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public void removeAllDescendants() {
        removeAllDescendantsInternal((N)this);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private void removeAllDescendantsInternal(N node) {
        Node iterator = node.getLastChild();
        
        while (iterator != null) {
            Node sibling = iterator.getPreviousSibling();
            
            removeAllDescendantsInternal((N)iterator);
            iterator.remove();
            
            iterator = sibling;
        }
    }
    
    //====================
    // Conversion Methods
    //====================
    
    /**
     * 
     */
    public TreeModel toTreeModel() {
        return new NodeTreeModel(this);
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getClass().getSimpleName();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class NodeTreeModel implements TreeModel {
        
        /**
         * 
         */
        private final Node root;
        
        /**
         * 
         */
        NodeTreeModel(Node root) {
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
            return parent != null ? ((Node)parent).getChildCount() : 0;
        }
        
        /**
         * 
         */
        public Object getChild(Object parent, int index) {
            return parent != null ? ((Node)parent).getChild(index) : null;
        }
        
        /**
         * 
         */
        public boolean isLeaf(Object node) {
            return (node != null) && ((Node)node).isLeaf();
        }
        
        /**
         * 
         */
        @SuppressWarnings("unchecked")
        public int getIndexOfChild(Object parent, Object child) {
            if ((parent != null) && (child != null)) {
                Node childNode = (Node)child;
                
                if (((Node)parent).hasChild(childNode)) {
                    return childNode.getIndex();
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
