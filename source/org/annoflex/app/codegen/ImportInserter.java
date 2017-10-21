/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.codegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.annoflex.app.dom.Config;
import org.annoflex.app.dom.ImportInfo;
import org.annoflex.app.dom.TypeDescriptor;
import org.annoflex.util.text.CommandQueue;
import org.annoflex.util.text.LineInfo;
import org.annoflex.util.text.Span;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class ImportInserter {
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void insertImports(Config config, HashSet<TypeDescriptor> newImports,
            CommandQueue commandQueue) {
        
        ArrayList<ImportInfo> curImports = config.getImports();
        
        if (curImports.isEmpty()) {
            ArrayList<ImportEntry> sortedImports = createSortedImportList(newImports);
            Span packageDeclaration = config.getPackageDeclaration();
            
            if (packageDeclaration != null) {
                insertImportListAt(sortedImports,packageDeclaration.end,1,config,commandQueue);
                return;
            }
            
            Span prologComment = config.getPrologComment();
            
            if (prologComment != null) {
                insertImportListAt(sortedImports,prologComment.end,0,config,commandQueue);
                return;
            }
            
            insertImportListAt(sortedImports,0,0,config,commandQueue);
        }
        
        else {
            newImports = removeExistingImports(newImports,curImports);
            
            if (newImports != null) {
                insertIntoImportList(newImports,config,commandQueue);
            }
        }
    }
    
    /**
     * 
     */
    private void insertImportListAt(ArrayList<ImportEntry> sortedImports,
            int insertionPosition, int startSeparatorCount, Config config,
            CommandQueue commandQueue) {
        
        // TODO: Detect whether an empty line after the imports is necessary.
        if (!isLineStart(insertionPosition,config)) {
            
            // insert new line if the insertion position has content after it in the
            // same line otherwise skip the line separator in order no to produce
            // unwanted whitespace after the new imports
            int newInsertionPosition = skipLineSeparator(insertionPosition,config);
            
            if (newInsertionPosition == insertionPosition) {
                startSeparatorCount++;
            }
            
            else {
                insertionPosition = newInsertionPosition;
            }
        }
        
        String importBlock = createImportBlock(sortedImports,startSeparatorCount,0,config);
        commandQueue.insertion(insertionPosition,importBlock);
    }
    
    //================
    // Filter Methods
    //================
    
    /**
     * 
     */
    private HashSet<TypeDescriptor> removeExistingImports(
            HashSet<TypeDescriptor> newImports,
            ArrayList<ImportInfo> curImports) {
        
        HashSet<TypeDescriptor> newSet = null;
        Iterator<TypeDescriptor> iterator = newImports.iterator();
        
        while (iterator.hasNext()) {
            TypeDescriptor curType = iterator.next();
            
            if (!containsType(curImports,curType)) {
                if (newSet == null) {
                    newSet = new HashSet<>();
                }
                
                newSet.add(curType);
            }
        }
        
        return newSet;
    }
    
    /**
     * 
     */
    private boolean containsType(ArrayList<ImportInfo> imports,
            TypeDescriptor type) {
        
        int size = imports.size();
        
        for (int i=0;i<size;i++) {
            if (containsType(imports.get(i),type)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    private boolean containsType(ImportInfo importInfo, TypeDescriptor type) {
        if (importInfo.getIsStatic()) {
            return false;
        }
        
        TypeDescriptor importType = importInfo.getType();
        int typeComponentCount = type.getComponentCount();
        int importTypeComponentCount = importType.getComponentCount();
        
        if (importInfo.getOnDemand()) {
            typeComponentCount--;
        }
        
        if (importTypeComponentCount != typeComponentCount) {
            return false;
        }
        
        for (int i=0;i<importTypeComponentCount;i++) {
            if (!importType.getComponent(i).equals(type.getComponent(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    //========================
    // List Insertion Methods
    //========================
    
    /**
     * 
     */
    private void insertIntoImportList(HashSet<TypeDescriptor> newImports,
            Config config, CommandQueue commandQueue) {
        
        // create import list with existing and new imports
        ArrayList<ImportEntry> allImports = createImportEntries(newImports,config);
        
        // determine groups of contiguous new imports
        ArrayList<ImportGroup> importGroups = createImportGroups(allImports);
        
        // append insertion for each group
        for (int i=0;i<importGroups.size();i++) {
            ImportGroup curGroup = importGroups.get(i);
            
            // before last existing import
            if (curGroup.nextImport != null) {
                int insertionPosition = curGroup.nextImport.importInfo.getSourceRange().start;
                int startSeparatorCount = 0;
                int endSeparatorCount = 0;
                
                // insert new line if the import does not start at a line
                if (!isLineStart(insertionPosition,config)) {
                    startSeparatorCount++;
                }
                
                // insert new line if the imports at the start do not start with
                // the same component and there is not already a separating line
                // between previous and next import
                if ((curGroup.prevImport != null) &&
                    !hasSameFirstComponent(curGroup.prevImport.type,curGroup.getFirstImport().type) &&
                    (getLineDelta(curGroup.prevImport.importInfo.getSourceRange().end,
                            curGroup.nextImport.importInfo.getSourceRange().start,config) <= 1)) {
                    
                    startSeparatorCount++;
                }
                
                // insert new line if the imports at the end do not start with the same component
                if (!hasSameFirstComponent(curGroup.getLastImport().type,curGroup.nextImport.type)) {
                    endSeparatorCount++;
                }
                
                String code = createImportBlock(curGroup.imports,
                        startSeparatorCount,endSeparatorCount,config);
                commandQueue.insertion(insertionPosition,code);
            }
            
            // after last existing import
            else {
                int insertionPosition = curGroup.prevImport.importInfo.getSourceRange().end;
                int startSeparatorCount = 0;
                
                // insert new line if the imports at the start do not start with the same component
                if (!hasSameFirstComponent(curGroup.prevImport.type,curGroup.getFirstImport().type)) {
                    startSeparatorCount++;
                }
                
                // insert new line if the import has content after it in the same line
                // otherwise skip the line separator in order no to produce unwanted
                // whitespace after the new imports
                int newInsertionPosition = skipLineSeparator(insertionPosition,config);
                
                if (newInsertionPosition == insertionPosition) {
                    startSeparatorCount++;
                }
                
                else {
                    insertionPosition = newInsertionPosition;
                }
                
                String code = createImportBlock(curGroup.imports,startSeparatorCount,0,config);
                commandQueue.insertion(insertionPosition,code);
            }
        }
    }
    
    /**
     * 
     */
    private ArrayList<ImportEntry> createImportEntries(
            HashSet<TypeDescriptor> newImports, Config config) {
        
        ArrayList<ImportEntry> imports = new ArrayList<>();
        
        // append existing imports
        ArrayList<ImportInfo> curImports = config.getImports();
        
        for (int i=0;i<curImports.size();i++) {
            ImportInfo curImportInfo = curImports.get(i);
            
            imports.add(new ImportEntry(curImportInfo.getType(),curImportInfo));
        }
        
        // insert new imports
        Iterator<TypeDescriptor> newImportIterator = newImports.iterator();
        
        while (newImportIterator.hasNext()) {
            TypeDescriptor importType = newImportIterator.next();
            ImportEntry newImportEntry = new ImportEntry(importType,null);
            
            imports.add(getInsertionIndex(imports,newImportEntry),newImportEntry);
        }
        
        return imports;
    }
    
    /**
     * 
     */
    private int getInsertionIndex(ArrayList<ImportEntry> imports,
            ImportEntry newImportEntry) {
        
        int size = imports.size();
        
        for (int i=0;i<size;i++) {
            if (imports.get(i).compareTo(newImportEntry) >= 0) {
                return i;
            }
        }
        
        return size;
    }
    
    /**
     * 
     */
    private ArrayList<ImportGroup> createImportGroups(
            ArrayList<ImportEntry> allImports) {
        
        ArrayList<ImportGroup> importGroups = new ArrayList<>();
        ImportGroup curGroup = null;
        int size = allImports.size();
        
        for (int i=0;i<size;i++) {
            ImportEntry curEntry = allImports.get(i);
            
            if (curEntry.importInfo == null) {
                ImportEntry nextImport = i < (size - 1) ? allImports.get(i+1) : null;
                
                if (curGroup == null) {
                    ImportEntry prevImport = i > 0 ? allImports.get(i-1) : null;
                    
                    curGroup = new ImportGroup(prevImport,nextImport);
                    importGroups.add(curGroup);
                }
                
                curGroup.addImport(curEntry,nextImport);
            }
            
            else {
                curGroup = null;
            }
        }
        
        return importGroups;
    }
    
    /**
     * 
     */
    private boolean hasSameFirstComponent(TypeDescriptor type1,
            TypeDescriptor type2) {
        
        return (type1 != null) && (type2 != null) &&
               type1.getComponent(0).equals(type2.getComponent(0));
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * 
     */
    private ArrayList<ImportEntry> createSortedImportList(HashSet<TypeDescriptor> newImports) {
        ArrayList<ImportEntry> sortedImports = new ArrayList<>();
        Iterator<TypeDescriptor> iterator = newImports.iterator();
        
        while (iterator.hasNext()) {
            sortedImports.add(new ImportEntry(iterator.next(),null));
        }
        
        sortedImports.sort(null);
        
        return sortedImports;
    }
    
    /**
     * 
     */
    private boolean isLineStart(int position, Config config) {
        return position == config.getTextInfo().getLineInfo().lineStartAt(position);
    }
    
    /**
     * 
     */
    private int getLineDelta(int index1, int index2, Config config) {
        LineInfo lineInfo = config.getTextInfo().getLineInfo();
        
        return Math.abs(lineInfo.lineAt(index1) - lineInfo.lineAt(index2));
    }
    
    /**
     * 
     */
    private int skipLineSeparator(int position, Config config) {
        LineInfo lineInfo = config.getTextInfo().getLineInfo();
        int line = lineInfo.lineAt(position);
        
        if (position == lineInfo.lineContentEnd(line)) {
            return lineInfo.lineEnd(line);
        }
        
        return position;
    }
    
    /**
     * 
     */
    private String createImportBlock(ArrayList<ImportEntry> imports,
            int startSeparatorCount, int endSeparatorCount, Config config) {
        
        if (!imports.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            String lineSeparator = config.getTextInfo().getLineSeparator();
            
            StringToolkit.append(builder,lineSeparator,startSeparatorCount);
            
            int size = imports.size();
            ImportEntry lastImport = null;
            
            for (int i=0;i<size;i++) {
                ImportEntry curImport = imports.get(i);
                
                if ((lastImport != null) &&
                    !hasSameFirstComponent(lastImport.type,curImport.type)) {
                    
                    builder.append(lineSeparator);
                }
                
                builder.append("import ");
                builder.append(curImport.type.getQualifiedName());
                builder.append(';');
                builder.append(lineSeparator);
                
                lastImport = curImport;
            }
            
            StringToolkit.append(builder,lineSeparator,endSeparatorCount);
            
            return builder.toString();
        }
        
        return null;
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class ImportEntry implements Comparable<ImportEntry> {
        
        public final TypeDescriptor type;
        public final ImportInfo importInfo;
        
        /**
         * 
         */
        public ImportEntry(TypeDescriptor type, ImportInfo importInfo) {
            this.type = type;
            this.importInfo = importInfo;
        }
        
        /**
         * 
         */
        public boolean getIsStatic() {
            return importInfo != null ? importInfo.getIsStatic() : false;
        }
        
        /**
         * 
         */
        public boolean getOnDemand() {
            return importInfo != null ? importInfo.getOnDemand() : false;
        }
        
        /**
         * 
         */
        public int compareTo(ImportEntry o) {
            
            // first order
            if (getIsStatic() != o.getIsStatic()) {
                return getIsStatic() ? -1 : 1;
            }
            
            // second order
            int componentCount = type.getComponentCount();
            int oComponentCount = o.type.getComponentCount();
            int minComponentCount = Math.min(componentCount,oComponentCount);
            
            for (int i=0;i<minComponentCount;i++) {
                int result = type.getComponent(i).compareTo(o.type.getComponent(i));
                
                if (result != 0) {
                    return result;
                }
            }
            
            if (componentCount != oComponentCount) {
                return componentCount - oComponentCount;
            }
            
            // third order
            if (getOnDemand() != o.getOnDemand()) {
                return getOnDemand() ? 1 : -1;
            }
            
            return 0;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            StringBuilder builder = new StringBuilder();
            
            if (importInfo != null) {
                builder.append("Old: ");
                builder.append(importInfo);
            }
            
            else {
                builder.append("New: ");
                builder.append(type.getQualifiedName());
            }
            
            return builder.toString();
        }
    }
    
    /**
     * 
     */
    static final class ImportGroup {
        
        public final ArrayList<ImportEntry> imports = new ArrayList<>();
        public ImportEntry prevImport;
        public ImportEntry nextImport;
        
        /**
         * 
         */
        public ImportGroup(ImportEntry prevImport, ImportEntry nextImport) {
            this.prevImport = prevImport;
            this.nextImport = nextImport;
        }
        
        /**
         * 
         */
        public void addImport(ImportEntry importEntry, ImportEntry nextImport) {
            imports.add(importEntry);
            this.nextImport = nextImport;
        }
        
        /**
         * 
         */
        public ImportEntry getFirstImport() {
            return imports.get(0);
        }
        
        /**
         * 
         */
        public ImportEntry getLastImport() {
            return imports.get(imports.size()-1);
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            StringBuilder builder = new StringBuilder();
            
            builder.append("[prev=");
            builder.append(prevImport);
            builder.append(",next=");
            builder.append(nextImport);
            builder.append(",imports=");
            builder.append(imports);
            builder.append("]");
            
            return builder.toString();
        }
    }
}
