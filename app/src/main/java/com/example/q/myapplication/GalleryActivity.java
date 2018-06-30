package com.example.q.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity {
    GalleryAdapter galleryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setGalleryAdapter(true);
    }
    public ArrayList<String> loadGallery() {

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        //set query
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        //set variable to get query data
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        //query(get image path)
        ArrayList<String> imagePath = new ArrayList<>();
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            imagePath.add(absolutePathOfImage);
        }
        cursor.close();
        return imagePath;
    }
    public void setGalleryAdapter(boolean state){
        GridView gridView = findViewById(R.id.galleryView);
        ViewGroup root = findViewById(R.id.root_layout);
        if(state){
            //remove other layouts
            while(root.getChildCount()>1){
                root.removeViewAt(1);
            }
            //set adapter
            galleryAdapter = new GalleryAdapter(getApplicationContext(),loadGallery());
            gridView.setAdapter(galleryAdapter);
        }
        else{
            gridView.setAdapter(null);
        }
    }
    public class GalleryAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<String> imagePath;
        GalleryAdapter(Context context,ArrayList<String> imagePath){
            this.context = context;
            this.imagePath = imagePath;
        }

        public int getCount() {
            return imagePath.size();
        }
        public Object getItem(int position) {
            return imagePath.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                //read image from image path string
                File f = new File((String) getItem(position));
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
                //set image
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageBitmap(image);
                //set parameter of imageView
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, width /3));

                //set image onclick listener, show image int big size
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageView imageView = new ImageView(getApplicationContext());
                        ViewGroup root = findViewById(R.id.root_layout);
                        imageView.setImageDrawable(((ImageView)view).getDrawable());
                        imageView.setBackgroundColor(0xFFFFFFFF);
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                        root.addView(imageView);
                        setGalleryAdapter(false);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setGalleryAdapter(true);
                            }
                        });
                    }
                });
                return imageView;
            }catch (FileNotFoundException e){
                e.printStackTrace();
                return null;
            }
        }

    }
}
