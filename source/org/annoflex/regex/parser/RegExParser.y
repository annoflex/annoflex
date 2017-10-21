// yacc -v -Jnorun -Jnodebug -Jfinal -Jsemantic=RegExParserVal -Jclass=RegExParser RegExParser.y

%{
/*
 * AnnoFlex - A code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.parser;

import org.annoflex.regex.LookafterType;
import org.annoflex.regex.LookbeforeType;
import org.annoflex.regex.Modifier;
import org.annoflex.regex.Quantifier;
import org.annoflex.regex.QuantifierException;
import org.annoflex.regex.dom.CharClassOperator;
import org.annoflex.regex.dom.CharRef;
import org.annoflex.regex.dom.ROMAlternationExpr;
import org.annoflex.regex.dom.ROMCCOperator;
import org.annoflex.regex.dom.ROMCCRange;
import org.annoflex.regex.dom.ROMCCSequence;
import org.annoflex.regex.dom.ROMCharExpr;
import org.annoflex.regex.dom.ROMCharRef;
import org.annoflex.regex.dom.ROMCharacterClass;
import org.annoflex.regex.dom.ROMClassExpr;
import org.annoflex.regex.dom.ROMClassRef;
import org.annoflex.regex.dom.ROMCompilationUnit;
import org.annoflex.regex.dom.ROMConcatenationExpr;
import org.annoflex.regex.dom.ROMCondition;
import org.annoflex.regex.dom.ROMLookafter;
import org.annoflex.regex.dom.ROMLookaroundExpr;
import org.annoflex.regex.dom.ROMLookbefore;
import org.annoflex.regex.dom.ROMMacro;
import org.annoflex.regex.dom.ROMMacroExpr;
import org.annoflex.regex.dom.ROMModifier;
import org.annoflex.regex.dom.ROMModifierExpr;
import org.annoflex.regex.dom.ROMName;
import org.annoflex.regex.dom.ROMNameList;
import org.annoflex.regex.dom.ROMQuantifier;
import org.annoflex.regex.dom.ROMQuantifierExpr;
import org.annoflex.regex.dom.ROMRootAlternation;
import org.annoflex.regex.dom.ROMRootElement;
import org.annoflex.regex.dom.ROMSequenceExpr;
import org.annoflex.regex.dom.ROMSequenceRef;
import org.annoflex.regex.dom.ROMStringSequence;
import org.annoflex.regex.dom.SequenceRef;
import org.annoflex.regex.unicode.Property;
import org.annoflex.regex.unicode.PropertySelector;
%}

/* single characters */

%token LSB    1  /* [ */
%token RSB    2  /* ] */
%token HAT    3  /* ^ */
%token LCB    4  /* { */
%token RCB    5  /* } */
%token OR     6  /* | */
%token LRB    7  /* ( */
%token RRB    8  /* ) */
%token LT     9  /* < */
%token GT     10 /* > */
%token QUOTE  11 /* " */
%token DOT    12 /* . */
%token QMARK  13 /* ? */
%token STAR   14 /* * */
%token PLUS   15 /* + */
%token SLASH  16 /* / */
%token TILDE  17 /* ~ */
%token EMARK  18 /* ! */
%token DOLLAR 19 /* $ */
%token COMMA  20 /* , */
%token MINUS  21 /* - */

/* whitespace and miscellaneous characters */

%token WS 22 /* \p{whitespace} */
%token CH 23 /* [^] */

/* escape sequences */

%token ESCAPED_CHAR            24 /* \... */
%token ESCAPED_CHAR_CLASS      25 /* \... */
%token ESCAPED_CHAR_SEQUENCE   26 /* \... */

/* special character class tokens */

%token OR_OR            27 /* || */
%token AND_AND          28 /* && */
%token MINUS_MINUS      29 /* -- */
%token TILDE_TILDE      30 /* ~~ */
%token NAMED_CHAR_CLASS 31 /* [:name:] */

/* angle and curly bracket context tokens */

%token NAME   32 /* [a-zA-Z][a-zA-Z0-9_]* */
%token NUMBER 33 /* [0-9]+ */

%%

CompilationUnit:
    RootExpr {
        $$ = new RegExParserVal(new ROMCompilationUnit($1.n));
    }
    
    | Condition RootExpr {
        $$ = new RegExParserVal(new ROMCompilationUnit($1.n,$2.n));
    }
    
    | {
        $$ = new RegExParserVal(new ROMCompilationUnit());
    };

RootExpr:
    SimpleAlternation {
        $$ = new RegExParserVal(new ROMRootAlternation(new ROMRootElement($1.n)));
    }
    
    | LookaroundExpr {
        $$ = new RegExParserVal(new ROMRootAlternation(new ROMRootElement($1.n)));
    }
    
    | RootAlternation
    
    | SimpleAlternation OR RootAlternation {
        $3.n.appendChild(new ROMRootElement($1.n),true);
        $$ = $3;
    }
    
    | RootAlternation OR SimpleAlternation {
        $1.n.appendChild(new ROMRootElement($3.n));
        $$ = $1;
    }
    
    | SimpleAlternation OR RootAlternation OR SimpleAlternation {
        $3.n.appendChild(new ROMRootElement($1.n),true);
        $3.n.appendChild(new ROMRootElement($5.n));
        $$ = $3;
    };

RootAlternation:
    RootAlternationElement
    
    | RootAlternation OR RootAlternationElement {
        $1.n.appendChildren($3.n);
        $$ = $1;
    }
    
    | RootAlternation OR SimpleAlternation OR RootAlternationElement {
        $1.n.appendChild(new ROMRootElement($3.n));
        $1.n.appendChildren($5.n);
        $$ = $1;
    };

RootAlternationElement:
    LRB LookaroundExpr RRB {
        $$ = new RegExParserVal(new ROMRootAlternation(new ROMRootElement($2.n)));
    }
    
    | LRB Condition LookaroundExpr RRB {
        $$ = new RegExParserVal(new ROMRootAlternation(new ROMRootElement($2.n,$3.n)));
    }
    
    | LRB Condition SimpleAlternation RRB {
        $$ = new RegExParserVal(new ROMRootAlternation(new ROMRootElement($2.n,$3.n)));
    };

//=======================
// Lookaround Expression
//=======================

LookaroundExpr:
    SimpleAlternation Lookafter {
        $2.n.appendChild($1.n,true);
        $$ = $2;
    }
    
    | Lookbefore SimpleAlternation {
        $1.n.appendChild($2.n);
        $$ = $1;
    }
    
    | Lookbefore SimpleAlternation Lookafter {
        $1.n.appendChild($2.n);
        $1.n.appendChildren($3.n);
        $$ = $1;
    };

Lookbefore:
    HAT {
        $$ = new RegExParserVal(new ROMLookaroundExpr(new ROMLookbefore(LookbeforeType.START_OF_LINE)));
    };

Lookafter:
    DOLLAR {
        $$ = new RegExParserVal(new ROMLookaroundExpr(new ROMLookafter(LookafterType.END_OF_LINE)));
    }
    
    | SLASH SimpleAlternation {
        $$ = new RegExParserVal(new ROMLookaroundExpr(new ROMLookafter(LookafterType.EXPRESSION),$2.n));
    };

//===================
// Simple Expression
//===================

SimpleAlternation:
    Concatenation
    
    | SimpleAlternation OR Concatenation {
        if ($1.n.isAlternationExpr() && $3.n.isAlternationExpr()) {
            $1.n.appendChildren($3.n);
            $$ = $1;
        }
        
        else if ($1.n.isAlternationExpr()) {
            $1.n.appendChild($3.n);
            $$ = $1;
        }
        
        else if ($3.n.isAlternationExpr()) {
            $3.n.appendChild($1.n,true);
            $$ = $3;
        }
        
        else {
            $$ = new RegExParserVal(new ROMAlternationExpr($1.n,$3.n));
        }
    };

Concatenation:
    QuantifierExpr
    
    | QuantifierExpr Concatenation {
        if ($1.n.isConcatenationExpr() && $2.n.isConcatenationExpr()) {
           $1.n.appendChildren($2.n);
           $$ = $1;
        }
        
        else if ($1.n.isConcatenationExpr()) {
           $1.n.appendChild($2.n);
           $$ = $1;
        }
        
        else if ($2.n.isConcatenationExpr()) {
           $2.n.appendChild($1.n,true);
           $$ = $2;
        }
        
        else {
           $$ = new RegExParserVal(new ROMConcatenationExpr($1.n,$2.n));
        }
    };

QuantifierExpr:
    ModifierExpr
    
    | QuantifierExpr Quantifier {
        $$ = new RegExParserVal(new ROMQuantifierExpr($1.n,$2.n));
    };

ModifierExpr:
    SimpleExpr
    
    | Modifier ModifierExpr {
        $$ = new RegExParserVal(new ROMModifierExpr($1.n,$2.n));
    };

SimpleExpr:
    SimpleExprChar {
        $$ = new RegExParserVal(new ROMCharExpr(new ROMCharRef(new CharRef((Character)$1.t.value()))));
    }
    
    | ESCAPED_CHAR {
        $$ = new RegExParserVal(new ROMCharExpr(new ROMCharRef((CharRef)$1.t.value())));
    }
    
    | CharacterClass {
        $$ = new RegExParserVal(new ROMClassExpr($1.n));
    }
    
    | DOT {
        $$ = new RegExParserVal(new ROMClassExpr(new ROMClassRef(PropertySelector.forBinary(Property.VWHITE_SPACE,true))));
    }
    
    | ESCAPED_CHAR_CLASS {
        $$ = new RegExParserVal(new ROMClassExpr(new ROMClassRef((PropertySelector)$1.t.value())));
    }
    
    | QUOTE StringSequence QUOTE {
        $$ = new RegExParserVal(new ROMSequenceExpr($2.n));
    }
    
    | ESCAPED_CHAR_SEQUENCE {
        $$ = new RegExParserVal(new ROMSequenceExpr(new ROMSequenceRef((SequenceRef)$1.t.value())));
    }
    
    | Macro {
        $$ = new RegExParserVal(new ROMMacroExpr($1.n));
    }
    
    | LRB SimpleAlternation RRB {
        $$ = $2;
    };

SimpleExprChar:
    COMMA
    | MINUS
    | CH;

//=================
// Character Class
//=================

CharacterClass:
    LSB RSB {
        $$ = new RegExParserVal(new ROMCharacterClass(false));
    }
    
    | LSB HAT RSB {
        $$ = new RegExParserVal(new ROMCharacterClass(true));
    }
    
    | LSB CCSequenceList RSB {
        $$ = $2;
    }
    
    | LSB HAT CCSequenceList RSB {
        ((ROMCharacterClass)$3.n).setInvert(true);
        $$ = $3;
    };

CCSequenceList:
    CCSequence {
        $$ = new RegExParserVal(new ROMCharacterClass($1.n));
    }
    
    | CCSequenceList CCOperator CCSequence {
        $1.n.appendChild($2.n);
        $1.n.appendChild($3.n);
        $$ = $1;
    };

CCOperator:
    OR_OR {
        $$ = new RegExParserVal(new ROMCCOperator(CharClassOperator.UNION));
    }
    
    | AND_AND {
        $$ = new RegExParserVal(new ROMCCOperator(CharClassOperator.INTERSECTION));
    }
    
    | MINUS_MINUS {
        $$ = new RegExParserVal(new ROMCCOperator(CharClassOperator.SET_DIFFERENCE));
    }
    
    | TILDE_TILDE {
        $$ = new RegExParserVal(new ROMCCOperator(CharClassOperator.SYMMETRIC_DIFFERENCE));
    };

CCSequence:
    MINUS {
        $$ = new RegExParserVal(new ROMCCSequence(new ROMCharRef(new CharRef((Character)$1.t.value()))));
    }
    
    | MINUS CCSequenceEnd {
        $2.n.appendChild(new ROMCharRef(new CharRef((Character)$1.t.value())),true);
        $$ = $2;
    }
    
    | CCSequenceEnd;

CCSequenceEnd:
    CCCharacter {
        $$ = new RegExParserVal(new ROMCCSequence($1.n));
    }
    
    | CCCharacter MINUS {
        $$ = new RegExParserVal(new ROMCCSequence($1.n,
                new ROMCharRef(new CharRef((Character)$2.t.value()))));
    }
    
    | CCCharacter CCSequenceEnd {
        $2.n.appendChild($1.n,true);
        $$ = $2;
    }
    
    | CCNoSimpleCharSequenceElement {
        $$ = new RegExParserVal(new ROMCCSequence($1.n));
    }
    
    | CCNoSimpleCharSequenceElement MINUS {
        $$ = new RegExParserVal(new ROMCCSequence($1.n,
                new ROMCharRef(new CharRef((Character)$2.t.value()))));
    }
    
    | CCNoSimpleCharSequenceElement CCSequenceEnd {
        $2.n.appendChild($1.n,true);
        $$ = $2;
    };

CCNoSimpleCharSequenceElement:
    CCCharacter MINUS CCCharacter {
        $$ = new RegExParserVal(new ROMCCRange($1.n,$3.n));
    }
    
    | ESCAPED_CHAR_CLASS {
        $$ = new RegExParserVal(new ROMClassRef((PropertySelector)$1.t.value()));
    }
    
    | NAMED_CHAR_CLASS {
        $$ = new RegExParserVal(new ROMClassRef((PropertySelector)$1.t.value()));
    }
    
    | ESCAPED_CHAR_SEQUENCE {
        $$ = new RegExParserVal(new ROMSequenceRef((SequenceRef)$1.t.value()));
    }
    
    | Macro
    | CharacterClass;

CCCharacter:
    CCCharacterChar {
        $$ = new RegExParserVal(new ROMCharRef(new CharRef((Character)$1.t.value())));
    }
    
    | ESCAPED_CHAR {
        $$ = new RegExParserVal(new ROMCharRef((CharRef)$1.t.value()));
    };

CCCharacterChar:
    OR
    | LRB
    | RRB
    | LT
    | GT
    | QUOTE
    | DOT
    | QMARK
    | STAR
    | PLUS
    | SLASH
    | TILDE
    | EMARK
    | DOLLAR
    | COMMA
    | WS
    | CH;

//=================
// String Sequence
//=================

StringSequence:
    StringElement {
        $$ = new RegExParserVal(new ROMStringSequence($1.n));
    }
    
    | StringSequence StringElement {
        $1.n.appendChild($2.n);
        $$ = $1;
    };

StringElement:
    StringElementChar {
        $$ = new RegExParserVal(new ROMCharRef(new CharRef((Character)$1.t.value())));
    }
    
    | ESCAPED_CHAR {
        $$ = new RegExParserVal(new ROMCharRef((CharRef)$1.t.value()));
    }
    
    | ESCAPED_CHAR_CLASS {
        $$ = new RegExParserVal(new ROMClassRef((PropertySelector)$1.t.value()));
    }
    
    | ESCAPED_CHAR_SEQUENCE {
        $$ = new RegExParserVal(new ROMSequenceRef((SequenceRef)$1.t.value()));
    };

StringElementChar:
    LSB
    | RSB
    | HAT
    | LCB
    | RCB
    | OR
    | LRB
    | RRB
    | LT
    | GT
    | DOT
    | QMARK
    | STAR
    | PLUS
    | SLASH
    | TILDE
    | EMARK
    | DOLLAR
    | COMMA
    | MINUS
    | WS
    | CH;

//===================
// Simple Leaf Nodes
//===================

Condition:
    LT STAR GT {
        $$ = new RegExParserVal(new ROMCondition());
    }
    
    | LT NameList GT {
        $$ = new RegExParserVal(new ROMCondition($2.n));
    }
    
    | LRB Condition RRB {
        $$ = $2;
    };

NameList:
    Name {
        $$ = new RegExParserVal(new ROMNameList($1.n));
    }
    
    | NameList COMMA Name {
        $1.n.appendChild($3.n);
        $$ = $1;
    };

Macro:
    LCB Name RCB {
        $$ = new RegExParserVal(new ROMMacro($2.n));
    };

Name:
    NAME {
        $$ = new RegExParserVal(new ROMName((String)$1.t.value()));
    };

Modifier:
    EMARK {
        $$ = new RegExParserVal(new ROMModifier(Modifier.NOT));
    }
    
    | TILDE {
        $$ = new RegExParserVal(new ROMModifier(Modifier.UNTIL));
    };

Quantifier:
    STAR {
        $$ = new RegExParserVal(new ROMQuantifier(Quantifier.ZERO_OR_MORE));
    }
    
    | PLUS {
        $$ = new RegExParserVal(new ROMQuantifier(Quantifier.ONE_OR_MORE));
    }
    
    | QMARK {
        $$ = new RegExParserVal(new ROMQuantifier(Quantifier.ZERO_OR_ONE));
    }
    
    | LCB NUMBER RCB {
        try {
            $$ = new RegExParserVal(new ROMQuantifier(Quantifier.createExactly((Integer)$2.t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    }
    
    | LCB NUMBER COMMA RCB {
        try {
            $$ = new RegExParserVal(new ROMQuantifier(Quantifier.createAtLeast((Integer)$2.t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    }
    
    | LCB NUMBER COMMA NUMBER RCB {
        try {
            $$ = new RegExParserVal(new ROMQuantifier(Quantifier.create((Integer)$2.t.value(),(Integer)$4.t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    }
    
    | LCB COMMA NUMBER RCB {
        try {
            $$ = new RegExParserVal(new ROMQuantifier(Quantifier.createUpTo((Integer)$3.t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    };

%%
private final RegExScanner scanner = new RegExScanner();

public ROMCompilationUnit parse(String string) throws RegExParseException {
    scanner.setString(string);
    
    yydebug = false;
    yyparse();
    
    return (ROMCompilationUnit)yyval.n;
}

void yyerror(String s) {
    throw new RegExParseException(s);
}

int yylex() {
    try {
        RegExToken token = scanner.getNextToken();
        
        while ((token != null) && (token.type() == RegExTokenType.WS) &&
               scanner.isIgnoreWhitespace()) {
            
            token = scanner.getNextToken();
        }
        
        if (token != null) {
            yylval = new RegExParserVal(token);
            
            return token.type().ordinal() + 1;
        }
    }
    
    catch(RegExScanException e) {
        throw new RegExParseException(e.getMessage());
    }
    
    yylval = null;
    
    return 0;
}
