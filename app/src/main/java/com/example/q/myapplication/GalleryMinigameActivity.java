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
    Bitmap image;
    int count=0;
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
                image = BitmapFactory.decodeStream(new FileInputStream(f));
                loadGame(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }



    }

    public void loadGame(Bitmap image){
        ViewGroup root = findViewById(R.id.minigame_root_layout);
        while(root.getChildCount()>1)
            root.removeViewAt(1);
        //divide to 3X3 image
        Bitmap[][] puzzles = new Bitmap[3][3];
        int width = image.getWidth()/3, height = image.getHeight()/3;
        TableLayout tableLayout = findViewById(R.id.table);
        TableRow row = findViewById(R.id.row1);
        for(int j=0;j<3;j++){
            row = (TableRow) tableLayout.getChildAt(j);
            if(row.getChildCount()>0)
                row.removeAllViews();
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

        shuffle();
        count=0;
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
                if(isComplete()){
                    showCompleteView();
                }
            }
        });

    }
    public void shuffle(){
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
        int shuffle_direction=5; //0=up,1=down,2=left,3=right
        for(int i=0;i<70;i++) {
            do {
                shuffle_direction = shuffle_random.nextInt(4); //0=up,1=down,2=left,3=right
            }
            while (old_direction + shuffle_direction == 1 || old_direction + shuffle_direction == 5);
            if (shuffle_direction == 0 && y > 0) {
                ImageView imageView = (ImageView) ((TableRow) table.getChildAt(y - 1)).getChildAt(x);
                swap(imageView,blankView);
                blankView=imageView;
                y--;
            } else if (shuffle_direction == 1 && y < 2) {
                ImageView imageView = (ImageView) ((TableRow) table.getChildAt(y + 1)).getChildAt(x);
                swap(imageView,blankView);
                blankView=imageView;
                y++;
            } else if (shuffle_direction == 2 && x > 0) {
                ImageView imageView = (ImageView) ((TableRow) table.getChildAt(y)).getChildAt(x - 1);
                swap(imageView,blankView);
                blankView=imageView;
                x--;
            } else if (shuffle_direction == 3 && x < 2) {
                ImageView imageView = (ImageView) ((TableRow) table.getChildAt(y)).getChildAt(x + 1);
                swap(imageView,blankView);
                blankView=imageView;
                x++;
            }
            else {
                i--;
                continue;
            }
        }
    }
    public void swap(ImageView view1,ImageView view2){
        //view2 should be null drawable at this line
        Object temp_tag = view1.getTag();
        view1.setTag(view2.getTag());
        view2.setTag(temp_tag);

        view2.setImageDrawable(view1.getDrawable());
        view1.setImageDrawable(null);

        count++;
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
        //complete view layout
        ViewGroup root = findViewById(R.id.minigame_root_layout);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0x55FFFFFF);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);
        root.addView(linearLayout);

        //complete! text
        TextView complete_message = new TextView(getApplicationContext());
        complete_message.setText(getResources().getText(R.string.complete)); complete_message.setTextColor(Color.BLACK); complete_message.setTextSize(50);
        complete_message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(complete_message);
        //score text
        TextView score = new TextView(getApplicationContext());
        score.setText("이동 수: "+String.valueOf(count)); score.setTextSize(30);
        score.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(score);
        //replay btn
        Button regame_btn = new Button(getApplicationContext());
        regame_btn.setText("다시하기");
        regame_btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        regame_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGame(image);
            }
        });
        linearLayout.addView(regame_btn);

        //exit btn
        Button exit_btn = new Button(getApplicationContext());
        exit_btn.setText("끝내기");
        exit_btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroy();
            }
        });
        linearLayout.addView(exit_btn);

    }

}
