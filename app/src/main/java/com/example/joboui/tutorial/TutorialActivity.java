package com.example.joboui.tutorial;

import static com.example.joboui.login.LoginActivity.proceed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.joboui.R;
import com.example.joboui.adapters.TutorialVpAdapter;
import com.example.joboui.databinding.ActivityTutorialBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class TutorialActivity extends AppCompatActivity {

    private ActivityTutorialBinding tutorialBinding;
    private final ArrayList<Domain.TutorialModel> tutorialList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialBinding = ActivityTutorialBinding.inflate(getLayoutInflater());
        setContentView(tutorialBinding.getRoot());

        populateTutorials();

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
                showPb();
                new ViewModelProvider(this).get(UserViewModel.class).updateExistingUser(new Models.UserUpdateForm(true)).observe(this, user -> {
                    if (user.isPresent()) {
                        proceed(TutorialActivity.this);
                    } else {
                        hidePb();
                    }
                });
            }
        });

        hidePb();
        setWindowColors();
    }

    private void showPb() {
        tutorialBinding.nextButton.setEnabled(false);
        tutorialBinding.tutorialPb.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
        tutorialBinding.tutorialPb.setVisibility(View.GONE);
        tutorialBinding.nextButton.setEnabled(true);
    }

    private void populateTutorials() {
        Domain.TutorialModel tutorialModel = new Domain.TutorialModel("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.clker.com%2Fcliparts%2F3%2Fm%2F1%2FO%2F7%2Fu%2Fsearch-icon-red-hi.png&f=1&nofb=1", "Look for the job you want ", "Search");
        Domain.TutorialModel tutorialModel1 = new Domain.TutorialModel("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fthumbs.dreamstime.com%2Fb%2Fattractive-man-sleeping-home-couch-mobile-phone-digital-tablet-pad-his-hands-young-shirt-jeans-internet-61244350.jpg&f=1&nofb=1", "At the comfort of your couch", "Convinience");
        tutorialList.add(tutorialModel);
        tutorialList.add(tutorialModel1);
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.white));
        getWindow().setNavigationBarColor(getColor(R.color.white));

    }

}