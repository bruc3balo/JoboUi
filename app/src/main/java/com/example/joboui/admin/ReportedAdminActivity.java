package com.example.joboui.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.joboui.databinding.ActivityReportedAdminBinding;
import com.example.joboui.model.Models;

import java.util.LinkedList;

public class ReportedAdminActivity extends AppCompatActivity {

    private ActivityReportedAdminBinding binding;
    //private LinkedList<Models>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportedAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView rv = binding.reportsRv;
        rv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
       // rv.setAdapter();
    }
}