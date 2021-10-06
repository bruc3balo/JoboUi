package com.example.joboui.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.joboui.globals.GlobalVariables;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Domain {

    @Entity(tableName = GlobalVariables.USER_DB)
    public static class User implements Serializable {

        @PrimaryKey
        @NotNull
        private String id;
        private String id_number;
        private String phone_number;
        private String bio;
        private String location;
        private String email_address;
        private String names;
        private String username;
        private String role;
        private String created_at;
        private String updated_at;
        private boolean is_deleted;
        private boolean is_disabled;
        private String specialities;
        private String preferred_working_hours;
        private String last_known_location;
        private String password;

        public User() {
        }

        public User(@NotNull String id, String id_number, String phone_number, String bio, String location, String email_address, String names, String username, String role, String createdAt, String updatedAt, boolean is_deleted, boolean is_disabled, String specialities, String preferred_working_hours, String last_known_location, String password) {
            this.id = id;
            this.id_number = id_number;
            this.phone_number = phone_number;
            this.bio = bio;
            this.location = location;
            this.email_address = email_address;
            this.names = names;
            this.username = username;
            this.role = role;
            this.created_at = createdAt;
            this.updated_at = updatedAt;
            this.is_deleted = is_deleted;
            this.is_disabled = is_disabled;
            this.specialities = specialities;
            this.preferred_working_hours = preferred_working_hours;
            this.last_known_location = last_known_location;
            this.password = password;
        }

        @NonNull
        public String getId() {
            return id;
        }

        public void setId(@NonNull String id) {
            this.id = id;
        }

        public String getId_number() {
            return id_number;
        }

        public void setId_number(String id_number) {
            this.id_number = id_number;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getEmail_address() {
            return email_address;
        }

        public void setEmail_address(String email_address) {
            this.email_address = email_address;
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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public boolean isIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(boolean is_deleted) {
            this.is_deleted = is_deleted;
        }

        public boolean isIs_disabled() {
            return is_disabled;
        }

        public void setIs_disabled(boolean is_disabled) {
            this.is_disabled = is_disabled;
        }

        public String getSpecialities() {
            return specialities;
        }

        public void setSpecialities(String specialities) {
            this.specialities = specialities;
        }

        public String getPreferred_working_hours() {
            return preferred_working_hours;
        }

        public void setPreferred_working_hours(String preferred_working_hours) {
            this.preferred_working_hours = preferred_working_hours;
        }

        public String getLast_known_location() {
            return last_known_location;
        }

        public void setLast_known_location(String last_known_location) {
            this.last_known_location = last_known_location;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class TutorialModel {
        private String imageId;
        private String explanation;
        private String title;

        public TutorialModel(String imageId, String explanation, String title) {
            this.imageId = imageId;
            this.explanation = explanation;
            this.title = title;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class Services {
        private String serviceImageUrl;
        private String serviceTitle;
        private String serviceDescription;

        public Services(String serviceImageUrl, String serviceTitle) {
            this.serviceImageUrl = serviceImageUrl;
            this.serviceTitle = serviceTitle;
        }


        public String getServiceImageUrl() {
            return serviceImageUrl;
        }

        public void setServiceImageUrl(String serviceImageUrl) {
            this.serviceImageUrl = serviceImageUrl;
        }

        public String getServiceTitle() {
            return serviceTitle;
        }

        public void setServiceTitle(String serviceTitle) {
            this.serviceTitle = serviceTitle;
        }

        public String getServiceDescription() {
            return serviceDescription;
        }

        public void setServiceDescription(String serviceDescription) {
            this.serviceDescription = serviceDescription;
        }
    }

}
