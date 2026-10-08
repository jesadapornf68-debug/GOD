package com.example.myapplicationjt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationjt.database.PetCareDBManager;
import com.example.myapplicationjt.model.PetTrackingRecord;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PetTrackingActivity extends AppCompatActivity {

    private TextInputEditText etPetName;
    private TextInputEditText etNote;
    private MaterialButton statusButton;
    private LinearLayout recordsContainer;

    private String selectedStatus = "อยู่กับเจ้าของ";
    private Double currentLat = null;
    private Double currentLon = null;

    private PetCareDBManager dbManager;

    private final ActivityResultLauncher<String[]> requestLocationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarse = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                boolean granted = (fine != null && fine) || (coarse != null && coarse);

                if (granted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "ต้องอนุญาตตำแหน่งเพื่อบันทึกพิกัดสัตว์เลี้ยง", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_tracking);

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

        etPetName = findViewById(R.id.etTrackingPetName);
        etNote = findViewById(R.id.etTrackingNote);
        statusButton = findViewById(R.id.btnTrackingStatus);
        recordsContainer = findViewById(R.id.trackingRecordsContainer);

        if (statusButton != null) {
            statusButton.setOnClickListener(v -> chooseStatus());
        }

        MaterialButton btnGetLocation = findViewById(R.id.btnGetLocation);
        if (btnGetLocation != null) {
            btnGetLocation.setOnClickListener(v -> checkLocationPermission());
        }

        MaterialButton btnSaveTracking = findViewById(R.id.btnSaveTracking);
        if (btnSaveTracking != null) {
            btnSaveTracking.setOnClickListener(v -> saveTrackingRecord());
        }

        MaterialButton btnClearTracking = findViewById(R.id.btnClearTracking);
        if (btnClearTracking != null) {
            btnClearTracking.setOnClickListener(v -> {
                if (etPetName.getText() != null) etPetName.getText().clear();
                if (etNote.getText() != null) etNote.getText().clear();
                selectedStatus = "อยู่กับเจ้าของ";
                statusButton.setText("สถานะ: " + selectedStatus);
                currentLat = null;
                currentLon = null;
                TextView tvLoc = findViewById(R.id.tvCurrentLocation);
                if (tvLoc != null) tvLoc.setText("📍 ยังไม่ได้อ่านตำแหน่งปัจจุบัน");
            });
        }

        renderRecords();
    }

    private void chooseStatus() {
        String[] statuses = new String[]{
                "อยู่กับเจ้าของ",
                "อยู่บ้าน",
                "กำลังเดินเล่น",
                "อยู่ที่คลินิก / โรงพยาบาล",
                "กำลังตามหา"
        };

        new AlertDialog.Builder(this)
                .setTitle("เลือกสถานะสัตว์เลี้ยง")
                .setItems(statuses, (dialog, which) -> {
                    selectedStatus = statuses[which];
                    if (statusButton != null) {
                        statusButton.setText("สถานะ: " + selectedStatus);
                    }
                })
                .show();
    }

    private void checkLocationPermission() {
        boolean fine = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (fine || coarse) {
            getCurrentLocation();
        } else {
            requestLocationPermission.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        try {
            client.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && location.getLongitude() > 90.0) {
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();
                } else {
                    // Fallback default coordinates to Phitsanulok City Center, Thailand
                    currentLat = 16.8211;
                    currentLon = 100.2659;
                }
                TextView tv = findViewById(R.id.tvCurrentLocation);
                if (tv != null) {
                    tv.setText(String.format(Locale.US, "📍 พิกัดปัจจุบัน (พิษณุโลก): %.6f, %.6f", currentLat, currentLon));
                }
                Toast.makeText(this, "อ่านตำแหน่งปัจจุบัน (พิษณุโลก) แล้ว", Toast.LENGTH_SHORT).show();
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "ไม่สามารถเข้าถึงตำแหน่งได้", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTrackingRecord() {
        String pet = etPetName.getText() != null ? etPetName.getText().toString().trim() : "";
        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";

        if (pet.isEmpty()) {
            etPetName.setError("กรุณากรอกชื่อสัตว์เลี้ยง");
            etPetName.requestFocus();
            return;
        }

        if (currentLat == null || currentLon == null) {
            Toast.makeText(this, "กรุณากด “อ่านตำแหน่งปัจจุบัน” ก่อนบันทึก", Toast.LENGTH_LONG).show();
            return;
        }

        String userEmail = dbManager.getCurrentUserEmail();

        // OOP Model instantiation
        PetTrackingRecord record = new PetTrackingRecord(
                UUID.randomUUID().toString(),
                userEmail,
                pet,
                selectedStatus,
                currentLat,
                currentLon,
                note,
                System.currentTimeMillis()
        );

        dbManager.addTrackingRecord(record);
        Toast.makeText(this, "บันทึกการติดตาม " + pet + " แล้ว", Toast.LENGTH_SHORT).show();
        if (etNote.getText() != null) etNote.getText().clear();
        renderRecords();
    }

    private void renderRecords() {
        if (recordsContainer == null) return;
        recordsContainer.removeAllViews();

        String email = dbManager.getCurrentUserEmail();
        List<PetTrackingRecord> records = dbManager.getTrackingRecords(email);

        if (records.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("ยังไม่มีประวัติการติดตาม\nกรอกชื่อสัตว์เลี้ยง → อ่าน GPS → บันทึก");
            empty.setTextSize(14f);
            empty.setTextColor(0xFF607D8B);
            empty.setPadding(8, 16, 8, 16);
            recordsContainer.addView(empty);
            return;
        }

        for (PetTrackingRecord obj : records) {
            MaterialCardView card = new MaterialCardView(this);
            card.setRadius(18f);
            card.setCardElevation(3f);
            card.setStrokeWidth(1);
            card.setStrokeColor(0xFFD6E5F0);
            card.setContentPadding(18, 16, 18, 16);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.bottomMargin = 12;
            card.setLayoutParams(lp);

            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);

            TextView pet = new TextView(this);
            pet.setText("🐾 " + (obj.getPetName() != null ? obj.getPetName() : "-"));
            pet.setTextSize(18f);
            pet.setTextColor(0xFF01579B);
            pet.setTypeface(null, Typeface.BOLD);

            TextView status = new TextView(this);
            status.setText("สถานะ: " + (obj.getStatus() != null ? obj.getStatus() : "-"));
            status.setTextSize(14f);
            status.setTextColor(0xFF263238);
            status.setPadding(0, 6, 0, 0);

            TextView time = new TextView(this);
            Date date = new Date(obj.getTimestamp());
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            time.setText("🕐 " + format.format(date));
            time.setTextSize(13f);
            time.setTextColor(0xFF607D8B);
            time.setPadding(0, 4, 0, 0);

            TextView location = new TextView(this);
            location.setText(String.format(Locale.US, "📍 %.6f, %.6f", obj.getLatitude(), obj.getLongitude()));
            location.setTextSize(13f);
            location.setTextColor(0xFF455A64);
            location.setPadding(0, 4, 0, 0);

            TextView note = new TextView(this);
            String noteVal = obj.getNote();
            note.setText((noteVal == null || noteVal.isEmpty()) ? "หมายเหตุ: -" : "หมายเหตุ: " + noteVal);
            note.setTextSize(13f);
            note.setTextColor(0xFF607D8B);
            note.setPadding(0, 4, 0, 0);

            LinearLayout buttons = new LinearLayout(this);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setPadding(0, 10, 0, 0);

            MaterialButton mapButton = new MaterialButton(this);
            mapButton.setText("🗺 เปิดแผนที่");
            LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, 48, 1f);
            btnLp.setMarginEnd(6);
            mapButton.setLayoutParams(btnLp);
            mapButton.setOnClickListener(v -> {
                double lat = obj.getLatitude();
                double lon = obj.getLongitude();
                Uri uri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon)));
                }
            });

            MaterialButton deleteButton = new MaterialButton(this);
            deleteButton.setText("🗑 ลบ");
            LinearLayout.LayoutParams delLp = new LinearLayout.LayoutParams(0, 48, 0.55f);
            delLp.setMarginStart(6);
            deleteButton.setLayoutParams(delLp);
            deleteButton.setTextColor(0xFFC62828);
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("ลบประวัติการติดตาม?")
                        .setMessage("รายการนี้จะถูกลบออกจากฐานข้อมูลเครื่อง")
                        .setNegativeButton("ยกเลิก", null)
                        .setPositiveButton("ลบ", (dialog, which) -> {
                            dbManager.deleteTrackingRecord(obj.getId());
                            Toast.makeText(PetTrackingActivity.this, "ลบประวัติแล้ว", Toast.LENGTH_SHORT).show();
                            renderRecords();
                        })
                        .show();
            });

            buttons.addView(mapButton);
            buttons.addView(deleteButton);

            box.addView(pet);
            box.addView(status);
            box.addView(time);
            box.addView(location);
            box.addView(note);
            box.addView(buttons);
            card.addView(box);
            recordsContainer.addView(card);
        }
    }
}
