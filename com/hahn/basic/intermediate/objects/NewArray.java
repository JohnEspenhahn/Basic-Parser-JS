package com.hahn.basic.intermediate.objects;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class NewArray extends BasicObject {
    private List<BasicObject> dimensionValues;
    private int dimensions;
    private Node node;
    
    public NewArray(Node node, int dimensions, @NonNull List<BasicObject> dimValues) {
        super("new " + StringUtils.repeat("[]", dimensions), 
                new ParameterizedType<Type>(Type.ARRAY, Util.createArray(dimensions, Type.UNDEFINED)));
        
        this.dimensionValues = dimValues;        
        this.dimensions = dimensions;
        this.node = node;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        int dimension = dimensionValues.size();
        ListIterator<BasicObject> it = dimensionValues.listIterator(dimension);
        while (it.hasPrevious()) {
            BasicObject obj = it.previous();
            obj.setInUse(this);
            
            if (!obj.getType().doesExtend(Type.INT)) {
                throw new CompileException("Illegal type for array initialization. Expected int but got `" + obj.getType() + "` at dimension " + dimension, node);
            }
            
            dimension -= 1;
        }
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        Iterator<BasicObject> it = dimensionValues.iterator();
        while (it.hasNext()) {
            it.next().takeRegister(this);
        }
    }
    
    public List<BasicObject> getDimensionValues() {
        return dimensionValues;
    }
    
    public int getDimensions() {
        return dimensions;
    }
    
    @SuppressWarnings("unchecked")
    public Type getContainedType() {
        return ((ParameterizedType<Type>) getType()).getTypable(0);
    }
}
