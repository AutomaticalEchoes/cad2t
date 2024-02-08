package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.automatiicalechoes.cad2t.api.FileLoader;
import com.google.gson.JsonObject;
import net.minecraft.client.OptionInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.Optional;
import java.util.function.Predicate;

public class AttributeCheck implements Predicate<LivingEntity> {
    private final Attribute attribute;
    private final DoubleRangePredicate range;

    public AttributeCheck(Attribute attribute, DoubleRangePredicate range){
        this.attribute = attribute;
        this.range = range;
    }

    @Override
    public boolean test(LivingEntity livingEntity) {
        AttributeInstance attribute = livingEntity.getAttribute(this.attribute);
        if(attribute == null) return false;
        double value = attribute.getValue();
        return range.test(value);
    }

    public static AttributeCheck fromJson(JsonObject jsonObject){
        JsonObject attribute_check = jsonObject.get("attribute_check").getAsJsonObject();
        ResourceLocation attributeResource = FileLoader.ParseStringToResource(attribute_check.get("attribute").getAsString(), 0);
        Optional<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.getOptional(attributeResource);
        if(attribute.isEmpty()) throw new NullPointerException("invalid attribute '" + attributeResource + "' , not found in attribute registry");
        DoubleRangePredicate doubleRangePredicate = DoubleRangePredicate.fromJson(jsonObject);
        return new AttributeCheck(attribute.get(),doubleRangePredicate);
    }
}
