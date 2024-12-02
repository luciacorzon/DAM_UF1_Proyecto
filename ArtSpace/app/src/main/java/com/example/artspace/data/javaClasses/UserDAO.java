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

    // Garda nome e contrasinal
    private static final String USERS_FILE_NAME = "users.json";
    //Garda nome e hashset de favoritos
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

    /*FAVORITOS*/

    public void saveFavorite(User user, String artId) {
        // Cargar los favoritos existentes desde el archivo
        loadFavorites(user, FAVORITES_FILE_NAME);

        // Obtener el conjunto de favoritos del usuario
        HashSet<String> loadedFavorites = user.getFavorites();

        // Añadir el nuevo favorito
        loadedFavorites.add(artId);

        //Cargarlle a nova lista ao uusario
        user.setFavorites(loadedFavorites);

        // Escribir los favoritos actualizados en el archivo
       writeFavorites(user, FAVORITES_FILE_NAME);

        Log.d("USERDAO-FAV", "Guardado :" + user.getUsername() + artId);
        printUserFavorites(FAVORITES_FILE_NAME);
    }


    public void deleteFavorite(User user, String artId) {
        // Cargar los favoritos existentes desde el archivo
        loadFavorites(user, FAVORITES_FILE_NAME);

        // Obtener el conjunto de favoritos del usuario
        HashSet<String> loadedFavorites = user.getFavorites();

        // Eliminar el favorito
        loadedFavorites.remove(artId);

        // Escribir los favoritos actualizados en el archivo
        writeFavorites(user, FAVORITES_FILE_NAME);
        Log.d("USERDAO-FAV", "Borrado :" + user.getUsername() + artId);

        printUserFavorites(FAVORITES_FILE_NAME);
    }

    public void printUserFavorites(String filePath) {
        // Obtener el archivo de favoritos
        File file = new File(filePath);

        // Verificar si el archivo existe
        if (!file.exists()) {
            Log.d("USERDAO", "El archivo de favoritos no existe: " + filePath);
            return;
        }

        // Leer el contenido del archivo
        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            // Leer el contenido del archivo como un StringBuilder
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Convertir el contenido en JSON utilizando favGson para mostrar el formato
            String jsonString = jsonContent.toString();
            Log.d("USERDAO", "Contenido del archivo de favoritos en formato JSON:\n" + jsonString);

            // Si deseas deserializar y mostrar la lista de favoritos
            List<User> users = favGson.fromJson(jsonString, new TypeToken<List<User>>(){}.getType());

            // Mostrar los favoritos de cada usuario
            for (User user : users) {
                Log.d("USERDAO", "Usuario: " + user.getUsername());
                Log.d("USERDAO", "Favoritos: " + user.getFavorites());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("USERDAO", "Error al leer el archivo de favoritos: " + e.getMessage());
        }
    }




    public void loadFavorites(User user, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            // Si el archivo no existe, inicializamos los favoritos vacíos
            user.setFavorites(new HashSet<>());
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type favoritesType = new TypeToken<HashSet<String>>() {}.getType();
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



    public void writeFavorites(User user, String filePath) {
        File file = new File(filePath);
        List<User> users = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                // Usa gsonFav para deserializar los datos
                users = favGson.fromJson(reader, new TypeToken<List<User>>(){}.getType());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
        }

        // Comprobamos si el usuario ya está en la lista, si es así, actualizamos sus favoritos
        boolean userFound = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.get(i).setFavorites(user.getFavorites());
                userFound = true;
                break;
            }
        }

        // Si el usuario no estaba en la lista, lo añadimos
        if (!userFound) {
            users.add(user);
        }

        // Escribir los usuarios con los favoritos actualizados en el archivo usando gsonFav
        try (FileWriter writer = new FileWriter(filePath)) {
            // Usa favGson para serializar los datos
            favGson.toJson(users, writer);
            Log.d("FAVORITES", "Se han escrito los favoritos");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al escribir favoritos: " + e.getMessage());
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
        Log.d("userdao 1", "PRINTUSERSFROMFILE");
        List<User> users = getUsersList();
        if (users.isEmpty()) {
            Log.d("UserDAO 1.2", "No users found in the file.");
            Log.d("userdao FIN", "PRINTUSERSFROMFILE");
        } else {
            StringBuilder usersLog = new StringBuilder();
            for (User user : users) {
                usersLog.append("Username: ").append(user.getUsername())
                        .append(", Password: ").append(user.getPassword()).append("\n");

            }
            Log.d("UserDAO 1.2", "Users in file:\n" + usersLog.toString());
            Log.d("userdao FIN", "PRINTUSERSFROMFILE");
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
