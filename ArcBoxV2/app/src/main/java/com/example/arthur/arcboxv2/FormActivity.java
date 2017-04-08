package com.example.arthur.arcboxv2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormActivity extends Fragment implements View.OnClickListener{

    Button btnAdd;
    EditText edName, edWeight, edFrom, edTo, edFIO, edEmail, edPhone;
    TextView tvWait;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_form, container, false);

        btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        edName = (EditText) rootView.findViewById(R.id.edName);
        edWeight = (EditText) rootView.findViewById(R.id.edWeight);
        edFrom = (EditText) rootView.findViewById(R.id.edFrom);
        edTo = (EditText) rootView.findViewById(R.id.edTo);
        edFIO = (EditText) rootView.findViewById(R.id.edFIO);
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        edPhone = (EditText) rootView.findViewById(R.id.edPhone);
        tvWait = (TextView) rootView.findViewById(R.id.tvWait);

        getActivity();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        String name = edName.getText().toString();
        String weight = edWeight.getText().toString();
        String from = edFrom.getText().toString();
        String to = edTo.getText().toString();
        String fio = edFIO.getText().toString();
        String email = edEmail.getText().toString();
        String phone = edPhone.getText().toString();

        switch (v.getId()){
            case R.id.btnAdd:
                if(name.equalsIgnoreCase("") || weight.equalsIgnoreCase("") || from.equalsIgnoreCase("")
                        || to.equalsIgnoreCase("") || fio.equalsIgnoreCase("") || email.equalsIgnoreCase("")
                        || phone.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), "Incorrect ", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    FirebaseDatabase databases = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = databases.getReference("Order - " + name);
                    myRef.setValue(weight + " "
                            + from + " "
                            + to + " "
                            + fio + " "
                            + email + " "
                            + phone + ".");
                    edName.setText("");
                    edWeight.setText("");
                    edFrom.setText("");
                    edTo.setText("");
                    edFIO.setText("");
                    edEmail.setText("");
                    edPhone.setText("");
                    tvWait.setText("The order was placed! Expect a call from the operator.");

                    DatabaseReference mSimpleFirechatDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    FirebaseAuth mFirebaseAuth = mFirebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser mFirechatUser = mFirechatUser = mFirebaseAuth.getCurrentUser();
                    assert mFirechatUser != null;
                    String mPhotoUrl = mFirechatUser.getPhotoUrl().toString();
                    ChatMessage friendlyMessage = new
                            ChatMessage("Hello, your order is: " + name + " " + weight + " "
                            + from + " "
                            + to + " "
                            + fio + " "
                            + email + " "
                            + phone + ".",
                            "Order helper",
                            "https://api.adorable.io/avatars/285/abott@adorable.png");
                    mSimpleFirechatDatabaseReference.child("messages")
                            .push().setValue(friendlyMessage);
                }
                break;

        }

    }

}

