package com.example.farmersnet.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.farmersnet.RegisterActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class UploadImageUtil {

    private static Activity activity;
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

}
