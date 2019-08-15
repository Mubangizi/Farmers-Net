package com.example.farmersnet;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText regFirstNameText;
    private EditText regLastNameText;
    private EditText regEmailText;
    private EditText regDOBText;
    private EditText regAboutText;
    private EditText regInterestsText;
    private Button regButton;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private FirebaseAuth mAuth;
    private CollectionReference collectionReference;
    private String user_id;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regFirstNameText = findViewById(R.id.reg_fname);
        regLastNameText = findViewById(R.id.reg_lname);
        regEmailText = findViewById(R.id.reg_email);
        regAboutText = findViewById(R.id.reg_about);
        regDOBText = findViewById(R.id.reg_dob);
        regInterestsText =findViewById(R.id.reg_interests);
        regButton = findViewById(R.id.reg_button);

        FirebaseUtil.openFireBaseReference("Users", this);
        mAuth = FirebaseUtil.mAuth;
        collectionReference = FirebaseUtil.collectionReference;
        user_id = mAuth.getCurrentUser().getUid();

        //initialising the dialog
        builder = new AlertDialog.Builder(RegisterActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.layout_loading_dialog, null);
        TextView dialogtext = dialogView.findViewById(R.id.progTextView);
        String registering = "Registering";
        dialogtext.setText(registering);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(dialogView);
        }
        dialog = builder.create();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = regFirstNameText.getText().toString();
                final String lname = regLastNameText.getText().toString();
                final String about = regAboutText.getText().toString();
                final String email = regEmailText.getText().toString();
                String interest = regInterestsText.getText().toString();
                String dob =regDOBText.getText().toString();


                if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(email)){
                        dialog.show();
                        saveUserinfo(fname, lname, email, dob, about, interest);
                }else{
                    Toast.makeText(RegisterActivity.this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void saveUserinfo(String fname, String lname, String email, String dob, String about, String interest) {
        Map<String, String> usermap = new HashMap<>();
        usermap.put("fname", fname);
        usermap.put("lname", lname);
        usermap.put("email", email);
        usermap.put("dob", dob);
        usermap.put("about", about);
        usermap.put("interests", interest);

        collectionReference.document(user_id).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                    sendToMain();
                }else {
                    dialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, "Error: " +error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
