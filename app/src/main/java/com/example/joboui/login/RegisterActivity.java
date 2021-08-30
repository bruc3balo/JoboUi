package com.example.joboui.login;

import static com.example.joboui.models.Models.CLIENT;
import static com.example.joboui.models.Models.SERVICE_PROVIDER;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityRegisterBinding;
import com.example.joboui.databinding.RoleDialogBinding;
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
        registerUserButton.setOnClickListener(view -> showRoleDialog());

        setWindowColors();
    }

    private void showRoleDialog() {
        Dialog d = new Dialog(RegisterActivity.this);
        RoleDialogBinding roleDialogBinding = RoleDialogBinding.inflate(getLayoutInflater());
        d.setContentView(roleDialogBinding.getRoot());
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button confirmRoleButton = roleDialogBinding.confirmRoleButton;
        RadioGroup roleRadioGroup = roleDialogBinding.roleRadioGroup;
        ImageButton neverMindButton = roleDialogBinding.neverMindButton;

        d.show();

        roleRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == roleDialogBinding.clientRadio.getId()) {
                role = CLIENT;
            } else {
                role = SERVICE_PROVIDER;
            }
        });
        confirmRoleButton.setOnClickListener(view -> {
            if (role.equals("")) {
                Toast.makeText(RegisterActivity.this, "Pick a role", Toast.LENGTH_SHORT).show();
            } else {
                d.dismiss();
                Toast.makeText(RegisterActivity.this, role, Toast.LENGTH_SHORT).show();
                switch (role) {
                    case CLIENT:
                        goToTutorialsPage();
                        break;
                    case SERVICE_PROVIDER:
                        goToAdditionalInfoActivity();
                        break;
                }
            }
        });
        neverMindButton.setOnClickListener(view -> d.dismiss());
    }

    private void goToAdditionalInfoActivity() {
        startActivity(new Intent(this, ServiceProviderAdditionalActivity.class));
    }

    private void goToTutorialsPage() {
        startActivity(new Intent(this, TutorialActivity.class));
    }

    private void setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple));
        }

    }
}