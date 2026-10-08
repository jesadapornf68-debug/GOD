package com.example.myapplicationjt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationjt.database.PetCareDBManager;
import com.example.myapplicationjt.model.AbstractPet;
import com.example.myapplicationjt.model.Cat;
import com.example.myapplicationjt.model.Dog;
import com.example.myapplicationjt.model.Pet;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private Uri selectedImageUri = null;
    private ImageView ivPetPhoto;
    private LinearLayout savedCardsContainer;
    private TextInputEditText etName;
    private TextInputEditText etPhone;
    private TextInputEditText etPetName;
    private TextInputEditText etPetBreed;

    private PetCareDBManager dbManager;
    private SharedPreferences prefs;

    private final ActivityResultLauncher<String[]> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception ignored) {
                    }
                    if (ivPetPhoto != null) {
                        ivPetPhoto.setImageURI(uri);
                    }
                    Toast.makeText(this, "เลือกรูปภาพสัตว์เลี้ยงสำเร็จ", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.topToolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbManager = PetCareDBManager.getInstance(this);
        prefs = getSharedPreferences("UserProfilePrefs", Context.MODE_PRIVATE);

        etName = findViewById(R.id.etProfileName);
        etPhone = findViewById(R.id.etProfilePhone);
        etPetName = findViewById(R.id.etProfilePetName);
        etPetBreed = findViewById(R.id.etProfilePetBreed);
        ivPetPhoto = findViewById(R.id.ivPetPhoto);
        savedCardsContainer = findViewById(R.id.savedCardsContainer);

        MaterialCardView cardPetPhoto = findViewById(R.id.cardPetPhoto);
        if (cardPetPhoto != null) {
            cardPetPhoto.setOnClickListener(v -> pickImageLauncher.launch(new String[]{"image/*"}));
        }

        MaterialButton btnSaveProfile = findViewById(R.id.btnSaveProfile);
        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> saveCurrentPet(false));
        }

        MaterialButton btnAddAnotherPet = findViewById(R.id.btnAddAnotherPet);
        if (btnAddAnotherPet != null) {
            btnAddAnotherPet.setOnClickListener(v -> saveCurrentPet(true));
        }

        loadLastForm();
        renderSavedCards();
    }

    private void saveCurrentPet(boolean clearAfterSave) {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String petName = etPetName.getText() != null ? etPetName.getText().toString().trim() : "";
        String petBreed = etPetBreed.getText() != null ? etPetBreed.getText().toString().trim() : "";

        if (name.isEmpty()) {
            etName.setError("กรุณากรอกชื่อเจ้าของ");
            etName.requestFocus();
            return;
        }
        if (petName.isEmpty()) {
            etPetName.setError("กรุณากรอกชื่อสัตว์เลี้ยง");
            etPetName.requestFocus();
            return;
        }

        String userEmail = dbManager.getCurrentUserEmail();

        // Demonstrating OOP Inheritance & Polymorphism
        AbstractPet oopPet;
        if (petBreed.toLowerCase().contains("cat") || petBreed.contains("แมว")) {
            oopPet = new Cat(petName, 1, 3.5, petBreed);
        } else {
            oopPet = new Dog(petName, 1, 5.0, petBreed);
        }

        // Create Pet Model and insert into Local SQLite Database
        Pet pet = new Pet(
                UUID.randomUUID().toString(),
                userEmail,
                name,
                phone,
                petName,
                petBreed,
                selectedImageUri != null ? selectedImageUri.toString() : ""
        );

        dbManager.addPet(pet);
        Toast.makeText(this, "บันทึกข้อมูลเรียบร้อยแล้ว (" + oopPet.getSpecies() + ")", Toast.LENGTH_SHORT).show();

        prefs.edit()
                .putString("profile_name", name)
                .putString("profile_phone", phone)
                .apply();

        renderSavedCards();

        if (clearAfterSave) {
            if (etPetName.getText() != null) etPetName.getText().clear();
            if (etPetBreed.getText() != null) etPetBreed.getText().clear();
            if (ivPetPhoto != null) ivPetPhoto.setImageResource(R.drawable.ic_pet_logo);
            selectedImageUri = null;
        }
    }

    private void deleteRecord(Pet pet) {
        new AlertDialog.Builder(this)
                .setTitle("ลบข้อมูลนี้?")
                .setMessage("ข้อมูลสัตว์เลี้ยง " + pet.getPetName() + " จะถูกลบออกจากฐานข้อมูลเครื่อง")
                .setNegativeButton("ยกเลิก", null)
                .setPositiveButton("ลบ", (dialog, which) -> {
                    dbManager.deletePet(pet.getId());
                    Toast.makeText(this, "ลบข้อมูลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                    renderSavedCards();
                })
                .show();
    }

    private void renderSavedCards() {
        if (savedCardsContainer == null) return;
        savedCardsContainer.removeAllViews();

        String email = dbManager.getCurrentUserEmail();
        List<Pet> pets = dbManager.getPets(email);

        if (pets.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("ยังไม่มีข้อมูลที่บันทึก\nกด “บันทึกข้อมูล” ด้านล่างเพื่อเพิ่มรายการแรก");
            empty.setTextSize(14f);
            empty.setTextColor(Color.DKGRAY);
            empty.setPadding(8, 8, 8, 8);
            savedCardsContainer.addView(empty);
            return;
        }

        for (Pet p : pets) {
            MaterialCardView card = (MaterialCardView) LayoutInflater.from(this)
                    .inflate(R.layout.item_saved_pet, savedCardsContainer, false);

            ImageView iv = card.findViewById(R.id.ivSavedPet);
            TextView owner = card.findViewById(R.id.tvSavedOwner);
            TextView phone = card.findViewById(R.id.tvSavedPhone);
            TextView pet = card.findViewById(R.id.tvSavedPet);
            TextView details = card.findViewById(R.id.tvSavedDetails);
            MaterialButton edit = card.findViewById(R.id.btnEditSaved);
            MaterialButton delete = card.findViewById(R.id.btnDeleteSaved);

            owner.setText(p.getOwnerName() != null && !p.getOwnerName().isEmpty() ? p.getOwnerName() : "-");
            phone.setText(p.getOwnerPhone() != null && !p.getOwnerPhone().isEmpty() ? ("📞 " + p.getOwnerPhone()) : "ไม่ได้ระบุเบอร์โทรศัพท์");
            pet.setText("🐾 " + (p.getPetName() != null ? p.getPetName() : "-"));
            details.setText(p.getPetBreed() != null && !p.getPetBreed().isEmpty() ? p.getPetBreed() : "ยังไม่ได้ระบุสายพันธุ์ / อายุ / น้ำหนัก");

            String photo = p.getPhotoUri();
            if (photo != null && !photo.isEmpty()) {
                try {
                    iv.setImageURI(Uri.parse(photo));
                } catch (Exception ignored) {
                    iv.setImageResource(R.drawable.ic_pet_logo);
                }
            } else {
                iv.setImageResource(R.drawable.ic_pet_logo);
            }

            edit.setOnClickListener(v -> {
                etName.setText(p.getOwnerName());
                etPhone.setText(p.getOwnerPhone());
                etPetName.setText(p.getPetName());
                etPetBreed.setText(p.getPetBreed());
                if (p.getPhotoUri() != null && !p.getPhotoUri().isEmpty()) {
                    selectedImageUri = Uri.parse(p.getPhotoUri());
                    ivPetPhoto.setImageURI(selectedImageUri);
                }
                dbManager.deletePet(p.getId());
                renderSavedCards();
                Toast.makeText(this, "แก้ไขข้อมูลได้เลย แล้วกดบันทึกอีกครั้ง", Toast.LENGTH_SHORT).show();
            });

            delete.setOnClickListener(v -> deleteRecord(p));

            savedCardsContainer.addView(card);
        }
    }

    private void loadLastForm() {
        if (etName != null) etName.setText(prefs.getString("profile_name", ""));
        if (etPhone != null) etPhone.setText(prefs.getString("profile_phone", ""));
    }
}
