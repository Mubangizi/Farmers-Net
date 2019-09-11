package com.example.farmersnet.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

public class UploadImageUtil {

    private static Activity activity;


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
                startCropActivity();
            }
        }else {
            startCropActivity();
        }
    }

    private static void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
    }

    public void setresult(int requestCode, int resultCode, @Nullable Intent data, ImageView imageView){
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                this.imageUri = result.getUri();
                setProfileImageUri(imageUri);
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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
