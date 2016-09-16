package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.util.exceptions.UnimplementedException;

public abstract class RawObject implements IBasicObject {

	/**
     * @param type The type to cast to
     * @row Row to throw error at
     * @col Column to throw error at
     * @return A new, altered version of this
     */
    public IBasicObject castTo(Type type, CodeFile file, int row, int col) {
        return file.getFactory().CastedObject(this, getType().castTo(type, file, row, col), file, row, col);
    }
    
    /**
     * Get as an expression
     * @param container The container of the expression
     * @return ExpressionStatement
     */
    public final ExpressionStatement getAsExp(Statement container) {
        return container.getFactory().ExpressionStatement(container, this);
    }
    
    public String toTarget() { throw new UnimplementedException(); }

}
