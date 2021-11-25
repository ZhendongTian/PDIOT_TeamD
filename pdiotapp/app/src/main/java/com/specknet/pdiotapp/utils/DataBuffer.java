/**
 * DataBuffer class, used to store and retrieve live data.
 * Initialize with a buffer_size, retrieve with getLastN(), size is automatically maintained.
 */


package com.specknet.pdiotapp.utils;

import java.util.ArrayList;

public class DataBuffer<Float> extends ArrayList<Float> {

    private int maxSize;

    public DataBuffer(int size){
        this.maxSize = size;
    }

    public boolean add(Float k){
        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize);
        }
        return r;
    }

    public ArrayList getLastN(int n){
        ArrayList<Float> tail = (ArrayList<Float>) super.subList(Math.max(super.size() - n, 0), super.size());
        return tail;
    }

    public Float getYoungest() {
        return get(size() - 1);
    }

    public Float getOldest() {
        return get(0);
    }
}