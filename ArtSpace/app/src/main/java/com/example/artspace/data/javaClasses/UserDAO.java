package com.example.artspace.data.javaClasses;

import com.example.artspace.model.User;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.artspace.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;

public class UserDAO implements DAO<User> {

    private static final String USERS_FILE_NAME = "users.json";
    private Gson gson;
    private Context context;

    public UserDAO(Context context) {
        this.context = context;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private File getUsersFile() {
        return new File(context.getFilesDir(), USERS_FILE_NAME);
    }

    @Override
    public void save(User entity) {
        // Comprobamos si el usuario ya existe
        User existingUser = getByName(entity.getUsername());
        if (existingUser != null) {
            // Si el usuario existe, comprobamos si la contraseña es la misma
            if (!existingUser.getPassword().equals(entity.getPassword())) {
                // Devolvemos falso si las contraseñas no coinciden, lo manejarás en el fragmento
                return;
            }
        } else {
            // Si el usuario no existe, lo añadimos
            List<User> users = getUsersList();
            users.add(entity);
            writeUsersToFile(users);
            Log.d("UserDAO", "User saved: " + entity.getUsername());
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
        if (users.isEmpty()) {
            Log.d("UserDAO", "No users found in the file.");
        } else {
            StringBuilder usersLog = new StringBuilder();
            for (User user : users) {
                usersLog.append("Username: ").append(user.getUsername())
                        .append(", Password: ").append(user.getPassword()).append("\n");
            }
            Log.d("UserDAO", "Users in file:\n" + usersLog.toString());
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
