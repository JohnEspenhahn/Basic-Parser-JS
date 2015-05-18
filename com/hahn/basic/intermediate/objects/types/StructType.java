package com.hahn.basic.intermediate.objects.types;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.exceptions.CompileException;

public class StructType extends Type implements IParamHolding {    
    private final ArrayDeque<StructType> extended;
    private final Map<String, StructParam> params;

    private int typeParams;
    private int flags;
    
    /**
     * Create a new struct
     * @param name The name of the struct
     * @param parent The parent struct or null
     * @param isAbstract If true won't check for other types with the same name
     */
    protected StructType(String name, StructType parent, int flags, boolean isAbstract) {
        super(name, true, isAbstract);
        
        this.flags = flags;
        
        if (parent == null) {
            this.extended = new ArrayDeque<StructType>();
        } else {
            this.extended = new ArrayDeque<StructType>(parent.getExtended());
            this.extended.addLast(parent);
        }
        
        this.params = new HashMap<String, StructParam>();
    }
    
    private ArrayDeque<StructType> getExtended() {
        return extended;
    }
    
    public StructType getParent() {
        return (getExtended().size() == 0 ? null : getExtended().getLast());
    }
    
    public boolean hasFlag(BitFlag flag) {
        return (this.flags & flag.b) != 0;
    }
    
    public void setFlag(BitFlag flag) {
        this.flags |= flag.b;
    }
    
    @Override
    public int getExtendDepth(Type t) {
        StructType parent = getParent();
        if (parent != null) {
            int parentExtendDepth = parent.getExtendDepth(t);
            if (parentExtendDepth >= 0) return parentExtendDepth + 1;
        }
        
        // Default
        return super.getExtendDepth(t);
    }
    
    @Override
    public Type getCommonType(Type other) {
        if (!(other instanceof StructType)) {
            if (this.doesExtend(other)) return other;
            else if (other.doesExtend(this)) return this;
            else return Type.OBJECT;
        }
        
        Iterator<StructType> thisExtended  = this.getExtended().iterator();        
        Iterator<StructType> otherExtended = ((StructType) other).getExtended().iterator();
        
        // TODO track shared interfaces
        StructType leastCommonExtended = Type.OBJECT;
        while (thisExtended.hasNext() && otherExtended.hasNext()) {
            StructType t = thisExtended.next(),
                       o = otherExtended.next();
            
            if (t == o) {
                leastCommonExtended = t;
            }
        }
        
        return leastCommonExtended;
        
    }
    
    /**
     * Extend this with no additional parameters
     * @param containingFrame The frame that contains the new struct's definition or null if system class
     * @param name The name of the new struct
     * @param flags Flags for this structs
     * @return A new struct object
     */
    public StructType extendAs(Frame containingFrame, String name, int flags) {
        return extendAs(containingFrame, name, null, flags);
    }
    
    /**
     * Extend this
     * @param containingFrame The frame that contains the new struct's definition
     * @param name The name of the new struct
     * @param ps The parameters added by this new struct
     * @param flags Flags for this struct
     * @return A new struct object
     */
    public StructType extendAs(Frame containingFrame, String name, List<BasicObject> ps, int flags) {
        StructType struct = new StructType(name, this, flags, false);
        struct.loadParams(ps);
        
        return struct;
    }
    
    public void loadParams(List<BasicObject> ps) {
        if (ps != null) {
            for (int i = 0; i < ps.size(); i++) {
                BasicObject p = ps.get(i);
                addParam(p, null, null, false);
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
    
    public int numTypeParams() {
        return this.typeParams;
    }
    
    /**
     * Add a basic object as a struct param
     * @param nameNode The node with the name of the parameter
     * @param type The type of the parameter
     * @param outName The name to output in the target language or null for default
     * @param override If true will override a pre-existing param within this class
     * @return This
     * @throws CompileException If the variable is already defined
     */
    public StructType addParam(Node nameNode, Type type, String outName, boolean override) {
        return addParam(new Param(nameNode.getValue(), type), nameNode, outName, override);
    }
    
    /**
     * Add a basic object as a struct param
     * @param p The basic object to use when defining the struct param
     * @param node The node to throw an error at
     * @param outName The name to output in the target language or null for default
     * @param override If true will override a pre-existing param within this class
     * @return This
     * @throws CompileException If the variable is already defined
     */
    private StructType addParam(BasicObject p, Node node, String outName, boolean override) {        
        putParam(p, node, outName, override);
        
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
        return putParam(p, node, null, false);
    }
    
    /**
     * Add a basic object as a struct param
     * @param p The basic object to use when defining the struct param
     * @param node The node to throw an error at if the parameter is already defined
     * @param outName The name to output in the target language or null for default
     * @param override If true will override a pre-existing param within this class
     * @return The added struct param
     * @throws CompileException If the variable is already defined
     */
    private StructParam putParam(BasicObject p, Node node, String outName, boolean override) {
        if ((override && checkSuperParamUnique(p.getName(), node)) || checkParamUnique(p.getName(), node)) {
            StructParam param = new StructParam(params.size(), p, outName);
            
            // Store in hashmap with vanilla name
            params.put(p.getName(), param);
           
            return param;
        } else {
            throw new RuntimeException();
        }
    }
    
    /**
     * Check that the given variable with the given name is unique within all super structs
     * @param name The name of the variable
     * @param throwNode Throws uniqueness errors at this node
     * @return True if is unique
     */
    private boolean checkSuperParamUnique(String name, Node node) {
        StructParam p = getParam(name, false, true, true, node);
        return (p == null || p == params.get(name));
    }
    
    /**
     * Check that the given variable with the given name is unique within this struct and all super structs
     * @param name The name of the variable 
     * @param throwNode Throws uniqueness errors at this node
     * @return True if is unique
     */
    private boolean checkParamUnique(String name, Node throwNode) {
        return getParam(name, true, true, true, throwNode) == null;
    }
    
    /**
     * Get a struct param
     * @param name The name of the parameter to get
     * @return Then parameter found or null if it can't be retrieved
     */
    public StructParam getParamSafe(String name) {
        return getParam(name, false, false, true, null);
    }
    
    /**
     * Get a struct param
     * @param nameNode The node that contains the requested name
     * @return The param
     * @throw CompileException If the requested param is not defined
     */
    public StructParam getParam(Node nameNode) {
        return getParam(nameNode.getValue(), false, false, false, nameNode);
    }
    
    /**
     * Get a variable unique to this frame's instance
     * @param name The name of the variable
     * @param requireThisUnique If true will throw uniqueness error if explicitly defined in this class
     * @param requireSuperUnique If true will throw uniqueness error if defined in a super class of this class
     * @param safe If false will throw additional errors
     * @param throwNode Throws uniqueness errors at this node
     * @return The variable found or null
     * @throws CompileException If requireUnique is true and the variable is already defined
     * @throws CompileException If safe is false and an error occurs
     */
    public StructParam getParam(String name, boolean requireThisUnique, boolean requireSuperUnique, boolean safe, Node throwNode) {        
        boolean fromParent = false;
        StructParam sVar = params.get(name);
        if (sVar != null && requireThisUnique) {
            throw new CompileException("The instance variable `" + name + "` is already defined", throwNode);
        } else if (sVar == null && getParent() != null) {
            fromParent = true;
            sVar =  getParent().getParamSafe(name);
            if (sVar != null && requireSuperUnique) {
                throw new CompileException("The instance variable `" + name + "` is already defined in a super class", throwNode);
            }
        }
        
        if (sVar != null) {
            // Private variables
            if (fromParent && sVar.hasFlag(BitFlag.PRIVATE)) {
                if (safe) return null;
                else throw new CompileException("The instance variable `" + name + "` is private", throwNode);
            }
            
            return sVar;
        } else if (!safe) {
            throw new CompileException("Unknown variable `" + name + "` in " + this, throwNode);
        } else {
            return null;
        }
    }
    
    public Collection<StructParam> getDefinedParams() {
        return params.values();
    }
    
    public class StructParam extends Param {
        public final int idx;
        
        private String outName;
        
        /**
         * A structure param
         * @param i The index in the structure
         * @param p The defining BasicObject
         * @param givenOutName The predefined output name or null for default name (see {@link getOutName()})
         */
        private StructParam(int i, BasicObject p, String givenOutName) {
            super(p.getName(), p.getType(), p.getFlags());
            
            this.idx = i;
            this.outName = getOutName(givenOutName);
        }
        
        /**
         * Get the proper output name
         * @param givenOutName A given output name or null for default name ($ + {@link getName()})
         * @return The proper output name
         */
        private String getOutName(String givenOutName) {
            return (givenOutName == null ? "$" + getName() : givenOutName);
        }
        
        public int getOffset() {
            return idx;
        }
        
        public String getOutName() {
            return outName;
        }

        @Override
        public String toTarget() {
            return getOutName();
        }
    }
}
