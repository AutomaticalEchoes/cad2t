package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.automatiicalechoes.cad2t.api.FileLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;
import java.util.function.Predicate;

public class EffectCheck implements Predicate<LivingEntity> {
    private final Set<MobEffect> mobEffects;
    public EffectCheck(Set<MobEffect> mobEffects){
        this.mobEffects = mobEffects;
    }
    @Override
    public boolean test(LivingEntity livingEntity) {
        return livingEntity.getActiveEffects().stream().anyMatch(mobEffectInstance -> mobEffects.contains(mobEffectInstance.getEffect()));
    }

    public static EffectCheck fromJson(JsonObject jsonObject){
        JsonArray effects = jsonObject.getAsJsonArray("effects");
        Set<MobEffect> mobEffects = FileLoader.ReadSet(effects, BuiltInRegistries.MOB_EFFECT, Registries.MOB_EFFECT);
        return new EffectCheck(mobEffects);
    }
}
