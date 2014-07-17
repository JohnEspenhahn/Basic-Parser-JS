package com.hahn.basic.intermediate.objects;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangObject;
import com.hahn.basic.target.asm.objects.ASMAddressPointer;
import com.hahn.basic.target.asm.raw.ASMInlineLabel;
import com.hahn.basic.target.asm.raw.ASMPointer;

public class VarGlobal extends Var {
    public final int idx;
    private final int[] defaultVal;
    
    public VarGlobal(String name, Type type) {
        this(name, type, (byte) 0);
    }
    
    protected VarGlobal(String name, Type type, int... vals) {
        super(null, name, type);
        this.idx = Compiler.NEXT_GLOBAL_IDX;
        this.defaultVal = vals;
        
        Compiler.NEXT_GLOBAL_IDX += defaultVal.length;
    }
    
    @Override
    public int getUses() {
        return 2;
    }
    
    @Override
    public boolean canSetLiteral() {
        return false;
    }
    
    @Override
    public void takeRegister(boolean lastUse) {
        return;
    }
    
    @Override
    public BasicObject getAddress() {
        return new ASMAddressPointer(getName());
    }
    
    @Override
    public LangObject toTarget() {
        return new ASMPointer(new ASMInlineLabel(getName()));
    }
    
    @Override
    public BasicObject getForCreateVar() {
        return this;
    }
    
    public void setDefaultValue(int line, LiteralNum literal) {
        Main.setLine(line);
        Type.merge(getType(), literal.getType());
        
        defaultVal[0] = literal.getValue();
    }
    
    public int[] getDefaultValue() {
        return defaultVal;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof VarGlobal) {
            return ((VarGlobal) o).idx == idx;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return idx;
    }
}
