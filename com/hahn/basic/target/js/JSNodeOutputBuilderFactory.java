package com.hahn.basic.target.js;

import com.hahn.basic.target.OutputBuilderFactory;
import com.hahn.basic.intermediate.Compiler;

public class JSNodeOutputBuilderFactory implements OutputBuilderFactory {
    
    public JSOutputBuilder newInstance(Compiler c) {
        return new JSNodeOutputBuilder(c);
    }
    
}
