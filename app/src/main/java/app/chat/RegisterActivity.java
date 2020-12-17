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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private TextView alreadyHaveAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rootRef= FirebaseDatabase.getInstance().getReference();

        initViews();

        mAuth= FirebaseAuth.getInstance();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack= new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intentBack);
                finish();
            }
        });
    }

    private void initViews() {
            userEmail= findViewById(R.id.registerEmail);
            userPassword= findViewById(R.id.registerPassword);
            alreadyHaveAccount= findViewById(R.id.alreadyHaveAccount);
            loadingBar= new ProgressDialog(this);
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent= new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }

    public void createAccount(View view){
        createNewAccount();
    }

    private void createNewAccount() {
        String getEmail= userEmail.getText().toString();
        String getPassword= userPassword.getText().toString();

        if(TextUtils.isEmpty(getEmail))
            Toast.makeText(RegisterActivity.this, "Please Enter a valid Email", Toast.LENGTH_SHORT).show();

        if(TextUtils.isEmpty(getPassword))
            Toast.makeText(RegisterActivity.this, "Please Enter a valid password", Toast.LENGTH_SHORT).show();

        else{
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(getEmail, getPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String currentUserID= mAuth.getCurrentUser().getUid();
                                rootRef.child("Users").child(currentUserID).setValue("");

                                Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                SendUserToMainActivity();
                            }

                            else{
                                String message= task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent= new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}