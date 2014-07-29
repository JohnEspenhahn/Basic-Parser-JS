package com.hahn.basic.target;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class LangBuildTarget {
    public abstract void init();
    
    public abstract void append(ILangCommand cmd);
    public abstract void appendString(String str);
    public abstract void writeRunnableTo(FileOutputStream os) throws IOException;
    
    @Override
    public abstract String toString();
    
    public String endCodeArea() { return ""; }
}
