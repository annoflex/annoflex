//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 4 "RegExParser.y"
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
//#line 63 "RegExParser.java"




public class RegExParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:RegExParserVal
String   yytext;//user variable to return contextual strings
RegExParserVal yyval; //used to return semantic vals from action routines
RegExParserVal yylval;//the 'lval' (result) I got from yylex()
RegExParserVal valstk[] = new RegExParserVal[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new RegExParserVal();
  yylval=new RegExParserVal();
  valptr=-1;
}
final void val_push(RegExParserVal val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    RegExParserVal[] newstack = new RegExParserVal[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final RegExParserVal val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final RegExParserVal val_peek(int relative)
{
  return valstk[valptr-relative];
}
final RegExParserVal dup_yyval(RegExParserVal val)
{
  return val;
}
//#### end semantic value section ####
public final static short LSB=1;
public final static short RSB=2;
public final static short HAT=3;
public final static short LCB=4;
public final static short RCB=5;
public final static short OR=6;
public final static short LRB=7;
public final static short RRB=8;
public final static short LT=9;
public final static short GT=10;
public final static short QUOTE=11;
public final static short DOT=12;
public final static short QMARK=13;
public final static short STAR=14;
public final static short PLUS=15;
public final static short SLASH=16;
public final static short TILDE=17;
public final static short EMARK=18;
public final static short DOLLAR=19;
public final static short COMMA=20;
public final static short MINUS=21;
public final static short WS=22;
public final static short CH=23;
public final static short ESCAPED_CHAR=24;
public final static short ESCAPED_CHAR_CLASS=25;
public final static short ESCAPED_CHAR_SEQUENCE=26;
public final static short OR_OR=27;
public final static short AND_AND=28;
public final static short MINUS_MINUS=29;
public final static short TILDE_TILDE=30;
public final static short NAMED_CHAR_CLASS=31;
public final static short NAME=32;
public final static short NUMBER=33;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    1,    1,    1,    1,    1,    1,    5,
    5,    5,    6,    6,    6,    4,    4,    4,    8,    7,
    7,    3,    3,    9,    9,   10,   10,   11,   11,   13,
   13,   13,   13,   13,   13,   13,   13,   13,   15,   15,
   15,   16,   16,   16,   16,   19,   19,   21,   21,   21,
   21,   20,   20,   20,   22,   22,   22,   22,   22,   22,
   24,   24,   24,   24,   24,   24,   23,   23,   25,   25,
   25,   25,   25,   25,   25,   25,   25,   25,   25,   25,
   25,   25,   25,   25,   25,   17,   17,   26,   26,   26,
   26,   27,   27,   27,   27,   27,   27,   27,   27,   27,
   27,   27,   27,   27,   27,   27,   27,   27,   27,   27,
   27,   27,   27,    2,    2,    2,   28,   28,   18,   29,
   14,   14,   12,   12,   12,   12,   12,   12,   12,
};
final static short yylen[] = {                            2,
    1,    2,    0,    1,    1,    1,    3,    3,    5,    1,
    3,    5,    3,    4,    4,    2,    2,    3,    1,    1,
    2,    1,    3,    1,    2,    1,    2,    1,    2,    1,
    1,    1,    1,    1,    3,    1,    1,    3,    1,    1,
    1,    2,    3,    3,    4,    1,    3,    1,    1,    1,
    1,    1,    2,    1,    1,    2,    2,    1,    2,    2,
    3,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    2,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    3,    3,    3,    1,    3,    3,    1,
    1,    1,    1,    1,    1,    3,    4,    5,    4,
};
final static short yydefred[] = {                         0,
    0,   19,    0,    0,    0,    0,   33,  122,  121,   39,
   40,   41,   31,   34,   36,    0,    1,    0,    0,    5,
    0,   10,    0,   22,    0,   26,   28,    0,   30,   32,
   37,   42,    0,   69,   70,   71,   72,   73,   74,   75,
   76,   77,   78,   79,   80,   81,   82,   83,    0,   84,
   85,   68,   62,   64,   63,   66,   65,    0,   46,   54,
    0,    0,   67,  120,    0,    0,    0,    0,    0,    0,
    0,  117,   92,   93,   94,   95,   96,   97,   98,   99,
  100,  101,  102,  103,  104,  105,  106,  107,  108,  109,
  110,  111,  112,  113,   89,   90,   91,    0,   86,   88,
    0,    2,    0,    0,   20,   16,    0,    0,    0,    0,
  125,  123,  124,   25,   27,   29,   43,    0,   53,   44,
   48,   49,   50,   51,    0,    0,   57,   59,   60,  119,
    0,    0,  116,    0,    0,    0,   38,   13,  114,  115,
    0,   35,   87,    0,    0,   23,    0,    0,   11,   18,
    0,    0,   45,   47,   61,   15,   14,  118,    0,    0,
    0,  126,    0,    0,   12,  129,  127,    0,  128,
};
final static short yydgoto[] = {                         16,
   17,   18,   19,   20,   21,   22,  106,   23,   24,   25,
   26,  115,   27,   28,   29,   30,   98,   31,   58,   59,
  125,   60,   61,   62,   63,   99,  100,   71,   65,
};
final static short yysindex[] = {                       389,
  150,    0,  -21,  415,   -1,  337,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  467,   68,    0,
    8,    0,  545,    0,  363,    0,    0,  545,    0,    0,
    0,    0,  176,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  280,    0,
    0,    0,    0,    0,    0,    0,    0,   13,    0,    0,
  202,  228,    0,    0,   12,  493,  441,   19,   36,   11,
    2,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  311,    0,    0,
  415,    0,  571,  545,    0,    0,  571,  545,   72,   61,
    0,    0,    0,    0,    0,    0,    0,   18,    0,    0,
    0,    0,    0,    0,  254,  592,    0,    0,    0,    0,
   46,   24,    0,   67,   62,  545,    0,    0,    0,    0,
  -21,    0,    0,  519,   66,    0,   70,   73,    0,    0,
   52,    3,    0,    0,    0,    0,    0,    0,  571,  571,
   85,    0,    1,   73,    0,    0,    0,   90,    0,
};
final static short yyrindex[] = {                        92,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   96,    0,
  101,    0,    0,    0,   10,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   22,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   31,   80,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   28,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   87,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  102,    0,   69,  105,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  118,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  103,    5,   -4,   -3,   16, -103,   14,    0,  -23,    0,
   94,    0,    0,    0,    0,    4,    0,    6,   91,   -5,
    0,   50,    7,    0,    0,   27,    0,    0,   -2,
};
final static int YYTABLESIZE=616;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         68,
   69,  114,   72,  149,   56,  167,   57,  162,   67,   24,
   64,  140,   70,  107,  120,   24,  130,   24,  109,  153,
  139,  141,  163,   52,  136,   24,  137,   17,   24,  136,
   64,  137,   55,  168,  104,   17,   56,  105,   57,  121,
  122,  123,  124,  138,  121,  122,  123,  124,   52,   52,
   52,   52,   56,  133,   57,  149,  165,   55,   55,   55,
   55,  132,  134,  135,   56,   56,   57,   57,   21,  157,
  131,  159,  136,  103,  156,  136,   21,  136,  160,  146,
  151,   58,  104,  104,  161,  105,  105,  104,   56,  166,
  105,    3,   64,  152,  169,    4,   68,   69,  119,  147,
    6,    7,  148,  132,    8,  144,   58,   58,   58,   58,
  127,  129,  146,   56,   56,   56,   56,    9,  145,  154,
  102,  116,  150,  118,  143,    0,    0,    0,   56,    0,
   57,    0,  155,    0,    0,    0,  146,    0,  158,  134,
  135,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    1,   32,   33,    3,  164,   34,   35,   36,   37,   38,
   39,   40,   41,   42,   43,   44,   45,   46,   47,   48,
   49,   50,   51,   52,   53,   54,    1,  117,    0,    3,
   55,   34,   35,   36,   37,   38,   39,   40,   41,   42,
   43,   44,   45,   46,   47,   48,   49,   50,   51,   52,
   53,   54,    1,    0,    0,    3,   55,   34,   35,   36,
   37,   38,   39,   40,   41,   42,   43,   44,   45,   46,
   47,   48,  126,   50,   51,   52,   53,   54,    1,    0,
    0,    3,   55,   34,   35,   36,   37,   38,   39,   40,
   41,   42,   43,   44,   45,   46,   47,   48,  128,   50,
   51,   52,   53,   54,    1,    0,    0,    3,   55,   34,
   35,   36,   37,   38,   39,   40,   41,   42,   43,   44,
   45,   46,   47,   48,   49,   50,   51,   52,   53,   54,
    1,    0,    0,    3,   55,   34,   35,   36,   37,   38,
   39,   40,   41,   42,   43,   44,   45,   46,   47,   48,
    0,   50,   51,   52,   53,   54,    0,    0,    0,    0,
   55,   73,   74,   75,   76,   77,   78,   79,   80,   81,
   82,  142,   83,   84,   85,   86,   87,   88,   89,   90,
   91,   92,   93,   94,   95,   96,   97,   73,   74,   75,
   76,   77,   78,   79,   80,   81,   82,    0,   83,   84,
   85,   86,   87,   88,   89,   90,   91,   92,   93,   94,
   95,   96,   97,    1,    0,    0,  110,    0,    0,  108,
    0,    0,    0,    6,    7,  111,  112,  113,    0,    8,
    9,    0,   10,   11,    0,   12,   13,   14,   15,    1,
    0,    2,    3,    0,    0,    4,    0,    5,    0,    6,
    7,    0,    0,    0,    0,    8,    9,    0,   10,   11,
    0,   12,   13,   14,   15,    1,    0,    2,    3,    0,
    0,   66,    0,    5,    0,    6,    7,    0,    0,    0,
    0,    8,    9,    0,   10,   11,    0,   12,   13,   14,
   15,    1,    0,    2,    3,    0,    0,  108,  133,    0,
    0,    6,    7,    0,    0,    0,    0,    8,    9,    0,
   10,   11,    0,   12,   13,   14,   15,    1,    0,    2,
    3,    0,    0,  101,    0,    0,    0,    6,    7,    0,
    0,    0,    0,    8,    9,    0,   10,   11,    0,   12,
   13,   14,   15,    1,    0,    0,    3,    0,    0,   66,
    0,    5,    0,    6,    7,    0,    0,    0,    0,    8,
    9,    0,   10,   11,    0,   12,   13,   14,   15,    1,
    0,    2,    3,    0,    0,  108,    0,    0,    0,    6,
    7,    0,    0,    0,    0,    8,    9,    0,   10,   11,
    0,   12,   13,   14,   15,    1,    0,    0,    3,    0,
    0,  108,    0,    0,    0,    6,    7,    0,    0,    0,
    0,    8,    9,    0,   10,   11,    0,   12,   13,   14,
   15,    1,    0,    0,    3,    0,    0,  101,    0,    0,
    0,    6,    7,    0,    0,    0,    0,    8,    9,    0,
   10,   11,    0,   12,   13,   14,   15,   34,   35,   36,
   37,   38,   39,   40,   41,   42,   43,   44,   45,   46,
   47,   48,    0,   50,   51,   52,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          4,
    4,   25,    5,  107,    1,    5,    1,    5,    4,    0,
   32,   10,   14,    6,    2,    6,    5,    8,   23,    2,
   10,   20,   20,    2,    6,   16,    8,    0,   19,    6,
   32,    8,    2,   33,   16,    8,   33,   19,   33,   27,
   28,   29,   30,    8,   27,   28,   29,   30,   27,   28,
   29,   30,   49,    8,   49,  159,  160,   27,   28,   29,
   30,   66,   67,   67,   61,   62,   61,   62,    0,    8,
   66,    6,    6,    6,    8,    6,    8,    6,    6,  103,
   20,    2,   16,   16,   33,   19,   19,   16,    2,    5,
   19,    0,   32,   33,    5,    0,  101,  101,   49,  104,
    0,    0,  107,  108,    0,  101,   27,   28,   29,   30,
   61,   62,  136,   27,   28,   29,   30,    0,  103,  125,
   18,   28,  109,   33,   98,   -1,   -1,   -1,  125,   -1,
  125,   -1,  126,   -1,   -1,   -1,  160,   -1,  141,  144,
  144,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
    1,    2,    3,    4,  159,    6,    7,    8,    9,   10,
   11,   12,   13,   14,   15,   16,   17,   18,   19,   20,
   21,   22,   23,   24,   25,   26,    1,    2,   -1,    4,
   31,    6,    7,    8,    9,   10,   11,   12,   13,   14,
   15,   16,   17,   18,   19,   20,   21,   22,   23,   24,
   25,   26,    1,   -1,   -1,    4,   31,    6,    7,    8,
    9,   10,   11,   12,   13,   14,   15,   16,   17,   18,
   19,   20,   21,   22,   23,   24,   25,   26,    1,   -1,
   -1,    4,   31,    6,    7,    8,    9,   10,   11,   12,
   13,   14,   15,   16,   17,   18,   19,   20,   21,   22,
   23,   24,   25,   26,    1,   -1,   -1,    4,   31,    6,
    7,    8,    9,   10,   11,   12,   13,   14,   15,   16,
   17,   18,   19,   20,   21,   22,   23,   24,   25,   26,
    1,   -1,   -1,    4,   31,    6,    7,    8,    9,   10,
   11,   12,   13,   14,   15,   16,   17,   18,   19,   20,
   -1,   22,   23,   24,   25,   26,   -1,   -1,   -1,   -1,
   31,    1,    2,    3,    4,    5,    6,    7,    8,    9,
   10,   11,   12,   13,   14,   15,   16,   17,   18,   19,
   20,   21,   22,   23,   24,   25,   26,    1,    2,    3,
    4,    5,    6,    7,    8,    9,   10,   -1,   12,   13,
   14,   15,   16,   17,   18,   19,   20,   21,   22,   23,
   24,   25,   26,    1,   -1,   -1,    4,   -1,   -1,    7,
   -1,   -1,   -1,   11,   12,   13,   14,   15,   -1,   17,
   18,   -1,   20,   21,   -1,   23,   24,   25,   26,    1,
   -1,    3,    4,   -1,   -1,    7,   -1,    9,   -1,   11,
   12,   -1,   -1,   -1,   -1,   17,   18,   -1,   20,   21,
   -1,   23,   24,   25,   26,    1,   -1,    3,    4,   -1,
   -1,    7,   -1,    9,   -1,   11,   12,   -1,   -1,   -1,
   -1,   17,   18,   -1,   20,   21,   -1,   23,   24,   25,
   26,    1,   -1,    3,    4,   -1,   -1,    7,    8,   -1,
   -1,   11,   12,   -1,   -1,   -1,   -1,   17,   18,   -1,
   20,   21,   -1,   23,   24,   25,   26,    1,   -1,    3,
    4,   -1,   -1,    7,   -1,   -1,   -1,   11,   12,   -1,
   -1,   -1,   -1,   17,   18,   -1,   20,   21,   -1,   23,
   24,   25,   26,    1,   -1,   -1,    4,   -1,   -1,    7,
   -1,    9,   -1,   11,   12,   -1,   -1,   -1,   -1,   17,
   18,   -1,   20,   21,   -1,   23,   24,   25,   26,    1,
   -1,    3,    4,   -1,   -1,    7,   -1,   -1,   -1,   11,
   12,   -1,   -1,   -1,   -1,   17,   18,   -1,   20,   21,
   -1,   23,   24,   25,   26,    1,   -1,   -1,    4,   -1,
   -1,    7,   -1,   -1,   -1,   11,   12,   -1,   -1,   -1,
   -1,   17,   18,   -1,   20,   21,   -1,   23,   24,   25,
   26,    1,   -1,   -1,    4,   -1,   -1,    7,   -1,   -1,
   -1,   11,   12,   -1,   -1,   -1,   -1,   17,   18,   -1,
   20,   21,   -1,   23,   24,   25,   26,    6,    7,    8,
    9,   10,   11,   12,   13,   14,   15,   16,   17,   18,
   19,   20,   -1,   22,   23,   24,
};
}
final static short YYFINAL=16;
final static short YYMAXTOKEN=33;
final static String yyname[] = {
"end-of-file","LSB","RSB","HAT","LCB","RCB","OR","LRB","RRB","LT","GT","QUOTE",
"DOT","QMARK","STAR","PLUS","SLASH","TILDE","EMARK","DOLLAR","COMMA","MINUS",
"WS","CH","ESCAPED_CHAR","ESCAPED_CHAR_CLASS","ESCAPED_CHAR_SEQUENCE","OR_OR",
"AND_AND","MINUS_MINUS","TILDE_TILDE","NAMED_CHAR_CLASS","NAME","NUMBER",
};
final static String yyrule[] = {
"$accept : CompilationUnit",
"CompilationUnit : RootExpr",
"CompilationUnit : Condition RootExpr",
"CompilationUnit :",
"RootExpr : SimpleAlternation",
"RootExpr : LookaroundExpr",
"RootExpr : RootAlternation",
"RootExpr : SimpleAlternation OR RootAlternation",
"RootExpr : RootAlternation OR SimpleAlternation",
"RootExpr : SimpleAlternation OR RootAlternation OR SimpleAlternation",
"RootAlternation : RootAlternationElement",
"RootAlternation : RootAlternation OR RootAlternationElement",
"RootAlternation : RootAlternation OR SimpleAlternation OR RootAlternationElement",
"RootAlternationElement : LRB LookaroundExpr RRB",
"RootAlternationElement : LRB Condition LookaroundExpr RRB",
"RootAlternationElement : LRB Condition SimpleAlternation RRB",
"LookaroundExpr : SimpleAlternation Lookafter",
"LookaroundExpr : Lookbefore SimpleAlternation",
"LookaroundExpr : Lookbefore SimpleAlternation Lookafter",
"Lookbefore : HAT",
"Lookafter : DOLLAR",
"Lookafter : SLASH SimpleAlternation",
"SimpleAlternation : Concatenation",
"SimpleAlternation : SimpleAlternation OR Concatenation",
"Concatenation : QuantifierExpr",
"Concatenation : QuantifierExpr Concatenation",
"QuantifierExpr : ModifierExpr",
"QuantifierExpr : QuantifierExpr Quantifier",
"ModifierExpr : SimpleExpr",
"ModifierExpr : Modifier ModifierExpr",
"SimpleExpr : SimpleExprChar",
"SimpleExpr : ESCAPED_CHAR",
"SimpleExpr : CharacterClass",
"SimpleExpr : DOT",
"SimpleExpr : ESCAPED_CHAR_CLASS",
"SimpleExpr : QUOTE StringSequence QUOTE",
"SimpleExpr : ESCAPED_CHAR_SEQUENCE",
"SimpleExpr : Macro",
"SimpleExpr : LRB SimpleAlternation RRB",
"SimpleExprChar : COMMA",
"SimpleExprChar : MINUS",
"SimpleExprChar : CH",
"CharacterClass : LSB RSB",
"CharacterClass : LSB HAT RSB",
"CharacterClass : LSB CCSequenceList RSB",
"CharacterClass : LSB HAT CCSequenceList RSB",
"CCSequenceList : CCSequence",
"CCSequenceList : CCSequenceList CCOperator CCSequence",
"CCOperator : OR_OR",
"CCOperator : AND_AND",
"CCOperator : MINUS_MINUS",
"CCOperator : TILDE_TILDE",
"CCSequence : MINUS",
"CCSequence : MINUS CCSequenceEnd",
"CCSequence : CCSequenceEnd",
"CCSequenceEnd : CCCharacter",
"CCSequenceEnd : CCCharacter MINUS",
"CCSequenceEnd : CCCharacter CCSequenceEnd",
"CCSequenceEnd : CCNoSimpleCharSequenceElement",
"CCSequenceEnd : CCNoSimpleCharSequenceElement MINUS",
"CCSequenceEnd : CCNoSimpleCharSequenceElement CCSequenceEnd",
"CCNoSimpleCharSequenceElement : CCCharacter MINUS CCCharacter",
"CCNoSimpleCharSequenceElement : ESCAPED_CHAR_CLASS",
"CCNoSimpleCharSequenceElement : NAMED_CHAR_CLASS",
"CCNoSimpleCharSequenceElement : ESCAPED_CHAR_SEQUENCE",
"CCNoSimpleCharSequenceElement : Macro",
"CCNoSimpleCharSequenceElement : CharacterClass",
"CCCharacter : CCCharacterChar",
"CCCharacter : ESCAPED_CHAR",
"CCCharacterChar : OR",
"CCCharacterChar : LRB",
"CCCharacterChar : RRB",
"CCCharacterChar : LT",
"CCCharacterChar : GT",
"CCCharacterChar : QUOTE",
"CCCharacterChar : DOT",
"CCCharacterChar : QMARK",
"CCCharacterChar : STAR",
"CCCharacterChar : PLUS",
"CCCharacterChar : SLASH",
"CCCharacterChar : TILDE",
"CCCharacterChar : EMARK",
"CCCharacterChar : DOLLAR",
"CCCharacterChar : COMMA",
"CCCharacterChar : WS",
"CCCharacterChar : CH",
"StringSequence : StringElement",
"StringSequence : StringSequence StringElement",
"StringElement : StringElementChar",
"StringElement : ESCAPED_CHAR",
"StringElement : ESCAPED_CHAR_CLASS",
"StringElement : ESCAPED_CHAR_SEQUENCE",
"StringElementChar : LSB",
"StringElementChar : RSB",
"StringElementChar : HAT",
"StringElementChar : LCB",
"StringElementChar : RCB",
"StringElementChar : OR",
"StringElementChar : LRB",
"StringElementChar : RRB",
"StringElementChar : LT",
"StringElementChar : GT",
"StringElementChar : DOT",
"StringElementChar : QMARK",
"StringElementChar : STAR",
"StringElementChar : PLUS",
"StringElementChar : SLASH",
"StringElementChar : TILDE",
"StringElementChar : EMARK",
"StringElementChar : DOLLAR",
"StringElementChar : COMMA",
"StringElementChar : MINUS",
"StringElementChar : WS",
"StringElementChar : CH",
"Condition : LT STAR GT",
"Condition : LT NameList GT",
"Condition : LRB Condition RRB",
"NameList : Name",
"NameList : NameList COMMA Name",
"Macro : LCB Name RCB",
"Name : NAME",
"Modifier : EMARK",
"Modifier : TILDE",
"Quantifier : STAR",
"Quantifier : PLUS",
"Quantifier : QMARK",
"Quantifier : LCB NUMBER RCB",
"Quantifier : LCB NUMBER COMMA RCB",
"Quantifier : LCB NUMBER COMMA NUMBER RCB",
"Quantifier : LCB COMMA NUMBER RCB",
};

//#line 605 "RegExParser.y"
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
//#line 582 "RegExParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    //if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      //if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          //if (yydebug)
          //  yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        //if (yydebug)
          //debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      //if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            //if (yydebug)
              //debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            //if (yydebug)
              //debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        //if (yydebug)
          //{
          //yys = null;
          //if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          //if (yys == null) yys = "illegal-symbol";
          //debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          //}
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    //if (yydebug)
      //debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 102 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCompilationUnit(val_peek(0).n));
    }
break;
case 2:
//#line 106 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCompilationUnit(val_peek(1).n,val_peek(0).n));
    }
break;
case 3:
//#line 110 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCompilationUnit());
    }
break;
case 4:
//#line 115 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMRootAlternation(new ROMRootElement(val_peek(0).n)));
    }
break;
case 5:
//#line 119 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMRootAlternation(new ROMRootElement(val_peek(0).n)));
    }
break;
case 7:
//#line 125 "RegExParser.y"
{
        val_peek(0).n.appendChild(new ROMRootElement(val_peek(2).n),true);
        yyval = val_peek(0);
    }
break;
case 8:
//#line 130 "RegExParser.y"
{
        val_peek(2).n.appendChild(new ROMRootElement(val_peek(0).n));
        yyval = val_peek(2);
    }
break;
case 9:
//#line 135 "RegExParser.y"
{
        val_peek(2).n.appendChild(new ROMRootElement(val_peek(4).n),true);
        val_peek(2).n.appendChild(new ROMRootElement(val_peek(0).n));
        yyval = val_peek(2);
    }
break;
case 11:
//#line 144 "RegExParser.y"
{
        val_peek(2).n.appendChildren(val_peek(0).n);
        yyval = val_peek(2);
    }
break;
case 12:
//#line 149 "RegExParser.y"
{
        val_peek(4).n.appendChild(new ROMRootElement(val_peek(2).n));
        val_peek(4).n.appendChildren(val_peek(0).n);
        yyval = val_peek(4);
    }
break;
case 13:
//#line 156 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMRootAlternation(new ROMRootElement(val_peek(1).n)));
    }
break;
case 14:
//#line 160 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMRootAlternation(new ROMRootElement(val_peek(2).n,val_peek(1).n)));
    }
break;
case 15:
//#line 164 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMRootAlternation(new ROMRootElement(val_peek(2).n,val_peek(1).n)));
    }
break;
case 16:
//#line 173 "RegExParser.y"
{
        val_peek(0).n.appendChild(val_peek(1).n,true);
        yyval = val_peek(0);
    }
break;
case 17:
//#line 178 "RegExParser.y"
{
        val_peek(1).n.appendChild(val_peek(0).n);
        yyval = val_peek(1);
    }
break;
case 18:
//#line 183 "RegExParser.y"
{
        val_peek(2).n.appendChild(val_peek(1).n);
        val_peek(2).n.appendChildren(val_peek(0).n);
        yyval = val_peek(2);
    }
break;
case 19:
//#line 190 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMLookaroundExpr(new ROMLookbefore(LookbeforeType.START_OF_LINE)));
    }
break;
case 20:
//#line 195 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMLookaroundExpr(new ROMLookafter(LookafterType.END_OF_LINE)));
    }
break;
case 21:
//#line 199 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMLookaroundExpr(new ROMLookafter(LookafterType.EXPRESSION),val_peek(0).n));
    }
break;
case 23:
//#line 210 "RegExParser.y"
{
        if (val_peek(2).n.isAlternationExpr() && val_peek(0).n.isAlternationExpr()) {
            val_peek(2).n.appendChildren(val_peek(0).n);
            yyval = val_peek(2);
        }
        
        else if (val_peek(2).n.isAlternationExpr()) {
            val_peek(2).n.appendChild(val_peek(0).n);
            yyval = val_peek(2);
        }
        
        else if (val_peek(0).n.isAlternationExpr()) {
            val_peek(0).n.appendChild(val_peek(2).n,true);
            yyval = val_peek(0);
        }
        
        else {
            yyval = new RegExParserVal(new ROMAlternationExpr(val_peek(2).n,val_peek(0).n));
        }
    }
break;
case 25:
//#line 234 "RegExParser.y"
{
        if (val_peek(1).n.isConcatenationExpr() && val_peek(0).n.isConcatenationExpr()) {
           val_peek(1).n.appendChildren(val_peek(0).n);
           yyval = val_peek(1);
        }
        
        else if (val_peek(1).n.isConcatenationExpr()) {
           val_peek(1).n.appendChild(val_peek(0).n);
           yyval = val_peek(1);
        }
        
        else if (val_peek(0).n.isConcatenationExpr()) {
           val_peek(0).n.appendChild(val_peek(1).n,true);
           yyval = val_peek(0);
        }
        
        else {
           yyval = new RegExParserVal(new ROMConcatenationExpr(val_peek(1).n,val_peek(0).n));
        }
    }
break;
case 27:
//#line 258 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMQuantifierExpr(val_peek(1).n,val_peek(0).n));
    }
break;
case 29:
//#line 265 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMModifierExpr(val_peek(1).n,val_peek(0).n));
    }
break;
case 30:
//#line 270 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharExpr(new ROMCharRef(new CharRef((Character)val_peek(0).t.value()))));
    }
break;
case 31:
//#line 274 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharExpr(new ROMCharRef((CharRef)val_peek(0).t.value())));
    }
break;
case 32:
//#line 278 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMClassExpr(val_peek(0).n));
    }
break;
case 33:
//#line 282 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMClassExpr(new ROMClassRef(PropertySelector.forBinary(Property.VWHITE_SPACE,true))));
    }
break;
case 34:
//#line 286 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMClassExpr(new ROMClassRef((PropertySelector)val_peek(0).t.value())));
    }
break;
case 35:
//#line 290 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMSequenceExpr(val_peek(1).n));
    }
break;
case 36:
//#line 294 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMSequenceExpr(new ROMSequenceRef((SequenceRef)val_peek(0).t.value())));
    }
break;
case 37:
//#line 298 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMMacroExpr(val_peek(0).n));
    }
break;
case 38:
//#line 302 "RegExParser.y"
{
        yyval = val_peek(1);
    }
break;
case 42:
//#line 316 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharacterClass(false));
    }
break;
case 43:
//#line 320 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharacterClass(true));
    }
break;
case 44:
//#line 324 "RegExParser.y"
{
        yyval = val_peek(1);
    }
break;
case 45:
//#line 328 "RegExParser.y"
{
        ((ROMCharacterClass)val_peek(1).n).setInvert(true);
        yyval = val_peek(1);
    }
break;
case 46:
//#line 334 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharacterClass(val_peek(0).n));
    }
break;
case 47:
//#line 338 "RegExParser.y"
{
        val_peek(2).n.appendChild(val_peek(1).n);
        val_peek(2).n.appendChild(val_peek(0).n);
        yyval = val_peek(2);
    }
break;
case 48:
//#line 345 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCOperator(CharClassOperator.UNION));
    }
break;
case 49:
//#line 349 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCOperator(CharClassOperator.INTERSECTION));
    }
break;
case 50:
//#line 353 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCOperator(CharClassOperator.SET_DIFFERENCE));
    }
break;
case 51:
//#line 357 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCOperator(CharClassOperator.SYMMETRIC_DIFFERENCE));
    }
break;
case 52:
//#line 362 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCSequence(new ROMCharRef(new CharRef((Character)val_peek(0).t.value()))));
    }
break;
case 53:
//#line 366 "RegExParser.y"
{
        val_peek(0).n.appendChild(new ROMCharRef(new CharRef((Character)val_peek(1).t.value())),true);
        yyval = val_peek(0);
    }
break;
case 55:
//#line 374 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCSequence(val_peek(0).n));
    }
break;
case 56:
//#line 378 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCSequence(val_peek(1).n,
                new ROMCharRef(new CharRef((Character)val_peek(0).t.value()))));
    }
break;
case 57:
//#line 383 "RegExParser.y"
{
        val_peek(0).n.appendChild(val_peek(1).n,true);
        yyval = val_peek(0);
    }
break;
case 58:
//#line 388 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCSequence(val_peek(0).n));
    }
break;
case 59:
//#line 392 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCSequence(val_peek(1).n,
                new ROMCharRef(new CharRef((Character)val_peek(0).t.value()))));
    }
break;
case 60:
//#line 397 "RegExParser.y"
{
        val_peek(0).n.appendChild(val_peek(1).n,true);
        yyval = val_peek(0);
    }
break;
case 61:
//#line 403 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCCRange(val_peek(2).n,val_peek(0).n));
    }
break;
case 62:
//#line 407 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMClassRef((PropertySelector)val_peek(0).t.value()));
    }
break;
case 63:
//#line 411 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMClassRef((PropertySelector)val_peek(0).t.value()));
    }
break;
case 64:
//#line 415 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMSequenceRef((SequenceRef)val_peek(0).t.value()));
    }
break;
case 67:
//#line 423 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharRef(new CharRef((Character)val_peek(0).t.value())));
    }
break;
case 68:
//#line 427 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharRef((CharRef)val_peek(0).t.value()));
    }
break;
case 86:
//#line 455 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMStringSequence(val_peek(0).n));
    }
break;
case 87:
//#line 459 "RegExParser.y"
{
        val_peek(1).n.appendChild(val_peek(0).n);
        yyval = val_peek(1);
    }
break;
case 88:
//#line 465 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharRef(new CharRef((Character)val_peek(0).t.value())));
    }
break;
case 89:
//#line 469 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCharRef((CharRef)val_peek(0).t.value()));
    }
break;
case 90:
//#line 473 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMClassRef((PropertySelector)val_peek(0).t.value()));
    }
break;
case 91:
//#line 477 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMSequenceRef((SequenceRef)val_peek(0).t.value()));
    }
break;
case 114:
//#line 510 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCondition());
    }
break;
case 115:
//#line 514 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMCondition(val_peek(1).n));
    }
break;
case 116:
//#line 518 "RegExParser.y"
{
        yyval = val_peek(1);
    }
break;
case 117:
//#line 523 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMNameList(val_peek(0).n));
    }
break;
case 118:
//#line 527 "RegExParser.y"
{
        val_peek(2).n.appendChild(val_peek(0).n);
        yyval = val_peek(2);
    }
break;
case 119:
//#line 533 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMMacro(val_peek(1).n));
    }
break;
case 120:
//#line 538 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMName((String)val_peek(0).t.value()));
    }
break;
case 121:
//#line 543 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMModifier(Modifier.NOT));
    }
break;
case 122:
//#line 547 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMModifier(Modifier.UNTIL));
    }
break;
case 123:
//#line 552 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMQuantifier(Quantifier.ZERO_OR_MORE));
    }
break;
case 124:
//#line 556 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMQuantifier(Quantifier.ONE_OR_MORE));
    }
break;
case 125:
//#line 560 "RegExParser.y"
{
        yyval = new RegExParserVal(new ROMQuantifier(Quantifier.ZERO_OR_ONE));
    }
break;
case 126:
//#line 564 "RegExParser.y"
{
        try {
            yyval = new RegExParserVal(new ROMQuantifier(Quantifier.createExactly((Integer)val_peek(1).t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    }
break;
case 127:
//#line 574 "RegExParser.y"
{
        try {
            yyval = new RegExParserVal(new ROMQuantifier(Quantifier.createAtLeast((Integer)val_peek(2).t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    }
break;
case 128:
//#line 584 "RegExParser.y"
{
        try {
            yyval = new RegExParserVal(new ROMQuantifier(Quantifier.create((Integer)val_peek(3).t.value(),(Integer)val_peek(1).t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    }
break;
case 129:
//#line 594 "RegExParser.y"
{
        try {
            yyval = new RegExParserVal(new ROMQuantifier(Quantifier.createUpTo((Integer)val_peek(1).t.value())));
        }
        
        catch(QuantifierException e) {
            throw new RegExParseException(e.getMessage());
        }
    }
break;
//#line 1278 "RegExParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    //if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      //if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        //if (yydebug)
          //yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      //if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public RegExParser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public RegExParser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
