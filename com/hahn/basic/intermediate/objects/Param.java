package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.objects.types.Type;
import com.sun.istack.internal.Nullable;

public class Param extends BasicObject {
    @Nullable
    private List<String> flags;
    
    public Param(String name, Type type) {
        this(name, type, null);
    }
    
    public Param(String name, Type type, List<String> flags) {
        super(name, type);
        
        this.flags = flags;
    }
    
    public boolean hasFlags() {
        return flags != null;
    }
    
    public List<String> getFlags() {
        return flags;
    }
    
    @Override
    public boolean hasFlag(String name) {
        return hasFlags() && getFlags().contains(name);
    }
    
    @Override
    public String toTarget() {
        throw new RuntimeException("Cannot convert `Param` to target language");
    }
}
