package com.hahn.basic.intermediate.objects;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class EmptyArray extends BasicObject implements IArray {
    private Node node;
    private List<BasicObject> dimensionSizes;
    
    /**
     * Create an empty array defined at node `node` of type `type` (where `type` extends Array)
     * @param node The node defining at
     * @param type The type of the array. Contains information about number of dimensions
     * @param dimensionSizes Contain information about the size of the dimensions
     * @throws IllegalArgumentException If type does not extend Array
     */
    public EmptyArray(Node node, ParameterizedType<Type> type, List<BasicObject> dimensionSizes) {
        super(type.toString(), type);
        
        if (!type.doesExtend(Type.ARRAY)) throw new IllegalArgumentException();
        
        this.node = node;
        this.dimensionSizes = dimensionSizes;
    }
    
    public Node getNode() {
        return node;
    }
    
    public List<BasicObject> getDimensionSizes() {
        return dimensionSizes;
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
        throw new RuntimeException("Cann't change type of predefined array");
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        int dimension = getDimensionSizes().size();
        ListIterator<BasicObject> it = getDimensionSizes().listIterator(dimension);
        while (it.hasPrevious()) {
            BasicObject obj = it.previous();
            obj.setInUse(this);
            
            if (!obj.getType().doesExtend(Type.REAL)) {
                throw new CompileException("Illegal type for array initialization. Expected `real` but got `" + obj.getType() + "` at dimension " + dimension, getNode());
            }
            
            dimension -= 1;
        }
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        Iterator<BasicObject> it = getDimensionSizes().iterator();
        while (it.hasNext()) {
            it.next().takeRegister(this);
        }
    }
}
