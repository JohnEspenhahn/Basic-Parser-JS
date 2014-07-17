package com.hahn.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.lexer.IEnumToken;
import com.hahn.basic.lexer.Lexer;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.parser.IEnumExpression;
import com.hahn.basic.parser.Node;
import com.hahn.basic.parser.Parser;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.LangFactory;
import com.hahn.basic.util.CompileException;

public class BASICMain extends Main {
    public static final String VERSION = "1.0.0";
    
    private final Lexer lexer;
    private final Parser parser;
    private final LangFactory factory;
    
    private List<PackedToken> stream;

    public BASICMain(LangFactory factory, Class<? extends IEnumToken> tokens, Class<? extends IEnumExpression> expressions) {
        System.out.println("BASIC Parser v" + VERSION);
        
        this.factory = factory;
        
        // Create handlers
        this.lexer = new Lexer(tokens);
        this.parser = new Parser(tokens, expressions);
    }

    @Override
    public void handleTermInput(String input) {
        long start = System.currentTimeMillis();
        
        // Reset
        lexer.reset();
        
        // Parse
        lexLineToStream(input, 1);
        handleStream();
        
        System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");
    }
    
    @Override
    public void handleFileLine(String str, int line) {
        lexLineToStream(str, line);
    }
    
    private void lexLineToStream(String str, int line) {
        List<PackedToken> added = lexer.lex(str, line);
        
        // Add to stream
        if (stream == null) stream = added;
        else stream.addAll(added);
    }
    
    @Override
    public void handleFileReadComplete() {
        handleStream();
    }

    private void handleStream() {
        try {
            Node tree_head = parse();
            
            if (tree_head != null) {                
                // Print
                if (DEBUG) {
                    tree_head.print();
                    System.out.println();
                }
                
                String result = compile(tree_head);
                System.out.println(result);
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
        
        return parser.parse(stream.toArray(new PackedToken[0]));
    }
    
    private String compile(Node tree_head) {
        LangBuildTarget code = Compiler.compile(tree_head, factory);
        
        if (inputFile != null) {
            writeToFile(code, new File(inputFile.getAbsolutePath() + ".bin")); 
        } else if (DEBUG) {
            code.printBytecode();
        }
        
        return code.toString();
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
