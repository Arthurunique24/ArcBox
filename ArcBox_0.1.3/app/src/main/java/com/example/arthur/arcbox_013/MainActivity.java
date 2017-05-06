package com.example.arthur.arcbox_013;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.arthur.arcbox_013.SupportClasses.BottomNavigationViewHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirechatUser;
    private String mUsername;
    private String mPhotoUrl;

    private ChatActivity chatActivity;
    private FormActivity formActivity;
    private AccountActivity accountActivity;
    private SettingsActivity settingsActivity;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public static final String DEFAULT_NAME = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create FragmentManager
        fragmentManager = getSupportFragmentManager();

        //Initialization fragments
        chatActivity = new ChatActivity();
        formActivity = new FormActivity();
        accountActivity = new AccountActivity();
        settingsActivity = new SettingsActivity();

        //Set activity_chat like basic
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, chatActivity, ChatActivity.TAG);
        fragmentTransaction.commit();

        //Buttom Navigation View initialization
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //DisableShiftMode from BottomNavigationView
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //here mGoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirechatUser = mFirebaseAuth.getCurrentUser();
        if (mFirechatUser == null) {
            startActivity(new Intent(this, AuthorizationActivity.class));
            //finish();
            //return;
        } else {
            mUsername = mFirechatUser.getDisplayName();
            if (mFirechatUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirechatUser.getPhotoUrl().toString();
            }
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_chat:
                    fragmentTransaction.replace(R.id.container, chatActivity, ChatActivity.TAG);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_form:
                    fragmentTransaction.replace(R.id.container, formActivity, FormActivity.TAG);
                    fragmentTransaction.commit();
                    return true;
                //case R.id.navigation_notifications:
                    //return true;
                case R.id.navigation_account:
                    fragmentTransaction.replace(R.id.container, accountActivity, AccountActivity.TAG);
                    fragmentTransaction.commit();
            }
            return false;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = DEFAULT_NAME;
                startActivity(new Intent(this, AuthorizationActivity.class));
                return true;
            case R.id.action_settings:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, settingsActivity, AccountActivity.TAG);
                fragmentTransaction.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
