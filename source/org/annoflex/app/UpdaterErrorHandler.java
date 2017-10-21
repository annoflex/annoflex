/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import org.annoflex.util.BundleCache;
import org.annoflex.util.problem.ErrorHandler;
import org.annoflex.util.problem.Handling;
import org.annoflex.util.problem.Problem;
import org.annoflex.util.text.LineInfo;
import org.annoflex.util.text.Span;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
final class UpdaterErrorHandler implements ErrorHandler<Span> {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final TextInfo textInfo;
    
    /**
     * 
     */
    private final BundleCache bundleCache;
    
    /**
     * 
     */
    private final Console console;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public UpdaterErrorHandler(TextInfo textInfo, BundleCache bundleCache,
            Console console) {
        
        this.textInfo = textInfo;
        this.bundleCache = bundleCache;
        this.console = console;
    }
    
    //=======================
    // Error Handler Methods
    //=======================
    
    /**
     * 
     */
    public Handling lookupHandling(Problem problem) {
        return null;
    }
    
    /**
     * 
     */
    public void handleError(Problem problem, Span context) {
        console.printError(createMessage(problem,context));
        printContext(context);
    }
    
    /**
     * 
     */
    public void handleWarning(Problem problem, Span context) {
        console.printWarning(createMessage(problem,context));
        printContext(context);
    }
    
    //=================
    // Message Methods
    //=================
    
    /**
     * 
     */
    private String createMessage(Problem problem, Span context) {
        return bundleCache.getString(problem) + createLineInfo(context);
    }
    
    /**
     * 
     */
    private String createLineInfo(Span context) {
        if ((context != null) && !context.equals(0,0)) {
            LineInfo lineInfo = textInfo.getLineInfo();
            int firstLine = lineInfo.lineAt(context.start) + 1;
            int lastLine = lineInfo.lineAt(context.end-1) + 1;
            
            StringBuilder buffer = new StringBuilder();
            buffer.append(" (");
            buffer.append(bundleCache.getString(Messages.ON_ROW));
            buffer.append(" ");
            
            if (firstLine == lastLine) {
                buffer.append(firstLine);
            }
            
            else {
                buffer.append(firstLine);
                buffer.append("-");
                buffer.append(lastLine);
            }
            
            buffer.append(")");
            
            return buffer.toString();
        }
        
        return "";
    }
    
    //=================
    // Context Methods
    //=================
    
    /**
     * 
     */
    private void printContext(Span context) {
        if ((context != null) && !context.equals(0,0)) {
            Span[] contextList = getContextList(context);
            
            for (int i=0;i<contextList.length;i++) {
                boolean showStartArrow = (i == 0);
                boolean showEndArrow = (i == contextList.length - 1);
                
                printSingleContext(contextList[i],showStartArrow,showEndArrow);
            }
            
        }
    }
    
    /**
     * 
     */
    private Span[] getContextList(Span span) {
        LineInfo lineInfo = textInfo.getLineInfo();
        int firstLine = lineInfo.lineAt(span.start);
        int lastLine = lineInfo.lineAt(span.end-1);
        
        if (lastLine > firstLine) {
            int lineCount = lastLine - firstLine + 1;
            Span[] list = new Span[lineCount];
            
            list[0] = new Span(span.start,lineInfo.lineContentEnd(firstLine));
            
            for (int i=1;i<list.length-1;i++) {
                int curLine = firstLine + i;
                
                list[i] = new Span(lineInfo.lineStart(curLine),lineInfo.lineContentEnd(curLine));
            }
            
            int lastLineStart = lineInfo.lineStart(lastLine);
            list[lineCount-1] = new Span(lastLineStart,span.end);
            
            return list;
        }
        
        return new Span[] {span};
    }
    
    /**
     * 
     */
    private void printSingleContext(Span context, boolean showStartArrow,
            boolean showEndArrow) {
        
        Line line = createLine(context,4);
        String marker = generateMarker(line,showStartArrow,showEndArrow);
        
        int startIndex = StringToolkit.skip(line.content,0,
                line.content.length(),StringToolkit.WHITESPACE,false);
        
        if ((startIndex != line.content.length()) &&
            (line.content.charAt(startIndex) == '*')) {
            
            startIndex--;
        }
        
        console.print(line.content.substring(startIndex));
        console.print(marker.substring(startIndex));
    }
    
    /**
     * 
     */
    private Line createLine(Span context, int tabWidth) {
        String fileContent = textInfo.getText();
        LineInfo lineInfo = textInfo.getLineInfo();
        int line = lineInfo.lineAt(context.start);
        int lineStart = lineInfo.lineStart(line);
        int lineContentLength = lineInfo.lineContentLength(line);
        int contextStart = context.start - lineStart;
        int contextEnd = contextStart + context.length();
        int newContextStart = contextStart;
        int newContextEnd = contextEnd;
        
        StringBuilder builder = new StringBuilder();
        
        for (int i=0;i<lineContentLength;i++) {
            char curChar = fileContent.charAt(lineStart+i);
            
            if (curChar != '\t') {
                builder.append(curChar);
            }
            
            else {
                for (int j=0;j<tabWidth;j++) {
                    builder.append(" ");
                }
                
                if (i < contextStart) {
                    newContextStart += tabWidth - 1;
                }
                
                if (i < contextEnd) {
                    newContextEnd += tabWidth - 1;
                }
            }
        }
        
        return new Line(builder.toString(),newContextStart,newContextEnd);
    }
    
    /**
     * 
     */
    private String generateMarker(Line line, boolean showStartArrow,
            boolean showEndArrow) {
        
        StringBuilder builder = new StringBuilder();
        
        for (int i=0;i<line.contextStart;i++) {
            builder.append(" ");
        }
        
        builder.append(showStartArrow ? "^" : "-");
        
        int contextLength = line.contextEnd - line.contextStart;
        
        for (int i=0;i<contextLength-2;i++) {
            builder.append("-");
        }
        
        if (contextLength >= 2) {
            builder.append(showEndArrow ? "^" : "-");
        }
        
        return builder.toString();
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class Line {
        
        /**
         * 
         */
        public final String content;
        
        /**
         * 
         */
        public final int contextStart;
        
        /**
         * 
         */
        public final int contextEnd;
        
        /**
         * 
         */
        public Line(String content, int contextStart, int contextEnd) {
            this.content = content;
            this.contextStart = contextStart;
            this.contextEnd = contextEnd;
        }
    }
}
