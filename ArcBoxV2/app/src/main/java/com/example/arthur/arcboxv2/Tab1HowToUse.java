package com.example.arthur.arcboxv2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class Tab1HowToUse extends Fragment {

    TextView tvHowToUse;
    Firebase firebaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_use, container, false);

        Firebase.setAndroidContext(getActivity());
        tvHowToUse = (TextView) rootView.findViewById(R.id.tvHowToUse);
        firebaseReference = new Firebase("https://arcboxv2.firebaseio.com/HowTo/type");

        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                tvHowToUse.setText(text);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        return rootView;
    }
}
