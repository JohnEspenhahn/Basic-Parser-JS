package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;

public class Param extends BasicObject {
    private int flags;
    
    public Param(String name, Type type) {
        this(name, type, 0);
    }
    
    public Param(String name, Type type, int flags) {
        super(name, type);
        
        this.flags = flags;
    }
    
    @Override
    public int getFlags() {
        return flags;
    }
    
    @Override
    public boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
    }
    
    @Override
    public String toTarget() {
        throw new RuntimeException("Cannot convert `Param` to target language");
    }
}
