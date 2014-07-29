package com.hahn.basic.intermediate;

import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class FuncHead extends Frame {    
    private final String name;
    
    private final Param[] params;
    
    private final Type rtnType;
    
    private final String funcId;
    
    public FuncHead(String name, boolean rawName, Node funcHeadNode, Type rtn, Param... params) {
        super(LangCompiler.getGlobalFrame(), funcHeadNode);
        
        if (rawName) {
            this.funcId = name;
        } else {
            this.funcId = createFuncId(name, params);
        }
        
        this.rtnType = rtn;
        
        this.name = name;
        this.params = params;
        
        for (Param p: params) {
        	addVar(LangCompiler.factory.VarParameter(this, p.getName(), p.getType()));
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
    
    public Param[] getParams() {
        return params;
    }
    
    public boolean hasSameName(FuncHead func) {
        return this.name.equals(func.name);
    }
    
    public boolean hasSameReturnAs(FuncHead func) {
        return this.rtnType.equals(func.rtnType);
    }
    
    @Override
    public boolean endsWithBlock() {
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FuncHead) {
            FuncHead func = (FuncHead) obj;
            if (hasSameReturnAs(func) && numParams() == func.numParams()) {
                Param[] funcParams = func.getParams();
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
        String funcID = "func_" + name + "__";
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
