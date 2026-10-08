package com.example.myapplicationjt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplicationjt.model.PetTrackingRecord;

import java.util.ArrayList;
import java.util.List;

public class TrackingDAO {
    private final DatabaseHelper dbHelper;

    public TrackingDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public boolean insertRecord(PetTrackingRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_TRACK_ID, record.getId());
        cv.put(DatabaseHelper.COL_TRACK_USER_EMAIL, record.getUserEmail());
        cv.put(DatabaseHelper.COL_TRACK_PET_NAME, record.getPetName());
        cv.put(DatabaseHelper.COL_TRACK_STATUS, record.getStatus());
        cv.put(DatabaseHelper.COL_TRACK_LAT, record.getLatitude());
        cv.put(DatabaseHelper.COL_TRACK_LON, record.getLongitude());
        cv.put(DatabaseHelper.COL_TRACK_NOTE, record.getNote());
        cv.put(DatabaseHelper.COL_TRACK_TIMESTAMP, record.getTimestamp());

        long result = db.insertWithOnConflict(DatabaseHelper.TABLE_TRACKING, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public List<PetTrackingRecord> getRecords(String email) {
        List<PetTrackingRecord> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = (email != null && !email.isEmpty() && !email.equals("guest"))
                ? DatabaseHelper.COL_TRACK_USER_EMAIL + "=?"
                : null;
        String[] selectionArgs = (selection != null) ? new String[]{email} : null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_TRACKING, null, selection, selectionArgs, null, null, DatabaseHelper.COL_TRACK_TIMESTAMP + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_ID));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_USER_EMAIL));
                String petName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_PET_NAME));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_STATUS));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_LAT));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_LON));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_NOTE));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRACK_TIMESTAMP));

                PetTrackingRecord r = new PetTrackingRecord(id, userEmail, petName, status, lat, lon, note, timestamp);
                list.add(r);
            }
            cursor.close();
        }
        return list;
    }

    public boolean deleteRecord(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_TRACKING, DatabaseHelper.COL_TRACK_ID + "=?", new String[]{id});
        return rows > 0;
    }

    public void clearAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_TRACKING, null, null);
    }
}
