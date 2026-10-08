package com.example.myapplicationjt.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Appointment {
    private String appointmentId;
    private String ownerName;
    private String ownerEmail;
    private String petName;
    private String hospitalName;
    private String date;
    private String time;
    private String status;

    public Appointment() {}

    public Appointment(String appointmentId, String ownerName, String ownerEmail, String petName, String hospitalName, String date, String time, String status) {
        this.appointmentId = appointmentId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.petName = petName;
        this.hospitalName = hospitalName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void book() {
        this.status = "Confirmed";
    }

    public void cancel() {
        this.status = "Cancelled";
    }

    public String showDetail() {
        return "นัดหมายสำหรับ " + petName + " ที่ " + hospitalName + " วันที่: " + date + " " + time + " (สถานะ: " + status + ")";
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", appointmentId != null ? appointmentId : "");
            obj.put("userEmail", ownerEmail != null ? ownerEmail : "");
            obj.put("petName", petName != null ? petName : "");
            obj.put("hospitalName", hospitalName != null ? hospitalName : "");
            obj.put("date", date != null ? date : "");
            obj.put("time", time != null ? time : "");
            obj.put("status", status != null ? status : "Pending");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Appointment fromJson(JSONObject obj) {
        Appointment a = new Appointment();
        a.setAppointmentId(obj.optString("id", obj.optString("appointmentId", "")));
        a.setOwnerEmail(obj.optString("userEmail", obj.optString("ownerEmail", "")));
        a.setPetName(obj.optString("petName", ""));
        a.setHospitalName(obj.optString("hospitalName", ""));
        a.setDate(obj.optString("date", ""));
        a.setTime(obj.optString("time", ""));
        a.setStatus(obj.optString("status", "Pending"));
        return a;
    }
}
