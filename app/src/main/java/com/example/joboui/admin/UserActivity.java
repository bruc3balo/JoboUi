package com.example.joboui.admin;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.UserRvAdapter;
import com.example.joboui.databinding.ActivityUserBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.utils.AppRolesEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;
    private UserRvAdapter userRvAdapter;
    private ArrayAdapter<String> adapter;
    private final LinkedList<String> roleList = new LinkedList<>();
    private final LinkedList<Models.AppUser> allUserList = new LinkedList<>();
    private final LinkedList<Models.AppUser> userList = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        getRoles();

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());


        AppCompatSpinner roleSpinner = binding.roleSpinner;
        adapter = new ArrayAdapter<>(UserActivity.this, android.R.layout.simple_list_item_1, roleList);
        roleSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UserActivity.this, roleList.get(position), Toast.LENGTH_SHORT).show();
                filterUsers(binding.roleSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        userRvAdapter = new UserRvAdapter(this, userList);
        RecyclerView usersRv = binding.usersRv;
        usersRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        usersRv.setAdapter(userRvAdapter);

        userRepository.getUserLive().observe(this, appUser -> {
            if (!appUser.isPresent()) {
                Toast.makeText(UserActivity.this, "Failed to get user info", Toast.LENGTH_SHORT).show();
                return;
            }

            getUsers();
        });
    }


    public void getUsers() {
        new ViewModelProvider(this).get(UserViewModel.class).getLiveAllUsers().observe(this, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(UserActivity.this, "No users", Toast.LENGTH_SHORT).show();
                return;
            }

            allUserList.clear();
            try {
                JsonArray serviceArray = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.get().getData()));

                for (int i = 0; i < serviceArray.size(); i++) {

                    try {
                        System.out.println("count " + i);
                        Models.AppUser appUser = getObjectMapper().readValue(new JsonObject(serviceArray.getJsonObject(i).getMap()).toString(), Models.AppUser.class);
                        allUserList.add(appUser);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                }

                filterUsers(binding.roleSpinner.getSelectedItem().toString());

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

    }

    private void getRoles() {
        roleList.clear();
        for (AppRolesEnum role : AppRolesEnum.values()) {
            roleList.add(role.name());
        }
    }

    private void filterUsers(String role) {
        userList.clear();
        adapter.notifyDataSetChanged();
        userRvAdapter.notifyDataSetChanged();
        allUserList.forEach(p -> {
            if (p.getRole().getName().equals(role)) {
                userList.add(p);
                if (!userList.isEmpty()) {
                    userRvAdapter.notifyItemInserted(userList.size() - 1);
                }
            }
        });
    }
}