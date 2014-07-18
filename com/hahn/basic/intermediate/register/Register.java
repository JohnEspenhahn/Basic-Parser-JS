package com.hahn.basic.intermediate.register;

import java.util.ArrayList;
import java.util.List;

public class Register extends IRegister {
    private static final List<Register> VALUES = new ArrayList<Register>(),
                                        ALL_REGS = new ArrayList<Register>();
    
    public static final Register A = new Register("A", 0),
                                 B = new Register("B", 1),
                                 C = new Register("C", 2),
                                 I = new Register("I", 3),
                                 XH = new Register("XH", 4),
                                 XL = new Register("XL", 5),
                                 YH = new Register("YH", 6),
                                 YL = new Register("YL", 7);
    
    public static final Register PUSH = new Register("PUSH", 0x0C, false),
                                 PEEK = new Register("PEEK", 0x0E, false),
                                  POP = new Register("POP" , 0x0C, false),
                                   EX = new Register("EX"  , 0x21, false),
                                   PC = new Register("PC"  , 0x22, false),
                                   SP = new Register("SP"  , 0x23, false);
    
    private final int bytecode;
    private boolean inUse;
    
    private Register(String name, int bytecode) {
        this(name, bytecode, true);
    }
    
    private Register(String name, int bytecode, boolean avaliable) {
        super(name);
        
        this.inUse = false;
        this.bytecode = bytecode;
        
        Register.ALL_REGS.add(this);
        
        if (avaliable) {
            Register.VALUES.add(this);
        }
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
    
    public int getAsByte() {
        return bytecode;
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
    
    public static Register fromName(String name) {
        for (Register r: Register.getAllRegisters()) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        
        return null;
    }
    
    public static List<Register> values() {
        return Register.VALUES;
    }
    
    public static List<Register> getAllRegisters() {
        return Register.ALL_REGS;
    }
}
