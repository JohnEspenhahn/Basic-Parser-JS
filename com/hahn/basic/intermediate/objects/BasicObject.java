package com.hahn.basic.intermediate.objects;

import lombok.NonNull;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public abstract class BasicObject implements IIntermediate, ITypeable, IBasicHolderExcludeList {
    private String name;
    private Type type;
    
    private int uses;
    private IIntermediate statementOfLastUse;
    
    public BasicObject(String name, Type type) {
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
    
    public Literal getLiteral() {
        return null;
    }
    
    public boolean canSetLiteral() {
        return false;
    }
    
    public void setLiteral(Literal literal) { }
    
    public boolean hasLiteral() {
        return false;
    }
    
    /**
     * Called from forward optimize
     * @param by
     */
    public void takeRegister(IIntermediate by) {
        // Basic objects don't have registers
    }
    
    /**
     * @param t The type to cast to
     * @return A new, altered version of this
     */
    public BasicObject castTo(Type t) {
        return new BasicObjectHolder(this, t);
    }
    
    /*
     * ------------------------------- Use Management -------------------------------
     */
    
    /**
     * TODO: Should only be called once; but always called
     * prior to the object being used for the first time,
     * and before any other statements are added.
     * 
     * Called while still compiling, do any finalizations
     * needed in order for this object to be used
     * @param by The calling statement
     * @return This
     */
    public BasicObject getForUse(Statement by) {
        return this;
    }
    
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
    
    /*
     * ------------------------------- To Target Tools -------------------------------
     */
    
    /**
     * Convert to its final form
     * @param builder
     * @return A final form object
     */
    public abstract String toTarget(LangBuildTarget builder);
    
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
    public ExpressionStatement getAsExp(Statement container) {
        return LangCompiler.factory.ExpressionStatement(container, this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof BasicObject) {
            BasicObject bo = (BasicObject) o;
            return bo.getType().equals(getType()) && bo.getName().equals(getName());
        } else {
            return super.equals(o);
        }
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
