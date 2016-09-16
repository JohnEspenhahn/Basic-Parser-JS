package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class PushObject extends BasicObject {	
	public PushObject() {
		super("PUSH", Type.UNDEFINED);
	}
	
	@Override
	public void takeRegister(IIntermediate by) {
	    StackRegister.push();
	}
}
