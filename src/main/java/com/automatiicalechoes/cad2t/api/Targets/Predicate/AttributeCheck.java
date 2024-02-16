package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class AttributeCheck implements Predicate<LivingEntity> {
    public static final Double PROJECT_NULL = -24.24D;
    public static final Map<String,Function<LivingEntity, Double>> FUNCTION_MAP = new HashMap<>();
    static {
        FUNCTION_MAP.put("health", AttributeCheck::getHeath);
        FUNCTION_MAP.put("attack_damage", livingEntity -> getAttribute(livingEntity, Attributes.ATTACK_DAMAGE));
        FUNCTION_MAP.put("attack_speed", livingEntity -> getAttribute(livingEntity,Attributes.ATTACK_SPEED));
        FUNCTION_MAP.put("armor", livingEntity -> getAttribute(livingEntity,Attributes.ARMOR));
        FUNCTION_MAP.put("movement_speed", livingEntity -> getAttribute(livingEntity,Attributes.MOVEMENT_SPEED));
        FUNCTION_MAP.put("max_health", livingEntity -> getAttribute(livingEntity,Attributes.MAX_HEALTH));
    }

    private final HashMap<Function<LivingEntity,Double>, DoubleRangePredicate> attributes_predicate;

    public AttributeCheck(HashMap<Function<LivingEntity,Double>, DoubleRangePredicate> attributes_predicate){
        this.attributes_predicate = attributes_predicate;
    }

    @Override
    public boolean test(LivingEntity livingEntity) {
        for (Map.Entry<Function<LivingEntity, Double>, DoubleRangePredicate> entry : attributes_predicate.entrySet()) {
            Double value = entry.getKey().apply(livingEntity);
            if(value.equals(PROJECT_NULL) || !entry.getValue().test(value)) return false;
        }
      return true;
    }

    public static AttributeCheck fromJson(JsonObject attributeCheck){
        HashMap<Function<LivingEntity,Double>, DoubleRangePredicate> map = new HashMap<>();
        for (Map.Entry<String, Function<LivingEntity, Double>> entry : FUNCTION_MAP.entrySet()) {
            if(attributeCheck.has(entry.getKey())){
                JsonArray array = attributeCheck.get(entry.getKey()).getAsJsonArray();
                DoubleRangePredicate doubleRangePredicate = DoubleRangePredicate.fromJson(array);
                map.put(entry.getValue(),doubleRangePredicate);
            }
        }
        return new AttributeCheck(map);
    }

    public static double getHeath(LivingEntity livingEntity){
        return livingEntity.getHealth();
    }

    public static double getAttribute(LivingEntity livingEntity, Attribute attr){
        AttributeInstance attribute = livingEntity.getAttribute(attr);
        if(attribute == null) return PROJECT_NULL;
        return attribute.getValue();
    }

}
