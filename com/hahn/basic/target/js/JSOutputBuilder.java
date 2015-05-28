package com.hahn.basic.target.js;

import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.target.Command;
import com.hahn.basic.target.OutputBuilder;
import com.hahn.basic.target.js.library.LibraryJS;

public class JSOutputBuilder implements OutputBuilder {
    public static final Library JS = new LibraryJS();
    
	private StringBuilder builder;

	public JSOutputBuilder(Compiler compiler) {
		builder = new StringBuilder();
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
	public String getOutputExtension() {
	    return "html";
	}
	
	@Override
	public void appendString(String str) {
	    builder.append(str);
	}

	@Override
	public void append(Command cmd) {
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
