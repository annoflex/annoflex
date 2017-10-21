/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import java.io.File;
import java.io.IOException;

import org.annoflex.util.BuildInfo;
import org.annoflex.util.BundleCache;
import org.annoflex.util.text.TextFile;

/**
 * @author Stefan Czaska
 */
public class AnnoFlex {
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    public static final int EXIT_STATUS_SUCCESS = 0;
    
    /**
     * 
     */
    public static final int EXIT_STATUS_ERROR = 1;
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private BundleCache bundleCache;
    
    /**
     * 
     */
    private Console console;
    
    //=============
    // Run Methods
    //=============
    
    /**
     * 
     */
    public void run(String[] args) {
        try {
            bundleCache = new BundleCache();
            console = new Console(bundleCache);
            
            Arguments arguments = new Arguments(bundleCache,console,args);
            
            switch(arguments.getAppMode()) {
            case NO_ACTION:
                break;
            
            case PRINT_HELP:
                arguments.printHelp();
                break;
            
            case PRINT_VERSION:
                printVersion();
                break;
            
            case GENERATE_SCANNER:
                generateScanner(arguments);
                break;
            }
            
            System.exit(determineExitCode());
        }
        
        catch(Throwable t) {
            bundleCache = null;
            console = null;
            
            t.printStackTrace();
            
            System.exit(EXIT_STATUS_ERROR);
        }
    }
    
    /**
     * 
     */
    private int determineExitCode() {
        if (console.getErrorCount() != 0) {
            return EXIT_STATUS_ERROR;
        }
        
        return EXIT_STATUS_SUCCESS;
    }
    
    //=================
    // Version Methods
    //=================
    
    /**
     * 
     */
    private void printVersion() {
        BuildInfo buildInfo = new BuildInfo(AnnoFlex.class);
        
        console.print("AnnoFlex Version: "+getAnnoFlexVersion(buildInfo));
        console.print("AnnoFlex Build Id: "+getAnnoFlexBuildId(buildInfo));
        console.print("Java Version: "+getJavaVersion());
        console.print("OS Version: "+getOSVersion());
    }
    
    /**
     * 
     */
    private String getAnnoFlexVersion(BuildInfo buildInfo) {
        return buildInfo.major()+"."+buildInfo.minor();
    }
    
    /**
     * 
     */
    private String getAnnoFlexBuildId(BuildInfo buildInfo) {
        return buildInfo.build()+"-"+buildInfo.year()+buildInfo.month()+
                buildInfo.day()+"-"+buildInfo.revision();
    }
    
    /**
     * 
     */
    private String getJavaVersion() {
        return System.getProperty("java.version");
    }
    
    /**
     * 
     */
    private String getOSVersion() {
        return System.getProperty("os.name")+" ("+System.getProperty("os.arch")+")";
    }
    
    //====================
    // Generation Methods
    //====================
    
    /**
     * 
     */
    private void generateScanner(Arguments arguments) {
        File file = arguments.getFile();
        
        console.printInfo(bundleCache.getString(Messages.LOADING_FILE)+" \""+file+"\"");
        
        String charSet = arguments.getCharSet();
        TextFile textFile = null;
        
        try {
            textFile = new TextFile(file,charSet);
        }
        
        catch(IOException e) {
            console.printError(bundleCache.getString(Messages.FILE_LOAD_ERROR)+
                    " ("+e.getLocalizedMessage()+")");
        }
        
        if (textFile != null) {
            console.printInfo(bundleCache.getString(Messages.COMPUTING_SCANNER));
            
            TextInfo textInfo = new TextInfo(textFile.getContent(),
                    arguments.getLineSeparatorMode());
            
            Updater updater = new Updater();
            updater.setErrorHandler(new UpdaterErrorHandler(textInfo,bundleCache,console));
            
            String newContent = updater.update(textInfo);
            
            if (!updater.hasErrors()) {
                console.printInfo(bundleCache.getString(Messages.SAVING_FILE)+" \""+file+"\"");
                
                textFile.setContent(newContent);
                
                // set the user specified charset as the charset on load can be
                // different than the user specified one (due to the presence of
                // a BOM)
                textFile.setCharSet(charSet);
                
                // Overwrite the BOM property only if explicitly specified by the
                // user. Otherwise use a BOM only if the file has one on loading.
                switch(arguments.getBOMMode()) {
                case ON: textFile.setHasBOM(true); break;
                case OFF: textFile.setHasBOM(false); break;
                case AUTO: break;
                default:
                    throw new IllegalStateException("invalid BOM mode");
                }
                
                textFile.setLineSeparator(textInfo.getLineSeparator());
                
                try {
                    textFile.save(file);
                }
                
                catch(IOException e) {
                    console.printError(bundleCache.getString(Messages.FILE_SAVE_ERROR)
                            +" ("+e.getLocalizedMessage()+")");
                }
            }
        }
        
        console.printSummary();
    }
    
    //================
    // Static Methods
    //================
    
    /**
     * 
     */
    public static void main(String[] args) {
        new AnnoFlex().run(args);
    }
}
