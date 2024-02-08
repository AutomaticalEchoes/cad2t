package com.automatiicalechoes.cad2t.api;

import com.automatiicalechoes.cad2t.Cad2t;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ChunkAdditionTypes {
    public static final ResourceLocation NULL = new ResourceLocation(Cad2t.MODID,"null");
    public static final ChunkAddition<?> EMPTY = new ChunkAddition<>(NULL, null);
    public static final Holder<Integer> STRUCTURE_ALL = new Holder.Direct<>(0);
    public static final Holder<Integer> BIOMES_ALL = new Holder.Direct<>(1);
    private static final HashMap<ResourceLocation , ChunkAddition<?>> ADDITIONS = new HashMap<>();
    private static final HashMap<Holder<?>, Set<ResourceLocation>> SOURCES = new HashMap<>();
    protected static void RegisterAddition(@Nonnull ChunkAddition<?> chunkAddition, Set<Holder<?>> sources) throws Exception{
        if (chunkAddition.getRegisterName().equals(NULL) ){
            throw new Exception("addition cannot not be named 'null'");
        }else if (ADDITIONS.containsKey(chunkAddition.getRegisterName())){
            throw new Exception("addition '" + chunkAddition.getRegisterName() + "' already registered" );
        }else {
            ADDITIONS.put(chunkAddition.getRegisterName(), chunkAddition);
            for (Holder<?> source : sources) {
                if(SOURCES.containsKey(source)){
                    SOURCES.get(source).add(chunkAddition.getRegisterName());
                }else {
                    HashSet<ResourceLocation> resourceLocations = new HashSet<>();
                    resourceLocations.add(chunkAddition.getRegisterName());
                    SOURCES.put(source,resourceLocations);
                }
            }
        }
    }

    public static Optional<ChunkAddition<?>> getAddition(ResourceLocation resourceLocation){
        return Optional.ofNullable(ADDITIONS.getOrDefault(resourceLocation,null));
    }

    public static Set<ChunkAddition<?>> getAdditionsByActionType(ResourceLocation resourceLocation){
        return getAdditionsByActionType(ADDITIONS.values(),resourceLocation);
    }

    public static Set<ChunkAddition<?>> getAdditionsByActionType(Collection<ChunkAddition<?>> collection, ResourceLocation resourceLocation){
        return collection.stream()
                .filter(chunkAddition -> chunkAddition.Action().getResource().equals(resourceLocation))
                .collect(Collectors.toSet());
    }

    public static Set<ChunkAddition<?>> getAdditions(Holder<?> source){
        if(SOURCES.containsKey(source)){
            Set<ChunkAddition<?>> additions = new HashSet<>();
            for (ResourceLocation resourceLocation : SOURCES.get(source)) {
                ChunkAddition<?> chunkAddition = ADDITIONS.get(resourceLocation);
                if(chunkAddition != null){
                    additions.add(chunkAddition);
                }
            }
            return additions;
        }
        return Set.of();
    }

    public static <T> Set<ChunkAddition<T>> getAdditions(Holder<?> source, T target) {
        Set<ChunkAddition<T>> collect = new HashSet<>();
        Set<ChunkAddition<?>> additions = getAdditions(source);
        if(!additions.isEmpty()){
            collect.addAll(additions.stream()
                    .filter(chunkAddition -> chunkAddition.getTarget().isValida(target))
                    .map(chunkAddition -> (ChunkAddition<T>) chunkAddition).collect(Collectors.toSet()));
        }
        return collect;
    }

    public static Set<Holder<Integer>> All(String s){
        if(s.equals("structures")) return Set.of(STRUCTURE_ALL);
        if(s.equals("biomes")) return Set.of(BIOMES_ALL);
        return Set.of();
    }

}


