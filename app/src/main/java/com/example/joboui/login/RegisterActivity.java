package com.example.joboui.login;

import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.PASSWORD;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.LoginActivity.proceed;
import static com.example.joboui.login.SignInActivity.getSp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.joboui.R;
import com.example.joboui.databinding.ActivityRegisterBinding;
import com.example.joboui.databinding.RoleDialogBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.tutorial.VerificationActivity;
import com.example.joboui.utils.AppRolesEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegister;
    private String role = HY;
    private final ArrayList<String> phoneNumberList = new ArrayList<>();
    private final ArrayList<String> usernameList = new ArrayList<>();
    private Models.NewUserForm newUserForm;
    UserViewModel userViewModel;
    private Models.AppUser createdUser = new Models.AppUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        activityRegister = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegister.getRoot());

        Button registerUserButton = activityRegister.registerUserButton;

        EditText namesField = activityRegister.namesField;
        EditText userNameField = activityRegister.usernameField;
        EditText emailAddressField = activityRegister.emailAddressField;
        EditText passwordF = activityRegister.passwordF;
        EditText cPasswordF = activityRegister.cPasswordF;
        EditText phoneNumberField = activityRegister.phoneNumberField;

        userNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!userNameField.getText().toString().isEmpty()) {
                    if (usernameList.contains(userNameField.getText().toString())) {
                        userNameField.setError("Username is taken");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phoneNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!phoneNumberField.getText().toString().isEmpty()) {
                    if (phoneNumberList.contains(phoneNumberField.getText().toString())) {
                        phoneNumberField.setError("Number is used");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerUserButton.setOnClickListener(view -> {
            if (validateForm(namesField, userNameField, emailAddressField, passwordF, cPasswordF, phoneNumberField)) {
                showRoleDialog();
            }
        });

        setWindowColors();

        populatePhoneNumberList();

        populateUserNamesList();

        hidePb();
    }

    private boolean validateForm(EditText namesField, EditText usernameField, EditText emailAddressField, EditText passwordF, EditText cPasswordF, EditText phoneNumberField) {
        boolean valid = false;

        if (namesField.getText().toString().isEmpty()) {
            namesField.setError("required");
            namesField.requestFocus();
        } else if (usernameField.getText().toString().isEmpty()) {
            usernameField.setError("required");
            usernameField.requestFocus();
        } else if (usernameList.contains(usernameField.getText().toString())) {
            usernameField.setError("username is taken");
            usernameField.requestFocus();
        } else if (usernameField.getText().toString().contains("-")) {
            usernameField.setError("- not permitted");
            usernameField.requestFocus();
        } else if (emailAddressField.getText().toString().isEmpty()) {
            emailAddressField.setError("required");
            emailAddressField.requestFocus();
        } else if (!emailAddressField.getText().toString().contains("@")) {
            emailAddressField.setError("Invalid email format");
            emailAddressField.requestFocus();
        } else if (passwordF.getText().toString().length() < 6) {
            passwordF.setError("Min characters 6");
            passwordF.requestFocus();
        } else if (!cPasswordF.getText().toString().equals(passwordF.getText().toString())) {
            cPasswordF.setError("Passwords don't match");
            cPasswordF.requestFocus();
        } else if (phoneNumberField.getText().toString().isEmpty()) {
            phoneNumberField.setError("Required");
            phoneNumberField.requestFocus();
        } else if (!phoneNumberField.getText().toString().startsWith("+254")) {
            phoneNumberField.setError("Must start with +254");
            phoneNumberField.setText("+254");
            phoneNumberField.setSelection(4);
            phoneNumberField.requestFocus();
        } else if (phoneNumberField.getText().toString().length() < 12) {
            phoneNumberField.setError("Invalid phone number");
            phoneNumberField.requestFocus();
        } else if (phoneNumberList.contains(phoneNumberField.getText().toString()) || phoneNumberList.contains(phoneNumberField.getText().toString().replace("+", ""))) {
            phoneNumberField.setError("Phone number already added");
            phoneNumberField.requestFocus();
        } else {
            //initPhoneVerification(phoneNumberField.getText().toString());String names, String username, String emailAddress, String password, String phone_number
            newUserForm = new Models.NewUserForm(namesField.getText().toString(), usernameField.getText().toString(), emailAddressField.getText().toString(), cPasswordF.getText().toString(), phoneNumberField.getText().toString());
            valid = true;
        }

        return valid;
    }

    private void populatePhoneNumberList() {
        userViewModel.getAllPhoneNumbers().observe(this, numbers -> {
            phoneNumberList.clear();
            phoneNumberList.addAll(numbers);
            System.out.println("NUMBERS " + phoneNumberList.toString());
        });
    }

    private void populateUserNamesList() {
        userViewModel.getAllUsernames().observe(this, names -> {
            usernameList.clear();
            usernameList.addAll(names);
            System.out.println("NAMES " + usernameList.toString());
        });
    }

    private void showRoleDialog() {
        Dialog d = new Dialog(RegisterActivity.this);
        RoleDialogBinding roleDialogBinding = RoleDialogBinding.inflate(getLayoutInflater());
        d.setContentView(roleDialogBinding.getRoot());
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button confirmRoleButton = roleDialogBinding.confirmRoleButton;
        RadioGroup roleRadioGroup = roleDialogBinding.roleRadioGroup;
        Button neverMindButton = roleDialogBinding.cancelButton;

        d.show();

        roleRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == roleDialogBinding.clientRadio.getId()) {
                role = AppRolesEnum.valueOf("ROLE_CLIENT").name();
            } else {
                role = AppRolesEnum.valueOf("ROLE_SERVICE_PROVIDER").name();
            }
            newUserForm.setRole(role);
        });

        confirmRoleButton.setOnClickListener(view -> {
            if (role.equals(HY)) {
                Toast.makeText(RegisterActivity.this, "Pick a role", Toast.LENGTH_SHORT).show();
            } else {
                d.dismiss();
                try {
                    sendRegisterRequest();
                } catch (JSONException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        neverMindButton.setOnClickListener(view -> d.dismiss());
    }

    private void sendRegisterRequest() throws JSONException, JsonProcessingException {
        Toast.makeText(this, "Sending request", Toast.LENGTH_SHORT).show();
        showPb();
        userViewModel.createNewUser(newUserForm).observe(this, user -> {
            if (user.isPresent()) {
                System.out.println("ACCOUNT CREATION SUCCESS");
                logInNewUser();
            } else {
                hidePb();
                Toast.makeText(RegisterActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
            }
        });
        if (!this.isDestroyed()) {
            new Handler().postDelayed(this::hidePb, 8000);
        }
    }


    private void logInNewUser() {
        try {
            Thread.sleep(2000);

            Map<String, ?> map = getSp(USER_DB, getApplication());
            Models.UsernameAndPasswordAuthenticationRequest request = new Models.UsernameAndPasswordAuthenticationRequest(Objects.requireNonNull(map.get(USERNAME)).toString(), Objects.requireNonNull(map.get(PASSWORD)).toString());
            userViewModel.getAccessToken(new Models.UsernameAndPasswordAuthenticationRequest(request.getUsername(), request.getPassword())).observe(RegisterActivity.this, loginResponse -> {
                if (loginResponse.isPresent()) {
                    System.out.println("=================================  SUCCESS LOGIN  ======================================");
                    Toast.makeText(RegisterActivity.this, request.getUsername(), Toast.LENGTH_SHORT).show();
                    System.out.println("============== SUCCESSFUL LOGIN ==================");
                    hidePb();
                    proceed(RegisterActivity.this);
                } else {
                    hidePb();
                    Toast.makeText(RegisterActivity.this, "Failed to sign you in. Sign in to continue", Toast.LENGTH_SHORT).show();
                    finish();
                    System.out.println("No login response for " + request.getUsername());
                    //todo redirect to sign in page
                }
            });
        } catch (JSONException | JsonProcessingException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Failed to get access token. Try logging in", Toast.LENGTH_SHORT).show();
            System.out.println("error while getting response ");
        }
    }


    private void showPb() {
        activityRegister.registerUserButton.setEnabled(false);
        activityRegister.registerPb.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
        activityRegister.registerPb.setVisibility(View.GONE);
        activityRegister.registerUserButton.setEnabled(true);
    }

    public static void goToAdditionalInfoActivity(Activity activity) {
        activity.startActivity(new Intent(activity, ServiceProviderAdditionalActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
    }

    public static void goToVerificationPage(Activity activity) {
        activity.startActivity(new Intent(activity, VerificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.deep_purple));
        getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
    }
}