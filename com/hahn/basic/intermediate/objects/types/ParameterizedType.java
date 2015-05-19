package com.hahn.basic.intermediate.objects.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.TypeUtils;
import com.hahn.basic.util.CompilerUtils;
import com.hahn.basic.util.exceptions.CompileException;

/**
 * A type consisting of n ITypable parameter types
 * and one ITypeable return type
 * 
 * @author John Espenhahn
 *
 * @param <T> Some ITypeable object to allow this to store
 * either abstract types of actual typed objects
 */
public class ParameterizedType<T extends ITypeable> extends Type {
    public static final ParameterizedType<Type> STRING_ARRAY = new ParameterizedType<Type>(Type.ARRAY, new Type[] { Type.STRING });
    
    private final StructType base;
    private final T[] types;
    
    private Type returnType;
    
    /**
     * Create a new parameterized type with no parameters and a undefined return type
     * @param base The base type of this
     */
    @SuppressWarnings("unchecked")
    public ParameterizedType(StructType base) {
        this(base, (T[]) new ITypeable[0], Type.UNDEFINED);
    }
    
    /**
     * Create a new parameterized type with the given parameters and an undefined return type
     * @param base The base type of this
     * @param params The parameters' types
     */
    public ParameterizedType(StructType base, T[] params) {
        this(base, params, Type.UNDEFINED);
    }
    
    @SuppressWarnings("unchecked")
    public static Type tryToUnpack(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType<ITypeable>) type).getTypable(0).getType();
        } else {
            return type;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static int countParameterizedTypes(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType<ITypeable>) type).numTypeParams();
        } else {
            return 0;
        }
    }
    
    /**
     * Create a new parameterized type with the given parameters and return type
     * @param base The base type of this
     * @param types The parameters' types
     * @param returnType The return type of this
     */
    public ParameterizedType(StructType base, T[] types, Type returnType) {
        super(createName(base, types, returnType), false, true);
        
        this.base = base;
        this.types = types;
        this.returnType = returnType;
    }
    
    /**
     * Create what should be a unique name based on the given parameters
     * @param base The base type
     * @param params The parameter's types
     * @param returnType The return type
     * @return The name in format base<p1,pn...[;return]> 
     */
    private static String createName(StructType base, ITypeable[] params, Type returnType) {
        if (base == Type.ARRAY) {
            return params[0].getType().getName() + "[]";
        } else {
            return base.getName() 
                    + "<"
                    + (params.length > 0 ? TypeUtils.joinTypes(params, ',') : "") 
                    + (returnType != Type.UNDEFINED || base.doesExtend(Type.FUNCTION) ? ";"+returnType.getType() : "")
                    + ">";
        }
    }
    
    @Override
    public String getFuncIdName() {
        return base.getFuncIdName() + "$" + StringUtils.join(Stream.of(getTypes()).map(t -> t.getType().getFuncIdName()).iterator(), '$');
    }

    /**
     * @return The base type
     */
    @Override
    public StructType getAsStruct() {
        return getBase();
    }
    
    public StructType getBase() {
        return base;
    }
    
    /**
     * @return The number of parameterized types
     */
    public int numTypeParams() {
        return types.length;
    }
    
    public T[] getTypes() {
        return types;
    }
    
    public T getTypable(int idx) {
        return types[idx];
    }
    
    public Type getReturn() {
        return returnType.getType();
    }
    
    public void setReturn(Type t) {
        returnType = t;
    }
    
    @Override
    public int getExtendDepth(Type t) {
        if (t instanceof ParameterizedType) {
            ParameterizedType<?> p = (ParameterizedType<?>) t;
            
            int extendDepth = base.getExtendDepth(p.base);
            if (extendDepth >= 0 && getReturn().canAutocast(p.getReturn()) && types.length == p.types.length) {
                for (int i = 0; i < types.length; i++) {
                    if (!getTypable(i).getType().canAutocast(p.getTypable(i).getType())) {
                        return -1;
                    }
                }
                
                return extendDepth;
            } else {
                return -1;
            }
        } else {
            return base.getExtendDepth(t);
        }
    }
    
    @Override
    public Type getCommonType(Type other) {
        if (other instanceof ParameterizedType && ((ParameterizedType<?>) other).getBase() == getBase()) {
            @SuppressWarnings("unchecked")
            ParameterizedType<ITypeable> pOther = (ParameterizedType<ITypeable>) other;
            StructType base = getBase();
            
            if (this.numTypeParams() == pOther.numTypeParams()) {
                Type[] params = new Type[numTypeParams()];
                for (int i = 0; i < params.length; i++) {
                    params[i] = this.getTypable(i).getType().getCommonType(pOther.getTypable(i).getType());
                }
                
                return new ParameterizedType<Type>(base, params);
            }
        }
        
        Type commonBase = other.getCommonType(base);
        if (commonBase instanceof StructType) {
            int numParams = ((StructType) commonBase).numTypeParams();
            Type[] params = new Type[numParams];
            Arrays.fill(params, Type.OBJECT);
            return new ParameterizedType<Type>((StructType) commonBase, params); 
        } else {
            return commonBase;
        }
    }
    
    /**
     * Get types from type list
     * @param base The base type for the parameterized type result
     * @param head EnumExpression.PARAM_TYPES for result, otherwise will return empty list
     * @param allowReturn Can include a return type
     * @return List of types or empty list if head not PARAM_TYPES
     */
    public static ParameterizedType<ITypeable> getParameterizedType(StructType base, Node head, boolean allowReturn) {
        Type returnType = Type.UNDEFINED;
        List<Type> params = null;
               
        Iterator<Node> it = CompilerUtils.getIterator(head);
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> t = node.getToken();
            
            if (t == EnumExpression.TYPE) {
                if (allowReturn) returnType = Type.fromNode(node);
                else throw new CompileException("Cannot provide return type for the parameterized type `" + base + "`");
            } else if (t == EnumExpression.TYPE_LIST) {
                params = getTypeList(node);
            }
        }

        Type[] aParams = (params != null ? params.toArray(new Type[params.size()]) : new Type[0]);        
        return new ParameterizedType<ITypeable>(base, (ITypeable[]) aParams, returnType);
    }
    
    /**
     * Parse the expression into a list of types
     * @param head EnumExpression.TYPE_LIST
     * @return List of types
     */
    private static List<Type> getTypeList(Node head) {
        List<Type> types = new ArrayList<Type>();
        
        Iterator<Node> it = CompilerUtils.getIterator(head);
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> t = node.getToken();
            
            if (t == EnumExpression.TYPE) {
                types.add(Type.fromNode(node));
            }
        }
        
        return types;
    }
}
