package app.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText messageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupNameRef, groupMessageKeyRef;

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName= getIntent().getExtras().getString("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth= FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        init();
        GetUserInfo();
        GetAllMessages();
    }

    private void init() {
        mToolbar= findViewById(R.id.groupChatBarLayout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        messageInput= findViewById(R.id.inputGroupMessage);
        displayTextMessages= findViewById(R.id.groupChatDisplayText);
        mScrollView= findViewById(R.id.myScrollView);
    }

    public void SendMessage(View view){
        SaveMessageInfoToDatabase();

        messageInput.setText("");
    }

    private void GetUserInfo() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentUserName= snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SaveMessageInfoToDatabase() {
        String message= messageInput.getText().toString();
        String messageKey= groupNameRef.push().getKey();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this, "Please write message first...", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calForDate=  Calendar.getInstance();
            SimpleDateFormat currentDateFormat= new SimpleDateFormat("MMM dd, yyyy");
            currentDate= currentDateFormat.format(calForDate.getTime());

            Calendar calForTime=  Calendar.getInstance();
            SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm a");
            currentTime= currentDateFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey= new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupMessageKeyRef= groupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap= new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);

            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void DisplayMessages(DataSnapshot snapshot) {
        Iterator iterator= snapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate= (String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage= (String)((DataSnapshot)iterator.next()).getValue();
            String chatName= (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime= (String)((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatName+ ":\n"+ chatMessage+"\n"+ chatDate+"\t\t"+chatTime+"\n\n");
        }
    }

    private void GetAllMessages() {
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}