package com.wsec;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wsec.model.TrustedContact;
import com.wsec.utils.SharedPrefUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.container_women_safety)
    CardView containerWomenSafety;
    @BindView(R.id.container_women_health)
    CardView containerWomenHealth;
    @BindView(R.id.container_emergency)
    CardView containerEmergency;
    @BindView(R.id.container_location_share)
    CardView containerLocationShare;
    @BindView(R.id.container_dev_info)
    CardView containerDevInfo;
    LocationManager locationManager;

    ProgressDialog dialog;
    @BindView(R.id.container_siren_bot)
    CardView containerSirenBot;
    @BindView(R.id.container_messenger_bot)
    CardView containerMessengerBot;
    private FusedLocationProviderClient fusedLocationClient;

    String FACEBOOK_BOT_LINK = "http://m.me/108653704101610";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait...");
        dialog.setMessage("Getting your current approximate location");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @OnClick(R.id.container_women_safety)
    public void onContainerWomenSafetyClicked() {
        startActivity(new Intent(this, ActivityWomenSafety.class));
    }

    @OnClick(R.id.container_women_health)
    public void onContainerWomenHealthClicked() {
        startActivity(new Intent(this, ActivityWomenHealth.class));

    }

    @OnClick(R.id.container_emergency)
    public void onContainerEmergencyClicked() {
        startActivity(new Intent(this, EmergencyActivity.class));

    }

    @OnClick(R.id.container_location_share)
    public void onContainerLocationShareClicked() {
        dialog.show();
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    getLastKnownLoc();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();

            }
        }).check();


    }

    @OnClick(R.id.container_messenger_bot)
    public void onMessengerBotClicked() {
        try {
            Uri uri = Uri.parse(FACEBOOK_BOT_LINK);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setPackage("com.facebook.orca");
            i.setData(uri);
            startActivity(i);
        } catch (Exception e) {
            Uri uri = Uri.parse(FACEBOOK_BOT_LINK);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(uri);
            startActivity(i);
        }
    }

    @OnClick(R.id.container_siren_bot)
    public void onSirenBotClicked() {
        startActivity(new Intent(this, SpeechRecognitionActivity.class));
    }

    @OnClick(R.id.container_dev_info)
    public void onDevInfoClicked() {
        startActivity(new Intent(this,DeveloperInfoActivity.class));
    }



    private void getLastKnownLoc() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        shareLocation(task);
                    }
                }, 1500))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MenuActivity.this, "Couldn't Retrieve Location !", Toast.LENGTH_SHORT).show();

                    }
                })
        ;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN) {

                if (event.getRepeatCount() == 21) {
                    sendEmergencySMSToTrustedContacts();
                }

                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void sendEmergencySMSToTrustedContacts() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<TrustedContact>>() {
        }.getType();
        List<TrustedContact> trustedContactList = gson.fromJson(SharedPrefUtil.getInstance().getTrustedContactsJSON(this), listType) == null ? new ArrayList<>() : gson.fromJson(SharedPrefUtil.getInstance().getTrustedContactsJSON(this), listType);


        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                double lat = task.getResult().getLatitude();
                double lon = task.getResult().getLongitude();

                if (trustedContactList.size() > 0) {
                    for (TrustedContact trustedContact : trustedContactList) {
                        String message = String.format("Help Me Please. My Current Location : https://maps.google.com/?q=%s,%s", lat, lon);
                        sendEmergencySMS(trustedContact.getContactNo(), message);
                    }
                }

            }
        });


    }

    private void sendEmergencySMS(String phoneNumber, String message) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(this, SmsSentReceiver.class), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(this, SmsDeliveredReceiver.class), 0);
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
            }
            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                    sentPendingIntents, deliveredPendingIntents);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getBaseContext(), "SMS sending failed...", Toast.LENGTH_SHORT).show();
        }

    }

    private void shareLocation(Task<Location> task) {
        if (task.getResult() != null) {
            double lat = task.getResult().getLatitude();
            double lon = task.getResult().getLongitude();


            double accuracy = task.getResult().getAccuracy();

            Intent txtIntent = new Intent(Intent.ACTION_SEND);
            txtIntent.setType("text/plain");
            txtIntent.putExtra(Intent.EXTRA_SUBJECT, "My Location");
            txtIntent.putExtra(Intent.EXTRA_TEXT, String.format("https://maps.google.com/?q=%s,%s", lat, lon));
            startActivity(Intent.createChooser(txtIntent, "Share Location"));
        } else {
            Toast.makeText(this, "Couldn't Retrieve Location", Toast.LENGTH_SHORT).show();
        }

    }


    public class SmsDeliveredReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public class SmsSentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show();

                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "SMS generic failure", Toast.LENGTH_SHORT)
                            .show();

                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "SMS no service", Toast.LENGTH_SHORT)
                            .show();

                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "SMS null PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context, "SMS radio off", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


}
