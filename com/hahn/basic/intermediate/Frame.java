package com.hahn.basic.intermediate;

import static com.hahn.basic.definition.EnumToken.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import com.hahn.basic.intermediate.objects.VarTemp;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Struct.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.CallFuncStatement;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.EndLoopStatement;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CastException;
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
    
    public boolean hasFrameHead() {
        return frameHead != null;
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
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        StringBuilder str = new StringBuilder();
        for (Compilable c: getTargetCode()) {
            String cs = c.toTarget(builder);
            if (cs != null && cs.length() > 0) {
                str.append(cs);
                
                if (!c.endsWithBlock()) {
                    str.append(builder.getEOL());
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
     * All variables used within this frame, including child frames,
     * are called to this function to be tracked
     * @param var The variable to be tracked
     */
    protected void trackVar(AdvancedObject var) {
        if (parent != null) {
            parent.trackVar(var);
        }
    }
    
    public boolean safeAddVar(AdvancedObject var) {
    	trackVar(var);
        
        String name = var.getName();
        if (safeGetVar(name) == null) {
            vars.put(name, var);
            return true;
        } else {
            return false;
        }
    }
    
    public AdvancedObject addVar(AdvancedObject var) {
        if (safeAddVar(var)) return var;
        else throw new DuplicateDefinitionException("The local var `" + var + "` is already defined");
    }
    
    public VarTemp createTempVar(Type type) {
        return (VarTemp) addVar(new VarTemp(this, type));
    }
    
    public AdvancedObject safeGetVar(String name) {
        // Local var
        AdvancedObject obj = vars.get(name);
        if (obj != null) {
            return obj;
        } 
        
        // Var from parent (still local)
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
    
    public AdvancedObject getVar(String name) {
        AdvancedObject obj = safeGetVar(name);
        
        // Not found
        if (obj != null) return obj;
        else throw new CompileException("Variable `" + name + "` is not defined in this scope");
    }
    
    public String getLabel(String name) {
        return LangCompiler.getLabel(name, this);
    }
    
    /**
     * `Block` handler
     * @param head EnumExpression.BLOCK
     */
    public void handleBlock(Node head) {
        List<Node> children = head.getAsChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            Enum<?> token = child.getToken();
            
            if (token == EnumExpression.BLOCK || token == EnumExpression.BLOCK_CNTNT) {
                handleBlock(child);
            } else if (token == EnumExpression.DEFINE) {
                addCode(defineVar(child));
            } else if (token == EnumExpression.STRUCT) {
                defineStruct(child);
            } else if (token == EnumExpression.DEF_FUNC) {
                defineFunc(child);
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
            } else if (token == EnumExpression.EXPRESSION) {
                addCode(handleStatementExpression(child));
            } else if (token == EnumToken.EOL || token == EnumToken.OPEN_BRACE || token == EnumToken.CLOSE_BRACE) {
                continue;
            } else {                
                throw new CompileException("Illegal left-hand side token `" + child + "`");
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
     * @return The object the with retrieved value
     */
    private BasicObject accessVar(Node head) {
        List<Node> children = head.getAsChildren();

        AdvancedObject obj = getVar(children.get(0).getValue());
        if (children.size() > 1) {
            if (obj.getType().doesExtend(Type.STRUCT)) {
                Node nextAccess = children.get(1);
                return inAccessVar(obj, nextAccess);
            } else {
                throw new CompileException("Invalid access of type '" + obj.getType() + "'");
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
    private BasicObject inAccessVar(AdvancedObject obj, Node head) {
        Type t = obj.getType();
        
        ExpressionStatement exp = LangCompiler.factory.ExpressionStatement(this, obj);
        Iterator<Node> it = Util.getIterator(head);
        while (it.hasNext()) {  
            Enum<?> accessMarker = it.next().getToken();
            if (accessMarker == OPEN_SQR && t.doesExtend(Type.ARRAY)) {
                BasicObject offset = handleExpression(it.next()).getAsExpObj();
                
                exp.setObj(LangCompiler.factory.VarAccess(exp, exp.getObj(), offset, Type.INT));                
                
                it.next(); // Skip CLOSE_SQR
            } else if (accessMarker == DOT && t.doesExtend(Type.STRUCT)) {               
                String name = it.next().getValue();
                StructParam sp = t.castToStruct().getStructParam(name);
                
                exp.setObj(LangCompiler.factory.VarAccess(exp, exp.getObj(), sp, sp.getType()));
            } else {
                throw new CompileException("Invalid access of `" + obj.getName() + "` of type `" + t + "`");
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
        
        BasicObject var = accessVar(children.get(0));
        BasicObject obj = handleExpression(children.get(2)).getAsExpObj();
        
        switch (children.get(1).getValue()) {
            case "=": 
                return updateVar(var, obj, OPCode.SET);
            case "+=":
                return updateVar(var, obj, OPCode.ADDE);
            case "-=":
                return updateVar(var, obj, OPCode.SUBE);
            case "*=":
                return updateVar(var, obj, OPCode.MULE);
            case "/=":
                return updateVar(var, obj, OPCode.DIVE);
            case "%=":
                return updateVar(var, obj, OPCode.MODE);
            case "&=":
                return updateVar(var, obj, OPCode.ANDE);
            case "|=":
                return updateVar(var, obj, OPCode.BORE);
            case "^=":
                return updateVar(var, obj, OPCode.XORE);
           default:
               throw new RuntimeException("Unhandled modify var '" + children.get(1).getValue() + "'");
        }
    }
    
    /**
     * Do the modification of a variable
     * @param var The variable to modify
     * @param obj The object doing the modification
     * @param op The operation to perform on the variable
     * @return Object to update the var
     */
    protected OPObject updateVar(BasicObject var, BasicObject obj, OPCode op) {
        return LangCompiler.factory.OPObject(this, op, var, obj);
    }
    
    /**
     * `Define` var handler
     * @param head EnumExpression.DEFINE
     * @return Statement to define the vars
     */
    public DefineVarStatement defineVar(Node head) {
        return defineVar(head, null, true);
    }
    
    protected DefineVarStatement defineVar(Node head, List<BasicObject> varsList) {
        return defineVar(head, varsList, false);
    }
    
    private DefineVarStatement defineVar(Node head, List<BasicObject> varsList, boolean canInit) {
        Iterator<Node> it = Util.getIterator(head);
        
        DefineVarStatement define = LangCompiler.factory.DefineVarStatement(this, false);
        
        // Init var
        Type type = Type.UNDEFINED;
        ArrayList<String> flags = new ArrayList<String>();
        
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumToken.FLAGS) {
                flags.add(node.getValue());
            } else if (token == EnumExpression.TYPE) {
                type = Type.fromNode(node);
            } else if (token == EnumToken.IDENTIFIER) {
                String name = node.getValue();
                
                // Create var
                final BasicObject obj;
                if (varsList != null) {
                    obj = new Param(name, type, flags);                    
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
                            throw new CompileException("Can not initialize `" + obj.getName() + "` here");
                        }
                        
                        List<Node> modify_children = nextHead.getAsChildren();
                        
                        BasicObject o = handleExpression(modify_children.get(1)).getAsExpObj();
                        define.addVar(obj, o);
                        
                        hasInit = true;
                    }
                }
                
                // Default value
                if (!hasInit && canInit) {
                    define.addVar(obj, LiteralNum.UNDEFINED);
                }
                
                // Make var available
                if (varsList != null) {
                    varsList.add(obj);
                } else {
                    addVar((AdvancedObject) obj);
                }
            }
        }
        
        define.setFlags(flags);
        
        return define;
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
            throw new CompileException("Can only return from a function");
        }
        
        FuncHead func = (FuncHead) f;
        
        // Set result
        BasicObject result = null;
        if (head != null) {
            List<Node> children = head.getAsChildren();
            if (children.size() > 1) {
                result = handleExpression(children.get(1)).getAsExpObj();
                
                try {
                    result.castTo(func.getReturnType());
                } catch (CastException e) {
                    throw new CastException("Invalid return type - ", e);
                }
            }
        }
        
        // Return
        return LangCompiler.factory.ReturnStatement(this, (FuncHead) f, result);
    }
    
    /**
     * `Struct` definition handler
     * @param EnumExpression.STRUCT
     */
    public void defineStruct(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        it.next(); // skip 'struct'
        
        String structName = it.next().getValue();
        List<BasicObject> structVars = new ArrayList<BasicObject>();
        
        while (it.hasNext()) {
            Node next = it.next();
            
            if (next.getToken() == EnumExpression.DEFINE) {
                defineVar(next, structVars);
            }
        }
        
        Type.STRUCT.extendAs(structName, structVars);
    }
    
    /**
     * Define a functions
     * @param head EnumExpression.DEF_FUNC
     */
    public void defineFunc(Node head) {
        doDefineFunc(head, false);
    }
    
    /**
     * Define an anonymous functions
     * @param head EnumExpression.ANON_FUNC
     */
    public FuncPointer defineAnonFunc(Node head) {
        return doDefineFunc(head, true);
    }
    
    public FuncPointer doDefineFunc(Node head, boolean anonymous) {
        Iterator<Node> it = Util.getIterator(head);
        
        Type rtnType = Type.VOID;
        String name = null;
        Node body = null;
        
        List<Param> params = new ArrayList<Param>();
        while (it.hasNext()) {
            Node parent = it.next();
            Enum<?> token = parent.getToken();
            
            if (token == EnumExpression.TYPE) {
                rtnType = Type.fromNode(parent);
            } else if (token == EnumToken.IDENTIFIER) {
                name = parent.getValue();
            } else if (token == EnumExpression.DEF_PARAMS) {      
                Iterator<Node> pIt = Util.getIterator(parent);
                
                while (pIt.hasNext()) {
                    Node pNode = pIt.next();
                    if (pNode.getToken() == EnumToken.COMMA) {
                        continue;
                    } else {
                        Node pType = pNode;
                        Node pName = pIt.next();
                        
                        params.add(new Param(pName.getValue(), Type.fromNode(pType)));
                    }
                }
            } else if (token == EnumExpression.BLOCK) {
                body = parent;
            }
        }
        
        // Convert params list to array
        Param[] aParams = params.toArray(new Param[params.size()]);
           
        // Define function
        if (!anonymous) {
            LangCompiler.defineFunc(body, name, false, rtnType, aParams);
            return null;
        } else {
            name = getLabel("afunc");
            
            LangCompiler.defineFunc(body, name, false, rtnType, aParams);
            return LangCompiler.factory.FuncPointer(name, new ParameterizedType<ITypeable>(Type.FUNC, (ITypeable[]) aParams));
        }
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
        String name = it.next().getValue();
        
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
        FuncCallPointer funcCallPointer = LangCompiler.factory.FuncCallPointer(name, aParams);
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
     * @return FuncPointer
     */
    private FuncPointer getFuncPointer(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        
        String name = null;
        ParameterizedType<ITypeable> types = null;
        
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumToken.IDENTIFIER) {
                name = node.getValue();
            } else if (token == EnumExpression.TYPE_LIST) {
                types = ParameterizedType.getParameterizedType(Type.FUNC, node, false);
            }
        }
        
        // If no types provided use default
        if (types == null) {
            types = new ParameterizedType<ITypeable>(Type.FUNC);
        }
        
        return LangCompiler.factory.FuncPointer(name, types);
    }
    
    /**
     * Create a new instance based on node data
     * @param head EnumExpression.CREATE
     * @return Object instance
     */
    public BasicObject createInstance(Node head) {
    	List<Node> children = head.getAsChildren();
    	
        Type type = Type.fromNode(children.get(1));
        
        List<BasicObject> params = new ArrayList<BasicObject>();
        getFuncCallParams(children.get(3), params);
        
        return LangCompiler.factory.NewInstance(type, params);
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
                    throw new CompileException("Unhandled expression '" + token + "' in for loop definition");
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
            throw new CompileException("Invalid use of `break`");
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
            
        Node typeNode = it.next();
        if (Type.isValidNode(typeNode)) {
            Type type = Type.fromNode(typeNode);
            
            it.next(); // Skip colon
            
            ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            return nextExp.getAsExpObj().castTo(type);
        }
        
        throw new RuntimeException("Invalid cast definition '" + head + "'");
    }
    
    private ExpressionStatement parseTernary(ExpressionStatement cnd_exp, Iterator<Node> it) {
        Node node_then = null, node_colon = null, node_else = null;
        for (int i = 0; i < 3; i++) {
            if (it.hasNext()) {
                if (i == 0) {
                    it = parseTernaryEnterExp(it);
                    node_then = it.next();
                } else if (i == 1) {
                    node_colon = it.next();
                } else if (i == 2) {
                    it = parseTernaryEnterExp(it);
                    node_else  = it.next();
                }
            } else {
                throw new CompileException("Unexpected end of input");
            }
        }
        
        if (node_colon.getToken() != EnumToken.COLON) {
            throw new CompileException("Expected `:` but got `" + node_colon + "`");
        }
        
        ExpressionStatement exp = LangCompiler.factory.ExpressionStatement(this, null);
        exp.setObj(LangCompiler.factory.TernaryObject(exp, cnd_exp.getObj(), node_then, node_else));
        
        return exp;
    }
    
    private Iterator<Node> parseTernaryEnterExp(Iterator<Node> it) {
        List<Node> children = it.next().getAsChildren();
        while (children.size() == 1) {
            Enum<?> token = children.get(0).getToken();
            if (token == EnumExpression.EXPRESSION || token == EnumExpression.EVAL_CNDTN) {
                children = children.get(0).getAsChildren();
            } else {
                break;
            }
        }
        
        return children.iterator();
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
                throw new CompileException("Illegal left-hand side token `" + child + "`");
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
    
    private ExpressionStatement doHandleExpression(Iterator<Node> it) {
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
    
    private void handleNextExpressionChild(Iterator<Node> it, ExpressionStatement exp, BasicObject temp) {
        Node child = it.next();
        String val = child.getValue();
        Enum<?> token = child.getToken();
        
        BasicObject obj = handleNextExpressionChildObject(child, it, temp);
        if (obj != null) {
            exp.setObj(obj);
        } else if (token == QUESTION) {
            exp.setObj(parseTernary(exp, it));
            
        } else if (token == OPEN_PRNTH) {
            ExpressionStatement nextExp = doHandleExpression(Util.getIterator(it.next()));
            nextExp.setForcedGroup(true);
            
            exp.setObj(nextExp);
            
            // Skip ending parenthesis
            it.next();
            
        } else if (token == NOT) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            exp.setObj(LangCompiler.factory.OPObject(exp, op, nextExp.getObj(), null));
        } else if (token == ADD_SUB || token == MULT_DIV || token == AND || token == MSC_BITWISE || token == SC_BITWISE) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            exp.setObj(LangCompiler.factory.OPObject(exp, op, exp.getObj(), nextExp.getObj()));
        } else if (token == NOTEQUAL || token == EQUALS || token == LESS_EQU || token == GTR_EQU || token == LESS || token == GTR) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = LangCompiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            if (temp == null) temp = createTempVar(Type.BOOL);
            exp.setObj(LangCompiler.factory.ConditionalObject(exp, op, exp.getObj(), nextExp.getObj(), temp));
        } else if (!child.isTerminal()) {
            exp.setObj(doHandleExpression(Util.getIterator(child)));
        } else {
            throw new CompileException("Unexpected token `" + child + "`");
        }
    }
    
    private BasicObject handleNextExpressionChildObject(Node child, Iterator<Node> it, BasicObject temp) {
        Enum<?> token = child.getToken();
        
        if (child.isTerminal()) {
            String val = child.getValue();
            if (token == NUMBER || token == HEX_NUMBER || token == CHAR) {
                return Util.parseInt(child);
            } else if (token == TRUE) {
                return new LiteralBool(true);
            } else if (token == FALSE) {
                return new LiteralBool(false);
            } else if (token == STRING) {
                return LangCompiler.getString(val.substring(1, val.length() - 1));
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
                return getFuncPointer(child);
            } else if (token == EnumExpression.ANON_FUNC) {
                return defineAnonFunc(child);
            } else if (token == EnumExpression.CALL_FUNC) {
                CallFuncStatement fc = callFunc(child);
                return fc.getFuncCallPointer();
            }
        }
        
        return null;
    }

    @Override
    public String toString() {
        return getTargetCodeString();
    }
}
