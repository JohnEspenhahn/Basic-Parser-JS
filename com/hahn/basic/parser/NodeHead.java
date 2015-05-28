package com.hahn.basic.parser;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.intermediate.CodeFile;

public class NodeHead extends Node {
    private CodeFile file;
    
    public NodeHead(CodeFile file) {
        super(null, EnumExpression.START, 0, 0, 0);
        
        this.file = file;
    }
    
    @Override
    public CodeFile getFile() {
        return file;
    }
}
