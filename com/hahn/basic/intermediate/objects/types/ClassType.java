package com.hahn.basic.intermediate.objects.types;

import java.util.Collection;
import java.util.List;

import com.hahn.basic.intermediate.FuncBridge;
import com.hahn.basic.intermediate.FuncGroup;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class ClassType extends StructType {
    private FuncBridge funcBridge;
    
    public ClassType(String name, ClassType parent) {
        super(name, parent);
        
        this.funcBridge = new FuncBridge(this);
    }
    
    /**
     * Extend this
     * @param name The name of the new class
     * @param ps The parameters added by this new class
     * @return A new class object
     */
    @Override
    public ClassType extendAs(String name, List<BasicObject> ps) {
        ClassType newClass = LangCompiler.factory.ClassType(name, this);
        newClass.loadVars(ps);
        
        return newClass;
    }
    
    public FuncHead defineFunc(Node head, String name, boolean rawName, Type rtnType, Param... params) {
        return funcBridge.defineFunc(LangCompiler.getGlobalFrame(), head, name, rawName, rtnType, params);
    }
    
    public Collection<FuncGroup> getDefinedFuncs() {
        return funcBridge.getFuncs();
    }
    
    /**
     * Get a function
     * @param nameNode The node that contains the requested name
     * @return The function
     * @throw CompileException If the requested function is not defined
     */
    public FuncHead getFunc(Node nameNode, ITypeable[] types) {
        return getFunc(nameNode, types, false);
    }
    
    /**
     * Get a function
     * @param nameNode The node that contains the requested name
     * @param safe If false can throw an exception
     * @return The function; or, if `safe` is true and there is an error, null
     * @throw CompileException If `safe` is false and the function is not defined
     */
    public FuncHead getFunc(Node nameNode, ITypeable[] types, boolean safe) {
        String name = nameNode.getValue();
        FuncHead func = funcBridge.getFunc(name, types);
        if (func != null) {
            return func;
        } else if (parent instanceof ClassType) {
            func =  ((ClassType) parent).getFunc(nameNode, types, true);
            if (func != null) return func;
        } 
        
        // If reached this point then not found
        if (!safe) {
            throw new CompileException("Unknown function `" + name + "` in " + this, nameNode);
        } else {
            return null;
        }
    }
    
    public abstract String toTarget();
}
