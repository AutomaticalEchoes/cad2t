package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import java.util.Set;

public class DoubleSet implements DoubleRangePredicate {
    private final Set<Double> doubleSet;
    public DoubleSet(Double... a){
        this.doubleSet = Set.of(a);
    }

    public DoubleSet(Set<Double> doubleSet){
        this.doubleSet = doubleSet;
    }

    @Override
    public boolean test(Double aDouble) {
        return doubleSet.contains(aDouble);
    }
}

