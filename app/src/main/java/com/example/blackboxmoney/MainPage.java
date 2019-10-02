package com.example.blackboxmoney;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainPage extends AppCompatActivity {

    private FirebaseFunctions mFunctions;

    private Double ver;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference myDB;
    private Integer BBm;
    private Integer Stm;
    private Integer pccredentials = null;
    private String[] property;
    private Switch cs = null;
    private Switch ms = null;
    private TextView accNumber;
    private TextView Bm;
    private TextView Sm;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mFunctions = FirebaseFunctions.getInstance();

        //TODO version control - autoreminder
        version = (TextView) findViewById(R.id.version);

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getV();
            }
        });

        getV();

        cs = (Switch) findViewById(R.id.MiD);
        ms = (Switch) findViewById(R.id.MS);
        accNumber = (TextView) findViewById(R.id.YA);
        TextView profilet = (TextView) findViewById(R.id.Profilet);
        ImageView profile = (ImageView) findViewById(R.id.Profile);
        TextView mailet = (TextView) findViewById(R.id.Mailet);
        ImageView mail = (ImageView) findViewById(R.id.Mail);
        Bm = (TextView) findViewById(R.id.BBs);
        Sm = (TextView) findViewById(R.id.Sts);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        final String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
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
                Integer moneyBB = Integer.parseInt(userData.get("moneyBB").toString());
                Integer moneySt = Integer.parseInt(userData.get("moneySt").toString());
                if (userData.get("property") != null) {
                    property = userData.get("property").toString().split(",");
                    if (property[0].equals("BBGB") || property[0].equals("STB") || property != null) {
                        ms.setVisibility(View.VISIBLE);
                    }
                }
                String userpassport;
                Integer userpassport1;
                if (userData.get("passport") != null) {
                    userpassport1 = Integer.parseInt(userData.get("passport").toString());
                    userpassport = String.valueOf(userpassport1);
                } else {
                    userpassport = "No passport attached";
                }

                accNumber.setText("Your account number:" + "\n" + accountNumber);
                Bm.setText("BB money: " + moneyBB);
                Sm.setText("St money: " + moneySt);
                pccredentials = accountNumber;

                BBm = moneyBB;
                Stm = moneySt;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cs.setText("St");
                } else {
                    cs.setText("BB");
                }
            }
        });

        ms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ms.setText("Take money");
                } else {
                    ms.setText("Send money");
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, Profile.class));
            }
        });

        profilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, Profile.class));
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, Mail.class));
            }
        });

        mailet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, Mail.class));
            }
        });

        findViewById(R.id.Trans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText in = (EditText) findViewById(R.id.Trans_in);
                Integer ta = Integer.parseInt(in.getText().toString());
                Integer ma = Integer.parseInt(((EditText) findViewById(R.id.moneyAm)).getText().toString());
                transferMoney(ta, ma, pccredentials);
            }
        });
    }

    private void getV() {
        Task<Double> a = getVersion();

        a.addOnCompleteListener(new OnCompleteListener<Double>() {
            @Override
            public void onComplete(@NonNull Task<Double> task) {
                ver = Objects.requireNonNull(task.getResult());
                System.out.println(ver);
                System.out.println(Double.parseDouble(version.getText().toString().split(" ")[1]));
                if (ver != Double.parseDouble(version.getText().toString().split(" ")[1])) {
                    version.setTextColor(Color.rgb(250, 20, 20));
                    version.setText(version.getText() + " \n" + "outdated");
                } else {
                    version.setTextColor(Color.rgb(20, 250, 20));
                    version.setText("version " + ver);
                }
            }
        });
    }

    public void transferMoney (Integer transferAccount, Integer moneyAm, Integer myAccount) {
        if (cs.isChecked()) {
            if (ms.isChecked()) {
                performTransfer(moneyAm, transferAccount, Stm, "moneySt", myAccount, "take");
            } else
                performTransfer(moneyAm, transferAccount, Stm, "moneySt", myAccount, "send");
        } else {
            if (ms.isChecked()) {
                performTransfer(moneyAm, transferAccount, BBm, "moneyBB", myAccount, "take");
            } else
                performTransfer(moneyAm, transferAccount, BBm, "moneyBB", myAccount, "send");
        }
    }

    private void performTransfer(Integer moneyAm, Integer transferAccount, Integer realMon, String type, Integer myAccount, String mod) {
        Map log = new HashMap();
        log.put("fromId", myAccount);
        log.put("toId", transferAccount);
        log.put("amount", moneyAm);
        log.put("type", type);
        log.put("modificator", mod);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("transactionlog").push().setValue(log);
//        if (realMon != null){
//            if (moneyAm < realMon && moneyAm > 0){
//                realMon -= moneyAm;
//                myDB.child(type).setValue(realMon);
//            }
//        }
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

    private Task<Double> getVersion() {
        return mFunctions
                .getHttpsCallable("returnVersion")
                .call("Hi")
                .continueWith(new Continuation<HttpsCallableResult, Double>() {
                    @Override
                    public Double then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Double result = (Double) task.getResult().getData();
                        System.out.println(result);
                        return result;
                    }
                });
    }
}
