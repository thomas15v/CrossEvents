package com.thomas15v.crossevents.formatting;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * Created by thomas15v on 9/06/15.
 */
public interface Formatter <I> extends JsonSerializer<I>, JsonDeserializer<I> {
}
