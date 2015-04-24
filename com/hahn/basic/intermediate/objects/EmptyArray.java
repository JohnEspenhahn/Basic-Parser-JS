package com.hahn.basic.intermediate.objects;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class EmptyArray extends BasicObject {
    private Node node;
    private List<BasicObject> dimensionSizes;
    
    public EmptyArray(Node node, Type type, List<BasicObject> dimensionSizes) {
        super(type.toString() + StringUtils.repeat("[]", dimensionSizes.size()), type);
        
        this.node = node;
        this.dimensionSizes = dimensionSizes;
    }
    
    public Node getNode() {
        return node;
    }
    
    public int dimensions() {
        return dimensionSizes.size();
    }
    
    public List<BasicObject> getDimensionSizes() {
        return dimensionSizes;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        int dimension = dimensions();
        ListIterator<BasicObject> it = getDimensionSizes().listIterator(dimension);
        while (it.hasPrevious()) {
            BasicObject obj = it.previous();
            obj.setInUse(this);
            
            if (!obj.getType().doesExtend(Type.REAL)) {
                throw new CompileException("Illegal type for array initialization. Expected real but got `" + obj.getType() + "` at dimension " + dimension, getNode());
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
