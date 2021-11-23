package com.example.joboui.clientUi;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.directToLogin;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.adapters.ServicesPageGrid.allServiceList;
import static com.example.joboui.adapters.ServicesPageGrid.serviceList;
import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.LOGGED_IN;
import static com.example.joboui.globals.GlobalVariables.SERVICE_DB;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.clearSp;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;
import static com.example.joboui.utils.SimilarityClass.alike;
import static com.example.joboui.utils.SimilarityClass.printSimilarity;
import static com.example.joboui.utils.SimilarityClass.similarity;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.joboui.R;
import com.example.joboui.adapters.ServicesPageGrid;
import com.example.joboui.databinding.ActivityClientBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.login.ServiceProviderAdditionalActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ClientActivity extends AppCompatActivity {
    private ActivityClientBinding clientBinding;
    private ServicesPageGrid servicesPageGridAdapter;
    private Domain.User me;
    private DrawerLayout drawerLayout;
    private boolean backPressed = false;


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
            Optional<Domain.Services> service = servicesPageGridAdapter.getItem(position);
            bundle.putSerializable(SERVICE_DB, service.orElse(null));
            startActivity(new Intent(ClientActivity.this, ServiceRequestActivity.class).putExtras(bundle));
        });

        SearchView clientSearch = clientBinding.clientSearch;
        clientSearch.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showSearch(clientBinding.introText, clientSearch);
            } else {
                hideSearch(clientBinding.introText, clientSearch);
                serviceList.clear();
                serviceList.putAll(allServiceList);
                servicesPageGridAdapter.notifyDataSetChanged();
            }
        });

        clientSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchServices(newText);
                return false;
            }
        });


        drawerLayout = clientBinding.getRoot();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        NavigationView clientDrawer = clientBinding.clientNavigation;

        ImageView openDrawer = clientBinding.openDrawer;
        openDrawer.setOnClickListener(v -> openDrawer(drawerLayout));

        View view = clientDrawer.getHeaderView(0);

        clientDrawer.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                default:
                case R.id.myJobs:
                    closeDrawer(drawerLayout);
                    goToMyJobs();
                    break;

            }

            return false;
        });

        setWindowColors();

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, user -> {
            if (user.isPresent()) {
                me = user.get();
                clientBinding.welcomeText.setText(getWelcomeGreeting(me.getUsername()));
                TextView username = view.findViewById(R.id.username);
                username.setText(me.getUsername());
                TextView email = view.findViewById(R.id.emailAddress);
                email.setText(me.getEmail_address());
            }
        });
        }

    }

    private void searchServices(String query) {
        if (query.isEmpty()) {
            serviceList.clear();
            serviceList.putAll(allServiceList);
            servicesPageGridAdapter.notifyDataSetChanged();
        } else {
            serviceList.clear();
            servicesPageGridAdapter.notifyDataSetChanged();
            allServiceList.forEach((position, services) -> {
                if (alike(query,services.getName())) {
                    serviceList.put(position, services);
                    servicesPageGridAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void goToMyJobs() {
        startActivity(new Intent(ClientActivity.this, MyJobs.class));
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
    public void onBackPressed() {
        if (isDrawerOpen(clientBinding.getRoot())) {
            closeDrawer(clientBinding.getRoot());
        } else {
            if (!backPressed) {
                backPressed = true;
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
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

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.openDrawer(GravityCompat.START);
    }

    public static boolean isDrawerOpen(DrawerLayout drawerLayout) {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }


}