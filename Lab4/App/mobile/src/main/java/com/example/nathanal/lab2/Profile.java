package com.example.nathanal.lab2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

public class Profile implements Serializable {
    private static  final String TAG = "Profile";

    protected String username;
    protected String password;
    protected int height_cm;
    protected float weight_kg;
    protected String photoPath;

    public Profile (String username, String password){
        this.username = username;
        this.password = password;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();
        dataMap.putString("username",username);
        dataMap.putString("password",password);
        dataMap.putInt("height",height_cm);
        dataMap.putFloat("weight",weight_kg);
        final InputStream imageStream;
        try {
            imageStream = new FileInputStream(photoPath);
            final Bitmap userImage = BitmapFactory.decodeStream(imageStream);
            Asset asset = WearService.createAssetFromBitmap(userImage);
            dataMap.putAsset("photo", asset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataMap;
    }
}
