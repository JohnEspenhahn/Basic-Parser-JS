package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangObject;
import com.hahn.basic.target.asm.raw.ASMPointer;

public class VarPointer extends AdvancedObject {
    
    @Delegate(types=VarPointer.IDelegated.class)
    private AdvancedObject var;
    
    public VarPointer(AdvancedObject temp) {
        super(temp.getFrame(), temp.getName(), temp.getType());
        
        this.var = temp;
    }
    
    interface IDelegated {
        public Type getType();
        public String getName();
        public boolean setInUse();
        public void takeRegister(boolean lastUse);
        public int getUses();
    }
    
    public void setObj(AdvancedObject v) {
        this.var = v;
    }
    
    @Override
    public boolean canSetLiteral() {
        return false;
    }
    
    @Override
    public LangObject toTarget() {
        return new ASMPointer(var.toTarget());
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof VarPointer) {
            return ((VarPointer) o).var.equals(var);
        } else {
            return super.equals(o);
        }
    }
    
    @Override
    public int hashCode() {
        return var.hashCode();
    }
    
    @Override
    public String toString() {
        return "[" + var.toString() + "]";
    }
}
