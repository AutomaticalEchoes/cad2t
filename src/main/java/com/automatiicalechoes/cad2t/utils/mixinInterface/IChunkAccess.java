package com.automatiicalechoes.cad2t.utils.mixinInterface;

import com.automatiicalechoes.cad2t.api.Actions.CropGrowSpeedChange;
import com.automatiicalechoes.cad2t.api.ChunkAddition;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.Set;

public interface IChunkAccess {
    <T> Set<ChunkAddition<T>> getAddition(T target, int y);
    void putAddition(Set<ChunkAddition<?>> chunkAddition, int y);
    void LoadAdditions();
    Optional<CropGrowSpeedChange.Operation> getCropGrowOperation(Block block, int sectionNum);
}
