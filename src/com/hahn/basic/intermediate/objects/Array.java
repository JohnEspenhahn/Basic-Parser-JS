package com.hahn.basic.intermediate.objects;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class Array extends BasicObject implements IArray {
    
	private Frame frame;
    private List<IBasicObject> values;
    
    /**
     * Create an array with default values `values`
     * @param values The values for the dimensions
     * @throws IllegalArgumentException If type does not extend Array
     */
    public Array(Frame frame, List<IBasicObject> values) {
        super("[...]", IArray.toArrayType(Type.OBJECT, 1));
        
        this.frame = frame;
        this.values = values;
    }
    
    public CodeFile getFile() {
    	return this.frame.getFile();
    }
    
    public List<IBasicObject> getValues() {
        return values;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public ParameterizedType<ITypeable> getType() {
        return ((ParameterizedType<ITypeable>) super.getType());
    }
    
    @Override
    public Type getBaseType() {
        return IArray.getBaseType(getType());
    }
    
    @Override
    public int dimensions() {
        return getType().numTypeParams();
    }
    
    @Override
    public void setType(Type t) {
        if (!t.doesExtend(Type.ARRAY)) throw new IllegalArgumentException();
        
        super.setType(t);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
		Type baseType = null;
        
        ListIterator<IBasicObject> it = getValues().listIterator(getValues().size());
        while (it.hasPrevious()) {
            IBasicObject obj = it.previous();
            obj.setInUse(this);
            
            if (baseType == null) baseType = obj.getType();
            else baseType = baseType.getCommonType(obj.getType());
        }
        
        // Empty array initializer
        if (baseType == null) baseType = Type.UNDEFINED;
        
        int dimensions = 1;
        if (baseType.doesExtend(Type.ARRAY) && baseType instanceof ParameterizedType) {
            @SuppressWarnings("unchecked")
            ParameterizedType<ITypeable> pBaseType = (ParameterizedType<ITypeable>) baseType;
            baseType = IArray.getBaseType(pBaseType);
            dimensions = pBaseType.numTypeParams() + 1;
        }
        
        setType(IArray.toArrayType(baseType, dimensions));
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        Iterator<IBasicObject> it = getValues().iterator();
        while (it.hasNext()) {
            it.next().takeRegister(this);
        }
    }
}
