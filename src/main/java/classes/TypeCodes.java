package classes;

import java.util.HashMap;
import java.util.List;

class TypeCodes {

    public static Byte getTypeCode(Class<?> clazz){
        if (clazz.equals(Boolean.class)){
            return (byte) 1;
        } else if (clazz.equals(Short.class)){
            return (byte) 2;
        } else if (clazz.equals(Integer.class)){
            return (byte) 3;
        } else if (clazz.equals(Long.class)){
            return (byte) 4;
        } else if (clazz.equals(Double.class)){
            return (byte) 5;
        } else if (clazz.equals(Float.class)){
            return (byte) 6;
        } else if (clazz.equals(Character.class)){
            return (byte) 7;
        } else if (clazz.equals(Byte.class)){
            return (byte) 8;
        } else if (clazz.equals(String.class)){
            return (byte) 9;
        } else if (clazz.equals(List.class)){
            return (byte) 10;
        } else if (clazz.equals(HashMap.class)){
            return (byte) 11;
        } else if (clazz.equals(HasdSerializable.class)) {
            return (byte) 12;
        } else {
            throw new RuntimeException("Cannot find out type code.");
        }
    }

}
