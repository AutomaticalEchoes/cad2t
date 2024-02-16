package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public interface DoubleRangePredicate extends Predicate<Double> {
    static DoubleRangePredicate fromJson(JsonArray array){
        if(array.size() == 2){
            double min = array.get(0).getAsDouble();
            double max = array.get(1).getAsDouble();
            if(min < max) return new DoubleRange(min, max);
        }

        Set<Double> doubleSet = new HashSet<>();
        for (JsonElement jsonElement : array) {
            doubleSet.add(jsonElement.getAsDouble());
        }
        return new DoubleSet(doubleSet);
    }
}
