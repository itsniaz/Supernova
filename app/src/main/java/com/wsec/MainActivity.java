package com.wsec;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wsec.utils.SharedPrefUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fab_lets_start)
    ExtendedFloatingActionButton fabLetsStart;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        if(SharedPrefUtil.getInstance().getIsFirstRun(this))
        {
            showPermssionExplainations();
        }
        else
        {
            askPermissions();
        }
    }

    private void showPermssionExplainations() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Notice")
                .setMessage(Html.fromHtml("<p>\n" +
                        "  This application requires the following permissions :\n" +
                        "  <ul>\n" +
                        "    <li> Location Permission : To share your location on emergency</li>\n" +
                        "    <li> Contact Permission  : To Pick Your emergency contacts</li>\n" +
                        "    <li> Phone Permission : To call emergency helpline</li>\n" +
                        "    <li> SMS Permission : To send SMS to your trusted contacts in time of emergency</li>\n" +
                        "</ul>\n" +
                        "</p>"))
                .setPositiveButton("Ok", (dialogInterface, i) -> {

                    askPermissions();

                })
                .show();
    }

    private void askPermissions() {

        Dexter.withActivity(context)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                 Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.READ_CONTACTS,
                                 Manifest.permission.CALL_PHONE,
                                 Manifest.permission.SEND_SMS)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();

    }

    @OnClick(R.id.fab_lets_start)
    public void onViewClicked() {
        startActivity(new Intent(this,MenuActivity.class));
    }
}
