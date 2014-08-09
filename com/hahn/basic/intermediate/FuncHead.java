package com.hahn.basic.intermediate;

import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class FuncHead extends Frame {    
    private final String name;
    private final Var[] params;
    private final Type rtnType;
    
    private final String funcId;
    private final ClassType classIn;
    
    public FuncHead(Frame parent, ClassType classIn, String name, boolean rawName, Node funcHeadNode, Type rtn, Param... params) {
        super(parent, funcHeadNode); // TODO nest anon func
        
        if (rawName) {
            this.funcId = name;
        } else {
            this.funcId = createFuncId(name, params);
        }
        
        this.classIn = classIn;
        
        this.name = name;
        this.rtnType = rtn;
        
        this.params = new Var[params.length];
        for (int i = 0; i < params.length; i++) {
            Param p = params[i];
            
            Var var = LangCompiler.factory.VarParameter(this, p.getName(), p.getType(), p.getFlags());
            this.params[i] = var;
        	this.addVar(var);
        }
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Gets the ID of this function
     * @return name_param1_param2_etc
     */
    public String getFuncId() {
        return funcId;
    }
    
    public Type getReturnType() {
        return rtnType;
    }
    
    public int numParams() {
        return params.length;
    }
    
    public Var[] getParams() {
        return params;
    }
    
    public ClassType getClassIn() {
        return classIn;
    }
    
    public boolean hasSameName(FuncHead func) {
        return getName().equals(func.getName());
    }
    
    public boolean hasSameReturn(FuncHead func) {
        return getReturnType().equals(func.getReturnType());
    }
    
    public abstract String toFuncAreaTarget();
    
    @Override
    public boolean endsWithBlock() {
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FuncHead) {
            FuncHead func = (FuncHead) obj;
            if (hasSameReturn(func) && numParams() == func.numParams()) {
                Var[] funcParams = func.getParams();
                for (int i = 0; i < params.length; i++) {
                    if (!params[i].getType().equals(funcParams[i].getType())) {
                        return false;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }

    public boolean matches(ITypeable[] types) {
        if (params.length == types.length) {
            for (int i = 0; i < params.length; i++) {
                if (!params[i].getType().equals(types[i].getType())) {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    public static String createFuncId(String name, ITypeable... objs) {
        String funcID = "__" + name + "__";
        for (ITypeable o: objs) funcID += "_" + o.getType().getName();
        
        return funcID;
    }
    
    public static String toHumanReadable(FuncPointer p) {
        return toHumanReadable(p.getName(), p.getTypes(), p.getReturn());
    }
    
    public static String toHumanReadable(String name, ITypeable[] types, Type returnType) {
        String funcName = name + "::<";
        for (int i = 0; i < types.length; i++) {
            funcName += types[i].getType();
            if (i + 1 < types.length) {
                funcName += ", ";
            }
        }
        
        return funcName + ";" + returnType + ">";
    }
}
