package com.automatiicalechoes.cad2t.mixin;

import com.automatiicalechoes.cad2t.Cad2t;
import com.automatiicalechoes.cad2t.api.Actions.AdditionAction;
import com.automatiicalechoes.cad2t.api.Actions.CropGrowSpeedChange;
import com.automatiicalechoes.cad2t.api.ChunkAddition;
import com.automatiicalechoes.cad2t.api.ChunkAdditionTypes;
import com.automatiicalechoes.cad2t.api.FileLoader;
import com.automatiicalechoes.cad2t.api.Targets.BlockTarget;
import com.automatiicalechoes.cad2t.utils.mixinInterface.AdditionContainer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements AdditionContainer {
    @Shadow private PalettedContainerRO<Holder<Biome>> biomes;
    private Set<ChunkAddition<?>> additions = Cad2t.EMPTY_SET;
    private boolean operationMapLoaded = false;
    private boolean sectionAdditionLoaded = false;
    private Map<Block, CropGrowSpeedChange.Operation> growSpeedMap = Cad2t.EMPTY_MAP;
    private Map<ChunkAddition<?>, CropGrowSpeedChange.Operation> operationLog = Cad2t.EMPTY_LOG_MAP;

    @Override
    public void putAddition(Set<ChunkAddition<?>> chunkAdditionSet) {
        if(chunkAdditionSet.isEmpty()) return;
        if(additions == Cad2t.EMPTY_SET){
            additions = new HashSet<>();
        }
        for (ChunkAddition<?> chunkAddition : chunkAdditionSet) {
            if(additions.add(chunkAddition))
                putOperationFrom(chunkAddition);
        }

    }

    @Override
    public <T> Set<ChunkAddition<T>> getAddition(T target) {
        return additions.stream()
                .filter(chunkAddition -> chunkAddition.getTarget().isValida(target))
                .map(chunkAddition -> (ChunkAddition<T>)chunkAddition)
                .collect(Collectors.toSet());
    }

    @Override
    public void LoadAdditions() {
        if(FileLoader.Loaded && !sectionAdditionLoaded){
            biomes.getAll(biomeHolder -> putAddition(ChunkAdditionTypes.getAdditions(biomeHolder)));
            putAddition(ChunkAdditionTypes.getAdditions(ChunkAdditionTypes.BIOMES_ALL));
            sectionAdditionLoaded = true;
        }
    }

    public Optional<CropGrowSpeedChange.Operation> getOperationFor(Block block){
        if(!sectionAdditionLoaded) LoadAdditions();
        if(!this.operationMapLoaded) LoadOperationMap();
        return Optional.ofNullable(this.growSpeedMap.get(block));
    }

    public void LoadOperationMap(){
        if(additions != Cad2t.EMPTY_SET && !operationMapLoaded){
            putOperationFromSet(additions);
            operationMapLoaded = true;
        }
    }

    public void putOperationFrom(ChunkAddition<?> addition){
        if(addition.Action().getResource() == AdditionAction.CROP_GROW_SPEED_CHANGE){
            if(this.operationLog == Cad2t.EMPTY_LOG_MAP) operationLog = new HashMap<>();
            if(this.operationLog.containsKey(addition)) return;
            CropGrowSpeedChange.Operation operation = ((CropGrowSpeedChange) addition.Action()).getOperation();
            Set<Block> blockSet = ((BlockTarget<CropBlock>) addition.getTarget()).getBlockSet();
            putCropOperation(blockSet, operation);
            operationLog.put(addition,operation);
        }
    }

    public void putOperationFromSet(Collection<ChunkAddition<?>> collections){
        for (ChunkAddition<?> addition : collections) {
            putOperationFrom(addition);
        }
    }

    public void putCropOperation(Set<Block> blocks , CropGrowSpeedChange.Operation operation){
        if(this.growSpeedMap == Cad2t.EMPTY_MAP) growSpeedMap = new HashMap<>();
        for (Block block : blocks) {
            if(!growSpeedMap.containsKey(block)){
                growSpeedMap.put(block, operation);
            }else {
                CropGrowSpeedChange.Operation operation1 = growSpeedMap.get(block);
                CropGrowSpeedChange.Operation operation2 = operation1.merge(operation);
                growSpeedMap.put(block,operation2);
            }
        }
    }
}
