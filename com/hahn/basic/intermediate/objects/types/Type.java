package com.hahn.basic.intermediate.objects.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CastException;
import com.hahn.basic.util.exceptions.CompileException;

public class Type implements ITypeable {
    public static final int NO_MATCH = -0xffff;
    private static final List<Type> TYPES = new ArrayList<Type>();
    
    public static final Type VOID = new Type("void"),
                             BOOL = new Type("bool"),
                             CHAR = new TypeIntLike("char"),
                             INT  = new TypeIntLike("int"),
                             DBL  = new Type("dbl"),
                             /** UNDEFINED -> anything */
                             UNDEFINED = new Type("undefined", false, true);
    
    public static final Struct STRUCT = new Struct("struct", null),
                               FUNC   = STRUCT.extendAs("func").setTypeParams(-1),
                               ARRAY  = STRUCT.extendAs("array").add(new Param("length", Type.INT)).setTypeParams(1),
                               STRING = ARRAY.extendAs("string").setTypeParams(0);
    
    public static final int COUNT_PRIMATIVES = TYPES.size();
    
    public final boolean requiresInit;
    private final String name; 
    
    public Type(String name, boolean requiresInit, boolean isAbstract) {
        this.name = name;
        this.requiresInit = requiresInit;
        
        if (!isAbstract) {
            if (fromName(name) == null) {
                Type.TYPES.add(this);
            } else {
                throw new CompileException("The type '" + name + "' is already defined");
            }
        }
    }
    
    public Type (String name, boolean requiresInit) {
        this(name, requiresInit, false);
    }
    
    public Type(String name) {
        this(name, false, false);
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public Type getType() {
        return this;
    }
    
    public Struct getAsStruct() {
        return (Struct) this;
    }
    
    public boolean doesExtend(Type t) {
        return this == Type.UNDEFINED || t == Type.UNDEFINED || this.equals(t);
    }
    
    /**
     * More lenient than merge, but can only go in one direction.
     * Should be called from REGISTER OPTIMIZE
     * @param newType The type to try and change to
     * @param row
     * @param col
     * @return newType on success
     * @throws CastException If can not cast
     */
    public Type castTo(Type newType, int row, int col) {
        Type result = newType;
        if (newType instanceof TypeList) {
            for (Type t: (TypeList) newType) {
                result = castTo(t, row, col);
            }
        } else {
            result = doCastTo(newType, row, col);
        }
        
        return result;
    }
    
    private final Type doCastTo(Type newType, int row, int col) {
        if (this == UNDEFINED) return newType;
        else if (this.doesExtend(newType)) return newType;
        
        // TODO upcasting expression
        
        throw new CastException("Can not cast `" + this + "` to `" + newType + "`", row, col);
    }
    
    /**
     * Auto-casting from original to newType. Standard to
     * be called from REGISTER_OPTIMIZE
     * @param newType The new/given type
     * @param row
     * @param col
     * @param unsafe If true will throw exception on fail
     * @return Overruling type, or null if failed and unsafe is false 
     * @throws CompileException failed and unsafe is true
     */
    public Type merge(Type newType, int row, int col, boolean unsafe) {
        Type result = this;
        if (newType instanceof TypeList) {
            for (Type t: (TypeList) newType) {
                result = result.merge(t, row, col, unsafe);
                if (result == null) return null;
            }
        } else {
            result = doMerge(newType, row, col, unsafe);
        }
        
        return result;
    }
    
    private final Type doMerge(Type newType, int row, int col, boolean unsafe) {
        if (newType == null) return this;
        else if (this == UNDEFINED) return newType;
        else if (newType == UNDEFINED) return this;
        else if (newType.doesExtend(this)) return this;
        
        if (unsafe) throw new CompileException("Incompatible types `" + this + "` and `" + newType + "`", row, col);
        else return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Type) {
            return ((Type) obj).getName().equals(getName());
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    public static boolean isValidNode(Node node) {
        Enum<?> token = node.getToken();
        return token == EnumToken.IDENTIFIER || token == EnumExpression.TYPE;
    }
    
    public static Type fromNode(Node node) {
        return Type.fromNode(node, false);
    }

    @SuppressWarnings("unchecked")
    public static Type fromNode(Node head, boolean isGettingMain) {
        if (head == null) return Type.UNDEFINED; 
        
        Iterator<Node> it = Util.getIterator(head);
        
        Node nameNode = it.next();
        Type type = Type.fromName(nameNode.getValue());
        
        while (it.hasNext()) {
            // Check not allowed parameters
            if (!(type instanceof Struct) || ((Struct) type).getTypeParams() == 0) {
                throw new CompileException("The type `" + type.toString() + "` can not be parameterized", nameNode);
            }
            
            Struct mainType = (Struct) type;
            type = ParameterizedType.getParameterizedType(mainType, it.next(), mainType.doesExtend(Type.STRUCT));
            
            if (mainType.getTypeParams() != -1 && mainType.getTypeParams() != ((ParameterizedType<Type>) type).getTypes().length) {
                throw new CompileException("Invalid number of parameters for type `" + mainType.toString() + "`. Expected " + mainType.getTypeParams() + " but got " + ((ParameterizedType<Type>) type).getTypes().length, head);
            }
        }
        
        // Check requires parameters
        if (!isGettingMain && type instanceof Struct && ((Struct) type).getTypeParams() > 0) {
            throw new CompileException("The type `" + nameNode.getValue() + "` must be parameterized", nameNode);
        } else if (type == null)  {
            throw new CompileException("Invalid type `" + nameNode.getValue() + "` specified", nameNode);
        } else {
            return type;
        }
    }
    
    private static Type fromName(String name) {
        for (Type t: Type.TYPES) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        
        return null;
    }
    
    public static void reset() {
        Iterator<Type> it = Type.TYPES.iterator();
        
        for (int i = 0; it.hasNext(); i++) {
            it.next();
            
            if (i >= COUNT_PRIMATIVES)
                it.remove();
        }
    }
}
