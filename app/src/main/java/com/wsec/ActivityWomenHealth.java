package com.wsec;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityWomenHealth extends AppCompatActivity {

    @BindView(R.id.container_nearby_hospital)
    CardView containerNearbyHospital;
    @BindView(R.id.container_online_medicine)
    CardView containerOnlineMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_health);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.container_nearby_hospital)
    public void onContainerNearbyHospitalClicked() {
        String uri = "geo:"+ 0 + "," + 0 +"?q=hospitals+near+me";
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);

    }

    @OnClick(R.id.container_online_medicine)
    public void onContainerOnlineMedicineClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epharma.com.bd"));
        startActivity(browserIntent);

    }
}
