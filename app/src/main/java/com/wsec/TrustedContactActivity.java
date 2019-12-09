package com.wsec;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wsec.adapter.TrustedContactAdapter;
import com.wsec.model.TrustedContact;
import com.wsec.utils.SharedPrefUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrustedContactActivity extends AppCompatActivity implements RVOnlickListener {

    private static final int REQUEST_PICK_CONTACT = 100;
    @BindView(R.id.container_no_contact)
    CardView containerNoContact;
    @BindView(R.id.rv_trusted_contacts)
    RecyclerView rvTrustedContacts;
    @BindView(R.id.fab_add_contact)
    ExtendedFloatingActionButton fabAddContact;
    Context context;
    TrustedContactAdapter adapter;
    List<TrustedContact> trustedContactList;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contact);
        ButterKnife.bind(this);
        gson = new Gson();
        context = this;

        Type listType = new TypeToken<ArrayList<TrustedContact>>(){}.getType();
        trustedContactList = gson.fromJson(SharedPrefUtil.getInstance().getTrustedContactsJSON(context),listType) == null?new ArrayList<>():  gson.fromJson(SharedPrefUtil.getInstance().getTrustedContactsJSON(context),listType);

        if(trustedContactList.size()>0)
        {
            containerNoContact.setVisibility(View.GONE);
        }
        adapter = new TrustedContactAdapter(context,trustedContactList);
        rvTrustedContacts.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Type listType = new TypeToken<ArrayList<TrustedContact>>(){}.getType();
        trustedContactList = gson.fromJson(SharedPrefUtil.getInstance().getTrustedContactsJSON(context),listType) == null?new ArrayList<>():  gson.fromJson(SharedPrefUtil.getInstance().getTrustedContactsJSON(context),listType);
    }

    @OnClick(R.id.fab_add_contact)
    public void onViewClicked() {

        Dexter.withActivity(this).withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        pickContact();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        DialogOnDeniedPermissionListener.Builder
//                                .withContext(context)
//                                .withTitle("Contact permission")
//                                .withMessage("We need contact permission to pick your trusted contact")
//                                .withButtonText(android.R.string.ok)
//                                .build();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();

    }

    private void pickContact() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_CONTACT) {


            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {


                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String cNumber = "";
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    cNumber = phones.getString(phones.getColumnIndex("data1"));
                }
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                trustedContactList.add((new TrustedContact(name,cNumber)));
                String trustedContactJSON =  gson.toJson(trustedContactList);
                adapter = new TrustedContactAdapter(this,trustedContactList);
                rvTrustedContacts.setAdapter(adapter);
                rvTrustedContacts.setVisibility(View.VISIBLE);
                containerNoContact.setVisibility(View.GONE);
                SharedPrefUtil.getInstance().setTrustedContactsJSON(context,trustedContactJSON);
            }

        }
    }

    @Override
    public void onItemClick(int pos, String number) {


        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmation")
                .setMessage("Do you really wanna this person from your trusted contact ?")
                .setPositiveButton("Yes", (dialogInterface, index) -> {

                    List<TrustedContact> copyTrustedContactList = new ArrayList<>();

                    for(int i=0; i<trustedContactList.size();i++)
                    {
                        if(i != pos)
                        {
                            copyTrustedContactList.add(trustedContactList.get(i));
                        }
                    }

                    SharedPrefUtil.getInstance().setTrustedContactsJSON(this,gson.toJson(copyTrustedContactList));
                    trustedContactList = copyTrustedContactList;
                    adapter = new TrustedContactAdapter(this,copyTrustedContactList);
                    rvTrustedContacts.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                })
                .setNegativeButton("No",null)
                .show();
    }
}
