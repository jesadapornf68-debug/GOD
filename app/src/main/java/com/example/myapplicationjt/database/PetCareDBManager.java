package com.example.myapplicationjt.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplicationjt.model.AbstractPet;
import com.example.myapplicationjt.model.Appointment;
import com.example.myapplicationjt.model.Pet;
import com.example.myapplicationjt.model.PetTrackingRecord;
import com.example.myapplicationjt.model.User;

import java.util.List;

/**
 * Singleton OOP Database Manager encapsulating all local SQLite database operations
 */
public class PetCareDBManager {
    private static final String PREF_NAME = "PetCareSessionPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";

    private static PetCareDBManager instance;
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final UserDAO userDAO;
    private final PetDAO petDAO;
    private final AppointmentDAO appointmentDAO;
    private final TrackingDAO trackingDAO;
    private final SharedPreferences sessionPrefs;

    private PetCareDBManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DatabaseHelper(this.context);
        this.userDAO = new UserDAO(dbHelper);
        this.petDAO = new PetDAO(dbHelper);
        this.appointmentDAO = new AppointmentDAO(dbHelper);
        this.trackingDAO = new TrackingDAO(dbHelper);
        this.sessionPrefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PetCareDBManager getInstance(Context context) {
        if (instance == null) {
            instance = new PetCareDBManager(context);
        }
        return instance;
    }

    // --- Session Handling ---
    public boolean isLoggedIn() {
        return sessionPrefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn, String email) {
        sessionPrefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
                .putString(KEY_CURRENT_USER_EMAIL, email)
                .apply();
    }

    public String getCurrentUserEmail() {
        return sessionPrefs.getString(KEY_CURRENT_USER_EMAIL, "guest");
    }

    public void logout() {
        sessionPrefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
    }

    // --- Users (OOP) ---
    public boolean registerUser(User user) {
        return userDAO.insertUser(user);
    }

    public User loginUser(String email, String password) {
        User u = userDAO.login(email, password);
        if (u != null) {
            setLoggedIn(true, email);
        }
        return u;
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean saveHomeAddress(String email, String address) {
        return userDAO.updateAddress(email, address);
    }

    public String getHomeAddress(String email) {
        return userDAO.getAddress(email);
    }

    public String getUserName(String email) {
        return userDAO.getUserName(email);
    }

    // --- Pets (OOP) ---
    public boolean addPet(Pet pet) {
        return petDAO.insertPet(pet);
    }

    public List<Pet> getPets(String email) {
        return petDAO.getPetsByOwner(email);
    }

    public List<AbstractPet> getOOPPets(String email) {
        return petDAO.getOOPPets(email);
    }

    public boolean deletePet(String petId) {
        return petDAO.deletePet(petId);
    }

    // --- Appointments (OOP) ---
    public boolean addAppointment(Appointment appt) {
        return appointmentDAO.insertAppointment(appt);
    }

    public List<Appointment> getAppointments(String email) {
        return appointmentDAO.getAppointments(email);
    }

    // --- Tracking (OOP) ---
    public boolean addTrackingRecord(PetTrackingRecord record) {
        return trackingDAO.insertRecord(record);
    }

    public List<PetTrackingRecord> getTrackingRecords(String email) {
        return trackingDAO.getRecords(email);
    }

    public boolean deleteTrackingRecord(String id) {
        return trackingDAO.deleteRecord(id);
    }

    // --- Reset ---
    public void resetAllData() {
        userDAO.clearAll();
        petDAO.clearAll();
        appointmentDAO.clearAll();
        trackingDAO.clearAll();
        sessionPrefs.edit().clear().apply();
    }
}
