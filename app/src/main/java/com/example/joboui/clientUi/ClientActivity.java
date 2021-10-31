package com.example.joboui.clientUi;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.directToLogin;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.LOGGED_IN;
import static com.example.joboui.globals.GlobalVariables.SERVICE_DB;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.clearSp;
import static com.example.joboui.login.SignInActivity.getSp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.joboui.R;
import com.example.joboui.adapters.ServicesPageGrid;
import com.example.joboui.databinding.ActivityClientBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.login.ServiceProviderAdditionalActivity;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;

public class ClientActivity extends AppCompatActivity {
    ActivityClientBinding clientBinding;
    ServicesPageGrid servicesPageGridAdapter;
    private Domain.User me;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientBinding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(clientBinding.getRoot());

        Toolbar clientToolbar = clientBinding.clientToolbar;
        setSupportActionBar(clientToolbar);

        GridView servicesGrid = clientBinding.servicesGrid;
        servicesPageGridAdapter = new ServicesPageGrid(ClientActivity.this);
        servicesGrid.setAdapter(servicesPageGridAdapter);
        servicesGrid.setOnItemClickListener((adapterView, view, position, id) -> {
            //Toast.makeText(ClientActivity.this, servicesPageGridAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SERVICE_DB,servicesPageGridAdapter.getItem(position));
            startActivity(new Intent(ClientActivity.this,ServiceRequestActivity.class).putExtras(bundle));
        });

        SearchView clientSearch = clientBinding.clientSearch;
        clientSearch.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showSearch(clientBinding.introText, clientSearch);
            } else {
                hideSearch(clientBinding.introText, clientSearch);
            }
        });
        userRepository.getUserLive().observe(this, user -> {
            if (user.isPresent()) {
                me = user.get();
                clientBinding.welcomeText.setText(getWelcomeGreeting(me.getUsername()));
            }
        });

        setWindowColors();

    }

    private SpannableStringBuilder getWelcomeGreeting(String username) {
        String welcome;
        int hour;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hour = LocalDateTime.now().getHour();

        } else {
            hour = Calendar.getInstance().getTime().getHours();
        }

        Toast.makeText(this, String.valueOf(hour), Toast.LENGTH_SHORT).show();

        if (hour < 12) {
            welcome = "Good Morning ";
        } else if (hour > 12 && hour < 16) {
            welcome = "Good Afternoon ";
        } else if (hour > 16 && hour < 24) {
            welcome = "Good Evening ";
        } else {
            welcome = "Hello ";
        }

        ForegroundColorSpan foregroundColorSpanRed = new ForegroundColorSpan(getColor(R.color.bright_purple));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        SpannableStringBuilder welcomeText = new SpannableStringBuilder(welcome.concat(username));

        int end = welcome.length() + username.length();
        welcomeText.setSpan(foregroundColorSpanRed, welcome.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeText.setSpan(boldSpan, welcome.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        return welcomeText;
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
        addLogoutListener(ClientActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void showSearch(TextView tv, SearchView search) {
        tv.setVisibility(View.GONE);
        //search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void hideSearch(TextView tv, SearchView search) {
        tv.setVisibility(View.VISIBLE);
        // search.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
    }


    private void updateList() {
        servicesPageGridAdapter.notifyDataSetChanged();
    }

    public static void checkToLogoutUser(Activity activity, Application application) {

        //todo use live data instead
        Map<String, ?> map = getSp(USER_DB, application);

        if (map == null) {
            System.out.println("No user sp");
            //todo rectify
            return;
        }

        Boolean loggedIn = (Boolean) map.get(LOGGED_IN);
        if (loggedIn == null) {
            System.out.println("No logged in value sp");
            //todo rectify
            return;
        }

        if (!loggedIn) {
            directToLogin(activity);
        }
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));

    }


}