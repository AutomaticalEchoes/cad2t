package com.automatiicalechoes.cad2t.command;

import com.automatiicalechoes.cad2t.api.ChunkAddition;
import com.automatiicalechoes.cad2t.api.ChunkAdditionTypes;
import com.automatiicalechoes.cad2t.api.FileLoader;
import com.automatiicalechoes.cad2t.utils.mixinInterface.IChunkAccess;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber
public class LogCommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> CAD2T =
            Commands.literal("cad2t").requires(commandSourceStack -> commandSourceStack.hasPermission(2));
    public static final LiteralArgumentBuilder<CommandSourceStack>  LOG =
            Commands.literal("log");
    public static final LiteralArgumentBuilder<CommandSourceStack>  LOAD_INFO =
            Commands.literal("load_info");
    public static final LiteralArgumentBuilder<CommandSourceStack> REGISTRY_INFO =
            Commands.literal("registry_info");
    public static final LiteralArgumentBuilder<CommandSourceStack> ADDITIONS_IN_CHUNK =
            Commands.literal("additions_in_chunk");
    public static void register(CommandDispatcher<CommandSourceStack> p_249870_) {
        p_249870_.register(CAD2T
                .then(LOG
                        .then(LOAD_INFO.executes(context -> LoadInfo(context.getSource())))
                        .then(REGISTRY_INFO.executes(context -> RegistryInfo(context.getSource())))
                .then(ADDITIONS_IN_CHUNK.executes(context -> AdditionsInChunk(context.getSource())))
                )
        );
    }

    @SubscribeEvent
    public static void RegisterCommand(RegisterCommandsEvent event){
        register(event.getDispatcher());
    }

    public static int LoadInfo(CommandSourceStack sourceStack) throws CommandSyntaxException {
        if(!FileLoader.Loaded){
            sourceStack.sendFailure(Component.translatable("additions no load"));
            return 0;
        }
        for (String s : FileLoader.LoadMessage) {
            sourceStack.sendFailure(Component.translatable(s));
        }
        return 0;
    }

    public static int RegistryInfo(CommandSourceStack sourceStack) throws CommandSyntaxException {
        if(!FileLoader.Loaded){
            sourceStack.sendFailure(Component.translatable("additions no load"));
            return 0;
        }
        for (ResourceLocation resourceLocation : ChunkAdditionTypes.resourceLocations()) {
            sourceStack.sendFailure(Component.translatable(resourceLocation.toShortLanguageKey()));
        }
        return 0;
    }

    public static int AdditionsInChunk(CommandSourceStack sourceStack) throws CommandSyntaxException{
        if(!FileLoader.Loaded){
            sourceStack.sendFailure(Component.translatable("additions no load"));
            return 0;
        }
        Vec3 position = sourceStack.getPosition();
        LevelChunk chunk = sourceStack.getLevel().getChunk((int) position.x >> 4, (int) position.z >> 4);
        Set<ChunkAddition<?>> activeAddition = ((IChunkAccess) chunk).getActiveAddition((int) (position.y - sourceStack.getLevel().getMinBuildHeight()) >> 4);
        for (ChunkAddition<?> chunkAddition : activeAddition) {
            sourceStack.sendFailure(Component.literal(chunkAddition.registerName().toShortLanguageKey()));
        }
        return 1;
    }


}
