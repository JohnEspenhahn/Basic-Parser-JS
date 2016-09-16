package com.hahn.basic.intermediate.objects;

import java.util.Arrays;
import java.util.ListIterator;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.viewer.util.TextColor;

public abstract class FuncCallPointer extends FuncPointer {
    private Type returnType;

    /**
     * A callable pointer to a function
     * @param nameNode The node that defines the name of the function to call
     * @param objectIn The object that the function is in or null
     * @param params The provided parameters for the call
     */
    public FuncCallPointer(Node nameNode, IBasicObject objectIn, IBasicObject[] params) {
        super(nameNode, objectIn, new ParameterizedType<ITypeable>(Type.FUNCTION, (ITypeable[]) params));

        nameNode.setColor(TextColor.GREY);
        
        this.returnType = Type.UNDEFINED;
    }

    /**
     * Is this object used
     * @return True if uses is greater than zero
     */
    public boolean isUsed() {
        return getUses() > 0;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    /**
     * Cast to given type
     * @param t The type to cast to
     * @return this
     */
    @Override
    public IBasicObject castTo(Type t, CodeFile file, int row, int col) {
        this.returnType = this.returnType.castTo(t, file, row, col);

        return this;
    }

    @Override
    public boolean setInUse(IIntermediate by) {        
        ListIterator<IBasicObject> it = Arrays.asList(getParams()).listIterator(countParams());
        while (it.hasPrevious()) {
            IBasicObject param = it.previous();
            
            param.setInUse(this);
        }
        
        if (objectIn != null) objectIn.setInUse(this);
        
        checkFunction();
        returnType = returnType.autocast(func.getReturnType(), nameNode.getFile(), nameNode.getRow(), nameNode.getCol(), true);
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        if (objectIn != null) objectIn.takeRegister(this);
        for (IBasicObject param: getParams()) {
            param.takeRegister(this);
        }
        
        super.takeRegister(by);
    }
    
    /**
     * Get the function parameterized type
     * @return The parameterized type
     */
    @SuppressWarnings("unchecked")
    private ParameterizedType<IBasicObject> getParameterized() {
        return (ParameterizedType<IBasicObject>) super.getType();
    }

    /**
     * Get the actual parameter objects from the parameterized type
     * @return The parameter objects
     */
    public IBasicObject[] getParams() {
        return getParameterized().getTypes();
    }
    
    /**
     * Get the number of parameters
     * @return Number of parameters
     */
    public int countParams() {
        return getParams().length;
    }

    @Override
    public String toString() {
        return super.toString() + "(...)";
    }
}