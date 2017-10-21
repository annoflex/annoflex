/**
 * This is the first part of the tutorial of AnnoFlex. It consist of two
 * exemplary option declarations, four lexical rules and an empty code
 * area. The purpose of this file is to show how the structure of a scanner
 * definition looks like.
 * 
 * Option declarations must always be located in the class comment of a
 * scanner class. They are initiated with an "option" tag followed by an
 * option name, followed by an assignment character and then followed by an
 * option-specific configuration text. There are two exemplary options after
 * this paragraph. The first option specifies the name of the scanner main
 * method and the second option specifies that a small table with statistics
 * about the code generation should be added to the generated code.
 * 
 * @option methodName = getNextToken
 * @option statistics = enabled
 */
public class TutorialPart1 {
    
    // The following commented method declarations are lexical rules. They
    // define the behavior of our example scanner. A lexical rule consist of two
    // parts, a regular expression and a token creation method. The regular
    // expression is specified with an "expr" tag followed by the expression
    // text. The token creation method is a simple Java method.
    
    /** @expr [0-9]+       */ String createNumber()     { return "number"; }
    /** @expr [a-zA-Z]+    */ String createIdentifier() { return "identifier"; }
    /** @expr [ \n\r\t\f]+ */ String createWhitespace() { return "whitespace"; }
    /** @expr [^]          */ String createMisc()       { return "misc"; }
    
    // The code of the scanner is generated into the code area. This area must
    // be defined via two markers, a start marker and an end marker. The start
    // marker is "//%%LEX-MAIN-START%%" and the end marker is
    // "//%%LEX-MAIN-END%%". In this part of the tutorial the code area is empty
    // in order to keep the file small and the example easy. In part two of the
    // tutorial it will contain code.
    
    //%%LEX-MAIN-START%%
    
    // This is the code area into which the code is generated.
    
    //%%LEX-MAIN-END%%
    
    // That's it. This is the structure of a scanner definition. Part two of the
    // tutorial extends this example by the generated code and a main method
    // which uses the generated scanner.
}
