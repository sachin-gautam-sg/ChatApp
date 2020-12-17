package app.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText userEmail, userPassword;
    private TextView forgetPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intiViews();
        mAuth= FirebaseAuth.getInstance();
    }

    private void intiViews() {
        userEmail= findViewById(R.id.login_id);
        userPassword= findViewById(R.id.login_password);
        forgetPassword= findViewById(R.id.forget_password_link);
        loadingBar= new ProgressDialog(LoginActivity.this);
    }

    public void Login(View view){
        AllowUserToLogin();
    }

    private void AllowUserToLogin() {
        String getEmail= userEmail.getText().toString();
        String getPassword= userPassword.getText().toString();
        if(TextUtils.isEmpty(getEmail))
            Toast.makeText(LoginActivity.this, "Please Enter a valid Email", Toast.LENGTH_SHORT).show();

        if(TextUtils.isEmpty(getPassword))
            Toast.makeText(LoginActivity.this, "Please Enter a valid password", Toast.LENGTH_SHORT).show();

        else{
            loadingBar.setTitle("Sign in");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(getEmail, getPassword)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String message= task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent= new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void mobileLogin(View view){
    }

    public void SignUp(View view){
        Intent signUpIntent= new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(signUpIntent);
    }
}