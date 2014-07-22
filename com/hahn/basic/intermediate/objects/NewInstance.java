package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.objects.types.Type;

public abstract class NewInstance extends BasicObject {
	private List<BasicObject> params;
	
	public NewInstance(Type type, List<BasicObject> params) {
		super("new " + type.getName(), type);
		
		this.params = params;
	}
	
	public List<BasicObject> getParams() {
		return params;
	}

}
