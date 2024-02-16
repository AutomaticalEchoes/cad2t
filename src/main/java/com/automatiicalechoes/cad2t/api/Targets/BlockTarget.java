package com.automatiicalechoes.cad2t.api.Targets;

import com.automatiicalechoes.cad2t.api.Targets.Predicate.LogicPredicateSet;
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

    public BlockTarget(Set<Block> blockSet, Class<T> tClass, LogicPredicateSet<T> predicates) {
        super(tClass, predicates);
        this.blockSet = blockSet;
    }

    @Override
    public boolean checkTarget(T t) {
        return blockSet.isEmpty() || blockSet.contains(t);
    }

    public Set<Block> getBlockSet() {
        return blockSet;
    }
}
