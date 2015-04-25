package com.hahn.basic.target.js.statements;

import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncGroup;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.StructType.StructParam;
import com.hahn.basic.intermediate.statements.ClassDefinition;
import com.hahn.basic.target.js.JSPretty;
import com.hahn.basic.util.BitFlag;

public class JSClassDefinition extends ClassDefinition {
    
    public JSClassDefinition(Frame containingFrame, ClassType type) {
        super(containingFrame, type);
    }

    @Override
    public String toTarget() {
        ClassType clazz = getClassType();
        if (clazz.hasFlag(BitFlag.SYSTEM)) return "";
        
        boolean isChild = (clazz.getParent() instanceof ClassType);
        
        StringBuilder builder = new StringBuilder();
        builder.append(JSPretty.format(0, "var %s_=_(function(%s)_{^", clazz.getName(), (isChild ? EnumToken.___s : "")));
        
        JSPretty.addTab();
        
        ///////////////////////
        // Extend
        //////////////////////
        
        // Get short local name
        String localName = clazz.getName();
        if (localName.length() > EnumToken.___n.toString().length()) localName = EnumToken.___n.toString();
        
        // Get parent name
        String parentName = clazz.getParent().getName();
        
        // Extend super
        if (isChild) builder.append(JSPretty.format(0, "%s(%s,%s);^", EnumToken.___e, localName, parentName));
        
        // Main constructor
        builder.append(JSPretty.format(0, "function %s()_{^", localName));
        
        // Call super constructor and, if needed, add init frame
        if (!clazz.getInitFrame().isEmpty()) {
            JSPretty.addTab();
            builder.append(JSPretty.format(0, "%s.call(this);^", EnumToken.___s));
            builder.append(JSPretty.format(-1, "%s", clazz.getInitFrame()));
            JSPretty.removeTab();
        } else {
            builder.append(JSPretty.format(1, "%s.call(this)<;>", EnumToken.___s));
        }
        
        builder.append(JSPretty.format(0, "^"));
        builder.append(JSPretty.format(0, "}^"));
        
        // Define class's static parameters
        for (StructParam param: clazz.getDefinedParams()) {
            if (param.hasFlag(BitFlag.STATIC)) {
                builder.append(JSPretty.format(0, "%s.%s_=_undefined;^", localName, param.getName()));
            }
        }
        
        // TODO class static frame
        if (!clazz.getStaticFrame().isEmpty()) {
            builder.append(JSPretty.format(0, "%b^", clazz.getStaticFrame()));
        }
        
        // Add functions
        for (FuncGroup funcGroup: clazz.getDefinedFuncs()) {
            for (FuncHead func: funcGroup) {
                if (func.hasFrameHead()) {                    
                    if (func.hasFlag(BitFlag.STATIC)) {
                        builder.append(JSPretty.format(0, "%s.%s_=_%s;^", localName, func.getFuncId(), func.toFuncAreaTarget()));
                    } else {
                        builder.append(JSPretty.format(0, "%s.prototype.%s_=_%s;^", localName, func.getFuncId(), func.toFuncAreaTarget()));
                    }
                }
            }
        }
        
        builder.append(JSPretty.format(0, "return %s<;^>", localName));        
        
        JSPretty.removeTab();
        
        builder.append(JSPretty.format(0, "})(%s);^", (isChild ? parentName : "")));
        return builder.toString();
    }
    
}
