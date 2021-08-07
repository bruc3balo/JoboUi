package com.example.joboui.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.TutorialVpAdapter;
import com.example.joboui.databinding.ActivityTutorialBinding;
import com.example.joboui.models.Models;

import java.util.ArrayList;

public class TutorialActivity extends AppCompatActivity {

    private ActivityTutorialBinding tutorialBinding;
    private final ArrayList<Models.TutorialModel> tutorialList = new ArrayList<>();
    private boolean isServiceProvider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialBinding = ActivityTutorialBinding.inflate(getLayoutInflater());
        setContentView(tutorialBinding.getRoot());

        isServiceProvider = getIntent().getExtras() != null;

        ViewPager2 tutorialViewPager = tutorialBinding.tutorialViewPager;
        TutorialVpAdapter tutorialVpAdapter = new TutorialVpAdapter(this, tutorialList);
        tutorialViewPager.setAdapter(tutorialVpAdapter);

        Button next = tutorialBinding.nextButton;
        next.setOnClickListener(view -> {
            if (tutorialViewPager.getCurrentItem() != tutorialList.size() - 1) {
                tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem() + 1);
            } else {
                goToMainPage();
            }
        });

        setWindowColors();

    }

    private void setWindowColors () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.white));
            getWindow().setNavigationBarColor(getColor(R.color.white));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }

    }

    private void goToMainPage () {
        if (isServiceProvider) {
            Toast.makeText(TutorialActivity.this, "Finish Service Provider", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TutorialActivity.this, "Finish Client", Toast.LENGTH_SHORT).show();
        }
    }
}