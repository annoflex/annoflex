/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.app.dom;

import java.util.ArrayList;

/**
 * @author Stefan Czaska
 */
public class Options {
    
    //=======
    // Names
    //=======
    
    public static final String LOGO                 = "logo";
    public static final String STATISTICS           = "statistics";
    public static final String HEADINGS             = "headings";
    public static final String METHOD_NAME          = "methodName";
    public static final String METHOD_THROWS        = "methodThrows";
    public static final String DEFAULT_RETURN_VALUE = "defaultReturnValue";
    public static final String INPUT_MODE           = "inputMode";
    public static final String BUFFER_STRATEGY      = "bufferStrategy";
    public static final String BUFFER_INCREMENT     = "bufferIncrement";
    public static final String FUNCTIONALITY        = "functionality";
    public static final String JAVADOC              = "javadoc";
    public static final String VISIBILITY           = "visibility";
    public static final String INTERNAL             = "internal";
    public static final String NO_MATCH_ACION       = "noMatchAction";
    
    //================
    // Default Values
    //================
    
    public static final boolean         LOGO_DEFAULT_VALUE                 = true;
    public static final boolean         STATISTICS_DEFAULT_VALUE           = true;
    public static final HeadingType     HEADINGS_DEFAULT_VALUE             = HeadingType.ENABLED;
    public static final String          METHOD_NAME_DEFAULT_VALUE          = "getNextToken";
    public static final String          DEFAULT_RETURN_VALUE_DEFAULT_VALUE = "";
    public static final InputMode       INPUT_MODE_DEFAULT_VALUE           = InputMode.STRING;
    public static final BufferStrategy  BUFFER_STRATEGY_DEFAULT_VALUE      = BufferStrategy.CURRENT_MATCH;
    public static final BufferIncrement BUFFER_INCREMENT_DEFAULT_VALUE     = BufferIncrement.GOLDEN_RATIO;
    public static final NoMatchAction   NO_MATCH_ACTION_DEFAULT_VALUE      = NoMatchAction.ERROR;
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private boolean logo = LOGO_DEFAULT_VALUE;
    
    /**
     * 
     */
    private boolean statistics = STATISTICS_DEFAULT_VALUE;
    
    /**
     * 
     */
    private HeadingType headings = HEADINGS_DEFAULT_VALUE;
    
    /**
     * 
     */
    private String methodName = METHOD_NAME_DEFAULT_VALUE;
    
    /**
     * 
     */
    private final ArrayList<TypeDescriptor> methodThrows = new ArrayList<>();
    
    /**
     * 
     */
    private String defaultReturnValue = DEFAULT_RETURN_VALUE_DEFAULT_VALUE;
    
    /**
     * 
     */
    private InputMode inputMode = INPUT_MODE_DEFAULT_VALUE;
    
    /**
     * 
     */
    private BufferStrategy bufferStrategy = BUFFER_STRATEGY_DEFAULT_VALUE;
    
    /**
     * 
     */
    private BufferIncrement bufferIncrement = BUFFER_INCREMENT_DEFAULT_VALUE;
    
    /**
     * 
     */
    private final FunctionalityMap functionalityMap = new FunctionalityMap();
    
    /**
     * 
     */
    private final BooleanMap javaDocMap = new BooleanMap(true);
    
    /**
     * 
     */
    private final VisibilityMap visibilityMap = new VisibilityMap();
    
    /**
     * 
     */
    private final BooleanMap internalMap = new BooleanMap(false);
    
    /**
     * 
     */
    private NoMatchAction noMatchAction = NO_MATCH_ACTION_DEFAULT_VALUE;
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public void setLogo(boolean logo) {
        this.logo = logo;
    }
    
    /**
     * 
     */
    public boolean getLogo() {
        return logo;
    }
    
    /**
     * 
     */
    public void setStatistics(boolean statistics) {
        this.statistics = statistics;
    }
    
    /**
     * 
     */
    public boolean getStatistics() {
        return statistics;
    }
    
    /**
     * 
     */
    public void setHeadings(HeadingType headings) {
        if (headings != null) {
            this.headings = headings;
        }
    }
    
    /**
     * 
     */
    public HeadingType getHeadings() {
        return headings;
    }
    
    /**
     * 
     */
    public void setMethodName(String methodName) {
        if (methodName != null) {
            this.methodName = methodName;
        }
    }
    
    /**
     * 
     */
    public String getMethodName() {
        return methodName;
    }
    
    /**
     * 
     */
    public ArrayList<TypeDescriptor> getMethodThrows() {
        return methodThrows;
    }
    
    /**
     * 
     */
    public void setDefaultReturnValue(String defaultReturnValue) {
        if (defaultReturnValue != null) {
            this.defaultReturnValue = defaultReturnValue;
        }
    }
    
    /**
     * 
     */
    public String getDefaultReturnValue() {
        return defaultReturnValue;
    }
    
    /**
     * 
     */
    public void setInputMode(InputMode inputMode) {
        if (inputMode != null) {
            this.inputMode = inputMode;
        }
    }
    
    /**
     * 
     */
    public InputMode getInputMode() {
        return inputMode;
    }
    
    /**
     * 
     */
    public void setBufferStrategy(BufferStrategy bufferStrategy) {
        if (bufferStrategy != null) {
            this.bufferStrategy = bufferStrategy;
        }
    }
    
    /**
     * 
     */
    public BufferStrategy getBufferStrategy() {
        return bufferStrategy;
    }
    
    /**
     * 
     */
    public void setBufferIncrement(BufferIncrement bufferIncrement) {
        if (bufferIncrement != null) {
            this.bufferIncrement = bufferIncrement;
        }
    }
    
    /**
     * 
     */
    public BufferIncrement getBufferIncrement() {
        return bufferIncrement;
    }
    
    /**
     * 
     */
    public FunctionalityMap getFunctionalityMap() {
        return functionalityMap;
    }
    
    /**
     * 
     */
    public BooleanMap getJavaDocMap() {
        return javaDocMap;
    }
    
    /**
     * 
     */
    public VisibilityMap getVisibilityMap() {
        return visibilityMap;
    }
    
    /**
     * 
     */
    public BooleanMap getInternalMap() {
        return internalMap;
    }
    
    /**
     * 
     */
    public void setNoMatchAction(NoMatchAction noMatchAction) {
        if (noMatchAction != null) {
            this.noMatchAction = noMatchAction;
        }
    }
    
    /**
     * 
     */
    public NoMatchAction getNoMatchAction() {
        return noMatchAction;
    }
}
