package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class Var extends AdvancedObject {
    
    protected Var(Frame frame, String name, Type type) {
        super(frame, name, type);
    }
    
    @Override
    public void doTakeRegister(boolean lastUse) {
        if (!hasRegister()) {
            List<AdvancedObject> parallelObjs = getParallelObjs();
            if (parallelObjs.size() < LangCompiler.factory.getAvailableRegisters() || getUses() >= parallelObjs.get(0).getUses()) {
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
        return LangCompiler.factory.getNextRegister(this);
    }
    
    /**
     * Get the register to use if this is on the stack
     * @return The stack register to use
     */
    protected StackRegister getStackRegister() {
        return StackRegister.peek();
    }
}
