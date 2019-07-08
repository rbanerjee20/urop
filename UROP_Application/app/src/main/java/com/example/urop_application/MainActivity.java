package com.example.urop_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToNextScreen(View view) {
        Intent intent = new Intent(this, AdvertisingID.class);
        startActivity(intent);
    }

    public void isAdBlockerInstalled(View view) {
        String found = "true";
        PackageManager pm = getPackageManager();
        String packageName = "com.hsv.freeadblockerbrowser";
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = "false";
        }
        Toast toast = Toast.makeText(getApplicationContext(), found, Toast.LENGTH_LONG);
        toast.show();
    }
}
