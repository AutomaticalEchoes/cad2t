package com.automatiicalechoes.cad2t.mixin;

import com.automatiicalechoes.cad2t.api.Actions.CropGrowSpeedChange;
import com.automatiicalechoes.cad2t.api.ChunkAddition;
import com.automatiicalechoes.cad2t.api.ChunkAdditionTypes;
import com.automatiicalechoes.cad2t.utils.mixinInterface.AdditionContainer;
import com.automatiicalechoes.cad2t.utils.mixinInterface.IChunkAccess;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements IChunkAccess {
    private boolean additionLoaded = false;

    @Shadow @Final protected LevelChunkSection[] sections;

    @Shadow @Final protected LevelHeightAccessor levelHeightAccessor;

    @Shadow @Final private Map<Structure, LongSet> structuresRefences;

    @Shadow @Final private Map<Structure, StructureStart> structureStarts;

    @Shadow public abstract ChunkPos getPos();

    @Inject(method = "setStartForStructure", at = @At("RETURN"))
    private void setStartForStructure(Structure p_223010_, StructureStart value, CallbackInfo ci){
        if((ChunkAccess)(Object)this instanceof LevelChunk)
            AddStructureAdditions(value);
    }

    @Inject(method = "addReferenceForStructure", at = @At("RETURN"))
    private void addReferenceForStructure(Structure p_223007_, long p_223008_, CallbackInfo ci){
        if((ChunkAccess)(Object)this instanceof LevelChunk && this.levelHeightAccessor instanceof ServerLevel serverLevel){
            ChunkPos chunkPos = new ChunkPos(p_223008_);
            LevelChunk chunk = serverLevel.getChunk(chunkPos.x, chunkPos.z);
            StructureStart startForStructure = chunk.getStartForStructure(p_223007_);
            if(startForStructure != null)
                AddStructureAdditions(startForStructure);
        }
    }

    @Override
    public Set<ChunkAddition<?>> getActiveAddition(int sectionNum) {
        if(!additionLoaded) LoadAdditions();
        if(sectionNum >= sections.length) return Set.of();
        LevelChunkSection section = sections[sectionNum];
        return ((AdditionContainer)section).getActiveAddition();
    }

    @Override
    public void LoadAdditions(){
        if(this.levelHeightAccessor instanceof ServerLevel serverLevel){
            for (LevelChunkSection section : this.sections) {
                ((AdditionContainer)section).LoadAdditions();
            }
            for (Map.Entry<Structure, LongSet> entry : structuresRefences.entrySet()) {
                for (Long aLong : entry.getValue()) {
                    ChunkPos chunkPos = new ChunkPos(aLong);
                    if(chunkPos == this.getPos()) continue;
                    LevelChunk chunk = serverLevel.getChunk(chunkPos.x, chunkPos.z);
                    StructureStart startForStructure = chunk.getStartForStructure(entry.getKey());
                    if(startForStructure != null)
                        AddStructureAdditions(startForStructure);
                }
            }
            for (StructureStart value : structureStarts.values()) {
                AddStructureAdditions(value);
            }
            this.additionLoaded = true;
        }
    }


    @Override
    public <T> Set<ChunkAddition<T>> getAddition(T target, int sectionNum) {
        if(!additionLoaded) LoadAdditions();
        if(sectionNum >= sections.length) return Set.of();
        LevelChunkSection section = sections[sectionNum];
        return ((AdditionContainer)section).getAddition(target);
    }

    @Override
    public void putAddition(Set<ChunkAddition<?>> additions, int sectionNum) {
        if(sectionNum >= sections.length) return;
        LevelChunkSection section = sections[sectionNum];
        ((AdditionContainer)section).putAddition(additions);
    }

    public Optional<CropGrowSpeedChange.Operation> getCropGrowOperation(Block block, int sectionNum){
        if(sectionNum >= sections.length) return Optional.empty();
        LevelChunkSection section = sections[sectionNum];
        return ((AdditionContainer)section).getOperationFor(block);
    }

    private void AddStructureAdditions(StructureStart value) {
        if(levelHeightAccessor instanceof ServerLevel serverLevel){
            Set<ChunkAddition<?>> additions = new HashSet<>();
            serverLevel.registryAccess().registry(Registries.STRUCTURE)
                    .flatMap(structureRegistry -> structureRegistry.getResourceKey(value.getStructure())
                    .flatMap(structureRegistry::getHolder))
                    .ifPresent(structureReference -> additions.addAll(ChunkAdditionTypes.getAdditions(structureReference)));
            additions.addAll(ChunkAdditionTypes.getAdditions(ChunkAdditionTypes.STRUCTURE_ALL));
            if(additions.isEmpty()) return;
            int max = (value.getBoundingBox().maxY() - levelHeightAccessor.getMinBuildHeight()) >> 4;
            int min = (value.getBoundingBox().minY() - levelHeightAccessor.getMinBuildHeight()) >> 4;
            for(int i = min; i <= max ; i++){
                putAddition(additions,i);
            }
        }
    }
}
