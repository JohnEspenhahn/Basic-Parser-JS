package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;

public abstract class StringConst extends BasicObject {

	public StringConst(String str) {
		super(str, Type.STRING);
	}
	
	public String getString() {
	    return getName();
	}
	
	@Override
	public String toString() {
	    return "\"" + getString() + "\"";
	}
}
