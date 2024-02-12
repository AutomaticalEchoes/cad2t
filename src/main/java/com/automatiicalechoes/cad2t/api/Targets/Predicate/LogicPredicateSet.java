package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import java.util.Set;
import java.util.function.Predicate;

public abstract class LogicPredicateSet<T> implements Predicate<T> {
    protected final Set<Predicate<T>> predicates;

    public LogicPredicateSet(Set<Predicate<T>> predicates){
        this.predicates = predicates;
    }

    public boolean isEmpty(){
        return predicates.isEmpty();
    }

    public static <T> LogicPredicateSet<T> Empty(){
        return new Or<>(Set.of());
    }

    public static class Or<T> extends LogicPredicateSet<T>{
        public Or(Set<Predicate<T>> predicates) {
            super(predicates);
        }

        @Override
        public boolean test(T t) {
            return predicates.size() == 0 || predicates.stream().anyMatch(predicate -> predicate.test(t));
        }
    }

    public static class And<T> extends LogicPredicateSet<T>{
        public And(Set<Predicate<T>> predicates) {
            super(predicates);
        }

        @Override
        public boolean test(T t) {
            return predicates.size() ==0 || predicates.stream().allMatch(predicate -> predicate.test(t));
        }
    }
}
