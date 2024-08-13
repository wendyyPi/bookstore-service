package com.group.bookstore.frontend;
/**
 * @author Ziyu Wang
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.group.bookstore.R;
import com.group.bookstore.backend.User;
import com.group.bookstore.backend.UserManager;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth_sign;
    private EditText emailEditText_sign;
    private EditText passwordEditText_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        delete_input();
        FirebaseApp.initializeApp(this);
        // this part for the sign up function
        TextView loginPrompt = findViewById(R.id.login_prompt);
        mAuth_sign = FirebaseAuth.getInstance();
        emailEditText_sign = findViewById(R.id.email_signup);
        passwordEditText_sign = findViewById(R.id.password_signup);

        // change the hint size for email input
        SpannableString email_hint = new SpannableString("Please input email");//define hint
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12,true);//set up the size
        email_hint.setSpan(ass, 0, email_hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        emailEditText_sign.setHint(new SpannedString(email_hint));
        // change the hint size for password input
        SpannableString passowrd_hint = new SpannableString("Please input password");//define hint
        AbsoluteSizeSpan ass1 = new AbsoluteSizeSpan(12,true);//set up the size
        passowrd_hint.setSpan(ass, 0, passowrd_hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        passwordEditText_sign.setHint(new SpannedString(passowrd_hint));

        Button signUpButton = findViewById(R.id.signup_button);
        emailEditText_sign.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                Toast.makeText(SignUpActivity.this, "Email rules: Must be a valid email address.", Toast.LENGTH_SHORT).show();
            }
        });
        passwordEditText_sign.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                String show="Password rules: Must be\n at least 6 characters\n with at least one digit and one letter.";
                Toast.makeText(SignUpActivity.this, show, Toast.LENGTH_SHORT).show();

            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();

            }
        });




        loginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void delete_input() {
        emailEditText_sign = findViewById(R.id.email_signup);
        passwordEditText_sign = findViewById(R.id.password_signup);
        ImageView emailDelete_sign = findViewById(R.id.del_email_sign);
        ImageView passwordDelete_sign = findViewById(R.id.del_password_sign);
        EditTextClearTools.addclerListener(emailEditText_sign, emailDelete_sign);
        EditTextClearTools.addclerListener(passwordEditText_sign, passwordDelete_sign);
    }

    private void SignUp(){
        String user = emailEditText_sign.getText().toString().trim();
        String pass = passwordEditText_sign.getText().toString().trim();

        if(user.isEmpty()||pass.isEmpty()){
            Toast.makeText(this,"Login Detail cannot be empty",Toast.LENGTH_SHORT).show();
        }else{
            mAuth_sign.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this,"Register Successfully",Toast.LENGTH_SHORT).show();
                        UserManager.getInstance().getUsers().add(new User(user));
                        UserManager.upLoad();
                        finish();

                    }else{
                        Toast.makeText(SignUpActivity.this,"Resgister failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
}