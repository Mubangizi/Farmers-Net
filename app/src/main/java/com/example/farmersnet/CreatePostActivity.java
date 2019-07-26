package com.example.farmersnet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {
    private String TAG;
    private EditText titleEditText;
    private EditText articleEditText;
    private Button postBtn;
    private TextView addImageTextView;
    private ImageView postImageView;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Uri postImageUri =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        titleEditText = findViewById(R.id.create_title_editText);
        articleEditText = findViewById(R.id.article_editText);
        postBtn = findViewById(R.id.post_button);
        addImageTextView = findViewById(R.id.addimage_textView);
        postImageView = findViewById(R.id.post_imageView);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference  = FirebaseStorage.getInstance().getReference();

        getSupportActionBar().setTitle("Create Post");


        addImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    if (ContextCompat.checkSelfPermission(CreatePostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //ask for permission
                        ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else {
                        startCropActivity();
                    }
                }else {
                    startCropActivity();
                }
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title_data = titleEditText.getText().toString();
                String article_data = articleEditText.getText().toString();
                if(!TextUtils.isEmpty( title_data) && !TextUtils.isEmpty(article_data) ){

                    final Map<String, Object> postMap = new HashMap<>();
                    postMap.put("title", title_data);
                    postMap.put("article", article_data);
                    postMap.put("timestamp", FieldValue.serverTimestamp());

                   if (postImageUri != null){
                       String randomName = UUID.randomUUID().toString();
                       final StorageReference postImagePath = storageReference.child("Post_Images/"+randomName+".jpg");
                       UploadTask uploadTask = postImagePath.putFile(postImageUri);
                       final Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                           @Override
                           public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                               if(!task.isSuccessful()){
                                   String error = task.getException().getMessage();
                                   Toast.makeText(CreatePostActivity.this, "Error: "+ error, Toast.LENGTH_SHORT).show();
                               }
                               return postImagePath.getDownloadUrl();
                           }
                       }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                           @Override
                           public void onComplete(@NonNull Task<Uri> task) {
                               Uri downloadUri = task.getResult();
                               postMap.put("image", downloadUri.toString());
                               postdata(postMap);
                           }
                       });
                   }else {
                       postdata(postMap);
                   }


                }else {
                    Toast.makeText(CreatePostActivity.this, "Please Fill required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void postdata(Map<String, Object> postMap){
        firebaseFirestore.collection("Posts")
                .add(postMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(CreatePostActivity.this, "Post Created", Toast.LENGTH_SHORT).show();
                        sendToMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(CreatePostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(CreatePostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                postImageView.setImageURI(postImageUri);
                postImageView.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
