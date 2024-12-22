package com.example.rifafauzi6.mynotesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rifafauzi6.mynotesapp.Db.NoteHelper;
import com.example.rifafauzi6.mynotesapp.Db.UserHelper;
import com.example.rifafauzi6.mynotesapp.Entity.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailInput, passwordInput;
    private Button btnLogin, btnRegister;
    private ImageView btnLoginGoogle;

    private static final int RC_SIGN_IN = 9001;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == RESULT_OK){
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try{
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mAuth = FirebaseAuth.getInstance();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }catch(ApiException e){
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        NoteHelper noteHelper = new NoteHelper(this);
        noteHelper.open();

        emailInput = findViewById(R.id.email_form);
        passwordInput = findViewById(R.id.password_form);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnLoginGoogle = findViewById(R.id.login_google);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLoginGoogle.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("896440147353-thmfqm9a7t1r2iphavbp44rsnhc3d4ap.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_login) {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginWithEmailPassword(email, password);
            }
        } else if (v.getId() == R.id.login_google) {
            signIn();
        }
    }
    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

    private void loginWithEmailPassword(String email, String password){
        UserHelper userHelper = new UserHelper(this);
        userHelper.open();

        User user = userHelper.getUserByEmail(email);

        if(user != null && user.getPassword().equals(password)){
            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
