package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.automatiicalechoes.cad2t.api.FileLoader;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.Set;
import java.util.function.Predicate;

public class ItemCheck implements Predicate<Item> {
    private final Set<Holder<Item>> holderSet;

    public ItemCheck(Set<Holder<Item>> holderSet){
        this.holderSet = holderSet;
    }

    @Override
    public boolean test(Item item) {
        return holderSet.contains(item.builtInRegistryHolder());
    }

    public static ItemCheck fromJson(JsonObject jsonObject){
        Set<Holder<Item>> items = FileLoader.ReadHolder(jsonObject, "items", Registries.ITEM);
        return new ItemCheck(items);
    }


    public static ItemCheck fromJson(JsonObject jsonObject, String nameSpace){
        Set<Holder<Item>> items = FileLoader.ReadHolder(jsonObject, nameSpace, Registries.ITEM);
        return new ItemCheck(items);
    }
}
