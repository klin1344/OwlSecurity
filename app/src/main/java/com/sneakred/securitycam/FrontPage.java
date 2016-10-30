package com.sneakred.securitycam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FrontPage extends AppCompatActivity {
    private Button startButton;
    private Button contactsButton;
    private Button aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        getSupportActionBar().hide();


        startButton = (Button) findViewById(R.id.frontpage_start_security);
        contactsButton = (Button) findViewById(R.id.frontpage_emergency_contacts);
        aboutButton = (Button) findViewById(R.id.frontpage_about);


        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(FrontPage.this, MainActivity.class);
                startActivity(startIntent);
                startButton.getBackground().setAlpha(128);
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent contactsIntent = new  Intent(FrontPage.this, EmergencyContacts.class);
                startActivity(contactsIntent);
                contactsButton.getBackground().setAlpha(128);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent aboutIntent = new Intent(FrontPage.this, About.class);
                startActivity(aboutIntent);
                aboutButton.getBackground().setAlpha(128);
            }
        });
    }
}
