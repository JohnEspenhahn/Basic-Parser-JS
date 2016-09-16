package com.hahn.basic.target.js;

import com.hahn.basic.target.OutputBuilderFactory;
import com.hahn.basic.intermediate.Compiler;

public class JSOutputBuilderFactory implements OutputBuilderFactory {
    
    public JSOutputBuilder newInstance(Compiler c) {
        return new JSOutputBuilder(c);
    }
    
}
