package com.example.joboui.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.TutorialVpAdapter;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivityTutorialBinding;
import com.example.joboui.models.Models;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class TutorialActivity extends AppCompatActivity {

    private ActivityTutorialBinding tutorialBinding;
    private final ArrayList<Models.TutorialModel> tutorialList = new ArrayList<>();
    private boolean isServiceProvider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialBinding = ActivityTutorialBinding.inflate(getLayoutInflater());
        setContentView(tutorialBinding.getRoot());

        populateTutorials();

        isServiceProvider = getIntent().getExtras() != null;

        ViewPager2 tutorialViewPager = tutorialBinding.tutorialViewPager;
        TutorialVpAdapter tutorialVpAdapter = new TutorialVpAdapter(this, tutorialList);
        tutorialViewPager.setAdapter(tutorialVpAdapter);

        CircleIndicator3 indicator = tutorialBinding.tutorialIndicator;
        indicator.setViewPager(tutorialViewPager);

        // optionalA
        tutorialVpAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());

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

    private void populateTutorials() {
        Models.TutorialModel tutorialModel = new Models.TutorialModel("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.clker.com%2Fcliparts%2F3%2Fm%2F1%2FO%2F7%2Fu%2Fsearch-icon-red-hi.png&f=1&nofb=1", "Look for the job you want ", "Search");
        Models.TutorialModel tutorialModel1 = new Models.TutorialModel("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fthumbs.dreamstime.com%2Fb%2Fattractive-man-sleeping-home-couch-mobile-phone-digital-tablet-pad-his-hands-young-shirt-jeans-internet-61244350.jpg&f=1&nofb=1", "At the comfort of your couch", "Convinience");
        tutorialList.add(tutorialModel);
        tutorialList.add(tutorialModel1);
    }

    private void setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.white));
            getWindow().setNavigationBarColor(getColor(R.color.white));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }

    }

    private void goToMainPage() {
        if (isServiceProvider) {
            Toast.makeText(TutorialActivity.this, "Finish Service Provider", Toast.LENGTH_SHORT).show();
            goToServiceProviderPage();
        } else {
            Toast.makeText(TutorialActivity.this, "Finish Client", Toast.LENGTH_SHORT).show();
            goToClientPage();
        }
    }

    private void goToClientPage() {
        startActivity(new Intent(this, ClientActivity.class));
        finish();
    }

    private void goToServiceProviderPage() {
        startActivity(new Intent(this, ServiceProviderActivity.class));
        finish();
    }
}