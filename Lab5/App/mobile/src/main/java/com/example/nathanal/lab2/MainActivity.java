package com.example.nathanal.lab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MyHistoryFragment
        .OnFragmentInteractionListener, MyProfileFragment.OnFragmentInteractionListener, NewRecordingFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getSimpleName();

    private NewRecordingFragment newRecFragment;
    private MyProfileFragment myProfileFragment;
    private MyHistoryFragment myHistoryFragment;
    private SectionStatePagerAdapter mSectionStatePagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());

        // Do this in case of detaching of Fragments
        myProfileFragment = new MyProfileFragment();
        newRecFragment = new NewRecordingFragment();
        myHistoryFragment = new MyHistoryFragment();

        ViewPager mViewPager = findViewById(R.id.mainViewPager);
        setUpViewPager(mViewPager);

        // Set NewRecordingFragment as default tab once started the activity
        mViewPager.setCurrentItem(mSectionStatePagerAdapter.getPositionByTitle("New Activity"));
    }

    private void setUpViewPager(ViewPager mViewPager) {
        mSectionStatePagerAdapter.addFragment(myProfileFragment, "My Profile");
        mSectionStatePagerAdapter.addFragment(newRecFragment, "New Activity");
        mSectionStatePagerAdapter.addFragment(myHistoryFragment, "My History");
        mViewPager.setAdapter(mSectionStatePagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
