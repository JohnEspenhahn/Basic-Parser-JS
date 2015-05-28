package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.target.CommandFactory;

public interface IFileObject {
    public CodeFile getFile();
    public CommandFactory getFactory();
}
