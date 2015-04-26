package com.hahn.basic.intermediate.objects.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.NonNull;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.EmptyArray;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CastException;
import com.hahn.basic.util.exceptions.CompileException;

public class Type implements ITypeable {
    public static final int NO_MATCH = -0xffff;
    private static final List<Type> TYPES = new ArrayList<Type>();
    
    public static final Type VOID = new Type("void"),
                             BOOL = new Type("bool"),
                             REAL = new Type("real"),
                             REALSTR = new RealOrStringType(),
                             /** UNDEFINED -> anything */
                             UNDEFINED = new Type("undefined", false, true),
                             /** NULL -> extends OBJECT */
                             NULL = new Type("null", false, true);
    
    public static final StructType STRUCT = new StructType("struct", null, 0, true);
    
    public static final ClassType  OBJECT   = new ClassType(null, "Object", STRUCT, BitFlag.ABSTRACT.b | BitFlag.SYSTEM.b, false),
                                   FUNCTION = OBJECT.extendAs(null, "function", BitFlag.FINAL.b | BitFlag.SYSTEM.b).setTypeParams(-1),
                                   ARRAY    = OBJECT.extendAs(null, "Array", BitFlag.FINAL.b | BitFlag.SYSTEM.b).setTypeParams(-1),
                                   STRING   = OBJECT.extendAs(null, "String", BitFlag.FINAL.b | BitFlag.SYSTEM.b).setTypeParams(0);
    
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
    
    public StructType getAsStruct() {
        return (StructType) this;
    }
    
    /**
     * Check if the given type extends this type
     * @param t The given type
     * @return True if `t` extends this
     */
    public final boolean doesExtend(Type t) {
        return getExtendDepth(t) >= 0;
    }
    
    /**
     * Check if the given type extends this type
     * @param t The given type
     * @return A value >= 0 if does extend, the large the number the further the abstraction
     */
    public int getExtendDepth(Type t) {
        if (this == Type.UNDEFINED || t == Type.UNDEFINED || this.equals(t)) {
            return 0;
        } else {
            return -1;
        }
    }
    
    /**
     * More lenient than merge, but can only go in one direction.
     * Should be called from REGISTER OPTIMIZE
     * @param newType The type to try and change to
     * @param row Row to throw error at
     * @param col Column to throw error at
     * @return newType on success
     * @throws CastException If can not cast
     */
    public Type castTo(Type newType, int row, int col) {
        if (this == UNDEFINED) return newType;
        else if (this.doesExtend(newType)) return newType;
        else if (this.doesExtend(NULL) && newType.doesExtend(OBJECT)) return newType;
        
        // TODO upcasting and expression to check at runtime
        
        throw new CastException("Can not cast `" + this + "` to `" + newType + "`", row, col);
    }
    
    /**
     * Auto-casting from original to newType. Standard to
     * be called from REGISTER_OPTIMIZE
     * @param newType The new/given type
     * @param row Row to throw error at
     * @param col Column to throw error at
     * @param unsafe If true will throw exception on fail
     * @return Common type, or null if failed and unsafe is false 
     * @throws CompileException failed and unsafe is true
     */
    public Type autocast(Type newType, int row, int col, boolean unsafe) {
        if (newType == null) return this;
        else if (this == UNDEFINED) return newType;
        else if (newType == UNDEFINED) return this;
        else if (this.doesExtend(newType)) return newType;
        else if (this == REAL && newType == REALSTR) return REAL;
        else if (this == STRING && newType == REALSTR) return STRING;
        else if (this.doesExtend(NULL) && newType.doesExtend(OBJECT)) return newType;
        
        if (unsafe) throw new CompileException("Incompatible types `" + this + "` and `" + newType + "`", row, col);
        else return null;
    }
    
    public boolean canAutocast(Type newType) {
        return autocast(newType, -1, -1, false) != null;
    }
    
    /**
     * Combine two types and return the common type. Used with arithmetic and ternary
     * @param t1 Type one
     * @param t2 Type two
     * @param row Row to throw error at
     * @param col Column to throw error at
     * @param unsafe If true will throw exception on fail
     * @return Common type, or null if failed and unsafe is false 
     * @throws CompileException failed and unsafe is true
     */
    public static Type merge(Type t1, Type t2, int row, int col, boolean unsafe) {
        if (t1 == null && t2 == null) return null;
        else if (t1 == null || t1 == UNDEFINED) return t2;
        else if (t2 == null || t2 == UNDEFINED) return t1;
        else if (t1.doesExtend(t2)) return t2;
        else if (t2.doesExtend(t1)) return t1;
        else if (t1.doesExtend(NULL) && t2.doesExtend(OBJECT)) return t2;
        else if (t2.doesExtend(NULL) && t1.doesExtend(OBJECT)) return t1;
        else if (t1.doesExtend(STRING) && t2.doesExtend(REAL)) return STRING;
        else if (t2.doesExtend(STRING) && t1.doesExtend(REAL)) return STRING;
        
        if (unsafe) throw new CompileException("Incompatible types `" + t1 + "` and `" + t2 + "`", row, col);
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
    
    public void reverseOptimize() { }
    
    public void forwardOptimize() { }
    
    public String toTarget() {
        return "";
    }
    
    /**
     * Check if the given node is a valid node to be parsed as a type
     * @param node The node to check
     * @return True if a valid node to parse
     */
    public static boolean isTypeNode(Node node) {
        Enum<?> token = node.getToken();
        return token == EnumToken.IDENTIFIER || token == EnumExpression.TYPE;
    }
    
    /**
     * Parse the given node to a type
     * @param node The node to parse
     * @return The type
     * @throw CompileException If the node cannot be parsed to a valid type
     */
    public static Type fromNode(Node node) {
        return Type.fromNode(node, false);
    }

    /**
     * Convert the given node into a type
     * @param head The given node
     * @param isGettingMain If a main type?
     * @return The new type
     * @throw CompileException If the node cannot be parsed to a valid type
     */
    @SuppressWarnings("unchecked")
    public static Type fromNode(Node head, boolean isGettingMain) {
        if (head == null) return Type.UNDEFINED; 
        
        Iterator<Node> it = Util.getIterator(head);
        
        Node nameNode = it.next();
        Type type = Type.fromName(nameNode.getValue());
        if (type == null) throw new CompileException("Invalid type `" + nameNode.getValue() + "` specified", nameNode);
        
        Node child = null;
        if (it.hasNext()) {
            child = it.next();
            if (child.getToken() == EnumExpression.PARAM_TYPES) {
                // Check not allowed parameters
                if (!(type instanceof StructType) || ((StructType) type).getTypeParams() == 0) {
                    throw new CompileException("The type `" + type.toString() + "` can not be parameterized", nameNode);
                }
                
                StructType mainType = type.getAsStruct();
                type = ParameterizedType.getParameterizedType(mainType, child, mainType.doesExtend(Type.FUNCTION));
                
                if (mainType.getTypeParams() != -1 && mainType.getTypeParams() != ((ParameterizedType<Type>) type).getTypes().length) {
                    throw new CompileException("Invalid number of parameters for type `" + mainType.toString() + "`. Expected " + mainType.getTypeParams() + " but got " + ((ParameterizedType<Type>) type).getTypes().length, head);
                }
                
                // Go past PARAM_TYPES
                if (it.hasNext()) child = it.next();
            }
        }
        
        // Check requires parameters
        if (!isGettingMain && type instanceof StructType && ((StructType) type).getTypeParams() > 0) {
            throw new CompileException("The type `" + nameNode.getValue() + "` must be parameterized", nameNode);
        }
        
        // Check for array definition
        if (child != null) {
            int dimensions = 0;
            
            do {
                Enum<?> token = child.getToken();
                
                if (token == EnumToken.CLOSE_SQR) {
                    continue;
                } else if (token == EnumToken.OPEN_SQR) {
                    dimensions += 1;
                }
            } while (it.hasNext() && (child = it.next()) != null);
            
            if (dimensions > 0) {
                type = EmptyArray.toArrayType(type, dimensions);
            }
        }
        
        // Return
        return type;
    }
    
    /**
     * Get the node with the actual name of the type
     * @param head The given node
     * @return The new type
     * @throw CompileException If the node cannot be parsed to a valid type
     */
    public static Node getNameNode(@NonNull Node head) {
        Iterator<Node> it = Util.getIterator(head);
        
        return it.next();
    }
    
    /**
     * Gets the Type with the given name or null
     * if no Type with that name exists
     * @param name The name of the type to get
     * @return The type or null
     */
    public static Type fromName(String name) {
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
    
    /**
     * Get all types not marked as "abstract"
     * @return Types
     */
    public static List<Type> getPublicTypes() {
        return Type.TYPES;
    }
}
