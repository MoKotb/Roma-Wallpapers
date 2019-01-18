package com.yomko.romawallpapers.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Admin.AdminActivity;
import com.yomko.romawallpapers.Home.HomeActivity;
import com.yomko.romawallpapers.Model.User;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Register.RegisterActivity;
import com.yomko.romawallpapers.Utility.Validation;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loading)
    MKLoader loading;
    @BindView(R.id.emailEdt)
    EditText emailEdt;
    @BindView(R.id.passwordEdt)
    EditText passwordEdt;
    @BindView(R.id.loginBtn)
    Button loginBtn;
    @BindView(R.id.faceButton)
    Button faceButton;
    @BindView(R.id.googleBtn)
    SignInButton googleBtn;
    @BindView(R.id.RegisterText)
    TextView RegisterText;
    @BindView(R.id.loginView)
    NestedScrollView loginView;

    private Snackbar snackbar;
    private String message, email, password;
    private Intent intent;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private FirebaseDatabase database1;
    private GoogleApiClient apiClient;
    private static final int RC_SIGN_IN = 1;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        database1 = FirebaseDatabase.getInstance();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        RegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        apiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                message = getResources().getString(R.string.error);
                showMessage(message);
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        faceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        message = getResources().getString(R.string.error);
                        showMessage(message);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        message = getResources().getString(R.string.error);
                        showMessage(message);
                    }
                });
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void userLogin() {
        email = emailEdt.getText().toString();
        password = passwordEdt.getText().toString();
        if (Validation.validateEmail(emailEdt)) {
            if (Validation.validatePassword(passwordEdt)) {
                loading.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            loading.setVisibility(View.GONE);
                            message = getResources().getString(R.string.invalidEmailOrPassword);
                            showMessage(message);
                        } else {
                            final User[] newUser = new User[1];
                            database = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference reference = database.child(getResources().getString(R.string.Users)).child(task.getResult().getUser().getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newUser[0] = dataSnapshot.getValue(User.class);
                                    newUser[0].setUserID(dataSnapshot.getKey());
                                    if (newUser[0] != null) {
                                        if (newUser[0].getUserType().equals(getResources().getString(R.string.type0))) {
                                            intent = new Intent(LoginActivity.this, AdminActivity.class);
                                            SaveUserData(newUser[0]);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            SaveUserData(newUser[0]);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    message = getResources().getString(R.string.error);
                                    showMessage(message);
                                }
                            });
                        }
                    }
                });
            } else {
                message = getResources().getString(R.string.invalidPassword);
                showMessage(message);
            }
        } else {
            message = getResources().getString(R.string.invalidEmail);
            showMessage(message);
        }
    }

    private void showMessage(String message) {
        snackbar = Snackbar.make(loginView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                message = getResources().getString(R.string.error);
                showMessage(message);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        loading.setVisibility(View.VISIBLE);
        final String type = getResources().getString(R.string.type1);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                User newUser = new User(user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), type);
                                DatabaseReference reference = database1.getReference().child(getResources().getString(R.string.Users));
                                reference.child(user.getUid()).setValue(newUser);
                                newUser.setUserID(user.getUid());
                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                                SaveUserData(newUser);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            message = getResources().getString(R.string.emailExist);
                            showMessage(message);
                        }
                        loading.setVisibility(View.GONE);
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        loading.setVisibility(View.VISIBLE);
        final String type = getResources().getString(R.string.type1);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                User newUser = new User(user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), type);
                                DatabaseReference reference = database1.getReference().child(getResources().getString(R.string.Users));
                                reference.child(user.getUid()).setValue(newUser);
                                newUser.setUserID(user.getUid());
                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                                SaveUserData(newUser);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            message = getResources().getString(R.string.emailExist);
                            showMessage(message);
                        }
                        loading.setVisibility(View.GONE);
                    }
                });
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
