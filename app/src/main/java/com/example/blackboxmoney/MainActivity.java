package com.example.blackboxmoney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myDB;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }

            }
        };

        ETemail = (EditText) findViewById(R.id.et_email);
        ETpassword = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(ETemail.getText().toString().trim(),ETpassword.getText().toString().trim());
            }
        });
        findViewById(R.id.btn_registration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(ETemail.getText().toString().trim(),ETpassword.getText().toString().trim());
            }
        });
    }

    public boolean verifyEmailAndPassword(String email, String password){
        if (email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email is required",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Please enter a valid email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Password is required",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(MainActivity.this, "Minimum lenght of password should be 6",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void createAccount (String email, String password) {
        if (verifyEmailAndPassword(email, password)) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Registration succeed.",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                myDB = database.getReference().child("Users").child(userId);

                                String inname = "Name";
                                String insur = "Surname";
                                String inm = "Middle Initials";
                                final Integer[] pn = {null};
                                Integer monBB = 0;
                                Integer monSt = 0;
                                String stat = "Link your passport to the account to see your Status";

                                final BlockingQueue<Integer> Block = new ArrayBlockingQueue<Integer>(1, true);
                                Random random = new Random();
                                int generatedNumber = 0;

//                                while (pn[0] == null) {
                                generatedNumber = 100000 + random.nextInt(899999);
//                                    Query pnQuery = database.getReference();//.orderByChild("pccredentials holder").equalTo(generatedNumber);
//
//                                    final int finalGeneratedNumber = generatedNumber;
//                                    pnQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()) {
//                                                if (dataSnapshot.getChildrenCount() > 0) {
//                                                    System.out.println("number exists");
//                                                    try {
//                                                        Block.put(0);
//                                                    } catch (InterruptedException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                } else {
//                                                    pn[0] = finalGeneratedNumber;
//                                                    try {
//                                                        Block.put(1);
//                                                    } catch (InterruptedException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            } else {
//                                                pn[0] = finalGeneratedNumber;
//                                                try {
//                                                    Block.put(1);
//                                                } catch (InterruptedException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                                            try {
//                                                Block.put(0);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                    Integer a = 0;
//                                    try {
//                                        a = Block.take();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    if (a == 0) {
//                                        pn[0] = generatedNumber;
//                                    }
//                                }

                                Map newPost = new HashMap();
                                newPost.put("name", inname);
                                newPost.put("surname", insur);
                                newPost.put("mi", inm);
//                                newPost.put("passport", pc);
                                newPost.put("pcredentials", generatedNumber);
                                newPost.put("moneyBB", monBB);
                                newPost.put("moneySt", monSt);
                                newPost.put("status", stat);

                                myDB.setValue(newPost);

                                Map newPost1 = new HashMap();
                                newPost1.put(String.valueOf(generatedNumber), userId);

                                database.getReference().child("pccredentials holder").updateChildren(newPost1);

                                
                                Map newPost2 = new HashMap();
                                newPost2.put("Body", "Hello world");
                                newPost2.put("From", "System");
                                newPost2.put("Heading", "Welcome to BB mail system");

                                database.getReference().child("Mail").child(userId).child("Mail1").updateChildren(newPost2);
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            // ...
                        }
                    });
        }
    }

    public void signIn (String email, String password) {
        if (verifyEmailAndPassword(email, password)) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                finish();
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Authentication succeed.",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(MainActivity.this, MainPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }
//Todo 111
    @Override
    public void onStart() {
        super.onStart();
//         Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            finish();
//            startActivity(new Intent(this, MainPage.class));
//        }
    }
}
