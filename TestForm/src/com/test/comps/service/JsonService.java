package com.test.comps.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.test.comps.TPanel;
import com.test.comps.TProp;
import com.test.editor.BookTypeAdapter;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JsonService {

    private static Gson GSON;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TProp.class, new TProp());
        gsonBuilder.registerTypeAdapter(TPanel.class, new BookTypeAdapter());
        gsonBuilder.setPrettyPrinting();
        GSON = gsonBuilder.create();
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(toJson(obj));
        } catch (IOException e) {
            //??Libra.eMsg(e, true);
        }
    }

    public static <E> E loadFile(Class<E> eClass, String fileName) {
        E result = null;
        try (JsonReader reader = GSON.newJsonReader(new FileReader(fileName))) {

            result = GSON.fromJson(reader, eClass);
        } catch (IOException e) {
            //??Libra.eMsg(e, true);
            e.printStackTrace();
        }
        return result;
    }

    public static <E> E lf(Class<E> eClass, String fileName) {
        E result = null;
        try (JsonReader reader = GSON.newJsonReader(new FileReader(fileName))) {

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
