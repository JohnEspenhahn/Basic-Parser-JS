package com.hahn.basic.target;

import com.hahn.basic.intermediate.Compiler;

public interface OutputBuilderFactory {
    OutputBuilder newInstance(Compiler c);
}
