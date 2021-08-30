package com.example.joboui.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityAdminBinding;



//1. Manage Services
//2. Manage Users
//3. Manage Feedback
//4. Manage Complaints
//5. Chat with users
//6. View Logs
//7.

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding adminBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(adminBinding.getRoot());

        setWindowColors();
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