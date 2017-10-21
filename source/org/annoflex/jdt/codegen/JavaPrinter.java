/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.codegen;

import org.annoflex.util.SystemToolkit;

/**
 * @author Stefan Czaska
 */
public class JavaPrinter {
    
    // TODO: Add support for line wrapping.
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final StringBuilder builder = new StringBuilder();
    
    /**
     * 
     */
    private int indentationSize = 4;
    
    /**
     * 
     */
    private boolean indentationWithTabs;
    
    /**
     * 
     */
    private String lineSeparator = SystemToolkit.LINE_SEPARATOR;
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setIndentationSize(int indentationSize) {
        this.indentationSize = indentationSize;
    }
    
    /**
     * 
     */
    public int getIndentationSize() {
        return indentationSize;
    }
    
    /**
     * 
     */
    public void setIndentationWithTabs(boolean indentationWithTabs) {
        this.indentationWithTabs = indentationWithTabs;
    }
    
    /**
     * 
     */
    public boolean getIndentationWithTabs() {
        return indentationWithTabs;
    }
    
    /**
     * 
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    /**
     * 
     */
    public String getLineSeparator() {
        return lineSeparator;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public int textLength() {
        return builder.length();
    }
    
    /**
     * 
     */
    public void append(String string) {
        builder.append(string);
    }
    
    /**
     * 
     */
    public void append(String string, int count) {
        for (int i=0;i<count;i++) {
            builder.append(string);
        }
    }
    
    /**
     * 
     */
    public void append(char character) {
        builder.append(character);
    }
    
    /**
     * 
     */
    public void append(char character, int count) {
        for (int i=0;i<count;i++) {
            builder.append(character);
        }
    }
    
    /**
     * 
     */
    public void append(int value) {
        builder.append(value);
    }
    
    /**
     * 
     */
    public void appendEscaped(char character) {
        append("\\");
        
        // octal
        if (character < 256) {
            switch(character) {
            case '\b': append('b'); break;
            case '\t': append('t'); break;
            case '\n': append('n'); break;
            case '\f': append('f'); break;
            case '\r': append('r'); break;
            case '\"': append('"'); break;
            case '\'': append('\''); break;
            case '\\': append('\\'); break;
            default: append(Integer.toOctalString(character)); break;
            }
        }
        
        // 3 hex digits
        else if (character < 4096) {
            append("u0");
            append(Integer.toHexString(character));
        }
        
        // 4 hex digits
        else {
            append("u");
            append(Integer.toHexString(character));
        }
    }
    
    /**
     * 
     */
    public void indent(int count) {
        for (int i=0;i<count;i++) {
            indent();
        }
    }
    
    /**
     * 
     */
    public void indent() {
        if (indentationWithTabs) {
            append('\t');
        }
        
        else {
            append(' ',indentationSize);
        }
    }
    
    /**
     * 
     */
    public void ensureSpace() {
        int length = builder.length();
        
        if (length > 0) {
            char lastChar = builder.charAt(length-1);
            
            if ((lastChar != ' ') && (lastChar != '\n') && (lastChar != '\r')) {
                append(' ');
            }
        }
    }
    
    /**
     * 
     */
    public void lineSeparator() {
        builder.append(lineSeparator);
    }
    
    /**
     * 
     */
    public void javadoc(String... lines) {
        ensureEmptyLine(1);
        
        indent();
        append("/**");
        lineSeparator();
        
        for (int i=0;i<lines.length;i++) {
            indent();
            append(" * ");
            append(lines[i]);
            lineSeparator();
        }
        
        indent();
        append(" */");
        lineSeparator();
    }
    
    /**
     * 
     */
    public void ensureEmptyLine(int indentCount) {
        if (isEmptyLineNecessary()) {
            indent(indentCount);
            lineSeparator();
        }
    }
    
    /**
     * 
     */
    public void line(int indentCount, String line) {
        indent(indentCount);
        append(line);
        lineSeparator();
    }
    
    /**
     * 
     */
    public void variable(String visibility, String modifiers, String type,
            String name, String value) {
        
        indent();
        append(visibility);
        ensureSpace();
        append(modifiers);
        ensureSpace();
        append(type);
        append(" ");
        append(name);
        
        if (value != null) {
            append(" = ");
            append(value);
        }
        
        append(";");
        lineSeparator();
    }
    
    /**
     * 
     */
    public void variableWithMethod(String visibility, String modifiers,
            String type, String name, String methodName, String string) {
        
        // public static final Type NAME = method(
        //         "<string>");
        
        // Note: Determine the available space and the consumed space without
        // line separators in order to make the line wrapping independent of it.
        // Otherwise the code generation would behave differently on different
        // platforms.
        
        indent();
        
        int startTextLength = textLength();
        
        append(visibility);
        ensureSpace();
        append(modifiers);
        ensureSpace();
        append(type);
        append(" ");
        append(name);
        append(" = ");
        append(methodName);
        append("(");
        
        int endTextLength = textLength();
        
        lineSeparator();
        indent();
        append("\"");
        
        int length = string.length();
        int availableSpace = endTextLength - startTextLength - 1 - 3;
        int curSpace = 0;
        
        for (int i=0;i<length;) {
            int lengthBefore = textLength();
            
            appendEscaped(string.charAt(i++));
            appendEscaped(string.charAt(i++));
            
            int lengthAfter = textLength();
            int lengthDelta = lengthAfter - lengthBefore;
            
            curSpace += lengthDelta;
            
            if ((curSpace >= availableSpace) && (i < length)) {
                curSpace = 0;
                
                append("\" +");
                lineSeparator();
                indent();
                append("\"");
            }
        }
        
        append("\");");
        lineSeparator();
    }
    
    /**
     * 
     */
    public void setter(String visibility, String name, String param) {
        indent();
        append(visibility);
        
        ensureSpace();
        
        append("void ");
        append(name);
        append("(");
        append(param);
        append(") {");
        lineSeparator();
    }
    
    /**
     * 
     */
    public void getter(String visibility, String name, String returnType,
            String returnStatement) {
        
        indent();
        append(visibility);
        
        ensureSpace();
        
        append(returnType);
        append(" ");
        append(name);
        append("() {");
        lineSeparator();
        
        indent(2);
        append("return ");
        append(returnStatement);
        append(";");
        lineSeparator();
        
        indent();
        append("}");
        lineSeparator();
    }
    
    /**
     * 
     */
    public void methodHead(String visibility, String modifiers, String type,
            String name, String parameters) {
        
        methodHead(visibility,modifiers,type,name,parameters,null);
    }
    
    /**
     * 
     */
    public void methodHead(String visibility, String modifiers, String type,
            String name, String parameters, String exceptions) {
        
        indent();
        
        if (visibility != null) {
            append(visibility);
        }
        
        ensureSpace();
        
        if (modifiers != null) {
            append(modifiers);
        }
        
        ensureSpace();
        append(type);
        append(" ");
        append(name);
        append("(");
        
        if (parameters != null) {
            append(parameters);
        }
        
        append(")");
        
        if (exceptions != null) {
            append(" throws ");
            append(exceptions);
        }
        
        append(" {");
        lineSeparator();
    }
    
    //==================
    // Fragment Methods
    //==================
    
    /**
     * 
     */
    public String createIntIncrement(String destVar, String srcVar, int value) {
        if (destVar.equals(srcVar)) {
            if (value == 0) {
                return null;
            }
            
            if (value > 0) {
                if (value == 1) {
                    return destVar+"++;";
                }
                
                return destVar+" += "+Integer.valueOf(value)+";";
            }
            
            if (value == -1) {
                return destVar+"--;";
            }
            
            // handle integer minimum value as a special case as the negation
            // does not work for this value
            if (value == Integer.MIN_VALUE) {
                return destVar+" += Integer.MIN_VALUE;";
            }
            
            return destVar+" -= "+Integer.valueOf(-value)+";";
        }
        
        if (value == 0) {
            return destVar+" = "+srcVar+";";
        }
        
        if (value > 0) {
            return destVar+" = "+srcVar+" + "+Integer.valueOf(value)+";";
        }
        
        // handle integer minimum value as a special case as the negation
        // does not work for this value
        if (value == Integer.MIN_VALUE) {
            return destVar+" = "+srcVar+" + Integer.MIN_VALUE;";
        }
        
        return destVar+" = "+srcVar+" - "+Integer.valueOf(-value)+";";
    }
    
    //================
    // Helper Methods
    //================
    
    /**
     * 
     */
    private boolean isEmptyLineNecessary() {
        
        // the document begins always with an empty line
        int length = builder.length();
        
        if (length == 0) {
            return true;
        }
        
        // Otherwise an empty line is necessary if the buffer ends with a line
        // separator and the line of this line separator contains at last one
        // non-whitespace character. If this line is empty or contains only
        // whitespace then no new empty line is necessary.
        int iterator = length - 1;
        char lastChar = builder.charAt(iterator);
        
        if (((lastChar == '\n') || (lastChar == '\r')) && (iterator > 0)) {
            iterator--;
            
            if ((lastChar == '\n') && (builder.charAt(iterator) == '\r')) {
                iterator--;
            }
            
            while (iterator >= 0) {
                char curChar = builder.charAt(iterator);
                
                if ((curChar != ' ') && (curChar != '\t')) {
                    return (curChar != '\n') && (curChar != '\r');
                }
                
                iterator--;
            }
        }
        
        return false;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return builder.toString();
    }
}
