package com.example.joboui.models;

import android.graphics.drawable.Drawable;

public class Models {
    public static final String CLIENT = "Client";
    public static final String SERVICE_PROVIDER = "Service Provider";
    public static final String ROLE = "role";

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
