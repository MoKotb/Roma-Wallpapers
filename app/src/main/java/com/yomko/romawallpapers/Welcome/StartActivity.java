package com.yomko.romawallpapers.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Admin.AdminActivity;
import com.yomko.romawallpapers.Home.HomeActivity;
import com.yomko.romawallpapers.Login.LoginActivity;
import com.yomko.romawallpapers.Model.User;
import com.yomko.romawallpapers.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.loading)
    MKLoader loading;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private DatabaseReference database;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        loading.setVisibility(View.VISIBLE);

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                loading.setVisibility(View.VISIBLE);
                final User[] newUser = new User[1];
                if (firebaseAuth.getCurrentUser() != null) {
                    database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference reference = database.child(getResources().getString(R.string.Users)).child(firebaseAuth.getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newUser[0] = dataSnapshot.getValue(User.class);
                            if (newUser[0] != null) {
                                if (newUser[0].getUserType().equals(getResources().getString(R.string.type0))) {
                                    intent = new Intent(StartActivity.this, AdminActivity.class);
                                    SaveUserData(newUser[0]);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    newUser[0].setUserID(dataSnapshot.getKey());
                                    intent = new Intent(StartActivity.this, HomeActivity.class);
                                    SaveUserData(newUser[0]);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                intent = new Intent(StartActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkUser();
                        }
                    }, 2500);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    private void checkUser() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(listener);
    }

    private void SaveUserData(User user) {
        SharedPreferences sharedpreferences = getSharedPreferences(getResources().getString(R.string.SaveUser), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getResources().getString(R.string.userID), user.getUserID());
        editor.putString(getResources().getString(R.string.userEmail), user.getUserEmail());
        editor.putString(getResources().getString(R.string.userName), user.getUserName());
        editor.apply();
    }

}
