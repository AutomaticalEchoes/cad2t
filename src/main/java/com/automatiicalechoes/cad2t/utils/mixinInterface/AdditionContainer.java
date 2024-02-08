package com.automatiicalechoes.cad2t.utils.mixinInterface;

import com.automatiicalechoes.cad2t.api.Actions.CropGrowSpeedChange;
import com.automatiicalechoes.cad2t.api.ChunkAddition;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.Set;

public interface AdditionContainer {
    void putAddition(Set<ChunkAddition<?>> chunkAddition);
    <T> Set<ChunkAddition<T>> getAddition(T target);
    void LoadAdditions();
    Optional<CropGrowSpeedChange.Operation> getOperationFor(Block block);
}
