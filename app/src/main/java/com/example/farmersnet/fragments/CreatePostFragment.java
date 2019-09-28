package com.example.farmersnet.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmersnet.MainActivity;
import com.example.farmersnet.R;
import com.example.farmersnet.chatRooms.CreateChatRoomActivity;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.LoadingScreenUtil;
import com.example.farmersnet.utils.UploadImageUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class CreatePostFragment extends Fragment {
    private String TAG;
    private EditText titleEditText;
    private EditText articleEditText;
    private Button postBtn;
    private TextView addImageTextView;
    private TextView userNameTextView;
    private ImageView postImageView;
    private ImageView userImageView;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Uri postImageUri;
    private CollectionReference collectionReference;
    private UploadImageUtil uploadImageUtil;
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createpost_layout, null);

        titleEditText = view.findViewById(R.id.create_title_editText);
        articleEditText = view.findViewById(R.id.article_editText);
        userNameTextView = view.findViewById(R.id.create_username_textView);
        postBtn = view.findViewById(R.id.post_button);
        addImageTextView = view.findViewById(R.id.addimage_textView);
        postImageView = view.findViewById(R.id.post_imageView);
        userImageView = view.findViewById(R.id.create_user_profile_image);
        FirebaseUtil.openFireBaseReference("Posts", getActivity());
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        storageReference  = FirebaseStorage.getInstance().getReference();
        uploadImageUtil = new UploadImageUtil(getActivity());
        user_id = FirebaseUtil.mAuth.getCurrentUser().getUid();
        LoadingScreenUtil.createscreen("Creating", getContext());

        //setting username
        GetUserNameUtil.setusername(user_id, view.getContext(), userNameTextView, userImageView);


        addImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageActivity();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title_data = titleEditText.getText().toString();
                final String article_data = articleEditText.getText().toString();
                if(!TextUtils.isEmpty( title_data) && !TextUtils.isEmpty(article_data) ){
                    //show loading screen
                    LoadingScreenUtil.dialog.show();
                    if (postImageUri != null){
                        String randomName = UUID.randomUUID().toString();
                        final StorageReference postImagePath = storageReference.child("Post_Images/"+randomName+".jpg");

                        UploadTask uploadTask = postImagePath.putFile(postImageUri);

                        final Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if(!task.isSuccessful()){
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), "Error: "+ error, Toast.LENGTH_SHORT).show();
                                }
                                return postImagePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri downloadUri = task.getResult();
                                postdata(title_data, article_data, downloadUri.toString());
                            }
                        });
                    }else {
                        postdata(title_data, article_data, null);
                    }


                }else {
                    Toast.makeText(getContext(), "Please Fill required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void postdata(String title_data, String article_data, String downloadUri){

        final Map<String, Object> postMap = new HashMap<>();
        postMap.put("title", title_data);
        postMap.put("article", article_data);
        postMap.put("timestamp", FieldValue.serverTimestamp());
        postMap.put("user_id", user_id);
        if(downloadUri != null){
            postMap.put("image", downloadUri);
        }

        postBtn.setEnabled(false);
        collectionReference
                .add(postMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID:" + documentReference.getId());
                        Toast.makeText(getContext(), "Post Created", Toast.LENGTH_SHORT).show();
                        LoadingScreenUtil.dialog.dismiss();
                        sendToMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        postBtn.setEnabled(true);
                        LoadingScreenUtil.dialog.dismiss();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(getContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void startImageActivity() {
        Intent intent = CropImage.activity(postImageUri).getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadImageUtil.setresult(requestCode, resultCode, data, postImageView);
        postImageView.setVisibility(View.VISIBLE);
        postImageUri = uploadImageUtil.getImageUri();
    }
}
