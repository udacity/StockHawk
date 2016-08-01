package com.sam_chordas.android.stockhawk.retrofit;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Abhishek on 29-07-2016.
 */

/*
Using the particular answer to deserialize the JSON Response.
http://stackoverflow.com/questions/23070298/get-nested-json-object-with-gson-using-retrofit/23071080#23071080

 */
public class Deserializable implements JsonDeserializer<List<StockItem>> {

    @Override
    public List<StockItem> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {


        return new Gson().fromJson(

                json
                        .getAsJsonObject().get("query")
                        .getAsJsonObject().get("results")
                        .getAsJsonObject().get("quote")
                        .getAsJsonArray(), typeOfT);
    }
}
