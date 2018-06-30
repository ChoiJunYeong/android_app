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

public class GalleryMinigameActivity extends AppCompatActivity {
    //GalleryMinigameActivity.GalleryAdapter galleryAdapter;
    Drawable lost_piece;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_minigame);
        //setGalleryAdapter(true);
        Intent intent = getIntent();
        String image_path = intent.getStringExtra("image");
        if(image_path ==null)
            onDestroy();
        else {
            try {
                File f = new File(image_path);
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
                loadGame(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //---------------------------------------------------------------------------------------------------------------------------
        //find blank part
        TableLayout table = findViewById(R.id.table);
        int x=0,y=0;
        ImageView blankView = null;
        for(y=0;y<3;y++){
            TableRow row = (TableRow) table.getChildAt(y);
            for(x=0;x<3;x++){
                blankView = (ImageView) row.getChildAt(x);
                if(blankView.getTag().equals("blank")){
                    break;
                }
            }
            if(blankView.getTag().equals("blank")){
                break;
            }
        }


        Random shuffle_random = new Random();
        int old_direction = -10;
        int shuffle_direction; //0=up,1=down,2=left,3=right
        for(int i=0;i<30;i++) {
            do {
                shuffle_direction = shuffle_random.nextInt(4); //0=up,1=down,2=left,3=right
            } while(old_direction+shuffle_direction==1 || old_direction+shuffle_direction==5);
            old_direction = shuffle(shuffle_direction,x,y);
        }
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
    /*
    public void setGalleryAdapter(boolean state){
        GridView gridView = findViewById(R.id.galleryView);
        ViewGroup root = findViewById(R.id.minigame_root_layout);
        if(state){
            //remove other layouts
            while(root.getChildCount()>2){
                root.removeViewAt(2);
            }
            //set adapter
            galleryAdapter = new GalleryMinigameActivity.GalleryAdapter(getApplicationContext(),loadGallery());
            gridView.setAdapter(galleryAdapter);
        }
        else{
            gridView.setAdapter(null);
        }
    }
    */
    /*
    public class GalleryAdapter extends BaseAdapter {
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
                        //set new image layout
                        ImageView imageView = new ImageView(getApplicationContext());
                        ViewGroup root = findViewById(R.id.root_layout);
                        imageView.setImageDrawable(((ImageView)view).getDrawable());
                        //root.addView(imageView);
                        //set backgroud to not show other view in backgroud
                        imageView.setBackgroundColor(0xFFFFFFFF);

                        setGalleryAdapter(false);

                        Bitmap image = (Bitmap) ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                        loadGame(image);
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
    */

    public void loadGame(Bitmap image){
        //divide to 3X3 image
        Bitmap[][] puzzles = new Bitmap[3][3];
        int width = image.getWidth()/3, height = image.getHeight()/3;
        TableLayout tableLayout = findViewById(R.id.table);
        TableRow row = findViewById(R.id.row1);
        for(int j=0;j<3;j++){
            row = (TableRow) tableLayout.getChildAt(j);
            for(int i=0;i<3;i++){
                puzzles[i][j] = Bitmap.createBitmap(image,i*width,j*height,width,height);
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageBitmap(puzzles[i][j]);
                imageView.setTag(i*10+j);
                if(row==null)
                    Toast.makeText(getApplicationContext(),"Failed to load game. Please restart app.",Toast.LENGTH_LONG).show();
                else
                    row.addView(imageView);
            }
        }
        //blank one puzzle piece
        Random random = new Random();
        int value = random.nextInt(9);
        row = (TableRow) tableLayout.getChildAt(value/3);
        lost_piece = ((ImageView)row.getChildAt(value%3)).getDrawable();
        ((ImageView)row.getChildAt(value%3)).setImageBitmap(null);
        row.getChildAt(value%3).setTag("blank");

        //move puzzle
        for(int j=0;j<3;j++){
            row = (TableRow)tableLayout.getChildAt(j);
            for(int i=0;i<3;i++){
                ImageView imageView = (ImageView)row.getChildAt(i);
                setOnClickMove(i,j,imageView);
            }
        }
    }

    public void setOnClickMove(int i,int j,ImageView view){
        //set direction
        final boolean up,down,left,right;
        final int x=i,y=j;
        if(i==0){
            left=false;right=true;
        }
        else if(i==2){
            left=true;right=false;
        }
        else{
            left=true;right=true;
        }
        if(j==0){
            up=false;down=true;
        }
        else if(j==2){
            up=true;down=false;
        }
        else{
            up=true;down=true;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableLayout tableLayout = findViewById(R.id.table);
                TableRow row = (TableRow) tableLayout.getChildAt(y);


                if(left){
                    if(row.getChildAt(x-1).getTag() == "blank")
                        swap((ImageView) view, (ImageView) row.getChildAt(x - 1));
                }
                if(right){
                    if(row.getChildAt(x+1).getTag() == "blank")
                        swap((ImageView)view, (ImageView) row.getChildAt(x+1));
                }
                if(up){
                    TableRow row2 = (TableRow) tableLayout.getChildAt(y-1);
                    if(row2.getChildAt(x).getTag() == "blank")
                        swap((ImageView)view, (ImageView) row2.getChildAt(x));}
                if(down){
                    TableRow row2 = (TableRow) tableLayout.getChildAt(y+1);
                    if(row2.getChildAt(x).getTag() == "blank")
                        swap((ImageView)view, (ImageView) row2.getChildAt(x));}
            }
        });

    }
    public int shuffle(int shuffle_direction,int x,int y){
        TableLayout table = findViewById(R.id.table);
        if(shuffle_direction == 0 && y>0) {
            ImageView imageView = (ImageView) ((TableRow) table.getChildAt(y - 1)).getChildAt(x);
            imageView.performClick();
        }
        else if(shuffle_direction == 1 && y<2){
            ImageView imageView = (ImageView)((TableRow)table.getChildAt(y+1)).getChildAt(x);
            imageView.performClick();
        }
        else if(shuffle_direction == 2 && x>0){
            ImageView imageView = (ImageView)((TableRow)table.getChildAt(y)).getChildAt(x-1);
            imageView.performClick();
        }
        else if(shuffle_direction == 3 && x<2){
            ImageView imageView = (ImageView)((TableRow)table.getChildAt(y)).getChildAt(x+1);
            imageView.performClick();
        }
        return shuffle_direction;
    }
    public void swap(ImageView view1,ImageView view2){
        //view2 should be null drawable at this line
        Object temp_tag = view1.getTag();
        view1.setTag(view2.getTag());
        view2.setTag(temp_tag);

        view2.setImageDrawable(view1.getDrawable());
        view1.setImageDrawable(null);
        if(isComplete()){
            showCompleteView();
        }
    }
    public boolean isComplete(){
        TableLayout tableLayout = findViewById(R.id.table);
        for(int j=0;j<3;j++){
            TableRow row = (TableRow) tableLayout.getChildAt(j);
            for(int i=0;i<3;i++){
                if(row.getChildAt(i).getTag() == "blank" || row.getChildAt(i).getTag().equals(i*10+j));
                else
                    return false;
            }
        }
        return true;
    }
    public void showCompleteView(){
        TableLayout table = findViewById(R.id.table);
        for(int j=0;j<3;j++){
            TableRow row = (TableRow) table.getChildAt(j);
            for(int i=0;i<3;i++){
                ImageView blankView = (ImageView) row.getChildAt(i);
                if(blankView.getTag().equals("blank")){
                    blankView.setImageDrawable(lost_piece);
                    blankView.setTag(i*10+j);
                    break;
                }
            }
        }

        ViewGroup root = findViewById(R.id.minigame_root_layout);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0x55FFFFFF);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);
        root.addView(linearLayout);

        TextView complete_message = new TextView(getApplicationContext());
        complete_message.setText(getResources().getText(R.string.complete)); complete_message.setTextColor(Color.BLACK); complete_message.setTextSize(50);
        complete_message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(complete_message);

        Button regame_btn = new Button(getApplicationContext());
        regame_btn.setText("다시하기");
        regame_btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(regame_btn);

        Button exit_btn = new Button(getApplicationContext());
        exit_btn.setText("끝내기");
        exit_btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(exit_btn);
    }

}
