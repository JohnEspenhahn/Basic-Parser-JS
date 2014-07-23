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
                             CHAR = new TypeUIntLike("char"),
                             UINT = new TypeUIntLike("uint"),
                             DBL = new Type("dbl"),
                             /** UNDEFINED -> anything */
                             UNDEFINED = new Type("undefined", false, true);
    
    public static final Struct STRUCT = Struct.STRUCT,
                               FUNC = STRUCT.extendAs("func").setTypeParams(-1),
                               ARRAY = STRUCT.extendAs("array").add(new Param("length", Type.UINT)).setTypeParams(1),
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
    
    public int sizeOf() {
        return 1;
    }
    
    public boolean doesExtend(Type t) {
        return this == Type.UNDEFINED || t == Type.UNDEFINED || this.equals(t);
    }
    
    public Struct castToStruct() {
        return (Struct) this;
    }
    
    /**
     * More lenient than merge, but can only go in one direction.
     * Should be called from REGISTER OPTIMIZE
     * @param t The type to try and change to
     * @return t on success
     */
    public Type castTo(Type t) {
        if (t == null || t == Type.UNDEFINED) {
            return this;
        } else if (this.equals(t)) {
            return t;
        } else if (this == UINT) {
            if (t == BOOL) return BOOL;
        } else if (this == BOOL) {
            if (t == UINT) return UINT;
        } else if (this == UNDEFINED) {
            return t;
        } else if (this == STRING && t.equals(ParameterizedType.UINT_ARRAY)) {
            return ParameterizedType.UINT_ARRAY;
        } else if ((this.equals(ParameterizedType.UINT_ARRAY) || this.equals(ParameterizedType.CHAR_ARRAY)) && t == STRING) {
            return STRING;
        } else if (this.doesExtend(t)) {
            return t;
        }
        
        throw new CastException("Can not cast `" + this + "` to `" + t + "`");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return ((String) obj).equals(getName());
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
    
    /**
     * Auto-casting. Standard to be called from REGISTER OPTIMIZE
     * @param original The original/expected type
     * @param newType The new/given type
     * @return Overruling type
     */
    public static Type merge(Type original, Type newType) {
        if (newType == null) return original;
        else if (original == null) return newType;
        else if (original.doesExtend(newType)) return newType;
        else if (original == UNDEFINED) return newType;
        else if (newType == UNDEFINED) return original; 
        else if (original == UINT && newType == BOOL) return BOOL;
        
        throw new CompileException("Incompatible types `" + original.toString() + "` and `" + newType.toString() + "`");
    }
    
    public static Type fromNode(Node node) {
        return Type.fromNode(node, false);
    }

    @SuppressWarnings("unchecked")
    public static Type fromNode(Node node, boolean isGettingMain) {        
        Iterator<Node> it = Util.getIterator(node);
        
        Node nameNode = it.next();
        Type type = Type.fromName(nameNode.getValue());
        
        while (it.hasNext()) {
            // Check not allowed parameters
            if (!(type instanceof Struct) || ((Struct) type).getTypeParams() == 0) {
                throw new CompileException("The type `" + type.toString() + "` can not be parameterized");
            }
            
            Struct mainType = (Struct) type;
            type = ParameterizedType.getParameterizedType(mainType, it.next(), mainType.doesExtend(Type.STRUCT));
            
            if (mainType.getTypeParams() != -1 && mainType.getTypeParams() != ((ParameterizedType<Type>) type).getTypes().length) {
                throw new CompileException("Invalid number of parameters for type `" + mainType.toString() + "`. Expected " + mainType.getTypeParams() + " but got " + ((ParameterizedType<Type>) type).getTypes().length);
            }
        }
        
        // Check requires parameters
        if (!isGettingMain && type instanceof Struct && ((Struct) type).getTypeParams() > 0) {
            throw new CompileException("The type `" + nameNode.getValue() + "` must be parameterized");
        } else if (type == null)  {
            throw new CompileException("Invalid type `" + nameNode.getValue() + "` specified");
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
