package com.example.myapplicationjt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationjt.model.Hospital;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HospitalSearchActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private final List<Hospital> hospitalList = new ArrayList<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchUserLocation();
                } else {
                    Toast.makeText(this, "ไม่ได้เปิด GPS แสดงพิกัดเริ่มต้น", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hospital_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Back Button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Initialize Hospital Models (Phitsanulok, Thailand)
        hospitalList.add(new Hospital("h1", "โรงพยาบาลสัตว์พิษณุโลก", "อ.เมือง จ.พิษณุโลก", "055-258123", 16.8260, 100.2600));
        hospitalList.add(new Hospital("h2", "โรงพยาบาลสัตว์มหาวิทยาลัยนเรศวร", "ม.นเรศวร จ.พิษณุโลก", "055-965000", 16.7320, 100.1930));
        hospitalList.add(new Hospital("h3", "โรงพยาบาลสัตว์เอกอารักษ์ พิษณุโลก", "อ.เมือง จ.พิษณุโลก", "055-303333", 16.8150, 100.2700));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        // 2. Hospital Cards Click Listeners
        MaterialCardView cardA = findViewById(R.id.cardHospitalA);
        if (cardA != null) {
            cardA.setOnClickListener(v -> openGoogleMapsDirections("โรงพยาบาลสัตว์พิษณุโลก", 16.8260, 100.2600));
        }

        MaterialCardView cardB = findViewById(R.id.cardHospitalB);
        if (cardB != null) {
            cardB.setOnClickListener(v -> openGoogleMapsDirections("โรงพยาบาลสัตว์มหาวิทยาลัยนเรศวร", 16.7320, 100.1930));
        }

        MaterialCardView cardC = findViewById(R.id.cardHospitalC);
        if (cardC != null) {
            cardC.setOnClickListener(v -> openGoogleMapsDirections("โรงพยาบาลสัตว์เอกอารักษ์ พิษณุโลก", 16.8150, 100.2700));
        }

        // 3. View All Map Button
        MaterialButton btnViewAllMap = findViewById(R.id.btnViewAllMap);
        if (btnViewAllMap != null) {
            btnViewAllMap.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("geo:16.8211,100.2659?q=โรงพยาบาลสัตว์+พิษณุโลก");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(mapIntent);
                } catch (Exception e) {
                    Uri webUri = Uri.parse("https://www.google.com/maps/search/โรงพยาบาลสัตว์+พิษณุโลก/@16.8211,100.2659,13z");
                    startActivity(new Intent(Intent.ACTION_VIEW, webUri));
                }
            });
        }

        // 4. Setup Filter Chip Clicks
        setupFilterChips();

        // 5. Bottom Navigation Listeners
        LinearLayout navHome = findViewById(R.id.navHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }

        LinearLayout navHospital = findViewById(R.id.navHospital);
        if (navHospital != null) {
            navHospital.setOnClickListener(v -> Toast.makeText(this, "คุณอยู่ที่หน้าค้นหาโรงพยาบาลแล้ว", Toast.LENGTH_SHORT).show());
        }

        LinearLayout navAppointment = findViewById(R.id.navAppointment);
        if (navAppointment != null) {
            navAppointment.setOnClickListener(v -> {
                startActivity(new Intent(this, AppointmentActivity.class));
                finish();
            });
        }

        LinearLayout navOwner = findViewById(R.id.navOwner);
        if (navOwner != null) {
            navOwner.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            });
        }

        LinearLayout navMore = findViewById(R.id.navMore);
        if (navMore != null) {
            navMore.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
            });
        }
    }

    private void setupFilterChips() {
        int[] petChipIds = new int[]{R.id.chipDog, R.id.chipCat, R.id.chipRabbit, R.id.chipBird};
        for (int id : petChipIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setOnClickListener(v -> Toast.makeText(this, "กรองตาม: " + tv.getText(), Toast.LENGTH_SHORT).show());
            }
        }

        int[] serviceChipIds = new int[]{R.id.chipCheckup, R.id.chipVaccine, R.id.chipSurgery, R.id.chipXray, R.id.chipBlood, R.id.chipMoreServices};
        for (int id : serviceChipIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setOnClickListener(v -> Toast.makeText(this, "กรองตามบริการ: " + tv.getText(), Toast.LENGTH_SHORT).show());
            }
        }

        int[] distChipIds = new int[]{R.id.chipNearest, R.id.chip5km, R.id.chip10km, R.id.chip20km, R.id.chip15km};
        for (int id : distChipIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setOnClickListener(v -> Toast.makeText(this, "ระยะทาง: " + tv.getText(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchUserLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchUserLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && location.getLongitude() > 90.0) {
                    updateDistances(location.getLatitude(), location.getLongitude());
                } else {
                    // Force/fallback to Phitsanulok City Center, Thailand (16.8211, 100.2659)
                    updateDistances(16.8211, 100.2659);
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
            updateDistances(16.8211, 100.2659);
        }
    }

    private void updateDistances(double lat, double lon) {
        int[] viewIds = new int[]{R.id.tvDistA, R.id.tvDistB, R.id.tvDistC};

        for (int i = 0; i < hospitalList.size() && i < viewIds.length; i++) {
            Hospital hosp = hospitalList.get(i);
            float[] results = new float[1];
            Location.distanceBetween(lat, lon, hosp.getLatitude(), hosp.getLongitude(), results);
            double distanceKm = results[0] / 1000.0;

            TextView tv = findViewById(viewIds[i]);
            if (tv != null) {
                tv.setText(String.format(Locale.getDefault(), "📍 %.1f กม.", distanceKm));
            }
        }
    }

    private void openGoogleMapsDirections(String hospitalName, double lat, double lon) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + " (" + hospitalName + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon);
            startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }
}