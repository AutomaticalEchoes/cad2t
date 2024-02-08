package com.automatiicalechoes.cad2t.api.Targets;

public interface AdditionTarget<T> {
    boolean filter(T t);
    Class<T> tClass();
    default boolean isValida(Object o){
        if(tClass().isInstance(o)){
            return filter((T)o);
        }
        return false;
    }

}
