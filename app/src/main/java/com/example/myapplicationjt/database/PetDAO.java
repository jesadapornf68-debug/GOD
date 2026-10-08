package com.example.myapplicationjt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplicationjt.model.AbstractPet;
import com.example.myapplicationjt.model.Cat;
import com.example.myapplicationjt.model.Dog;
import com.example.myapplicationjt.model.Pet;

import java.util.ArrayList;
import java.util.List;

public class PetDAO {
    private final DatabaseHelper dbHelper;

    public PetDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public boolean insertPet(Pet pet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_PET_ID, pet.getId());
        cv.put(DatabaseHelper.COL_PET_OWNER_EMAIL, pet.getOwnerEmail());
        cv.put(DatabaseHelper.COL_PET_OWNER_NAME, pet.getOwnerName());
        cv.put(DatabaseHelper.COL_PET_OWNER_PHONE, pet.getOwnerPhone());
        cv.put(DatabaseHelper.COL_PET_NAME, pet.getPetName());
        cv.put(DatabaseHelper.COL_PET_BREED, pet.getPetBreed());
        cv.put(DatabaseHelper.COL_PET_PHOTO_URI, pet.getPhotoUri());

        // Determine species (Dog or Cat) for OOP representation
        String breedLower = pet.getPetBreed() != null ? pet.getPetBreed().toLowerCase() : "";
        if (breedLower.contains("cat") || breedLower.contains("แมว")) {
            cv.put(DatabaseHelper.COL_PET_SPECIES, "Cat");
        } else {
            cv.put(DatabaseHelper.COL_PET_SPECIES, "Dog");
        }

        long result = db.insertWithOnConflict(DatabaseHelper.TABLE_PETS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public List<Pet> getPetsByOwner(String email) {
        List<Pet> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = (email != null && !email.isEmpty() && !email.equals("guest"))
                ? DatabaseHelper.COL_PET_OWNER_EMAIL + "=?"
                : null;
        String[] selectionArgs = (selection != null) ? new String[]{email} : null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_PETS, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PET_ID));
                String ownerEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PET_OWNER_EMAIL));
                String ownerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PET_OWNER_NAME));
                String ownerPhone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PET_OWNER_PHONE));
                String petName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PET_NAME));
                String petBreed = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PET_BREED));
                String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PET_PHOTO_URI));

                Pet p = new Pet(id, ownerEmail, ownerName, ownerPhone, petName, petBreed, photoUri);
                list.add(p);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * Demonstrating OOP Polymorphism: instantiating Dog and Cat subclasses of AbstractPet
     */
    public List<AbstractPet> getOOPPets(String email) {
        List<AbstractPet> list = new ArrayList<>();
        List<Pet> pets = getPetsByOwner(email);
        for (Pet p : pets) {
            String breed = p.getPetBreed() != null ? p.getPetBreed() : "";
            if (breed.toLowerCase().contains("cat") || breed.contains("แมว")) {
                list.add(new Cat(p.getId(), p.getPetName(), 1, 3.5, breed));
            } else {
                list.add(new Dog(p.getId(), p.getPetName(), 1, 5.0, breed));
            }
        }
        return list;
    }

    public boolean deletePet(String petId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_PETS, DatabaseHelper.COL_PET_ID + "=?", new String[]{petId});
        return rows > 0;
    }

    public void clearAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PETS, null, null);
    }
}
