package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.util.structures.BitFlag;

import lombok.NonNull;

public abstract class BasicObject extends RawObject {
    private String name;
    private Type type;
    
    private int uses;
    private IIntermediate statementOfLastUse;
    
    /**
     * A simple object that doesn't do any complex
     * register or literal optimization
     * @param name The name of the object
     * @param type The type of the object
     */
    public BasicObject(String name, @NonNull Type type) {
        this.name = name;
        this.type = type;
        this.uses = 0;
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
    
    /**
     * Update the object's type
     * @param t The nonnull new type
     */
    public void setType(@NonNull Type t) {
        this.type = t;
    }
    
    /*
     * ------------------------------- Variable Management -------------------------------
     */
    
    /**
     * Checks if this object has a given flag
     * @param flag The flag to check
     * @return True if has the flag
     */
    public boolean hasFlag(BitFlag flag) {
        return false;
    }
    
    /**
     * Get the flag bitmap
     * @return The flag bitmap
     */
    public int getFlags() {
        return 0;
    }
    
    
    /*
     * ------------------------------- Literal Management -------------------------------
     */
    
    /**
     * @return The literal or null if has none
     */
    public Literal getLiteral() {
        return null;
    }
    
    public void setLiteral(Literal literal) {
        // Basic objects don't handle literals
    }
    
    /**
     * @return True if has a literal
     */
    public boolean hasLiteral() {
        return false;
    }
    
    /**
     * @return True if can override any current literal
     */
    public boolean canSetLiteral() {
        return false;
    }
    
    /**
     * Checks if the object has a literal that can be updated
     * @param frame The calling frame
     * @param op The operation that is trying to be performed
     * @return True if has a literal and it can be modified
     */
    public boolean canUpdateLiteral(Frame frame, OPCode op) {
        return hasLiteral() && canSetLiteral();
    }
    
    /**
     * Check if this object's literal can survive through
     * to this frame
     * @param frame The frame from which this is being call
     * @return False if the literal cannot survive and is
     * therefor invalid
     */
    public boolean canLiteralSurvive(Frame frame) {
        return true;
    }
    
    /**
     * If is a literal, modify it
     * @param op The operation to preform on the literal
     * @param lit The value to use in update. Can be null
     * @param file The file updating in
     * @return True if should remove the containing op command
     */
    public boolean updateLiteral(OPCode op, Literal lit, CodeFile file) {
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
     * Reduce the uses counter by 1
     */
    public void decUses() {
        uses -= 1;
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
     * @return True if this object is local to a frame
     */
    public boolean isLocal() {
        return false;
    }
    
    /**
     * Only TernaryObject should return true
     * @return True if TernaryObject
     */
    public boolean isTernary() {
        return false;
    }
    
    /**
     * Only Prefix OPCode expressions should return true
     * @return True if Prefix OPCode object
     */
    public boolean isPrefixIncDec() {
        return false;
    }
    
    /**
     * Expression objects, such as OPObject, should return true.
     * Used for checking the groupings of some statements
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
     * @return Only VarSuper should return true
     */
    public boolean isVarSuper() {
        return false;
    }
    
    /**
     * @return `This` type flag. (0 = NOT_THIS, 1 = IS_THIS, 2 = IS_IMPLIED_THIS)
     */
    public int getVarThisFlag() {
        return NOT_THIS;
    }
    
    /**
     * @return True if this objects data is transient and
     * the object can be reused
     */
    public boolean isTemp() {
        return false;
    }
    
    /**
     * @return True if this is an instance of ClassObject
     */
    public boolean isClassObject() {
        return false;
    }
    
    /**
     * @return The creatable version of this
     */
    public IBasicObject getForCreateVar() {
        return this;
    }
    
    /**
     * @return The reference to the variable actually being accessed
     */
    public IBasicObject getAccessedObject() {
        return this;
    }
    
    /**
     * Used by VarAccess
     * @return The var being accessed within
     */
    public IBasicObject getAccessedWithinVar() {
        return null;
    }
    
    /**
     * Used by VarAccess
     * @return The index of the variable being accessed within getVar()
     */
    public IBasicObject getAccessedAtIdx() {
        return null;
    }
    
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    public static final int NOT_THIS = 0, IS_THIS = 1, IS_IMPLIED_THIS = 2;
}
