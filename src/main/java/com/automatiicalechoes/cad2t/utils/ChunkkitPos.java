package com.automatiicalechoes.cad2t.utils;

import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Consumer;

public class ChunkkitPos {
    private int x, y ,z;
    private Consumer<ServerLevel> action = null;

    public ChunkkitPos(int x, int y , int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ChunkkitPos onChange(Consumer<ServerLevel> consumer){
        this.action = consumer;
        return this;
    }

    public boolean isInsideOrChange(Vec3i vec3i ,ServerLevel serverLevel){
        int x1 = vec3i.getX() >> 4;
        int y1 = vec3i.getY() >> 4;
        int z1 = vec3i.getZ() >> 4;
        boolean flag = (x == x1 && y == y1 && z == z1);
        if(!flag && action != null){
            this.x = x1;
            this.y = y1;
            this.z = z1;
            action.accept(serverLevel);
        }
        return flag;
    }

    public int getY() {
        return y;
    }

    public static ChunkkitPos FromVec3i(Vec3i vec3i){
        int x = vec3i.getX() >> 4;
        int y = vec3i.getY() >> 4;
        int z = vec3i.getZ() >> 4;
        return new ChunkkitPos(x,y,z);
    }
}
