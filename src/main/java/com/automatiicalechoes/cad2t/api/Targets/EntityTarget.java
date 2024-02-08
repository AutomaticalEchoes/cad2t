package com.automatiicalechoes.cad2t.api.Targets;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Set;
import java.util.function.Predicate;

public class EntityTarget<T extends Entity> extends PredicateTarget<T> {
    private final Set<EntityType<?>> entityTypes;

    public EntityTarget(Set<EntityType<?>> entityTypes, Class<T> tClass){
        super(tClass);
        this.entityTypes = entityTypes;
    }

    public EntityTarget(Set<EntityType<?>> entityTypes, Class<T> tClass, Set<Predicate<T>> predicates){
        super(tClass, predicates);
        this.entityTypes = entityTypes;
    }

    @Override
    public boolean filter(T entity) {
        return (entityTypes.isEmpty() || entityTypes.contains(entity.getType())) && super.filter(entity);
    }

}
