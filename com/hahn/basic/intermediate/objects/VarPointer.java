package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Compilable;

public abstract class VarPointer extends AdvancedObject {
    
    @Delegate(types=VarPointer.IDelegated.class)
    private AdvancedObject var;
    
    public VarPointer(AdvancedObject temp) {
        super(temp.getFrame(), temp.getName(), temp.getType());
        
        this.var = temp;
    }
    
    interface IDelegated {
        public Type getType();
        public String getName();
        public boolean setInUse(Compilable by);
        public void doTakeRegister(boolean lastUse);
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
