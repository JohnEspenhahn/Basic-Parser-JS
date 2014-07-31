package com.hahn.basic.intermediate.objects;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;

public class VarLocal extends Var {

	public VarLocal(Frame frame, String name, Type type, List<String> flags) {
		super(frame, name, type, flags);
	}

	@Override
	public boolean isLocal() {
        return true;
    }
}
