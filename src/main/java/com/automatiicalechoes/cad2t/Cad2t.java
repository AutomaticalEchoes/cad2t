package com.automatiicalechoes.cad2t;

import com.automatiicalechoes.cad2t.api.Actions.CropGrowSpeedChange;
import com.automatiicalechoes.cad2t.api.ChunkAddition;
import com.automatiicalechoes.cad2t.api.FileLoader;
import com.mojang.logging.LogUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Cad2t.MODID)
public class Cad2t
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cad2t";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final Set<ChunkAddition<?>> EMPTY_SET = Set.of();
    public static final Map<Block, CropGrowSpeedChange.Operation> EMPTY_MAP = Map.of();
    public static final Map<ChunkAddition<?>, CropGrowSpeedChange.Operation> EMPTY_LOG_MAP = Map.of();
    public static RegistryAccess REGISTRY_ACCESS;
    public Cad2t() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        REGISTRY_ACCESS = event.getServer().registryAccess();
        FileLoader.Load();
    }

}
