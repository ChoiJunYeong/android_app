package com.example.q.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class picture_expansion extends AppCompatActivity {
    Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_expansion);
        //setGalleryAdapter(true);
        Intent intent = getIntent();
        String image_path = intent.getStringExtra("image");
        if(image_path ==null)
            onDestroy();
        else {
            try {
                File f = new File(image_path);
                image = BitmapFactory.decodeStream(new FileInputStream(f));
                loadimage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }



    public void loadimage(Bitmap image) {
        ViewGroup root = findViewById(R.id.activity_picture_expansion);

        //get display size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        image=resize(image, widthPixels, heightPixels);
        int width = image.getWidth(), height = image.getHeight();
        TableLayout tableLayout = findViewById(R.id.table);
        TableRow row = findViewById(R.id.row1);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageBitmap(image);
        if (row == null)
            Toast.makeText(getApplicationContext(), "Failed to load image. Please restart app.", Toast.LENGTH_LONG).show();
        else
            row.addView(imageView);
    }

    public Bitmap resize(Bitmap image,int width,int height ){
        float width_original = image.getWidth();
        float height_original = image.getHeight();
        return Bitmap.createScaledBitmap(image,width,(int)(((float)width) * (width_original/height_original)),true);
    }



}
