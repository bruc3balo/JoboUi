package com.example.joboui.login;

import static com.example.joboui.models.Models.CLIENT;
import static com.example.joboui.models.Models.SERVICE_PROVIDER;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityRegisterBinding;
import com.example.joboui.tutorial.TutorialActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegister;
    private String role = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegister = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegister.getRoot());

        Button registerUserButton = activityRegister.registerUserButton;
        registerUserButton.setOnClickListener(view -> {
            switch (role) {
                case "":
                    Toast.makeText(this, "Pick a role", Toast.LENGTH_SHORT).show();
                    break;
                case CLIENT:
                    goToTutorialsPage();
                    break;
                case SERVICE_PROVIDER:
                    goToAdditionalInfoActivity();
                    break;
            }
        });

        RadioGroup roleRadioGroup = activityRegister.roleRadioGroup;
        roleRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == activityRegister.clientRadio.getId()) {
                role = CLIENT;
            } else {
                role = SERVICE_PROVIDER;
            }
        });

        setWindowColors();
    }

    private void goToAdditionalInfoActivity () {
        startActivity(new Intent(this, ServiceProviderAdditionalActivity.class));
    }

    private void goToTutorialsPage () {
        startActivity(new Intent(this, TutorialActivity.class));
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