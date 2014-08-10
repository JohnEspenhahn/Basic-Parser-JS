package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class NewInstance extends BasicObject {
    private FuncHead constructor;
	private BasicObject[] params;
	
	public NewInstance(Type type, Node typeNode, List<BasicObject> params) {
		super("new " + type.getName(), type);
		
		this.params = params.toArray(new BasicObject[params.size()]);
		
		// Get constructor
		if (type.doesExtend(Type.OBJECT) && params.size() > 0) {
            this.constructor = ((ClassType) type).getFunc(typeNode, this.params);
        }
	}
	
	public BasicObject[] getParams() {
		return params;
	}
	
	public FuncHead getConstructor() {
	    return constructor;
	}

}
