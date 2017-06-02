package com.example.arthur.arcbox_013;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arthur.arcbox_013.SupportClasses.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = "ChatFragmentTag";

    private DatabaseReference firechatDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder> firebaseAdapter;
    private RecyclerView messageRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firechatUser;

    private Button sendButton;
    private String username;
    private String photoUrl;
    private EditText msgEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_chat, container, false);


        firebaseAuth = firebaseAuth.getInstance();
        firechatUser = firebaseAuth.getCurrentUser();
        if (firechatUser == null) {
            startActivity(new Intent(getActivity(), AuthorizationActivity.class));
            //finish();
            //return;
        } else {
            username = firechatUser.getDisplayName();
            if (firechatUser.getPhotoUrl() != null) {
                photoUrl = firechatUser.getPhotoUrl().toString();
            }
        }

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        messageRecyclerView = (RecyclerView) rootView.findViewById(R.id.messageRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);

        firechatDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage,
                FirechatMsgViewHolder>(
                ChatMessage.class,
                R.layout.chat_message,
                FirechatMsgViewHolder.class,
                firechatDatabaseReference.child(username + "/" + "Chat")) {

            @Override
            protected void populateViewHolder(FirechatMsgViewHolder viewHolder, ChatMessage friendlyMessage, int position) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.msgTextView.setText(friendlyMessage.getText());
                viewHolder.userTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.userImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(),
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.userImageView);
                }
            }
        };

        msgEditText = (EditText) rootView.findViewById(R.id.msgEditText);
        sendButton = (Button) rootView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Objects.equals(msgEditText.getText().toString(), "")){
                    ChatMessage friendlyMessage = new
                            ChatMessage(msgEditText.getText().toString(),
                            username,
                            photoUrl);
                    firechatDatabaseReference.child(username + "/" + "Chat")
                            .push().setValue(friendlyMessage);
                    msgEditText.setText("");
                } else Toast.makeText(getActivity(), "Incorrect message", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(firebaseAdapter);


        getActivity();
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    private static class FirechatMsgViewHolder extends RecyclerView.ViewHolder {
        public TextView msgTextView;
        public TextView userTextView;
        public CircleImageView userImageView;

        public FirechatMsgViewHolder(View v) {
            super(v);
            msgTextView = (TextView) itemView.findViewById(R.id.msgTextView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            userImageView = (CircleImageView) itemView.findViewById(R.id.userImageView);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
