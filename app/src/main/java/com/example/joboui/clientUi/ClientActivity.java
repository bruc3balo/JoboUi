package com.example.joboui.clientUi;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.directToLogin;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.LOGGED_IN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.clearSp;
import static com.example.joboui.login.SignInActivity.getSp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.joboui.R;
import com.example.joboui.adapters.ServicesPageGrid;
import com.example.joboui.databinding.ActivityClientBinding;
import com.example.joboui.domain.Domain;

import java.util.ArrayList;
import java.util.Map;

public class ClientActivity extends AppCompatActivity {
    ActivityClientBinding clientBinding;
    ServicesPageGrid servicesPageGridAdapter;
    private final ArrayList<Domain.Services> serviceList = new ArrayList<>();
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
        servicesPageGridAdapter = new ServicesPageGrid(serviceList);
        servicesGrid.setAdapter(servicesPageGridAdapter);
        servicesGrid.setOnItemClickListener((adapterView, view, position, id) -> Toast.makeText(ClientActivity.this, "" + position, Toast.LENGTH_SHORT).show());

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
                clientBinding.welcomeText.setText("Good Morning " + me.getUsername());
            }
        });


        addDummyServices();
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
        addLogoutListener(ClientActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void showSearch(TextView tv, SearchView search) {
        tv.setVisibility(View.GONE);
        search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void hideSearch(TextView tv, SearchView search) {
        tv.setVisibility(View.VISIBLE);
        search.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void addDummyServices() {
        Domain.Services services = new Domain.Services("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmedia2.s-nbcnews.com%2Fi%2Fnewscms%2F2016_10%2F1002361%2Fcleaning-products-stock-today-160307-tease_4097ed238bc46047a15831a86dd47267.jpg&f=1&nofb=1", "Cleaning");
        for (int i = 0; i <= 12; i++) {
            serviceList.add(services);
            updateList();
        }
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