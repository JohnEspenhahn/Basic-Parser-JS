package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.register.IRegister;
import com.hahn.basic.intermediate.register.Register;
import com.hahn.basic.intermediate.register.StackRegister;

public abstract class Var extends AdvancedObject {
    
    protected Var(Frame frame, String name, Type type) {
        super(frame, name, type);
    }
    
    @Override
    public void takeRegister(boolean lastUse) {
        if (!hasRegister()) {
            List<AdvancedObject> parallelObjs = getParallelObjs();
            if (parallelObjs.size() < Register.getNumFree() || getUses() >= parallelObjs.get(0).getUses()) {
                setRegister(getStandardRegister());
            } else {
                setRegister(getStackRegister());
            }
        } else if (lastUse) {
            releaseRegister();
        }
    }
    
    protected IRegister getStandardRegister() {
        for (Register r: Register.values()) {
            if (r.isAvaliable()) {
                r.reserve();
                
                return r;
            }
        }
        
        throw new RuntimeException("No standard registers left!");
    }
    
    /**
     * Get the register to use if this is on the stack
     * @return The stack register to use
     */
    protected StackRegister getStackRegister() {
        return StackRegister.peek();
    }
}
