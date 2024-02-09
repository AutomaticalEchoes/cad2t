package com.automatiicalechoes.cad2t.api.Targets;

public interface AdditionTarget<T> {
    boolean checkTarget(T t);
    Class<T> tClass();
    default boolean isValida(Object o){
        if(tClass().isInstance(o)){
            return checkTarget((T)o);
        }
        return false;
    }

}
