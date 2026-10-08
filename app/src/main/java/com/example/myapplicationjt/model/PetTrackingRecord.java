package com.example.myapplicationjt.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PetTrackingRecord {
    private String id;
    private String petName;
    private String status;
    private double latitude;
    private double longitude;
    private String note;
    private long timestamp;
    private String userEmail;

    public PetTrackingRecord() {}

    public PetTrackingRecord(String id, String userEmail, String petName, String status, double latitude, double longitude, String note, long timestamp) {
        this.id = id;
        this.userEmail = userEmail;
        this.petName = petName;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id != null ? id : "");
            obj.put("userEmail", userEmail != null ? userEmail : "");
            obj.put("petName", petName != null ? petName : "");
            obj.put("status", status != null ? status : "");
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("note", note != null ? note : "");
            obj.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static PetTrackingRecord fromJson(JSONObject obj) {
        PetTrackingRecord r = new PetTrackingRecord();
        r.setId(obj.optString("id", ""));
        r.setUserEmail(obj.optString("userEmail", ""));
        r.setPetName(obj.optString("petName", ""));
        r.setStatus(obj.optString("status", ""));
        r.setLatitude(obj.optDouble("latitude", obj.optDouble("lat", 0.0)));
        r.setLongitude(obj.optDouble("longitude", obj.optDouble("lon", 0.0)));
        r.setNote(obj.optString("note", ""));
        r.setTimestamp(obj.optLong("timestamp", 0L));
        return r;
    }
}
