package com.hahn.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.lexer.ILexer;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.lexer.regex.IEnumRegexToken;
import com.hahn.basic.parser.IEnumExpression;
import com.hahn.basic.parser.Node;
import com.hahn.basic.parser.Parser;
import com.hahn.basic.target.ILangFactory;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.exceptions.CompileException;

public class BASICMain extends Main {
    public static final String VERSION = "1.5.0";
    
    private final ILexer lexer;
    private final Parser parser;
    private final ILangFactory factory;
    
    /** The stream of lexed tokens */
    private List<PackedToken> stream;

    public BASICMain(ILangFactory factory, ILexer lexer, Class<? extends IEnumRegexToken> tokens, Class<? extends IEnumExpression> expressions) {
        System.out.println("BASIC Parser v" + VERSION);
        
        this.factory = factory;
        
        // Create handlers
        this.lexer = lexer;
        this.parser = new Parser(tokens, expressions);
    }
    
    @Override
    public LangBuildTarget getLangBuildTarget() {
        return this.factory.getLangBuildTarget();
    }
    
    @Override
    public void printShellTitle() {
        System.out.println("Basic shell started");
    }

    @Override
    public void handleTermInput() {
        long start = System.currentTimeMillis();
        
        // Reset
        lexer.reset();
        
        // Parse
        lexLines();
        handleStream();
        
        System.out.println();
        System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");
    }
    
    @Override
    public void handleFileInput() {
        lexLines();
        handleStream();
    }
    
    private void lexLines() {
        stream = lexer.lex(getLines());
    }

    private void handleStream() {
        try {
            Node tree_head = parse();
            
            if (tree_head != null) {
                tree_head.printFormattedInput();
                
                // Print
                if (DEBUG) {
                    tree_head.print();
                    System.out.println();
                }
                
                compile(tree_head);
            } else {
                System.err.println("Empty input source");
            }
        } catch (CompileException e) {
            printCompileException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stream = null;
        }
    }

    private Node parse() {
        if (stream == null) return null;
        
        return parser.parse(stream.toArray(new PackedToken[stream.size()]));
    }
    
    private void compile(Node tree_head) {
        LangBuildTarget code = LangCompiler.compile(tree_head, factory);
        
        if (inputFile != null) {
            writeToFile(code, getTargetFile()); 
        } else {
            System.out.println(code.toString());
        }
    }
    
    private void writeToFile(LangBuildTarget code, File targ) {
        FileOutputStream os = null;
        try {
            targ.delete();
            targ.createNewFile();
            
            os = new FileOutputStream(targ);
            
            code.writeRunnableTo(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
