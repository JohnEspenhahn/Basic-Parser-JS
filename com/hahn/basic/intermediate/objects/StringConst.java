package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;

public abstract class StringConst extends BasicObject {

	public StringConst(String str) {
		super(str, Type.STRING);
	}
	
	@Override
	public abstract String toTarget();
}
