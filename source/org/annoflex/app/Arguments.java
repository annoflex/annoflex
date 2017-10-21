/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

import org.annoflex.util.BundleCache;
import org.annoflex.util.Message;
import org.annoflex.util.SystemToolkit;
import org.annoflex.util.text.StringToolkit;
import org.annoflex.util.text.TextFile;
import org.annoflex.util.text.TextFormatter;

/**
 * @author Stefan Czaska
 */
final class Arguments {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final BundleCache bundleCache;
    
    /**
     * 
     */
    private final Console console;
    
    /**
     * 
     */
    private AppMode appMode = AppMode.NO_ACTION;
    
    /**
     * 
     */
    private BOMMode bomMode = BOMMode.AUTO;
    
    /**
     * 
     */
    private String charSet = TextFile.DEFAULT_CHAR_SET;
    
    /**
     * 
     */
    private LineSeparatorMode lineSeparatorMode = LineSeparatorMode.AUTO;
    
    /**
     * 
     */
    private File file;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public Arguments(BundleCache bundleCache, Console console, String[] args) {
        this.bundleCache = bundleCache;
        this.console = console;
        
        args = removeInvalidEntries(args);
        
        if ((args == null) || (args.length == 0)) {
            appMode = AppMode.PRINT_HELP;
            return;
        }
        
        for (int i=0;i<args.length;i++) {
            String curArg = args[i];
            
            switch(curArg) {
            case "-b":
            case "--bom":
                if (!isValidValue(args,i+1)) {
                    console.printError(curArg+" "+bundleCache.getString(
                            Messages.BOM_MODE_REQUIRED));
                    return;
                }
                
                String bom = args[++i];
                
                switch(bom.toLowerCase()) {
                case "on":   bomMode = BOMMode.ON; break;
                case "off":  bomMode = BOMMode.OFF; break;
                case "auto": bomMode = BOMMode.AUTO; break;
                default:
                    console.printError(bom+" "+bundleCache.getString(
                            Messages.INVALID_BOM_VALUE));
                    return;
                }
                break;
            
            case "-c":
            case "--charset":
                if (!isValidValue(args,i+1)) {
                    console.printError(curArg+" "+bundleCache.getString(
                            Messages.CHARSET_REQUIRED));
                    return;
                }
                
                String charSet = args[++i];
                
                try {
                    Charset.forName(charSet);
                }
                
                catch(IllegalCharsetNameException | UnsupportedCharsetException e) {
                    console.printError(charSet+" "+bundleCache.getString(
                            Messages.UNSUPPORTED_ENCODING));
                    return;
                }
                
                this.charSet = charSet;
                break;
            
            case "-h":
            case "--help":
                if (!isFirstAndOnlyParam(args,curArg,i)) {
                    console.printError(curArg+" "+bundleCache.getString(
                            Messages.FIRST_AND_ONLY_PARAM));
                    return;
                }
                
                appMode = AppMode.PRINT_HELP;
                return;
            
            case "-l":
            case "--linesep":
                if (!isValidValue(args,i+1)) {
                    console.printError(curArg+" "+bundleCache.getString(
                            Messages.LINE_SEPARATOR_REQUIRED));
                    return;
                }
                
                String lineSeparator = args[++i];
                
                switch(lineSeparator.toLowerCase()) {
                case "lf":     lineSeparatorMode = LineSeparatorMode.LF; break;
                case "cr":     lineSeparatorMode = LineSeparatorMode.CR; break;
                case "crlf":   lineSeparatorMode = LineSeparatorMode.CRLF; break;
                case "system": lineSeparatorMode = LineSeparatorMode.SYSTEM; break;
                case "auto":   lineSeparatorMode = LineSeparatorMode.AUTO; break;
                default:
                    console.printError(lineSeparator+" "+bundleCache.getString(
                            Messages.INVALID_LINE_SEPARATOR_VALUE));
                    return;
                }
                break;
            
            case "-v":
            case "--version":
                if (!isFirstAndOnlyParam(args,curArg,i)) {
                    console.printError(curArg+" "+bundleCache.getString(
                            Messages.FIRST_AND_ONLY_PARAM));
                    return;
                }
                
                appMode = AppMode.PRINT_VERSION;
                return;
            
            default:
                if (curArg.startsWith("-")) {
                    console.printError(curArg+" "+bundleCache.getString(
                            Messages.UNKNOWN_PARAM));
                    return;
                }
                
                if (!isLastParam(args,i)) {
                    console.printError(curArg+" "+bundleCache.getString(
                            Messages.INVALID_PARAM));
                    return;
                }
                
                file = new File(curArg);
            }
        }
        
        // validate file
        if (file == null) {
            console.printError(bundleCache.getString(Messages.MISSING_FILE_PARAM));
            return;
        }
        
        if (!file.exists()) {
            console.printError("\""+file+"\" "+bundleCache.getString(Messages.DOES_NOT_EXIST));
            return;
        }
        
        if (!file.isFile()) {
            console.printError("\""+file+"\" "+bundleCache.getString(Messages.NOT_A_FILE));
            return;
        }
        
        if (!file.getName().endsWith(".java")) {
            console.printError("\""+file+"\" "+bundleCache.getString(Messages.NOT_A_JAVA_FILE));
            return;
        }
        
        appMode = AppMode.GENERATE_SCANNER;
    }
    
    //==============
    // Help Methods
    //==============
    
    /**
     * 
     */
    public void printHelp() {
        TextFormatter formatter = new TextFormatter();
        int lineLength = 80;
        
        console.print(bundleCache.getString(Messages.USAGE)+": annoflex ["+
                bundleCache.getString(Messages.OPTIONS)+"] <"+
                bundleCache.getString(Messages.FILE)+">");
        
        console.print(bundleCache.getString(Messages.EXAMPLE)+": annoflex MyScanner.java");
        
        // file
        int filePrefix = 2;
        formatter.setLineLength(lineLength-filePrefix);
        
        console.print("");
        console.print(bundleCache.getString(Messages.FILE)+":");
        printText(StringToolkit.createString(' ',filePrefix),Messages.FILE_DESCRIPTION,
                formatter,filePrefix);
        
        // options
        int optionLength = 17;
        formatter.setLineLength(lineLength-optionLength);
        
        console.print("");
        console.print(bundleCache.getString(Messages.OPTIONS)+":");
        
        printText("  -b, --bom      ",Messages.BOM_DESCRIPTION,formatter,optionLength);
        printText("  -c, --charset  ",Messages.CHARSET_DESCRIPTION,formatter,optionLength);
        printText("  -h, --help     ",Messages.HELP_DESCRIPTION,formatter,optionLength);
        printText("  -l, --linesep  ",Messages.LINE_SEPARATOR_DESCRIPTION,formatter,optionLength);
        printText("  -v, --version  ",Messages.VERSION_DESCRIPTION,formatter,optionLength);
    }
    
    /**
     * 
     */
    private void printText(String prefix, Message message,
            TextFormatter formatter, int spaceLength) {
        
        String description = bundleCache.getString(message);
        formatter.setText(description);
        String[] lines = formatter.computeLines();
        
        console.print(prefix+lines[0]);
        
        if (lines.length > 1) {
            String space = StringToolkit.createString(' ',spaceLength);
            
            for (int i=1;i<lines.length;i++) {
                console.print(space+lines[i]);
            }
        }
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public AppMode getAppMode() {
        return appMode;
    }
    
    /**
     * 
     */
    public BOMMode getBOMMode() {
        return bomMode;
    }
    
    /**
     * 
     */
    public String getCharSet() {
        return charSet;
    }
    
    /**
     * 
     */
    public LineSeparatorMode getLineSeparatorMode() {
        return lineSeparatorMode;
    }
    
    /**
     * 
     */
    public File getFile() {
        return file;
    }
    
    //===================
    // Parameter Methods
    //===================
    
    /**
     * 
     */
    private String[] removeInvalidEntries(String[] args) {
        if ((args != null) && (args.length > 0)) {
            ArrayList<String> list = new ArrayList<>();
            
            for (int i=0;i<args.length;i++) {
                String curArg = args[i];
                
                if ((curArg != null) && !curArg.isEmpty()) {
                    list.add(curArg);
                }
            }
            
            return list.toArray(SystemToolkit.EMPTY_STRING_ARRAY);
        }
        
        return null;
    }
    
    /**
     * 
     */
    private boolean isFirstAndOnlyParam(String[] args, String curArg,
            int index) {
        
        return (index == 0) && (args.length == 1);
    }
    
    /**
     * 
     */
    private boolean isValidValue(String[] args, int index) {
        return index < args.length && !args[index].startsWith("-");
    }
    
    /**
     * 
     */
    private boolean isLastParam(String[] args, int index) {
        return index == args.length - 1;
    }
}
