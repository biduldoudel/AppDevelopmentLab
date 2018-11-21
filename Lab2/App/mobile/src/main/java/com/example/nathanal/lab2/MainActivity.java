package com.example.nathanal.lab2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.RegisterButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                TextView textView = findViewById(R.id.LoginMessage);
                textView.setText("Java callback");
            }
        });
    }

    public void clickedLoginButtonXmlCallback(View view) {
        TextView textView = findViewById(R.id.LoginMessage);
        textView.setText("XML callback");
    }
}
