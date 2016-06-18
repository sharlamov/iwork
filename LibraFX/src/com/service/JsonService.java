package com.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JsonService {

    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <E> List<E> fromJsonList(String json, Class<E> eClass) {
        Gson gson = new Gson();
        return gson.fromJson(json, new ListOfSomething<>(eClass));
    }

    public static <E> E fromJson(String json, Class<E> eClass) {
        return GSON.fromJson(json, eClass);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static void saveFile(Object obj, String fileName) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(toJson(obj));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <E> E loadFile(Class<E> eClass, String fileName) {
        try {
            JsonReader reader = new JsonReader(new FileReader(fileName));
            return GSON.fromJson(reader, eClass);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    static class ListOfSomething<X> implements ParameterizedType {

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
