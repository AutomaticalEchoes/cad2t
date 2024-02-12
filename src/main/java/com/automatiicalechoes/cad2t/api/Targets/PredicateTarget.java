package com.automatiicalechoes.cad2t.api.Targets;

import com.automatiicalechoes.cad2t.api.Targets.Predicate.LogicPredicateSet;

import java.util.Set;
import java.util.function.Predicate;

public abstract class PredicateTarget<T> implements AdditionTarget<T> {
    private final LogicPredicateSet<T> predicates;
    private final Class<T> tClass;

    public PredicateTarget(Class<T> tClass){
        this(tClass, LogicPredicateSet.Empty());
    }

    public PredicateTarget(Class<T> tClass, Set<Predicate<T>> predicates){
        this(tClass, new LogicPredicateSet.And<>(predicates));
    }

    public PredicateTarget(Class<T> tClass, LogicPredicateSet<T> predicates) {
        this.tClass = tClass;
        this.predicates = predicates;
    }

    @Override
    public abstract boolean checkTarget(T t);

    public boolean filter(T t){
        return predicates.test(t);
    }

    @Override
    public Class<T> tClass() {
        return tClass;
    }
}
