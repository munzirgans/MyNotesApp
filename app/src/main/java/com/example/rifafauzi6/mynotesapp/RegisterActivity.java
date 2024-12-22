package com.example.rifafauzi6.mynotesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rifafauzi6.mynotesapp.Db.UserHelper;
import com.example.rifafauzi6.mynotesapp.Entity.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnLogin;
    private UserHelper userHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.email_form);
        etPassword = findViewById(R.id.password_form);
        etConfirmPassword = findViewById(R.id.confirm_password_form);
        btnRegister = findViewById(R.id.btn_submit);
        btnLogin = findViewById(R.id.btn_login);

        userHelper = new UserHelper(this);

        btnRegister.setOnClickListener(this);
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v.getId() == R.id.btn_login){
            navigateToLogin();
        }else if(v.getId() == R.id.btn_submit){
            registerUser();
        }
    }

    private void registerUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the password and confirm password match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the email is already registered
        userHelper.open();
        if (userHelper.checkUserExist(email)) {
            Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show();
            userHelper.close();
            return;
        }

        // Create a new User object
        User newUser = new User(0, email, password);

        // Add user to the database
        long result = userHelper.addUser(newUser);
        userHelper.close();

        if (result > 0) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            // Navigate to the login activity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
