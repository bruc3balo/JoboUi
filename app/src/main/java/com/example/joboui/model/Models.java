package com.example.joboui.model;


import static com.example.joboui.globals.GlobalVariables.HY;

import androidx.annotation.NonNull;

import com.example.joboui.domain.Domain;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Models {

    public static class NewUserForm implements Serializable {

        private String names;

        private String username;

        private String email_address;

        private String password;

        private String phone_number = HY;

        private String id_number = HY;

        private String bio = HY;

        private LinkedHashMap<String, String> preferred_working_hours = new LinkedHashMap<>();

        private LinkedList<String> specialities = new LinkedList<>();

        private String role;


        public NewUserForm() {

        }

        public NewUserForm(String names, String username, String email_address, String password, String phone_number) {
            this.names = names;
            this.username = username;
            this.password = password;
            this.phone_number = phone_number;
            this.email_address = email_address;
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

        public void setPreferred_working_hours(LinkedHashMap<String, String> preferred_working_hours) {
            this.preferred_working_hours = preferred_working_hours;
        }

        public List<String> getSpecialities() {
            return specialities;
        }

        public void setSpecialities(LinkedList<String> specialities) {
            this.specialities = specialities;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class RoleCreationForm implements Serializable {


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

    public static class RoleToUserForm implements Serializable {

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

    public static class UsernameAndPasswordAuthenticationRequest implements Serializable {
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

    public static class UserUpdateForm implements Serializable {

        private String names;

        private String email_address;

        private String password;

        private String role;

        private String phone_number;

        private String id_number;

        private String bio;

        private Boolean verified;

        private LinkedHashMap<String, String> preferred_working_hours;

        private LinkedList<String> specialities;

        private Boolean disabled;

        private Boolean deleted;

        public UserUpdateForm() {

        }

        public UserUpdateForm(Boolean disabled, Boolean deleted, Boolean verified) {
            this.disabled = disabled;
            this.deleted = deleted;
            this.verified = verified;
        }

        public UserUpdateForm(String phone_number) {
            this.phone_number = phone_number;
        }

        public UserUpdateForm(String id_number, String bio, LinkedHashMap<String, String> preferred_working_hours, LinkedList<String> specialities, Boolean verified) {
            this.id_number = id_number;
            this.bio = bio;
            this.preferred_working_hours = preferred_working_hours;
            this.specialities = specialities;
            this.verified = verified;
        }

        public UserUpdateForm(String id_number, String bio, LinkedHashMap<String, String> preferred_working_hours, LinkedList<String> specialities) {
            this.id_number = id_number;
            this.bio = bio;
            this.preferred_working_hours = preferred_working_hours;
            this.specialities = specialities;
        }

        public UserUpdateForm(String names, String email_address, String password, String role, String phone_number, String id_number, String bio, LinkedHashMap<String, String> preferred_working_hours, LinkedList<String> specialities, Boolean verified) {
            this.names = names;
            this.email_address = email_address;
            this.password = password;
            this.role = role;
            this.phone_number = phone_number;
            this.id_number = id_number;
            this.bio = bio;
            this.preferred_working_hours = preferred_working_hours;
            this.specialities = specialities;
            this.verified = verified;
        }

        public UserUpdateForm(Boolean verified) {
            this.verified = verified;
        }

        public UserUpdateForm(LinkedList<String> specialities) {
            this.specialities = specialities;
        }

        public Boolean getVerified() {
            return verified;
        }

        public void setVerified(Boolean verified) {
            this.verified = verified;
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

        public LinkedHashMap<String, String> getPreferred_working_hours() {
            return preferred_working_hours;
        }

        public void setPreferred_working_hours(LinkedHashMap<String, String> preferred_working_hours) {
            this.preferred_working_hours = preferred_working_hours;
        }

        public LinkedList<String> getSpecialities() {
            return specialities;
        }

        public void setSpecialities(LinkedList<String> specialities) {
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

    public static class AppUser implements Serializable {

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

        private Boolean verified;

        private float rating;

        public AppUser() {

        }

        public AppUser(Long id, String names, String username, String id_number, String email_address, String phone_number, String password, String bio, String last_known_location, Date created_at, Date updated_at, AppRole role, String preferred_working_hours, String specialities, Boolean disabled, Boolean deleted, Boolean verified,float rating) {
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
            this.verified = verified;
            this.rating = rating;
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

        public Boolean getVerified() {
            return verified;
        }

        public void setVerified(Boolean verified) {
            this.verified = verified;
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

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }
    }

    public static class AppRole implements Serializable {

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

    public static class Permissions implements Serializable {

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

    public static class ServiceRequestForm implements Serializable {

        private String description;

        private String name;

        public ServiceRequestForm() {

        }

        public ServiceRequestForm(String description, String name) {
            this.description = description;
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ServiceUpdateForm implements Serializable {

        private String name;

        private Boolean disabled;

        private String description;

        public ServiceUpdateForm() {
        }

        public ServiceUpdateForm(Boolean disabled) {
            this.disabled = disabled;
        }

        public ServiceUpdateForm(String name, Boolean disabled, String description) {
            this.name = name;
            this.disabled = disabled;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class ServicesModel implements Serializable {

        private Long id;
        private String name;
        private String description;
        private Boolean disabled;
        private Date created_at;
        private Date updated_at;


        public ServicesModel(String name) {
            this.name = name;
        }


        public ServicesModel() {

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
    }

    public static class JobRequestForm implements Serializable {

        private String local_service_provider_username;

        private String client_username;

        private String job_location;

        private String specialities;

        private String scheduled_at;

        private String job_description;

        private String job_price_range;

        public JobRequestForm() {

        }

        public String getLocal_service_provider_username() {
            return local_service_provider_username;
        }

        public void setLocal_service_provider_username(String local_service_provider_username) {
            this.local_service_provider_username = local_service_provider_username;
        }

        public String getClient_username() {
            return client_username;
        }

        public void setClient_username(String client_username) {
            this.client_username = client_username;
        }

        public String getJob_location() {
            return job_location;
        }

        public void setJob_location(String job_location) {
            this.job_location = job_location;
        }

        public String getSpecialities() {
            return specialities;
        }

        public void setSpecialities(String specialities) {
            this.specialities = specialities;
        }

        public String getScheduled_at() {
            return scheduled_at;
        }

        public void setScheduled_at(String scheduled_at) {
            this.scheduled_at = scheduled_at;
        }

        public String getJob_description() {
            return job_description;
        }

        public void setJob_description(String job_description) {
            this.job_description = job_description;
        }

        public String getJob_price_range() {
            return job_price_range;
        }

        public void setJob_price_range(String job_price_range) {
            this.job_price_range = job_price_range;
        }
    }

    public static class JobUpdateForm implements Serializable {

        private String job_location;

        private String scheduled_at;

        private Boolean completed_at;

        private String job_price_range;

        private String job_price;

        private Integer job_status;

        private Boolean reported;

        public JobUpdateForm(Integer job_status) {
            this.job_status = job_status;
        }

        public JobUpdateForm(String job_price, String job_price_range, Integer job_status) {
            this.job_price = job_price;
            this.job_price_range = job_price_range;
            this.job_status = job_status;
        }

        public JobUpdateForm() {
        }


        public JobUpdateForm(String job_location, String scheduled_at, Boolean completed_at, String job_price_range, String job_price, Integer job_status) {
            this.job_location = job_location;
            this.scheduled_at = scheduled_at;
            this.completed_at = completed_at;
            this.job_price_range = job_price_range;
            this.job_price = job_price;
            this.job_status = job_status;
        }

        public JobUpdateForm(Boolean reported) {
            this.reported = reported;
        }

        public Boolean getReported() {
            return reported;
        }

        public void setReported(Boolean reported) {
            this.reported = reported;
        }

        public String getJob_location() {
            return job_location;
        }

        public void setJob_location(String job_location) {
            this.job_location = job_location;
        }

        public String getScheduled_at() {
            return scheduled_at;
        }

        public void setScheduled_at(String scheduled_at) {
            this.scheduled_at = scheduled_at;
        }

        public Boolean getCompleted_at() {
            return completed_at;
        }

        public void setCompleted_at(Boolean completed_at) {
            this.completed_at = completed_at;
        }

        public String getJob_price_range() {
            return job_price_range;
        }

        public void setJob_price_range(String job_price_range) {
            this.job_price_range = job_price_range;
        }

        public String getJob_price() {
            return job_price;
        }

        public void setJob_price(String job_price) {
            this.job_price = job_price;
        }

        public Integer getJob_status() {
            return job_status;
        }

        public void setJob_status(Integer job_status) {
            this.job_status = job_status;
        }
    }

    public static class Job implements Serializable {

        private Long id;

        private String local_service_provider_username;

        private String client_username;

        private String job_location;

        private String specialities;

        private String created_at;

        private String updated_at;

        private String scheduled_at;

        private String completed_at;

        private String job_price_range;

        private BigDecimal job_price;

        private String job_description;

        private Integer job_status;

        private Boolean reported;

        private Set<Payment> payments = new LinkedHashSet<>();

        public Job() {

        }

        public Job(Boolean reported) {
            this.reported = reported;
        }

        public Job(Long id, String local_service_provider_username, String client_username, String job_location, String specialities, String created_at, String updated_at, String scheduled_at, String completed_at, String job_price_range, BigDecimal job_price, String job_description, Integer job_status) {
            this.id = id;
            this.local_service_provider_username = local_service_provider_username;
            this.client_username = client_username;
            this.job_location = job_location;
            this.specialities = specialities;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.scheduled_at = scheduled_at;
            this.completed_at = completed_at;
            this.job_price_range = job_price_range;
            this.job_price = job_price;
            this.job_description = job_description;
            this.job_status = job_status;
        }

        public Boolean getReported() {
            return reported;
            //todo reported api
        }

        public Set<Payment> getPayments() {
            return payments;
        }

        public void setPayments(Set<Payment> payments) {
            this.payments = payments;
        }

        public void setReported(Boolean reported) {
            this.reported = reported;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLocal_service_provider_username() {
            return local_service_provider_username;
        }

        public void setLocal_service_provider_username(String local_service_provider_username) {
            this.local_service_provider_username = local_service_provider_username;
        }

        public String getClient_username() {
            return client_username;
        }

        public void setClient_username(String client_username) {
            this.client_username = client_username;
        }

        public String getJob_location() {
            return job_location;
        }

        public void setJob_location(String job_location) {
            this.job_location = job_location;
        }

        public String getSpecialities() {
            return specialities;
        }

        public void setSpecialities(String specialities) {
            this.specialities = specialities;
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

        public String getScheduled_at() {
            return scheduled_at;
        }

        public void setScheduled_at(String scheduled_at) {
            this.scheduled_at = scheduled_at;
        }

        public String getCompleted_at() {
            return completed_at;
        }

        public void setCompleted_at(String completed_at) {
            this.completed_at = completed_at;
        }

        public String getJob_price_range() {
            return job_price_range;
        }

        public void setJob_price_range(String job_price_range) {
            this.job_price_range = job_price_range;
        }

        public BigDecimal getJob_price() {
            return job_price;
        }

        public void setJob_price(BigDecimal job_price) {
            this.job_price = job_price;
        }

        public String getJob_description() {
            return job_description;
        }

        public void setJob_description(String job_description) {
            this.job_description = job_description;
        }

        public Integer getJob_status() {
            return job_status;
        }

        public void setJob_status(Integer job_status) {
            this.job_status = job_status;
        }
    }

    public static class Payment implements Serializable {

        private Long id;

        private String CheckoutRequestID;

        private String ResponseCode;

        private boolean paid;

        private String narration;

        private String transaction_description;

        private String PhoneNumber;

        private Long job_id;

        private Date confirmed_at;

        private Date created_at;

        private String access_token;

        private Integer resultCode;

        private String Amount;

        private String MpesaReceiptNumber;


        public Payment() {

        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCheckoutRequestID() {
            return CheckoutRequestID;
        }

        public void setCheckoutRequestID(String checkoutRequestID) {
            CheckoutRequestID = checkoutRequestID;
        }

        public String getResponseCode() {
            return ResponseCode;
        }

        public void setResponseCode(String responseCode) {
            ResponseCode = responseCode;
        }

        public boolean isPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }

        public String getNarration() {
            return narration;
        }

        public void setNarration(String narration) {
            this.narration = narration;
        }

        public String getTransaction_description() {
            return transaction_description;
        }

        public void setTransaction_description(String transaction_description) {
            this.transaction_description = transaction_description;
        }

        public String getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            PhoneNumber = phoneNumber;
        }

        public Long getJob_id() {
            return job_id;
        }

        public void setJob_id(Long job_id) {
            this.job_id = job_id;
        }

        public Date getConfirmed_at() {
            return confirmed_at;
        }

        public void setConfirmed_at(Date confirmed_at) {
            this.confirmed_at = confirmed_at;
        }

        public Date getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Date created_at) {
            this.created_at = created_at;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Integer getResultCode() {
            return resultCode;
        }

        public void setResultCode(Integer resultCode) {
            this.resultCode = resultCode;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getMpesaReceiptNumber() {
            return MpesaReceiptNumber;
        }

        public void setMpesaReceiptNumber(String mpesaReceiptNumber) {
            MpesaReceiptNumber = mpesaReceiptNumber;
        }
    }

    public static class Messages implements Serializable {

        public static final String MESSAGES = "Messages";

        private String threadId;
        public static final String THREAD_ID = "threadId";

        private String messageId;
        public static final String MESSAGE_ID = "messageId";
        private String senderUsername;
        public static final String SENDER_UID = "senderUsername";
        private String receiverUsername;
        public static final String RECEIVER_UID = "receiverUsername";
        private String messageContent;
        public static final String MESSAGE_CONTENT = "messageContent";
        private String createdAt;
        private int status;
        public static final String STATUS = "status";
        private String lastModified;
        public static final String LAST_MODIFIED = "lastModified";
        private String openedAt;

        public static final String MESSAGE_SUR = "MSG";


        public static final int DRAFT = 0;
        public static final int QUEUED = 1;
        public static final int SENT = 2;
        public static final int RECEIVED = 3;
        public static final int OPENED = 4;


        public Messages() {
        }


        public Messages(String threadId, String messageId, String senderUsername, String receiverUsername, String messageContent, String createdAt, int status, String lastModified, String openedAt) {
            this.threadId = threadId;
            this.messageId = messageId;
            this.senderUsername = senderUsername;
            this.receiverUsername = receiverUsername;
            this.messageContent = messageContent;
            this.createdAt = createdAt;
            this.status = status;
            this.lastModified = lastModified;
            this.openedAt = openedAt;
        }

        public Messages(@NotNull String messageId, String senderUsername, String receiverUsername, String messageContent, String createdAt) {
            this.messageId = messageId;
            this.senderUsername = senderUsername;
            this.receiverUsername = receiverUsername;
            this.messageContent = messageContent;
            this.createdAt = createdAt;
        }

        public @NotNull String getMessageId() {
            return messageId;
        }

        public void setMessageId(@NotNull String messageId) {
            this.messageId = messageId;
        }

        public String getOpenedAt() {
            return openedAt;
        }

        public void setOpenedAt(String openedAt) {
            this.openedAt = openedAt;
        }

        public String getThreadId() {
            return threadId;
        }

        public void setThreadId(String threadId) {
            this.threadId = threadId;
        }

        public String getSenderUsername() {
            return senderUsername;
        }

        public void setSenderUsername(String senderUsername) {
            this.senderUsername = senderUsername;
        }

        public String getReceiverUsername() {
            return receiverUsername;
        }

        public void setReceiverUsername(String receiverUsername) {
            this.receiverUsername = receiverUsername;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
    }

    public static class MessageBundle implements Serializable {
        private final LinkedList<Models.Messages> messagesList = new LinkedList<>();
        private Domain.User me;
        private AppUser you;

        public MessageBundle() {
        }

        public MessageBundle(Domain.User me) {
            this.me = me;
        }

        public MessageBundle(Domain.User me, AppUser you) {
            this.me = me;
            this.you = you;
        }

        public LinkedList<Messages> getMessagesList() {
            return messagesList;
        }

        public Domain.User getMe() {
            return me;
        }

        public void setMe(Domain.User me) {
            this.me = me;
        }

        public AppUser getYou() {
            return you;
        }

        public void setYou(AppUser you) {
            this.you = you;
        }
    }

    public static class Review implements Serializable {
        private Long id;
        private String local_service_provider_username;
        private String local_service_provider_review;
        private String client_username;
        private String client_review;
        private Long job_id;
        private String created_at;
        private String updated_at;
        private Boolean reported;


        public Review() {

        }

        public Review(Long id, String local_service_provider_username, String local_service_provider_review, String client_username, String client_review, Long job_id, String created_at, String updated_at, Boolean reported) {
            this.id = id;
            this.local_service_provider_username = local_service_provider_username;
            this.local_service_provider_review = local_service_provider_review;
            this.client_username = client_username;
            this.client_review = client_review;
            this.job_id = job_id;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.reported = reported;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLocal_service_provider_username() {
            return local_service_provider_username;
        }

        public void setLocal_service_provider_username(String local_service_provider_username) {
            this.local_service_provider_username = local_service_provider_username;
        }

        public String getLocal_service_provider_review() {
            return local_service_provider_review;
        }

        public void setLocal_service_provider_review(String local_service_provider_review) {
            this.local_service_provider_review = local_service_provider_review;
        }

        public String getClient_username() {
            return client_username;
        }

        public void setClient_username(String client_username) {
            this.client_username = client_username;
        }

        public String getClient_review() {
            return client_review;
        }

        public void setClient_review(String client_review) {
            this.client_review = client_review;
        }

        public Long getJob_id() {
            return job_id;
        }

        public void setJob_id(Long job_id) {
            this.job_id = job_id;
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

        public Boolean getReported() {
            return reported;
        }

        public void setReported(Boolean reported) {
            this.reported = reported;
        }


    }

    public static class MakeStkRequest implements Serializable {

        private String transaction_type;

        private String amount;

        private String phone_number;

        private String party_a;

        private Long job_id;

        private String transaction_desc = "-";


        public MakeStkRequest() {

        }

        public MakeStkRequest(String transaction_type, String amount, String phone_number, String party_a, Long job_id, String transaction_desc) {
            this.transaction_type = transaction_type;
            this.amount = amount;
            this.phone_number = phone_number;
            this.party_a = party_a;
            this.job_id = job_id;
            this.transaction_desc = transaction_desc;
        }

        public String getTransaction_type() {
            return transaction_type;
        }

        public void setTransaction_type(String transaction_type) {
            this.transaction_type = transaction_type;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getParty_a() {
            return party_a;
        }

        public void setParty_a(String party_a) {
            this.party_a = party_a;
        }

        public Long getJob_id() {
            return job_id;
        }

        public void setJob_id(Long job_id) {
            this.job_id = job_id;
        }

        public String getTransaction_desc() {
            return transaction_desc;
        }

        public void setTransaction_desc(String transaction_desc) {
            this.transaction_desc = transaction_desc;
        }
    }

    public static class ServiceCashFlow {

        private ServicesModel service;
        private BigDecimal amount;

        public ServiceCashFlow() {
        }

        public ServiceCashFlow(ServicesModel service, BigDecimal amount) {
            this.service = service;
            this.amount = amount;
        }

        public ServicesModel getService() {
            return service;
        }

        public void setService(ServicesModel service) {
            this.service = service;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class FeedbackForm {
        private String username;

        private String comment;

        private Integer rating;

        public FeedbackForm() {
        }

        public FeedbackForm(String username, String comment, Integer rating) {
            this.username = username;
            this.comment = comment;
            this.rating = rating;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }

    public static class Feedback {

        private Long id;

        private Models.AppUser user;

        private Integer rating;

        private String comment;

        public Feedback() {
        }

        public Feedback(Long id, AppUser user, Integer rating) {
            this.id = id;
            this.user = user;
            this.rating = rating;
        }

        public Feedback(AppUser user, Integer rating, String comment) {
            this.user = user;
            this.rating = rating;
            this.comment = comment;
        }

        public Feedback(Long id, AppUser user, Integer rating, String comment) {
            this.id = id;
            this.user = user;
            this.rating = rating;
            this.comment = comment;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public AppUser getUser() {
            return user;
        }

        public void setUser(AppUser user) {
            this.user = user;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class FeedbackChart {

        private int rating;

        private int amount;

        public FeedbackChart() {
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

}