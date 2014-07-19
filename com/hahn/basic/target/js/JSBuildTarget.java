package com.hahn.basic.target.js;

import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.LangBuildTarget;

public class JSBuildTarget extends LangBuildTarget {
	StringBuilder builder; 

	public JSBuildTarget() {
		builder = new StringBuilder();
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

}
