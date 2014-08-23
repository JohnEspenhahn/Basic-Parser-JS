package com.hahn.basic.intermediate.objects.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.exceptions.CompileException;

public class StructType extends Type {    
    private final StructType parent;
    private final Map<String, StructParam> params;

    private int typeParams;
    private int flags;
    
    /**
     * Create a new struct
     * @param name The name of the struct
     * @param parent The parent struct or null
     */
    protected StructType(String name, StructType parent, int flags) {
        super(name, true);
        
        this.flags = flags;
        
        this.parent = parent;
        this.params = new HashMap<String, StructParam>();
    }
    
    public StructType getParent() {
        return parent;
    }
    
    public boolean hasFlag(BitFlag flag) {
        return (this.flags & flag.b) != 0;
    }
    
    public void setFlag(BitFlag flag) {
        this.flags |= flag.b;
    }
    
    @Override
    public boolean doesExtend(Type t) {
        return super.doesExtend(t) || (parent != null && parent.doesExtend(t));
    }
    
    /**
     * Extend this with no additional parameters
     * @param name The name of the new struct
     * @param flags Flags for this structs
     * @return A new struct object
     */
    public StructType extendAs(String name, int flags) {
        return this.extendAs(name, null, flags);
    }
    
    /**
     * Extend this
     * @param name The name of the new struct
     * @param ps The parameters added by this new struct
     * @param flags Flags for this struct
     * @return A new struct object
     */
    public StructType extendAs(String name, List<BasicObject> ps, int flags) {
        StructType struct = new StructType(name, this, flags);
        struct.loadParams(ps);
        
        return struct;
    }
    
    public void loadParams(List<BasicObject> ps) {
        if (ps != null) {
            for (int i = 0; i < ps.size(); i++) {
                this.addParam(ps.get(i), null);
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
     * @param nameNode The node with the name of the parameter
     * @param type The type of the parameter
     * @return This
     * @throws CompileException If the variable is already defined
     */
    public StructType addParam(Node nameNode, Type type) {
        return addParam(new Param(nameNode.getValue(), type), nameNode);
    }
    
    /**
     * Add a basic object as a struct param
     * @param p The basic object to use when defining the struct param
     * @param node The node to throw an error at
     * @return This
     * @throws CompileException If the variable is already defined
     */
    public StructType addParam(BasicObject p, Node node) {
        putParam(p, node);
        
        return this;
    }
    
    /**
     * Add a basic object as a struct param
     * @param p The basic object to use when defining the struct param
     * @param node The node to throw an error at if the parameter is already defined
     * @return The added struct param
     * @throws CompileException If the variable is already defined
     */
    public StructParam putParam(BasicObject p, Node node) {
        if (getParam(p.getName(), node) == null)   {
            StructParam param = new StructParam(params.size(), p);
            params.put(p.getName(), param);
           
            return param;
        } else {
            throw new RuntimeException();
        }
    }
    
    /**
     * Get a struct param
     * @param nameNode The node that contains the requested name
     * @return The param
     * @throw CompileException If the requested param is not defined
     */
    public StructParam getParam(Node nameNode) {
        return getParam(nameNode, false);
    }
    
    /**
     * Get a struct param
     * @param nameNode The node that contains the requested name
     * @param safe If false can throw an exception
     * @return The param; or, if `safe` is true and there is an error, null
     * @throw CompileException If `safe` is false and the param is not defined
     */
    public StructParam getParam(Node nameNode, boolean safe) {
        String name = nameNode.getValue();
        StructParam param = getParamSafe(name);        
        
        if (param != null) {
            return param;
        } else if (safe) {
            return null;
        } else {
            throw new CompileException("Unknown variable `" + name + "` in " + this, nameNode);
        }
    }
    
    public StructParam getParamSafe(String name) {
        return getParam(name, null);
    }
    
    /**
     * Get a variable unique to this frame's instance <br>
     * <b>Precondition</b> If `getting` is false call Main.setLine
     * @param name The name of the variable
     * @param throwNode If not null can throw an error at this node 
     * @return The variable found or null
     * @throws CompileException If throwNode is not null and the variable is already defined
     */
    public StructParam getParam(String name, Node throwNode) {
        StructParam sVar = params.get(name);
        if (sVar != null) {
            if (throwNode == null) return sVar;
            else throw new CompileException("The instance variable `" + name + "` is already defined", throwNode);
        } else if (parent != null) {
            sVar =  parent.getParamSafe(name);
            if (sVar != null) {
                if (throwNode == null) return sVar;
                else throw new CompileException("The instance variable `" + name + "` is already defined in a super class", throwNode);
            }
        }
        
        return null;
    }
    
    public Collection<StructParam> getDefinedParams() {
        return params.values();
    }
    
    public class StructParam extends Param {
        public final int idx;
        
        public StructParam(int i, BasicObject p) {
            super(p.getName(), p.getType(), p.getFlags());
            
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
