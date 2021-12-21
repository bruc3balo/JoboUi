package com.example.joboui.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.joboui.globals.GlobalVariables;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Domain {

    @Entity(tableName = GlobalVariables.USER_DB)
    public static class User implements Serializable {

        @PrimaryKey
        @NotNull
        private Long id;
        private String id_number;
        private String phone_number;
        private String bio;
        private String email_address;
        private String names;
        private String username;
        private String role;
        private String created_at;
        private String updated_at;
        private boolean is_deleted;
        private boolean is_disabled;
        private boolean verified;
        private String specialities;
        private String preferred_working_hours;
        private String last_known_location;
        private String password;
        private float rating;



        public User() {
        }

        public User(@NotNull Long id, String id_number, String phone_number, String bio, String email_address, String names, String username, String role, String createdAt, String updatedAt, boolean is_deleted, boolean is_disabled, boolean verified, String specialities, String preferred_working_hours, String last_known_location, String password,float rating) {
            this.id = id;
            this.id_number = id_number;
            this.phone_number = phone_number;
            this.bio = bio;
            this.email_address = email_address;
            this.names = names;
            this.username = username;
            this.role = role;
            this.created_at = createdAt;
            this.updated_at = updatedAt;
            this.is_deleted = is_deleted;
            this.is_disabled = is_disabled;
            this.verified = verified;
            this.specialities = specialities;
            this.preferred_working_hours = preferred_working_hours;
            this.last_known_location = last_known_location;
            this.password = password;
            this.rating = rating;
        }



        @NonNull
        public Long getId() {
            return id;
        }

        public void setId(@NonNull Long id) {
            this.id = id;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
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

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
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

    //todo entity
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

    @Entity(tableName = GlobalVariables.SERVICE_DB)
    public static class Services implements Serializable{
        @PrimaryKey
        @NotNull
        private Long id;
        private String name;
        private String description;
        private Boolean disabled;
        private String created_at;
        private String updated_at;

        public Services(@NotNull Long id, String name, String description, Boolean disabled, String created_at, String updated_at) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.disabled = disabled;
            this.created_at = created_at;
            this.updated_at = updated_at;
        }

        @Ignore
        public Services(String name) {
            this.name = name;
        }

        @Ignore
        public Services() {

        }

        @NonNull
        public Long getId() {
            return id;
        }

        public void setId(@NonNull Long id) {
            this.id = id;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
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
    }

}
