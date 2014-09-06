package com.hahn.basic.intermediate.objects.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;
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
    public static final ParameterizedType<Type> UINT_ARRAY = new ParameterizedType<Type>(Type.ARRAY, new Type[] { Type.INT });
    public static final ParameterizedType<Type> CHAR_ARRAY = new ParameterizedType<Type>(Type.ARRAY, new Type[] { Type.CHAR });
    
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
        return base.getName() 
                + "<"
                + (params.length > 0 ? Util.joinTypes(params, ',') : "") 
                + (returnType != Type.UNDEFINED || base.doesExtend(Type.FUNC) ? ";"+returnType.getType() : "")
                + ">";
    }
    
    @Override
    public StructType getAsStruct() {
        return base;
    }
    
    public int numTypes() {
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
    public boolean doesExtend(Type t) {
        if (t instanceof ParameterizedType) {
            ParameterizedType<?> p = (ParameterizedType<?>) t;
            
            if (base.doesExtend(p.base) && getReturn().doesExtend(p.getReturn()) && types.length == p.types.length) {
                for (int i = 0; i < types.length; i++) {
                    if (!getTypable(i).getType().equals(p.getTypable(i).getType())) {
                        return false;
                    }
                }
                
                return true;
            } else {
                return false;
            }
        } else {
            return base.doesExtend(t);
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
               
        Iterator<Node> it = Util.getIterator(head);
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
        
        Iterator<Node> it = Util.getIterator(head);
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
