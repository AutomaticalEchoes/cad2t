package com.automatiicalechoes.cad2t.api.Targets;

import java.util.Set;
import java.util.function.Predicate;

public abstract class PredicateTarget<T> implements AdditionTarget<T> {
    private final Set<Predicate<T>> predicates;
    private final Class<T> tClass;

    public PredicateTarget(Class<T> tClass){
        this(tClass, Set.of());
    }

    public PredicateTarget(Class<T> tClass, Set<Predicate<T>> predicates) {
        this.tClass = tClass;
        this.predicates = predicates;
    }

    @Override
    public abstract boolean checkTarget(T t);

    public boolean filter(T t){
        for (Predicate<T> predicate : predicates) {
            if(!predicate.test(t)) return false;
        }
        return true;
    }

    @Override
    public Class<T> tClass() {
        return tClass;
    }
}
