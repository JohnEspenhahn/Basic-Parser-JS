package com.hahn.basic.intermediate;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.VarTemp;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.ILangFactory;
import com.hahn.basic.target.LangBuildTarget;

public class LangCompiler {    
    private static Map<String, Library> libs = new HashMap<String, Library>();
    private static Map<String, Integer> labels = new HashMap<String, Integer>();
    private static Map<String, StringConst> strings = new HashMap<String, StringConst>();
    
    public static ILangFactory factory;
    private static Frame globalFrame, frame;
    
    private static FuncBridge funcBridge;

    public static LangBuildTarget compile(Node h, ILangFactory f) {
        globalFrame = frame = new Frame(null, h);
        factory = f;
        
        // Reset
        reset();
        
        // Init
        LangBuildTarget builder = factory.getLangBuildTarget(); 
        builder.init();
        
        // Optimize
        frame.reverseOptimize();
        frame.forwardOptimize();
        
        // Optimize classes
        ListIterator<Type> it = Type.getPublicTypes().listIterator(Type.getPublicTypes().size());
        while (it.hasPrevious()) it.previous().reverseOptimize();
        while (it.hasNext()) it.next().forwardOptimize();
        
        // Compile class area
        builder.appendString(builder.startClassArea());
        for (Type t: Type.getPublicTypes()) {
            builder.appendString(t.toTarget());
        }
        
        // Convert to target
        builder.appendString(frame.toTarget());
        builder.appendString(builder.endCodeArea());
        
        // Compile functions
        for (FuncGroup funcGroup: funcBridge.getFuncs()) {
            for (FuncHead func: funcGroup) {
                if (func.hasFrameHead()) {
                    func.reverseOptimize();
                    func.forwardOptimize();
                    
                    builder.appendString(func.toFuncAreaTarget());
                }
            }
        }
        builder.appendString(builder.endFuncArea());
        
        // Put library import strings
        for (Library lib: libs.values()) {
            builder.appendString(lib.toTarget());
        }
        
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
    
    public static void addLibrary(String name) {        
        if (!libs.containsKey(name)) {
            Library lib = Library.getLib(name); 
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
    		StringConst strConst = LangCompiler.factory.StringConst(str);
    		strings.put(str, strConst);
    		
    		return strConst;
    	}
    }
    
    public static FuncHead defineFunc(String name, boolean rawName, Type rtnType, Param... params) {
        return LangCompiler.defineFunc(getGlobalFrame(), null, name, rawName, rtnType, params);
    }
    
    public static FuncHead defineFunc(Frame parent, Node head, String name, boolean rawName, Type rtnType, Param... params) {
        return funcBridge.defineFunc(parent, head, name, rawName, rtnType, params);
    }
    
    public static FuncHead getFunc(BasicObject objIn, Node nameNode, ITypeable[] types) {
        if (objIn != null && objIn.getType() instanceof ClassType) {
            return ((ClassType) objIn.getType()).getFunc(objIn, nameNode, types);
        } else {
            String name = nameNode.getValue();
            return funcBridge.getFunc(name, types);
        }
    }
    
    public static Frame getGlobalFrame() {
    	return globalFrame;
    }
}
