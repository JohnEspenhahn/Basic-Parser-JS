package com.hahn.basic.intermediate.objects.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.NonNull;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CompileException;

public class ParameterizedType<T extends ITypeable> extends Type {
    public static final ParameterizedType<Type> UINT_ARRAY = new ParameterizedType<Type>(Type.ARRAY, new Type[] { Type.UINT });
    public static final ParameterizedType<Type> CHAR_ARRAY = new ParameterizedType<Type>(Type.ARRAY, new Type[] { Type.CHAR });
    
    private final Struct base;
    private final T[] types;
    
    private ITypeable returnType;
    
    @SuppressWarnings("unchecked")
    public ParameterizedType(Struct base) {
        this(base, (T[]) new ITypeable[0], Type.UNDEFINED);
    }
    
    public ParameterizedType(Struct base, T[] types) {
        this(base, types, Type.UNDEFINED);
    }
    
    public ParameterizedType(Struct base, @NonNull T[] types, @NonNull ITypeable returnType) {
        super(base.toString(), false, true);
        
        this.base = base;
        this.types = types;
        this.returnType = returnType;
    }
    
    @Override
    public String getName() {
        return base.getName() 
                + "<"
                + (types.length > 0 ? Util.toString(types, "@") : "") 
                + (returnType != Type.UNDEFINED || base.doesExtend(Type.FUNC) ? "@@"+returnType.getType() : "")
                + ">";
    }
    
    @Override
    public String toString() {
        return base.getName() 
                + "<"
                + (types.length > 0 ? Util.toString(types, ",") : "") 
                + (returnType != Type.UNDEFINED || base.doesExtend(Type.FUNC) ? ";"+returnType.getType() : "")
                + ">";
    }
    
    @Override
    public Struct castToStruct() {
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
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof ParameterizedType) {
            ParameterizedType<?> p = (ParameterizedType<?>) o;
            
            if (base.equals(p.base) && types.length == p.types.length
                    && getReturn().doesExtend(p.getReturn()) ) {
                
                for (int i = 0; i < types.length; i++) {
                    if (!getTypable(i).getType().equals(p.getTypable(i).getType())) {
                        return false;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get types from type list
     * @param base The base type for the parameterized type result
     * @param head EnumExpression.PARAM_TYPES for result, otherwise will return empty list
     * @param allowReturn Can include a return type
     * @return List of types or empty list if head not PARAM_TYPES
     */
    public static ParameterizedType<ITypeable> getParameterizedType(Struct base, Node head, boolean allowReturn) {
        Type returnType = Type.UNDEFINED;
        List<Type> params = null;
        
        Enum<?> token = head.getToken();
        if (token == EnumExpression.PARAM_TYPES) {        
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
        } else if (token == EnumExpression.TYPE_LIST) {
            params = getTypeList(head);
        }

        Type[] aParams = (params != null ? params.toArray(new Type[params.size()]) : new Type[0]);
        
        return new ParameterizedType<ITypeable>(base, (ITypeable[]) aParams, returnType);
    }
    
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
