package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public interface DoubleRangePredicate extends Predicate<Double> {

    static DoubleRangePredicate fromJson(JsonObject jsonObject){
        JsonArray range = jsonObject.getAsJsonArray("range");
        if(range.size() == 2){
            double min = range.get(0).getAsDouble();
            double max = range.get(1).getAsDouble();
            return new DoubleRange(min, max);
        }else {
            Set<Double> doubleSet = new HashSet<>();
            for (JsonElement jsonElement : range) {
                doubleSet.add(jsonElement.getAsDouble());
            }
            return new DoubleSet(doubleSet);
        }

    }
}
