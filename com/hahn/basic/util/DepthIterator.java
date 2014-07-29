package com.hahn.basic.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class DepthIterator<E> implements Iterator<E> {
    private final Stack<Iterator<E>> itStack;
    
    public DepthIterator(Iterator<E> it) {
        itStack = new Stack<Iterator<E>>();
        itStack.push(it);
    }

    @Override
    public boolean hasNext() {
        if (itStack.isEmpty()) {
            return false;
        } else {
            for (int i = itStack.size() - 1; i >= 0; i--) {
                if (itStack.get(i).hasNext()) {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    public boolean hasShallowNext() {
        return !itStack.isEmpty() && itStack.peek().hasNext();
    }
    
    public DepthIterator<E> enter(Iterator<E> it) {
        itStack.push(it);
        return this;
    }
    
    public DepthIterator<E> enter(List<E> list) {
        return enter(list.iterator());
    }

    @Override
    public E next() {
        while (!hasShallowNext()) {
            itStack.pop();
        }
        
        if (itStack.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return itStack.peek().next();
        }
    }

    @Override
    public void remove() {
        itStack.peek().remove();
    }
}
