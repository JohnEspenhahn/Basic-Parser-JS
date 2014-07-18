package com.hahn.basic.util;

public class DuplicateDefinitionException extends CompileException {
	private static final long serialVersionUID = -2931528828963017742L;

	public DuplicateDefinitionException(String mss, boolean addLine) {
		super(mss, addLine);
	}

	public DuplicateDefinitionException(String mss, int col) {
		super(mss, col);
	}

	public DuplicateDefinitionException(String mss, String badLinePart) {
		super(mss, badLinePart);
	}

	public DuplicateDefinitionException(String mss) {
		super(mss);
	}

}
