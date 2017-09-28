package com.estafet.batchprime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PrimeBatch implements Serializable {

    private List<Integer> primeList;

    public PrimeBatch() {
        this.primeList = new ArrayList<>();
    }

    public PrimeBatch(List<Integer> primeList) {
        this.primeList = primeList;
    }

    public List<Integer> getPrimeList() {
        return primeList;
    }

    public void setPrimeList(List<Integer> primeList) {
        this.primeList = primeList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimeBatch that = (PrimeBatch) o;

        return primeList != null ? primeList.equals(that.primeList) : that.primeList == null;
    }

    @Override
    public int hashCode() {
        return primeList != null ? primeList.hashCode() : 0;
    }
}
