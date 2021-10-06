package com.example.joboui.serviceProviderUi;

import static com.example.joboui.globals.GlobalDb.userRepository;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityServiceProviderBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ServiceProviderActivity extends AppCompatActivity {

    ActivityServiceProviderBinding serviceProviderBinding;
    private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> updateUi(firebaseAuth.getCurrentUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceProviderBinding = ActivityServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(serviceProviderBinding.getRoot());

        Toolbar serviceProviderToolbar = serviceProviderBinding.serviceProviderToolbar;
        setSupportActionBar(serviceProviderToolbar);

        setWindowColors();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            FirebaseAuth.getInstance().signOut();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }


    private void updateUi(FirebaseUser user) {
        if (user != null) {

            Domain.User localUser = userRepository.getUser();
            if (localUser != null) {

            }

        } else {
            goToLoginPage();
        }
    }


    private void goToLoginPage() {
        startActivity(new Intent(ServiceProviderActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void addListener() {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void removeListener() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);

    }

    private void setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.purple));
            getWindow().setNavigationBarColor(getColor(R.color.purple));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.purple));
        }

    }

}