package com.wsec;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityWomenSafety extends AppCompatActivity {

    @BindView(R.id.container_eveteasing)
    CardView containerEveteasing;
    @BindView(R.id.container_domestic_violance)
    CardView containerDomesticViolance;
    @BindView(R.id.container_rad)
    CardView containerRad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_safety);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.container_eveteasing)
    public void onContainerEveteasingClicked() {

        Intent intent = new Intent(this, PDFViewer.class);
        intent.putExtra("fileName","ET.pdf");
        startActivity(intent);
    }

    @OnClick(R.id.container_domestic_violance)
    public void onContainerDomesticViolanceClicked() {

        Intent intent = new Intent(this, PDFViewer.class);
        intent.putExtra("fileName","DV.pdf");
        startActivity(intent);
    }

    @OnClick(R.id.container_rad)
    public void onContainerRadClicked() {

        Intent intent = new Intent(this, PDFViewer.class);
        intent.putExtra("fileName","RAD.pdf");
        startActivity(intent);
    }
}
