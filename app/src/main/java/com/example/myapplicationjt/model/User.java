package com.example.myapplicationjt.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private String address;
    private final List<AbstractPet> registeredPets = new ArrayList<>();

    public User() {}

    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<AbstractPet> getRegisteredPets() {
        return registeredPets;
    }

    public void registerPet(AbstractPet pet) {
        registeredPets.add(pet);
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", userId != null ? userId : "");
            obj.put("name", name != null ? name : "");
            obj.put("email", email != null ? email : "");
            obj.put("password", password != null ? password : "");
            obj.put("address", address != null ? address : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static User fromJson(JSONObject obj) {
        User u = new User();
        u.setUserId(obj.optString("id", obj.optString("userId", "")));
        u.setName(obj.optString("name", ""));
        u.setEmail(obj.optString("email", ""));
        u.setAddress(obj.optString("address", ""));
        return u;
    }
}
