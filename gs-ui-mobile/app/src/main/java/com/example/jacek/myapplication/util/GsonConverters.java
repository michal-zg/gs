package com.example.jacek.myapplication.util;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;

import org.joda.time.DateTime;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by jacek on 2016-09-18.
 */
public class GsonConverters {

    public static class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

        @Override
        public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return new DateTime(json.getAsString());
        }
    }

}
