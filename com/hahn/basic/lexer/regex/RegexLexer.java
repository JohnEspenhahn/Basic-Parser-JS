package com.hahn.basic.lexer.regex;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.hahn.basic.lexer.ILexer;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.util.exceptions.LexException;

public class RegexLexer implements ILexer {
    /** The pattern compiled from the provided tokens */
    private final Pattern LexRegex;
    
    /** The provided tokens */
    private final IEnumRegexToken[] Tokens;
    
    /** True if in a comment */
    private boolean comment;
    
    /**
     * Create a new regex lexer
     * @param enumTokens An Enum that implements IEnumRegexToken. The tokens to lex input to
     */
    public RegexLexer(Class<? extends IEnumRegexToken> enumTokens) {
        // Compile tokens
        if (!enumTokens.isEnum()) throw new InvalidParameterException("Lexer tokens must be an Enum");
        Tokens = enumTokens.getEnumConstants();
        
        StringBuilder regex = new StringBuilder();
        regex.append("(\\s+)|(/\\*)|(\\*/)|(//)|");
        for (int i = 0; i < Tokens.length; i++) {
            String tRegex = Tokens[i].getRegex();
            
            for (EnumRegexTokenShorthand shorthand: EnumRegexTokenShorthand.values()) {
                tRegex = tRegex.replace(shorthand.toString(), shorthand.Regex);
            }
            
            // Make all groups non capture
            tRegex = tRegex.replaceFirst("(?<!\\\\)\\((?!\\?:)", "(?:");
            
            // Check pattern
            try {
                tRegex = Pattern.compile(tRegex).pattern();
            } catch (PatternSyntaxException e) {
                System.err.println("Error compiling token regex `" + tRegex + "`");
                e.printStackTrace();
            } finally {
                regex.append("(" + tRegex + ")");
            }

            // 'Or' in next pattern
            if (i < Tokens.length - 1) { regex.append("|"); }
        }
        
        LexRegex = Pattern.compile(regex.toString());
        
        // Prepare this
        reset();
    }
    
    @Override
    public void reset() {
        this.comment = false;
    }
    
    @Override
    public List<PackedToken> lex(List<String> input) {
        List<PackedToken> stream = new ArrayList<PackedToken>();
        
        Iterator<String> it = input.iterator();
        for (int row = 1; it.hasNext(); row++) {
            String line = it.next();
            
            int lastEnd = 0;
            
            Matcher matcher = LexRegex.matcher(line);
            
            while (matcher.find()) {
                // Ensure full match
                if (matcher.start() == lastEnd) {
                    lastEnd = matcher.end();
                } else {
                    // Go back some if last was <<WORD>> token
                    if (stream.size() > 0) {
                        PackedToken lastToken = stream.get(stream.size() - 1);
                        String lastRegex = ((IEnumRegexToken) lastToken.token).getRegex(); 
                        
                        if (lastRegex.contains(EnumRegexTokenShorthand.WORD.toString())) {
                            lastEnd -= lastToken.value.length();
                        }
                    }
                    
                    throw new LexException(row, lastEnd);
                }
                
                // 2 For full capture, space
                for (int g = 2; g <= matcher.groupCount(); g++) {
                    String group = matcher.group(g);
                    if (group != null) {
                        // Comment block start
                        if (g == 2) {
                            comment = true;
                            
                        // Comment block end
                        } else if (g == 3) {
                            comment = false;
                        
                        // Single line comment
                        } else if (g == 4) {
                            return stream;
                            
                        // Code
                        } else if (!comment) {
                            stream.add(new PackedToken((Enum<?>) Tokens[g - 5], group, row, matcher.start()));
                        }
                        break;
                    }
                }
            }
    
            // Ensure full match
            if (lastEnd != line.length()) {
                throw new LexException(row, lastEnd); 
            }
        }

        return stream;
    }
}
