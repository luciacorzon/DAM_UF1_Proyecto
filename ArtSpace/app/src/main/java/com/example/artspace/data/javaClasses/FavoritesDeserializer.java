package com.example.artspace.data.javaClasses;

import com.example.artspace.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashSet;

public class FavoritesDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String username = jsonObject.get("username").getAsString();

        HashSet<String> favorites = new HashSet<>();
        if (jsonObject.has("favorites") && !jsonObject.get("favorites").isJsonNull()) {
            JsonArray favoritesArray = jsonObject.getAsJsonArray("favorites");
            for (JsonElement element : favoritesArray) {
                favorites.add(element.getAsString());
            }
        }

        return new User(username, "", favorites);
    }
}