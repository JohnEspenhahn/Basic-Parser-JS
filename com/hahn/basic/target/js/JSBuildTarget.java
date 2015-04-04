package com.hahn.basic.target.js;

import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.register.SimpleRegisterFactory;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.library.LibraryJQuery;
import com.hahn.basic.target.js.library.LibraryJS;

public class JSBuildTarget extends LangBuildTarget {
    public static final Library JS = new LibraryJS();
    public static final Library JQuery = new LibraryJQuery();
    
	StringBuilder builder;
	SimpleRegisterFactory registerFactory;

	public JSBuildTarget() {
		builder = new StringBuilder();
		registerFactory = new SimpleRegisterFactory();
	}
	
	@Override
	public void init() {
	    builder.setLength(0);
	    
	    registerFactory.reset();
	    
	    LangCompiler.addLibrary("JS");
	}
	
	@Override
	public String getStart() {
	    return "<!DOCTYPE html>\n<html>\n<body onload='main()'></body>\n<head>\n";
	}
	
	@Override
	public String getInputExtension() {
	    return "b";
	}
	
	@Override
	public String getExtension() {
	    return "html";
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
		builder.append(cmd.toTarget());
	}
	
	@Override
	public String getCodeEnd() {
	    return (Main.getInstance().isPretty() ? "\n" : ";");
	}
	
	@Override
	public String getContentStart() {
	    JSPretty.addTab();
	    return (Main.getInstance().isPretty() ? "\n<script>\nfunction main() {\n" : "<script>function main(){");
	}
	
	@Override
    public String getEnd() {
        return (Main.getInstance().isPretty() ? "\n" : "") + "</script>\n</head>\n</html>";
    }
	
	@Override
	public String getContentEnd() {
	    StringBuilder endBuilder = new StringBuilder();
	    
	    JSPretty.removeTab();
	    endBuilder.append(Main.getInstance().isPretty() ? "}\n" : "}");
        
        return endBuilder.toString();
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
