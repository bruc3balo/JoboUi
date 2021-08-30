package com.example.joboui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.joboui.R;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding activity_sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_sign_in = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(activity_sign_in.getRoot());


        Button signInUserButton = activity_sign_in.signInUserButton;
        signInUserButton.setOnClickListener(view -> goToClientPage());

        setWindowColors();

    }

    private void goToClientPage ( ) {
        startActivity(new Intent(this, ClientActivity.class));
        finish();
    }

    private void setWindowColors () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple));
        }

    }
}