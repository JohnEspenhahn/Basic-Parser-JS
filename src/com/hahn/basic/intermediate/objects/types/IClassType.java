package com.hahn.basic.intermediate.objects.types;

import java.util.Collection;
import java.util.List;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.function.FuncGroup;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.ClassObject;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.CommandFactory;

public interface IClassType {
	ClassObject getClassObj();
	Frame getContainingFrame();
	CodeFile getFile();
	CommandFactory getFactory();
	Compiler getCompiler();
	ClassType setSystemClass();
	Var getThis();
	Var getImpliedThis();
	Var getSuper();
	ClassType extendAs(Frame containingFrame, String name, int flags);
	ClassType extendAs(Frame containingFrame, String name, List<IBasicObject> ps, int flags);
	ClassType setTypeParams(int num);
	ClassType systemParam(String name, Type type, String outName, boolean override);
	Frame getStaticFrame();
	void addInitStatements(List<Statement> inits);
	void addInitStatement(Statement init);
	Frame getInitFrame();
	FuncHead defineFunc(CodeFile file, Node head, String inName, String outName, Type rtnType, Param... params);
	FuncHead defineFunc(CodeFile file, Node head, boolean override, String inName, String outName, Type rtnType, Param... params);
	Collection<FuncGroup> getDefinedFuncs();
	FuncHead getConstructor(ITypeable[] types);
	FuncHead getFunc(IBasicObject objIn, Node nameNode, ITypeable[] types);
	FuncHead getFunc(IBasicObject objIn, Node nameNode, ITypeable[] types, boolean safe, boolean shallow);	
}
