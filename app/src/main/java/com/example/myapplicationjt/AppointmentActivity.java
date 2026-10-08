package com.example.myapplicationjt;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationjt.database.PetCareDBManager;
import com.example.myapplicationjt.model.Appointment;
import com.example.myapplicationjt.model.Pet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppointmentActivity extends AppCompatActivity {

    private PetCareDBManager dbManager;
    private String selectedPetName = "Sania";
    private String selectedHospital = "โรงพยาบาลสัตว์พิษณุโลก";
    private String selectedService = "ตรวจทั่วไป";
    private String selectedDate = "10 ต.ค. 2026";
    private String selectedTime = "10:30";

    private TextView tvPetName;
    private TextView tvHospitalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = PetCareDBManager.getInstance(this);

        // 1. Back Button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvPetName = findViewById(R.id.tvPetName);
        tvHospitalName = findViewById(R.id.tvHospitalName);

        // Load First Pet from Database if available
        String email = dbManager.getCurrentUserEmail();
        List<Pet> pets = dbManager.getPets(email);
        if (pets != null && !pets.isEmpty()) {
            selectedPetName = pets.get(0).getPetName();
            if (tvPetName != null) {
                tvPetName.setText(selectedPetName);
            }
        }

        // 2. Pet Selection Card Click Listener
        MaterialCardView cardSelectPet = findViewById(R.id.cardSelectPet);
        if (cardSelectPet != null) {
            cardSelectPet.setOnClickListener(v -> showPetSelectionDialog(pets));
        }

        // 3. Hospital Selection Card Click Listener
        MaterialCardView cardSelectHospital = findViewById(R.id.cardSelectHospital);
        if (cardSelectHospital != null) {
            cardSelectHospital.setOnClickListener(v -> showHospitalSelectionDialog());
        }

        // 4. Setup Service Clicks
        setupServiceSelection();

        // 5. Setup Time Slot Clicks
        setupTimeSelection();

        // 6. Confirm Booking Button
        MaterialButton btnBook = findViewById(R.id.btnBook);
        EditText etNote = findViewById(R.id.etNote);

        if (btnBook != null) {
            btnBook.setOnClickListener(v -> {
                String note = etNote != null ? etNote.getText().toString().trim() : "";

                Appointment appt = new Appointment(
                        UUID.randomUUID().toString(),
                        email,
                        email,
                        selectedPetName,
                        selectedHospital,
                        selectedDate + " (" + selectedService + ")",
                        selectedTime,
                        note.isEmpty() ? "Confirmed" : note
                );
                appt.book();

                dbManager.addAppointment(appt);
                Toast.makeText(this, "บันทึกการนัดหมายสัตว์เลี้ยง (" + selectedPetName + ") เรียบร้อยแล้ว!", Toast.LENGTH_LONG).show();
                finish();
            });
        }
    }

    private void showPetSelectionDialog(List<Pet> pets) {
        List<String> petNames = new ArrayList<>();
        if (pets != null && !pets.isEmpty()) {
            for (Pet p : pets) {
                petNames.add(p.getPetName());
            }
        } else {
            petNames.add("Sania");
            petNames.add("Milo");
            petNames.add("Coco");
        }

        String[] nameArray = petNames.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("เลือกสัตว์เลี้ยง");
        builder.setItems(nameArray, (dialog, which) -> {
            selectedPetName = nameArray[which];
            if (tvPetName != null) {
                tvPetName.setText(selectedPetName);
            }
        });
        builder.show();
    }

    private void showHospitalSelectionDialog() {
        String[] hospitals = new String[]{
                "โรงพยาบาลสัตว์พิษณุโลก",
                "โรงพยาบาลสัตว์มหาวิทยาลัยนเรศวร",
                "โรงพยาบาลสัตว์เอกอารักษ์ พิษณุโลก",
                "คลินิกรักษาสัตว์ แอนิมอลแลนด์ พิษณุโลก"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("เลือกโรงพยาบาล");
        builder.setItems(hospitals, (dialog, which) -> {
            selectedHospital = hospitals[which];
            if (tvHospitalName != null) {
                tvHospitalName.setText(selectedHospital);
            }
        });
        builder.show();
    }

    private void setupServiceSelection() {
        int[] serviceIds = new int[]{
                R.id.svcCheckup, R.id.svcVaccine, R.id.svcBlood, R.id.svcSurgery, R.id.svcOther
        };
        String[] serviceNames = new String[]{
                "ตรวจทั่วไป", "ฉีดวัคซีน", "ตรวจเลือด", "ผ่าตัด", "อื่นๆ"
        };

        for (int i = 0; i < serviceIds.length; i++) {
            final int index = i;
            LinearLayout layout = findViewById(serviceIds[i]);
            if (layout != null) {
                layout.setOnClickListener(v -> {
                    selectedService = serviceNames[index];
                    Toast.makeText(this, "เลือกบริการ: " + selectedService, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void setupTimeSelection() {
        int[] timeIds = new int[]{
                R.id.time900, R.id.time930, R.id.time1000, R.id.time1030,
                R.id.time1100, R.id.time1300, R.id.time1400, R.id.time1500
        };

        for (int id : timeIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setOnClickListener(v -> {
                    selectedTime = tv.getText().toString();
                    Toast.makeText(this, "เลือกเวลานัดหมาย: " + selectedTime, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}