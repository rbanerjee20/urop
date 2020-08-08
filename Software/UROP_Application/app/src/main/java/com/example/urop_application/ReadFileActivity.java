package com.example.urop_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class ReadFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_file);

        TextView textView = findViewById(R.id.trackingFileInfoTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(MainActivity.getTrackingFileInfo());
    }


}
