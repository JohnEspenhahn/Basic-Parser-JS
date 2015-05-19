package com.hahn.basic.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hahn.basic.util.structures.FlagMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
class BNFParser {
    private static Pattern BNF_PATTERN = Pattern.compile("(<[_a-zA-Z][_a-zA-Z0-9]*>)|([_a-zA-Z][_a-zA-Z0-9]*)|\\s+|\\$|\\?|\\(|\\)|\\[|\\]|\\{|\\}|\\||\\*");

    private final Class<? extends Enum> Tokens, Expressions;
    
    private int lastEnd;
    private Matcher currentMatcher;
    private String currentBNF;

    private final FlagMap<Flag> flags = new FlagMap<Flag>();
    
    public BNFParser(Parser parser) {        
        this.Tokens = (Class<? extends Enum>) parser.getTokenClass();
        this.Expressions = (Class<? extends Enum>) parser.getExpressionClass();
    }

    public Enum<?>[][] parseBNF(String bnf) throws Exception {
        List<Enum<?>[]> subexpressions = new ArrayList<Enum<?>[]>();
        List<Enum<?>> subexpression = new ArrayList<Enum<?>>();

        this.lastEnd = 0;
        this.currentBNF = bnf;
        this.currentMatcher = BNFParser.BNF_PATTERN.matcher(bnf);
        while (currentMatcher.find()) {
            if (currentMatcher.start() != lastEnd) {
                throw new Exception("Invalid BNF token at " + lastEnd + " in " + bnf);
            } else {
                lastEnd = currentMatcher.end();
            }

            String group = currentMatcher.group();            
            if (currentMatcher.group(1) != null) {
                subexpression.add(Enum.valueOf(Expressions, group.substring(1, group.length() - 1)));
                doSetFlag(Flag.NEXT);
            } else if (currentMatcher.group(2) != null) {
                subexpression.add(Enum.valueOf(Tokens, group));
                doSetFlag(Flag.NEXT);
            } else {
                switch (group) {
                case "$":
                    subexpression.add(EnumOption.FULL_LOOP);
                    doSetFlag(Flag.FULL_LOOP);
                    break;
                case "?":
                    subexpression.add(EnumOption.OPTIONAL);
                    doSetFlag(Flag.REQUIRES_NEXT);
                    break;
                case "*":
                    subexpression.add(EnumOption.LAZY_MATCH);
                    doSetFlag(Flag.REQUIRES_NEXT);
                    break;
                case "{":
                    subexpression.add(EnumOption.LOOP_OPTIONAL_START);
                    doSetFlag(Flag.LOOP_OPTIONAL_START);
                    break;
                case "}":
                    subexpression.add(EnumOption.LOOP_OPTIONAL_END);
                    doSetFlag(Flag.LOOP_OPTIONAL_END);
                    break;
                case "[":
                    subexpression.add(EnumOption.LOOP_START);
                    doSetFlag(Flag.LOOP_START);
                    break;
                case "]":
                    subexpression.add(EnumOption.LOOP_END);
                    doSetFlag(Flag.LOOP_END);
                    break;
                case "(":
                    subexpression.add(EnumOption.BLOCK_OPTIONAL_START);
                    doSetFlag(Flag.OPTIONAL_BLOCK_START);
                    break;
                case ")":
                    subexpression.add(EnumOption.BLOCK_OPTIONAL_END);
                    doSetFlag(Flag.OPTIONAL_BLOCK_END);
                    break;
                case "|":
                    doSetFlag(Flag.END);
                    subexpressions.add(subexpression.toArray(new Enum<?>[0]));
                    subexpression.clear();
                    clearFlags();
                    break;
                }
            }
        }

        if (lastEnd != bnf.length()) { throw new Exception("Invalid BNF '" + bnf + "' at " + lastEnd); }

        // Finish flagging
        doSetFlag(Flag.END);
        clearFlags();

        subexpressions.add(subexpression.toArray(new Enum<?>[0]));
        return subexpressions.toArray(new Enum<?>[0][0]);
    }

    /*
     * Flags
     */
    
    private void error(String err) throws Exception {
        this.error(err, lastEnd);
    }

    private void error(String err, int idx) throws Exception {
        throw new Exception("Invalid BNF '" + currentBNF + "':\n\t\t\t" + err + " at " + idx);
    }

    private void doSetFlag(Flag flag) throws Exception {
        if (isMarked(Flag.FULL_LOOP) && flag != Flag.END) error("full loop must be at end", currentMatcher.start() - 1);
        else if (isMarked(Flag.REQUIRES_NEXT) && flag != Flag.NEXT) error("invalid preceding token");
        
        switch (flag.name()) {
        case "END":
            if (isMarked(Flag.LOOP_START)) error("missing ending ']'");
            if (isMarked(Flag.LOOP_OPTIONAL_START)) error("missing ending '}'");
            if (isMarked(Flag.OPTIONAL_BLOCK_START)) error("missing ending ')'");
            
            break;
        case "FULL_LOOP":
        case "REQUIRES_NEXT":
            if (isMarked(Flag.REQUIRES_NEXT)) error("invalid preceding token");
            flags.mark(flag);
            
            break;
        case "OPTIONAL_BLOCK_START":
            if (isMarked(Flag.OPTIONAL_BLOCK_START)) error("can't nest optional block");
            else flags.mark(flag);
            
            break;
        case "OPTIONAL_BLOCK_END":
            if (!isMarked(Flag.OPTIONAL_BLOCK_START)) error("required starting '('");
            else clearFlag(Flag.OPTIONAL_BLOCK_START);
            
            break;
        case "LOOP_OPTIONAL_START":
            if (isMarked(Flag.LOOP_OPTIONAL_START)) error("can't nest two of the same loop");
            else flags.mark(flag);
            
            break;
        case "LOOP_OPTIONAL_END":
            if (!isMarked(Flag.LOOP_OPTIONAL_START)) error("required starting '{'");
            else clearFlag(Flag.LOOP_OPTIONAL_START);
            
            break;
        case "LOOP_START":
            if (isMarked(Flag.LOOP_START)) error("can't nest two of the same loop");
            else flags.mark(flag);
            
            break;
        case "LOOP_END":
            if (!isMarked(Flag.LOOP_START)) error("required starting '['");
            else clearFlag(Flag.LOOP_START);
            
            break;
        case "NEXT":
            clearFlag(Flag.REQUIRES_NEXT);
            break;
        }
    }

    private void clearFlag(Flag flag) {
        flags.reset(flag);
    }

    private boolean isMarked(Flag flag) {
        return flags.isMarked(flag);
    }

    private void clearFlags() {
        this.flags.clear();
    }

    private enum Flag {
        NEXT, END, REQUIRES_NEXT, LOOP_OPTIONAL_START, LOOP_OPTIONAL_END, LOOP_START, LOOP_END, OPTIONAL_BLOCK_START, OPTIONAL_BLOCK_END, FULL_LOOP
    }
}
