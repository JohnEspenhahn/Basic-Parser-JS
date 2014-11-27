package com.hahn.basic.intermediate;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.StructType.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class FuncHead extends Frame {
    private int flags;
    private final String name;
    private final Var[] params;
    private final boolean[] isOptional;
    
    private final Type rtnType;
    private boolean allChildrenReturn;
    
    private final String funcId;    
    private final ClassType classIn;
    private final boolean isConstructor;
    
    public FuncHead(Frame parent, ClassType classIn, String name, boolean rawName, Node funcHeadNode, Type rtn, Param... params) {
        super(parent, funcHeadNode); // TODO nest anon func
        
        if (rawName) {
            this.funcId = name;
        } else {
            this.funcId = createFuncId(name, params);
        }
        
        this.classIn = classIn;
        this.isConstructor = Util.isConstructorName(name);
        
        this.flags = 0;
        this.name = name;
        this.rtnType = rtn;
        this.allChildrenReturn = true;
        if (rtn == Type.VOID) flagHasReturn();
        
        this.params = new Var[params.length];
        this.isOptional = new boolean[params.length];
        for (int i = 0; i < params.length; i++) {
            Param p = params[i];
            
            Var var = LangCompiler.factory.VarParameter(this, p.getName(), p.getType(), p.getFlags());
            this.params[i] = var;
            this.isOptional[i] = false;
            
        	this.addVar(var);
        }
        
        // Add `this` and `super` variables if applicable
        if (classIn != null) {
            addVar(classIn.getThis());
            
            if (classIn.getParent() instanceof ClassType) {
                addVar(classIn.getSuper());
            }
        }
    }
    
    public void setFlags(int i) {
        this.flags = i;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public boolean hasFlag(BitFlag flag) {
        return (this.flags & flag.b) != 0;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isConstructor() {
        return isConstructor;
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
    
    @Override
    public ClassType getClassIn() {
        return classIn;
    }
    
    @Override
    public boolean hasReturn() {
        return this.allChildrenReturn || super.hasReturn();
    }
    
    @Override
    public BasicObject safeGetInstanceVar(String name) {
        if (getClassIn() != null) {
            StructParam param = classIn.getParamSafe(name);
            if (param != null) { 
                return LangCompiler.factory.VarAccess(this, getClassIn().getThis(), param, param.getType(), -1, -1);
            }
        }
        
        return null;
    }
    
    public void makeOptional(BasicObject p) {
        for (int i = 0; i < params.length; i++) {
            Var funcParam = params[i];
            if (funcParam == p) {
                isOptional[i] = true;
                return;
            }
        }
    }
    
    public boolean hasSameName(FuncHead func) {
        return getName().equals(func.getName());
    }
    
    public boolean hasSameReturn(FuncHead func) {
        return getReturnType().equals(func.getReturnType());
    }
    
    @Override
    public boolean reverseOptimize() {
        for (int i = params.length - 1; i >= 0; i--) {
            params[i].setInUse(this);
            params[i].decUses();
        }
        
        super.reverseOptimize();
        
        if (!hasReturn() && hasFrameHead()) {
            throw new CompileException("Function must return a result of type `" + getReturnType() + "`", getFrameHead());
        }
        
        return false;
    }
    
    @Override
    protected boolean optimize(Compilable a, Compilable b) {
        if (a.isBlock() && !a.hasReturn()) {
            this.allChildrenReturn = false;
        }
        
        return super.optimize(a, b);
    }
    
    @Override
    public boolean forwardOptimize() {
        for (int i = 0; i < params.length; i++) {
            params[i].takeRegister(this);
        }
        
        return super.forwardOptimize();
    }
    
    public abstract String toFuncAreaTarget();
    
    @Override
    public boolean isBlock() {
        return true;
    }
    
    @Override
    public String toString() {
        return "function " + getName() + "(" + StringUtils.join(params, ", ") + ") {" + super.toString() + "}"; 
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
        // TODO handle conditions when the given type is optional
        
        int pIdx, tIdx;
        for (pIdx = 0, tIdx = 0; pIdx < params.length && tIdx < types.length; pIdx++) {
            boolean match = (params[pIdx].getType().autocast(types[tIdx].getType(), -1, -1, false) != null);
            if (match) {
                tIdx += 1;
            } else if (isOptional[pIdx]) {
                continue;
            } else {
                return false;
            }
        }
        
        while (pIdx < params.length && isOptional[pIdx]) {
            pIdx += 1;
        }
        
        if (pIdx == params.length && tIdx == types.length) return true;
        else return false;
    }
    
    public static String createFuncId(String name, ITypeable... objs) {
        if (name.equals(EnumToken.SUPER.getString())) name = EnumToken.CONSTRUCTOR.getString();
        
        String funcID = name + "__";
        for (ITypeable o: objs) funcID += "_" + o.getType().getName();
        
        return funcID;
    }
    
    public static String toHumanReadable(FuncPointer p) {
        return toHumanReadable(p.getObjectIn(), p.getName(), p.getTypes(), p.getReturn());
    }
    
    public static String toHumanReadable(BasicObject objectIn, String name, ITypeable[] types, Type returnType) {
        String funcName = (objectIn != null ? objectIn.getName() + "::" : "");
        
        funcName += name + "<";
        for (int i = 0; i < types.length; i++) {
            funcName += types[i].getType();
            if (i + 1 < types.length) {
                funcName += ", ";
            }
        }
        
        return funcName + ";" + returnType + ">";
    }
}
