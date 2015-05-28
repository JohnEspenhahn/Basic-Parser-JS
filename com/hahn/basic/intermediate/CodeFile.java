package com.hahn.basic.intermediate;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hahn.basic.intermediate.function.FuncGroup;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.lexer.ILexer;
import com.hahn.basic.lexer.ILexerFactory;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.parser.Node;
import com.hahn.basic.parser.Parser;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.target.OutputBuilder;
import com.hahn.basic.target.js.JSPretty;

public class CodeFile {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\n");
    
    private final Compiler compiler;
    private final String fullText;
    private final List<String> lines;
    private final Stack<Integer> rows, columns;
    
    private final OutputBuilder output;
    
    private Node node;
    private Frame frame;
    
    protected CodeFile(Compiler compiler, String input, OutputBuilder output, ILexerFactory lexerFactory, Parser parser) {
        this.compiler = compiler;
        this.fullText = input;
        this.output = output;
        this.lines = new ArrayList<String>();
        this.rows = new Stack<Integer>();
        this.columns = new Stack<Integer>();
        
        parse(lexerFactory.newInstance(this), parser);
    }
    
    private void parse(ILexer lexer, Parser parser) {
        // Get lines
        Matcher m = NEWLINE_PATTERN.matcher(fullText);
        
        int idx = 0;
        while (m.find()) {
            addLine(fullText.substring(idx, m.end()));
            idx = m.end();
        }
        
        // Last part without new line
        if (idx != fullText.length() - 1) {
            addLine(fullText.substring(idx));
        }
        
        // Lex
        List<PackedToken> tokens = lexer.lex();
        PackedToken[] tokensArr = tokens.toArray(new PackedToken[tokens.size()]);
        
        // Parse
        node = parser.parse(this, tokensArr);
        
        // Debug
        if (compiler.getStatus().isDebugging()) {
            node.print();
            System.out.println();
        }
    }
    
    public void startCompiling() {
        frame = new Frame(this, null, node);
        frame.addTargetCode();
        
        // //////////////////////
        // Reverse optimize
        // //////////////////////
        for (FuncGroup funcGroup : compiler.getFuncs()) {
            for (FuncHead func : funcGroup) {
                if (func.hasFrameHead()) {
                    func.addTargetCode();
                    func.reverseOptimize();
                }
            }
        }
        
        ListIterator<Type> classIt = Type.getPublicTypes().listIterator(Type.getPublicTypes().size());
        while (classIt.hasPrevious()) {
            classIt.previous().reverseOptimize();
        }
        
        frame.reverseOptimize();
    }
    
    public void finishCompiling() {
        if (frame == null) throw new RuntimeException("Tried to finish compiling a file before starting compiling!");
        
        // //////////////////////
        // Forward optimize
        // //////////////////////
        frame.forwardOptimize();
        
        for (FuncGroup funcGroup : compiler.getFuncs()) {
            for (FuncHead func : funcGroup) {
                if (func.hasFrameHead()) {
                    func.forwardOptimize();
                }
            }
        }
        
        ListIterator<Type> classIt = Type.getPublicTypes().listIterator();
        while (classIt.hasNext()) {
            classIt.next().forwardOptimize();
        }
        
        // //////////////////////
        // Start builder text
        // //////////////////////
        output.appendString(output.getStart());
        
        // Put library import strings
        for (Library lib : compiler.getLibraries()) {
            output.appendString(lib.toTarget());
        }
        
        // Compile class area
        output.appendString(output.getContentStart());
        for (Type t : Type.getPublicTypes()) {
            output.appendString(t.toTarget());
        }
        
        // Convert code to target
        output.appendString(frame.toTarget());
        output.appendString(output.getCodeEnd());
        
        // Convert functions to target
        for (FuncGroup funcGroup : compiler.getFuncs()) {
            for (FuncHead func : funcGroup) {
                if (func.hasFrameHead()) {
                    output.appendString(func.toFuncAreaTarget());
                    output.appendString(JSPretty.format("^"));
                }
            }
        }
        
        output.appendString(output.getContentEnd());
        output.appendString(output.getEnd());
    }
    
    public Compiler getCompiler() {
        return compiler;
    }
    
    public CommandFactory getFactory() {
        return getCompiler().getFactory();
    }
    
    public OutputBuilder getOutputBuilder() {
        return output;
    }
    
    @Override
    public String toString() {
        if (output == null) return "<<compiling...>>";
        else return output.toString();
    }
    
    public String getFullText() {
        return fullText;
    }
    
    public String getLine(int row) {
        return lines.get(row);
    }
    
    public void pushCurrentLine(int row) {
        pushCurrentPoint(row, -1);
    }
    
    public void pushCurrentPoint(int row, int col) {
        rows.push(row);
        columns.push(col);
    }
    
    public void popCurrentPoint() {
        if (!rows.isEmpty()) {
            rows.pop();
            columns.pop();
        }
    }
    
    public String getCurrentLine() {
        return getLine(getCurrentRow());
    }
    
    public int getCurrentRow() {
        return (rows.isEmpty() ? 0 : rows.peek());
    }
    
    public int getCurrentColumn() {
        return (columns.isEmpty() ? -1 : columns.peek());
    }
    
    protected void addLine(String line) {
        lines.add(line);
    }
}
