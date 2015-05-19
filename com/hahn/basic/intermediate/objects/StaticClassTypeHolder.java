package com.hahn.basic.intermediate.objects;

import lombok.experimental.Delegate;

import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.StructType;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.exceptions.CompileException;

public class StaticClassTypeHolder extends ClassType implements StaticClassExcludes {
    @Delegate(excludes=StaticClassExcludes.class)
    private final ClassType heldClass;
    
    public StaticClassTypeHolder(ClassType classType) {
        super(null, null, null, 0, true);
        
        this.heldClass = classType;
    }
    
    @Override
    public StructType getAsStruct() {
        return this;
    }
    
    @Override
    public StructParam getParamSafe(String name) {
        return getParam(name, false, true, null);
    }
    
    @Override
    public StructParam getParam(Node nameNode) {
        return getParam(nameNode.getValue(), false, false, nameNode);
    }
    
    @Override
    public StructParam getParam(String name, boolean requireUnique, boolean safe, Node throwNode) {
        StructParam p = heldClass.getParam(name, requireUnique, requireUnique, safe, throwNode);
        
        if (!p.hasFlag(BitFlag.STATIC)) {
            if (safe) return null;
            else throw new CompileException("Can not make a static reference to an instance variable", throwNode);
        } else if (!heldClass.getDefinedParams().contains(p)) {
            if (safe) return null;
            else throw new CompileException("Must access static variable `" + p + "` directly through its defining class", throwNode);
        } else {
            return p;
        }
    }
    
    @Override
    public FuncHead getFunc(BasicObject objIn, Node nameNode, ITypeable[] types) {
        return getFunc(objIn, nameNode, types, false, false);
    }
    
    @Override
    public FuncHead getFunc(BasicObject objIn, Node nameNode, ITypeable[] types, boolean safe, boolean shallow) {
        FuncHead func = heldClass.getFunc(objIn, nameNode, types, safe, false);
        
        if (!func.hasFlag(BitFlag.STATIC)) {
            if (safe) return null;
            else throw new CompileException("Can not make a static reference to a non-static function", nameNode);
        } else if (heldClass.getFunc(objIn, nameNode, types, true, true) != func) {
            if (safe) return null;
            else throw new CompileException("Must access static function `" + func + "` directly through its defining class", nameNode);                
        } else {
            return func;
        }
    }
}
