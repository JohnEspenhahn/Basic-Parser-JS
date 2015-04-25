package com.hahn.basic.target.js.objects;

import java.util.List;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.NewInstance;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.Util;

public class JSNewInstance extends NewInstance {
    
    public JSNewInstance(Type type, Node typeNode, List<BasicObject> params) {
        super(type, typeNode, params);
    }
    
    @Override
    public String toTarget() {
        if (getType() instanceof ClassType) {
            if (getParams().length == 0) { // Constructor with no params
                // If no frame head then auto-generated default constructor
                if (getConstructor().hasFrameHead()) {
                    return String.format("%s(%s,'%s')", EnumToken.___c, getType().getName(), getConstructor().getFuncId());
                } else {
                    return String.format("%s(%s)", EnumToken.___c, getType().getName());
                }
            } else { // Constructor with params
                return String.format("%s(%s,'%s',%s)", EnumToken.___c, getType().getName(), getConstructor().getFuncId(), Util.toTarget(getParams()));
            }
        } else if (getType().doesExtend(Type.STRUCT)) {
            return "{}";
        } else {
            throw new RuntimeException("Cannot create instance of type `" + getType() + "`");
        }
    }
    
}
