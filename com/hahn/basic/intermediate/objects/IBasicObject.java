package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.util.structures.BitFlag;

import lombok.NonNull;

public interface IBasicObject extends IIntermediate, ITypeable {
	String getName();
	void setName(String name);
	void setType(@NonNull Type t);
	
	boolean hasFlag(BitFlag flag);
	int getFlags();
	
	IBasicObject castTo(Type type, CodeFile file, int row, int col);
	
	Literal getLiteral();
	void setLiteral(Literal literal);
	boolean hasLiteral();
	boolean canSetLiteral();
	boolean canUpdateLiteral(Frame frame, OPCode op);
	boolean canLiteralSurvive(Frame frame);
	boolean updateLiteral(OPCode op, Literal lit, CodeFile file);
	
	boolean setInUse(IIntermediate by);
	void decUses();
	boolean isLastUse(IIntermediate by);
	void removeInUse();
	int getUses();
	void takeRegister(IIntermediate by);
	
	boolean isLocal();
	boolean isTernary();
	boolean isPrefixIncDec();
	boolean isExpression();
	boolean isGrouped();
	boolean isVar();
	boolean isVarSuper();
	int getVarThisFlag();
	boolean isTemp();
	boolean isClassObject();
	IBasicObject getForCreateVar();
	IBasicObject getAccessedObject();
	IBasicObject getAccessedWithinVar();
	IBasicObject getAccessedAtIdx();
	
	ExpressionStatement getAsExp(Statement container);
}
