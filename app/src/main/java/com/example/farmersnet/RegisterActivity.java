package com.example.farmersnet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.security.AccessController.getContext;

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
    private StorageReference storageReference;
    private ImageView profileImage;
    private String user_id;
    private Uri profileImageUri;

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
        profileImage = findViewById(R.id.reg_profile_image);

        FirebaseUtil.openFireBaseReference("Users", this);
        mAuth = FirebaseUtil.mAuth;
        collectionReference = FirebaseUtil.collectionReference;
        user_id = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

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

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //ask for permission
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else {
                        startCropActivity();
                    }
                }else {
                    startCropActivity();
                }
            }
        });

        collectionReference.document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String fname = task.getResult().getString("fname");
                    String lname = task.getResult().getString("lname");
                    String about = task.getResult().getString("about");
                    String interest = task.getResult().getString("interests");
                    String dob = task.getResult().getString("dob");
                    String email = task.getResult().getString("email");
                    regFirstNameText.setText(fname);
                    regLastNameText.setText(lname);
                    regAboutText.setText(about);
                    regInterestsText.setText(interest);
                    regDOBText.setText(dob);
                    regEmailText.setText(email);
                }
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = regFirstNameText.getText().toString();
                final String lname = regLastNameText.getText().toString();
                final String about = regAboutText.getText().toString();
                final String email = regEmailText.getText().toString();
                final String interest = regInterestsText.getText().toString();
                final String dob =regDOBText.getText().toString();

                if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(email)){
                    dialog.show();
                    if(profileImageUri != null) {
                        String randomName = UUID.randomUUID().toString();

                        final StorageReference imagepath = storageReference.child("user_images").child(user_id).child(randomName + ".jpg");
                        UploadTask uploadTask = imagepath.putFile(profileImageUri);

                        final Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                }
                                return imagepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri downloadUri = task.getResult();
                                saveUserinfo(fname, lname, email, dob, about, interest, downloadUri.toString());
                            }
                        });
                    }else {
                        saveUserinfo(fname, lname, email, dob, about, interest, null);
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void saveUserinfo(String fname, String lname, String email, String dob, String about, String interest, String downloadUri) {
        final Map<String, Object> usermap = new HashMap<>();
        usermap.put("fname", fname);
        usermap.put("lname", lname);
        usermap.put("email", email);
        usermap.put("dob", dob);
        usermap.put("about", about);
        usermap.put("interests", interest);

        if(downloadUri != null){
            usermap.put("profile_image", downloadUri);
        }


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImageUri = result.getUri();
                profileImage.setImageURI(profileImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
