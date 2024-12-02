package com.example.artspace.data.javaClasses;

import com.example.artspace.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class FavoritesSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Solo incluir username
        jsonObject.addProperty("username", user.getUsername());

        // Serializar el campo favorites
        JsonArray favoritesArray = new JsonArray();
        if (user.getFavorites() != null) {
            for (String favorite : user.getFavorites()) {
                favoritesArray.add(favorite);
            }
        }
        jsonObject.add("favorites", favoritesArray);

        return jsonObject;
    }
}