package com.example.myapplicationjt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplicationjt.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private final DatabaseHelper dbHelper;

    public AppointmentDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public boolean insertAppointment(Appointment appt) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_APPT_ID, appt.getAppointmentId());
        cv.put(DatabaseHelper.COL_APPT_USER_EMAIL, appt.getOwnerEmail());
        cv.put(DatabaseHelper.COL_APPT_OWNER_NAME, appt.getOwnerName());
        cv.put(DatabaseHelper.COL_APPT_PET_NAME, appt.getPetName());
        cv.put(DatabaseHelper.COL_APPT_HOSPITAL_NAME, appt.getHospitalName());
        cv.put(DatabaseHelper.COL_APPT_DATE, appt.getDate());
        cv.put(DatabaseHelper.COL_APPT_TIME, appt.getTime());
        cv.put(DatabaseHelper.COL_APPT_STATUS, appt.getStatus());

        long result = db.insertWithOnConflict(DatabaseHelper.TABLE_APPOINTMENTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public List<Appointment> getAppointments(String email) {
        List<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = (email != null && !email.isEmpty() && !email.equals("guest"))
                ? DatabaseHelper.COL_APPT_USER_EMAIL + "=?"
                : null;
        String[] selectionArgs = (selection != null) ? new String[]{email} : null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_APPOINTMENTS, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_ID));
                String ownerEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_USER_EMAIL));
                String ownerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_OWNER_NAME));
                String petName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_PET_NAME));
                String hospName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_HOSPITAL_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_TIME));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APPT_STATUS));

                Appointment a = new Appointment(id, ownerName, ownerEmail, petName, hospName, date, time, status);
                list.add(a);
            }
            cursor.close();
        }
        return list;
    }

    public void clearAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_APPOINTMENTS, null, null);
    }
}
