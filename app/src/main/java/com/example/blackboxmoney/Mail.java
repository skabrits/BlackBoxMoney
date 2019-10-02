package com.example.blackboxmoney;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Mail extends AppCompatActivity {

    private LinearLayout mailscr;
    private EditText tow;
    private EditText theme;
    private EditText body;
    private Button send;
    private ImageButton del;
    private ImageButton answ;
    private FirebaseAuth mAuth;
    private DatabaseReference mdb;
    private FirebaseFunctions mFunctions;
    private String userName;
    private String userSurname;
    private String userMI;
    private Integer accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        mFunctions = FirebaseFunctions.getInstance();
        mAuth = FirebaseAuth.getInstance();

        del = (ImageButton) findViewById(R.id.Dellet);
        answ = (ImageButton) findViewById(R.id.answer);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatabaseReference myDB = FirebaseDatabase.getInstance().getReference().child(
//                myDB.child("name").setValue(inname);
//                myDB.child("surname").setValue(insur);
//                myDB.child("mi").setValue(inm);
                Toast.makeText(getApplicationContext(), "not working now", Toast.LENGTH_SHORT).show();
            }
        });

        answ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "not working now", Toast.LENGTH_SHORT).show();
            }
        });


        tow = (EditText) findViewById(R.id.TO);
        theme = (EditText) findViewById(R.id.Theme);
        body = (EditText) findViewById(R.id.MessageText);

        final String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map userData = Objects.requireNonNull((HashMap) dataSnapshot.getValue());
                userName = userData.get("name").toString();
                userSurname = userData.get("surname").toString();
                userMI = userData.get("mi").toString();
                accountNumber = Integer.parseInt(userData.get("pcredentials").toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mdb = database.getReference().child("Mail").child(userId);

        mdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map userData = Objects.requireNonNull((HashMap) dataSnapshot.getValue());
                mailscr.removeAllViews();
                int j = 0;
                if (userData != null) {
                    for (Object i : userData.keySet()) {
                        if (!i.equals("Num")) {
                            final String head = ((Map) userData.get(i)).get("Heading").toString();
                            final String body = ((Map) userData.get(i)).get("Body").toString();
                            final String from = ((Map) userData.get(i)).get("From").toString();

                            Random random = new Random();
                            TextView Mail = new TextView(getApplicationContext());
                            Mail.setTag(String.valueOf(j));
                            Mail.setBackgroundColor(200 + Color.rgb(random.nextInt(50), 200 + random.nextInt(50), 200 + random.nextInt(50)));
                            Mail.setText(from + "\n" + head);
                            LinearLayout.LayoutParams rules = new LinearLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    ConstraintLayout.LayoutParams.MATCH_PARENT);
                            //rules.setMargins((int) zerocor[1], (int) zerocor[0], 100, 100);
                            rules.width = 700;
                            mailscr.addView(Mail, rules);

                            Mail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextView headT = (TextView) findViewById(R.id.Head);
                                    TextView fromT = (TextView) findViewById(R.id.From);
                                    TextView bodyT = (TextView) findViewById(R.id.BodyText);

                                    headT.setText("Theme:" + head);
                                    bodyT.setText(body);
                                    fromT.setText("From:" + from);
                                }
                            });


                            j += 1;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send = (Button) findViewById(R.id.Send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(body.getText().toString(), theme.getText().toString().trim(), (userName + " " + userSurname + " " + userMI + " " + "(" + accountNumber + ")").trim(), tow.getText().toString().trim().split(",")[0]);
            }
        });

        ImageButton we = (ImageButton) findViewById(R.id.WriteEmail);
        we.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.WE).setVisibility(View.VISIBLE);

            }
        });

        ImageButton wec = (ImageButton) findViewById(R.id.Del);
        wec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tow.setText("");
                theme.setText("");
                body.setText("");
                findViewById(R.id.WE).setVisibility(View.GONE);
            }
        });

        ImageView rev = (ImageView) findViewById(R.id.Rev);
        rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Mail.this, MainPage.class));
            }
        });

        mailscr = (LinearLayout) findViewById(R.id.Mailsl);
    }

    private Task<String> addMessage(String body, String head, String from, String To) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("Head", head);
        data.put("From", from);
        data.put("Body", body);
        data.put("TO", To);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void send(String body, String head, String from, String To) {
        addMessage(body, head, from, To);
    }
}
