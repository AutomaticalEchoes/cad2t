package com.automatiicalechoes.cad2t.mixin;

import com.automatiicalechoes.cad2t.api.ChunkAddition;
import com.automatiicalechoes.cad2t.api.Targets.PredicateTarget;
import com.automatiicalechoes.cad2t.utils.ChunkkitPos;
import com.automatiicalechoes.cad2t.utils.mixinInterface.AdditionEffectAble;
import com.automatiicalechoes.cad2t.utils.mixinInterface.IChunkAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements AdditionEffectAble {
    @Shadow public abstract BlockPos getOnPos();

    @Shadow public Level level;
    private final Set<ChunkAddition<Entity>> activeAdditions = new HashSet<>();
    private final ChunkkitPos chunkkitPos = ChunkkitPos.FromVec3i(BlockPos.ZERO).onChange(serverLevel -> {
        activeAdditions.clear();
        ChunkAccess chunk = serverLevel.getChunk(getOnPos());
        Entity entity = (Entity) (Object) EntityMixin.this;
        activeAdditions.addAll(((IChunkAccess) chunk).getAddition(entity, (entity.getOnPos().getY() - serverLevel.getMinBuildHeight()) >> 4));
    });

    @Inject(method = "tick" , at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        additionTick();
    }

    @Override
    public void additionTick(){
        if(level instanceof ServerLevel serverLevel){
            chunkkitPos.isInsideOrChange(getOnPos(), serverLevel);
            for (ChunkAddition<Entity> activeAddition : activeAdditions) {
                if(activeAddition.getTarget() instanceof PredicateTarget<Entity> predicateTarget && !predicateTarget.filter((Entity) (Object) this)) continue;
                activeAddition.Action().run((Entity) (Object) this);
            }
        }
    }


}
