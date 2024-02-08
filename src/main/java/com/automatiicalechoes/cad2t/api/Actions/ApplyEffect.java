package com.automatiicalechoes.cad2t.api.Actions;

import com.automatiicalechoes.cad2t.api.FileLoader;
import com.automatiicalechoes.cad2t.api.Targets.AdditionTarget;
import com.automatiicalechoes.cad2t.api.Targets.EntityTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Predicate;

import java.util.HashSet;
import java.util.Set;

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
        if(target.level().getGameTime() % 80  == 0){
            for (Pair<MobEffect, Integer> effect : effects) {
                target.addEffect(new MobEffectInstance(effect.a,80, effect.b));
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
        return new EntityTarget<>(entityTypes, LivingEntity.class);
    }

    public static Set<Predicate<LivingEntity>> ReadPredicates(JsonObject jsonObject){
        JsonObject predicates = jsonObject.get("predicates").getAsJsonObject();
        predicates.has()
    }



    public static Set<Pair<MobEffect ,Integer>> ReadEffect(JsonObject jsonObject){
        Set<Pair<MobEffect ,Integer>> pairs = new HashSet<>();
        JsonArray effects = jsonObject.getAsJsonArray("effects");
        for (JsonElement effect : effects) {
            String key = effect.getAsJsonObject().get("key").getAsString();
            ResourceLocation resourceLocation = ResourceLocation.tryParse(key);
            int level = effect.getAsJsonObject().get("level").getAsInt();
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(resourceLocation);
            if(mobEffect != null) pairs.add(new Pair(mobEffect,level));
        }
        return pairs;
    }
}
