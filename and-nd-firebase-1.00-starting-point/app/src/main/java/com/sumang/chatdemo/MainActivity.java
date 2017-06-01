/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sumang.chatdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    private ListView mUserListView;
    private ProgressBar mProgressBar;
    private static final int RC_SIGN_IN = 10;
    private static final int RC_PHOTO_PICKER = 2;
    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseRef;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Query mUserListQuery;

    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUsersDatabaseRef = mFirebaseDatabase.getReference().child("users");
        mUserListView = (ListView)findViewById(R.id.userListView);
        List<User> userList = new ArrayList<>();
        mUserAdapter = new UserAdapter(this, R.layout.item_user, userList);
        mUserListView.setAdapter(mUserAdapter);
        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
                if(mFirebaseUser!=null)
                {
                    mUsersDatabaseRef.orderByChild("users").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            setUserDetails(dataSnapshot.getValue(User.class), mFirebaseUser);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    onSignedInInitialize(mFirebaseUser.getDisplayName());
                }
                else
                {
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder().setTheme(R.style.AppTheme)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setIsSmartLockEnabled(false)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }
    boolean newUser = false;
    User userDtls;
    public void setUserDetails(User user, FirebaseUser mFirebaseUser)
    {
        this.userDtls = user;
        if(userDtls==null)
        {
            newUser = true;
            userDtls = new User();
            userDtls.setDisplayName(mFirebaseUser.getDisplayName());
            userDtls.setEmailID(mFirebaseUser.getEmail());
            userDtls.setId(mFirebaseUser.getUid());
            mUsersDatabaseRef.child(mFirebaseUser.getUid()).setValue(userDtls);
        }else
        {
            newUser = false;
        }
        Util.currentUser = userDtls;
    }

    private void onSignedInInitialize(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanUp()
    {
        mUsername = ANONYMOUS;
        mUserAdapter.clear();
    }

    private void attachDatabaseReadListener()
    {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mUserAdapter.add(dataSnapshot.getValue(User.class));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUsersDatabaseRef.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener()
    {
        if(mChildEventListener !=null) {
            mUsersDatabaseRef.removeEventListener(mChildEventListener);
            mChildEventListener =null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener!=null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mUserAdapter.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN)
        {
            if(resultCode== RESULT_OK)
            {
                Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Exiting", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}
