package com.automatiicalechoes.cad2t.api;

import com.automatiicalechoes.cad2t.api.Actions.AdditionAction;
import com.automatiicalechoes.cad2t.api.Targets.AdditionTarget;
import net.minecraft.resources.ResourceLocation;


public record ChunkAddition<T>(ResourceLocation registerName, AdditionAction<T> action) {

    public AdditionAction<T> Action() {
        return action;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChunkAddition chunkAddition && chunkAddition.registerName().equals(this.registerName());
    }

    public AdditionTarget<T> getTarget() {
        return action.getTarget();
    }

}
