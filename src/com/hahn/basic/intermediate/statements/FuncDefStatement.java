package com.hahn.basic.intermediate.statements;

import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public class FuncDefStatement extends Statement {
    private FuncPointer fp;
    private Node nameNode;
    
    public FuncDefStatement(Statement container, Node nameNode, FuncHead func) {
        super(container);
        
        this.nameNode = nameNode;
        
        this.fp = getFactory().FuncPointer(nameNode, null, new ParameterizedType<ITypeable>(Type.FUNCTION, (ITypeable[]) func.getParams(), func.getReturnType()));
        this.fp.setFunction(func);
    }
    
    public Node getNameNode() {
        return nameNode;
    }
    
    public FuncHead getFuncHead() {
        return fp.getFunction();
    }
    
    public FuncPointer getFuncPointer() {
        return fp;
    }
    
    @Override
    public String toTarget() {
        return "";
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        return getFuncHead().reverseOptimize();
    }
    
    @Override
    public boolean forwardOptimize() {
        return getFuncHead().forwardOptimize();
    }
    
    @Override
    public String toString() {
        return "define: " + getFuncHead();
    }
    
}
