package com.example.blackboxmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView rev;
    private EditText name;
    private EditText surname;
    private EditText mi;
    private TextView passport;
    private TextView account;
    private TextView status;
    private Button ac;
    private DatabaseReference myDB;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        rev = (ImageView) findViewById(R.id.TransferMoney);
        name = (EditText) findViewById(R.id.Name);
        surname = (EditText) findViewById(R.id.Surname);
        mi = (EditText) findViewById(R.id.MI);
        passport = (TextView) findViewById(R.id.Passport);
        account = (TextView) findViewById(R.id.Account);
        status = (TextView) findViewById(R.id.Status);
        ac = (Button) findViewById(R.id.ApplyChanges);

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        final String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myDB = database.getReference().child("Users").child(userId);


        myDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map userData = Objects.requireNonNull((HashMap) dataSnapshot.getValue());
                String userName = userData.get("name").toString();
                String userSurname = userData.get("surname").toString();
                String userMI = userData.get("mi").toString();
                Integer accountNumber = Integer.parseInt(userData.get("pcredentials").toString());
                String userStat = userData.get("status").toString();
                String userpassport;
                Integer userpassport1;
                if (userData.get("passport") != null) {
                    userpassport1 = Integer.parseInt(userData.get("passport").toString());
                    userpassport = String.valueOf(userpassport1);
                } else {
                    userpassport = "No passport attached";
                }


                name.setText(userName);
                surname.setText(userSurname);
                mi.setText(userMI);
                passport.setText("Passport:" + " " + userpassport);
                status.setText("Status:" + " " + userStat);
                account.setText("Account:" + " " + accountNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyChanges()) {
                    progressBar.setVisibility(View.VISIBLE);
                    String inname = name.getText().toString().trim();
                    String insur = surname.getText().toString().trim();
                    String inm = mi.getText().toString().trim();

                    myDB.child("name").setValue(inname);
                    myDB.child("surname").setValue(insur);
                    myDB.child("mi").setValue(inm);

                    Toast.makeText(Profile.this, "Operation succeed?",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, MainPage.class));
            }
        });
    }

    private boolean verifyChanges() {
        if (name.getText().toString().trim().equals("")) {
            Toast.makeText(Profile.this, "Name is required",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (surname.getText().toString().trim().equals("")) {
            Toast.makeText(Profile.this, "Surname is required",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (mi.getText().toString().trim().equals("")) {
//            Toast.makeText(Profile.this, "Middle initials are required",
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
