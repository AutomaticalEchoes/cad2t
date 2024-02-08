package com.automatiicalechoes.cad2t.api.Targets.Predicate;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public abstract class WeatherCheck<T> implements Predicate<T> {
    @Nullable
    final Boolean isRaining;
    @Nullable
    final Boolean isThundering;

    public WeatherCheck(@Nullable Boolean p_82059_, @Nullable Boolean p_82060_) {
        this.isRaining = p_82059_;
        this.isThundering = p_82060_;
    }

    boolean baseTest(ServerLevel serverlevel){
        if (this.isRaining != null && this.isRaining != serverlevel.isRaining()) {
            return false;
        } else {
            return this.isThundering == null || this.isThundering == serverlevel.isThundering();
        }
    }

    @Override
    public abstract boolean test(T t);

    public static FromEntity fromJson(JsonObject jsonObject){
        JsonObject weather = jsonObject.get("weather_check").getAsJsonObject();
        Boolean isRain = null;
        Boolean isThunder = null;
        if (weather.has("rain")){
            isRain = weather.get("rain").getAsBoolean();
        }
        if (weather.has("thunder")){
            isThunder = weather.get("thunder").getAsBoolean();
        }
        return new FromEntity(isRain, isThunder);
    }

    public static class FromEntity extends WeatherCheck<Entity>{

        public FromEntity(@org.jetbrains.annotations.Nullable Boolean p_82059_, @org.jetbrains.annotations.Nullable Boolean p_82060_) {
            super(p_82059_, p_82060_);
        }

        @Override
        public boolean test(Entity entity) {
            if(!(entity.level() instanceof ServerLevel serverlevel)) return false;
            return super.baseTest(serverlevel);
        }
    }
}
