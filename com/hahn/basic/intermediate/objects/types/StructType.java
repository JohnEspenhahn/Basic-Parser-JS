package com.hahn.basic.intermediate.objects.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public class StructType extends Type {    
    protected final StructType parent;
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
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return super.doesExtend(t) || (parent != null && parent.doesExtend(t));
    }
    
    /**
     * Extend this with no additional parameters
     * @param name The name of the new struct
     * @return A new struct object
     */
    public StructType extendAs(String name) {
        return this.extendAs(name, null);
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
    
    protected void loadVars(List<BasicObject> ps) {
        if (ps != null) {
            for (int i = 0; i < ps.size(); i++) {
                this.add(ps.get(i));
            }
        }
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
    
    /**
     * Get the number of required type parameters
     * @return The number of parameters or -1 for > 0
     */
    public int getTypeParams() {
        return this.typeParams;
    }
    
    /**
     * Add a basic object as a struct param
     * @param p The basic object to use when defining the struct param
     * @return This
     */
    public StructType add(BasicObject p) {
        params.put(p.getName(), new StructParam(params.size(), p));
        return this;
    }
    
    /**
     * Get a struct param
     * @param nameNode The node that contains the requested name
     * @return The param
     * @throw CompileException If the requested param is not defined
     */
    public StructParam getStructParam(Node nameNode) {
        return getStructParam(nameNode, false);
    }
    
    /**
     * Get a struct param
     * @param nameNode The node that contains the requested name
     * @param safe If false can throw an exception
     * @return The param; or, if `safe` is true and there is an error, null
     * @throw CompileException If `safe` is false and the param is not defined
     */
    public StructParam getStructParam(Node nameNode, boolean safe) {
        String name = nameNode.getValue();
        StructParam sVar = params.get(name);
        if (sVar != null) {
            return sVar;
        } else if (parent != null) {
            sVar =  parent.getStructParam(nameNode, true);
            if (sVar != null) return sVar;
        } 
        
        // If reached this point then not found
        if (!safe) {
            throw new CompileException("Unknown variable `" + name + "` in " + this, nameNode);
        } else {
            return null;
        }
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
