package com.example.artspace.data.javaClasses;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FavoritesDAO {

    private Context context;
    private Gson gson;

    public FavoritesDAO(Context context) {
        this.context = context;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // Método para obtener el archivo de favoritos del usuario
    private File getFavoritesFile(String userId) {
        return new File(context.getFilesDir(), userId + "_favorites.json");
    }

    // Método para obtener la lista de favoritos de un usuario
    public List<String> getFavorites(String userId) {
        File file = getFavoritesFile(userId);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fileInputStream);
            String[] favoritesArray = gson.fromJson(reader, String[].class);
            List<String> favorites = new ArrayList<>();
            if (favoritesArray != null) {
                for (String artworkId : favoritesArray) {
                    favorites.add(artworkId);
                }
            }
            reader.close();
            return favorites;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Método para agregar una obra de arte a los favoritos
    public void addFavorite(String userId, String artworkId) {
        List<String> favorites = getFavorites(userId);
        if (!favorites.contains(artworkId)) {
            favorites.add(artworkId);
            writeFavorites(userId, favorites);
        }
    }

    // Método para eliminar una obra de arte de los favoritos
    public void removeFavorite(String userId, String artworkId) {
        List<String> favorites = getFavorites(userId);
        favorites.remove(artworkId);
        writeFavorites(userId, favorites);
    }

    // Método para escribir la lista de favoritos de vuelta al archivo
    private void writeFavorites(String userId, List<String> favorites) {
        File file = getFavoritesFile(userId);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            gson.toJson(favorites, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
