package com.example.joboui.serviceProviderUi;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.clearSp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.joboui.R;
import com.example.joboui.adapters.ServiceProviderPageGrid;
import com.example.joboui.databinding.ActivityServiceProviderBinding;
import com.example.joboui.serviceProviderUi.pages.JobRequests;


public class ServiceProviderActivity extends AppCompatActivity {

    private ActivityServiceProviderBinding serviceProviderBinding;
    private ServiceProviderPageGrid serviceProviderPageGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceProviderBinding = ActivityServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(serviceProviderBinding.getRoot());

        Toolbar serviceProviderToolbar = serviceProviderBinding.serviceProviderToolbar;
        setSupportActionBar(serviceProviderToolbar);

        GridView serviceProviderGrid = serviceProviderBinding.serviceProviderGrid;
        serviceProviderGrid.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                default:break;

                case 0:
                    goToRequests();
                    break;
            }
        });

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, user -> {
            if (user.isPresent()) {
                serviceProviderToolbar.setTitle(user.get().getRole());
                serviceProviderToolbar.setSubtitle(user.get().getUsername());
                serviceProviderPageGrid = new ServiceProviderPageGrid(user.get().getUsername());
                serviceProviderGrid.setAdapter(serviceProviderPageGrid);
            }
        });
        }

        setWindowColors();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            userRepository.deleteUserDb();
            clearSp(USER_DB, getApplication());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    private void goToRequests() {
        startActivity(new Intent(ServiceProviderActivity.this, JobRequests.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
        addLogoutListener(ServiceProviderActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }


    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));

    }

}