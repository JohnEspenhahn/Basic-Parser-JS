package com.hahn.basic.target.js;

import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.library.LibraryBuiltinJS;
import com.hahn.basic.target.js.objects.register.JSRegister;

public class JSBuildTarget extends LangBuildTarget {
    public static final Library BuiltinJS = new LibraryBuiltinJS();
    
	StringBuilder builder;

	public JSBuildTarget() {
		builder = new StringBuilder();
	}
	
	@Override
	public void init() {
	    builder.setLength(0);
	    
	    JSRegister.init();
	    LangCompiler.addLibrary("BuiltinJS");
	}
	
	@Override
	public String getExtension() {
	    return ".js";
	}
	
	@Override
	public String getEOL() {
	    return ";";
	}
	
	@Override
	public void appendString(String str) {
	    builder.append(str);
	}

	@Override
	public void append(ILangCommand cmd) {
		builder.append(cmd.toTarget(this));
	}

	@Override
	public void writeRunnableTo(FileOutputStream os) throws IOException {
		os.write(builder.toString().getBytes());
		os.write('\n');
	}

	@Override
	public String toString() {
	    return builder.toString();
	}
}
