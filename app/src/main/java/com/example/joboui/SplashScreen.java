package com.example.joboui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.joboui.databinding.ActivitySplashScreenBinding;
import com.example.joboui.globals.GlobalDb;
import com.example.joboui.login.LoginActivity;

import org.jetbrains.annotations.NotNull;

public class SplashScreen extends AppCompatActivity {

    private boolean goneToLogin;
    public static final int SPLASH_WAIT_TIME = 2000;
    private boolean original = true;
    private int count = 0;
    private int locationAskCount = 0;
    private int storageAskCount = 0;
    private ActivitySplashScreenBinding screenBinding;
    private boolean locationGranted = false;
    private boolean storageGranted = false;

    public static final int ERROR_DIALOG_REQUEST = 101;
    public static final int GPS_PERMISSION_CODE = 10;
    public static final int LOCATION_PERMISSION_CODE = 12;
    public static final int STORAGE_PERMISSION_CODE = 11;

    private final ActivityResultLauncher<Intent> gpsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            // Handle the Intent
            System.out.println("app result received");
            askPermissions();
        }
    });
    private final ActivityResultLauncher<Intent> settingsPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            // Handle the Intent
            System.out.println("app result received");
            askPermissions();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(screenBinding.getRoot());

        setWindowColors();

        goneToLogin = false;

        GlobalDb.init(getApplication());

    }

    private void setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple));
        }

    }


    private void askPermissions() {
        System.out.println("app asking permission");
        if (!locationGranted) {
            getLocationPermission();
        } else if (!storageGranted) {
            getStoragePermission();
        } else if (!isMapsEnabled()) {
            getGpsPermission();
        } else if (isMapsEnabled()) {
            System.out.println("app gps permission granted");
            goToLogin();
        } else {
            goToLogin();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        askPermissions();
    }

    @SuppressLint("SetTextI18n")
    private void getLocationPermission() {
        System.out.println("app location permission");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //check if location is allowed
            Dialog d = new Dialog(this);
            d.setContentView(R.layout.new_info_layout);
            d.getWindow().setBackgroundDrawableResource(R.color.transparent);
            TextView infoTv = d.findViewById(R.id.newInfoTv);
            if (locationAskCount > 1) {
                infoTv.setText("Location access required. \n You Will not be able to continue. Allow them manually");
            } else {
                infoTv.setText("Location access required to get protect you and connect you with jobs");
            }
            Button dismiss = d.findViewById(R.id.dismissButton);
            dismiss.setOnClickListener(v -> d.dismiss());
            if (locationAskCount > 1) {
                showOpenPermissionsDialog(this, "Location access required. \n You Will not be able to continue. Allow them manually");
            } else {
                d.setOnDismissListener(dialog -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE)); //when dialog dismissed
            }
            d.show();
        } else {
            locationGranted = true;
            System.out.println("app location granted");
            askPermissions();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getStoragePermission() {
        System.out.println("app storage permission");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Dialog d = new Dialog(this);
            d.setContentView(R.layout.new_info_layout);
            d.getWindow().setBackgroundDrawableResource(R.color.transparent);
            TextView infoTv = d.findViewById(R.id.newInfoTv);
            if (storageAskCount > 1) {
                infoTv.setText("Location access required. \n You Will not be able to continue. Allow them manually");
            } else {
                infoTv.setText("Storage access required to allow uploading of profile picture");
            }
            Button dismiss = d.findViewById(R.id.dismissButton);
            dismiss.setOnClickListener(v -> d.dismiss());
            if (storageAskCount > 1) {
                showOpenPermissionsDialog(this, "Location access required. \n You Will not be able to continue. Allow them manually");
            } else {
                d.setOnDismissListener(dialog -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE));
            }
            d.show();
        } else {
            storageGranted = true;
            System.out.println("app storage permission granted");
            askPermissions();
        }
    }

    private boolean isMapsEnabled() {
        System.out.println("app gps permission");
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void getGpsResult() {
        Intent enableGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        gpsLauncher.launch(enableGps);
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        settingsPermission.launch(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                locationGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                askPermissions();
                locationAskCount++;

                break;

            case STORAGE_PERMISSION_CODE:

                storageGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                askPermissions();
                storageAskCount++;
                break;
        }
    }


    private void showOpenPermissionsDialog(Context context, String s) {
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.new_info_layout);
        d.getWindow().setBackgroundDrawableResource(R.color.transparent);
        TextView infoTv = d.findViewById(R.id.newInfoTv);
        infoTv.setText(s);
        Button dismiss = d.findViewById(R.id.dismissButton);
        dismiss.setOnClickListener(v -> d.dismiss());
        d.setOnDismissListener(dialog -> openSettings());
        d.show();
    }

    @SuppressLint("SetTextI18n")
    private void getGpsPermission() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.new_info_layout);
        d.getWindow().setBackgroundDrawableResource(R.color.transparent);
        TextView infoTv = d.findViewById(R.id.newInfoTv);
        infoTv.setText("This application requires GPS to work properly, you need enable it. Flip the switch");
        Button dismiss = d.findViewById(R.id.dismissButton);
        dismiss.setOnClickListener(v -> d.dismiss());
        d.setOnDismissListener(dialog -> getGpsResult());
        d.show();

    }


    private void goToLogin() {
        if (!goneToLogin) {
            goneToLogin = true;
            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        } else {
            System.out.println("Login already started");
        }
    }

    private void hideNavBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}