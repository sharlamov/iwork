package com.test.comps;

import com.google.gson.*;
import com.test.comps.service.JsonService;
import com.test.table.PropTable;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;

public class TProp extends HashMap<String, Object> implements JsonDeserializer<Object> {


    public <T> T fetch(Object key) {
        Object obj = get(key);
        return obj == null ? null : (T) obj;
    }

    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jobject = (JsonObject) jsonElement;

        TProp prop = new TProp();
        for (Entry<String, JsonElement> el: jobject.entrySet()) {
            switch (el.getKey()){
                case "font" :
                    prop.put(el.getKey(), JsonService.fromJson(el.getValue().toString(), Font.class));
                    break;
                    default: prop.put(el.getKey(), el.getValue());
            }
        }

        return prop;
    }
}
