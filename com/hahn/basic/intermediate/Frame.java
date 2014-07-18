package com.hahn.basic.intermediate;

import static com.hahn.basic.definition.EnumToken.ADD_SUB;
import static com.hahn.basic.definition.EnumToken.AND;
import static com.hahn.basic.definition.EnumToken.ARROW;
import static com.hahn.basic.definition.EnumToken.CHAR;
import static com.hahn.basic.definition.EnumToken.DOT;
import static com.hahn.basic.definition.EnumToken.EQUALS;
import static com.hahn.basic.definition.EnumToken.FALSE;
import static com.hahn.basic.definition.EnumToken.GTR;
import static com.hahn.basic.definition.EnumToken.GTR_EQU;
import static com.hahn.basic.definition.EnumToken.HEX_NUMBER;
import static com.hahn.basic.definition.EnumToken.LESS;
import static com.hahn.basic.definition.EnumToken.LESS_EQU;
import static com.hahn.basic.definition.EnumToken.MSC_BITWISE;
import static com.hahn.basic.definition.EnumToken.MULT_DIV;
import static com.hahn.basic.definition.EnumToken.NOTEQUAL;
import static com.hahn.basic.definition.EnumToken.NUMBER;
import static com.hahn.basic.definition.EnumToken.OPEN_SQR;
import static com.hahn.basic.definition.EnumToken.STRING;
import static com.hahn.basic.definition.EnumToken.TRUE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.library.Common;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.LiteralBool;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.VarGlobal;
import com.hahn.basic.intermediate.objects.VarTemp;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Struct;
import com.hahn.basic.intermediate.objects.types.Struct.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.register.Register;
import com.hahn.basic.intermediate.statements.Command;
import com.hahn.basic.intermediate.statements.ContinueStatement;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.EndLoopStatement;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.IfStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.ReturnStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.WhileStatement;
import com.hahn.basic.intermediate.statements.function.DefaultCallFuncStatement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.CastException;
import com.hahn.basic.util.CompileException;
import com.hahn.basic.util.DepthIterator;
import com.hahn.basic.util.DuplicateDefinitionException;
import com.hahn.basic.util.Util;

public class Frame extends Statement {    
    @SuppressWarnings("unchecked")
    public static Map<Enum<?>, String> EXPRESSION_HANDLERS = (Map<Enum<?>, String>) Util.arr2map(
                Enum.class, String.class,
                new Object[][] {
                    { EnumExpression.BLOCK,      "handleBlock"      },
                    { EnumExpression.BLOCK_CNTNT,"handleBlock"      },
                    { EnumExpression.DEFINE,     "defineVar"        },
                    { EnumExpression.DEFINE_G,   "defineGlobalVar"  },
                    { EnumExpression.MODIFY,     "modifyVar"        },
                    { EnumExpression.STRUCT,     "defineStruct"     },
                    { EnumExpression.DEF_FUNC,   "defineFunc"       },
                    { EnumExpression.CALL_FUNC,  "callFunc"         },
                    { EnumExpression.CREATE,     "createInstance"   },
                    { EnumExpression.DELETE,     "deleteInstance"   },
                    { EnumExpression.RETURN,     "doReturn"         },
                    { EnumToken     .CONTINUE,   "doContinue"       },
                    { EnumToken     .BREAK,      "doBreak"          },
                    { EnumToken     .IMPORT,     "doImport"         },
                    { EnumExpression.IF_STMT,    "ifStatement"      },
                    { EnumExpression.WHILE_STMT, "whileStatement"   },
                    { EnumExpression.FOR_STMT,   "forStatement"     },
                }
            );
    
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
    
    public Frame getLoop() {
        if (endLoop != null) {
            return this;
        } else if (parent != null) {
            return parent.getLoop();
        } else {
            return null;
        }
    }
    
    /*
     * =====================================================
     * START OPTIMIZE CODE
     * =====================================================
     */
    
    @Override
    public void addTargetCode() {
        if (frameHead != null) {
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
    public boolean forwardOptimize() {
        super.forwardOptimize();
        
        // Release the frame's variables
        inUseVars.clear();
        for (AdvancedObject o: vars.values()) {
            o.releaseRegister();
        }
        
        return false;
    }
    
    /**
     * All variables used within this frame, including subframes,
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
    
    private AdvancedObject safeGetVar(String name) {
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
        return Compiler.getLabel(name, this);
    }
    
    /**
     * `Block` handler
     * @param head EnumExpression.BLOCK
     */
    public void handleBlock(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        while (it.hasNext()) {
            Node child = it.next();
            Enum<?> token = child.getToken();
            
            if (token != EnumToken.EOL && token != EnumToken.OPEN_BRACE && token != EnumToken.CLOSE_BRACE) {                
                String handlerName = EXPRESSION_HANDLERS.get(token);
                if (handlerName == null) {
                    throw new RuntimeException("No handler defined for token `" + token + "`");
                }
                
                Method handler;
                try {
                    handler = Frame.class.getMethod(handlerName, Node.class);
                    handler.invoke(this, child);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof RuntimeException) {
                        throw (RuntimeException) e.getTargetException();
                    } else {
                        throw new CompileException(e.getTargetException().getMessage());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }
    
    /**
     * `Import` handler
     * @param head EnumToken.IMPORT
     */
    public void doImport(Node head) {
        String name = head.getValue().replace("import ", "").trim();
        Compiler.addLibrary(name);
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
        Var temp = createTempVar(Type.UINT);
        
        // Create temp var
        addCode(new DefineVarStatement(this, temp, obj.getAddress(), true));
        
        Iterator<Node> it = Util.getIterator(head);
        while (it.hasNext()) {  
            Enum<?> accessMarker = it.next().getToken();
            if (accessMarker == OPEN_SQR && t.doesExtend(Type.ARRAY)) {
                // Is past .length
                boolean pastLng = false;
                
                BasicObject offset = handleExpression(it.next());
                if (offset != LiteralNum.ZERO) { 
                    // If accessing address literal, add 1 here
                    if (offset.hasLiteral()) {
                        pastLng = true;
                        addCode(new Command(this, OPCode.ADD, temp, new LiteralNum(offset.getLiteral().getValue() + 1))); 
                    } else {
                        addCode(new Command(this, OPCode.ADD, temp, offset));
                    }
                }
                
                // Go past .length
                if (!pastLng) addCode(new Command(this, OPCode.ADD, temp, LiteralNum.ONE));
                
                // Skip CLOSE_SQR
                it.next();
            } else if (accessMarker == DOT && t.doesExtend(Type.STRUCT)) {               
                String name = it.next().getValue();
                
                Struct struct = t.castToStruct();
                StructParam sp = struct.getStructParam(name);
                if (sp.getOffset() > 0) { 
                    addCode(new Command(this, OPCode.ADD, temp, new LiteralNum(sp.getOffset()))); 
                }
                
                // Load type of object
                temp.setType(sp.getType());
            } else {
                throw new CompileException("Invalid access of `" + obj.getName() + "` of type `" + t + "`");
            }
            
            // Continue or return?
            if (it.hasNext()) {
                addCode(new Command(this, OPCode.SET, temp, temp.getAddress()));
            } else {
                return temp.getPointer();
            }
        }
        
        throw new RuntimeException("Failed to load pointer var");
    }
    
    /**
     * `Modify` var handler
     * @param head EnumExpression.MODIFY
     */
    public void modifyVar(Node head) {
        List<Node> children = head.getAsChildren();
        
        BasicObject v = accessVar(children.get(0));
        BasicObject o = handleExpression(children.get(2));        
        switch (children.get(1).getValue()) {
            case "=": 
                updateVar(v, o, OPCode.SET);
                break;
            case "+=":
                updateVar(v, o, OPCode.ADD);
                break;
            case "-=":
                updateVar(v, o, OPCode.SUB);
                break;
            case "*=":
                updateVar(v, o, OPCode.MUL);
                break;
            case "/=":
                updateVar(v, o, OPCode.DIV);
                break;
            case "%=":
                updateVar(v, o, OPCode.MOD);
                break;
            case "&=":
                updateVar(v, o, OPCode.AND);
                break;
            case "|=":
                updateVar(v, o, OPCode.BOR);
                break;
            case "^=":
                updateVar(v, o, OPCode.XOR);
                break;
           default:
               throw new RuntimeException("Unhandled modify var '" + children.get(1).getValue() + "'");
        }
    }
    
    /**
     * Do the modification of a variable
     * @param var The variable to modify
     * @param obj The object doing the modification
     * @param op The operation to perform on the variable
     */
    protected void updateVar(BasicObject var, BasicObject obj, OPCode op) {
        Type.merge(var.getType(), obj.getType());
        addCode(new Command(this, op, var, obj));
    }
    
    /**
     * `Define global` var handler
     * @param head EnumExpression.DEFINE_G
     */
    public void defineGlobalVar(Node head) {
        List<Node> children = head.getAsChildren();
        defineVar(children.get(1), true);
    }
    
    /**
     * `Define` var handler
     * @param head EnumExpression.DEFINE
     */
    public void defineVar(Node head) {
        defineVar(head, null, true, false);
    }
    
    protected void defineVar(Node head, boolean isGlobal) {
        defineVar(head, null, true, isGlobal);
    }
    
    protected void defineVar(Node head, List<BasicObject> varsList) {
        defineVar(head, varsList, false, false);
    }
    
    private void defineVar(Node head, List<BasicObject> varsList, boolean canInit, boolean isGlobal) {
        Iterator<Node> it = Util.getIterator(head);
        
        // Init var
        Type type = Type.fromNode(it.next());
        while (it.hasNext()) {
            Node node = it.next();
         
            if (node.getToken() != EnumToken.COMMA) {
                String name = node.getValue();
                final BasicObject obj;
                
                // Create var
                if (isGlobal) {
                    obj = Compiler.factory.VarGlobal(name, type);                    
                } else if (varsList != null) {
                    obj = new Param(name, type);                    
                } else {
                    obj = new Var(this, name, type);
                }
                
                // Modify var
                boolean hasInit = false;
                if (it.hasNext()) {
                    Node nextHead = it.next();
                    Enum<?> nextToken = nextHead.getToken();
                    
                    if (nextToken == EnumExpression.DEF_MODIFY) {
                        if (!canInit || !(obj instanceof AdvancedObject)) {
                            throw new CompileException("Can not initialize `" + obj.getName() + "` here");
                        }
                        
                        List<Node> modify_children = nextHead.getAsChildren();
                        
                        BasicObject o = handleExpression(modify_children.get(1));
                        addCode(new DefineVarStatement(this, (AdvancedObject) obj, o));
                        
                        hasInit = true;
                    }
                }
                
                // Default value
                if (!hasInit && canInit && obj instanceof AdvancedObject) {
                    addCode(new DefineVarStatement(this, (AdvancedObject) obj, LiteralNum.UNDEFINED));
                }
                
                // Make var available
                if (isGlobal) {
                    Compiler.addGlobalVar((VarGlobal) obj);
                } else if (varsList != null) {
                    varsList.add(obj);                    
                } else {
                    addVar((AdvancedObject) obj);
                }
            }
        }
    }
    
    /**
     * `Return` statement handler
     * @param head EnumExpression.RETURN or null
     */
    public void doReturn(Node head) {
        Frame f = this;
        while (f.parent != null) {
            f = f.parent;
        }
        
        if (!(f instanceof FuncHead)) {
            throw new CompileException("Can only return from a function");
        }
        
        FuncHead func = (FuncHead) f;
        
        // Set result
        if (head != null) {
            List<Node> children = head.getAsChildren();
            if (children.size() > 1) {
                BasicObject result = this.handleExpression(children.get(1));
                
                try {
                    result.castTo(func.getReturnType());
                } catch (CastException e) {
                    throw new CastException("Invalid return type - ", e);
                }
                
                addCode(new Command(this, OPCode.SET, Register.EX, result));
            }
        }
        
        // Return
        addCode(new ReturnStatement(this, (FuncHead) f));
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
        
        String name = it.next().getValue();
        
        List<Param> params = new ArrayList<Param>();
        while (it.hasNext()) {
            Node parent = it.next();
            Enum<?> token = parent.getToken();
            
            if (token == ARROW) {
                break;
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
            }
        }
        
        // Convert params list to array
        Param[] aParams = params.toArray(new Param[params.size()]);
        
        // Get return type
        Node next = it.next();
        Type rtnType = Type.VOID;
        if (next.getToken() != EnumExpression.BLOCK) {
            rtnType = Type.fromNode(next);
            next = it.next();
        }
        
        // Define function
        if (!anonymous) {
            Compiler.defineFunc(next, name, rtnType, aParams);
            return null;
        } else {
            name = getLabel("@anon_func");
            
            Compiler.defineFunc(next, name, rtnType, aParams);
            return Compiler.factory.FuncPointer(name, new ParameterizedType<ITypeable>(Type.FUNC, (ITypeable[]) aParams));
        }
    }
    
    /**
     * Call a function
     * @param head EnumExpression.CALL_FUNC
     */    
    public FuncCallPointer callFunc(Node head) {
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
        FuncCallPointer funcCallPointer = Compiler.factory.FuncCallPointer(name, aParams);        
        addCode(new DefaultCallFuncStatement(this, funcCallPointer));        
        return funcCallPointer;
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
                BasicObject v = handleExpression(pNode);
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
        
        return Compiler.factory.FuncPointer(name, types);
    }
    
    /**
     * Create a new instance based on node data
     * @param head EnumExpression.CREATE
     * @return FuncCallPointer alloc
     */
    public FuncCallPointer createInstance(Node head) {
        Compiler.addLibrary("com.hahn.common");
        
        List<Node> children = head.getAsChildren();
        Type type = Type.fromNode(children.get(1));
        String name = type.toString();
        
        if (!type.doesExtend(Type.STRUCT)) {
            throw new CompileException("Can not create a new instance of `" + name + "`");
        } else {
            FuncHead alloc = Common.ALLOC;
            
            List<BasicObject> callParams = new ArrayList<BasicObject>();
            getFuncCallParams(children.get(3), callParams);
            
            // TODO constructor rather than hard-coded
            BasicObject[] allocParams = new BasicObject[1];
            if (type.doesExtend(Type.ARRAY)) {
                if (callParams.size() != 1) {
                    throw new CompileException("No constructor for `" + name + "` with the given parameters is defined");
                }
                
                alloc = Common.ARR_ALLOC;
                allocParams[0] = callParams.get(0);
            } else {
                if (callParams.size() > 0) {
                    throw new CompileException("No constructor for `" + name + "` with the given parameters is defined");
                }
                
                allocParams[0] = new LiteralNum(type.sizeOf());
            }
            
            // Get FuncCall object, must manually set function to 'alloc' because it is hidden
            FuncCallPointer funcCallPointer = Compiler.factory.FuncCallPointer(alloc.getName(), allocParams);
            funcCallPointer.setFunction(alloc);
            funcCallPointer.castTo(type);
            
            addCode(new DefaultCallFuncStatement(this, funcCallPointer));
            
            return funcCallPointer;
        }
    }
    
    /**
     * Delete an instance of an object
     * @param head EnumExpression.DELETE
     * @return FuncCallPointer dealloc
     */
    public FuncCallPointer deleteInstance(Node head) {
        Compiler.addLibrary("com.hahn.common");
        
        List<Node> children = head.getAsChildren();
        BasicObject var = accessVar(children.get(1));
        
        if (var instanceof AdvancedObject && var.getType().doesExtend(Type.STRUCT)) {
            FuncCallPointer funcCallPointer = Compiler.factory.FuncDeallocCallPointer(var);            
            addCode(new DefaultCallFuncStatement(this, funcCallPointer));
            
            return funcCallPointer;
        } else {
            throw new CompileException("Can not delete non-instance object `" + var.getName() + "`");
        }
    }
    
    /**
     * `If` statement handler
     * @param head EnumExpression.IF_STMT
     */
    public void ifStatement(Node head) {
        Iterator<Node> it = Util.getIterator(head);
        
        List<Conditional> elses = new ArrayList<Conditional>();
        while (it.hasNext()) {
            Node child = it.next();
            Enum<?> token = child.getToken();
            
            if (token == EnumExpression.CONDITIONAL) {
                elses.add(createConditional(child));
            } else if (token == EnumExpression.BLOCK) {
                elses.add(new Conditional(child));
            }
        }
        
        addCode(new IfStatement(this, elses));
    }
    
    /**
     * `While` statement handler
     * @param head EnumExpression.WHILE_STMT
     */
    public void whileStatement(Node head) {
        List<Node> children = head.getAsChildren();
        addCode(new WhileStatement(this, createConditional(children.get(1))));
    }
    
    /**
     * @param head EnumExpression.CONDITIONAL
     * @return Conditional holder
     */
    private Conditional createConditional(Node head) {
        List<Node> cChildren = head.getAsChildren();
        return new Conditional(cChildren.get(1), cChildren.get(3));
    }
    
    /**
     * `For` statement handler
     * @param head EnumExpression.FOR_STMT
     */
    public void forStatement(Node head) {
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
        
        addCode(new ForStatement(this, define, condition, modification, body));
    }
    
    /**
     * `Continue` handler
     * @param head EnumToken.CONTINUE
     */
    public void doContinue(Node head) {        
        addCode(new ContinueStatement(this));
    }
    
    /**
     * `Break` handler
     * @param head EnumToken.BREAK
     */
    public void doBreak(Node head) {
        Frame loop = getLoop();
        if (loop == null) {
            throw new CompileException("Invalid use of `break`");
        }
        
        addCode(Compiler.factory.BreakStatement(this));
    }
    
    /**
     * Cast the next experession object
     * @param head EnumExpression.CAST
     * @param it Parent expression parsing iterator
     * @param prev
     * @param temp
     * @return ObjectHolder
     */
    private BasicObject doCast(Node head, DepthIterator<Node> it, BasicObject prev, BasicObject temp) {
        Iterator<Node> childIt = Util.getIterator(head);
        while (childIt.hasNext()) {
            Node child = childIt.next();
            
            if (Type.isValidNode(child)) {
                Type type = Type.fromNode(child);
                
                if (it.hasNext()) {
                    return handleNextExpressionChild(it, prev, temp).castTo(type);
                } else {
                    throw new CompileException("Nothing provided to cast to `" + type + "`");
                }
            }
        }
        
        throw new RuntimeException("Invalid cast definition '" + head + "'");
    }
    
    /**
     * Handle an expression
     * @param head EnumExpression.EXPRESSION or one of it's children
     * @return An object with the result of the expression
     */    
    public BasicObject handleExpression(Node head) {
        return doHandleExpression(new DepthIterator<Node>(Util.getIterator(head)));
    }
    
    private BasicObject doHandleExpression(DepthIterator<Node> it) {
        BasicObject prev = null;
        
        // Add tokens
        while (it.hasNext()) {            
            prev = handleNextExpressionChild(it, prev, null);
        }
 
        if (prev == null) {
            throw new CompileException("Incomplete command"); 
        } else {
            return prev;
        }
    }
    
    private BasicObject handleNextExpressionChild(DepthIterator<Node> it, BasicObject prev, BasicObject temp) {
        Node child = it.next();
        Enum<?> token = child.getToken();

        if (child.isTerminal()) {
            String val = child.getValue();
            if (token == NUMBER || token == HEX_NUMBER || token == CHAR) {
                prev = Util.parseInt(child);
            } else if (token == TRUE) {
                prev = new LiteralBool(true);
            } else if (token == FALSE) {
                prev = new LiteralBool(false);
            } else if (token == STRING) {
                prev = Compiler.getString(val.substring(1, val.length() - 1));
            } else if (token == ADD_SUB || token == MULT_DIV || token == AND || token == MSC_BITWISE) {
                OPCode op = OPCode.fromSymbol(val);
                BasicObject next = handleNextExpressionChild(it, prev, temp);
                
                // Ensure modifiable
                prev = temp = getTemp(prev, temp);
                
                addCode(new Command(this, op, prev, next));
            } else if (token == NOTEQUAL || token == EQUALS || token == LESS_EQU || token == GTR_EQU || token == LESS || token == GTR) {
                OPCode op = OPCode.fromSymbol(val);
                BasicObject next = handleNextExpressionChild(it, prev, temp);
                
                if (temp == null) temp = createTempVar(Type.BOOL);
                prev = Compiler.factory.ConditionalObject(temp, op, prev, next);
            }
        } else {
            if (token == EnumExpression.ACCESS) {
                prev = accessVar(child);
            } else if (token == EnumExpression.CREATE) {
                prev = createInstance(child);
            } else if (token == EnumExpression.DELETE) { 
                prev = deleteInstance(child);
            } else if (token == EnumExpression.CALL_FUNC) {
                prev = callFunc(child);
            } else if (token == EnumExpression.CAST) {
                prev = doCast(child, it, prev, temp);
            } else if (token == EnumExpression.FUNC_POINTER) {
                prev = getFuncPointer(child);
            } else if (token == EnumExpression.ANON_FUNC) {
                prev = defineAnonFunc(child);
            } else {
                prev = doHandleExpression(it.enter(child.getAsChildren()));
            }
        }
        
        return prev;
    }
    
    private BasicObject getTemp(BasicObject prev, BasicObject temp) {
        if (prev.isTemp()) {
            return prev;
        } else if (temp == null) { 
            temp = createTempVar(prev.getType());
            addCode(new DefineVarStatement(this, temp, prev));
            return temp;
        } else {
            addCode(new DefineVarStatement(this, temp, prev));
            return temp;
        }
    }
}
