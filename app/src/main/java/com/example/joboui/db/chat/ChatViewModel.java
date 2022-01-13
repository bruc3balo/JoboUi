package com.example.joboui.db.chat;

import static com.example.joboui.globals.GlobalDb.application;
import static com.example.joboui.globals.GlobalDb.db;
import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;
import static com.example.joboui.model.Models.Messages.MESSAGES;
import static com.example.joboui.model.Models.Messages.RECEIVER_UID;
import static com.example.joboui.model.Models.Messages.SENDER_UID;
import static com.example.joboui.utils.DataOps.getMyIdFromThreadId;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends AndroidViewModel {

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<Optional<Models.AppUser>> getUserMutableByUsername(String username) {
        MutableLiveData<Optional<Models.AppUser>> userMutableLiveData = new MutableLiveData<>();

        ObjectMapper mapper = getObjectMapper();

        String header = "Bearer " + Objects.requireNonNull(getSp(USER_DB, application).get(REFRESH_TOKEN)).toString();

        System.out.println("headers " + header);

        userApi.getUsers(username, header).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                System.out.println("=============== SUCCESS GETTING USER " + username + "===================");
                try {
                    //extract user data
                    JsonResponse jsonResponse = response.body();


                    if (jsonResponse == null) {
                        System.out.println("NO USER DATA " + response.code());
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    } else {
                        System.out.println("USER  DAA");
                    }

                    System.out.println("=============== SUCCESS GETTING USER " + getObjectMapper().writeValueAsString(jsonResponse.getData()) + "===================");


                    if (jsonResponse.getData() == null) {
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    JsonObject userJson = new JsonArray(mapper.writeValueAsString(jsonResponse.getData())).getJsonObject(0);

                    //save user to offline db
                    Models.AppUser user = mapper.readValue(userJson.toString(), Models.AppUser.class);

                    userMutableLiveData.setValue(Optional.of(user));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(application, "Something went wrong", Toast.LENGTH_SHORT).show();
                    userMutableLiveData.setValue(Optional.empty());

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(application, "Failed to login " + t, Toast.LENGTH_SHORT).show();
                userMutableLiveData.setValue(Optional.empty());

            }
        });

        return userMutableLiveData;
    }

    private MutableLiveData<Optional<Models.MessageBundle>> observeMessages(Models.Job job) {
        MutableLiveData<Optional<Models.MessageBundle>> mutableLiveData = new MutableLiveData<>();
        final boolean[] listenerAdded = {false};


        try {
            //get me
            Optional<Domain.User> user = userRepository.getUser();
            Thread.sleep(2000);

            System.out.println("populating me");


            if (user.isPresent()) {
                Models.MessageBundle bundle = new Models.MessageBundle(user.get());

                String youUsername = bundle.getMe().getRole().equals("ROLE_CLIENT") ? job.getLocal_service_provider_username() : job.getClient_username();

                MutableLiveData<Optional<Models.AppUser>> person2m = getUserMutableByUsername(youUsername);
                Thread.sleep(5000);

                Optional<Models.AppUser> person2 = person2m.getValue();


                System.out.println("populating you " + youUsername);


                if (person2 != null && person2.isPresent()) {
                    //get you
                    bundle.setYou(person2.get());


                    //get messages
                    DatabaseReference messageRef = db.getReference().child(MESSAGES);
                    messageRef.keepSynced(true);
                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot threadShots) {
                            if (!threadShots.exists()) {
                                mutableLiveData.setValue(Optional.empty());
                                return;
                            }

                            for (DataSnapshot threads : threadShots.getChildren()) { //get threads

                                if (getMyIdFromThreadId(Objects.requireNonNull(threads.getKey()), bundle.getMe().getUsername()).equals(bundle.getMe().getUsername())) { //my threads

                                    for (DataSnapshot messagesItems : threads.getChildren()) { // get messages
                                        Models.Messages message;
                                        String senderUid = Objects.requireNonNull(messagesItems.child(SENDER_UID).getValue()).toString();
                                        String receiverUid = Objects.requireNonNull(messagesItems.child(RECEIVER_UID).getValue()).toString();

                                        if (senderUid.equals(bundle.getMe().getUsername()) || receiverUid.equals(bundle.getMe().getUsername())) { //i'm in the message
                                            message = messagesItems.getValue(Models.Messages.class);
                                            assert message != null;
                                            //bundle.getMessagesList().add(message);
                                            if (!listenerAdded[0]) {
                                                listenerAdded[0] = true;

                                                messageRef.child(message.getThreadId()).addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                        Models.Messages messages = snapshot.getValue(Models.Messages.class);
                                                        bundle.getMessagesList().add(messages);
                                                        mutableLiveData.setValue(Optional.of(bundle));
                                                    }

                                                    @Override
                                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                        System.out.println("ITEM CHANGED");

                                                        Models.Messages newMessage = snapshot.getValue(Models.Messages.class);
                                                        Optional<Models.Messages> oldMessage = bundle.getMessagesList().stream().filter(i -> {
                                                            assert newMessage != null;
                                                            return i.getMessageId().equals(newMessage.getMessageId());
                                                        }).findFirst();

                                                        if (oldMessage.isPresent()) {
                                                            bundle.getMessagesList().set(bundle.getMessagesList().indexOf(oldMessage.get()), newMessage);
                                                        } else {
                                                            bundle.getMessagesList().add(newMessage);

                                                        }

                                                        mutableLiveData.setValue(Optional.of(bundle));
                                                    }

                                                    @Override
                                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                                        System.out.println("ITEM REMOVED");
                                                        System.out.println(bundle.getMessagesList().size() + " old message size");
                                                        Models.Messages removedMessage = snapshot.getValue(Models.Messages.class);
                                                        assert removedMessage != null;
                                                        System.out.println("ITEM REMOVED "+removedMessage.getMessageId());
                                                        bundle.getMessagesList().removeIf(i -> i.getMessageId().equals(Objects.requireNonNull(removedMessage).getMessageId()));
                                                        mutableLiveData.setValue(Optional.of(bundle));
                                                        System.out.println(bundle.getMessagesList().size() + " new cart size");
                                                    }

                                                    @Override
                                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                        try {
                                                            System.out.println(" my message moved :: " + getObjectMapper().writeValueAsString(snapshot.getValue()));
                                                        } catch (JsonProcessingException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        System.out.println(" my message cancelled  :: " + error.getMessage());

                                                    }
                                                });
                                            }

                                        }
                                    }
                                }

                            }
                            bundle.getMessagesList().sort(Comparator.comparing(Models.Messages::getCreatedAt));
                            mutableLiveData.setValue(Optional.of(bundle));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(application, error.getMessage(), Toast.LENGTH_SHORT).show();
                            mutableLiveData.setValue(Optional.empty());
                        }
                    });

                } else {
                    System.out.println("populating you not");
                    mutableLiveData.setValue(Optional.empty());
                    return mutableLiveData;
                }

            } else {
                System.out.println("populating me not");
                mutableLiveData.setValue(Optional.empty());
                return mutableLiveData;
            }

        } catch (Exception e) {
            e.printStackTrace();
            mutableLiveData.setValue(Optional.empty());
            return mutableLiveData;
        }

        return mutableLiveData;
    }


    //get chat info from firebase for a job
    private MutableLiveData<Optional<Models.MessageBundle>> getMessages(Models.Job job) {
        MutableLiveData<Optional<Models.MessageBundle>> mutableLiveData = new MutableLiveData<>();

        System.out.println("populating messages");


        try {
            //get me
            Optional<Domain.User> user = userRepository.getUser();
            Thread.sleep(2000);

            System.out.println("populating me");


            if (user.isPresent()) {
                Models.MessageBundle bundle = new Models.MessageBundle(user.get());

                String youUsername = bundle.getMe().getRole().equals("ROLE_CLIENT") ? job.getLocal_service_provider_username() : job.getClient_username();

                MutableLiveData<Optional<Models.AppUser>> person2m = getUserMutableByUsername(youUsername);
                Thread.sleep(5000);

                Optional<Models.AppUser> person2 = person2m.getValue();


                System.out.println("populating you " + youUsername);


                if (person2 != null && person2.isPresent()) {
                    //get you
                    bundle.setYou(person2.get());


                    //get messages
                    DatabaseReference messageRef = db.getReference().child(MESSAGES);
                    messageRef.keepSynced(true);
                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                mutableLiveData.setValue(Optional.empty());
                                return;
                            }

                            for (DataSnapshot threads : snapshot.getChildren()) { //get threads

                                if (getMyIdFromThreadId(Objects.requireNonNull(threads.getKey()), bundle.getMe().getUsername()).equals(bundle.getMe().getUsername())) { //my threads

                                    for (DataSnapshot messagesItems : threads.getChildren()) { // get messages
                                        Models.Messages message;
                                        String senderUid = Objects.requireNonNull(messagesItems.child(SENDER_UID).getValue()).toString();
                                        String receiverUid = Objects.requireNonNull(messagesItems.child(RECEIVER_UID).getValue()).toString();

                                        if (senderUid.equals(bundle.getMe().getUsername()) || receiverUid.equals(bundle.getMe().getUsername())) { //i'm in the message
                                            message = messagesItems.getValue(Models.Messages.class);
                                            assert message != null;
                                            bundle.getMessagesList().add(message);
                                        }
                                    }
                                }

                            }
                            bundle.getMessagesList().sort(Comparator.comparing(Models.Messages::getCreatedAt));
                            mutableLiveData.setValue(Optional.of(bundle));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(application, error.getMessage(), Toast.LENGTH_SHORT).show();
                            mutableLiveData.setValue(Optional.empty());
                        }
                    });

                } else {
                    System.out.println("populating you not");
                    mutableLiveData.setValue(Optional.empty());
                    return mutableLiveData;
                }

            } else {
                System.out.println("populating me not");
                mutableLiveData.setValue(Optional.empty());
                return mutableLiveData;
            }

        } catch (Exception e) {
            e.printStackTrace();
            mutableLiveData.setValue(Optional.empty());
            return mutableLiveData;
        }


        return mutableLiveData;

    }

    public LiveData<Optional<Models.MessageBundle>> getChat(Models.Job job) {
        return getMessages(job);
    }

}
