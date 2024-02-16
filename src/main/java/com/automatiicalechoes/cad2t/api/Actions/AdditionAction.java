package com.automatiicalechoes.cad2t.api.Actions;

import com.automatiicalechoes.cad2t.Cad2t;
import com.automatiicalechoes.cad2t.api.Targets.AdditionTarget;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

public interface AdditionAction<T> {
    HashMap<ResourceLocation, Function<JsonObject, AdditionAction<?>>> ACTION_CODECS = new HashMap<>();
    ResourceLocation APPLY_EFFECT = AdditionAction.RegisterActionTypeWithCodec("apply_effect", ApplyEffect::fromJson);
    ResourceLocation CROP_GROW_SPEED_CHANGE = AdditionAction.RegisterActionTypeWithCodec("crop_grow_speed_change", CropGrowSpeedChange::fromJson);
    AdditionTarget<T> getTarget();
    ResourceLocation getResource();
    void run(T target);


    static ResourceLocation RegisterActionTypeWithCodec(String name, Function<JsonObject, AdditionAction<?>> codec){
        ResourceLocation resourceLocation = new ResourceLocation(Cad2t.MODID, name);
        ACTION_CODECS.put(resourceLocation, codec);
        return resourceLocation;
    }
}
