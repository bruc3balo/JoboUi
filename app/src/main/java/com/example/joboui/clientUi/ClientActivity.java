package com.example.joboui.clientUi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.ServicesPageGrid;
import com.example.joboui.databinding.ActivityClientBinding;
import com.example.joboui.models.Models;

import java.util.ArrayList;

public class ClientActivity extends AppCompatActivity {
    ActivityClientBinding clientBinding;
    ServicesPageGrid servicesPageGridAdapter;
    private final ArrayList<Models.Services> serviceList = new ArrayList<>();


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


    private void showSearch(TextView tv, SearchView search) {
        tv.setVisibility(View.GONE);
        search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void hideSearch(TextView tv, SearchView search) {
        tv.setVisibility(View.VISIBLE);
        search.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));;
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