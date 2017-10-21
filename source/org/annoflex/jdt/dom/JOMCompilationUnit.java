/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

import java.util.ArrayList;

/**
 * @author Stefan Czaska
 */
public class JOMCompilationUnit extends JOMNode {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMCompilationUnit() {
        super(JOMNodeType.COMPILATION_UNIT);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public JOMPackage getPackage() {
        return (JOMPackage)getChildByType(JOMNodeType.PACKAGE,false);
    }
    
    /**
     * 
     */
    public ArrayList<JOMImport> collectImports() {
        JOMNode iterator = getChildByType(JOMNodeType.IMPORT,false);
        
        if (iterator != null) {
            ArrayList<JOMImport> list = new ArrayList<>();
            
            do {
                list.add((JOMImport)iterator);
                
                iterator = iterator.getSiblingByType(JOMNodeType.IMPORT,false);
            } while(iterator != null);
            
            return list;
        }
        
        return null;
    }
    
    /**
     * 
     */
    public ArrayList<JOMTypeDeclaration> collectTypeDeclarations() {
        JOMNode iterator = getChildByType(JOMNodeType.TYPE_DECLARATION,false);
        
        if (iterator != null) {
            ArrayList<JOMTypeDeclaration> list = new ArrayList<>();
            
            do {
                list.add((JOMTypeDeclaration)iterator);
                
                iterator = iterator.getSiblingByType(JOMNodeType.TYPE_DECLARATION,false);
            } while(iterator != null);
            
            return list;
        }
        
        return null;
    }
    
    /**
     * 
     */
    public JOMCommentList getCommentList() {
        return (JOMCommentList)getChildByType(JOMNodeType.COMMENT_LIST,false);
    }
}
