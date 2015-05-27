package com.hahn.basic.intermediate;

import static com.hahn.basic.definition.EnumToken.ADD;
import static com.hahn.basic.definition.EnumToken.ADD_ADD;
import static com.hahn.basic.definition.EnumToken.AND;
import static com.hahn.basic.definition.EnumToken.BOOL_AND;
import static com.hahn.basic.definition.EnumToken.BOOL_OR;
import static com.hahn.basic.definition.EnumToken.BOR;
import static com.hahn.basic.definition.EnumToken.CHAR;
import static com.hahn.basic.definition.EnumToken.CLOSE_PRNTH;
import static com.hahn.basic.definition.EnumToken.CLOSE_SQR;
import static com.hahn.basic.definition.EnumToken.COLON;
import static com.hahn.basic.definition.EnumToken.COMMA;
import static com.hahn.basic.definition.EnumToken.DIV;
import static com.hahn.basic.definition.EnumToken.DOT;
import static com.hahn.basic.definition.EnumToken.EQUALS;
import static com.hahn.basic.definition.EnumToken.FALSE;
import static com.hahn.basic.definition.EnumToken.GTR;
import static com.hahn.basic.definition.EnumToken.GTR_EQU;
import static com.hahn.basic.definition.EnumToken.HASH;
import static com.hahn.basic.definition.EnumToken.HEX_INT;
import static com.hahn.basic.definition.EnumToken.IDENTIFIER;
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
import static com.hahn.basic.definition.EnumToken.REAL;
import static com.hahn.basic.definition.EnumToken.RSHIFT;
import static com.hahn.basic.definition.EnumToken.STRING;
import static com.hahn.basic.definition.EnumToken.SUB;
import static com.hahn.basic.definition.EnumToken.SUB_SUB;
import static com.hahn.basic.definition.EnumToken.SUPER;
import static com.hahn.basic.definition.EnumToken.THIS;
import static com.hahn.basic.definition.EnumToken.TILDE;
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
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.Array;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.EmptyArray;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.IArray;
import com.hahn.basic.intermediate.objects.LiteralBool;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
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
import com.hahn.basic.intermediate.statements.FuncDefStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.ParamDefaultValStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.CompilerUtils;
import com.hahn.basic.util.LiteralUtils;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.exceptions.DuplicateDefinitionException;
import com.hahn.basic.util.exceptions.UnhandledNodeException;
import com.hahn.basic.util.structures.BitFlag;
import com.hahn.basic.util.structures.NestedListIterator;
import com.hahn.basic.viewer.util.TextColor;

public class Frame extends Statement {    
    private final Frame parent;
    private final Node frameHead;
    
    // For compilation
    protected HashMap<String, AdvancedObject> vars;
    
    // For optimization
    private List<AdvancedObject> inUseVars;
    
    /** For loops and functions (recursion) */
    protected EndLoopStatement endLoop;
    private boolean hasReturn;
    
    public Frame(Frame parent, Node head) {
    	this(parent, head, false);
    }
    
    /**
     * Create a new frame
     * @param parent The parent frame
     * @param head The frame head
     * @param loopable True if frame can be looped (ex: for loop, function).
     */
    public Frame(Frame parent, Node head, boolean loopable) {
        super(null);
        
        this.parent = parent;
        this.frameHead = head;
        
        // For compilation
        this.vars = new HashMap<String, AdvancedObject>();
        
        // For optimization
        this.inUseVars = new ArrayList<AdvancedObject>();
        
        // Special loop handling. Functions are loopable because of recursion 
        if (loopable) { this.endLoop = new EndLoopStatement(this); }
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
        if (endLoop != null && !(this instanceof FuncHead)) {
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
                if (Main.getInstance().isPretty() || eol) {
                    str.append(Compiler.factory.getEOL());
                    
                    // Pretty print new line
                    if (Main.getInstance().isPretty() && eol) str.append('\n');
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
        
        obj = safeGetClassObj(name);
        if (obj != null) return obj;
        
        return null;
    }
    
    /**
     * Get an object representation of a class
     * with the given name.
     * @param name The name of the class
     * @return The class object found or null
     */
    public BasicObject safeGetClassObj(String name) {
        Type t = Type.fromName(name);
        if (t != null && t.doesExtend(Type.OBJECT)) {
            return ((ClassType) t).getClassObj();
        } else {
            return null;
        }
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
        Enum<?> token = nameNode.getToken();
        
        if (token == IDENTIFIER || token == THIS || token == SUPER) {    
            BasicObject obj = safeGetVar(name);
            
            // Found
            if (obj != null) {
                if (obj.getVarThisFlag() != Var.NOT_THIS) {
                    nameNode.setColor(TextColor.LIGHT_BLUE);
                } else if (obj.getVarThisFlag() == Var.NOT_THIS && obj != obj.getAccessedObject()) {
                    if (obj.getAccessedObject().hasFlag(BitFlag.PRIVATE)) {
                        throw new CompileException("The field `" + name + "` is private", nameNode);
                    }
                }
                
                return obj;
            } else {
                throw new CompileException("Variable `" + name + "` is not defined in this scope", nameNode);
            }
        } else {
            return handleNextExpressionChildObject(nameNode, null);
        }
    }
    
    public String getLabel(String name) {
        return Compiler.getLabel(name, this);
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
            } else if (token == EnumExpression.IF_STMT) {
                addCode(ifStatement(child));
            } else if (token == EnumExpression.WHILE_STMT) {
                addCode(whileStatement(child));
            } else if (token == EnumExpression.FOR_STMT) {
                addCode(forStatement(child));
            } else if (token == EnumExpression.DIRECTIVE) {
                handleDirective(child);
            } else if (token == EnumExpression.IMPORT) {
                doImport(child);
            } else if (token == EnumExpression.EXPRESSION) {
                addCode(handleStatementExpression(child));
            } else if (token == EnumToken.COMMENT || token == EnumToken.EOL || token == EnumToken.OPEN_BRACE || token == EnumToken.CLOSE_BRACE) {
                continue;
            } else {
                throw new CompileException("Illegal left-hand side token `" + child + "`", child);
            }
        }
    }

    /**
     * `Import` handler
     * @param head EnumExpression.IMPORT
     */
    public void doImport(Node head) {
        StringBuilder path = new StringBuilder();
        
        Iterator<Node> it = CompilerUtils.getIterator(head);
        while (it.hasNext()) {
            Node n = it.next();
            Enum<?> token = n.getToken();
            
            if (token == EnumToken.IMPORT) {
                continue;
            } else if (token == IDENTIFIER) {
                path.append(n.getValue());
            } else if (token == IDENTIFIER) {
                path.append(".");
            }
        }
        
        // Update location
        Main.getInstance().setLine(head.getRow(), head.getCol());
        
        // Parse the name of the library from the quoted string form
        String strPath = path.toString();
        Compiler.addLibrary(head, strPath);
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
            
            if (this instanceof FuncHead && ((FuncHead) this).getClassIn() != null) {
                return ((FuncHead) this).getClassIn().getImpliedThis();
            } else {
                return null;
            }
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
        ExpressionStatement exp = Compiler.factory.ExpressionStatement(this, obj);
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
                exp.setObj(Compiler.factory.VarAccess(exp, exp.getObj(), offset, arrType.getTypable(0).getType(), child.getRow(), child.getCol()), child);
            } else if (accessMarker == DOT && type.doesExtend(Type.STRUCT)) {               
                Node nameNode = it.next();
                Node prthNode = (it.hasNext() ? it.next() : null);
                Node nextGroupNode = (it.hasNext() ? it.next() : null);
                
                if (leaveLast && 
                        (prthNode == null || (prthNode.getToken() == EnumExpression.PRNTH_PARAMS || nextGroupNode == null))) {
                    // Go back
                    it.previous();
                    if (prthNode != null) it.previous();
                    if (nextGroupNode != null) it.previous();
                    
                    break;
                }
                
                // Unneeded
                if (nextGroupNode != null) it.previous();
                
                // Function
                if (prthNode != null && prthNode.getToken() == EnumExpression.PRNTH_PARAMS) {
                    Node node = prthNode.getAsChildren().get(1);
                    List<BasicObject> params = new ArrayList<BasicObject>();
                    if (node.getToken() == EnumExpression.CALL_PARAMS) {
                        getFuncCallParams(node, params);
                    }
                    
                    // Get FuncCall object
                    BasicObject[] aParams = params.toArray(new BasicObject[params.size()]);
                    exp.setObj(Compiler.factory.FuncCallPointer(nameNode, exp.getObj(), aParams), child);
                    
                // Variable
                } else {
                    if (prthNode != null) it.previous();
                    
                    nameNode.setColor(TextColor.LIGHT_BLUE);
                    
                    StructParam sp = exp.getObj().getType().getAsStruct().getParam(nameNode);               
                    exp.setObj(Compiler.factory.VarAccess(exp, exp.getObj(), sp, sp.getType(), child.getRow(), child.getCol()), child);
                    
                    if (sp.hasFlag(BitFlag.PRIVATE)) {
                        throw new CompileException("The field `" + nameNode.getValue() + "` is private", nameNode);
                    }
                }
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
        boolean isPrefix = (children.size() == 2 && children.get(0).getToken() != EnumExpression.ACCESS);
        
        Node varNode = (isPrefix ? children.get(1) : children.get(0));
        BasicObject var = accessVar(varNode);
        
        Node opNode = (isPrefix ? children.get(0) : children.get(1));
        
        Node objNode = (children.size() > 2 ? children.get(2) : null);
        BasicObject obj = (children.size() > 2 ? handleExpression(objNode).getAsExpObj() : null);
        
        switch (opNode.joinToString()) {
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
            case "++":
                if (isPrefix) return prefixUpdateVar(var, varNode, OPCode.PADD);
                else return postfixUpdateVar(var, varNode, OPCode.PADD);
            case "--":
                if (isPrefix) return prefixUpdateVar(var, varNode, OPCode.PSUB);
                else return postfixUpdateVar(var, varNode, OPCode.PSUB);
           default:
               throw new UnhandledNodeException(opNode);
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
        return Compiler.factory.ArithmeticSetObject(this, op, var, varNode, obj, objNode);
    }
    
    /**
     * Do the pre-unary modification of a variable
     * @param var The variable to modify
     * @param varNode The node by which the var is defined
     * @param op The operation to perform on the variable
     * @return OPObject to update the var
     */
    protected OPObject prefixUpdateVar(BasicObject var, Node varNode, OPCode op) {
        return Compiler.factory.OPObject(this, op, var, varNode, null, null);
    }
    
    /**
     * Do the post-unary modification of a variable
     * @param var The variable to modify
     * @param varNode The node by which the var is defined
     * @param op The operation to perform on the variable
     * @return OPObject to update the var
     */
    protected OPObject postfixUpdateVar(BasicObject var, Node varNode, OPCode op) {
        return Compiler.factory.PostfixOPObject(this, op, var, varNode);
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
        Iterator<Node> it = CompilerUtils.getIterator(head);
        
        // Init var
        int flags = 0;
        Type type = Type.UNDEFINED;
        DefineVarStatement define = Compiler.factory.DefineVarStatement(this, false);
        
        // For empty array creation
        EmptyArray emptyArray = null;
        
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumExpression.FLAG) {
                flags |= BitFlag.valueOf(node);
            } else if (token == EnumExpression.TYPE) {
                type = Type.fromNode(node);
            } else if (token == EnumExpression.CREATE_EARR) {
                emptyArray = createEmptyArray(node);
            } else if (token == EnumToken.IDENTIFIER) {
                String name = node.getValue();
                
                // Create var
                final BasicObject obj;
                if (struct != null) { // Instance var
                    node.setColor(TextColor.LIGHT_BLUE);
                    
                    obj = struct.putParam(new Param(name, type, flags), node);
                } else { // Local var
                    obj = Compiler.factory.VarLocal(this, name, type, flags);
                }
                
                // Modify var
                BasicObject val = null;
                Node mainNode = null, valNode = null;
                
                boolean hasInit = false;
                if (emptyArray != null) {
                    val = emptyArray;
                    valNode = emptyArray.getNode();
                    mainNode = emptyArray.getNode();
                    
                    // Reset
                    emptyArray = null;
                    
                    hasInit = true;
                } else if (it.hasNext()) {
                    Node nextHead = it.next();
                    Enum<?> nextToken = nextHead.getToken();
                    
                    if (nextToken == EnumExpression.DEF_MODIFY) {                        
                        List<Node> modify_children = nextHead.getAsChildren();
                        
                        mainNode = nextHead;                        
                        valNode = modify_children.get(1); // Value to assign                         
                        val = handleExpression(valNode).getAsExpObj();
                        
                        hasInit = true;
                    }
                } else if (canInit) { // Default value
                    mainNode = node;
                    valNode = node;
                    
                    val = LiteralNum.UNDEFINED;
                    if (type.doesExtend(Type.OBJECT)) val = LiteralNum.NULL;
                    else if (type.doesExtend(Type.STRUCT)) val = Compiler.factory.DefaultStruct(type.getAsStruct());
                    
                    hasInit = true;
                }
                
                // Do the init
                if (hasInit) {
                    if (!canInit) {
                        throw new CompileException("Can not initialize `" + obj.getName() + "` here", mainNode);
                    } else if (val == null || valNode == null || mainNode == null) {
                        throw new IllegalArgumentException();
                    }
                    
                    if (struct instanceof ClassType) {
                        defineClassVar((ClassType) struct, obj, node, val, valNode);                    
                    }
                    
                    define.addVar(obj, val, mainNode);  
                }
                
                // Make var available
                if (struct == null) {
                    Main.getInstance().setLine(node.getRow(), node.getCol());
                    addVar((AdvancedObject) obj);
                }
            }
        }
        
        define.setFlags(flags);
        
        return define;
    }
    
    /**
     * Parse an empty array definition
     * @param head EnumExpression.CREATE_EARR
     * @return An empty array object
     */
    private EmptyArray createEmptyArray(Node head) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        it.next(); // Skip 'new'
        
        Node typeNode = it.next();
        Type type = Type.fromNode(typeNode);
        
        int dimensions = ParameterizedType.countParameterizedTypes(type);
        if (dimensions > 0) {
            throw new CompileException("Illegal array definition, must define size of first dimension", typeNode);
        }
        
        List<BasicObject> sizes = new ArrayList<BasicObject>();
        
        Node open_brace = null;
        while (it.hasNext()) {
            Node node = it.next();
            if (node.getToken() == EnumToken.OPEN_SQR) {
                dimensions += 1;
                open_brace = node;
            } else if (node.getToken() == EnumToken.CLOSE_SQR) {
                if (dimensions == 1 && sizes.size() == 0) {
                    throw new CompileException("Illegal array definition, must define size of first dimension", open_brace);
                }
            } else if (node.getToken() == EnumExpression.EXPRESSION) {                
                sizes.add(handleExpression(node).getAsExpObj());
            } else {
                throw new UnhandledNodeException(node);
            }
        }
        
        if (sizes.size() == 0) {
            throw new CompileException("Illegal array definition, must define size of first dimension", typeNode);
        }
        
        return Compiler.factory.EmptyArray(head, IArray.toArrayType(type, dimensions), sizes);
    }
    
    /**
     * Parse an array definition
     * @param head EnumExpression.CREATE_ARR
     * @return An Array definition
     */
    private Array createArray(Node head) {
        List<BasicObject> values = new ArrayList<BasicObject>();
        
        Iterator<Node> it = CompilerUtils.getIterator(head);
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == OPEN_SQR || token == CLOSE_SQR || token == COMMA) {
                continue;
            } else if (token == EnumExpression.EXPRESSION) {
                values.add(handleExpression(node).getAsExpObj());
            } else {
                throw new RuntimeException("Unhandled node in create array: " + node);
            }
        }
        
        return Compiler.factory.Array(values);
    }
    
    private void defineClassVar(ClassType classIn, BasicObject var, Node varNode, BasicObject val, Node valNode) {
        VarAccess access = Compiler.factory.VarAccess(this, classIn.getThis(), var, var.getType(), varNode.getRow(), varNode.getCol());
        OPObject op = Compiler.factory.OPObject(this, OPCode.SET, access, varNode, val, valNode);
        classIn.addInitStatement(Compiler.factory.ExpressionStatement(this, op));
    }
    
    /**
     * `Return` statement handler
     * @param head EnumExpression.RETURN or null
     * @return The statement to do the return
     */
    public Compilable doReturn(Node head) {
        Frame f = this;
        while (f.parent != Compiler.getGlobalFrame()) {
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
        } else {
            result = LiteralNum.VOID;
        }
        
        Type newType = result.getType().autocast(func.getReturnType(), head.getRow(), head.getCol(), false);
        if (newType == null) {
            throw new CompileException("Invalid return type. Expected `" + func.getReturnType() + "` but got `" + result.getType() + "`", head);
        }
        
        // Return
        return Compiler.factory.ReturnStatement(this, (FuncHead) f, result);
    }
    
    /**
     * `Struct` definition handler
     * @param head EnumExpression.STRUCT
     */
    public void defineStruct(Node head) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        it.next(); // skip 'struct'
        
        Node nameNode = it.next();
        Main.getInstance().setLine(nameNode.getRow(), nameNode.getCol());
        StructType struct = Type.STRUCT.extendAs(this, nameNode.getValue(), 0);
        
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
        Iterator<Node> it = CompilerUtils.getIterator(head);
        it.next(); // skip 'class'
        
        Node nameNode = it.next();
        Node parentNode = null;
        int flags = 0;
        
        // Keep track of location
        Main.getInstance().setLine(nameNode.getRow(), nameNode.getCol());
        
        // Don't add class code if library
        if (Main.getInstance().isLibrary()) {
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
                    throw new UnhandledNodeException(ckey);
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
            
            ClassType classType = ((ClassType) parentType).extendAs(this, nameNode.getValue(), flags);
            
            // Handle class content
            handleClassContent(nameNode, classType, it);
        } else {
            if (parentNode == null) parentNode = head;
            throw new CompileException("Cannot extend the non-class type `" + parentType + "`", parentNode);
        }
    }
    
    private void handleClassContent(Node nameNode, ClassType classIn, Iterator<Node> it) {        
        while (it.hasNext()) {
            Node child = it.next();
            Enum<?> token = child.getToken();
            
            if (token == EnumExpression.CLASS_CNTNT) {
                handleClassContent(nameNode, classIn, CompilerUtils.getIterator(child));
            } else if (token == EnumExpression.DEF_FUNC) {
                defineClassFunc(child, classIn);
            } else if (token == EnumExpression.CONSTRUCTOR) {
                defineConstructor(child, classIn);
            } else if (token == EnumExpression.DEFINE) {
                defineVar(child, classIn, true);
            } else if (token == EnumToken.COMMENT || token == EnumToken.EOL) {
                continue;
            } else if (token == EnumToken.CLOSE_BRACE) {
                break;
            } else {
                throw new UnhandledNodeException(child);
            }
        }
    }
    
    /**
     * Defines a function in the global frame
     * @param head EnumExpression.DEF_FUNC
     * @return Statement to help do optimization of function at the correct time
     */
    public FuncDefStatement defineFunc(Node head) {
        return doDefineFunc(head, null, false);
    }
    
    /**
     * Define an anonymous functions
     * @param head EnumExpression.ANON_FUNC
     * @return Pointer to the anonymous function
     */
    public FuncPointer defineAnonFunc(Node head) {
        return doDefineFunc(head, null, true).getFuncPointer();
    }
    
    /**
     * Define a function in a class
     * @param head EnumExpression.DEF_FUNC
     * @param classIn The class to define the function in
     */
    public FuncPointer defineClassFunc(Node head, ClassType classIn) {
        return doDefineFunc(head, classIn, false).getFuncPointer();
    }
    
    public FuncPointer defineConstructor(Node head, ClassType classIn) {
        if (classIn == null) throw new CompileException("Illegal definition of a constructor outside of a class", head);
        
        return defineClassFunc(head, classIn);
    }
    
    public FuncDefStatement doDefineFunc(Node head, ClassType classIn, boolean anonymous) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        
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
                Iterator<Node> pIt = CompilerUtils.getIterator(child);
                
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
                if (!Main.getInstance().isLibrary()) body = child;
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
            Main.getInstance().setLine(nameNode.getRow(), nameNode.getCol());
            
            if (classIn == null) {
                func = Compiler.defineFunc(Compiler.getGlobalFrame(), body, name, null, rtnType, aParams);
            } else {
                func = classIn.defineFunc(body, name, null, rtnType, aParams);
            }
        } else {
            // Anonymous
            String name = getLabel("afunc");
            nameNode.setValue(name);
            
            func = Compiler.defineFunc(this, body, name, null, rtnType, aParams);
        }
        
        // Put flags
        func.setFlags(flags);
        
        // Put default value
        if (!inits.isEmpty()) {
            ParamDefaultValStatement defaultVal = Compiler.factory.ParamDefaultValStatement(func, false);
            for (DefinePair pair: inits) defaultVal.addVar(pair);
            
            func.addCode(defaultVal);
        }
        
        // Return
        return Compiler.factory.FuncDefStatement(this, nameNode, func);
    }
    
    /**
     * Call a function
     * @param head EnumExpression.CALL_FUNC
     * @return The statement that should be added to the frame
     * and can extract the FuncCallPointer from
     */    
    public CallFuncStatement callFunc(Node head) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        
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
        FuncCallPointer funcCallPointer = Compiler.factory.FuncCallPointer(nameNode, objectIn, aParams);
        return Compiler.factory.DefaultCallFuncStatement(this, funcCallPointer);
    }
    
    /**
     * Get the parameters of a function call
     * @param head EnumExpression.CALL_PARAMS
     * @param params The list to add the params to
     */
    private void getFuncCallParams(Node head, List<BasicObject> params) {
        if (head.getToken() != EnumExpression.CALL_PARAMS) return;
        
        Iterator<Node> it = CompilerUtils.getIterator(head);
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
        Iterator<Node> it = CompilerUtils.getIterator(head);
        
        Node nameNode = null;
        ParameterizedType<ITypeable> types = null;
        
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (token == EnumToken.IDENTIFIER) {
                nameNode = node;
            } else if (token == EnumExpression.TYPE_LIST) {
                types = ParameterizedType.getParameterizedType(Type.FUNCTION, node, false);
            }
        }
        
        // If no types provided use default
        if (types == null) {
            types = new ParameterizedType<ITypeable>(Type.FUNCTION);
        }
        
        return Compiler.factory.FuncPointer(nameNode, objectIn, types);
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
        
        // Standard definition
        if (children.size() > 3) {
            List<BasicObject> params = new ArrayList<BasicObject>();
            getFuncCallParams(children.get(3), params);
            
            return Compiler.factory.NewInstance(type, typeNode, params);
        } else if (type.doesExtend(Type.ARRAY)) {
            throw new CompileException("Illegal array definition, must define size of first dimension", typeNode);
        } else {
            throw new CompileException("Illegal definition of type `" + type + "`. Missing parameters", typeNode);
        }
    }
    
    /**
     * `If` statement handler
     * @param head EnumExpression.IF_STMT
     * @return The IfStatement
     */
    public Compilable ifStatement(Node head) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        
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
        
        return Compiler.factory.IfStatement(this, conditionals);
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
        
        return Compiler.factory.WhileStatement(this, blockChildren.get(1), blockChildren.get(3));
    }
    
    /**
     * `For` statement handler
     * @param head EnumExpression.FOR_STMT
     * @return The ForStatement
     */
    public Compilable forStatement(Node head) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        
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
                    throw new UnhandledNodeException(child);
                }
            }
        }
        
        return Compiler.factory.ForStatement(this, define, condition, modification, body);
    }
    
    /**
     * `Continue` handler
     * @param head EnumToken.CONTINUE
     * @return ContinueStatement
     */
    public Compilable doContinue(Node head) {        
        return Compiler.factory.ContinueStatement(this);
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
        
        return Compiler.factory.BreakStatement(this);
    }
    
    /**
     * Cast the next expression object
     * @param head EnumExpression.CAST
     * @param temp
     * @return ObjectHolder
     */
    private BasicObject doCast(Node head, BasicObject temp) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        
        Node typeNode = null;
        while (it.hasNext()) {
            Node node = it.next();
            Enum<?> token = node.getToken();
            
            if (Type.isTypeNode(node)) {
                typeNode = node;
            } else if (token == COLON || token == OPEN_PRNTH || token == CLOSE_PRNTH) {
                continue;
            } else if (token == EnumExpression.EXPRESSION) {                
                ExpressionStatement nextExp = handleExpression(it.next());                
                return nextExp.getAsExpObj().castTo(Type.fromNode(node), typeNode.getRow(), typeNode.getCol());
            } else {
                throw new UnhandledNodeException(node);
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
            Main.getInstance().setIsLibrary(true);
            break;
        case "end_library":
            Main.getInstance().setIsLibrary(false);
            break;
        case "eof":
            Main.getInstance().setIsLibrary(false);
            break;
        default:
            throw new CompileException("Unknown preprocessor directive `" + directive + "`", head);
        }
    }
    
    /**
     * Parse a regex expression
     * @param head EnumExpression.REGEX
     * @return
     */
    private BasicObject parseRegex(Node head) {
        Iterator<Node> it = CompilerUtils.getIterator(head);
        it.next(); // Skip open '/'
        
        Node n = it.next();
        String regex = "";
        while (n.getToken() != EnumToken.DIV) {
            regex += n.getValue();
            
            if (it.hasNext()) {
                n = it.next();
            } else {
                break;
            }
        }
        
        return Compiler.getRegex(regex);
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
            
        // Only other valid form is a ternary operation and prefix-inc/dec
        } else {
            ExpressionStatement exp = handleExpression(child);
            if (exp.getObj().isTernary()) {
                return exp;
            } else if (exp.getObj().isPrefixIncDec()) {
                return exp;
            } else {
                throw new CompileException("Illegal left-hand side token `" + child.joinToString() + "`", child);
            }
        }
    }
    
    /**
     * Handle an expression
     * @param head EnumExpression.EXPRESSION or one of it's children
     * @return An object with the result of the expression
     */    
    public ExpressionStatement handleExpression(Node head) {
        return doHandleExpression(CompilerUtils.getIterator(head));
    }
    
    public ExpressionStatement doHandleExpression(Iterator<Node> it) {
        ExpressionStatement exp = Compiler.factory.ExpressionStatement(this, null);
        
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
            
            exp.setObj(Compiler.factory.TernaryObject(exp, exp.getObj(), node_then, node_else, child.getRow(), child.getCol()), child);
            
        } else if (token == OPEN_PRNTH) {
            Node inPrnthNode = it.next();
            ExpressionStatement nextExp = doHandleExpression(CompilerUtils.getIterator(inPrnthNode));
            nextExp.setForcedGroup(true);
            
            exp.setObj(nextExp, inPrnthNode);
            
            // Skip ending parenthesis
            it.next();
            
        } else if (token == NOT || token == HASH || token == TILDE || token == SUB_SUB || token == ADD_ADD
                        /* Prefix */ || (exp.getObj() == null && token == SUB)) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = Compiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            exp.setObj(Compiler.factory.OPObject(exp, op, nextExp.getObj(), nextExp.getNode(), null, null), child);
        } else if (token == ADD || token == SUB || token == MULT || token == DIV || token == MOD || token == AND || token == BOR || token == XOR || token == LSHIFT || token == RSHIFT || token == BOOL_AND || token == BOOL_OR) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = Compiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            exp.setObj(Compiler.factory.ArithmeticObject(exp, op, exp.getObj(), exp.getNode(), nextExp.getObj(), nextExp.getNode()), child);
        } else if (token == NOTEQUAL || token == EQUALS || token == LESS_EQU || token == GTR_EQU || token == LESS || token == GTR) {
            OPCode op = OPCode.fromSymbol(val);
            
            ExpressionStatement nextExp = Compiler.factory.ExpressionStatement(this, null);
            handleNextExpressionChild(it, nextExp, temp);
            
            if (temp == null) temp = createTempVar(Type.BOOL);
            exp.setObj(Compiler.factory.ConditionalObject(exp, op, exp.getObj(), exp.getNode(), nextExp.getObj(), nextExp.getNode(), temp), child);
            
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
            if (token == HEX_INT || token == CHAR) {
                return LiteralUtils.parseInt(child);
            } else if (token == REAL){
                return LiteralUtils.parseFloat(child);
            } else if (token == TRUE) {
                return new LiteralBool(true);
            } else if (token == FALSE) {
                return new LiteralBool(false);
            } else if (token == STRING) {
                return Compiler.getString(val.substring(1, val.length() - 1));
            } else if (token == NULL) {
                return LiteralNum.NULL;
            }
        } else {
            if (token == EnumExpression.ACCESS) {
                return accessVar(child);
            } else if (token == EnumExpression.REGEX) {
                return parseRegex(child);
            } else if (token == EnumExpression.MODIFY) {
                return modifyVar(child);
            } else if (token == EnumExpression.CREATE) {
                return createInstance(child);
            } else if (token == EnumExpression.CREATE_EARR) {
                return createEmptyArray(child);
            } else if (token == EnumExpression.CREATE_ARR) {
                return createArray(child);
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
