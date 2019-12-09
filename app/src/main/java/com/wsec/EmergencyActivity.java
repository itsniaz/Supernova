package com.wsec;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmergencyActivity extends AppCompatActivity {

    @BindView(R.id.container_trusted_contacts)
    CardView containerTrustedContacts;
    @BindView(R.id.container_emergency_helpline)
    CardView containerEmergencyHelpline;
    @BindView(R.id.container_suicide_prevention)
    CardView containerSuicidePrevention;
    @BindView(R.id.container_ambulance)
    CardView containerAmbulance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.container_trusted_contacts)
    public void onContainerTrustedContactsClicked() {
        startActivity(new Intent(this,TrustedContactActivity.class));
    }

    private void callKanPeteRoi() {

        String phone = "01779554391";
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+phone));
        startActivity(callIntent);

    }

    @OnClick(R.id.container_emergency_helpline)
    public void onContainerEmergencyHelplineClicked() {
        dial999();
    }


    private void dial999() {
        String phone = "999";
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+phone));
        startActivity(callIntent);
    }

    @OnClick(R.id.container_suicide_prevention)
    public void onContainerSuicidePreventionClicked() {
        callKanPeteRoi();

    }

    @OnClick(R.id.container_ambulance)
    public void onContainerAmbulanceClicked() {
        String uri = "geo:"+ 0 + "," + 0 +"?q=ambulances+near+me";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);

    }
}
