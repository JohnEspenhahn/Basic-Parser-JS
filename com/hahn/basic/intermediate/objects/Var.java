package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

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
    
    public void addFlag(int flag) {
        this.flags |= flag;
    }
    
    @Override
    public boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
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
    
    public static class Flag {
        public static final int CONST   = 0b00000000000000000000000000000001;
        public static final int PRIVATE = 0b00000000000000000000000000000010;
        
        /**
         * Get the flag from the given EnumExpression.FLAG node
         * @param node EnumExpression.FLAG
         * @return The flag
         * @throws CompileException if not a valid flag
         */
        public static int valueOf(Node node) {
            String name = node.getAsChildren().get(0).getValue();
            
            switch (name) {
            case "const": return Flag.CONST;
            case "private": return Flag.PRIVATE;
            
            default:
                throw new CompileException("Invalid variable flag `" + name + "`");
            }
        }
    }
}
