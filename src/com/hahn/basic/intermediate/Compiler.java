package com.hahn.basic.intermediate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hahn.basic.intermediate.function.FuncBridge;
import com.hahn.basic.intermediate.function.FuncGroup;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPair;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.lexer.ILexerFactory;
import com.hahn.basic.lexer.regex.IEnumRegexToken;
import com.hahn.basic.parser.IEnumExpression;
import com.hahn.basic.parser.Node;
import com.hahn.basic.parser.Parser;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.target.OutputBuilderFactory;

public class Compiler {    
    private final CompilerStatus status;
    
    private final Map<String, Library> libs;
    private final Map<String, Integer> labels;
    private final Map<String, StringConst> strings;
    
    private final ILexerFactory lexerFactory;
    private final Parser parser;
    
    private final CommandFactory factory;
    private final OutputBuilderFactory outputFactory;
    
    private final FuncBridge funcBridge;
    
    private final CodeFile globalFile;
    private final Frame globalFrame;
    private final Node globalNode;
    
    public Compiler(CommandFactory factory, ILexerFactory lexerFactory, Class<? extends IEnumRegexToken> tokens, Class<? extends IEnumExpression> expressions, OutputBuilderFactory outputFactory, CompilerStatus status) {
        this.status = status;
        
        this.lexerFactory = lexerFactory;
        this.parser = new Parser(tokens, expressions);
        this.factory = factory;
        
        this.outputFactory = outputFactory;
        
        this.libs = new HashMap<String, Library>();
        this.labels = new HashMap<String, Integer>();
        this.strings = new HashMap<String, StringConst>();
        
        this.funcBridge = new FuncBridge(null);
        
        // Globals
        this.globalFile = createCodeFile("");
        this.globalNode = Node.newTopNode(globalFile);
        this.globalFrame = new Frame(globalFile, null, null);
        
        Type.setupSystemTypes(this);
        
        // XXX Add global libraries
        addLibrary(getGlobalNode(), "kava.language.JS");
    }
    
    public CodeFile createCodeFile(String input) {
        return new CodeFile(this, input, outputFactory.newInstance(this), lexerFactory, parser);
    }
    
    public CompilerStatus getStatus() {
        return status;
    }
    
    public CodeFile compile(String input) {
        CodeFile file = createCodeFile(input);
        file.startCompiling();
        file.finishCompiling();
        
        return file;
    }
    
    public synchronized String getLabel(String name, Frame f) {
        if (f instanceof FuncHead) {
            name = ((FuncHead) f).getFuncId().toString() + name;
        }
        
        Integer idx = labels.get(name);
        if (idx == null) {
            labels.put(name, 1);
            return name + "_0";
        } else {
            labels.put(name, idx + 1);
            return name + "_" + idx;
        }
    }
    
    public synchronized void addLibrary(Node node, String name) {
        if (!libs.containsKey(name)) {
            Library lib = Library.getLib(node, name);
            libs.put(name, lib);
            
            lib.define(node.getFile());
        }
    }
    
    /**
     * Convert a string to a string constant
     * 
     * @param str The string to get (does not need inner parenthesis)
     * @return StringConst
     */
    public synchronized StringConst getString(String str) {
        StringConst var = strings.get(str);
        if (var != null) {
            return var;
        } else {
            StringConst strConst = factory.StringConst(str);
            strings.put(str, strConst);
            
            return strConst;
        }
    }
    
    /**
     * Convert a regex to a string constant
     * 
     * @param str The string to get (does not need inner parenthesis)
     * @return StringConst
     */
    public synchronized StringConst getRegex(String str) {
        return getString(str.replace("\\", "\\\\"));
    }
    
    public synchronized FuncHead defineFunc(CodeFile file, String inName, String outName, Type rtnType, Param... params) {
        return defineFunc(file, getGlobalFrame(), null, inName, outName, rtnType, params);
    }
    
    public synchronized FuncHead defineFunc(CodeFile file, Frame parent, Node head, String inName, String outName, Type rtnType, Param... params) {
        return funcBridge.defineFunc(file, parent, false, head, inName, outName, rtnType, params);
    }
    
    public synchronized FuncCallPair getFunc(IBasicObject objIn, Node nameNode, ITypeable[] types) {
        if (objIn != null && objIn.getType() instanceof ClassType) {
            // If object in is implied this, check for global function first
            if (objIn.getVarThisFlag() == Var.IS_IMPLIED_THIS) {
                FuncCallPair funcPair = getGlobalFunc(nameNode, types);
                
                // If found in global frame, return that
                if (funcPair != null) { return funcPair; }
            }
            
            // If no global function by this name, check `this`
            FuncHead func = ((ClassType) objIn.getType()).getFunc(objIn, nameNode, types);
            return new FuncCallPair(func, objIn);
        } else {
            return getGlobalFunc(nameNode, types);
        }
    }
    
    public synchronized FuncCallPair getGlobalFunc(Node nameNode, ITypeable[] types) {
        String name = nameNode.getValue();
        FuncHead func = funcBridge.getFunc(name, types);
        
        if (func != null) return new FuncCallPair(func, null);
        else return null;
    }
    
    public CommandFactory getFactory() {
        return factory;
    }
    
    public Frame getGlobalFrame() {
        return globalFrame;
    }
    
    public CodeFile getGlobalFile() {
        return globalFile;
    }
    
    public Node getGlobalNode() {
        return globalNode;
    }
    
    public Collection<FuncGroup> getFuncs() {
        return funcBridge.getFuncs();
    }
    
    public Collection<Library> getLibraries() {
        return libs.values();
    }
}
