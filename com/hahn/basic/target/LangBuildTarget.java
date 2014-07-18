package com.hahn.basic.target;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class LangBuildTarget {
    public abstract void append(ILangCommand cmd);    
    public abstract void writeRunnableTo(FileOutputStream os) throws IOException;
    
    public void endCodeArea() { }
}
