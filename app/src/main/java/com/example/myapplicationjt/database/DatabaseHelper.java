package com.example.myapplicationjt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "petcare_local.db";
    public static final int DATABASE_VERSION = 1;

    // Table: Users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ADDRESS = "address";

    // Table: Pets
    public static final String TABLE_PETS = "pets";
    public static final String COL_PET_ID = "id";
    public static final String COL_PET_OWNER_EMAIL = "owner_email";
    public static final String COL_PET_OWNER_NAME = "owner_name";
    public static final String COL_PET_OWNER_PHONE = "owner_phone";
    public static final String COL_PET_NAME = "pet_name";
    public static final String COL_PET_SPECIES = "species"; // Dog, Cat, Other
    public static final String COL_PET_BREED = "breed";
    public static final String COL_PET_AGE = "age";
    public static final String COL_PET_WEIGHT = "weight";
    public static final String COL_PET_PHOTO_URI = "photo_uri";

    // Table: Appointments
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COL_APPT_ID = "id";
    public static final String COL_APPT_USER_EMAIL = "user_email";
    public static final String COL_APPT_OWNER_NAME = "owner_name";
    public static final String COL_APPT_PET_NAME = "pet_name";
    public static final String COL_APPT_HOSPITAL_NAME = "hospital_name";
    public static final String COL_APPT_DATE = "date";
    public static final String COL_APPT_TIME = "time";
    public static final String COL_APPT_STATUS = "status";

    // Table: Tracking
    public static final String TABLE_TRACKING = "tracking";
    public static final String COL_TRACK_ID = "id";
    public static final String COL_TRACK_USER_EMAIL = "user_email";
    public static final String COL_TRACK_PET_NAME = "pet_name";
    public static final String COL_TRACK_STATUS = "status";
    public static final String COL_TRACK_LAT = "latitude";
    public static final String COL_TRACK_LON = "longitude";
    public static final String COL_TRACK_NOTE = "note";
    public static final String COL_TRACK_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " TEXT PRIMARY KEY, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_ADDRESS + " TEXT" +
                ");";
        db.execSQL(createUsers);

        // Create Pets Table
        String createPets = "CREATE TABLE " + TABLE_PETS + " (" +
                COL_PET_ID + " TEXT PRIMARY KEY, " +
                COL_PET_OWNER_EMAIL + " TEXT, " +
                COL_PET_OWNER_NAME + " TEXT, " +
                COL_PET_OWNER_PHONE + " TEXT, " +
                COL_PET_NAME + " TEXT NOT NULL, " +
                COL_PET_SPECIES + " TEXT, " +
                COL_PET_BREED + " TEXT, " +
                COL_PET_AGE + " INTEGER, " +
                COL_PET_WEIGHT + " REAL, " +
                COL_PET_PHOTO_URI + " TEXT" +
                ");";
        db.execSQL(createPets);

        // Create Appointments Table
        String createAppts = "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                COL_APPT_ID + " TEXT PRIMARY KEY, " +
                COL_APPT_USER_EMAIL + " TEXT, " +
                COL_APPT_OWNER_NAME + " TEXT, " +
                COL_APPT_PET_NAME + " TEXT NOT NULL, " +
                COL_APPT_HOSPITAL_NAME + " TEXT NOT NULL, " +
                COL_APPT_DATE + " TEXT, " +
                COL_APPT_TIME + " TEXT, " +
                COL_APPT_STATUS + " TEXT" +
                ");";
        db.execSQL(createAppts);

        // Create Tracking Table
        String createTracking = "CREATE TABLE " + TABLE_TRACKING + " (" +
                COL_TRACK_ID + " TEXT PRIMARY KEY, " +
                COL_TRACK_USER_EMAIL + " TEXT, " +
                COL_TRACK_PET_NAME + " TEXT NOT NULL, " +
                COL_TRACK_STATUS + " TEXT, " +
                COL_TRACK_LAT + " REAL, " +
                COL_TRACK_LON + " REAL, " +
                COL_TRACK_NOTE + " TEXT, " +
                COL_TRACK_TIMESTAMP + " INTEGER" +
                ");";
        db.execSQL(createTracking);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKING);
        onCreate(db);
    }
}
