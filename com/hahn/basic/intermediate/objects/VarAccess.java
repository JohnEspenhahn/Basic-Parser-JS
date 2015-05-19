package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.StructType.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.structures.BitFlag;

public abstract class VarAccess extends BasicObject {
    private final BasicObject var, index;
    private BasicObject accessed;
    
    /**
     * Access a property of a var at a given index
     * @param container Containing statement
     * @param var The var to access
     * @param index The index of a property to access. Can be either a literal, another variable, or a struct param
     * @param type The type of the property at the given index
     * @param row Row to throw error at
     * @param col Column to throw error at
     */
    public VarAccess(Statement container, BasicObject var, BasicObject index, Type type, int row, int col) {
        super(var.getName() + "[" + index.getName() + "]", type);
        
        this.var = var;
        this.index = index;
        
        if (getAccessedAtIdx() instanceof StructParam) {
            this.accessed = var.getType().getAsStruct().getParamSafe(index.getName());
        }
        
        if (getAccessedWithinVar().isVarSuper()) {
            throw new CompileException("Only functions are accessable via the 'super' keyword", row, col);
        }
    }
    
    @Override
    public BasicObject getAccessedWithinVar() {
        return var;
    }
    
    @Override
    public BasicObject getAccessedAtIdx() {
        return index;
    }
    
    /**
     * If it can be determined gets the actual object that is being accessed
     * @return The actual object being accessed, or null if it cannot be determined
     */
    @Override
    public final BasicObject getAccessedObject() {
        return accessed;
    }
    
    /*
     * ------------------------------- Variable Management -------------------------------
     */
    
    @Override
    public boolean hasFlag(BitFlag flag) {
        if (accessed != null) return accessed.hasFlag(flag);
        else return false;
    }
    
    @Override
    public int getFlags() {
        if (accessed != null) return accessed.getFlags();
        else return 0;
    }
    
    /*
     * ------------------------------- Literal Management -------------------------------
     */
    
    public Literal getLiteral() {
        if (accessed != null) return accessed.getLiteral();
        else return null;
    }
    
    public void setLiteral(Literal literal) {
        if (accessed != null) accessed.setLiteral(literal);
        else return;
    }
    
    public boolean hasLiteral() {
        if (accessed != null) return accessed.hasLiteral();
        else return false;
    }
    
    public boolean canSetLiteral() {
        if (accessed != null) return accessed.canSetLiteral();
        else return false;
    }
    
    public boolean canUpdateLiteral(Frame frame, OPCode op) {
        if (accessed != null) return accessed.canUpdateLiteral(frame, op);
        else return false;
    }
    
    public boolean canLiteralSurvive(Frame frame) {
        if (accessed != null) return accessed.canLiteralSurvive(frame);
        else return false;
    }
    
    public boolean updateLiteral(OPCode op, Literal lit) {
        if (accessed != null) return accessed.updateLiteral(op, lit);
        else return false;
    }
    
    /*
     * ------------------------------- Use Management -------------------------------
     */
    
    @Override
    public boolean setInUse(IIntermediate by) {
        getAccessedAtIdx().setInUse(this);
        getAccessedWithinVar().setInUse(this);
        
        return super.setInUse(this);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        getAccessedWithinVar().takeRegister(this);
        getAccessedAtIdx().takeRegister(this);
        
        super.takeRegister(this);
    }
    
    /*
     * ------------------------------- To Target Tools -------------------------------
     */
    
    public boolean isVarSuper() {
        return var.isVarSuper(); // Because of special case this should be right
    }
    
    public int getVarThisFlag() {
        return var.getVarThisFlag();
    }
    
    @Override
    public abstract String toTarget();
}
