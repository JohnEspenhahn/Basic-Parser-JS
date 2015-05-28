package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.structures.BitFlag;

public abstract class Var extends AdvancedObject {
    private int flags;
    
    protected Var(Frame frame, String name, Type type, int flags) {
        super(frame, name, type);
        
        this.flags = flags;
    }
    
    @Override
    public boolean isVar() {
        return true;
    }
    
    @Override
    public int getFlags() {
        return flags;
    }
    
    public void addFlag(BitFlag flag) {
        this.flags |= flag.b;
    }
    
    @Override
    public boolean hasFlag(BitFlag flag) {
        return (this.flags & flag.b) != 0;
    }
    
    @Override
    public void doTakeRegister(boolean lastUse) {
        if (!hasRegister()) {
            List<AdvancedObject> parallelObjs = getParallelObjs();
            if (parallelObjs.size() < getFactory().getAvailableRegisters() || getUses() >= parallelObjs.get(0).getUses()) {
                setRegister(getStandardRegister());
            } else {
                setRegister(getStackRegister());
            }
        } 
        
        if (lastUse) {
            releaseRegister();
        }
    }
    
    protected IRegister getStandardRegister() {
        return getFactory().getNextRegister(this);
    }
    
    /**
     * Get the register to use if this is on the stack
     * @return The stack register to use
     */
    protected StackRegister getStackRegister() {
        return StackRegister.peek();
    }
}
