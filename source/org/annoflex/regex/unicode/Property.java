/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

import java.util.HashMap;

/**
 * @author Stefan Czaska
 */
public enum Property {
    
    //====================
    // Enumeration Values
    //====================
    
    // binary properties
    ALNUM                             (PropertyType.BINARY,Source.BINARY,"alnum","alnum"),
    ALPHABETIC                        (PropertyType.BINARY,Source.BINARY,"alphabetic","alpha"),
    ANY                               (PropertyType.BINARY,Source.BINARY,"any","any"),
    ASCII                             (PropertyType.BINARY,Source.BINARY,"ascii","ascii"),
    ASCII_HEX_DIGIT                   (PropertyType.BINARY,Source.BINARY,"asciihexdigit","ahex"),
    ASSIGNED                          (PropertyType.BINARY,Source.BINARY,"assigned","assigned"),
    BIDI_CONTROL                      (PropertyType.BINARY,Source.BINARY,"bidicontrol","bidic"),
    BIDI_MIRRORED                     (PropertyType.BINARY,Source.BINARY,"bidimirrored","bidim"),
    BLANK                             (PropertyType.BINARY,Source.BINARY,"blank","blank"),
    CASE_IGNORABLE                    (PropertyType.BINARY,Source.BINARY,"caseignorable","ci"),
    CASED                             (PropertyType.BINARY,Source.BINARY,"cased","cased"),
    CHANGES_WHEN_CASEFOLDED           (PropertyType.BINARY,Source.BINARY,"changeswhencasefolded","cwcf"),
    CHANGES_WHEN_CASEMAPPED           (PropertyType.BINARY,Source.BINARY,"changeswhencasemapped","cwcm"),
    CHANGES_WHEN_LOWERCASED           (PropertyType.BINARY,Source.BINARY,"changeswhenlowercased","cwl"),
    CHANGES_WHEN_NFKC_CASEFOLDED      (PropertyType.BINARY,Source.BINARY,"changeswhennfkccasefolded","cwkcf"),
    CHANGES_WHEN_TITLECASED           (PropertyType.BINARY,Source.BINARY,"changeswhentitlecased","cwt"),
    CHANGES_WHEN_UPPERCASED           (PropertyType.BINARY,Source.BINARY,"changeswhenuppercased","cwu"),
    CNTRL                             (PropertyType.BINARY,Source.BINARY,"cntrl","cntrl"),
    COMPOSITION_EXCLUSION             (PropertyType.BINARY,Source.BINARY,"compositionexclusion","ce"),
    DASH                              (PropertyType.BINARY,Source.BINARY,"dash","dash"),
    DEFAULT_IGNORABLE_CODE_POINT      (PropertyType.BINARY,Source.BINARY,"defaultignorablecodepoint","di"),
    DEPRECATED                        (PropertyType.BINARY,Source.BINARY,"deprecated","dep"),
    DIACRITIC                         (PropertyType.BINARY,Source.BINARY,"diacritic","dia"),
    DIGIT                             (PropertyType.BINARY,Source.BINARY,"digit","digit"),
    EXPANDS_ON_NFC                    (PropertyType.BINARY,Source.BINARY,"expandsonnfc","xonfc"),
    EXPANDS_ON_NFD                    (PropertyType.BINARY,Source.BINARY,"expandsonnfd","xonfd"),
    EXPANDS_ON_NFKC                   (PropertyType.BINARY,Source.BINARY,"expandsonnfkc","xonfkc"),
    EXPANDS_ON_NFKD                   (PropertyType.BINARY,Source.BINARY,"expandsonnfkd","xonfkd"),
    EXTENDER                          (PropertyType.BINARY,Source.BINARY,"extender","ext"),
    FULL_COMPOSITION_EXCLUSION        (PropertyType.BINARY,Source.BINARY,"fullcompositionexclusion","compex"),
    GRAPH                             (PropertyType.BINARY,Source.BINARY,"graph","graph"),
    GRAPHEME_BASE                     (PropertyType.BINARY,Source.BINARY,"graphemebase","grbase"),
    GRAPHEME_EXTEND                   (PropertyType.BINARY,Source.BINARY,"graphemeextend","grext"),
    GRAPHEME_LINK                     (PropertyType.BINARY,Source.BINARY,"graphemelink","grlink"),
    HEX_DIGIT                         (PropertyType.BINARY,Source.BINARY,"hexdigit","hex"),
    HYPHEN                            (PropertyType.BINARY,Source.BINARY,"hyphen","hyphen"),
    ID_START                          (PropertyType.BINARY,Source.BINARY,"idstart","ids"),
    ID_CONTINUE                       (PropertyType.BINARY,Source.BINARY,"idcontinue","idc"),
    IDEOGRAPHIC                       (PropertyType.BINARY,Source.BINARY,"ideographic","ideo"),
    IDS_BINARY_OPERATOR               (PropertyType.BINARY,Source.BINARY,"idsbinaryoperator","idsb"),
    IDS_TRINARY_OPERATOR              (PropertyType.BINARY,Source.BINARY,"idstrinaryoperator","idst"),
    JOIN_CONTROL                      (PropertyType.BINARY,Source.BINARY,"joincontrol","joinc"),
    LOGICAL_ORDER_EXCEPTION           (PropertyType.BINARY,Source.BINARY,"logicalorderexception","loe"),
    LOWERCASE                         (PropertyType.BINARY,Source.BINARY,"lowercase","lower"),
    MATH                              (PropertyType.BINARY,Source.BINARY,"math","math"),
    NONCHARACTER_CODE_POINT           (PropertyType.BINARY,Source.BINARY,"noncharactercodepoint","nchar"),
    OTHER_ALPHABETIC                  (PropertyType.BINARY,Source.BINARY,"otheralphabetic","oalpha"),
    OTHER_DEFAULT_IGNORABLE_CODE_POINT(PropertyType.BINARY,Source.BINARY,"otherdefaultignorablecodepoint","odi"),
    OTHER_GRAPHEME_EXTEND             (PropertyType.BINARY,Source.BINARY,"othergraphemeextend","ogrext"),
    OTHER_ID_CONTINUE                 (PropertyType.BINARY,Source.BINARY,"otheridcontinue","oidc"),
    OTHER_ID_START                    (PropertyType.BINARY,Source.BINARY,"otheridstart","oids"),
    OTHER_LOWERCASE                   (PropertyType.BINARY,Source.BINARY,"otherlowercase","olower"),
    OTHER_MATH                        (PropertyType.BINARY,Source.BINARY,"othermath","omath"),
    OTHER_UPPERCASE                   (PropertyType.BINARY,Source.BINARY,"otheruppercase","oupper"),
    PATTERN_SYNTAX                    (PropertyType.BINARY,Source.BINARY,"patternsyntax","patsyn"),
    PATTERN_WHITE_SPACE               (PropertyType.BINARY,Source.BINARY,"patternwhitespace","patws"),
    PREPENDED_CONCATENATION_MARK      (PropertyType.BINARY,Source.BINARY,"prependedconcatenationmark","pcm"),
    PRINT                             (PropertyType.BINARY,Source.BINARY,"print","print"),
    PUNCT                             (PropertyType.BINARY,Source.BINARY,"punct","punct"),
    QUOTATION_MARK                    (PropertyType.BINARY,Source.BINARY,"quotationmark","qmark"),
    RADICAL                           (PropertyType.BINARY,Source.BINARY,"radical","radical"),
    SENTENCE_TERMINAL                 (PropertyType.BINARY,Source.BINARY,"sentenceterminal","sterm"),
    SOFT_DOTTED                       (PropertyType.BINARY,Source.BINARY,"softdotted","sd"),
    TERMINAL_PUNCTUATION              (PropertyType.BINARY,Source.BINARY,"terminalpunctuation","term"),
    UNIFIED_IDEOGRAPH                 (PropertyType.BINARY,Source.BINARY,"unifiedideograph","uideo"),
    UPPERCASE                         (PropertyType.BINARY,Source.BINARY,"uppercase","upper"),
    VARIATION_SELECTOR                (PropertyType.BINARY,Source.BINARY,"variationselector","vs"),
    WHITE_SPACE                       (PropertyType.BINARY,Source.BINARY,"whitespace","wspace"),
    WORD                              (PropertyType.BINARY,Source.BINARY,"word","word"),
    XDIGIT                            (PropertyType.BINARY,Source.BINARY,"xdigit","xdigit"),
    XID_CONTINUE                      (PropertyType.BINARY,Source.BINARY,"xidcontinue","xidc"),
    XID_START                         (PropertyType.BINARY,Source.BINARY,"xidstart","xids"),
    
    // other binary properties
    HWHITE_SPACE         (PropertyType.BINARY,Source.BINARY,"hwhitespace","hwhitespace"),
    VWHITE_SPACE         (PropertyType.BINARY,Source.BINARY,"vwhitespace","vwhitespace"),
    POSIX_ALNUM          (PropertyType.BINARY,Source.BINARY,"posixalnum","posixalnum"),
    POSIX_ALPHA          (PropertyType.BINARY,Source.BINARY,"posixalpha","posixalpha"),
    POSIX_ASCII          (PropertyType.BINARY,Source.BINARY,"posixascii","posixascii"),
    POSIX_BLANK          (PropertyType.BINARY,Source.BINARY,"posixblank","posixblank"),
    POSIX_CNTRL          (PropertyType.BINARY,Source.BINARY,"posixcntrl","posixcntrl"),
    POSIX_DIGIT          (PropertyType.BINARY,Source.BINARY,"posixdigit","posixdigit"),
    POSIX_GRAPH          (PropertyType.BINARY,Source.BINARY,"posixgraph","posixgraph"),
    POSIX_LOWER          (PropertyType.BINARY,Source.BINARY,"posixlower","posixlower"),
    POSIX_PRINT          (PropertyType.BINARY,Source.BINARY,"posixprint","posixprint"),
    POSIX_PUNCT          (PropertyType.BINARY,Source.BINARY,"posixpunct","posixpunct"),
    POSIX_SPACE          (PropertyType.BINARY,Source.BINARY,"posixspace","posixspace"),
    POSIX_UPPER          (PropertyType.BINARY,Source.BINARY,"posixupper","posixupper"),
    POSIX_WORD           (PropertyType.BINARY,Source.BINARY,"posixword","posixword"),
    POSIX_XDIGIT         (PropertyType.BINARY,Source.BINARY,"posixxdigit","posixxdigit"),
    JAVA_IDENTIFIER_START(PropertyType.BINARY,Source.BINARY,"javaidentifierstart","javaidentifierstart"),
    JAVA_IDENTIFIER_PART (PropertyType.BINARY,Source.BINARY,"javaidentifierpart","javaidentifierpart"),
    
    // catalog properties
    BLOCK (PropertyType.CATALOG,Source.NON_BINARY,"block","blk"),
    AGE   (PropertyType.CATALOG,Source.NON_BINARY,"age","age"),
    SCRIPT(PropertyType.CATALOG,Source.NON_BINARY,"script","sc"),
    
    // enumeration properties
    BIDI_CLASS               (PropertyType.ENUMERATION,Source.NON_BINARY,"bidiclass","bc"),
    BIDI_PAIRED_BRACKET_TYPE (PropertyType.ENUMERATION,Source.NON_BINARY,"bidipairedbrackettype","bpt"),
    DECOMPOSITION_TYPE       (PropertyType.ENUMERATION,Source.NON_BINARY,"decompositiontype","dt"),
    EAST_ASIAN_WIDTH         (PropertyType.ENUMERATION,Source.NON_BINARY,"eastasianwidth","ea"),
    GENERAL_CATEGORY         (PropertyType.ENUMERATION,Source.NON_BINARY,"generalcategory","gc"),
    GRAPHEME_CLUSTER_BREAK   (PropertyType.ENUMERATION,Source.NON_BINARY,"graphemeclusterbreak","gcb"),
    HANGUL_SYLLABLE_TYPE     (PropertyType.ENUMERATION,Source.NON_BINARY,"hangulsyllabletype","hst"),
    INDIC_POSITIONAL_CATEGORY(PropertyType.ENUMERATION,Source.NON_BINARY,"indicpositionalcategory","inpc"),
    INDIC_SYLLABIC_CATEGORY  (PropertyType.ENUMERATION,Source.NON_BINARY,"indicsyllabiccategory","insc"),
    JOINING_GROUP            (PropertyType.ENUMERATION,Source.NON_BINARY,"joininggroup","jg"),
    JOINING_TYPE             (PropertyType.ENUMERATION,Source.NON_BINARY,"joiningtype","jt"),
    LINE_BREAK               (PropertyType.ENUMERATION,Source.NON_BINARY,"linebreak","lb"),
    NFC_QUICK_CHECK          (PropertyType.ENUMERATION,Source.NON_BINARY,"nfcquickcheck","nfcqc"),
    NFD_QUICK_CHECK          (PropertyType.ENUMERATION,Source.NON_BINARY,"nfdquickcheck","nfdqc"),
    NFKC_QUICK_CHECK         (PropertyType.ENUMERATION,Source.NON_BINARY,"nfkcquickcheck","nfkcqc"),
    NFKD_QUICK_CHECK         (PropertyType.ENUMERATION,Source.NON_BINARY,"nfkdquickcheck","nfkdqc"),
    NUMERIC_TYPE             (PropertyType.ENUMERATION,Source.NON_BINARY,"numerictype","nt"),
    SENTENCE_BREAK           (PropertyType.ENUMERATION,Source.NON_BINARY,"sentencebreak","sb"),
    WORD_BREAK               (PropertyType.ENUMERATION,Source.NON_BINARY,"wordbreak","wb"),
    
    // string properties
    CASE_FOLDING            (PropertyType.STRING,null,"casefolding","cf"),
    DECOMPOSITION_MAPPING   (PropertyType.STRING,null,"decompositionmapping","dm"),
    FC_NFKC_CLOSURE         (PropertyType.STRING,null,"fcnfkcclosure","fcnfkc"),
    LOWERCASE_MAPPING       (PropertyType.STRING,null,"lowercasemapping","lc"),
    NFKC_CASEFOLD           (PropertyType.STRING,null,"nfkccasefold","nfkccf"),
    SIMPLE_CASE_FOLDING     (PropertyType.STRING,null,"simplecasefolding","scf"),
    SIMPLE_LOWERCASE_MAPPING(PropertyType.STRING,null,"simplelowercasemapping","slc"),
    SIMPLE_TITLECASE_MAPPING(PropertyType.STRING,null,"simpletitlecasemapping","stc"),
    SIMPLE_UPPERCASE_MAPPING(PropertyType.STRING,null,"simpleuppercasemapping","suc"),
    TITLECASE_MAPPING       (PropertyType.STRING,null,"titlecasemapping","tc"),
    UPPERCASE_MAPPING       (PropertyType.STRING,null,"uppercasemapping","uc"),
    
    // numeric properties
    CANONICAL_COMBINING_CLASS(PropertyType.NUMERIC,null,"canonicalcombiningclass","ccc"),
    NUMERIC_VALUE            (PropertyType.NUMERIC,null,"numericvalue","nv"),
    
    // miscellaneous properties
    BIDI_MIRRORING_GLYPH  (PropertyType.MISCELLANEOUS,null,"bidimirroringglyph","bmg"),
    BIDI_PAIRED_BRACKET   (PropertyType.MISCELLANEOUS,null,"bidipairedbracket","bpb"),
    ISO_COMMENT           (PropertyType.MISCELLANEOUS,null,"isocomment","isc"),
    JAMO_SHORT_NAME       (PropertyType.MISCELLANEOUS,null,"jamoshortname","jsn"),
    NAME                  (PropertyType.MISCELLANEOUS,Source.CHAR_NAME,"name","na"),
    NAME_ALIAS            (PropertyType.MISCELLANEOUS,Source.CHAR_NAME,"namealias","namealias"),
    SCRIPT_EXTENSIONS     (PropertyType.MISCELLANEOUS,Source.NON_BINARY,"scriptextensions","scx"),
    UNICODE_1_NAME        (PropertyType.MISCELLANEOUS,null,"unicode1name","na1"),
    UNICODE_RADICAL_STROKE(PropertyType.MISCELLANEOUS,null,"unicoderadicalstroke","urs");
    
    //===========
    // Constants
    //===========
    
    /**
     * 
     */
    private static final HashMap<String,Property> NAME_MAP = new HashMap<>();
    
    /**
     * 
     */
    static {
        Property[] values = Property.values();
        
        for (int i=0;i<values.length;i++) {
            Property property = values[i];
            
            NAME_MAP.put(property.getLongName(),property);
            NAME_MAP.put(property.getShortName(),property);
        }
        
        // some properties have additional aliases which are handled here for
        // simplicity
        NAME_MAP.put("space",WHITE_SPACE);
        NAME_MAP.put("sfc",SIMPLE_CASE_FOLDING);
    }
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String longName;
    
    /**
     * 
     */
    private final String shortName;
    
    /**
     * 
     */
    private final PropertyType type;
    
    /**
     * 
     */
    private final String sourceFile;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    private Property(PropertyType type, String sourceFile, String longName,
            String shortName) {
        
        this.longName = longName;
        this.shortName = shortName;
        this.type = type;
        this.sourceFile = sourceFile;
    }
    
    //==================
    // Property Methods
    //==================
    
    /**
     * 
     */
    public final String getLongName() {
        return longName;
    }
    
    /**
     * 
     */
    public final String getShortName() {
        return shortName;
    }
    
    /**
     * 
     */
    public final PropertyType getType() {
        return type;
    }
    
    /**
     * 
     */
    public final String getSourceFile() {
        return sourceFile;
    }
    
    //==============
    // Type Methods
    //==============
    
    /**
     * 
     */
    public final boolean isBinary() {
        return type == PropertyType.BINARY;
    }
    
    /**
     * 
     */
    public final boolean isCatalog() {
        return type == PropertyType.CATALOG;
    }
    
    /**
     * 
     */
    public final boolean isEnumeration() {
        return type == PropertyType.ENUMERATION;
    }
    
    /**
     * 
     */
    public final boolean isString() {
        return type == PropertyType.STRING;
    }
    
    /**
     * 
     */
    public final boolean isNumeric() {
        return type == PropertyType.NUMERIC;
    }
    
    /**
     * 
     */
    public final boolean isMiscellaneous() {
        return type == PropertyType.MISCELLANEOUS;
    }
    
    //=======================
    // Normalization Methods
    //=======================
    
    /**
     * 
     */
    public final String normalizeValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }
        
        switch(type) {
        case BINARY:
        case CATALOG:
        case ENUMERATION:
        // Note: There exists aliases for numeric values which have the form of
        // a symbolic value. An example is the canonical combining class
        // property which has several short and long names for numeric values
        // which must be normalized as a symbolic value.
        case NUMERIC:
            return PropertyToolkit.normalizeSymbolicValue(value);
        
        case MISCELLANEOUS:
            switch(this) {
            case JAMO_SHORT_NAME:
            case NAME:
            case NAME_ALIAS:
            case UNICODE_1_NAME:
                return PropertyToolkit.normalizeCharName(value);
            
            // Note: Script extension values have the form of script values.
            // Thus normalize them as a symbolic value.
            case SCRIPT_EXTENSIONS:
                return PropertyToolkit.normalizeSymbolicValue(value);
            
            default:
                return value;
            }
        
        default:
            return value;
        }
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getClass().getSimpleName());
        buffer.append("[name=");
        buffer.append(longName);
        buffer.append(",type=");
        buffer.append(type);
        buffer.append(",file=");
        buffer.append(sourceFile);
        buffer.append("]");
        
        return buffer.toString();
    }
    
    //================
    // Static Methods
    //================
    
    /**
     * 
     */
    public static Property forName(String normalizedName) {
        return NAME_MAP.get(normalizedName);
    }
}
