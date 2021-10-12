package com.example.joboui.admin;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.clearSp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        Toolbar adminToolbar = adminBinding.adminToolbar;
        setSupportActionBar(adminToolbar);

        userRepository.getUserLive().observe(this, user -> {
            if (user.isPresent()) {
                adminToolbar.setTitle(user.get().getRole());
                adminToolbar.setSubtitle(user.get().getUsername());
            }
        });



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

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
        addLogoutListener(AdminActivity.this);
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