package com.example.arthur.arcboxv2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Tab1HowToUse extends Fragment {

    TextView tvHowToUse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_use, container, false);

        //Firebase.setAndroidContext(this);

        return rootView;
    }
}
