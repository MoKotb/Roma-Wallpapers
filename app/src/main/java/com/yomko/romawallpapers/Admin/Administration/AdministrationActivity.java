package com.yomko.romawallpapers.Admin.Administration;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Model.User;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Utility.Validation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdministrationActivity extends AppCompatActivity {

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
    @BindView(R.id.AddBtn)
    Button AddBtn;
    @BindView(R.id.administrationView)
    NestedScrollView administrationView;

    private Snackbar snackbar;
    private String message, email, password, username, phone, confirmpassword, type;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);
        setTitle(getResources().getString(R.string.AddTitle));
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAdmin();
            }
        });

    }

    private void addAdmin() {
        email = emailEdt.getText().toString();
        password = passwordEdt.getText().toString();
        username = userNameEdt.getText().toString();
        phone = phoneEdt.getText().toString();
        confirmpassword = confirmPasswordEdt.getText().toString();
        type = getResources().getString(R.string.type0);

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
                                            message = username + " " + getResources().getString(R.string.addSuccessfully);
                                            Toast.makeText(AdministrationActivity.this, "" + message, Toast.LENGTH_SHORT).show();
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

    private void showMessage(String message) {
        snackbar = Snackbar.make(administrationView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
