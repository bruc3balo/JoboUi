package com.example.joboui.clientUi;

import static com.example.joboui.globals.GlobalDb.userRepository;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.SplashScreen;
import com.example.joboui.adapters.ServicesPageGrid;
import com.example.joboui.databinding.ActivityClientBinding;
import com.example.joboui.login.LoginActivity;
import com.example.joboui.models.Models;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ClientActivity extends AppCompatActivity {
    ActivityClientBinding clientBinding;
    ServicesPageGrid servicesPageGridAdapter;
    private final ArrayList<Models.Services> serviceList = new ArrayList<>();
    private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> updateUi(firebaseAuth.getCurrentUser());

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

        addDummyServices();
        setWindowColors();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            FirebaseAuth.getInstance().signOut();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
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
        Models.Services services = new Models.Services("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmedia2.s-nbcnews.com%2Fi%2Fnewscms%2F2016_10%2F1002361%2Fcleaning-products-stock-today-160307-tease_4097ed238bc46047a15831a86dd47267.jpg&f=1&nofb=1", "Cleaning");
        for (int i = 0; i <= 12; i++) {
            serviceList.add(services);
            updateList();
        }
    }

    private void updateList() {
        servicesPageGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void updateUi(FirebaseUser user) {
        if (user != null) {

            Models.User localUser = userRepository.getUser(user.getUid());
            if (localUser != null) {
                clientBinding.welcomeText.setText("Good Morning " + localUser.getFirstName());
            }

        } else {
            goToLoginPage();
        }
    }

    private void goToLoginPage() {
        startActivity(new Intent(ClientActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
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
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void removeListener() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);

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