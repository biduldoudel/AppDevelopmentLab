package com.example.nathanal.lab2;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView helloView;
    private ConstraintLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView helloView = (TextView) findViewById(R.id.HelloView);
        helloView.setText("Hello Round");

        mLayout = findViewById(R.id.container);
        // Enables Always-on
        setAmbientEnabled();
    }
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        updateDisplay();
    }

    private void updateDisplay() {
        if(isAmbient()){
            mLayout.setBackgroundColor(getResources().getColor(android.R.color.black,getTheme()));
        }else{
            mLayout.setBackgroundColor(getResources().getColor(android.R.color.white,getTheme()));
        }
    }
}
