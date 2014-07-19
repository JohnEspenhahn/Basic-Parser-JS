package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;

public abstract class PushObject extends BasicObject {	
	public PushObject() {
		super("PUSH", Type.UNDEFINED);
	}

	@Override
	public abstract String toTarget();
}
