/**
 * This is the third part of the tutorial of AnnoFlex. It shows how the
 * generated scanner code can be customized. In order to keep this example
 * simply we remove only some unused fields and methods. For further details
 * about customizing the scanner code have a look at the manual. 
 * 
 * The removal of class members can be handled with the "functionality"
 * option. It can be used to add and remove members to and from the code
 * generation. In the following example we first remove all methods and fields
 * and then we add everything which we want to have. Methods and fields which
 * are strictly necessary or unnecessary in conjunction with the specified
 * functionality are automatically added or removed by AnnoFlex. So it is save
 * just to specify what we want, AnnoFlex does the rest.
 * 
 * @option functionality = all- stringMethods+ getMatchText+
 * 
 * @option methodName = getNextToken
 * @option statistics = enabled
 */
public class TutorialPart3 {
    
    // lexical rules
    
    /** @expr [0-9]+       */ String createNumber()     { return "number"; }
    /** @expr [a-zA-Z]+    */ String createIdentifier() { return "identifier"; }
    /** @expr [ \n\r\t\f]+ */ String createWhitespace() { return "whitespace"; }
    /** @expr [^]          */ String createMisc()       { return "misc"; }
    
    // the code area, now with a reduced set of fields and methods
    
    //%%LEX-MAIN-START%%
    
    //================================================
    //     _                      _____ _             
    //    / \   _ __  _ __   ___ |  ___| | ___ _  __  
    //   / _ \ |  _ \|  _ \ / _ \| |_  | |/ _ \ \/ /  
    //  / ___ \| | | | | | | (_) |  _| | |  __/>  <   
    // /_/   \_\_| |_|_| |_|\___/|_|   |_|\___/_/\_\  
    //                                                
    //================================================
    
    /*************************************************
     *             Generation Statistics             *
     * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                               *
     * Rules:           4                            *
     * Lookaheads:      0                            *
     * Alphabet length: 4                            *
     * NFA states:      15                           *
     * DFA states:      5                            *
     * Static size:     308 Bytes                    *
     * Instance size:   20 Bytes                     *
     *                                               *
     ************************************************/
    
    //=================
    // Table Constants
    //=================
    
    /**
     * Maps Unicode characters to DFA input symbols.
     */
    private static final byte[] CHARACTER_MAP = createCharacterMap(
    "\0\t\3\2\0\1\3\2\0\22\3\1\0\17\1\n\0\7\2\32\0\6\2\32");
    
    /**
     * The transition table of the DFA.
     */
    private static final byte[][] TRANSITION_TABLE = createTransitionTable(
    "\5\1\2\1\3\1\4\1\0\1\2\1\0\2\0\2\3\1\0\1\0\3\4\1\0\4");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\1\3\1\4\1");
    
    //===============
    // String Fields
    //===============
    
    /**
     * The current string to be scanned.
     */
    private String string = "";
    
    //===============
    // Region Fields
    //===============
    
    /**
     * The end of the scan region.
     */
    private int regionEnd;
    
    //============
    // Dot Fields
    //============
    
    /**
     * The start position of the next scan.
     */
    private int dot;
    
    //==============
    // Match Fields
    //==============
    
    /**
     * The start of the last match.
     */
    private int matchStart;
    
    /**
     * The end of the last match.
     */
    private int matchEnd;
    
    //===============
    // Table Methods
    //===============
    
    /**
     * Creates the character map of the scanner.
     * 
     * @param characterMapData The compressed data of the character map.
     * @return The character map of the scanner.
     */
    private static byte[] createCharacterMap(String characterMapData) {
        byte[] characterMap = new byte[123];
        int length = characterMapData.length();
        int i = 0;
        int j = 0;
        
        while (i < length) {
            byte curValue = (byte)characterMapData.charAt(i++);
            
            for (int x=characterMapData.charAt(i++);x>0;x--) {
                characterMap[j++] = curValue;
            }
        }
        
        return characterMap;
    }
    
    /**
     * Creates the transition table of the scanner.
     * 
     * @param transitionTableData The compressed data of the transition table.
     * @return The transition table of the scanner.
     */
    private static byte[][] createTransitionTable(String transitionTableData) {
        byte[][] transitionTable = new byte[5][4];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 4) {
                k = 0;
                j++;
            }
        }
        
        return transitionTable;
    }
    
    /**
     * Creates the action map of the scanner.
     * 
     * @param actionMapData The compressed data of the action map.
     * @return The action map of the scanner.
     */
    private static byte[] createActionMap(String actionMapData) {
        byte[] actionMap = new byte[5];
        int length = actionMapData.length();
        int i = 0;
        int j = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)actionMapData.charAt(i++) - 1);
            
            for (int x=actionMapData.charAt(i++);x>0;x--) {
                actionMap[j++] = curValue;
            }
        }
        
        return actionMap;
    }
    
    //================
    // String Methods
    //================
    
    /**
     * Sets the string to be scanned. The scan region is set to the entire
     * string.
     * 
     * @param string The new string to be scanned.
     */
    public void setString(String string) {
        this.string = string != null ? string : "";
        
        regionEnd = this.string.length();
        
        dot = 0;
        
        matchStart = 0;
        matchEnd = 0;
    }
    
    /**
     * Returns the current string to be scanned.
     * 
     * @return The current string to be scanned.
     */
    public String getString() {
        return string;
    }
    
    //===============
    // Match Methods
    //===============
    
    /**
     * Returns the text of the last match.
     * 
     * @return The text of the last match.
     */
    public String getMatchText() {
        return string.substring(matchStart,matchEnd);
    }
    
    //==============
    // Scan Methods
    //==============
    
    /**
     * Performs at the current position the next step of the lexical analysis
     * and returns the result.
     * 
     * @return The result of the next step of the lexical analysis.
     * @throws IllegalStateException If a lexical error occurs
     */
    public String getNextToken() {
        if (dot < regionEnd) {
            
            // find longest match
            int curState = 0;
            int iterator = dot;
            int matchState = -1;
            int matchPosition = 0;
            
            do {
                char curChar = string.charAt(iterator);
                
                curState = TRANSITION_TABLE[curState][curChar >= 123 ?
                        0 : CHARACTER_MAP[curChar]];
                
                if (curState == -1) {
                    break;
                }
                
                if (ACTION_MAP[curState] != -1) {
                    matchState = curState;
                    matchPosition = iterator;
                }
            } while (++iterator < regionEnd);
            
            // match found, perform action
            if (matchState != -1) {
                int endPosition = matchPosition + 1;
                
                matchStart = dot;
                matchEnd = endPosition;
                dot = endPosition;
                
                switch(ACTION_MAP[matchState]) {
                case 0: return createNumber();
                case 1: return createIdentifier();
                case 2: return createWhitespace();
                case 3: return createMisc();
                }
            }
            
            // no match found, set match values and report as error
            matchStart = dot;
            matchEnd = dot;
            
            throw new IllegalStateException("invalid input");
        }
        
        // no match found, set match values and return to caller
        matchStart = dot;
        matchEnd = dot;
        
        return null;
    }
    
    //%%LEX-MAIN-END%%
    
    /**
     * This method shows how the scanner can be used. It creates a new scanner
     * and uses it to determine the tokens of a test string.
     */
    public static void main(String[] args) {
        TutorialPart3 scanner = new TutorialPart3();
        
        scanner.setString("Test 123 +-*/");
        System.out.println("scanning \""+scanner.getString()+"\"");
        
        String curToken = scanner.getNextToken();
        
        while (curToken != null) {
            System.out.println(curToken+": \""+scanner.getMatchText()+"\"");
            curToken = scanner.getNextToken();
        }
    }
    
    // That is the end of the tutorial. Further details about the definition of
    // lexical scanners with AnnoFlex can be found in the manual. It describes
    // all available features and provides for each topic explanatory code
    // examples.
}
