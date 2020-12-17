package app.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.chat.adapter.TabsAccessorAdapters;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapters mTabsAccessorAdapters;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mAuth= FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatApp");

        mTabsAccessorAdapters= new TabsAccessorAdapters(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapters);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initViews() {
        mToolbar= findViewById(R.id.main_page_toolbar);
        mViewPager= findViewById(R.id.main_tabs_pager);
        mTabLayout= findViewById(R.id.main_tabs);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()== null){
            SendUserToLoginActivity();
        }
        else {
            VerifyUserExistance();
        }
    }

    private void VerifyUserExistance() {
        String currentUserID= mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()== R.id.mainLogout){
            mAuth.signOut();
            SendUserToLoginActivity();
        }

        if(item.getItemId()== R.id.mainCreateGroups){
            RequestNewGroup();
        }

        if(item.getItemId()== R.id.mainSettings){
            SendUserToSettingsActivity();
        }

        if(item.getItemId()== R.id.mainFindFriends){

        }
        return true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name:");
        final EditText groupNameField= new EditText(MainActivity.this);
        groupNameField.setHint("e.g. Coding Cafe");
        builder.setView(groupNameField);
        builder.setCancelable(false);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName= groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please write Group Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    CreateNewGroup(groupName);
                }
            }
        }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(final String groupName) {
        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, groupName+" is created.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent= new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void SendUserToSettingsActivity() {
        Intent settingsIntent= new Intent(MainActivity.this, SettingActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }
}