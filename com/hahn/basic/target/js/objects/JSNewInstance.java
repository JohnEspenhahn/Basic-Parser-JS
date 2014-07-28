package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.NewInstance;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.Util;

public class JSNewInstance extends NewInstance {
    
    public JSNewInstance(Type type, List<BasicObject> params) {
        super(type, params);
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        if (getType().doesExtend(Type.STRUCT)) {
            return "{}";
        } else {
            return String.format("new %s(%s)", getType().getName(), Util.toTarget(getParams(), ",", builder));
        }
    }
    
}
