package com.hahn.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.CompilerStatus;
import com.hahn.basic.lexer.ILexerFactory;
import com.hahn.basic.lexer.regex.IEnumRegexToken;
import com.hahn.basic.parser.IEnumExpression;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.target.OutputBuilder;
import com.hahn.basic.target.OutputBuilderFactory;

public class KavaMain extends Main {
    public static final String VERSION = "3.0.0";
    
    private final CompilerStatus status;
    
    private final CommandFactory factory;
    private final ILexerFactory lexerFactory;
    private final Class<? extends IEnumRegexToken> tokens;
    private final Class<? extends IEnumExpression> expressions;
    private final OutputBuilderFactory outputFactory;
    
    private Compiler compiler;

    public KavaMain(CommandFactory factory, ILexerFactory lexerFactory, Class<? extends IEnumRegexToken> tokens, Class<? extends IEnumExpression> expressions, OutputBuilderFactory outputFactory) {
        System.out.println("Kava Parser v" + VERSION);
        
        this.factory = factory;
        this.lexerFactory = lexerFactory;
        this.tokens = tokens;
        this.expressions = expressions;
        this.outputFactory = outputFactory;
        
        this.status = new CompilerStatus();
    }
    
    @Override
    public CommandFactory getCommandFactory() {
        return factory;
    }
    
    @Override
    public CompilerStatus getCompilerStatus() {
        return status;
    }
    
    @Override
    public void printShellTitle() {
        System.out.println("Kava shell started");
    }
    
    @Override
    public String handleInput(String input) {
        compiler = new Compiler(factory, lexerFactory, tokens, expressions, outputFactory, status);
        CodeFile file = compiler.compile(input);

        if (getInputFile() != null) writeToFile(file.getOutputBuilder());
        return file.toString();
    }
    
    private void writeToFile(OutputBuilder code) {
        File targ = getTargetFile();
        
        FileOutputStream os = null;
        try {
            targ.delete();
            targ.createNewFile();
            
            os = new FileOutputStream(targ);
            
            code.writeRunnableTo(os, ENCODING);
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
