package com.automatiicalechoes.cad2t.api.Targets.Predicate;

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
    private final Function<LivingEntity,Double> attributeGetter;
    private final DoubleRangePredicate range;

    public AttributeCheck(Function<LivingEntity,Double> attributeGetter, DoubleRangePredicate range){
        this.attributeGetter = attributeGetter;
        this.range = range;
    }

    @Override
    public boolean test(LivingEntity livingEntity) {
        Double value = attributeGetter.apply(livingEntity);
        if(value.equals(PROJECT_NULL)) return false;
        return range.test(value);
    }

    public static AttributeCheck fromJson(JsonObject attributeCheck){
        String attribute = attributeCheck.get("attribute").getAsString();
        Function<LivingEntity, Double> attributeFunction = FUNCTION_MAP.get(attribute);
        if(attributeFunction == null) throw new NullPointerException("invalid attribute '" + attribute + "'.");
        DoubleRangePredicate doubleRangePredicate = DoubleRangePredicate.fromJson(attributeCheck);
        return new AttributeCheck(attributeFunction,doubleRangePredicate);
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
