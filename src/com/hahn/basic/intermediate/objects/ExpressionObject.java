package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.util.structures.BitFlag;

public class ExpressionObject extends RawObject {
    private IBasicObject heldObj;
    
    private ExpressionStatement statement;
    
    public ExpressionObject(ExpressionStatement s) {        
        this.heldObj = s.getObj();
        
        this.statement = s;
    }
    
    public ExpressionStatement getStatement() {
        return statement;
    }
    
    public IBasicObject getHeldObject() {
        return heldObj;
    }
    
    @Override
    public boolean isGrouped() {
        return getStatement().isGrouped();
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        statement.reverseOptimize();
        
        return true;
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        statement.forwardOptimize();
    }
    
    @Override
	public String getName() {
		// TODO Auto-generated method stub
		return heldObj.getName();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		heldObj.setName(name);
	}

	@Override
	public boolean hasFlag(BitFlag flag) {
		// TODO Auto-generated method stub
		return heldObj.hasFlag(flag);
	}

	@Override
	public int getFlags() {
		// TODO Auto-generated method stub
		return heldObj.getFlags();
	}

	@Override
	public Literal getLiteral() {
		// TODO Auto-generated method stub
		return heldObj.getLiteral();
	}

	@Override
	public void setLiteral(Literal literal) {
		// TODO Auto-generated method stub
		heldObj.setLiteral(literal);
	}

	@Override
	public boolean hasLiteral() {
		// TODO Auto-generated method stub
		return heldObj.hasLiteral();
	}

	@Override
	public boolean canSetLiteral() {
		// TODO Auto-generated method stub
		return heldObj.canSetLiteral();
	}

	@Override
	public boolean canUpdateLiteral(Frame frame, OPCode op) {
		// TODO Auto-generated method stub
		return heldObj.canUpdateLiteral(frame, op);
	}

	@Override
	public boolean canLiteralSurvive(Frame frame) {
		// TODO Auto-generated method stub
		return heldObj.canLiteralSurvive(frame);
	}

	@Override
	public boolean updateLiteral(OPCode op, Literal lit, CodeFile file) {
		// TODO Auto-generated method stub
		return heldObj.updateLiteral(op, lit, file);
	}

	@Override
	public void decUses() {
		// TODO Auto-generated method stub
		heldObj.decUses();
	}

	@Override
	public boolean isLastUse(IIntermediate by) {
		// TODO Auto-generated method stub
		return heldObj.isLastUse(by);
	}

	@Override
	public void removeInUse() {
		// TODO Auto-generated method stub
		heldObj.removeInUse();
	}

	@Override
	public int getUses() {
		// TODO Auto-generated method stub
		return heldObj.getUses();
	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return heldObj.isLocal();
	}

	@Override
	public boolean isTernary() {
		// TODO Auto-generated method stub
		return heldObj.isTernary();
	}

	@Override
	public boolean isPrefixIncDec() {
		// TODO Auto-generated method stub
		return heldObj.isPrefixIncDec();
	}

	@Override
	public boolean isExpression() {
		// TODO Auto-generated method stub
		return heldObj.isExpression();
	}

	@Override
	public boolean isVar() {
		// TODO Auto-generated method stub
		return heldObj.isVar();
	}

	@Override
	public boolean isVarSuper() {
		// TODO Auto-generated method stub
		return heldObj.isVarSuper();
	}

	@Override
	public int getVarThisFlag() {
		// TODO Auto-generated method stub
		return heldObj.getVarThisFlag();
	}

	@Override
	public boolean isTemp() {
		// TODO Auto-generated method stub
		return heldObj.isTemp();
	}

	@Override
	public boolean isClassObject() {
		// TODO Auto-generated method stub
		return heldObj.isClassObject();
	}

	@Override
	public IBasicObject getForCreateVar() {
		// TODO Auto-generated method stub
		return heldObj.getForCreateVar();
	}

	@Override
	public IBasicObject getAccessedObject() {
		// TODO Auto-generated method stub
		return heldObj.getAccessedObject();
	}

	@Override
	public IBasicObject getAccessedWithinVar() {
		// TODO Auto-generated method stub
		return heldObj.getAccessedWithinVar();
	}

	@Override
	public IBasicObject getAccessedAtIdx() {
		// TODO Auto-generated method stub
		return heldObj.getAccessedAtIdx();
	}

	@Override
	public void setType(Type t) {
		// TODO Auto-generated method stub
		heldObj.setType(t);
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return heldObj.getType();
	}
}
