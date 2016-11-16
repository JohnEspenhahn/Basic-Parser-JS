package com.hahn.basic.target.js;

import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.target.OutputBuilderFactory;

import lombok.NonNull;

public class JSOutputBuilderFactory implements OutputBuilderFactory {
    
    public JSOutputBuilder newInstance(@NonNull Compiler c) {
        return new JSOutputBuilder(c);
    }
    
}
