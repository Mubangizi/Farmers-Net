package com.example.farmersnet.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;

public class UploadImageUtil {

    private static Activity activity;
    private final int GALLERY_PICK = 1;


    private static Uri imageUri;

    public UploadImageUtil(Activity activity){
        this.activity=activity;
    }
    public void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //ask for permission
             ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }else {
                startImageActivity();
            }
        }else {
            startImageActivity();
        }
    }

    private void startImageActivity() {
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(activity);
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(galleryIntent,  "SELECT IMAGE"), GALLERY_PICK);
    }

    public void setresult(int requestCode, int resultCode, @Nullable Intent data, ImageView imageView){
//        result = data;
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
//            this.imageUri = result.getUri();
            assert data != null;
            this.imageUri =data.getData();
            setProfileImageUri(imageUri);
            if(imageView != null){
                imageView.setImageURI(imageUri);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    public static Uri getImageUri() {
        return imageUri;
    }

    public void setProfileImageUri(Uri profileImageUri) {
        this.imageUri = profileImageUri;
    }

}
