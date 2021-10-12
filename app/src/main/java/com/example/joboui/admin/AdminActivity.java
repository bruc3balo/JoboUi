package com.example.joboui.admin;

import static com.example.joboui.clientUi.ClientActivity.checkToLogoutUser;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.LOGGED_IN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.editSp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityAdminBinding;
import com.example.joboui.domain.Domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;


//1. Manage Services
//2. Manage Users
//3. Manage Feedback
//4. Manage Complaints
//5. Chat with users
//6. View Logs
//7.

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding adminBinding;
    private Timer loginTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(adminBinding.getRoot());

        Toolbar adminToolbar = adminBinding.adminToolbar;
        setSupportActionBar(adminToolbar);

        userRepository.getUserLive().observe(this, new Observer<Domain.User>() {
            @Override
            public void onChanged(Domain.User user) {
                if (user != null) {
                    adminToolbar.setTitle(user.getRole());
                    adminToolbar.setSubtitle(user.getUsername());
                }
            }
        });



        setWindowColors();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            userRepository.deleteUserDb();
            Map<String, Boolean> map = new HashMap<>();
            map.put(LOGGED_IN, false);
            editSp(USER_DB, map, getApplication());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
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
        loginTimer = new Timer();
        loginTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { checkToLogoutUser(AdminActivity.this, getApplication()); }}, 1000, 1000);
    }

    private void removeListener() {
        loginTimer.cancel();
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));

    }
}