package com.example.artspace.data.javaClasses;

import com.example.artspace.model.User;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.artspace.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class UserDAO implements DAO<User> {

    private static final String USERS_FILE_NAME = "users.json";
    private static final String FAVORITES_FILE_NAME = "favorites.json";
    private Gson gson;

    private Gson favGson;
    private Context context;

    public UserDAO(Context context) {
        this.context = context;
        gson = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
                .registerTypeAdapter(User.class, new UserSerializer()).setPrettyPrinting().create();
        favGson = new GsonBuilder()
                .registerTypeAdapter(User.class, new FavoritesDeserializer())
                .registerTypeAdapter(User.class, new FavoritesSerializer())
                .setPrettyPrinting()
                .create();
    }

    private File getUsersFile() {
        return new File(context.getFilesDir(), USERS_FILE_NAME);
    }

    public File getFavoritesFile() {
        return new File(context.getFilesDir(), FAVORITES_FILE_NAME);
    }

    public User getUser(String username) {
        List<User> users = getUsersList();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void save(User entity) {
        User existingUser = getByName(entity.getUsername());
        if (existingUser != null) {
            if (!existingUser.getPassword().equals(entity.getPassword())) {
                return;
            }
        } else {
            List<User> users = getUsersList();
            users.add(entity);
            writeUsersToFile(users);
            Log.d("UserDAO", "User saved: " + entity.getUsername());
        }
    }


    public void saveFavorite(User user, String artId) {
        File file = getFavoritesFile();

        loadFavorites(user, file);

        HashSet<String> loadedFavorites = user.getFavorites();

        loadedFavorites.add(artId);

        user.setFavorites(loadedFavorites);

        writeFavorites(user, file);

        Log.d("USERDAO-FAV", "Guardado :" + user.getUsername() + artId);
        printUserFavorites(file);
    }


    public void deleteFavorite(User user, String artId) {
        File file = getFavoritesFile();

        loadFavorites(user, file);

        HashSet<String> loadedFavorites = user.getFavorites();
        if (loadedFavorites.contains(artId)) {
            var removed = loadedFavorites.remove(artId);
            user.setFavorites(loadedFavorites);
            writeFavoritesDeletion(user, file);
        }
    }



    public void printUserFavorites(File file) {

        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            String jsonString = jsonContent.toString();

            List<User> users = favGson.fromJson(jsonString, new TypeToken<List<User>>() {
            }.getType());

            for (User user : users) {
                Log.d("printFileUsers", "Usuario: " + user.getUsername());
                Log.d("printFileUsers", "Favoritos: " + user.getFavorites());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("USERDAO", "Error al leer el archivo de favoritos: " + e.getMessage());
        }
    }


    public void loadFavorites(User user, File file) {
        if (!file.exists()) {
            user.setFavorites(new HashSet<>());
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type favoritesType = new TypeToken<HashSet<String>>() {
            }.getType();
            HashSet<String> favorites = favGson.fromJson(reader, favoritesType);

            if (favorites == null) {
                favorites = new HashSet<>();
            }
            user.setFavorites(favorites);

        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            System.err.println("Error al cargar favoritos: " + e.getMessage());
        }
    }

    public HashSet<String> returnFavoritesForUser(File file, String username) {

        if (!file.exists()) {
            return new HashSet<>();
        }

        try (FileReader reader = new FileReader(file)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(User.class, new FavoritesDeserializer());
            Gson gson = gsonBuilder.create();

            Type userListType = new TypeToken<List<User>>() {
            }.getType();
            List<User> users = gson.fromJson(reader, userListType);

            if (users == null || users.isEmpty()) {
                return new HashSet<>();
            }

            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    HashSet<String> favorites = user.getFavorites();
                    return favorites;
                }
            }

            return new HashSet<>();
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }


    public void writeFavorites(User user, File file) {
        List<User> users = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                users = favGson.fromJson(reader, new TypeToken<List<User>>() {
                }.getType());
                if (users == null) {
                    users = new ArrayList<>();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FAVORITES", "Error al leer el archivo: " + e.getMessage());
            }
        }

        boolean userFound = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                HashSet<String> combinedFavorites = new HashSet<>(users.get(i).getFavorites());
                combinedFavorites.addAll(user.getFavorites());
                users.get(i).setFavorites(combinedFavorites);
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            users.add(user);
        }

        try (FileWriter writer = new FileWriter(file)) {
            favGson.toJson(users, writer);
            Log.d("UserDao", "Favoritos actualizados correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FAVORITES", "Error al escribir favoritos: " + e.getMessage());
        }
    }


    public void writeFavoritesDeletion(User user, File file) {
        List<User> users = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                users = favGson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
                if (users == null) {
                    users = new ArrayList<>();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FAVORITES", "Error al leer el archivo de favoritos: " + e.getMessage());
            }
        }

        boolean userFound = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.get(i).setFavorites(user.getFavorites());
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            users.add(user);
        }

        try (FileWriter writer = new FileWriter(file)) {
            favGson.toJson(users, writer);
            Log.d("UserDAO", "Favoritos actualizados correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FAVORITES", "Error al escribir los favoritos: " + e.getMessage());
        }
    }


    @Override
    public User getById(int id) {
        List<User> users = getUsersList();
        for (User user : users) {
            if (user.getUsername().equals(String.valueOf(id))) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void delete(int id) {
        List<User> users = getUsersList();
        User userToDelete = null;

        for (User user : users) {
            if (user.getUsername().equals(String.valueOf(id))) {
                userToDelete = user;
                break;
            }
        }

        if (userToDelete != null) {
            users.remove(userToDelete);
            writeUsersToFile(users);
            Log.d("UserDAO", "User deleted: " + userToDelete.getUsername());
        } else {
            Log.d("UserDAO", "User not found to delete.");
        }
    }

    public void deleteByName(String username) {
        List<User> users = getUsersList();
        User userToDelete = null;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                userToDelete = user;
                break;
            }
        }

        if (userToDelete != null) {
            users.remove(userToDelete);
            writeUsersToFile(users);
            Log.d("UserDAO", "User deleted: " + userToDelete.getUsername());
        } else {
            Log.d("UserDAO", "User not found to delete.");
        }
    }

    @Override
    public void update(User entity) {
        List<User> users = getUsersList();
        boolean userFound = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(entity.getUsername())) {
                users.set(i, entity);
                userFound = true;
                break;
            }
        }

        if (userFound) {
            writeUsersToFile(users);
            Log.d("UserDAO", "User updated: " + entity.getUsername());
        } else {
            Log.d("UserDAO", "User not found to update.");
        }
    }

    public List<User> getUsersList() {
        File file = getUsersFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            User[] usersArray = gson.fromJson(jsonContent.toString(), User[].class);
            return new ArrayList<>(Arrays.asList(usersArray));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void writeUsersToFile(List<User> users) {
        String jsonContent = gson.toJson(users);
        File file = getUsersFile();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printUsersFromFile() {
        List<User> users = getUsersList();
        if (!users.isEmpty()) {
            StringBuilder usersLog = new StringBuilder();
            for (User user : users) {
                usersLog.append("Username: ").append(user.getUsername())
                        .append(", Password: ").append(user.getPassword()).append("\n");

            }
        }
    }

    public void printFavoritesFromFile() {
        File file = getFavoritesFile();

        if (!file.exists()) {
            Log.d("printFavoritesFromFile", "El archivo de favoritos no existe.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            List<User> users = favGson.fromJson(jsonContent.toString(), new TypeToken<List<User>>() {
            }.getType());

            if (users == null || users.isEmpty()) {
                Log.d("printFavoritesFromFile", "No hay usuarios en el archivo de favoritos.");
                return;
            }

            for (User user : users) {
                Log.d("printFavoritesFromFile", "Usuario: " + user.getUsername());
                Log.d("printFavoritesFromFile", "Favoritos: " + user.getFavorites());
            }

        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            Log.e("printFavoritesFromFile", "Error al leer el archivo de favoritos: " + e.getMessage());
        }
    }


    public User getByName(String username) {
        List<User> users = getUsersList();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
