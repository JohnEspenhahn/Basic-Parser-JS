package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.objects.types.Type;

public abstract class NewInstance extends BasicObject {
	private BasicObject[] params;
	
	public NewInstance(Type type, List<BasicObject> params) {
		super("new " + type.getName(), type);
		
		this.params = params.toArray(new BasicObject[params.size()]);
	}
	
	public BasicObject[] getParams() {
		return params;
	}

}
