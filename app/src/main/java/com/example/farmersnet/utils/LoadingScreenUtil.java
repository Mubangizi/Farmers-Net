package com.example.farmersnet.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.farmersnet.R;
import com.example.farmersnet.RegisterActivity;



public class LoadingScreenUtil {
    public static AlertDialog dialog;
    public static AlertDialog.Builder builder;
    public Context context;

    //initialising the dialog
   public static void createscreen(String loadingText, Context context){
       builder = new AlertDialog.Builder(context);
       LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View dialogView = inflater.inflate(R.layout.layout_loading_dialog, null);
       TextView dialogtext = dialogView.findViewById(R.id.progTextView);
       dialogtext.setText(loadingText);
       builder.setCancelable(false); // if you want user to wait for some process to finish,
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           builder.setView(dialogView);
       }
       dialog = builder.create();
   }
}
