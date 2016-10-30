package com.sneakred.securitycam;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FrontPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permission.INTERNET)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permission.ACCESS_NETWORK_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permission.SEND_SMS)) {
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
                            permission.READ_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS},
                    1);
        }


        Button startButton = (Button) findViewById(R.id.frontpage_start_security);
        Button contactsButton = (Button) findViewById(R.id.frontpage_emergency_contacts);
        Button aboutButton = (Button) findViewById(R.id.frontpage_about);


        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(FrontPage.this, MainActivity.class);
                startActivity(startIntent);
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent contactsIntent = new Intent(FrontPage.this, EmergencyContacts.class);
                startActivity(contactsIntent);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent aboutIntent = new Intent(FrontPage.this, About.class);
                startActivity(aboutIntent);
            }
        });


    }
}
