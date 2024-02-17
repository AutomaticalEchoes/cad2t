package com.automatiicalechoes.cad2t.api.Actions;

import com.automatiicalechoes.cad2t.api.FileLoader;
import com.automatiicalechoes.cad2t.api.Targets.AdditionTarget;
import com.automatiicalechoes.cad2t.api.Targets.BlockTarget;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;

import java.util.Set;

public class CropGrowSpeedChange implements AdditionAction<CropBlock>{
    private final AdditionTarget<CropBlock> target;
    private final Operation operation;

    public CropGrowSpeedChange(AdditionTarget<CropBlock> target, Operation operation) {
        this.target = target;
        this.operation = operation;
    }

    @Override
    public AdditionTarget<CropBlock> getTarget() {
        return target;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public ResourceLocation getResource() {
        return AdditionAction.CROP_GROW_SPEED_CHANGE;
    }

    @Override
    public void run(CropBlock target) {

    }

    public static record Operation(float add, float mul, float change, float min, float max){
        public Operation separate(Operation operation){
            float add = this.add - operation.add;
            float mul = this.mul / operation.mul;
            float change = Math.min(this.change , operation.change);
            float min = Math.min(this.min , operation.min);
            float max = Math.max(this.max , operation.max);
            return new Operation(add, mul, change, min, max);
        }


        public Operation merge(Operation operation){
            float add = this.add + operation.add;
            float mul = this.mul * operation.mul;
            float change = Math.max(this.change, operation.change);
            float min = Math.max(this.min, operation.min);
            float max = Math.min(this.max, operation.max);
            return new Operation(add, mul, change, min, max);
        }

        public float opera(float speed){
            if(change != -1){
                speed = change;
            }else {
                speed = speed * mul + add;
            }
            speed = Math.min(speed, max);
            speed = Math.max(speed, min);
            return speed;
        }

        public static class Builder{
            float add = 0;
            float mul = 1;
            float change = -1;
            float min = 0;
            float max = 999;
            public Builder ADD(float num){
                this.add = num;
                return this;
            }
            public Builder MUL(float mul){
                this.mul = mul;
                return this;
            }
            public Builder CHANGE(float num){
                this.change = num;
                return this;
            }
            public Builder MIN(float num){
                this.min = num;
                return this;
            }
            public Builder MAX(float num){
                this.max = num;
                return this;
            }

            public Operation Build(){
                return new Operation(add,mul,change,min,max);
            }
        }
    }


    public static AdditionAction<CropBlock> fromJson(JsonObject jsonObject){
        BlockTarget<CropBlock> cropBlockBlockTarget = ReadTarget(jsonObject);
        Operation operation = ReadOperation(jsonObject);
        return new CropGrowSpeedChange(cropBlockBlockTarget, operation);
    }

    public static BlockTarget<CropBlock> ReadTarget(JsonObject jsonObject){
        Set<Block> blocks = FileLoader.ReadTargetSet(jsonObject, BuiltInRegistries.BLOCK, Registries.BLOCK);
        return new BlockTarget<>(blocks, CropBlock.class);
    }

    public static Operation ReadOperation(JsonObject jsonObject){
        Operation.Builder builder = new Operation.Builder();
        JsonObject operation = jsonObject.getAsJsonObject("operation");
        if(operation.has("add")) builder.ADD(operation.get("add").getAsFloat());

        if(operation.has("mul")) builder.MUL(operation.get("mul").getAsFloat());

        if(operation.has("change")) builder.CHANGE(operation.get("change").getAsFloat());

        if(operation.has("min")) builder.MIN(operation.get("min").getAsFloat());

        if(operation.has("max")) builder.MAX(operation.get("max").getAsFloat());
        return builder.Build();
    }



}
