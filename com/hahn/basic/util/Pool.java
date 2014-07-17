package com.hahn.basic.util;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Pool<T> {
    private ConcurrentLinkedQueue<T> free;
    
    public Pool() {
        free = new ConcurrentLinkedQueue<T>();
        
        for (int i = 0; i < 16; i++) {
            free.add(newInstance());
        }
    }
    
    public abstract T newInstance();
    public abstract void clearInstance(T t);
    
    public T get() {
        T l = free.poll();
        if (l == null) {
            l = newInstance();
        }
        
        return l;
    }
    
    public void free(T l) {      
        clearInstance(l);
        free.offer(l);
    }
}
