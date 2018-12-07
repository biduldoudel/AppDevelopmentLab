package com.example.nathanal.lab2;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Profile userProfile = null;
    private static final int REGISTER_PROFILE = 1;
    private static final int SEND_PROFILE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = findViewById(R.id.RegisterButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditProfile = new Intent(LoginActivity.this, EditProfileActivity.class);
                startActivityForResult(intentEditProfile, REGISTER_PROFILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_PROFILE && resultCode == RESULT_OK) {
            userProfile = (Profile) data.getSerializableExtra("userProfile");
            if (userProfile != null) {
                TextView username = findViewById(R.id.Username);
                TextView password = findViewById(R.id.Password);

                username.setText(userProfile.username);
                password.setText(userProfile.password);
            }
        }
    }

    public void clickedLoginButtonXmlCallback(View view) {
        TextView textView = findViewById(R.id.LoginMessage);
        if (userProfile != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userProfile", userProfile);
            startActivity(intent);
        } else {
            textView.setText("User not yet registered");
            textView.setTextColor(Color.RED);
        }
    }


}
