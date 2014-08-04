package com.hahn.basic.parser;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.lexer.IEnumToken;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.exceptions.ParseExpressionException;

public class Parser extends IParser {   
    private final BNFParser bnfParser;
    
    private Node node;  
    private int furthest_idx;
    
    private int stream_idx;
    private PackedToken[] stream;  
    
    public Parser(Class<? extends IEnumToken> enumTokens, Class<? extends IEnumExpression> enumExpressions) {
        super(enumTokens, enumExpressions);
        
        this.bnfParser = new BNFParser(this);
        
        parseExpressions();
    }

    /**
     * Parse a stream lexed by the lexer with the same IEnumToken
     * @param stream The stream of tokens lexed
     * @return The head node of the parse tree
     * @throws CompileException If parse failed
     */
    public Node parse(PackedToken[] stream) {
        this.stream_idx = 0;
        this.stream = stream;
        this.furthest_idx = 0;
        this.node = Node.newTopNode();
        
        if (stream.length == 0 || (Search.doExpressionSearch(this, EnumExpression.START, false) && this.atEnd())) {
            return this.node;
        } else {
            PackedToken last = stream[furthest_idx];
            
            throw new ParseExpressionException(last.row, last.col);
        }
    }
    
    private void parseExpressions() {        
        // Compile all expressions
        for (IEnumExpression exp: getExpressionClass().getEnumConstants()) {
            try {
                if (exp.getSubExpressions() == null) {
                    exp.setSubExpressions(bnfParser.parseBNF(exp.getBNFString()));
                }
            } catch (Exception err) {
                System.err.println(err.getMessage() + " when parsing BNF of " + exp);
                System.exit(1);
            }
        }   
    }
    
    public boolean atEnd() {
        return stream_idx == stream.length;
    }

    public int getStreamLength() {
        return stream.length;
    }
    
    @Override
    public void addToStreamIdx(int amnt) {
        this.stream_idx += amnt;
    }
    
    @Override
    public int getStreamIdx() {
        return stream_idx;
    }
    
    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public PackedToken[] getStream() {
        return stream;
    }
    
    @Override
    public int getFurthest() {
        return furthest_idx;
    }
    
    @Override
    public void setFurthest(int amnt) {
        if (amnt >= stream.length) {
            this.furthest_idx = stream.length - 1;
        } else {
            this.furthest_idx = amnt;
        }
    }
}
