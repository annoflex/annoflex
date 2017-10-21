/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import java.util.HashSet;

import org.annoflex.app.codegen.AutomatonInfo;
import org.annoflex.app.codegen.ImportInserter;
import org.annoflex.app.codegen.ScannerGenerator;
import org.annoflex.app.dom.Config;
import org.annoflex.app.dom.MethodInfo;
import org.annoflex.app.dom.TypeDescriptor;
import org.annoflex.app.parser.ConfigParser;
import org.annoflex.regex.automaton.Automaton;
import org.annoflex.regex.automaton.Rule;
import org.annoflex.regex.automaton.AutomatonCompiler;
import org.annoflex.util.integer.IntHandler;
import org.annoflex.util.problem.DefaultErrorHandler;
import org.annoflex.util.problem.ErrorHandler;
import org.annoflex.util.text.CommandQueue;
import org.annoflex.util.text.Section;
import org.annoflex.util.text.Span;
import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
final class Updater {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    public static final int MAX_DFA_STATE_COUNT = Short.MAX_VALUE;
    
    /**
     * 
     */
    private static final IntHandler NON_LINE_TERMINATOR_CHAR = StringToolkit
            .createHandler(new char[]{'\n','\r'},true);
    
    /**
     * 
     */
    private static final IntHandler TAB_CHAR = StringToolkit.createHandler('\t',false);
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private ErrorHandler<Span> errorHandler;
    
    /**
     * 
     */
    private boolean errors;
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setErrorHandler(ErrorHandler<Span> errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    /**
     * 
     */
    public ErrorHandler<Span> getErrorHandler() {
        return errorHandler;
    }
    
    /**
     * 
     */
    public boolean hasErrors() {
        return errors;
    }
    
    /**
     * 
     */
    public String update(TextInfo textInfo) {
        errors = false;
        
        // parse content of source file
        ConfigParser configParser = new ConfigParser();
        configParser.setErrorHandler(errorHandler);
        
        Config config = configParser.parse(textInfo);
        errors |= configParser.hasErrors();
        
        if (errors) {
            return null;
        }
        
        // compute DFA based on the data of the source file
        AutomatonCompiler<MethodInfo> compiler = new AutomatonCompiler<>();
        compiler.setErrorHandler(new RuleListCompilerErrorHandler(errorHandler,config));
        
        Automaton<MethodInfo> automaton = compiler.compile(config.getRuleList());
        errors |= compiler.hasErrors();
        
        if (errors) {
            return null;
        }
        
        // check DFA state count
        int totalDFAStateCount = automaton.getDFAList().getTotalStateCount();
        
        if (totalDFAStateCount > MAX_DFA_STATE_COUNT) {
            errors |= Problems.TOO_MANY_DFA_STATES.report(errorHandler,
                    Span.EMPTY_SPAN);
            
            if (errors) {
                return null;
            }
        }
        
        // compute scanner code
        AutomatonInfo automatonInfo = new AutomatonInfo(automaton);
        Span indentationSpan = determineIndentationSpan(config);
        int indentationSize = 4;
        boolean indentationWithTabs = StringToolkit.containsOnly(textInfo
                .getText(),indentationSpan,TAB_CHAR);
        String lineSeparator = textInfo.getLineSeparator();
        
        ScannerGenerator generator = new ScannerGenerator();
        generator.setIndentationSize(indentationSize);
        generator.setIndentationWithTabs(indentationWithTabs);
        generator.setLineSeparator(lineSeparator);
        
        String scannerCode = generator.generate(config,automatonInfo);
        
        // compute new content of source code file
        Section codeArea = config.getCodeArea();
        Span codeAreaStart = codeArea.getStart();
        Span codeAreaEnd = codeArea.getEnd();
        
        CommandQueue commandQueue = new CommandQueue(textInfo.getText());
        
        // insert imports of the scanner generator
        HashSet<TypeDescriptor> imports = generator.getImports();
        
        if ((imports != null) && !imports.isEmpty()) {
            new ImportInserter().insertImports(config,imports,commandQueue);
        }
        
        // insert line separator after the start marker of the code area
        commandQueue.insertion(codeAreaStart.end,lineSeparator);
        
        // Insert new scanner code. The line terminator transformation is
        // usually not necessary but it ensures that the result is always
        // homogeneous independent of what the code generator generates. The
        // overhead for it is very low and the benefits are high so it should be
        // done in any case.
        commandQueue.replacement(codeAreaStart.end,codeAreaEnd.start,
                scannerCode,StringToolkit.LINE_TERMINATOR,lineSeparator);
        
        // indent end marker of code area
        String endMarkerIndent = createEndMarkerIndent(indentationSize,
                indentationWithTabs);
        commandQueue.insertion(codeAreaEnd.start,endMarkerIndent);
        
        return commandQueue.applyCommands();
    }
    
    /**
     * 
     */
    private Span determineIndentationSpan(Config config) {
        String fileContent = config.getTextInfo().getText();
        int start = config.getCodeArea().getStart().start;
        int end = StringToolkit.skip(fileContent,start,0,
                NON_LINE_TERMINATOR_CHAR,true);
        
        return new Span(end,start);
    }
    
    /**
     * 
     */
    private String createEndMarkerIndent(int indentationSize,
            boolean indentationWithTabs) {
        
        if (indentationWithTabs) {
            return "\t";
        }
        
        return StringToolkit.createString(' ',indentationSize);
    }
    
    //==================
    // Internal Classes
    //==================
    
    /**
     * 
     */
    static final class RuleListCompilerErrorHandler extends DefaultErrorHandler<Rule<MethodInfo>,Span> {
        
        /**
         * 
         */
        private final Config config;
        
        /**
         * 
         */
        public RuleListCompilerErrorHandler(ErrorHandler<Span> handler,
                Config config) {
            
            super(handler);
            
            this.config = config;
        }
        
        /**
         * 
         */
        protected Span convertContext(Rule<MethodInfo> context) {
            return context != null ? context.getAction().getExprSpan() :
                config.getTypeDeclaration();
        }
    }
}
