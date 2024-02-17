package com.automatiicalechoes.cad2t.api.Actions;

import com.automatiicalechoes.cad2t.api.FileLoader;
import com.automatiicalechoes.cad2t.api.Targets.AdditionTarget;
import com.automatiicalechoes.cad2t.api.Targets.EntityTarget;
import com.automatiicalechoes.cad2t.api.Targets.Predicate.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ApplyEffect implements AdditionAction<LivingEntity>{
    private final AdditionTarget<LivingEntity> target;
    private final Set<Pair<MobEffect, Integer>> effects;
    public ApplyEffect(EntityTarget<LivingEntity> livingEntityTarget, Set<Pair<MobEffect, Integer>> effects){
        this.target = livingEntityTarget;
        this.effects = effects;
    }

    @Override
    public AdditionTarget<LivingEntity> getTarget() {
        return target;
    }

    @Override
    public ResourceLocation getResource() {
        return AdditionAction.APPLY_EFFECT;
    }

    @Override
    public void run(LivingEntity target) {
        if(target.level.getGameTime() % 100  == 0){
            for (Pair<MobEffect, Integer> effect : effects) {
                target.addEffect(new MobEffectInstance(effect.a,effect.a == MobEffects.NIGHT_VISION ? 420 : 100, effect.b));
            }
        }
    }

    public static AdditionAction<LivingEntity> fromJson(JsonObject jsonObject) {
        EntityTarget<LivingEntity> additionTarget = ReadTarget(jsonObject);
        Set<Pair<MobEffect, Integer>> pairs = ReadEffect(jsonObject);
        return new ApplyEffect(additionTarget, pairs);
    }

    public static EntityTarget<LivingEntity> ReadTarget(JsonObject jsonObject){
        Set<EntityType<?>> entityTypes = FileLoader.ReadTargetSet(jsonObject, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE);
        LogicPredicateSet<LivingEntity> predicates = ReadFilter(jsonObject);
        return predicates.isEmpty()? new EntityTarget<>(entityTypes, LivingEntity.class) : new EntityTarget<>(entityTypes,LivingEntity.class, predicates);
    }

    public static LogicPredicateSet<LivingEntity> ReadFilter(JsonObject jsonObject){
        JsonObject filter = jsonObject.get("filter").getAsJsonObject();
        boolean isOr = false;
        if(filter.has("logic")){
            String logic = filter.get("logic").getAsString();
            isOr = logic.equals("or");
        }
        return ReadPredicateSet(filter,isOr);
    }

    public static LogicPredicateSet<LivingEntity> ReadPredicateSet(JsonObject jsonObject, boolean isOr){
        Set<Predicate<LivingEntity>> predicateSet = new HashSet<>();
        JsonArray predicates = jsonObject.get("predicates").getAsJsonArray();
        for (JsonElement predicate : predicates) {
            JsonObject asJsonObject = predicate.getAsJsonObject();
            predicateSet.add(ReadPredicate(asJsonObject));
        }
        return isOr ? new LogicPredicateSet.Or<>(predicateSet) : new LogicPredicateSet.And<>(predicateSet);
    }

    public static Predicate<LivingEntity> ReadPredicate(JsonObject jsonObject){
        if(jsonObject.has("logic")){
            String logic = jsonObject.get("logic").getAsString();
            boolean isOr = logic.equals("or");
            return ReadPredicateSet(jsonObject, isOr);
        }
        String type = jsonObject.get("type").getAsString();
        return switch (type) {
            case "weather_check" -> WeatherCheck.fromJson(jsonObject);
            case "attribute_check" -> AttributeCheck.fromJson(jsonObject);
            case "equip_check" -> EquipCheck.fromJson(jsonObject);
            case "effect_check" -> EffectCheck.fromJson(jsonObject);
            default -> throw new IllegalStateException("invalid predicate type: " + type);
        };
    }

    public static Set<Pair<MobEffect ,Integer>> ReadEffect(JsonObject jsonObject){
        Set<Pair<MobEffect ,Integer>> pairs = new HashSet<>();
        JsonArray effects = jsonObject.getAsJsonArray("effects");
        for (JsonElement effect : effects) {
            JsonObject asJsonObject = effect.getAsJsonObject();
            String key = asJsonObject.get("key").getAsString();
            ResourceLocation resourceLocation = ResourceLocation.tryParse(key);
            int level = 0;
            if(asJsonObject.has("level")){
                level = asJsonObject.get("level").getAsInt() - 1;
            }
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(resourceLocation);
            if(mobEffect != null) pairs.add(new Pair(mobEffect,level));
        }
        return pairs;
    }
}
