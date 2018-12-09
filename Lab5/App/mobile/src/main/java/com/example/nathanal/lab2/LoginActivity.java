package com.example.nathanal.lab2;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Profile userProfile = null;
    private static final int REGISTER_PROFILE = 1;
    private static final int SEND_PROFILE = 2;
    private String userID;


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

    public void clickedLoginButtonXmlCallback(View view) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileRef = database.getReference("profiles");

        final TextView mTextView = findViewById(R.id.LoginMessage);
        final String usernameInput = ((EditText) findViewById(R.id.Username)).getText().toString();
        final String passwordInput = ((EditText) findViewById(R.id.Password)).getText().toString();

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean notMember = true;
                for (final DataSnapshot user : dataSnapshot.getChildren()) {
                    String usernameDatabase = user.child("username").getValue(String.class);
                    String passwordDatabase = user.child("password").getValue(String.class);
                    if (usernameInput.equals(usernameDatabase) && passwordInput.equals
                            (passwordDatabase)) {
                        userID = user.getKey();
                        notMember = false;
                        break;
                    }
                }
                if (notMember) {
                    mTextView.setText(R.string.not_registered_yet);
                    mTextView.setTextColor(Color.RED);
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MyProfileFragment.USER_ID, userID);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_PROFILE && resultCode == RESULT_OK && data != null) {
            userProfile = (Profile) data.getSerializableExtra(MyProfileFragment.USER_PROFILE);
            if (userProfile != null) {
                TextView username = findViewById(R.id.Username);
                username.setText(userProfile.username);
                TextView password = findViewById(R.id.Password);
                password.setText(userProfile.password);
            }
        }
    }
}

