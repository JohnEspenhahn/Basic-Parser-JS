package com.hahn.basic.intermediate.library;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.Util;

public class Common extends Library {
    public static final FuncHead ALLOC = new FuncHead("alloc", null, Type.UNDEFINED, Util.toParams(Type.UINT)),
                               DEALLOC = new FuncHead("dealloc", null, Type.BOOL, Util.toParams(Type.UNDEFINED)),
                             ARR_ALLOC = new FuncHead("arr_alloc", null, Type.UNDEFINED, Util.toParams(Type.UINT));

    public Common() {
        super("com.hahn.common");
    }

    @Override
    public void define() {
        // Functions are hidden
    }
    
    @Override
    public void define(LangBuildTarget b) {
    	// TODO define common
    }
}
