package com.example.appfordriver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private MapView mapView;
    private GoogleMap googleMap;
    private DatabaseReference markerRef;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> startActivity(new Intent(MapActivity.this, SettingActivity.class)));

        // Firebase 데이터베이스 참조
        markerRef = FirebaseDatabase.getInstance().getReference("markers");

        // Firebase에서 핑 정보 실시간 수신
        markerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (googleMap != null) {
                    googleMap.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Pothole pothole = snapshot.getValue(Pothole.class);
                        if (pothole != null) {
                            LatLng location = new LatLng(pothole.getLatitude(), pothole.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(location).title("포트홀"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
                Toast.makeText(MapActivity.this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 핑 버튼 클릭 시 현재 위치에 핑 남기기
        Button pingButton = findViewById(R.id.pingButton);
        pingButton.setOnClickListener(v -> addMarkerAtCurrentLocation());
    }

    private void addMarkerAtCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("My Ping"));

                            // Firebase에 위치 저장
                            String markerId = markerRef.push().getKey();
                            if (markerId != null) {
                                markerRef.child(markerId).setValue(new Pothole(currentLocation.latitude, currentLocation.longitude));
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // 위치 권한이 부여되지 않은 경우 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // 위치 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 부여된 경우
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                // 위치 권한이 거부된 경우
                Toast.makeText(this, "위치 권한을 부여해야 이 기능을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
