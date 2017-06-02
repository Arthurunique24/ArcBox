package com.example.arthur.arcbox_013;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.arcbox_013.SupportClasses.ChatMessage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AuthorizationActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    Animation anim = null;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton mAuthButton;
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        TextView tvHello, tvAuth, tvProjBy;
        tvHello = (TextView) findViewById(R.id.tvHello);
        tvAuth = (TextView) findViewById(R.id.tvAuth);
        tvProjBy = (TextView) findViewById(R.id.tvProjBy);
        mAuthButton = (SignInButton) findViewById(R.id.auth_button);
        mAuthButton.setOnClickListener(this);

        //Set animation
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        tvHello.startAnimation(anim);
        tvAuth.startAnimation(anim);
        tvProjBy.startAnimation(anim);

        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auth_button:
                Authorize();
                break;
        }
    }

    private void Authorize() {
        Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(authorizeIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.d("MyTag", "Google Sign In failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(AuthorizationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            InitialiseUserInFireBase();
                            startActivity(new Intent(AuthorizationActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    //Initialize user data in Firebase
    private void InitialiseUserInFireBase(){
        final DatabaseReference mSimpleDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        //String mPhotoUrl = firebaseUser.getPhotoUrl().toString();
        final String user = firebaseUser.getDisplayName();
        assert user != null;

        mSimpleDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user)){
                    Toast.makeText(AuthorizationActivity.this, "Already have user data", Toast.LENGTH_LONG).show();
                }
                else {
                    addUserDataInFirebase("OrdersContractCouriers", "First order with contract Couriers");
                    addUserDataInFirebase("OrdersFreeCouriers", "First order with free Couriers");
                    addUserDataInFirebase("CompletedOrders", "First order with free Couriers");


                    ChatMessage chatMessage = new ChatMessage("Welcome to ArcBox", "ArcBox Team", "https://api.adorable.io/avatars/285/abott@adorable.png");
                    DatabaseReference mSimpleDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String user = firebaseUser.getDisplayName();
                    assert user != null;

                    mSimpleDatabaseReference.child(user + "/" + "Chat").push().setValue(chatMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Function that add new branch in user data
    private void addUserDataInFirebase(String branch, String FirstData){
        DatabaseReference mSimpleDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        //String mPhotoUrl = firebaseUser.getPhotoUrl().toString();
        String user = firebaseUser.getDisplayName();
        assert user != null;

        mSimpleDatabaseReference.child(user + "/" + branch).push().setValue(FirstData, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError!= null)
                    Toast.makeText(AuthorizationActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Override button back to exit the app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
