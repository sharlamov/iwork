package com.test.comps.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.test.comps.TProp;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JsonService {

    public static Gson GSON = new GsonBuilder()/*.registerTypeAdapter(TProp.class, new TProp())*/.setPrettyPrinting().create();


    public static void ll(String s){
        try {

            String json = new String(Files.readAllBytes(Paths.get(s)));
            TProp prop = GSON.fromJson(json, TProp.class);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static <E> List<E> fromJsonList(String json, Class<E> eClass) {
        return GSON.fromJson(json, new ListOfSomething<>(eClass));
    }

    public static <E> E fromJson(String json, Class<E> eClass) {
        return GSON.fromJson(json, eClass);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static void saveFile(Object obj, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
            bw.write(toJson(obj));
        } catch (IOException e) {
            //??Libra.eMsg(e, true);
        }
    }

    public static <E> E loadFile(Class<E> eClass, String fileName) {
        E result = null;
        try (JsonReader reader = new JsonReader(new FileReader(fileName))){

            result = GSON.fromJson(reader, eClass);
        } catch (IOException e) {
            //??Libra.eMsg(e, true);
            e.printStackTrace();
        }
        return result;
    }

    private static class ListOfSomething<X> implements ParameterizedType {

        private Class<?> wrapped;

        public ListOfSomething(Class<X> wrapped) {
            this.wrapped = wrapped;
        }

        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }

    }
}
