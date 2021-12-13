package com.example.joboui.model;


import java.util.HashSet;
import java.util.Set;


public class MpesaModels {

    public static class MpesaCallbackMetadata {


        private Long id;

        private Set<MpesaCallbackMetadataItems> Item = new HashSet<>();

        public MpesaCallbackMetadata() {

        }

        public MpesaCallbackMetadata(Long id, Set<MpesaCallbackMetadataItems> Item) {
            this.id = id;
            this.Item = Item;
        }

        public Long getId() {
            return id;
        }



        public void setId(Long id) {
            this.id = id;
        }

        public Set<MpesaCallbackMetadataItems> getItem() {
            return Item;
        }

        public void setItem(Set<MpesaCallbackMetadataItems> item) {
            this.Item = item;
        }
    }

    public static class MpesaCallbackMetadataItems {

        private Long id;

        private String Name;

        private String Value;

        public MpesaCallbackMetadataItems() {

        }

        public MpesaCallbackMetadataItems(Long id, String name, String value) {
            this.id = id;
            this.Name = name;
            this.Value = value;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            this.Value = value;
        }
    }

    public static class MpesaStkCallback {

        private Long id;

        private String MerchantRequestID;

        private String CheckoutRequestID;

        private int ResultCode;

        private String ResultDesc;

        private MpesaCallbackMetadata callbackMetadata;

        public MpesaStkCallback() {

        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMerchantRequestID() {
            return MerchantRequestID;
        }

        public void setMerchantRequestID(String merchantRequestID) {
            MerchantRequestID = merchantRequestID;
        }

        public String getCheckoutRequestID() {
            return CheckoutRequestID;
        }

        public void setCheckoutRequestID(String checkoutRequestID) {
            CheckoutRequestID = checkoutRequestID;
        }

        public int getResultCode() {
            return ResultCode;
        }

        public void setResultCode(int resultCode) {
            ResultCode = resultCode;
        }

        public String getResultDesc() {
            return ResultDesc;
        }

        public void setResultDesc(String resultDesc) {
            ResultDesc = resultDesc;
        }

        public MpesaCallbackMetadata getCallbackMetadata() {
            return callbackMetadata;
        }

        public void setCallbackMetadata(MpesaCallbackMetadata callbackMetadata) {
            this.callbackMetadata = callbackMetadata;
        }
    }

    public static class STKPushResponseBody {


        private Long id;

        private String MerchantRequestID;

        private String ResponseCode;

        private String CustomerMessage;

        private String CheckoutRequestID;

        private String ResponseDescription;

        public STKPushResponseBody() {
        }

        public STKPushResponseBody(Long id, String merchantRequestId, String responseCode, String customerMessage, String checkoutRequestID, String responseDescription) {
            this.id = id;
            this.MerchantRequestID = merchantRequestId;
            ResponseCode = responseCode;
            CustomerMessage = customerMessage;
            CheckoutRequestID = checkoutRequestID;
            ResponseDescription = responseDescription;
        }
    }

    public static class QueryResponse {


        private Long id;

        private int ResponseCode;

        private String ResponseDescription;

        private String MerchantRequestID;

        private String CheckoutRequestID;


        private int ResultCode;

        private String ResultDesc;

        public QueryResponse() {
        }

        public QueryResponse(int responseCode, String responseDescription, String merchantRequestID, String checkoutRequestID, int resultCode, String resultDesc) {
            ResponseCode = responseCode;
            ResponseDescription = responseDescription;
            MerchantRequestID = merchantRequestID;
            CheckoutRequestID = checkoutRequestID;
            ResultCode = resultCode;
            ResultDesc = resultDesc;
        }
    }

}
