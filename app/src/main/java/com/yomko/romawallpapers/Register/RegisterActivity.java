package com.yomko.romawallpapers.Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Home.HomeActivity;
import com.yomko.romawallpapers.Login.LoginActivity;
import com.yomko.romawallpapers.Model.User;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Utility.Validation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.loading)
    MKLoader loading;
    @BindView(R.id.userNameEdt)
    EditText userNameEdt;
    @BindView(R.id.emailEdt)
    EditText emailEdt;
    @BindView(R.id.passwordEdt)
    EditText passwordEdt;
    @BindView(R.id.confirmPasswordEdt)
    EditText confirmPasswordEdt;
    @BindView(R.id.phoneEdt)
    EditText phoneEdt;
    @BindView(R.id.registerBtn)
    Button registerBtn;
    @BindView(R.id.LoginText)
    TextView LoginText;
    @BindView(R.id.registerView)
    NestedScrollView registerView;

    private Snackbar snackbar;
    private String message, email, password, username, phone, confirmpassword, type;
    private Intent intent;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
            }
        });

        LoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showMessage(String message) {
        snackbar = Snackbar.make(registerView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void userRegister() {
        email = emailEdt.getText().toString();
        password = passwordEdt.getText().toString();
        username = userNameEdt.getText().toString();
        phone = phoneEdt.getText().toString();
        confirmpassword = confirmPasswordEdt.getText().toString();
        type = "1";

        if (Validation.validateName(userNameEdt)) {
            if (Validation.validateEmail(emailEdt)) {
                if (Validation.validatePassword(passwordEdt)) {
                    if (Validation.validateConfirmPassword(password, confirmpassword)) {
                        if (Validation.validatePhone(phoneEdt)) {
                            loading.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    loading.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        message = getResources().getString(R.string.emailExist);
                                        showMessage(message);
                                    } else {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            User newUser = new User(username, email, phone, type);
                                            DatabaseReference reference = database.getReference().child(getResources().getString(R.string.Users));
                                            reference.child(user.getUid()).setValue(newUser);
                                            newUser.setUserID(user.getUid());
                                            intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                            SaveUserData(newUser);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        } else {
                            message = getResources().getString(R.string.invalidPhone);
                            showMessage(message);
                        }
                    } else {
                        message = getResources().getString(R.string.invalidConfirmPassword);
                        showMessage(message);
                    }
                } else {
                    message = getResources().getString(R.string.invalidPassword);
                    showMessage(message);
                }
            } else {
                message = getResources().getString(R.string.invalidEmail);
                showMessage(message);
            }
        } else {
            message = getResources().getString(R.string.invalidUsername);
            showMessage(message);
        }
    }

    private void SaveUserData(User user) {
        SharedPreferences sharedpreferences = getSharedPreferences(getResources().getString(R.string.SaveUser), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getResources().getString(R.string.userID), user.getUserID());
        editor.putString(getResources().getString(R.string.userEmail), user.getUserEmail());
        editor.apply();
    }

}
