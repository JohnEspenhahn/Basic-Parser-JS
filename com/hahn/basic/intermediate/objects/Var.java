package com.hahn.basic.intermediate.objects;

import java.util.ArrayList;
import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.sun.istack.internal.Nullable;

public abstract class Var extends AdvancedObject {
    @Nullable
    private List<String> flags;
    
    protected Var(Frame frame, String name, Type type, List<String> flags) {
        super(frame, name, type);
        
        this.flags = flags;
    }
    
    public boolean hasFlags() {
        return flags != null;
    }
    
    public List<String> getFlags() {
        return flags;
    }
    
    public void addFlag(String name) {
        if (!hasFlags()) {
            flags = new ArrayList<String>(1);
        }
        
        if (!getFlags().contains(name)) {
            getFlags().add(name);
        }
    }
    
    @Override
    public boolean hasFlag(String name) {
        return hasFlags() && getFlags().contains(name);
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
