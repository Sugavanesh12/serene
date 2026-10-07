package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private boolean isSignUpMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateModeUI();

        binding.tvToggleMode.setOnClickListener(v -> {
            isSignUpMode = !isSignUpMode;
            updateModeUI();
        });

        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

            if (!email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isSignUpMode) {
                // Sign Up flow
                if (AuthManager.isEmailRegistered(this, email)) {
                    Toast.makeText(this, "Email already registered! Please sign in.", Toast.LENGTH_LONG).show();
                    isSignUpMode = false;
                    updateModeUI();
                } else {
                    AuthManager.registerUser(this, email, password);
                    AuthManager.saveLogin(this, email);
                    Toast.makeText(this, "Account created successfully! Welcome to Serene.", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                }
            } else {
                // Sign In flow
                if (!AuthManager.isEmailRegistered(this, email)) {
                    Toast.makeText(this, "Email not registered! Please sign up first.", Toast.LENGTH_LONG).show();
                    isSignUpMode = true;
                    updateModeUI();
                } else if (AuthManager.validatePassword(this, email, password)) {
                    AuthManager.saveLogin(this, email);
                    Toast.makeText(this, "Welcome back, " + email + "!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    // Password incorrect -> Show Update / Reset Password Dialog
                    showPasswordIncorrectDialog(email);
                }
            }
        });
    }

    private void updateModeUI() {
        if (isSignUpMode) {
            binding.btnSignIn.setText("Create Account");
            binding.tvToggleMode.setText("Already have an account? Sign In");
        } else {
            binding.btnSignIn.setText("Sign In");
            binding.tvToggleMode.setText("Don't have an account? Sign Up");
        }
    }

    private void showPasswordIncorrectDialog(String email) {
        EditText input = new EditText(this);
        input.setHint("Enter new password (min 6 chars)");
        input.setPadding(40, 40, 40, 40);

        new AlertDialog.Builder(this)
                .setTitle("Incorrect Password")
                .setMessage("The password you entered for " + email + " is incorrect. Would you like to update your password now?")
                .setView(input)
                .setPositiveButton("Update Password", (dialog, which) -> {
                    String newPass = input.getText().toString().trim();
                    if (newPass.length() >= 6) {
                        AuthManager.updatePassword(this, email, newPass);
                        AuthManager.saveLogin(this, email);
                        Toast.makeText(this, "Password updated and logged in successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
