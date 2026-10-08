package com.example.myapplicationjt.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Pet {
    private String id;
    private String ownerEmail;
    private String ownerName;
    private String ownerPhone;
    private String petName;
    private String petBreed;
    private String photoUri;

    public Pet() {}

    public Pet(String id, String ownerEmail, String ownerName, String ownerPhone, String petName, String petBreed, String photoUri) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.petName = petName;
        this.petBreed = petBreed;
        this.photoUri = photoUri;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getPetBreed() { return petBreed; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id != null ? id : "");
            obj.put("ownerEmail", ownerEmail != null ? ownerEmail : "");
            obj.put("ownerName", ownerName != null ? ownerName : "");
            obj.put("ownerPhone", ownerPhone != null ? ownerPhone : "");
            obj.put("petName", petName != null ? petName : "");
            obj.put("petBreed", petBreed != null ? petBreed : "");
            obj.put("photoUri", photoUri != null ? photoUri : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Pet fromJson(JSONObject obj) {
        Pet p = new Pet();
        p.setId(obj.optString("id", ""));
        p.setOwnerEmail(obj.optString("ownerEmail", ""));
        p.setOwnerName(obj.optString("ownerName", obj.optString("owner", "")));
        p.setOwnerPhone(obj.optString("ownerPhone", obj.optString("phone", "")));
        p.setPetName(obj.optString("petName", ""));
        p.setPetBreed(obj.optString("petBreed", ""));
        p.setPhotoUri(obj.optString("photoUri", ""));
        return p;
    }
}
