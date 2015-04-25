package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class NewInstance extends BasicObject {
    private Node constructorNode;
    private FuncHead constructor;
    
	private BasicObject[] params;
	
	public NewInstance(Type type, Node typeNode, List<BasicObject> params) {
		super("new " + type.getName(), type);
		
		this.params = params.toArray(new BasicObject[params.size()]);
		
		// Get constructor
		if (type.doesExtend(Type.OBJECT)) {
		    constructorNode = new Node(typeNode, EnumToken.CONSTRUCTOR, "constructor", typeNode.getIdx(), typeNode.getRow(), typeNode.getCol());
        }
	}
	
	@Override
	public boolean setInUse(IIntermediate by) {	    
	    this.constructor = ((ClassType) getType()).getFunc(null, constructorNode, this.params);
	    
	    return super.setInUse(by);
	}
	
	public BasicObject[] getParams() {
		return params;
	}
	
	/**
	 * Get the constructor function. Not set until after reverse optimize!
	 * @return The function head of the constructor function, or null
	 */
	public FuncHead getConstructor() {
	    return constructor;
	}

}
