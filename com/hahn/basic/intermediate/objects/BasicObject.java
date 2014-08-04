package com.hahn.basic.intermediate.objects;

import lombok.NonNull;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;

public abstract class BasicObject implements IIntermediate, ITypeable {
    private String name;
    private Type type;
    
    private int uses;
    private IIntermediate statementOfLastUse;
    
    public BasicObject(String name, @NonNull Type type) {
        this.name = name;
        this.type = type;
        this.uses = 0;
    }

    public boolean isTemp() {
        return false;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(@NonNull Type t) {
        this.type = t;
    }
    
    public boolean hasFlag(String name) {
        return false;
    }

    /**
     * @param type The type to cast to
     * @row Row to throw error at
     * @col Column to throw error at
     * @return A new, altered version of this
     */
    public BasicObject castTo(Type type, int row, int col) {
        return new ObjectHolder(this, getType().castTo(type, row, col), row, col);
    }
    
    
    /*
     * ------------------------------- Literal Management -------------------------------
     */
    
    public Literal getLiteral() {
        return null;
    }
    
    public void setLiteral(Literal literal) {
        // Basic objects don't handle literals
    }
    
    public boolean canSetLiteral() {
        return false;
    }
    
    public boolean canLiteralSurvive(Frame frame) {
        return true;
    }
    
    public boolean canUpdateLiteral(Frame frame, OPCode op) {
        return canSetLiteral();
    }
    
    public boolean hasLiteral() {
        return false;
    }
    
    /**
     * If is a literal, modify it
     * @param op The operation to preform on the literal
     * @param lit The value to use in update
     * @return True if should remove the containing op command
     */
    public boolean updateLiteral(OPCode op, Literal lit) {
        return false;
    }
    
    /*
     * ------------------------------- Use Management -------------------------------
     */
    
    /**
     * Should be called from reverseOptimize to
     * keep track of the number of uses
     * @param by The object causing this to be set in use
     * @return True if first call (last use)
     */
    public boolean setInUse(IIntermediate by) {        
        uses += 1;        
        if (uses == 1) {
            statementOfLastUse = by;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * @param by The object to check if is last use by
     * @return True if last use is by this
     */
    public boolean isLastUse(IIntermediate by) {
        return by == statementOfLastUse;
    }
    
    public void removeInUse() { }
    
    public int getUses() {
        return uses;
    }
    
    /**
     * Called from forward optimize
     * @param by The calling object or statement
     */
    public void takeRegister(IIntermediate by) {
        // Basic objects don't have registers
    }
    
    /*
     * ------------------------------- To Target Tools -------------------------------
     */
    
    /**
     * Only TernaryObject should return true
     * @return True if TernaryObject
     */
    public boolean isTernary() {
        return false;
    }
    
    /**
     * @return True if this object is an expression
     */
    public boolean isExpression() {
        return false;
    }
    
    /**
     * @return True if this object should be grouped together
     */
    public boolean isGrouped() {
        return false;
    }
    
    /**
     * @return True if is an assignable variable. Anything added
     * to a Frame's `vars` should have this set to true
     */
    public boolean isVar() {
        return false;
    }
    
    /**
     * @return The creatable version of this
     */
    public BasicObject getForCreateVar() {
        return this;
    }
    
    /**
     * Get as an expression
     * @param container The container of the expression
     * @return ExpressionStatement
     */
    public final ExpressionStatement getAsExp(Statement container) {
        return LangCompiler.factory.ExpressionStatement(container, this);
    }
    
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
