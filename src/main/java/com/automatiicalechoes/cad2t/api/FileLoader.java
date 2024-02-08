package com.automatiicalechoes.cad2t.api;

import com.automatiicalechoes.cad2t.Cad2t;
import com.automatiicalechoes.cad2t.api.Actions.AdditionAction;
import com.google.gson.*;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class FileLoader {
    public static boolean Loaded = false;
    public static Gson gson = new Gson();
    public static int success = 0;
    public static int fail = 0;
    public static void Load(){
        success = 0;
        fail = 0;
        Optional<File> directory = getDirectoryFile();
        directory.ifPresentOrElse(FileLoader::ReadDirectory, () -> Cad2t.LOGGER.info("File 'Additions' is not exist"));
        Loaded = true;
    }

    public static Optional<File> getDirectoryFile(){
        Path path = FMLPaths.GAMEDIR.relative().toAbsolutePath();
        File file = path.toFile();
        if(file.isDirectory() && file.listFiles() != null){
            return Arrays.stream(file.listFiles())
                    .filter(file1 -> file1.isDirectory() && file1.getName().toLowerCase().matches("additions"))
                    .findFirst();
        }
        return Optional.empty();
    }

    public static void ReadDirectory(File file){
        if(file.listFiles() != null){
            List<File> files = Arrays.stream(file.listFiles()).filter(file1 -> file1.getName().toLowerCase().endsWith(".json")).toList();
            for (File file1 : files) {
                try {
                    TryBuild(file1);
                } catch (Exception e) {
                    Cad2t.LOGGER.info("Load addition file '" + file1.getName() +"' fail, cause:" + e.getMessage());
                    fail ++;
                }
            }
        }else {
            Cad2t.LOGGER.info("No addition loaded");
        }
        Cad2t.LOGGER.info("End loading addition, Success:" + success + ", Fail:" + fail + ".");
    }

    public static AdditionAction<?> ReadAction(JsonObject jsonObject){
        JsonArray actions = jsonObject.getAsJsonArray("actions");
        for (JsonElement action : actions) {
            JsonObject actionJson = action.getAsJsonObject();
            String type = actionJson.get("type").getAsString();
            ResourceLocation resourceLocation = ParseStringToResource(type, 0);
            Function<JsonObject, AdditionAction<?>> codec = AdditionAction.ACTION_CODECS.get(resourceLocation);
            if(codec == null)
                throw new NullPointerException("Invalid action type '" + type + "'");
            return codec.apply(actionJson);
        }
        throw new NullPointerException("no actions were found");
    }

    public static Set<Holder<?>> ReadSource(JsonObject jsonObject){
        Set<Holder<?>> holderSet = new HashSet<>();
        if(jsonObject.has("structures")){
            Set<Holder<Structure>> structureHolderSet = ReadHolder(jsonObject, "structures", Registries.STRUCTURE);
            holderSet.addAll(structureHolderSet.isEmpty() ? ChunkAdditionTypes.All("structures") : structureHolderSet);
        }
        if(jsonObject.has("biomes")){
            Set<Holder<Biome>> biomesHolderSet = ReadHolder(jsonObject, "biomes", Registries.BIOME);
            holderSet.addAll(biomesHolderSet.isEmpty() ? ChunkAdditionTypes.All("biomes") : biomesHolderSet);
        }
        return holderSet;
    }

    public static <T> Set<Holder<T>> ReadHolder(JsonObject jsonObject , String nameSpace,  ResourceKey<Registry<T>> registryResourceKey){
        JsonArray jsonArray = jsonObject.getAsJsonArray(nameSpace);
        Set<Holder<T>> holderSet = new HashSet<>();
        HashSet<TagKey<T>> tags = new HashSet<>();
        HashSet<ResourceLocation> resourceLocations = new HashSet<>();

        for (JsonElement jsonElement : jsonArray) {
            String asString = jsonElement.getAsString().toLowerCase();
            if(asString.startsWith("tag/")){
                ResourceLocation resourceLocation = ParseStringToResource(asString, 4);
                TagKey<T> structureTagKey = TagKey.create(registryResourceKey, resourceLocation);
                tags.add(structureTagKey);
            }else if(asString.startsWith("key/")){
                ResourceLocation resourceLocation = ParseStringToResource(asString, 4);
                resourceLocations.add(resourceLocation);
            }
        }

        Registry<T> registry = Cad2t.REGISTRY_ACCESS.registryOrThrow(registryResourceKey);

        if(!tags.isEmpty()){
            for (TagKey<T> tag : tags) {
                registry.getTag(tag).ifPresent(holders -> {
                    for (Holder<T> holder : holders) {
                        holderSet.add(holder);
                    }
                });
            }
        }

        if(!resourceLocations.isEmpty()){
            for (ResourceLocation resourceLocation : resourceLocations) {
                ResourceKey<T> key = ResourceKey.create(registry.key(), resourceLocation);
                registry.getHolder(key).ifPresent(holderSet::add);
            }
        }

       return holderSet;
    }

    public static ResourceLocation ParseStringToResource(String string, int cutLength){
        String tag = string.substring(cutLength);
        ResourceLocation resourceLocation = ResourceLocation.tryParse(tag);
        if(resourceLocation == null)
            throw new ResourceLocationException("Invalid format '" + string + "'");
        return resourceLocation;
    }

    public static <T> Set<T> ReadTargetSet(JsonObject jsonObject, Registry<T> registry, ResourceKey<Registry<T>> registryResourceKey){
        Set<T> typeSet = new HashSet<>();
        JsonArray action_targets = jsonObject.getAsJsonArray("action_targets");
        for (JsonElement action_target : action_targets) {
            String asString = action_target.getAsString();
            if(asString.startsWith("key/")){
                ResourceLocation resourceLocation = FileLoader.ParseStringToResource(asString, 4);
                Optional<T> optionalT = registry.getOptional(resourceLocation);
                optionalT.ifPresentOrElse(typeSet::add, () -> {
                    throw new NullPointerException("resource '" + resourceLocation + "' not found in " + registryResourceKey);
                });

            }else if(asString.startsWith("tag/")){
                ResourceLocation resourceLocation = FileLoader.ParseStringToResource(asString, 4);
                TagKey<T> tagKey = TagKey.create(registryResourceKey, resourceLocation);
                registry.getTag(tagKey).ifPresentOrElse(holders -> {
                    for (Holder<T> holder : holders) {
                        typeSet.add(holder.get());
                    }
                }, () -> {
                    throw new NullPointerException("tag '" + tagKey + "' not found in " + registryResourceKey);
                });
            }
        }
        return typeSet;
    }

    public static void TryBuild(File file) throws Exception{
        String s = FileUtils.readFileToString(file);
        JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();
        String name = file.getName().replace(".json", "");
        ChunkAddition<?> addition = new ChunkAddition<>(new ResourceLocation(Cad2t.MODID, name), ReadAction(jsonObject));
        Set<Holder<?>> holderSet = ReadSource(jsonObject);
        ChunkAdditionTypes.RegisterAddition(addition,holderSet);
        success ++;
        Cad2t.LOGGER.info("load Addition '" + file.getName() +"' success");
    }

}
