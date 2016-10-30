package com.sneakred.owlsecurity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class EmergencyContacts extends AppCompatActivity {
    private EditText num1;
    private EditText num2;
    private EditText num3;
    private EditText num4;
    private EditText num5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        getSupportActionBar().hide();



        num1 = (EditText) findViewById(R.id.emergencycontacts_phone_one);
        num2 = (EditText) findViewById(R.id.emergencycontacts_phone_two);
        num3 = (EditText) findViewById(R.id.emergencycontacts_phone_three);
        num4 = (EditText) findViewById(R.id.emergencycontacts_phone_four);
        num5 = (EditText) findViewById(R.id.emergencycontacts_phone_five);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
        num1.setText(sharedPref.getString("contact1", ""));
        num2.setText(sharedPref.getString("contact2", ""));
        num3.setText(sharedPref.getString("contact3", ""));
        num4.setText(sharedPref.getString("contact4", ""));
        num5.setText(sharedPref.getString("contact5", ""));

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("contact1", num1.getText().toString());
        editor.putString("contact2", num2.getText().toString());
        editor.putString("contact3", num3.getText().toString());
        editor.putString("contact4", num4.getText().toString());
        editor.putString("contact5", num5.getText().toString());
        editor.apply();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}
