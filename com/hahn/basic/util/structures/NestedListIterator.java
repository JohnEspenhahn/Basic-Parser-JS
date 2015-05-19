package com.hahn.basic.util.structures;

import java.util.ListIterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class NestedListIterator<E> implements ListIterator<E> {
    private final Stack<ListIterator<E>> itStack;
    
    public NestedListIterator(List<E> list) {
        this(list.listIterator());
    }
    
    public NestedListIterator(ListIterator<E> it) {
        itStack = new Stack<ListIterator<E>>();
        itStack.push(it);
    }
    
    public NestedListIterator<E> enter(ListIterator<E> it) {
        itStack.push(it);
        return this;
    }
    
    public NestedListIterator<E> enter(List<E> list) {
        return enter(list.listIterator());
    }
    
    @Override
    public void add(E e) {
        itStack.peek().add(e);
    }
    
    @Override
    public void remove() {
        itStack.peek().remove();
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
    public int nextIndex() {
        return itStack.peek().nextIndex();
    }

    @Override
    public boolean hasPrevious() {
        if (itStack.isEmpty()) {
            return false;
        } else {
            for (int i = itStack.size() - 1; i >= 0; i--) {
                if (itStack.get(i).hasPrevious()) {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    public boolean hasShallowPrevious() {
        return !itStack.isEmpty() && itStack.peek().hasPrevious();
    }

    @Override
    public E previous() {
        while (!hasShallowPrevious()) {
            itStack.pop();
        }
        
        if (itStack.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return itStack.peek().previous();
        }
    }

    @Override
    public int previousIndex() {
        return itStack.peek().previousIndex();
    }

    @Override
    public void set(E e) {
        itStack.peek().set(e);
    }
}
