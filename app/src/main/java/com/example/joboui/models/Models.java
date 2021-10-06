package com.example.joboui.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.joboui.globals.GlobalVariables;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Models {

    @Entity(tableName = GlobalVariables.USER_DB)
    public static class User implements Serializable {

        @PrimaryKey
        @NotNull
        private String uid;
        private String idNumber;
        private String phoneNumber;
        private String bio;
        private String location;
        private String emailAddress;
        private String firstName;
        private String lastName;
        private int role;
        private String createdAt;
        private String updatedAt;


        public User() {
        }

        @Ignore
        public User(@NotNull String uid) {
            this.uid = uid;
        }

        @Ignore
        public User(@NotNull String uid, String idNumber, String phoneNumber, String bio, String location, String emailAddress, String firstName, String lastName, int role, String createdAt, String updatedAt) {
            this.uid = uid;
            this.idNumber = idNumber;
            this.phoneNumber = phoneNumber;
            this.bio = bio;
            this.location = location;
            this.emailAddress = emailAddress;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        @NonNull
        public String getUid() {
            return uid;
        }

        public void setUid(@NonNull String uid) {
            this.uid = uid;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
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

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
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
