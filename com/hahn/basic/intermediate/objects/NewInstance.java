package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;

public abstract class NewInstance extends BasicObject {

	public NewInstance(Type type) {
		super("new " + type.getName(), type);
	}

	@Override
	public abstract String toTarget();
}
