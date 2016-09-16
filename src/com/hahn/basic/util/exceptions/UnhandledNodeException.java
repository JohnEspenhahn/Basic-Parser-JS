package com.hahn.basic.util.exceptions;

import com.hahn.basic.parser.Node;

public class UnhandledNodeException extends RuntimeException {
    private static final long serialVersionUID = -2815917376490412847L;

    public UnhandledNodeException(Node unhandled_node) {
        super("Unhandled node: " + unhandled_node);
    }
}
