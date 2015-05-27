package com.hahn.basic.intermediate;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import com.hahn.basic.intermediate.function.FuncBridge;
import com.hahn.basic.intermediate.function.FuncGroup;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPair;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.VarTemp;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.target.OutputBuilder;
import com.hahn.basic.target.js.JSPretty;

public class Compiler {    
    private static Map<String, Library> libs = new HashMap<String, Library>();
    private static Map<String, Integer> labels = new HashMap<String, Integer>();
    private static Map<String, StringConst> strings = new HashMap<String, StringConst>();
    
    public static CommandFactory factory;
    private static Frame globalFrame, frame;
    
    private static FuncBridge funcBridge;

    public static OutputBuilder compile(Node h, CommandFactory f) {
        globalFrame = frame = new Frame(null, h);
        factory = f;
        
        // Reset
        reset();
        
        // Init
        factory.reset();
        OutputBuilder builder = factory.getBuildTarget();
        
        frame.addTargetCode();
        
        ListIterator<Type> classIt = Type.getPublicTypes().listIterator(Type.getPublicTypes().size());
        
        ////////////////////////
        // Reverse optimize
        ////////////////////////
        for (FuncGroup funcGroup: funcBridge.getFuncs()) {
            for (FuncHead func: funcGroup) {
                if (func.hasFrameHead()) {
                    func.addTargetCode();
                    func.reverseOptimize();
                }
            }
        }

        while (classIt.hasPrevious()) classIt.previous().reverseOptimize();
        
        frame.reverseOptimize();
        
        ////////////////////////
        // Forward optimize
        ////////////////////////
        frame.forwardOptimize();
        
        for (FuncGroup funcGroup: funcBridge.getFuncs()) {
            for (FuncHead func: funcGroup) {
                if (func.hasFrameHead()) {
                    func.forwardOptimize();
                }
            }
        }
        
        while (classIt.hasNext()) classIt.next().forwardOptimize();
        
        ////////////////////////
        // Start builder text
        ////////////////////////
        builder.appendString(builder.getStart());
        
        // Put library import strings
        for (Library lib: libs.values()) {
            builder.appendString(lib.toTarget());
        }
        
        // Compile class area
        builder.appendString(builder.getContentStart());
        for (Type t: Type.getPublicTypes()) {
            builder.appendString(t.toTarget());
        }
        
        // Convert code to target
        builder.appendString(frame.toTarget());
        builder.appendString(builder.getCodeEnd());
        
        // Convert functions to target
        for (FuncGroup funcGroup: funcBridge.getFuncs()) {
            for (FuncHead func: funcGroup) {
                if (func.hasFrameHead()) {                    
                    builder.appendString(func.toFuncAreaTarget());
                    builder.appendString(JSPretty.format("^"));
                }
            }
        }
        
        builder.appendString(builder.getContentEnd());        
        builder.appendString(builder.getEnd());
        
        return builder;
    }
    
    private static void reset() {
        VarTemp.NEXT_TEMP_VAR = 0;
        Type.reset();
        
        funcBridge = new FuncBridge(null);
        
        strings.clear();
        labels.clear();
        libs.clear();
    }
    
    public static String getLabel(String name, Frame f) {
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
    
    public static void addLibrary(Node node, String name) {        
        if (!libs.containsKey(name)) {
            Library lib = Library.getLib(node, name); 
            libs.put(name, lib);
            
            lib.define();
        }
    }
    
    /**
     * Convert a string to a string constant
     * @param str The string to get (does not need inner parenthesis)
     * @return StringConst
     */
    public static StringConst getString(String str) {
    	StringConst var = strings.get(str);
    	if (var != null) {
    		return var;
    	} else {
    		StringConst strConst = Compiler.factory.StringConst(str);
    		strings.put(str, strConst);
    		
    		return strConst;
    	}
    }
    
    /**
     * Convert a regex to a string constant
     * @param str The string to get (does not need inner parenthesis)
     * @return StringConst
     */
    public static StringConst getRegex(String str) {
       return getString(str.replace("\\", "\\\\"));
    }
    
    public static FuncHead defineFunc(String inName, String outName, Type rtnType, Param... params) {
        return Compiler.defineFunc(getGlobalFrame(), null, inName, outName, rtnType, params);
    }
    
    public static FuncHead defineFunc(Frame parent, Node head, String inName, String outName, Type rtnType, Param... params) {
        return funcBridge.defineFunc(parent, false, head, inName, outName, rtnType, params);
    }
    
    public static FuncCallPair getFunc(BasicObject objIn, Node nameNode, ITypeable[] types) {
        if (objIn != null && objIn.getType() instanceof ClassType) {            
            // If object in is implied this, check for global function first
            if (objIn.getVarThisFlag() == Var.IS_IMPLIED_THIS) {
                FuncCallPair funcPair = getGlobalFunc(nameNode, types);
                
                // If found in global frame, return that
                if (funcPair != null) {
                    return funcPair;
                }
            }
            
            // If no global function by this name, check `this`
            FuncHead func = ((ClassType) objIn.getType()).getFunc(objIn, nameNode, types);
            return new FuncCallPair(func, objIn);
        } else {
            return getGlobalFunc(nameNode, types);
        }
    }
    
    public static FuncCallPair getGlobalFunc(Node nameNode, ITypeable[] types) {
        String name = nameNode.getValue();
        FuncHead func = funcBridge.getFunc(name, types);
        
        if (func != null) return new FuncCallPair(func, null);
        else return null;
    }
    
    public static Frame getGlobalFrame() {
    	return globalFrame;
    }
}
