package com.example.joboui.admin;

import static com.example.joboui.clientUi.ClientActivity.goToLoginPage;
import static com.example.joboui.globals.GlobalDb.userRepository;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityAdminBinding;
import com.example.joboui.domain.Domain;


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


        Toolbar clientToolbar = adminBinding.adminToolbar;
        setSupportActionBar(clientToolbar);

        setWindowColors();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            userRepository.deleteUserDb();
            updateUi();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("SetTextI18n")
    private void updateUi() {
        Domain.User localUser = userRepository.getUser();
        if (localUser != null) {

        } else {
            goToLoginPage(this);
        }
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));

    }
}