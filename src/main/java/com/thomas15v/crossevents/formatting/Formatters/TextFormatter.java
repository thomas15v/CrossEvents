package com.thomas15v.crossevents.formatting.Formatters;

import com.google.gson.*;
import com.thomas15v.crossevents.formatting.Formatter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.lang.reflect.Type;

/**
 * Created by thomas15v on 9/06/15.
 */
public class TextFormatter implements Formatter<Text> {
    @Override
    public Text deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Texts.parseJson(json.getAsString());
    }

    @Override
    public JsonElement serialize(Text src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Texts.toJson(src));
    }
}
