package com.automatiicalechoes.cad2t.mixin;

import com.automatiicalechoes.cad2t.api.Actions.CropGrowSpeedChange;
import com.automatiicalechoes.cad2t.utils.mixinInterface.IChunkAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CropBlock.class)
public class CropBlockMixin {
    @Inject(method = "getGrowthSpeed", at = @At("RETURN"), cancellable = true)
    private static void growSpeed(Block p_52273_, BlockGetter p_52274_, BlockPos p_52275_, CallbackInfoReturnable<Float> cir){
        if(p_52274_ instanceof ServerLevel serverLevel){
            ChunkAccess chunk = serverLevel.getChunk(p_52275_);
            Optional<CropGrowSpeedChange.Operation> cropGrowOperation = ((IChunkAccess) chunk).getCropGrowOperation(p_52273_, (p_52275_.getY() - serverLevel.getMinBuildHeight()) >> 4);
            cropGrowOperation.ifPresent(operation -> {
                float opera = operation.opera(cir.getReturnValue());
                cir.setReturnValue(opera);
            });
        }
    }
}
