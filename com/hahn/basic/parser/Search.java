package com.hahn.basic.parser;

import java.security.InvalidParameterException;

import com.hahn.basic.lexer.IEnumToken;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.util.FlagMap;

class Search extends IParser {
    public final IParser Owner;
    public final IEnumExpression ParentExpression;
    
    private final FlagMap<EnumOption> Flags;
    
    public final PackedToken[] Stream;
    public int stream_start;

    private Node temp_node;
    
    private int stream_offset;
    private int furthest_idx;
    
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
        this.subexp_end = subexpEnd;
        
        reset();
    }
    
    private void reset() {
        this.stream_offset = 0;
        this.Flags.clear();
        
        this.stream_start = Owner.getStreamIdx();
        if (stream_start < Stream.length) {
            PackedToken startToken = Stream[stream_start];
            this.temp_node = new Node(Owner.getNode(), (Enum<?>) ParentExpression, startToken.row, startToken.col);
        } else { 
            this.temp_node = new Node(Owner.getNode(), (Enum<?>) ParentExpression, 0, 0);
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
        if (getFurthest() > Owner.getFurthest()) {
            Owner.setFurthest(getFurthest());
        } else if (getStreamIdx() > Owner.getFurthest()) {
            Owner.setFurthest(getStreamIdx());
        }
    }

    private boolean match() {
        for (subexp_idx = subexp_start, stream_offset = 0; subexp_idx < subexp_end && subexp_idx < subexp.length; subexp_idx++) {
            Enum<?> obj = subexp[subexp_idx];

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
                return Flags.isMarked(EnumOption.OPTIONAL) && atEnd(subexp_idx + 1);
            }
        }

        return atEnd(subexp_idx);
    }
    
    private boolean atEnd(int idx) {
        return (idx >= subexp_end || idx >= subexp.length);
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
        if (expObj instanceof IEnumToken) {
            if (expObj == streamToken.token) {
                stream_offset += 1;
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
        }

        return true;
    }
    
    private boolean doBlockSearch(EnumOption endToken, boolean loop) {
        boolean matched = false;
        int startIdx = subexp_idx + 1, endIdx = findNextToken(endToken);
        
        Search search = new Search(this, ParentExpression);
        while (search.doSubExpressionSearch(subexp, startIdx, endIdx, false)) {
            matched = true;
            
            if (!loop) break;
        }
        
        this.subexp_idx = endIdx;
        return matched;
    }
    
    private void finish(boolean addSelf) {
        Node owner = Owner.getNode(); 
        
        if (addSelf) owner.addChild(getNode());
        else owner.addChildren(getNode().getAsChildren());
        
        Owner.addToStreamIdx(stream_offset);
    }

    public int findNextToken(Enum<?> token) {
        for (int i = subexp_idx; i < subexp.length; i++) {
            if (subexp[i] == token) return i;
        }

        return 0;
    }
    
    public int getExpressionIdx() {
        return subexp_idx;
    }

    @Override
    public Node getNode() {
        return temp_node;
    }

    @Override
    public PackedToken[] getStream() {
        return Stream;
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
    public int getFurthest() {
        return furthest_idx;
    }
    
    @Override
    public void setFurthest(int amnt) {
        this.furthest_idx = amnt;
    }
}
