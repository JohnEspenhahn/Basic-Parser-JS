package com.hahn.basic.util.exceptions;

import com.hahn.basic.parser.Node;

public class DuplicateDefinitionException extends CompileException {
	private static final long serialVersionUID = -2931528828963017742L;

	public DuplicateDefinitionException(String mss, Node node) {
		super(mss, node);
	}

	public DuplicateDefinitionException(String mss, String badLinePart) {
		super(mss, badLinePart);
	}

}
