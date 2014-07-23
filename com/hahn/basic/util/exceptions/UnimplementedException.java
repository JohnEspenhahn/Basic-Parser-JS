package com.hahn.basic.util.exceptions;

public class UnimplementedException extends RuntimeException {
    private static final long serialVersionUID = 6739938716670567244L;

    public UnimplementedException() {
        super();
    }
    
    public UnimplementedException(String message) {
        super(message);
    }
    
}
