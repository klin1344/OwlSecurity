package com.sneakred.securitycam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FrontPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

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
                Intent contactsIntent = new  Intent(FrontPage.this, EmergencyContacts.class);
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
