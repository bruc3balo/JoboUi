package com.example.joboui.login;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.HY;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityRegisterBinding;
import com.example.joboui.databinding.RoleDialogBinding;
import com.example.joboui.model.Models;
import com.example.joboui.tutorial.TutorialActivity;
import com.example.joboui.utils.AppRolesEnum;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegister;
    private String role = HY;
    private final ArrayList<String> phoneNumberList = new ArrayList<>();
    private Models.NewUserForm newUserForm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegister = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegister.getRoot());

        Button registerUserButton = activityRegister.registerUserButton;

        EditText namesField = activityRegister.namesField;
        EditText userNameField = activityRegister.usernameField;
        EditText emailAddressField = activityRegister.emailAddressField;
        EditText passwordF = activityRegister.passwordF;
        EditText cPasswordF = activityRegister.cPasswordF;
        EditText phoneNumberField = activityRegister.phoneNumberField;

        registerUserButton.setOnClickListener(view -> {
            if (validateForm(namesField, userNameField,emailAddressField, passwordF, cPasswordF, phoneNumberField)) {
                showRoleDialog();
            }
        });


        setWindowColors();

        populatePhoneNumberList();

        populateUserNamesList();

        hidePb();
    }

    private boolean validateForm(EditText namesField, EditText usernameField,EditText emailAddressField, EditText passwordF, EditText cPasswordF, EditText phoneNumberField) {
        boolean valid = false;

        if (namesField.getText().toString().isEmpty()) {
            namesField.setError("required");
            namesField.requestFocus();
        } else if (emailAddressField.getText().toString().isEmpty()) {
            emailAddressField.setError("required");
            emailAddressField.requestFocus();
        } else if (!emailAddressField.getText().toString().contains("@")) {
            emailAddressField.setError("Invalid email format");
            passwordF.requestFocus();
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
            phoneNumberField.requestFocus();
        } else if (phoneNumberField.getText().toString().length() < 12) {
            phoneNumberField.setError("Invalid phone number");
            phoneNumberField.requestFocus();
        } else if (phoneNumberList.contains(phoneNumberField.getText().toString())) {
            phoneNumberField.setError("Already added. Sign in");
            phoneNumberField.requestFocus();
        } else {
            //initPhoneVerification(phoneNumberField.getText().toString());String names, String username, String emailAddress, String password, String phone_number
            newUserForm = new Models.NewUserForm(namesField.getText().toString(),usernameField.getText().toString(),emailAddressField.getText().toString(),cPasswordF.getText().toString(),phoneNumberField.getText().toString());
            valid = true;
        }

        return valid;
    }

    private void populatePhoneNumberList() {

    }

    private void populateUserNamesList() {

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
                sendRegisterRequest();
            }
        });
        neverMindButton.setOnClickListener(view -> d.dismiss());
    }

    private void sendRegisterRequest() {
        Toast.makeText(this, "Sending request", Toast.LENGTH_SHORT).show();
        showPb();
        new Handler().postDelayed(this::hidePb,2500);
    }

    private void showPb () {
        activityRegister.registerUserButton.setEnabled(false);
        activityRegister.registerPb.setVisibility(View.VISIBLE);
    }

    private void hidePb () {
        activityRegister.registerPb.setVisibility(View.GONE);
        activityRegister.registerUserButton.setEnabled(true);
    }

    private void goToAdditionalInfoActivity() {
        startActivity(new Intent(this, ServiceProviderAdditionalActivity.class));
    }

    private void goToTutorialsPage() {
        startActivity(new Intent(this, TutorialActivity.class));
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.deep_purple));
        getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
    }
}