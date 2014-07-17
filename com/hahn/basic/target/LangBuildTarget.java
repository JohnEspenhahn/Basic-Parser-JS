package com.hahn.basic.target;

import java.io.FileOutputStream;
import java.io.IOException;


public interface LangBuildTarget {
    
    public void append(LangCommand cmd);
    public void appendBytecode(Number n);
    
    public int getBytecodeSize();
    public void fillBytecodeTo(String label, int index);
    public void printBytecode();
    
    public void writeRunnableTo(FileOutputStream os) throws IOException;
}
