package com.hahn.basic.parser;

import java.security.InvalidParameterException;

import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.lexer.regex.IEnumRegexToken;
import com.hahn.basic.util.FlagMap;

class Search extends IParser {
    public final IParser Owner;
    public final IEnumExpression ParentExpression;
    
    private final FlagMap<EnumOption> Flags;
    
    public final PackedToken[] Stream;
    public int stream_start;

    private Node temp_node;
    
    private int stream_offset;
    private int farthest_idx;
    
    private Enum<?>[] subexp;
    private int subexp_start, subexp_end;
    private int subexp_idx;

    public static boolean doExpressionSearch(IParser owner, IEnumExpression parentExp, boolean add) {
        Search search = new Search(owner, parentExp);
        for (Enum<?>[] subexp : parentExp.getSubExpressions()) {
            if (search.doSubExpressionSearch(subexp, 0, Integer.MAX_VALUE, add)) {
                return true;
            }
        }

        return false;
    }
    
    private Search(IParser owner, IEnumExpression parentExp) {
        super(owner.getTokenClass(), owner.getExpressionClass());
        if (!parentExp.getClass().isEnum()) throw new InvalidParameterException("Parent Expression must be an Enum");
        
        this.Owner = owner;
        this.Stream = owner.getStream();

        this.ParentExpression = parentExp;

        this.Flags = new FlagMap<EnumOption>();

        reset();
    }
    
    private void initSubExp(Enum<?>[] subexp, int subexpStart, int subexpEnd) {
        this.subexp = subexp;
        this.subexp_start = subexpStart;
        this.subexp_end = Math.min(subexpEnd, subexp.length);
        
        reset();
    }
    
    private void reset() {
        this.stream_offset = 0;
        this.Flags.clear();
        
        this.stream_start = Owner.getStreamIdx();
        if (stream_start < Stream.length) {
            PackedToken startToken = Stream[stream_start];
            if (this.temp_node != null && this.temp_node.getRow() == startToken.row && this.temp_node.getCol() == startToken.col) {
                this.temp_node.clearChildren();
            } else {
                this.temp_node = new Node(Owner.getNode(), (Enum<?>) ParentExpression, startToken.idx, startToken.row, startToken.col);
            }
        } else { 
            this.temp_node = null;
        }
    }
    
    private boolean doSubExpressionSearch(Enum<?>[] subExp, int subexpStart, int subexpEnd, boolean addSelf) {
        initSubExp(subExp, subexpStart, subexpEnd);
        
        if (match()) {
            checkFurthest();
            finish(addSelf);
            return true;
        } else {
            checkFurthest();
            return false;
        }
    }
    
    private void checkFurthest() {
        if (getFarthest() > Owner.getFarthest()) {
            Owner.setFarthest(getFarthest());
        } else if (getStreamIdx() > Owner.getFarthest()) {
            Owner.setFarthest(getStreamIdx());
        }
    }

    private boolean match() {
        resetStreamOffset();
        resetExpressionIdx();
        for (; getExpressionIdx() < getExpressionEndIdx(); addToExpressionIdx(1)) {
            Enum<?> obj = getExpressionToken();

            if (obj instanceof EnumOption) {
                // Handle option, fail if required
                if (!handleOption((EnumOption) obj)) { 
                    return false;
                }
            } else if (getStreamIdx() < Stream.length) {
                PackedToken token = Stream[getStreamIdx()];
                if (objectsDoMatch(obj, token)) {
                    Flags.reset(EnumOption.OPTIONAL);
                } else {
                    // Match complete fail
                    return false;
                }
            } else {
                // End of stream without end of expression
                return Flags.isMarked(EnumOption.OPTIONAL) && atEnd(1);
            }
        }

        return atEnd(0);
    }
    
    private boolean atEnd(int additionalOffset) {
        return (getExpressionIdx() + additionalOffset >= getExpressionEndIdx());
    }

    private boolean objectsDoMatch(Enum<?> expObj, PackedToken streamToken) {
        if (verifyMatch(expObj, streamToken)) {
            return true;
        } else if (Flags.isMarked(EnumOption.OPTIONAL)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean verifyMatch(Enum<?> expObj, PackedToken streamToken) {
        if (expObj instanceof IEnumRegexToken) {
            if (expObj == streamToken.token) {
                addToStreamIdx(1);
                temp_node.addChild(new Node(temp_node, streamToken));
                return true;
            } else {
                return false;
            }
        } else if (expObj instanceof IEnumExpression) {
            return Search.doExpressionSearch(this, (IEnumExpression) expObj, true);
        } else {
            return false;
        }
    }
    
    private boolean handleOption(EnumOption o) {
        if (o == EnumOption.OPTIONAL) {
            Flags.mark(EnumOption.OPTIONAL);
        } else if (o == EnumOption.LOOP_START) {
            return doBlockSearch(EnumOption.LOOP_END, true);
        } else if (o == EnumOption.LOOP_OPTIONAL_START) {
            doBlockSearch(EnumOption.LOOP_OPTIONAL_END, true);
        } else if (o == EnumOption.BLOCK_OPTIONAL_START) {
            doBlockSearch(EnumOption.BLOCK_OPTIONAL_END, false);
        } else if (o == EnumOption.FULL_LOOP) {
            Search.doExpressionSearch(this, ParentExpression, false);
        } else if (o == EnumOption.LAZY_MATCH) {
            return doLazyMatch();
        }

        return true;
    }
    
    private boolean doBlockSearch(EnumOption endToken, boolean loop) {
        boolean matched = false;
        int startIdx = getExpressionIdx() + 1, endIdx = findNextExpressionToken(endToken);
        if (endIdx < startIdx) return false;
        
        Search search = new Search(this, ParentExpression);
        while (search.doSubExpressionSearch(subexp, startIdx, endIdx, false)) {
            matched = true;
            
            if (!loop) break;
        }
        
        addToExpressionIdx(endIdx - startIdx);
        return matched;
    }
    
    private boolean doLazyMatch() {
        addToExpressionIdx(1);
        Enum<?> endToken = getExpressionToken();
        
        // Check if can match
        boolean matched = false;
        int endStreamIdx = getStreamIdx() + 1;
        while (endStreamIdx < Stream.length) {
            PackedToken streamToken = Stream[endStreamIdx];
            endStreamIdx += 1;
            
            if (streamToken.token == endToken) {
                matched = true;
                break;
            }
        }
        
        if (matched) {
            addToExpressionIdx(1);
           
            // Actually add the lazy matched tokens
            while (getStreamIdx() < endStreamIdx) {
                PackedToken streamToken = Stream[getStreamIdx()];
                temp_node.addChild(new Node(temp_node, streamToken));
                
                addToStreamIdx(1);
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    private void finish(boolean addSelf) {
        Node owner = Owner.getNode(); 
        
        if (addSelf) owner.addChild(getNode());
        else owner.addChildren(getNode().getAsChildren());
        
        Owner.addToStreamIdx(stream_offset);
    }

    /**
     * Find the next token equal to the given on in the current subexpression
     * @param token The token to find
     * @return The index of the next token in the subexpression or -1 if not found
     */
    public int findNextExpressionToken(Enum<?> token) {
        for (int i = subexp_idx; i < subexp.length; i++) {
            if (subexp[i] == token) return i;
        }

        return -1;
    }
    
    /**
     * Get current index being looked at in the subexpression
     * @return The index within the subexpression
     */
    public int getExpressionIdx() {
        return subexp_idx;
    }
    
    public int getExpressionEndIdx() {
        return subexp_end;
    }
    
    /**
     * Get the token currently being looked at in the subexpression
     * @return The token being looked at
     */
    public Enum<?> getExpressionToken() {
        return subexp[getExpressionIdx()];
    }
    
    /**
     * Shift the current index being looked at in the subexpression
     * @param amnt The amount to add to the subexpression index
     */
    public void addToExpressionIdx(int amnt) {
        subexp_idx += amnt;
    }
    
    public void setExpressionIdx(int idx) {
        subexp_idx = idx;
    }
    
    public void resetExpressionIdx() {
        setExpressionIdx(subexp_start);
    }

    @Override
    public Node getNode() {
        return temp_node;
    }

    @Override
    public PackedToken[] getStream() {
        return Stream;
    }

    /**
     * Reset the offset to the stream contributed by this search
     */
    public void resetStreamOffset() {
        this.stream_offset = 0;
    }

    @Override
    public void addToStreamIdx(int amnt) {
        this.stream_offset += amnt;
    }

    @Override
    public int getStreamIdx() {
        return stream_start + stream_offset;
    }
    
    @Override
    public int getFarthest() {
        return farthest_idx;
    }
    
    @Override
    public void setFarthest(int amnt) {
        this.farthest_idx = amnt;
    }
}
