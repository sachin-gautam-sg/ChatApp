package app.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth= FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();

        init();
        RetrieveUserInfo();
    }


    private void init() {
        userName= findViewById(R.id.setUserName);
        userStatus= findViewById(R.id.setProfileStatus);
        userProfileImage= findViewById(R.id.profile_image);
    }

    public void Update(View view){
        UpdateSettings();
    }

    private void UpdateSettings() {
        String setUsername= userName.getText().toString();
        String setStatus= userStatus.getText().toString();

        if(TextUtils.isEmpty(setUsername))
            Toast.makeText(this, "Please enter a valid username", Toast.LENGTH_SHORT).show();

        if(TextUtils.isEmpty(setStatus))
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();

        else{
            HashMap<String, String> profileMap= new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUsername);
            profileMap.put("status", setStatus);

            rootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(SettingActivity.this, "Profile Updated successfully", Toast.LENGTH_SHORT).show();
                            }

                            else
                                Toast.makeText(SettingActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private void RetrieveUserInfo() {
        rootRef.child("users").child(currentUserID)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")))){
                    String retrieveUserImage= snapshot.child("image").getValue().toString();
                    userName.setText(snapshot.child("name").getValue().toString());
                    userStatus.setText(snapshot.child("status").getValue().toString());
                }

                else if((snapshot.exists()) && (snapshot.hasChild("name"))){
                    userName.setText(snapshot.child("name").getValue().toString());
                    userStatus.setText(snapshot.child("status").getValue().toString());
                }

                else{
                    Toast.makeText(SettingActivity.this, "Please update your profile information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent= new Intent(SettingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}