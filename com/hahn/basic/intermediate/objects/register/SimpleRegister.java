package com.hahn.basic.intermediate.objects.register;

public class SimpleRegister extends Register {
    private final SimpleRegisterFactory factory;
    
    protected SimpleRegister(String name, SimpleRegisterFactory factory) {
        super(name);
        
        this.factory = factory;
    }
    
    @Override
    public final void release() {
        super.release();
        
        factory.release(this);
    }
}
