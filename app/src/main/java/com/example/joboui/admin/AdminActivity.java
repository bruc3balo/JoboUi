package com.example.joboui.admin;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.clientUi.ClientActivity.getWelcomeGreeting;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.clearSp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.joboui.R;
import com.example.joboui.adapters.AdminPageGrid;
import com.example.joboui.adapters.ServiceProviderPageGrid;
import com.example.joboui.admin.feedback.FeedbackActivity;
import com.example.joboui.databinding.ActivityAdminBinding;
import com.example.joboui.services.NotificationService;


//1. Manage Services
//2. Manage Users
//3. Manage Feedback
//4. Manage Complaints
//5. Chat with users
//6. View Logs
//7.

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding adminBinding;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(adminBinding.getRoot());

        Toolbar adminToolbar = adminBinding.adminToolbar;
        setSupportActionBar(adminToolbar);
        //adminToolbar.setOverflowIcon(getDrawable(R.drawable.more));

        GridView serviceProviderGrid = adminBinding.adminGrid;
        AdminPageGrid adminPageGrid = new AdminPageGrid();
        serviceProviderGrid.setAdapter(adminPageGrid);
        serviceProviderGrid.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                default:
                    break;

                case 0:
                    goToCashFlow();
                    break;

                case 1:
                    goToUsers();
                    break;

                case 2:
                    goToJobs();
                    break;

                case 3:
                    goToServices();
                    break;

                case 4:
                    goToReported();
                    break;

                case 5:
                    goToFeedback();
                    break;
            }
        });

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, user -> {
                user.ifPresent(value -> adminBinding.welcomeText.setText(getWelcomeGreeting(value.getUsername(), this)));
            });
        }


        setWindowColors();
    }

    private void goToUsers() {
        startActivity(new Intent(AdminActivity.this, UserActivity.class));
    }

    private void goToJobs() {
        startActivity(new Intent(AdminActivity.this, AllJobsActivity.class));
    }

    private void goToServices() {
        startActivity(new Intent(AdminActivity.this, ManageServicesAdmin.class));
    }

    private void goToFeedback() {
        startActivity(new Intent(AdminActivity.this, FeedbackActivity.class));
    }

    private void goToReported() {
        startActivity(new Intent(AdminActivity.this, ReportedAdminActivity.class));
    }

    private void goToCashFlow() {
        startActivity(new Intent(AdminActivity.this, CashFlowActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            logout(getApplication());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    public static void logout(Application application) {
        userRepository.deleteUserDb();
        clearSp(USER_DB, application);
        application.stopService(new Intent(application, NotificationService.class));
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