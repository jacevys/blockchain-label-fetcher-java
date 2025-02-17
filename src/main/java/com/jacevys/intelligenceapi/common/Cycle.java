package com.jacevys.intelligenceapi.common;

import java.util.Iterator;
import java.util.List;

public class Cycle<T> implements Iterator<T> {
    private final List<T> items;
    private int index = 0;

    public Cycle(List<T> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        T item = items.get(index);
        index = (index + 1) % items.size();

        return item;
    }
}