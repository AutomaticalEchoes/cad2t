package com.automatiicalechoes.cad2t.api.Targets.Predicate;

public record DoubleRange(double min, double max) implements DoubleRangePredicate {

    @Override
    public boolean test(Double aDouble) {
        return aDouble >= min && aDouble <= max;
    }

}
