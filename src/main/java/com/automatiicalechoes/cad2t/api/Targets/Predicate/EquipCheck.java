package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class EquipCheck implements Predicate<LivingEntity> {
    private final HashMap<EquipmentSlot, ItemCheck> predicates;
    public EquipCheck(HashMap<EquipmentSlot, ItemCheck> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(LivingEntity livingEntity) {
        for (Map.Entry<EquipmentSlot, ItemCheck> entry : predicates.entrySet()) {
            ItemStack itemBySlot = livingEntity.getItemBySlot(entry.getKey());
            if(!entry.getValue().test(itemBySlot.getItem())) return false;
        }
        return true;
    }

    public static EquipCheck fromJson(JsonObject jsonObject){
        HashMap<EquipmentSlot,ItemCheck> predicates = new HashMap<>();
        if(jsonObject.has("head")) {
            ItemCheck itemStack = ItemCheck.fromJson(jsonObject, "head");
            predicates.put(EquipmentSlot.HEAD, itemStack);
        }

        if(jsonObject.has("chest")){
            ItemCheck itemStack = ItemCheck.fromJson(jsonObject, "chest");
            predicates.put(EquipmentSlot.CHEST, itemStack);
        }

        if(jsonObject.has("legs")){
            ItemCheck itemStack = ItemCheck.fromJson(jsonObject, "legs");
            predicates.put(EquipmentSlot.LEGS, itemStack);
        }

        if(jsonObject.has("feet")){
            ItemCheck itemStack = ItemCheck.fromJson(jsonObject, "feet");
            predicates.put(EquipmentSlot.FEET, itemStack);
        }

        if(jsonObject.has("mainhand")){
            ItemCheck itemStack = ItemCheck.fromJson(jsonObject, "mainhand");
            predicates.put(EquipmentSlot.MAINHAND, itemStack);
        }

        if(jsonObject.has("offhand")){
            ItemCheck itemStack = ItemCheck.fromJson(jsonObject, "offhand");
            predicates.put(EquipmentSlot.OFFHAND, itemStack);
        }

        return new EquipCheck(predicates);
    }
}
