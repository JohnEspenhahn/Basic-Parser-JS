package com.hahn.basic.intermediate.function;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.Frame;
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
import com.hahn.basic.util.ConstructorUtils;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.structures.BitFlag;

public abstract class FuncHead extends Frame {
    private int flags;
    private final String name;
    private final Var[] params;
    private final boolean[] isOptional;
    
    private final Type rtnType;
    private boolean allChildrenReturn;
    
    private final String funcId;    
    private final ClassType classIn;
    
    /**
     * A function head defines a function header
     * 
     * @param parent Containing frame
     * @param classIn Containing class
     * @param inName The name for this language
     * @param outName The name for the target language or null to create function id
     * @param funcHeadNode The node of the function
     * @param rtn The return type of the function
     * @param params The parameters for the function
     */
    public FuncHead(Frame parent, ClassType classIn, String inName, String outName, Node funcHeadNode, Type rtn, Param... params) {
        super(parent, funcHeadNode, true); // TODO nest anon func
        
        if (outName != null) {
            this.funcId = outName;
        } else {
            this.funcId = createFuncId(inName, params);
        }
        
        this.classIn = classIn;
        
        this.flags = 0;
        this.name = inName;
        this.rtnType = rtn;
        this.allChildrenReturn = true;
        if (rtn == Type.VOID) flagHasReturn();
        
        this.params = new Var[params.length];
        this.isOptional = new boolean[params.length];
        for (int i = 0; i < params.length; i++) {
            Param p = params[i];
            
            Var var = Compiler.factory.VarParameter(this, p.getName(), p.getType(), p.getFlags());
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
        return ConstructorUtils.isConstructorName(getName());
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
    
    /**
     * Get all the variables used in this function that
     * were defined externally
     * @return List of variables
     */
    public List<BasicObject> getVarsDefExternal() {
        return endLoop.getVars();
    }
    
    @Override
    public BasicObject safeGetInstanceVar(String name) {
        if (getClassIn() != null) {
            StructParam param = classIn.getParamSafe(name);
            if (param != null) { 
                return Compiler.factory.VarAccess(this, getClassIn().getThis(), param, param.getType(), -1, -1);
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
        return String.format("%s%s %s(%s)", BitFlag.asString(flags), getReturnType(), getName(), StringUtils.join(params, ", ")); 
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
    
    public int getMatchDepth(ITypeable[] types) {
        int[] depth = new int[types.length];
        
        int pIdx, tIdx;
        for (pIdx = 0, tIdx = 0; pIdx < params.length && tIdx < types.length; pIdx++) {
            Type expectedType = params[pIdx].getType();
            Type givenType = types[tIdx].getType();
            
            int extendDepth;
            if ((extendDepth = givenType.getExtendDepth(expectedType)) >= 0) {
                depth[tIdx] = extendDepth;
                tIdx += 1;
                
            } else if (isOptional[pIdx]) {
                continue;
                
            } else {
                return -1;
            }
        }
        
        while (pIdx < params.length && isOptional[pIdx]) {
            pIdx += 1;
        }
        
        if (pIdx == params.length && tIdx == types.length) return Arrays.stream(depth).sum();
        else return -1;
    }
    
    public static String createFuncId(String name, ITypeable... objs) {
        if (name.equals(EnumToken.SUPER.getString())) name = EnumToken.CONSTRUCTOR.getString();
        
        String funcID = name + "__";
        for (ITypeable o: objs) funcID += "_" + o.getType().getFuncIdName();
        
        return funcID;
    }
    
    public static String toHumanReadable(FuncPointer p) {
        return toHumanReadable(p.getObjectIn(), p.getName(), p.getTypes(), p.getReturn());
    }
    
    public static String toHumanReadable(BasicObject objectIn, String name, ITypeable[] types, Type returnType) {
        String funcName = (objectIn != null ? objectIn.getName() + "." : "");
        
        funcName += name + "(";
        for (int i = 0; i < types.length; i++) {
            funcName += types[i].getType();
            if (i + 1 < types.length) {
                funcName += ", ";
            }
        }
        
        return funcName + ")"; 
    }
}
