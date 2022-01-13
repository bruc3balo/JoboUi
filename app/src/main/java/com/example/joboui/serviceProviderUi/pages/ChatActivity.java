package com.example.joboui.serviceProviderUi.pages;

import static com.example.joboui.globals.GlobalDb.application;
import static com.example.joboui.globals.GlobalDb.db;
import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.JOB;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;
import static com.example.joboui.model.Models.Messages.MESSAGES;
import static com.example.joboui.model.Models.Messages.SENT;
import static com.example.joboui.tutorial.VerificationActivity.editSingleValue;
import static com.example.joboui.utils.DataOps.animate;
import static com.example.joboui.utils.DataOps.getMessageId;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.ChatRvAdapter;
import com.example.joboui.databinding.ActivityChatBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.utils.AppRolesEnum;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Models.MessageBundle chat = new Models.MessageBundle();
    private ChatRvAdapter chatRvAdapter;

    private ActivityChatBinding binding;
    private boolean listenerAdded = false;
    private DatabaseReference chatRef;
    private final LinkedList<Models.Messages> messagesList = new LinkedList<>();

    private ChildEventListener chatEventListener;
    private Models.Job job;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listenerAdded = false;

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = binding.chatRv;
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        outProgress();

        EditText messageField = binding.messageField;
        ImageButton sendButton = binding.sendButton;
        setEditFieldAnimation(messageField, sendButton);
        sendButton.setOnClickListener(v1 -> validateMessage(messageField));

        ImageButton optionsButton = binding.optionsButton;


        if (getIntent().getExtras() != null) {
            job = (Models.Job) getIntent().getExtras().get(JOB);

            binding.toolbar.setSubtitle(job.getJob_price_range());
            userRepository.getUserLive().observe(this, user -> {
                if (user.isPresent()) {
                    chat.setMe(user.get());

                   if (user.get().getRole().equals(AppRolesEnum.ROLE_CLIENT.name()) && job.getJob_status() == JobStatus.NEGOTIATING.getCode()) {
                       optionsButton.setOnClickListener(v -> editSingleValue(InputType.TYPE_CLASS_NUMBER, "Enter new price", this, price -> {
                           updatePrice(price);
                           return null;
                       }));
                   } else {
                       optionsButton.setVisibility(View.GONE);
                   }

                    populateThread(job);
                    if (job.getClient_username().equals(user.get().getUsername())) {
                        if (user.get().getRole().equals("ROLE_CLIENT")) {
                            //this is my job
                            binding.toolbar.setTitle(job.getLocal_service_provider_username());
                        } else {
                            binding.toolbar.setTitle(job.getClient_username());
                        }
                    } else {
                        if (user.get().getRole().equals("ROLE_SERVICE_PROVIDER")) {
                            //this is my job
                            binding.toolbar.setTitle(job.getClient_username());
                        } else {
                            binding.toolbar.setTitle(job.getLocal_service_provider_username());
                        }
                    }
                }
            });
        }

    }


    private void updatePrice(String price) {
        new ViewModelProvider(this).get(JobViewModel.class).updateJob(job.getId(), new Models.JobUpdateForm(price)).observe(this, jsonResponse -> {

            if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                Toast.makeText(ChatActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JsonObject jobJson = new JsonObject(getObjectMapper().writeValueAsString(jsonResponse.get().getData()));
                this.job = getObjectMapper().readValue(jobJson.toString(), Models.Job.class);
                binding.toolbar.setSubtitle(job.getJob_price_range());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
    }

    private void setEditFieldAnimation(final EditText field, final ImageButton button) {

        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    button.setVisibility(View.GONE);
                } else {
                    button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    button.setVisibility(View.GONE);
                } else {
                    button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    button.setVisibility(View.GONE);
                } else {
                    button.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void inProgress() {
        binding.pb.setVisibility(View.VISIBLE);
        binding.sendButton.setEnabled(false);
        binding.optionsButton.setEnabled(false);
    }

    private void outProgress() {
        binding.pb.setVisibility(View.GONE);
        binding.sendButton.setEnabled(true);
        binding.optionsButton.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeMessageListener();
    }

    //get char from firebase
    private void populateThread(Models.Job job) {
        inProgress();
        System.out.println("populating thread");

        getChat(job).observe(this, messageBundle -> {

            outProgress();

            if (!messageBundle.isPresent()) {
                Toast.makeText(ChatActivity.this, "New chat", Toast.LENGTH_SHORT).show();
                outProgress();
                return;
            }

            chat = messageBundle.get();

            if (messageBundle.get().getMessagesList().isEmpty()) {
                outProgress();
                return;
            }

            messagesList.clear();
            messagesList.addAll(messageBundle.get().getMessagesList());
//            addMessageListener(chat.getMessagesList().getFirst().getThreadId());
            updateList();
        });
    }


    private void sendNewMessage(Models.Messages messages, EditText editText) {
        try {
            inProgress();
            db.getReference().child(MESSAGES).child(String.valueOf(job.getId())).child(messages.getMessageId()).setValue(messages).addOnCompleteListener(task -> {
                outProgress();
                if (task.isSuccessful()) {
                    messages.setStatus(SENT);
                    editText.setText("");
                    Toast.makeText(ChatActivity.this, "Message added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Message added to queued", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList() {
        animate(binding.pb, binding.pb.getAlpha(), 0.0f, false);
//        messagesList.stream().distinct().collect(Collectors.toCollection(() -> messagesList));
        chatRvAdapter.notifyDataSetChanged();
        messagesList.stream().findFirst().ifPresent(c -> {
            try {
                System.out.println("SINGLE " + getObjectMapper().writeValueAsString(c));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    private void removeMessageListener() {
        if (chatEventListener != null) {
            if (listenerAdded) {
                listenerAdded = false;
                chatRef.removeEventListener(chatEventListener);
                System.out.println("Listeners removed");
            } else {
                System.out.println("No listener");
            }
        }
    }

    //auto update messages
    private void addMessageListener(String threadId) {
        if (!listenerAdded) {
            listenerAdded = true;
            chatRvAdapter = new ChatRvAdapter(this, chat.getMe(), chat.getYou(), messagesList);
            binding.chatRv.setAdapter(chatRvAdapter);
            chatRef = FirebaseDatabase.getInstance().getReference().child(MESSAGES).child(threadId);
            chatRef.addChildEventListener(chatEventListener);
            System.out.println("Listeners added");
        }
    }


    //validate message data when sending messages
    private void validateMessage(EditText messageField) {
        if (chat == null || chat.getYou() == null || chat.getMe() == null) {
            Toast.makeText(ChatActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
            return;
        }


        if (messageField.getText().toString().isEmpty()) {
            messageField.setError("Empty field");
        } else {
            Models.Messages messages = new Models.Messages();
            messages.setSenderUsername(chat.getMe().getUsername());
            messages.setReceiverUsername(chat.getYou().getUsername());
            messages.setThreadId(String.valueOf(job.getId()));

            messages.setStatus(SENT);
            messages.setLastModified(Calendar.getInstance().getTime().toString());
            messages.setCreatedAt(Calendar.getInstance().getTime().toString());

            messages.setMessageContent(messageField.getText().toString());
            messages.setOpenedAt(HY);
            messages.setMessageId(getMessageId(messages.getThreadId()));
            sendNewMessage(messages, messageField);
        }
    }

    //live data

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

    private MutableLiveData<Optional<Models.MessageBundle>> getMessages(Models.Job job) {
        MutableLiveData<Optional<Models.MessageBundle>> mutableLiveData = new MutableLiveData<>();
        final boolean[] childListenerAdded = {false};

        System.out.println("populating messages");


        try {
            Models.MessageBundle bundle = new Models.MessageBundle(chat.getMe());

            String youUsername = bundle.getMe().getRole().equals("ROLE_CLIENT") ? job.getLocal_service_provider_username() : job.getClient_username();
            getUserMutableByUsername(youUsername).observe(this, person2 -> {
                if (!person2.isPresent()) {
                    chat = bundle;
                    mutableLiveData.setValue(Optional.of(bundle));
                    return;
                }

                System.out.println("populating you " + youUsername);

                //get you
                bundle.setYou(person2.get());
                chat = bundle;


                //get messages
                DatabaseReference messageRef = db.getReference().child(MESSAGES);
                messageRef.keepSynced(true);
                chatEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Models.Messages messages = snapshot.getValue(Models.Messages.class);

                        bundle.getMessagesList().add(messages);
                        mutableLiveData.setValue(Optional.of(bundle));
                        bundle.getMessagesList().forEach(i -> {
                            try {
                                System.out.println(getObjectMapper().writeValueAsString(i) + " added");
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        });
                        updateList();
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
                        updateList();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        System.out.println("ITEM REMOVED");
                        System.out.println(bundle.getMessagesList().size() + " old message size");
                        Models.Messages removedMessage = snapshot.getValue(Models.Messages.class);
                        assert removedMessage != null;
                        System.out.println("ITEM REMOVED " + removedMessage.getMessageId());
                        bundle.getMessagesList().removeIf(i -> i.getMessageId().equals(Objects.requireNonNull(removedMessage).getMessageId()));
                        mutableLiveData.setValue(Optional.of(bundle));
                        System.out.println(bundle.getMessagesList().size() + " new cart size");
                        updateList();
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
                };

                messageRef.child(String.valueOf(job.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        if (!snapshot1.exists()) {
                            Toast.makeText(ChatActivity.this, "new chat", Toast.LENGTH_SHORT).show();
                            outProgress();
                            return;
                        }
                        addMessageListener(String.valueOf(job.getId()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatActivity.this, "1 " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });


            System.out.println("populating me");


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