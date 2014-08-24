package com.hahn.basic.intermediate;

import static com.hahn.basic.definition.EnumToken.ADD;
import static com.hahn.basic.definition.EnumToken.AND;
import static com.hahn.basic.definition.EnumToken.BOOL_AND;
import static com.hahn.basic.definition.EnumToken.BOOL_OR;
import static com.hahn.basic.definition.EnumToken.BOR;
import static com.hahn.basic.definition.EnumToken.CHAR;
import static com.hahn.basic.definition.EnumToken.DIV;
import static com.hahn.basic.definition.EnumToken.DOT;
import static com.hahn.basic.definition.EnumToken.EQUALS;
import static com.hahn.basic.definition.EnumToken.FALSE;
import static com.hahn.basic.definition.EnumToken.FLOAT;
import static com.hahn.basic.definition.EnumToken.GTR;
import static com.hahn.basic.definition.EnumToken.GTR_EQU;
import static com.hahn.basic.definition.EnumToken.HEX_INTEGER;
import static com.hahn.basic.definition.EnumToken.INTEGER;
import static com.hahn.basic.definition.EnumToken.LESS;
import static com.hahn.basic.definition.EnumToken.LESS_EQU;
import static com.hahn.basic.definition.EnumToken.LSHIFT;
import static com.hahn.basic.definition.EnumToken.MOD;
import static com.hahn.basic.definition.EnumToken.MULT;
import static com.hahn.basic.definition.EnumToken.NOT;
import static com.hahn.basic.definition.EnumToken.NOTEQUAL;
import static com.hahn.basic.definition.EnumToken.NULL;
import static com.hahn.basic.definition.EnumToken.OPEN_PRNTH;
import static com.hahn.basic.definition.EnumToken.OPEN_SQR;
import static com.hahn.basic.definition.EnumToken.QUESTION;
import static com.hahn.basic.definition.EnumToken.RSHIFT;
import static com.hahn.basic.definition.EnumToken.STRING;
import static com.hahn.basic.definition.EnumToken.SUB;
import static com.hahn.basic.definition.EnumToken.TRUE;
import static com.hahn.basic.definition.EnumToken.XOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hahn.basic.Main;
import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.LiteralBool;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.VarAccess;
import com.hahn.basic.intermediate.objects.VarTemp;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.StructType;
import com.hahn.basic.intermediate.objects.types.StructType.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.CallFuncStatement;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.DefineVarStatement.DefinePair;
import com.hahn.basic.intermediate.statements.EndLoopStatement;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.ParamDefaultValStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.NestedListIterator;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.exceptions.DuplicateDefinitionException;

public class Frame extends Statement {    
    private final Frame parent;
    private final Node frameHead;
    
    // For compilation
    protected HashMap<String, AdvancedObject> vars;
    
    // For optimization
    private List<AdvancedObject> inUseVars;
    
    private EndLoopStatement endLoop;
    private boolean hasReturn;
    
    public Frame(Frame parent, Node head) {
    	this(parent, head, false);
    }
    
    public Frame(Frame parent, Node head, boolean loop) {
        super(null);
        
        this.parent = parent;
        this.frameHead = head;
        
        // For compilation
        this.vars = new HashMap<String, AdvancedObject>();
        
        // For optimization
        this.inUseVars = new ArrayList<AdvancedObject>();
        
        // Special loop handling
        if (loop) { this.endLoop = new EndLoopStatement(this); }
    }
    
    @Override
    public Frame getFrame() {
        return this;
    }
    
    public ClassType getClassIn() {
        if (parent != null) {
            return parent.getClassIn();
        } else {
            return null;
        }
    }
    
    public boolean hasFrameHead() {
        return frameHead != null;
    }
    
    protected Node getFrameHead() {
        return frameHead;
    }
    
    public Collection<AdvancedObject> getVars() {
        return vars.values();
    }
    
    public Frame getLoop() {
        if (endLoop != null) {
            return this;
        } else if (parent != null) {
            return parent.getLoop();
        } else {
            return null;
        }
    }
    
    public void flagHasReturn() {
        this.hasReturn = true;
    }
    
    @Override
    public boolean isBlock() {
        return true;
    }
    
    @Override
    public boolean hasReturn() {
        return hasReturn;
    }
    
    /**
     * Should be called from TO_TARGET
     * @return True if no target code
     */
    public boolean isEmpty() {
        return getTargetCode().isEmpty();
    }
    
    /**
     * Should be called from TO_TARGET
     * @return The size of the target code
     */
    public int getSize() {
        return getTargetCode().size();
    }
    
    @Override
    public String toTarget() {
        StringBuilder str = new StringBuilder();
        Iterator<Compilable> it = getTargetCode().iterator();
        while (it.hasNext()) {
            Compilable c = it.next();
            String cs = c.toTarget();
            if (cs != null && cs.length() > 0) {
                str.append(cs);
                
                boolean eol = !c.isBlock() && it.hasNext();
                if (Main.PRETTY || eol) {
                    str.append(LangCompiler.factory.getLangBuildTarget().getEOL());
                    
                    // Pretty print new line
                    if (Main.PRETTY && eol) str.append('\n');
                }
            }
        }
        
        return str.toString();
    }
    
    /*
     * =====================================================
     * START OPTIMIZE CODE
     * =====================================================
     */
    
    @Override
    public boolean useAddTargetCode() {
        return true;
    }
    
    @Override
    public void addTargetCode() {
        if (hasFrameHead()) {
            handleBlock(frameHead);
        }
        
        if (endLoop != null) {
            addCode(endLoop);
            endLoop = null;
        }
    }
    
    public void addInUseVar(AdvancedObject o) {
        if (!isInUsevar(o)) {
            updateParallelVar(o);
            
            inUseVars.add(o);
        }
    }
    
    public void removeInUseVar(AdvancedObject o) {
        inUseVars.remove(o);
    }
    
    private void updateParallelVar(AdvancedObject o) {
        for (AdvancedObject otherObj: inUseVars) {
            otherObj.addParallelObj(o);
        }
        
        if (parent != null) {
            parent.updateParallelVar(o);
        }
    }
    
    private boolean isInUsevar(AdvancedObject o) {
        if (inUseVars.contains(o)) {
            return true;
        } else if (parent != null) {
            return parent.isInUsevar(o);
        } else {
            return false;
        }
    }
    
    /*
     * =====================================================
     * START COMPILE CODE
     * =====================================================
     */
    
    @Override
    public boolean reverseOptimize() {
        super.reverseOptimizeTargetCode();
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        super.forwardOptimizeTargetCode();
        
        // Release the frame's variables
        inUseVars.clear();
        for (AdvancedObject o: vars.values()) {
            o.releaseRegister();
        }
        
        return false;
    }
    
    /**
     * Create a temporary variable of type `type`
     * @param type The type of the variable to create
     * @return The new variable
     */
    public VarTemp createTempVar(Type type) {
        return (VarTemp) addVar(new VarTemp(this, type));
    }
    
    /**
     * Add a variable to this frame <br>
     * <b>Precondition:</b> Main.setLine
     * @param var The variable to add
     * @return The variable added
     * @throws DuplicateDefinitionException If the variable is already defined in this scope
     */
    public AdvancedObject addVar(AdvancedObject var) {        
        String name = var.getName();
        if (safeGetLocalVar(name) == null) {
            trackVar(var);
            vars.put(name, var);
            return var;
        } else {
            throw new DuplicateDefinitionException("The variable `" + var + "` is already defined in this scope");
        }
    }
    
    /**
     * All variables used within this frame, including child frames,
     * are called to this function to be tracked
     * @param var The variable to be tracked
     */
    protected void trackVar(AdvancedObject var) {
        if (parent != null) {
            parent.trackVar(var);
        }
    }
    
    /**
     * Get a local or instance variable related to this
     * frame up to and including the global frame
     * @param name The name of the variable
     * @return The variable found or null
     */
    public BasicObject safeGetVar(String name) {
        // Local var
        BasicObject obj = safeGetLocalVar(name);
        if (obj != null) return obj;
        
        // Instance var
        obj = safeGetInstanceVar(name);
        if (obj != null) return obj;
        
        // TODO get static class pointer
        
        return null;
    }
    
    /**
     * Get a variable from this frame or parent frame up to
     * and including the global frame
     * @param name The name of the variable
     * @return The variable found or null
     */
    public BasicObject safeGetLocalVar(String name) {
        // Local var
        BasicObject obj = vars.get(name);
        if (obj != null) {
            return obj;
        }
        
        // Var from parent
        if (parent != null) {
            obj = parent.safeGetVar(name);
            if (obj != null) {
                if (endLoop != null) {
                    endLoop.addVar(obj);                
                }
                
                return obj;
            }
        }
        
        return null;
    }
    
    public BasicObject safeGetInstanceVar(String name) {
        return null;
    }
    
    public BasicObject getVar(Node nameNode) {
        String name = nameNode.getValue();
        BasicObject obj = safeGetVar(name);
        
        // Found
        if (obj != null) {
            ClassType classIn = getClassIn();
            if (classIn != null && obj.hasFlag(BitFlag.PRIVATE) && (!obj.isVarThis() || !classIn.getDefinedParams().contains(obj.getAccessedObject()))) {
                throw new CompileException("The field `" + name + "` is private", nameNode);
            }
            
            return obj;
        } else {
            throw new CompileException("Variable `" + name + "` is not defined in this scope", nameNode);
        }
    }
    
    public String getLabel(String name) {
        return LangCompiler.getLabel(name, this);
    }
    
    /*
     * =====================================================
     * PARSING CODE
     * =====================================================
     */
    
    /**
     * `Block` handler
     * @param head EnumExpression.BLOCK
     */
    public void handleBlock(Node head) {
        List<Node> children = head.getAsChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            Enum<?> token = child.getToken();
            
            if (token == EnumExpression.BLOCK || token == EnumExpression.BLOCK_CNTNT || token == EnumExpression.CLASS_CNTNT) {
                handleBlock(child);
            } else if (token == EnumExpression.DEFINE) {
                addCode(defineVar(child));
            } else if (token == EnumExpression.STRUCT) {
                defineStruct(child);
            } else if (token == EnumExpression.CLASS) {
                defineClass(child);
            } else if (token == EnumExpression.DEF_FUNC) {
                defineFunc(child);
            } else if (token == EnumExpression.CONSTRUCTOR) {
                defineConstructor(child, null);
            } else if (token == EnumExpression.RETURN) {
                addCode(doReturn(child));
            } else if (token == EnumToken.CONTINUE) {
                addCode(doContinue(child));
            } else if (token == EnumToken.BREAK) {
                addCode(doBreak(child));
            } else if (token == EnumToken.IMPORT) {
                doImport(child);
            } else if (token == EnumExpression.IF_STMT) {
                addCode(ifStatement(child));
            } else if (token == EnumExpression.WHILE_STMT) {
                addCode(whileStatement(child));
            } else if (token == EnumExpression.FOR_STMT) {
                addCode(forStatement(child));
            } else if (token == EnumExpression.DIRECTIVE) {
                handleDirective(child);
            } else if (token == EnumExpression.EXPRESSION) {
                addCode(handleStatementExpression(child));
            } else if (token == EnumToken.EOL || token == EnumToken.OPEN_BRACE || token == EnumToken.CLOSE_BRACE) {
                continue;
            } else {
                throw new CompileException("Illegal left-hand side token `" + child + "`", child);
            }
        }
    }

    /**
     * `Import` handler
     * @param head EnumToken.IMPORT
     */
    public void doImport(Node head) {
        String name = head.getValue().replace("import ", "").trim();
        LangCompiler.addLibrary(name);
    }
    
    /**
     * Access a variable
     * @param head EnumExpression.ACCESS
     * @return The object with the retrieved value
     * @throws CompileException If illegal indexing of a variable
     */
    private BasicObject accessVar(Node head) {
        return accessVar(new NestedListIterator<Node>(head.getAsChildren()), false);
    }
    
    /**
     * Access a variable
     * @param it Iterator of children of EnumExpression.ACCESS
     * @param leaveLast If true will return before processing the last access expression
     * @return The object with the retrieved value
     * @throws CompileException If illegal indexing of a variable
     */
    private BasicObject accessVar(NestedListIterator<Node> it, boolean leaveLast) {
        Node nameNode = it.next();
        if (leaveLast && !it.hasNext()) {
            it.previous();
            return null;
        }
        
        BasicObject obj = getVar(nameNode);
        if (it.hasNext()) {
            if (obj.getType().doesExtend(Type.STRUCT)) {
                return inAccessVar(obj, it.enter(it.next().getAsChildren()), leaveLast);
            } else {
                throw new CompileException("Illegal attempt to index var of type '" + obj.getType() + "'", nameNode);
            }
        } else {
            return obj;
        }
    }
    
    /**
     * Actually do the accessing of the var
     * @param obj The var object
     * @param head EnumExpression.IN_ACCESS
     * @return The object the with retrieved value
     */
    private BasicObject inAccessVar(BasicObject obj, NestedListIterator<Node> it, boolean leaveLast) {        
        ExpressionStatement exp = LangCompiler.factory.ExpressionStatement(this, obj);
        while (it.hasNext()) {
            Type type = exp.getObj().getType();
            
            Node child = it.next();
            Enum<?> accessMarker = child.getToken();
            if (accessMarker == OPEN_SQR && type.doesExtend(Type.ARRAY)) {
                BasicObject offset = handleExpression(it.next()).getAsExpObj();
                it.next(); // Skip CLOSE_SQR
                
                if (leaveLast && !it.hasNext()) {
                    for (int i = 0; i < 3; i++) it.previous();
                    break;
                }
                
                @SuppressWarnings("unchecked")
                ParameterizedType<ITypeable> arrType = (ParameterizedType<ITypeable>) type;
                exp.setObj(LangCompiler.factory.VarAccess(exp, exp.getObj(), offset, arrType.getTypable(0).getType(), child.getRow(), child.getCol()), child);
            } else if (accessMarker == DOT && type.doesExtend(Type.STRUCT)) {               
                Node nameNode = it.next();
                
                if (leaveLast && !it.hasNext()) {
                    it.previous();
                    break;
                }
                
                StructParam sp = exp.getObj().getType().getAsStruct().getParam(nameNode);
                
                exp.setObj(LangCompiler.factory.VarAccess(exp, exp.getObj(), sp, sp.getType(), child.getRow(), child.getCol()), child);
            } else {
                throw new CompileException("Illegal attempt to index var `" + exp.getObj() + "` of type `" + exp.getObj().getType() + "`", child);
            }
        }
        
        return exp.getAsExpObj();
    }
    
    /**
     * `Modify` var handler
     * @param head EnumExpression.MODIFY
     * @return The command to do the modification
     */
    public OPObject modifyVar(Node head) {
        List<Node> children = head.getAsChildren();
        
        Node varNode = children.get(0);
        BasicObject var = accessVar(varNode);
        
        Node objNode = children.get(2);
        BasicObject obj = handleExpression(objNode).getAsExpObj();
        
        switch (children.get(1).getValue()) {
            case "=": 
                return updateVar(var, varNode, obj, objNode, OPCode.SET);
            case "+=":
                return updateVar(var, varNode, obj, objNode, OPCode.ADDE);
            case "-=":
                return updateVar(var, varNode, obj, objNode, OPCode.SUBE);
            case "*=":
                return updateVar(var, varNode, obj, objNode, OPCode.MULE);
            case "/=":
                return updateVar(var, varNode, obj, objNode, OPCode.DIVE);
            case "%=":
                return updateVar(var, varNode, obj, objNode, OPCode.MODE);
            case "&=":
                return updateVar(var, varNode, obj, objNode, OPCode.ANDE);
            case "|=":
                return updateVar(var, varNode, obj, objNode, OPCode.BORE);
            case "^=":
                return updateVar(var, varNode, obj, objNode, OPCode.XORE);
           default:
               throw new RuntimeException("Unhandled modify var '" + children.get(1).getValue() + "'");
        }
    }
    
    /**
     * Do the modification of a variable
     * @param var The variable to modify
     * @param varNode The node by which the var is defined
     * @param obj The object doing the modification
     * @param objNode The node by which the obj is defined
     * @param op The operation to perform on the variable
     * @return ArithmeticSetObject to update the var
     */
    protected OPObject updateVar(BasicObject var, Node varNode, BasicObject obj, Node objNode, OPCode op) {
        return LangCompiler.factory.ArithmeticSetObject(this, op, var, varNode, obj, objNode);
    }
    
    /**
     * `Define` var handler
     * @param head EnumExpression.DEFINE
     * @return Statement to define the vars
     */
    public DefineVarStatement defineVar(Node head) {
        return defineVar(head, null, true);
    }
    
    protected void defineVar(Node head, StructType struct) {
        defineVar(head, struct, false);
    }
    
    private DefineVarStatement defineVar(Node head, StructType struct, boolean canInit) {        
        Iterator<Node> it = Util.getIterator(head);
        
        // Init var
        int flags = 0;
        Type type = Type.UNDEFINED;
        DefineVarStatement define = LangCompiler.factory.DefineVarStatement(this, false);
        
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumExpression.FLAG) {
                flags |= BitFlag.valueOf(node);
            } else if (token == EnumExpression.TYPE) {
                type = Type.fromNode(node);
            } else if (token == EnumToken.IDENTIFIER) {
                String name = node.getValue();
                
                // Create var
                final BasicObject obj;
                if (struct != null) {
                    obj = struct.putParam(new Param(name, type, flags), node);
                } else {
                    obj = LangCompiler.factory.VarLocal(this, name, type, flags);
                }
                
                // Modify var
                boolean hasInit = false;
                if (it.hasNext()) {
                    Node nextHead = it.next();
                    Enum<?> nextToken = nextHead.getToken();
                    
                    if (nextToken == EnumExpression.DEF_MODIFY) {
                        if (!canInit) {
                            throw new CompileException("Can not initialize `" + obj.getName() + "` here", nextHead);
                        }
                        
                        List<Node> modify_children = nextHead.getAsChildren();
                        
                        Node equNode = modify_children.get(0);
                        Node expNode = modify_children.get(1);
                        
                        BasicObject o = handleExpression(expNode).getAsExpObj();
                        
                        if (struct instanceof ClassType) {
                            defineClassVar((ClassType) struct, obj, node, o, expNode);
                        }
                        
                        define.addVar(obj, o, equNode);                        
                        hasInit = true;
                    }
                }
                
                // Default value
                if (!hasInit && canInit) {
                    BasicObject defaultVal = LiteralNum.UNDEFINED;
                    if (type.doesExtend(Type.OBJECT)) defaultVal = LiteralNum.NULL;
                    else if (type.doesExtend(Type.STRUCT)) defaultVal = LangCompiler.factory.DefaultStruct(type.getAsStruct());
                    
                    // Do set value
                    if (struct instanceof ClassType) {
                        defineClassVar((ClassType) struct, obj, node, defaultVal, node);
                    }
                    
                    define.addVar(obj, defaultVal, node);
                }
                
                // Make var available
                if (struct == null) {
                    Main.setLine(node.getRow(), node.getCol());
                    addVar((AdvancedObject) obj);
                }
            }
        }
        
        define.setFlags(flags);
        
        return define;
    }
    
    private void defineClassVar(ClassType classIn, BasicObject var, Node varNode, BasicObject val, Node valNode) {
        VarAccess access = LangCompiler.factory.VarAccess(this, classIn.getThis(), var, var.getType(), varNode.getRow(), varNode.getCol());
        OPObject op = LangCompiler.factory.OPObject(this, OPCode.SET, access, varNode, val, valNode);
        classIn.addInitStatement(LangCompiler.factory.ExpressionStatement(this, op));
    }
    
    /**
     * `Return` statement handler
     * @param head EnumExpression.RETURN or null
     * @return The statement to do the return
     */
    public Compilable doReturn(Node head) {
        Frame f = this;
        while (f.parent != LangCompiler.getGlobalFrame()) {
            f = f.parent;
        }
        
        if (!(f instanceof FuncHead)) {
            throw new CompileException("Can only return from a function", head);
        }
        
        FuncHead func = (FuncHead) f;
        
        // Set result
        BasicObject result = null;
        List<Node> children = head.getAsChildren();
        if (children.size() > 1) {
            Node resultNode = children.get(1);
            result = handleExpression(resultNode).getAsExpObj();
            
            Type newType = result.getType().autocast(func.getReturnType(), resultNode.getRow(), resultNode.getCol(), false);
            if (newType == null) {
                throw new CompileException("Invalid return type. Expected `" + func.getReturnType() + "` but got `" + result.getType() + "`", resultNode);
            }
        }
        
        // Return
        return LangCompiler.factory.ReturnStatement(this, (FuncHead) f, result);
    }
    
    /**
     * `Struct` definition handler
     * @param head EnumExpression.STRUCT
     */
    public void defineStruct(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        it.next(); // skip 'struct'
        
        Node nameNode = it.next();
        Main.setLine(nameNode.getRow(), nameNode.getCol());
        StructType struct = Type.STRUCT.extendAs(nameNode.getValue(), 0);
        
        while (it.hasNext()) {
            Node next = it.next();
            
            if (next.getToken() == EnumExpression.DEFINE) {
                defineVar(next, struct);
            }
        }        
    }
    
    /**
     * `Class` definition handler
     * @param head EnumExpression.CLASS
     */
    public void defineClass(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        it.next(); // skip 'class'
        
        Node nameNode = it.next();
        Node parentNode = null;
        int flags = 0;
        
        // Don't add class code if library
        if (Main.LIBRARY) {
            flags |= BitFlag.SYSTEM.b;
        }
        
        // Get parent classes
        Node node;
        while(true) {
            node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumExpression.C_FLAG) {
                flags |= BitFlag.valueOf(node);
            } else if (token == EnumExpression.C_PARENT) {
                List<Node> children = node.getAsChildren();
                Node ckey = children.get(0);
                Node cvalue = children.get(1);
                
                if (ckey.getToken() == EnumToken.EXTENDS) {
                    if (parent == null) parentNode = cvalue;
                    else throw new CompileException("Can only extend one class", ckey);
                } else {
                    throw new RuntimeException("Unhandled class parent key `" + ckey + "`");
                }
            } else {
                break;
            }
        }
        
        Type parentType = (parentNode == null ? Type.OBJECT : Type.fromNode(parentNode));
        if (parentType.doesExtend(Type.OBJECT)) {
            if (((ClassType) parentType).hasFlag(BitFlag.FINAL)) {
                throw new CompileException("The class `" + nameNode + "` cannot extend the final class `" + parentType + "`", parentNode);
            }
            
            ClassType classType = ((ClassType) parentType).extendAs(nameNode.getValue(), flags);
            
            // Handle class content
            handleClassContent(nameNode, classType, it);
        } else {
            throw new CompileException("Cannot extend the non-class type `" + parentType + "`", parentNode);
        }
    }
    
    private void handleClassContent(Node nameNode, ClassType classIn, Iterator<Node> it) {        
        while (it.hasNext()) {
            Node child = it.next();
            Enum<?> token = child.getToken();
            
            if (token == EnumExpression.CLASS_CNTNT) {
                handleClassContent(nameNode, classIn, Util.getIterator(child));
            } else if (token == EnumExpression.DEF_FUNC) {
                defineClassFunc(child, classIn);
            } else if (token == EnumExpression.CONSTRUCTOR) {
                defineConstructor(child, classIn);
            } else if (token == EnumExpression.DEFINE) {
                defineVar(child, classIn, true);
            } else if (token == EnumToken.EOL) {
                continue;
            } else if (token == EnumToken.CLOSE_BRACE) {
                break;
            } else {
                throw new RuntimeException("Unhandled class content token `" + token + "`");
            }
        }
    }
    
    /**
     * Defines a function in the global frame
     * @param head EnumExpression.DEF_FUNC
     */
    public void defineFunc(Node head) {
        doDefineFunc(head, null, false);
    }
    
    /**
     * Define an anonymous functions
     * @param head EnumExpression.ANON_FUNC
     * @return Pointer to the anonymous function
     */
    public FuncPointer defineAnonFunc(Node head) {
        return doDefineFunc(head, null, true);
    }
    
    /**
     * Define a function in a class
     * @param head EnumExpression.DEF_FUNC
     * @param classIn The class to define the function in
     */
    public FuncPointer defineClassFunc(Node head, ClassType classIn) {
        return doDefineFunc(head, classIn, false);
    }
    
    public FuncPointer defineConstructor(Node head, ClassType classIn) {
        if (classIn == null) throw new CompileException("Illegal definition of a constructor outside of a class", head);
        
        return defineClassFunc(head, classIn);
    }
    
    public FuncPointer doDefineFunc(Node head, ClassType classIn, boolean anonymous) {
        Iterator<Node> it = Util.getIterator(head);
        
        Type rtnType = Type.VOID;
        Node nameNode = null;
        Node body = null;
        int flags = 0;
        
        List<DefinePair> inits = new ArrayList<DefinePair>();        
        List<Param> params = new ArrayList<Param>();
        
        while (it.hasNext()) {
            Node child = it.next();
            Enum<?> token = child.getToken();
            
            if (token == EnumToken.FUNCTION || token == EnumToken.CONSTRUCTOR) {
                nameNode = child;
            } else if (token == EnumExpression.TYPE) {
                rtnType = Type.fromNode(child);
            } else if (token == EnumExpression.F_FLAG) {
                flags |= BitFlag.valueOf(child);
            } else if (token == EnumToken.IDENTIFIER) {
                nameNode = child;
            } else if (token == EnumExpression.DEF_PARAMS) {      
                Iterator<Node> pIt = Util.getIterator(child);
                
                while (pIt.hasNext()) {
                    Node pNode = pIt.next();
                    Enum<?> pToken = pNode.getToken();
                    
                    if (pToken == EnumToken.COMMA) {
                        continue;
                    } else if (pToken == EnumToken.ASSIGN) {
                        BasicObject pVal = handleExpression(pIt.next()).getAsExpObj();
                        inits.add(new DefinePair(pNode, params.get(params.size() - 1), pVal));
                    } else {
                        Type pType = Type.fromNode(pNode);
                        String pName = pIt.next().getValue();
                        
                        params.add(new Param(pName, pType));
                    }
                }
            } else if (token == EnumExpression.BLOCK) {
                if (!Main.LIBRARY) body = child;
                else body = null;
            }
        }
        
        // Convert params list to array
        Param[] aParams = params.toArray(new Param[params.size()]);
           
        // Define function
        FuncHead func;
        if (!anonymous) {
            // Named
            String name = nameNode.getValue();
            Main.setLine(nameNode.getRow(), nameNode.getCol());
            
            if (classIn == null) {
                func = LangCompiler.defineFunc(LangCompiler.getGlobalFrame(), body, name, false, rtnType, aParams);
            } else {
                func = classIn.defineFunc(body, name, false, rtnType, aParams);
            }
        } else {
            // Anonymous
            String name = getLabel("afunc");
            nameNode.setValue(name);
            
            func = LangCompiler.defineFunc(this, body, name, false, rtnType, aParams);
        }
        
        // Put flags
        func.setFlags(flags);
        
        // Put default value
        if (!inits.isEmpty()) {
            ParamDefaultValStatement defaultVal = LangCompiler.factory.ParamDefaultValStatement(func, false);
            for (DefinePair pair: inits) defaultVal.addVar(pair);
            
            func.addCode(defaultVal);
        }
        
        // Return
        if (!anonymous) return null;
        else return LangCompiler.factory.FuncPointer(nameNode, null, new ParameterizedType<ITypeable>(Type.FUNC, (ITypeable[]) aParams, rtnType));
    }
    
    /**
     * Call a function
     * @param head EnumExpression.CALL_FUNC
     * @return The statement that should be added to the frame
     * and can extract the FuncCallPointer from
     */    
    public CallFuncStatement callFunc(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        
        // Determine function
        NestedListIterator<Node> accessIt = new NestedListIterator<Node>(it.next().getAsChildren());
        BasicObject objectIn = accessVar(accessIt, true);
        Node nameNode = accessIt.next();
        
        List<BasicObject> params = new ArrayList<BasicObject>();
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumExpression.CALL_PARAMS) {
                getFuncCallParams(node, params);
            }
        }
        
        BasicObject[] aParams = params.toArray(new BasicObject[params.size()]);
        
        // Get FuncCall object
        FuncCallPointer funcCallPointer = LangCompiler.factory.FuncCallPointer(nameNode, objectIn, aParams);
        return LangCompiler.factory.DefaultCallFuncStatement(this, funcCallPointer);
    }
    
    /**
     * Get the parameters of a function call
     * @param head EnumExpression.CALL_PARAMS
     * @param params The list to add the params to
     */
    private void getFuncCallParams(Node head, List<BasicObject> params) {
        if (head.getToken() != EnumExpression.CALL_PARAMS) return;
        
        Iterator<Node> it = Util.getIterator(head);
        while (it.hasNext()) {
            Node pNode = it.next();
            if (pNode.getToken() == EnumToken.COMMA) {
                continue;
            } else {
                BasicObject v = handleExpression(pNode).getAsExpObj();
                params.add(v);
            }
        }
    }
    
    /**
     * Parse expression node to get function pointer
     * @param head EnumExpression.FUNC_POINTER
     * @param objectIn The object the function is in or null
     * @return FuncPointer
     */
    private FuncPointer getFuncPointer(Node head, AdvancedObject objectIn) {
        Iterator<Node> it = Util.getIterator(head);
        
        Node nameNode = null;
        ParameterizedType<ITypeable> types = null;
        
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumToken.IDENTIFIER) {
                nameNode = node;
            } else if (token == EnumExpression.TYPE_LIST) {
                types = ParameterizedType.getParameterizedType(Type.FUNC, node, false);
            }
        }
        
        // If no types provided use default
        if (types == null) {
            types = new ParameterizedType<ITypeable>(Type.FUNC);
        }
        
        return LangCompiler.factory.FuncPointer(nameNode, objectIn, types);
    }
    
    /**
     * Create a new instance based on node data
     * @param head EnumExpression.CREATE
     * @return Object instance
     */
    public BasicObject createInstance(Node head) {
    	List<Node> children = head.getAsChildren();
    	
    	Node typeNode = children.get(1);
        Type type = Type.fromNode(typeNode);
        if (!type.doesExtend(Type.STRUCT)) {
            throw new CompileException("Cannot create a new instance of type `" + type + "`", typeNode);
        } else if (type instanceof ClassType && ((ClassType) type).hasFlag(BitFlag.ABSTRACT)) {
            throw new CompileException("Cannot create a new instance of abstract class `" + type + "`", typeNode);
        }
        
        List<BasicObject> params = new ArrayList<BasicObject>();
        getFuncCallParams(children.get(3), params);
                
        return LangCompiler.factory.NewInstance(type, typeNode, params);
    }
    
    /**
     * `If` statement handler
     * @param head EnumExpression.IF_STMT
     * @return The IfStatement
     */
    public Compilable ifStatement(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        
        List<Conditional> conditionals = new ArrayList<Conditional>();
        while (it.hasNext()) {
            Node child = it.next();
            Enum<?> token = child.getToken();
            
            if (token == EnumExpression.CONDITIONAL) {
                conditionals.add(createConditional(child));
            } else if (token == EnumExpression.BLOCK) {
                conditionals.add(new Conditional(this, child));
            }
        }
        
        return LangCompiler.factory.IfStatement(this, conditionals);
    }
    
    /**
     * @param head EnumExpression.CONDITIONAL
     * @return Conditional holder
     */
    private Conditional createConditional(Node head) {
        List<Node> children = head.getAsChildren();
        return new Conditional(this, children.get(1), children.get(3));
    }
    
    /**
     * `While` statement handler
     * @param head EnumExpression.WHILE_STMT
     * @return The WhileStatement
     */
    public Compilable whileStatement(Node head) {
        Node block = head.getAsChildren().get(1);
        List<Node> blockChildren = block.getAsChildren();
        
        return LangCompiler.factory.WhileStatement(this, blockChildren.get(1), blockChildren.get(3));
    }
    
    /**
     * `For` statement handler
     * @param head EnumExpression.FOR_STMT
     * @return The ForStatement
     */
    public Compilable forStatement(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        
        Node define = null, condition = null;
        List<Node> modification = new ArrayList<Node>();
        Node body = null;
        
        while (it.hasNext()) {
            Node child = it.next();
            
            if (!child.isTerminal()) {
                Enum<?> token = child.getToken();
                
                if (token == EnumExpression.EXPRESSION) {
                    condition = child;
                } else if (token == EnumExpression.DEFINE) {
                    define = child;
                } else if (token == EnumExpression.MODIFY) {
                    modification.add(child);
                } else if (token == EnumExpression.BLOCK) {
                    body = child;
                } else {
                    throw new RuntimeException("Unhandled expression '" + token + "' in for-loop definition");
                }
            }
        }
        
        return LangCompiler.factory.ForStatement(this, define, condition, modification, body);
    }
    
    /**
     * `Continue` handler
     * @param head EnumToken.CONTINUE
     * @return ContinueStatement
     */
    public Compilable doContinue(Node head) {        
        return LangCompiler.factory.ContinueStatement(this);
    }
    
    /**
     * `Break` handler
     * @param head EnumToken.BREAK
     * @return BreakStatement
     */
    public Compilable doBreak(Node head) {
        Frame loop = getLoop();
        if (loop == null) {
            throw new CompileException("No loop to break out of ", head);
        }
        
        return LangCompiler.factory.BreakStatement(this);
    }
    
    /**
     * Cast the next expression object
     * @param head EnumExpression.CAST
     * @param temp
     * @return ObjectHolder
     */
    private BasicObject doCast(Node head, BasicObject temp) {
        Iterator<Node> it = Util.getIterator(head);
        
        while (it.hasNext()) {
            Node typeNode = it.next();
            if (Type.isValidNode(typeNode)) {
                Type type = Type.fromNode(typeNode);
                
                it.next(); // Skip colon
                
                ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
                handleNextExpressionChild(it, nextExp, temp);
                
                return nextExp.getAsExpObj().castTo(type, typeNode.getRow(), typeNode.getCol());
            }
        }
        
        throw new RuntimeException("Invalid cast definition '" + head + "'");
    }
    
    /**
     * Handle a directive statement
     * @param head EnumExpression.DIRECTIVE
     */
    private void handleDirective(Node head) {
        List<Node> children = head.getAsChildren();
        String directive = children.get(1).getValue();
        
        switch (directive) {
        case "library":
            Main.LIBRARY = true;
            break;
        case "end_library":
            Main.LIBRARY = false;
            break;
        case "eof":
            Main.LIBRARY = false;
            break;
        default:
            throw new CompileException("Unknown preprocessor directive `" + directive + "`", head);
        }
    }
    
    /**
     * Handle an expression as a statement rather than an object
     * @param child EnumExpression.EXPRESSION
     * @return ExpressionStatement
     */
    private ExpressionStatement handleStatementExpression(Node child) {
        List<Node> children = child.getAsChildren();
        
        // Statement Expression are all valid
        if (children.size() == 1 && children.get(0).getToken() == EnumExpression.STMT_EXPRS) {
            return handleExpression(children.get(0));
            
        // Only other valid form is a ternary operation
        } else {
            ExpressionStatement exp = handleExpression(child);
            if (exp.getObj().isTernary()) {
                return exp;
            } else {
                throw new CompileException("Illegal left-hand side token `" + child + "`", child);
            }
        }
    }
    
    /**
     * Handle an expression
     * @param head EnumExpression.EXPRESSION or one of it's children
     * @return An object with the result of the expression
     */    
    public ExpressionStatement handleExpression(Node head) {
        return doHandleExpression(Util.getIterator(head));
    }
    
    public ExpressionStatement doHandleExpression(Iterator<Node> it) {
        ExpressionStatement exp = LangCompiler.factory.ExpressionStatement(this, null);
        
        // Add tokens
        while (it.hasNext()) {            
            handleNextExpressionChild(it, exp, null);
        }
 
        if (exp.getObj() == null) {
            throw new CompileException("Incomplete command"); 
        } else {
            return exp;
        }
    }
    
    public void handleNextExpressionChild(Iterator<Node> it, ExpressionStatement exp, BasicObject temp) {
        Node child = it.next();
        String val = child.getValue();
        Enum<?> token = child.getToken();
        
        BasicObject obj = handleNextExpressionChildObject(child, temp);
        if (obj != null) {
            exp.setObj(obj, child);
            
        } else if (token == QUESTION) {
            Node node_then = it.next();        
            it.next(); // skip colon
            Node node_else = it.next();
            
            exp.setObj(LangCompiler.factory.TernaryObject(exp, exp.getObj(), node_then, node_else, child.getRow(), child.getCol()), child);
            
        } else if (token == OPEN_PRNTH) {
            Node inPrnthNode = it.next();
            ExpressionStatement nextExp = doHandleExpression(Util.getIterator(inPrnthNode));
            nextExp.setForcedGroup(true);
            
            exp.setObj(nextExp, inPrnthNode);
            
            // Skip ending parenthesis
            it.next();
            
        } else if (token == NOT) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            exp.setObj(LangCompiler.factory.OPObject(exp, op, nextExp.getObj(), nextExp.getNode(), null, null), child);
        } else if (token == ADD || token == SUB || token == MULT || token == DIV || token == MOD || token == AND || token == BOR || token == XOR || token == LSHIFT || token == RSHIFT || token == BOOL_AND || token == BOOL_OR) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            exp.setObj(LangCompiler.factory.ArithmeticObject(exp, op, exp.getObj(), exp.getNode(), nextExp.getObj(), nextExp.getNode()), child);
        } else if (token == NOTEQUAL || token == EQUALS || token == LESS_EQU || token == GTR_EQU || token == LESS || token == GTR) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            if (temp == null) temp = createTempVar(Type.BOOL);
            exp.setObj(LangCompiler.factory.ConditionalObject(exp, op, exp.getObj(), exp.getNode(), nextExp.getObj(), nextExp.getNode(), temp), child);
            
        } else if (!child.isTerminal()) {
            exp.setObj(handleExpression(child), child);
            
        } else {
            throw new CompileException("Unexpected token `" + child + "`", child);
        }
    }
    
    public BasicObject handleNextExpressionChildObject(Node child, BasicObject temp) {
        Enum<?> token = child.getToken();
        
        if (child.isTerminal()) {
            String val = child.getValue();
            if (token == INTEGER || token == HEX_INTEGER || token == CHAR) {
                return Util.parseInt(child);
            } else if (token == FLOAT ){
                return Util.parseFloat(child);
            } else if (token == TRUE) {
                return new LiteralBool(true);
            } else if (token == FALSE) {
                return new LiteralBool(false);
            } else if (token == STRING) {
                return LangCompiler.getString(val.substring(1, val.length() - 1));
            } else if (token == NULL) {
                return LiteralNum.NULL;
            }
        } else {
            if (token == EnumExpression.ACCESS) {
                return accessVar(child);
            } else if (token == EnumExpression.MODIFY) {
                return modifyVar(child);
            } else if (token == EnumExpression.CREATE) {
                return createInstance(child);
            } else if (token == EnumExpression.CAST) {
                return doCast(child, temp);
            } else if (token == EnumExpression.FUNC_POINTER) {
                return getFuncPointer(child, null);
            } else if (token == EnumExpression.ANON_FUNC) {
                return defineAnonFunc(child);
            } else if (token == EnumExpression.CALL_FUNC) {
                CallFuncStatement fc = callFunc(child);
                return fc.getFuncCallPointer();
            } else {
                return handleExpression(child).getAsExpObj();
            }
        }
        
        return null;
    }

    @Override
    public String toString() {
        return getTargetCodeString();
    }
}
