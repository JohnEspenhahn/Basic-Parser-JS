package com.hahn.basic.intermediate.objects;

import lombok.NonNull;

import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangObject;

public abstract class BasicObject implements ITypeable, IHolderExcludeList {
    private String name;
    private Type type;
    private int uses;
    
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
     * Called while still compiling, do any finalizations
     * needed in order for this object to be used
     * @param c The calling statement
     * @return This
     */
    public BasicObject getForUse(Statement s) {
        return this;
    }
    
    /**
     * Should be called from reverseRegisterOptimize
     * Keep track of the number of uses
     * @return True if first call (last use)
     */
    public boolean setInUse() {        
        boolean firstCall = (uses == 0);
        uses += 1;
        
        return firstCall;
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
     * @return A final form object
     */
    public abstract LangObject toTarget();
    
    /**
     * @return The creatable version of this
     */
    public BasicObject getForCreateVar() {
        return this;
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
