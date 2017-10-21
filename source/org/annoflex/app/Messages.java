/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app;

import org.annoflex.util.Message;

/**
 * @author Stefan Czaska
 */
final class Messages {
    
    //======================
    // Application Messages
    //======================
    
    static final Message LOADING_FILE                 = new Message(Messages.class,"loadingFile");
    static final Message FILE_LOAD_ERROR              = new Message(Messages.class,"fileLoadError");
    static final Message COMPUTING_SCANNER            = new Message(Messages.class,"computingScanner");
    static final Message SAVING_FILE                  = new Message(Messages.class,"savingFile");
    static final Message FILE_SAVE_ERROR              = new Message(Messages.class,"fileSaveError");
    static final Message INFO                         = new Message(Messages.class,"info");
    static final Message ERROR                        = new Message(Messages.class,"error");
    static final Message ERRORS                       = new Message(Messages.class,"errors");
    static final Message WARNING                      = new Message(Messages.class,"warning");
    static final Message WARNINGS                     = new Message(Messages.class,"warnings");
    static final Message ON_ROW                       = new Message(Messages.class,"onRow");
    static final Message FIRST_AND_ONLY_PARAM         = new Message(Messages.class,"firstAndOnlyParam");
    static final Message BOM_MODE_REQUIRED            = new Message(Messages.class,"bomModeRequired");
    static final Message CHARSET_REQUIRED             = new Message(Messages.class,"charsetRequired");
    static final Message LINE_SEPARATOR_REQUIRED      = new Message(Messages.class,"lineSeparatorRequired");
    static final Message UNKNOWN_PARAM                = new Message(Messages.class,"unknownParam");
    static final Message INVALID_PARAM                = new Message(Messages.class,"invalidParam");
    static final Message MISSING_FILE_PARAM           = new Message(Messages.class,"missingFileParam");
    static final Message DOES_NOT_EXIST               = new Message(Messages.class,"doesNotExist");
    static final Message NOT_A_FILE                   = new Message(Messages.class,"notAFile");
    static final Message NOT_A_JAVA_FILE              = new Message(Messages.class,"notAJavaFile");
    static final Message INVALID_BOM_VALUE            = new Message(Messages.class,"invalidBOMValue");
    static final Message UNSUPPORTED_ENCODING         = new Message(Messages.class,"unsupportedEncoding");
    static final Message INVALID_LINE_SEPARATOR_VALUE = new Message(Messages.class,"invalidLineSeparatorValue");
    static final Message USAGE                        = new Message(Messages.class,"usage");
    static final Message EXAMPLE                      = new Message(Messages.class,"example");
    static final Message OPTIONS                      = new Message(Messages.class,"options");
    static final Message FILE                         = new Message(Messages.class,"file");
    static final Message FILE_DESCRIPTION             = new Message(Messages.class,"fileDescription");
    static final Message BOM_DESCRIPTION              = new Message(Messages.class,"bomDescription");
    static final Message CHARSET_DESCRIPTION          = new Message(Messages.class,"charsetDescription");
    static final Message HELP_DESCRIPTION             = new Message(Messages.class,"helpDescription");
    static final Message LINE_SEPARATOR_DESCRIPTION   = new Message(Messages.class,"lineSeparatorDescription");
    static final Message VERSION_DESCRIPTION          = new Message(Messages.class,"versionDescription");
}
