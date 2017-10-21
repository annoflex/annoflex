/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class JOMCommentList extends JOMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMCommentList() {
        super(JOMNodeType.COMMENT_LIST);
    }
}
