package com.hahn.basic.intermediate.objects.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.exceptions.CompileException;

public class Struct extends Type {
    protected static final Struct STRUCT = new Struct("struct", null);
    
    private final int baseSize;
    private final Struct parent;
    private final Map<String, StructParam> params;

    private int typeParams;
    
    private Struct(String name, Struct parent) {
        super(name, true);
        
        this.params = new HashMap<String, StructParam>();
        this.parent = parent;
        
        if (parent == null) this.baseSize = 0;
        else this.baseSize = parent.sizeOf();
    }
    
    public Struct extendAs(String name, List<BasicObject> ps) {
        Struct struct = new Struct(name, this);
        struct.loadVars(ps);
        
        return struct;
    }
    
    public Struct extendAs(String name) {
        return this.extendAs(name, null);
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return super.doesExtend(t) || (parent != null && parent.doesExtend(t));
    }
    
    /**
     * Set the number of required parameters or -1 for > 0
     * @param num The number of parameters
     * @return This
     */
    public Struct setTypeParams(int num) {
        this.typeParams = num;
        return this;
    }
    
    public int getTypeParams() {
        return this.typeParams;
    }
    
    private void loadVars(List<BasicObject> ps) {
        if (ps != null) {
            for (int i = 0; i < ps.size(); i++) {
                this.add(ps.get(i));
            }
        }
    }
    
    public Struct add(BasicObject p) {
        params.put(p.getName(), new StructParam(baseSize + params.size(), p));
        return this;
    }
    
    public StructParam getStructParam(String name) {
        StructParam sVar = params.get(name);
        if (sVar != null) {
            return sVar;
        } else if (parent != null) {
            return parent.getStructParam(name);
        } else {
            throw new CompileException("Unknown variable '" + name + "' in " + this);
        }
    }
    
    public int getOffset(String name) {
        return getStructParam(name).idx;
    }
    
    public Type getType(String name) {
        return getStructParam(name).getType();
    }
    
    @Override
    public int sizeOf() {
        return baseSize + params.size();
    }
    
    public class StructParam extends BasicObject {
        public final int idx;
        
        public StructParam(int i, BasicObject p) {
            super(p.getName(), p.getType());
            
            this.idx = i;
        }
        
        public int getOffset() {
            return idx;
        }

        @Override
        public String toTarget(LangBuildTarget builder) {
            throw new RuntimeException("Cannot convert `StructParam` to target!");
        }
    }
}
