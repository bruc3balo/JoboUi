package com.example.joboui.model;


import static com.example.joboui.globals.GlobalVariables.HY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Models{

    public static class NewUserForm {

        private String names;

        private String username;

        private String emailAddress;

        private String password;

        private String phone_number = HY;

        private String id_number = HY;

        private String bio = HY;

        private Map<String, String> preferred_working_hours = new HashMap<>();

        private List<String> specialities = new ArrayList<>();

        private String role;


        public NewUserForm() {

        }

        public NewUserForm(String names, String username, String emailAddress, String password, String phone_number) {
            this.names = names;
            this.username = username;
            this.emailAddress = emailAddress;
            this.password = password;
            this.phone_number = phone_number;
        }


        public NewUserForm(String names, String username, String emailAddress, String password, String phone_number, String role) {
            this.names = names;
            this.username = username;
            this.emailAddress = emailAddress;
            this.password = password;
            this.phone_number = phone_number;
            this.role = role;
        }

        public NewUserForm(String names, String username, String emailAddress, String password, String phone_number, String id_number, String bio, Map<String, String> preferred_working_hours, List<String> specialities, String role) {
            this.names = names;
            this.username = username;
            this.emailAddress = emailAddress;
            this.password = password;
            this.phone_number = phone_number;
            this.id_number = id_number;
            this.bio = bio;
            this.preferred_working_hours = preferred_working_hours;
            this.specialities = specialities;
            this.role = role;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getId_number() {
            return id_number;
        }

        public void setId_number(String id_number) {
            this.id_number = id_number;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public Map<String, String> getPreferred_working_hours() {
            return preferred_working_hours;
        }

        public void setPreferred_working_hours(Map<String, String> preferred_working_hours) {
            this.preferred_working_hours = preferred_working_hours;
        }

        public List<String> getSpecialities() {
            return specialities;
        }

        public void setSpecialities(List<String> specialities) {
            this.specialities = specialities;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class RoleCreationForm {


        private String name;

        private Set<String> permissions = new LinkedHashSet<>();

        public RoleCreationForm() {

        }

        public RoleCreationForm(String name, Set<String> permissions) {
            this.name = name;
            this.permissions = permissions;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(Set<String> permissions) {
            this.permissions = permissions;
        }
    }

    public static class RoleToUserForm {

        private String username;

        private String role_name;

        public RoleToUserForm() {

        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole_name() {
            return role_name;
        }

        public void setRole_name(String role_name) {
            this.role_name = role_name;
        }
    }

    public static class UsernameAndPasswordAuthenticationRequest {
        private String username;
        private String password;

        public UsernameAndPasswordAuthenticationRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public UsernameAndPasswordAuthenticationRequest() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class UserUpdateForm {
        private String names;

        private String email_address;

        private String password;

        private String role;

        private String phone_number;

        private String id_number;

        private String bio;

        private Map<String, String> preferred_working_hours;

        private List<String> specialities;

        public UserUpdateForm() {

        }

        public UserUpdateForm(String names, String email_address, String password, String role, String phone_number, String id_number, String bio, Map<String, String> preferred_working_hours, List<String> specialities) {
            this.names = names;
            this.email_address = email_address;
            this.password = password;
            this.role = role;
            this.phone_number = phone_number;
            this.id_number = id_number;
            this.bio = bio;
            this.preferred_working_hours = preferred_working_hours;
            this.specialities = specialities;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        public String getEmail_address() {
            return email_address;
        }

        public void setEmail_address(String email_address) {
            this.email_address = email_address;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getId_number() {
            return id_number;
        }

        public void setId_number(String id_number) {
            this.id_number = id_number;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public Map<String, String> getPreferred_working_hours() {
            return preferred_working_hours;
        }

        public void setPreferred_working_hours(Map<String, String> preferred_working_hours) {
            this.preferred_working_hours = preferred_working_hours;
        }

        public List<String> getSpecialities() {
            return specialities;
        }

        public void setSpecialities(List<String> specialities) {
            this.specialities = specialities;
        }
    }

}