package com.example.nathanal.lab1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private void demo_logcat() {
        Log.v(TAG, "Verbose");
        Log.d(TAG, "Debug");
        Log.i(TAG, "Information");
        Log.w(TAG, "Warning");
        Log.e(TAG, "Error");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView helloView = findViewById(R.id.my_text);
        helloView.setText(getString(R.string.TestString));
        this.demo_logcat();
    }

}
