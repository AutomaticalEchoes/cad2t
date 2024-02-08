package com.automatiicalechoes.cad2t.api.Targets;

import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.function.Predicate;

public class BlockTarget<T extends Block> extends PredicateTarget<T>{
    private final Set<Block> blockSet;

    public BlockTarget(Set<Block> blockSet, Class<T> tClass){
        super(tClass);
        this.blockSet = blockSet;
    }

    public BlockTarget(Set<Block> blockSet, Class<T> tClass, Set<Predicate<T>> predicates) {
        super(tClass, predicates);
        this.blockSet = blockSet;
    }

    @Override
    public boolean filter(T t) {
        return (blockSet.isEmpty() || blockSet.contains(t)) && super.filter(t);
    }

    public Set<Block> getBlockSet() {
        return blockSet;
    }
}
