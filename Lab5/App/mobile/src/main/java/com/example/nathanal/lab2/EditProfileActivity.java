package com.example.nathanal.lab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private File imageFile;
    private Profile userProfile;
    private String userID;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference profileGetRef = database.getReference("profiles");


    private DatabaseReference profileRef = profileGetRef.push();
    private Uri savedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userID = intent.getExtras().getString(MyProfileFragment.USER_ID);
            fetchDataFromFirebase();
        }

        if (savedInstanceState != null) {
            savedImageUri = savedInstanceState.getParcelable("ImageUri");
            if (savedImageUri != null) {
                try {
                    InputStream imageStream = getContentResolver().openInputStream(savedImageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageView imageView = findViewById(R.id.userImage);
                    imageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageUri", savedImageUri);
    }

    private void fetchDataFromFirebase() {
    profileGetRef.child(userID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            TextView username = findViewById(R.id.editUsername);
            TextView password = findViewById(R.id.editPassword);
            TextView weight = findViewById(R.id.editUserWeight);
            TextView height = findViewById(R.id.editUserHeight);

            username.setText(dataSnapshot.child("username").getValue(String.class));
            password.setText(dataSnapshot.child("password").getValue(String.class));
            weight.setText(dataSnapshot.child("weight").getValue(float.class).toString());
            height.setText(dataSnapshot.child("height").getValue(int.class).toString());

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.child("photo").getValue(String.class));
            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    final Bitmap selectedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageView imageView = findViewById(R.id.userImage);
                    imageView.setImageBitmap(selectedImage);

                }
            });

            profileRef = profileGetRef.child(userID);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void editUser(View view) {
        TextView username = findViewById(R.id.editUsername);
        TextView password = findViewById(R.id.editPassword);
        userProfile = new Profile(username.getText().toString(), password.getText().toString());

        TextView height = findViewById(R.id.editUserHeight);
        TextView weight = findViewById(R.id.editUserWeight);

        try {
            userProfile.height_cm = Integer.valueOf(height.getText().toString());
        } catch (NumberFormatException e) {
            userProfile.height_cm = 0;
        }

        try {
            userProfile.weight_kg = Integer.valueOf(weight.getText().toString());
        } catch (NumberFormatException e) {
            userProfile.height_cm = 0;
        }

        if (imageFile == null) {
            userProfile.photoPath = "";
        } else {
            userProfile.photoPath = imageFile.getPath();
        }

        addProfileToFirebaseDB();


    }


    private void addProfileToFirebaseDB() {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) findViewById(R.id.userImage)).getDrawable();
        if (bitmapDrawable == null) {
            Toast.makeText(this, "Missing picture", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = storageRef.child(profileRef.getKey() + ".jpg");

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
        uploadTask.addOnSuccessListener(new PhotoUploadListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageFile = new File(getExternalFilesDir(null), "profileImage");
            try {
                copyImage(imageUri, imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final InputStream imageStream;
            try {
                savedImageUri =Uri.fromFile(imageFile);
                imageStream = getContentResolver().openInputStream(Uri.fromFile(imageFile));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView imageView = findViewById(R.id.userImage);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyImage(Uri uriInput, File fileOutput) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = getContentResolver().openInputStream(uriInput);
            out = new FileOutputStream(fileOutput);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }

    private class PhotoUploadListener implements OnSuccessListener<UploadTask.TaskSnapshot> {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    userProfile.photoPath = uri.toString();
                    profileRef.runTransaction(new ProfileDataUploadHandler());
                }
            });


        }
    }

    private class ProfileDataUploadHandler implements Transaction.Handler {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            mutableData.child("username").setValue(userProfile.username);
            mutableData.child("password").setValue(userProfile.password);
            mutableData.child("height").setValue(userProfile.height_cm);
            mutableData.child("weight").setValue(userProfile.weight_kg);
            mutableData.child("photo").setValue(userProfile.photoPath);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
            if (b) {
                Toast.makeText(EditProfileActivity.this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                intent.putExtra("userProfile", userProfile);
                setResult(AppCompatActivity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }


}