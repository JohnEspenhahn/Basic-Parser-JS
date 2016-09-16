package com.hahn.basic.intermediate.objects.register;


public abstract class Register extends IRegister {
    private boolean inUse;
    
    protected Register(String name) {
        super(name);
        
        this.inUse = false;
    }
    
    public void reserve() {
        inUse = true;
    }
    
    @Override
    public void release() {
        inUse = false;
    }
    
    public boolean isAvaliable() {
        return !inUse;
    }
    
    @Override
    public boolean isOnStack() {
        return false;
    }
    
    @Override
    public IRegister snapshot() {
        return this;
    }
}
