package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Compilable;

abstract class ObjectPointer extends AdvancedObject {
    
    @Delegate(types=ObjectPointer.IDelegated.class)
    private BasicObject obj;
    
    public ObjectPointer(Frame frame, BasicObject temp) {
        super(frame, null, null);
        
        this.obj = temp;
    }
    
    interface IDelegated {
        public Type getType();
        public String getName();
        public boolean setInUse(Compilable by);
        public void doTakeRegister(boolean lastUse);
        public int getUses();
    }
    
    public void setObj(BasicObject o) {
        this.obj = o;
    }
    
    public BasicObject getObj() {
        return obj;
    }
    
    @Override
    public boolean canSetLiteral() {
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof ObjectPointer) {
            return ((ObjectPointer) o).obj.equals(obj);
        } else {
            return super.equals(o);
        }
    }
    
    @Override
    public int hashCode() {
        return obj.hashCode();
    }
    
    @Override
    public String toString() {
        return "[" + obj.toString() + "]";
    }
}
