package com.example.myapplicationjt;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationjt.database.PetCareDBManager;
import com.example.myapplicationjt.model.AbstractPet;
import com.example.myapplicationjt.model.Appointment;
import com.example.myapplicationjt.model.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class DataRecordsActivity extends AppCompatActivity {

    private PetCareDBManager dbManager;
    private TextView tvAllRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_records);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.topToolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbManager = PetCareDBManager.getInstance(this);
        tvAllRecords = findViewById(R.id.tvAllRecords);

        loadAndDisplayRecords();
    }

    private void loadAndDisplayRecords() {
        StringBuilder sb = new StringBuilder();

        // 1. Read Home Address
        String email = dbManager.getCurrentUserEmail();
        String homeAddress = dbManager.getHomeAddress(email);
        if (homeAddress == null || homeAddress.isEmpty()) {
            homeAddress = "ยังไม่มีการบันทึก";
        }
        sb.append("🏠 ที่อยู่บ้านที่บันทึก:\n").append(homeAddress).append("\n\n");

        // 2. Read Registered Users (OOP User)
        List<User> users = dbManager.getAllUsers();
        sb.append("👥 บัญชีผู้ใช้ในระบบฐานข้อมูล (OOP User):\n");
        if (users.isEmpty()) {
            sb.append("- ยังไม่มีบัญชีผู้ใช้ในระบบ\n");
        } else {
            for (User u : users) {
                sb.append("• ชื่อ: ").append(u.getName())
                        .append(" (อีเมล: ").append(u.getEmail()).append(")\n");
            }
        }
        sb.append("\n");

        // 3. Read Pets (OOP AbstractPet / Dog / Cat Polymorphism)
        List<AbstractPet> oopPets = dbManager.getOOPPets("");
        sb.append("🐾 ข้อมูลสัตว์เลี้ยง (OOP Polymorphism - Dog / Cat):\n");
        if (oopPets.isEmpty()) {
            sb.append("- ยังไม่มีข้อมูลสัตว์เลี้ยง\n");
        } else {
            for (AbstractPet p : oopPets) {
                // Invoking polymorphic method getInfo()
                sb.append("• ").append(p.getInfo()).append("\n");
            }
        }
        sb.append("\n");

        // 4. Read Appointments (OOP Appointment)
        List<Appointment> appts = dbManager.getAppointments("");
        sb.append("📅 การนัดหมาย (OOP Appointment):\n");
        if (appts.isEmpty()) {
            sb.append("- ยังไม่มีประวัติการนัดหมาย\n");
        } else {
            for (Appointment a : appts) {
                sb.append("• ").append(a.showDetail()).append("\n");
            }
        }

        if (tvAllRecords != null) {
            tvAllRecords.setText(sb.toString().trim());
        }
    }
}
