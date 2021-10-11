package com.example.joboui.model;


import static com.example.joboui.globals.GlobalVariables.HY;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Models {

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

    public static class LoginResponse implements Serializable {
        private String access_token;
        private String refresh_token;
        private String auth_type;

        public LoginResponse() {
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getAuth_type() {
            return auth_type;
        }

        public void setAuth_type(String auth_type) {
            this.auth_type = auth_type;
        }
    }

    public static class AppUser implements Serializable{

        private Long id;

        private String names;


        private String username;


        private String id_number;


        private String email_address;

        private String phone_number;


        private String password;


        private String bio;


        private String last_known_location;


        private Date created_at;


        private Date updated_at;


        private AppRole role;


        private String preferred_working_hours;


        private String specialities;


        private Boolean disabled;


        private Boolean deleted;

        public AppUser() {

        }

        public AppUser(Long id, String names, String username, String id_number, String email_address, String phone_number, String password, String bio, String last_known_location, Date created_at, Date updated_at, AppRole role, String preferred_working_hours, String specialities, Boolean disabled, Boolean deleted) {
            this.id = id;
            this.names = names;
            this.username = username;
            this.id_number = id_number;
            this.email_address = email_address;
            this.phone_number = phone_number;
            this.password = password;
            this.bio = bio;
            this.last_known_location = last_known_location;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.role = role;
            this.preferred_working_hours = preferred_working_hours;
            this.specialities = specialities;
            this.disabled = disabled;
            this.deleted = deleted;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getId_number() {
            return id_number;
        }

        public void setId_number(String id_number) {
            this.id_number = id_number;
        }

        public String getEmail_address() {
            return email_address;
        }

        public void setEmail_address(String email_address) {
            this.email_address = email_address;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getLast_known_location() {
            return last_known_location;
        }

        public void setLast_known_location(String last_known_location) {
            this.last_known_location = last_known_location;
        }

        public Date getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Date created_at) {
            this.created_at = created_at;
        }

        public Date getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(Date updated_at) {
            this.updated_at = updated_at;
        }

        public AppRole getRole() {
            return role;
        }

        public void setRole(AppRole role) {
            this.role = role;
        }

        public String getPreferred_working_hours() {
            return preferred_working_hours;
        }

        public void setPreferred_working_hours(String preferred_working_hours) {
            this.preferred_working_hours = preferred_working_hours;
        }

        public String getSpecialities() {
            return specialities;
        }

        public void setSpecialities(String specialities) {
            this.specialities = specialities;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }
    }

    public static class AppRole implements Serializable{

        private Long id;
        private String name;
        private Set<Permissions> permissions = new LinkedHashSet<>();

        public AppRole() {

        }

        public AppRole(String name) {
            this.name = name;
        }

        public AppRole(Long id, String name, Set<Permissions> permissions) {
            this.id = id;
            this.name = name;
            this.permissions = permissions;
        }

        public AppRole(String name, Set<Permissions> permissions) {
            this.name = name;
            this.permissions = permissions;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<Permissions> getPermissions() {
            return permissions;
        }

        public void setPermissions(Set<Permissions> permissions) {
            this.permissions = permissions;
        }
    }

    public static class Permissions implements Serializable{

        private Long id;
        private String name;

        public Permissions() {
        }

        public Permissions(String name) {
            this.name = name;
        }

        public Permissions(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}