package com.example.arthur.arcbox_013;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FormActivity extends Fragment {

    public static final String TAG = "FormFragmentTag";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_form, container, false);



        getActivity();
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
