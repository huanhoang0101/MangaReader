package com.example.mangareader.Model;

import java.util.List;

public class User {
    private String UserName;
    private String Password;
    private String Name;
    private String Email;
    private String Favorites;

    public User() {
    }

    public User(String password, String email) {
        Password = password;
        Email = email;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFavorites() {
        return Favorites;
    }

    public void setFavorites(String favorites) {
        Favorites = favorites;
    }
}
