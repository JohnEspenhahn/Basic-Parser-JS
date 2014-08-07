package com.hahn.basic.intermediate.objects.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public class StructType extends Type {    
    private final StructType parent;
    private final Map<String, StructParam> params;

    private int typeParams;
    
    /**
     * Create a new struct
     * @param name The name of the struct
     * @param parent The parent struct or null
     */
    protected StructType(String name, StructType parent) {
        super(name, true);
        
        this.parent = parent;
        this.params = new HashMap<String, StructParam>();
        
        // Copy all parent parameters
        if (parent != null) {
            for (StructParam p: parent.getAllParams()) {
                add(p);
            }
        }
    }
    
    /**
     * Extend this
     * @param name The name of the new struct
     * @param ps The parameters added by this new struct
     * @return A new struct object
     */
    public StructType extendAs(String name, List<BasicObject> ps) {
        StructType struct = new StructType(name, this);
        struct.loadVars(ps);
        
        return struct;
    }
    
    /**
     * Extend this with no additional parameters
     * @param name The name of the new struct
     * @return A new struct object
     */
    public StructType extendAs(String name) {
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
    public StructType setTypeParams(int num) {
        this.typeParams = num;
        return this;
    }
    
    public int getTypeParams() {
        return this.typeParams;
    }
    
    protected void loadVars(List<BasicObject> ps) {
        if (ps != null) {
            for (int i = 0; i < ps.size(); i++) {
                this.add(ps.get(i));
            }
        }
    }
    
    public StructType add(BasicObject p) {
        params.put(p.getName(), new StructParam(params.size(), p));
        return this;
    }
    
    public StructParam getStructParam(Node nameNode) {
        String name = nameNode.getValue();
        StructParam sVar = params.get(name);
        if (sVar != null) {
            return sVar;
        } else {
            throw new CompileException("Unknown variable `" + name + "` in " + this, nameNode);
        }
    }
    
    public Collection<StructParam> getAllParams() {
        return params.values();
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
        public String toTarget() {
            return getName();
        }
    }
}
