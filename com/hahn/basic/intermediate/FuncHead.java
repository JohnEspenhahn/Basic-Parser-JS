package com.hahn.basic.intermediate;

import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.Label;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.StoreRegsStatement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.asm.raw.ASMLabel;

public class FuncHead extends Frame {    
    private final String name;
    
    private final Param[] params;
    
    private final Type rtnType;
    
    private final Label funcId;
    private final StoreRegsStatement storeRegs;
    
    public FuncHead(String name, Node funcHeadNode, Type rtn, Param... params) {
        super(null, funcHeadNode);
        
        this.funcId = new Label(createFuncId(name, params));
        this.rtnType = rtn;
        
        this.name = name;
        this.params = params;
        
        this.storeRegs = new StoreRegsStatement(this, params);
    }
    
    public int getRegsStored() {
        return storeRegs.getRegsStored();
    }
    
    public AdvancedObject[] getCurrentVars() {
        return storeRegs.getCurrentVars();
    }
    
    @Override
    protected void trackVar(AdvancedObject var) {
        if (storeRegs != null) {
            storeRegs.addVar(var);
        }
        
        super.trackVar(var);
    }
    
    /**
     * Gets the ID of this function
     * @return name_param1_param2_etc
     */
    public Label getFuncId() {
        return funcId;
    }
    
    public Type getReturnType() {
        return rtnType;
    }
    
    public Label getReturnLabel() {
        return new Label(funcId + "_rtn");
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
    public void addTargetCode() {
        addCode(new ASMLabel(getFuncId()));
        addCode(storeRegs);
        
        super.addTargetCode();
        
        doReturn(null);
    }
    
    @Override
    public boolean reverseOptimize() {
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {        
        return false;
    }
    
    @Override
    public void toTarget(LangBuildTarget builder) {
        super.reverseOptimize();
        super.forwardOptimize();
        
        storeRegs.doStore();
        
        super.toTarget(builder);
        
        storeRegs.clearStore();
    }
    
    public String getName() {
        return name;
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
        String funcID = "@func_" + name;
        for (ITypeable o: objs) funcID += "@" + o.getType().getName();
        
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
