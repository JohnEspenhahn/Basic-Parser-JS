package com.hahn.basic.lexer;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.hahn.basic.util.exceptions.CompileException;

public class Lexer {    
    private final Pattern LexRegex;
    
    private final IEnumToken[] Tokens;
    private boolean comment;
    
    /**
     * @param enumTokens An Enum that implements IEnumToken. The tokens to lex input to
     */
    public Lexer(Class<? extends IEnumToken> enumTokens) {
        // Compile tokens
        if (!enumTokens.isEnum()) throw new InvalidParameterException("Lexer tokens must be an Enum");
        Tokens = enumTokens.getEnumConstants();
        
        StringBuilder regex = new StringBuilder();
        regex.append("(\\s+)|(/\\*)|(\\*/)|(//)|");
        for (int i = 0; i < Tokens.length; i++) {
            String tRegex = Tokens[i].getRegex();
            
            for (EnumTokenShorthand shorthand: EnumTokenShorthand.values()) {
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
    
    public void reset() {
        this.comment = false;
    }
    
    public List<PackedToken> lex(String input, int row) {
        List<PackedToken> stream = new ArrayList<PackedToken>();
        
        int lastEnd = 0;
        
        Matcher matcher = LexRegex.matcher(input);
        
        while (matcher.find()) {
            // Ensure full match
            if (matcher.start() == lastEnd) {
                lastEnd = matcher.end();
            } else {
                // Go back some if last was <<WORD>> token
                if (stream.size() > 0) {
                    PackedToken lastToken = stream.get(stream.size() - 1);
                    String lastRegex = lastToken.token.getRegex(); 
                    
                    if (lastRegex.equals(EnumTokenShorthand.WORD.toString())) {
                        lastEnd -= lastToken.value.length();
                    }
                }
                
                throw new CompileException("Invalid token", lastEnd);
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
                        stream.add(new PackedToken(Tokens[g - 5], group, row, matcher.start()));
                    }
                    break;
                }
            }
        }

        // Ensure full match
        if (lastEnd != input.length()) {
            throw new CompileException("Invalid token", lastEnd); 
        }

        return stream;
    }
}
