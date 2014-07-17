package com.hahn.basic.intermediate.statements;

import java.util.ArrayList;
import java.util.List;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.VarParameters;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.register.Register;
import com.hahn.basic.intermediate.register.StackRegister;
import com.hahn.basic.intermediate.register.StackRegisterSnapshot;
import com.hahn.basic.target.LangObject;
import com.hahn.basic.target.asm.raw.ASMCommand;

public class StoreRegsStatement extends Statement {
    private int regsStored = -1;
    
    private VarParameters[] vps;
    private List<AdvancedObject> allVars;
    
    public StoreRegsStatement(FuncHead f, Param[] params) {
        super(f);
        
        allVars = new ArrayList<AdvancedObject>();
        
        // Create parameters
        vps = new VarParameters[params.length];
        for (int i = 0; i < params.length; i++) {
            vps[i] = new VarParameters(f, StackRegister.next(), params[i].getName(), params[i].getType());
            
            f.addVar(vps[i]);
            addVar(vps[i]);
        }
    }
    
    public void addVar(AdvancedObject v) {
        if (!allVars.contains(v)) {
            allVars.add(v);
        }
    }
    
    public AdvancedObject[] getCurrentVars() {
        return allVars.toArray(new AdvancedObject[allVars.size()]);
    }
    
    public int getRegsStored() {
        if (regsStored != -1) { 
            return regsStored;
        } else {
            throw new RuntimeException("Called `getRegsUsed` before calculated registers used!");
        }
    }
    
    @Override
    public void addTargetCode() { 
        // Instead call doStore after the FuncHead has been optimized
    }

    /**
     * Call after the FuncHead has been optimized, but before it is converted
     * to the final target form
     */
    public void doStore() {
        regsStored = 0;
        boolean[] counted = new boolean[8];
        for (AdvancedObject var: allVars) {
            if (var.getRegister() instanceof Register) {
                Register r = (Register) var.getRegister();
                if (!counted[r.getAsByte()]) {
                    counted[r.getAsByte()] = true;
                    regsStored += 1;
                }
            }
        }
        
        // Store registers used
        if (regsStored >= ReturnStatement.MIN_REGS_FOR_PUSHA) {
            regsStored = 8;
            addCode(new ASMCommand(OPCode.PUSHA));
        } else {
            List<Register> regs = Register.values();
            for (int i = 0; i < regsStored; i++) {
                addCode(new ASMCommand(OPCode.SET, (LangObject) Register.PUSH, (LangObject) regs.get(i)));
            }
        }
        
        StackRegisterSnapshot.setForcedOffset(regsStored);
        
        // Load parameters to registers
        for (VarParameters v: vps) {
            if (v.hasRegister() && !v.isRegisterOnStack()) {
                addCode(new Command(this, OPCode.SET, v.getRegister(), v.getStackRegister()));
            }
        }
    }
    
    /**
     * Call after the FuncHead has been converted to the final target form
     */
    public void clearStore() {
        StackRegisterSnapshot.setForcedOffset(0);
    }
    
    @Override
    public String toString() {
        return "DoStoreRegs";
    }
}
