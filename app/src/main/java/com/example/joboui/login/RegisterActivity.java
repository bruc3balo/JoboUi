package com.example.joboui.login;

import static com.example.joboui.SplashScreen.directToLogin;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.PASSWORD;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.editSp;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.joboui.R;
import com.example.joboui.databinding.ActivityRegisterBinding;
import com.example.joboui.databinding.RoleDialogBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.tutorial.TutorialActivity;
import com.example.joboui.utils.AppRolesEnum;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonObject;

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
        createUser(newUserForm).observe(this, success -> {
            if (success) {
                System.out.println("=========== LOGIN =============");
                try {
                    userViewModel.getAccessToken(new Models.UsernameAndPasswordAuthenticationRequest(newUserForm.getUsername(), newUserForm.getPassword())).observe(RegisterActivity.this, map -> {
                        if (map == null) {
                            hidePb();
                        } else {
                            if (map.isEmpty()) {
                                hidePb();
                                Toast.makeText(RegisterActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                            } else {
                               saveUserDetails(map.get(USERNAME));
                            }
                        }
                    });
                } catch (JSONException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("=========== NO LOGIN ===========");
            }

        });
    }

    private void saveUserDetails (String username) {
        userViewModel.getUserByUsername(username).observe(RegisterActivity.this, appUser -> {
            if (appUser != null) {
                System.out.println("=================================  SUCCESS LOGIN  ======================================");
                Toast.makeText(RegisterActivity.this, appUser.getUsername(), Toast.LENGTH_SHORT).show();
                if (appUser.getRole().getName().equals("ROLE_SERVICE_PROVIDER")) {
                    goToAdditionalInfoActivity();
                } else {
                    goToTutorialsPage();
                }
            } else {
                directToLogin(RegisterActivity.this);
                //todo save tutorial && route to additional info
                System.out.println("=================================  FAIL LOGIN  ======================================");
            }
            hidePb();
        });
    }

    private MutableLiveData<Boolean> createUser(Models.NewUserForm form) throws JSONException, JsonProcessingException {
        Toast.makeText(this, "Creating user "+form.getUsername(), Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = API_URL + CONTEXT_URL + "/user/save";
        ObjectMapper mapper = getObjectMapper();

        String data = mapper.writeValueAsString(form);
        JSONObject object = new JSONObject(data);

        MutableLiveData<Boolean> success = new MutableLiveData<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, response -> {

            try {
                JsonResponse jsonResponse = mapper.readValue(response.toString(), JsonResponse.class);
                JsonObject userJson = new JsonObject(mapper.writeValueAsString(jsonResponse.getData()));
                System.out.println("NEW USER : " + userJson);
                Models.AppUser user = createdUser = mapper.readValue(userJson.toString(), Models.AppUser.class);
                //userRepository.insert(new Domain.User(user.getId(), user.getId_number(), user.getPhone_number(), user.getBio(), user.getEmail_address(), user.getNames(), user.getUsername(), user.getRole().getName(), user.getCreated_at().toString(), user.getUpdated_at().toString(), user.getDeleted(), user.getDisabled(), user.getSpecialities(), user.getPreferred_working_hours(), user.getLast_known_location(), user.getPassword()));
                success.setValue(true);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show();
                System.out.println("====================== Error creating account =======================");
                hidePb();
                success.setValue(false);

            }

        }, error -> {
            hidePb();
            System.out.println("============================ ERROR SENDING CREATE REQUEST " + error.getMessage() + "==============================");
            Toast.makeText(this, "Failed to create user " + newUserForm.getUsername(), Toast.LENGTH_SHORT).show();
            success.setValue(false);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put(CONTENT_TYPE, APPLICATION_JSON);
                return header;
            }
        };

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);

        return success;
    }

    private void showPb() {
        activityRegister.registerUserButton.setEnabled(false);
        activityRegister.registerPb.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
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