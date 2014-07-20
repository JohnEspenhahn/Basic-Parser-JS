package com.hahn.basic.intermediate.objects.register;

import java.util.ArrayList;
import java.util.List;

public class Register extends IRegister {
    private static final List<Register> VALUES = new ArrayList<Register>();
    
    private int idx;
    private boolean inUse;
    
    private Register(String name) {
        super(name);
        
        this.inUse = false;
        
    	this.idx = Register.VALUES.size();
        Register.VALUES.add(this);
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
    
    public int getIndex() {
        return idx;
    }
    
    @Override
    public boolean isOnStack() {
        return false;
    }
    
    @Override
    public IRegister snapshot() {
        return this;
    }
    
    public static void freeAll() {
        for (Register r: Register.values()) {
            r.release();
        }
    }

    public static int getNumFree() {
        int numFree = 0;
        
        for (Register r: Register.values()) {
            if (r.isAvaliable()) {
                numFree += 1;
            }
        }
        
        return numFree;
    }
    
    public static List<Register> values() {
        return Register.VALUES;
    }
    
    public static int count() {
    	return Register.values().size();
    }
    
    public static Register fromIdx(int idx) {
    	return Register.values().get(idx);
    }
}
