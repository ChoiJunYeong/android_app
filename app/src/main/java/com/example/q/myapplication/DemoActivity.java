package com.example.q.myapplication;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DemoActivity extends AppCompatActivity {
    int gallery_page=0;
    ArrayList<String> img_path;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.





        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(checkPermission(Manifest.permission.READ_CONTACTS));
        permissions.add(checkPermission(Manifest.permission.WRITE_CONTACTS));
        permissions.add(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
        permissions.add(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        permissions.add(checkPermission(Manifest.permission.INTERNET));
        while(permissions.remove(null));
        if(!permissions.isEmpty())
            getPermission(permissions.toArray(new String[permissions.size()]), permissions.size());



        if(permissions.isEmpty())
            setTablayout();
        else
            recreate();
      //  startActivity(new Intent(this,GithubActivity.class));
    }


    public String checkPermission(String request){
        if(ContextCompat.checkSelfPermission(this,request) != PackageManager.PERMISSION_GRANTED){
            return request;
        }
        else
            return null;
    }
    public void getPermission(String[] permissions,int request_code){
        ActivityCompat.requestPermissions(this, permissions, request_code);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if(grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission Denied. Cannot Launch app.", Toast.LENGTH_SHORT).show();
                    onDestroy();
                }
            }
        }
    }



    public void setTablayout(){
        ViewGroup item_selector = findViewById(R.id.container);


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        findViewById(R.id.context_root_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.fab).setVisibility(View.GONE);
                        break;
                    case 1:
                        findViewById(R.id.gallery_root_layout).setVisibility(View.VISIBLE);
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gallery_page++;
                                setGalleryAdapter();
                            }
                        });
                        findViewById(R.id.fab).setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        ViewGroup container = findViewById(R.id.container);
                        View tab3 = getLayoutInflater().inflate(R.layout.activity_github, null);
                        container.addView(tab3);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String url = preferences.getString("URL", "");
                        if(url!="") {
                            Log.d("github url is",url);
                            getGithubLog(url);
                        }
                        else
                            GithubActivity.setRepository(context);
                        findViewById(R.id.fab).setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        findViewById(R.id.context_root_layout).setVisibility(View.GONE);
                        break;
                    case 1:
                        findViewById(R.id.gallery_root_layout).setVisibility(View.GONE);
                        break;
                    case 2:
                        ViewGroup container = findViewById(R.id.container);
                        container.removeView(findViewById(R.id.github_root_layout));
                        break;
                }
            }
        });

        View tab1 = getLayoutInflater().inflate(R.layout.activity_phonebook, null);
        item_selector.addView(tab1);

        mListview = (ListView) findViewById(R.id.listview);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent_detail = new Intent(getApplicationContext(), information.class);
                intent_detail.putExtra("Name", dataList.get(i).get("name"));
                intent_detail.putExtra("Phone", dataList.get(i).get("phone"));
                startActivityForResult(intent_detail, 0);
            }
        });

        findViewById(R.id.fab).setVisibility(View.GONE);


        View tab2 = getLayoutInflater().inflate(R.layout.activity_gallery, null);
        item_selector.addView(tab2);
        setGalleryAdapter();


    }



    ArrayList<Map<String, String>> dataList;
    ListView mListview;
    String Jsondata;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(0,1, data);
        super.onActivityResult(0,2, data);
        switch(resultCode){
            case 1:
                refresh();
                break;
            case 2:
                refresh();
                break;
        }

    }


    public void parsing_json(){
        Jsondata = "";
        if(dataList.size()!=0) {
            Jsondata ="["+ "{\"name\"" + ":" + "\"" + dataList.get(0).get("name") + "\"" + ","
                    + "\"phone\"" + ":" + "\"" + dataList.get(0).get("phone") + "\"" + "}";
            for (int i = 1; i < dataList.size(); i++) {
                String tempJson = ","+"{\"name\"" + ":" + "\"" + dataList.get(i).get("name") + "\"" + ","
                        + "\"phone\"" + ":" + "\"" + dataList.get(i).get("phone") + "\"" + "}";
                Jsondata = Jsondata + tempJson;
            }
            Jsondata = Jsondata + "]";
        }
    }

    public void deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
    }

    public void refresh(){
        dataList = new ArrayList<Map<String,String>>();
        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc");
        while (c.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            // 연락처 id 값
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            // 연락처 대표 이름
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            map.put("name", name);
            // ID로 전화 정보 조회
            Cursor phoneCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null, null);
            // 데이터가 있는 경우
            String number = "";
            if (phoneCursor.moveToFirst()) {
                number = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                map.put("phone", number);
            }
            phoneCursor.close();
            dataList.add(map);
        }// end while
        c.close();
        parsing_json();
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
                dataList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phone"},
                new int[]{android.R.id.text1, android.R.id.text2});
        mListview.setAdapter(adapter);
    }


    //연락처 추가
    public void ContactAdd(final String name11, final String phone11){
        Thread thread = new Thread(){
            @Override
            public void run() {

                ArrayList<ContentProviderOperation> list = new ArrayList<>();
                try{
                    list.add(
                            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                    .build()
                    );

                    list.add(
                            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name11)   //이름

                                    .build()
                    );

                    list.add(
                            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone11)           //전화번호
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE  , ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)   //번호타입(Type_Mobile : 모바일)

                                    .build()
                    );

                    list.add(
                            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Email.DATA  , "")  //이메일
                                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE  , ContactsContract.CommonDataKinds.Email.TYPE_WORK)     //이메일타입(Type_Work : 직장)

                                    .build()
                    );

                    getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, list);  //주소록추가
                    list.clear();   //리스트 초기화
                }catch(RemoteException e){
                    e.printStackTrace();
                }catch(OperationApplicationException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try{thread.join();}catch (InterruptedException e){}
    }


    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btnAddress:
                refresh();
                break;

            case R.id.button2:
                Context context = DemoActivity.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder add = new AlertDialog.Builder(DemoActivity.this);

                add.setTitle("추가하기");
                //add.setMessage("이름");
                final TextView name2 = new TextView(context);
                name2.setText("이름");
                layout.addView(name2);
                final EditText name1 = new EditText(context);
                //name1.setHint("이름");
                layout.addView(name1);
                //add.setView(name1);
                final TextView phone2 = new TextView(context);
                phone2.setText("전화번호");
                layout.addView(phone2);
                final EditText phone1 = new EditText(context);
                phone1.setInputType(0x00000003);
                //phone1.setHint("전화번호");
                layout.addView(phone1);
                //add.setView(phone1);

                add.setView(layout);

                add.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( name1.getText().toString().length() == 0 ) {
                            AlertDialog.Builder add2 = new AlertDialog.Builder(DemoActivity.this);
                            add2.setTitle("공백입니다");
                            add2.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            add2.show();


                        } else {
                            String value1 = name1.getText().toString();
                            String value2 = phone1.getText().toString();
                            ContactAdd(value1, value2);
                            refresh();
                        }
                    }
                });

                add.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                add.show();

                refresh();
                break;


        }
    }


    GalleryAdapter galleryAdapter;
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
    public void setGalleryAdapter(){
        GridView gridView = findViewById(R.id.galleryView);
        ViewGroup root = findViewById(R.id.gallery_root_layout);
        int page_size = 20;
        img_path = loadGallery();
        if(img_path.size()>gallery_page*page_size)
            img_path.subList(0,gallery_page*page_size).clear();
        else if(gallery_page==0){}
        else{
            Toast.makeText(this,"Last page",Toast.LENGTH_SHORT).show();
            gallery_page--;
            return;
        }

        if(img_path.size()>page_size)
            img_path.subList(page_size,img_path.size()).clear();
        //set adapter
        galleryAdapter = new GalleryAdapter(getApplicationContext(),img_path);
        gridView.setAdapter(galleryAdapter);
    }
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
                //set parameter of imageView
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int widthPixels = displayMetrics.widthPixels;
                int heightPixels = displayMetrics.heightPixels;

                //read image from image path string
                File f = new File((String) getItem(position));
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
                //set image
                ImageView imageView = new ImageView(getApplicationContext());
                image =  Bitmap.createScaledBitmap(image,widthPixels/4,heightPixels/6,false);
                imageView.setImageBitmap(image);
                imageView.setTag((String) getItem(position));
                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, heightPixels/6));

                //set image onclick listener, show image int big size
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        /*
                            this is function that show only one picture at big size
                        */
                        /*ImageView imageView = new ImageView(getApplicationContext());
                        ViewGroup root = findViewById(R.id.gallery_root_layout);
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
                        */
                        Intent intent = new Intent(getApplicationContext(),GalleryMinigameActivity.class);
                        intent.putExtra("image",view.getTag().toString());
                        startActivity(intent);
                        return true;
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*
                            this is function that show only one picture at big size
                        */
                        /*ImageView imageView = new ImageView(getApplicationContext());
                        ViewGroup root = findViewById(R.id.gallery_root_layout);
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
                        */
                        Intent intent = new Intent(getApplicationContext(),picture_expansion.class);
                        intent.putExtra("image",view.getTag().toString());
                        startActivity(intent);

                    }
                });
                return imageView;
            }catch (FileNotFoundException e){
                e.printStackTrace();
                return null;
            }
        }

    }


    public void getGithubLog(final String url){

        final ArrayList<Integer> graph_points = new ArrayList<>();
        //new tread for get html of github
        Thread thread = new Thread(new Runnable() {


            @Override
            public void run() {
                //stored data
                Map<String, Integer> month = new HashMap<String, Integer>();
                month.put("Jan",1);month.put("Feb",2);month.put("Mar",3);month.put("Apr",4);month.put("May",5);month.put("Jun",6);month.put("Jul",7);month.put("Aug",8);month.put("Sep",9);month.put("Oct",10);month.put("Nov",11);month.put("Dec",12);
                final ArrayList<String[]> data = new ArrayList<String[]>();
                try  {
                    //access github
                    Document github_doc = Jsoup.connect(url).ignoreHttpErrors(true).get();


                    //set day list
                    Elements list_of_day = github_doc.getElementsByClass("commit-group-title");
                    ArrayList<Integer[]> days = new ArrayList<>();
                    for(Element day : list_of_day){
                        int index = day.toString().indexOf("Commits on ")+11;
                        Integer[] date = new Integer[3];
                        String day_str=day.toString().substring(index);
                        date[1] = month.get(day_str.substring(0,3));                    //mm
                        day_str=day_str.substring(4);
                        date[2] = Integer.parseInt(day_str.split(", ")[0]);     //dd
                        date[0] = Integer.parseInt(day_str.split(", ")[1].substring(0,4));     //yy
                        days.add(date);
                    }

                    //get elements day-by-day
                    Elements list_by_day = github_doc.getElementsByClass("commit-group table-list table-list-bordered");


                    for(Element list_one_day : list_by_day){
                        String[] unit_data = new String[3];
                        int[] commit_data = {0,0};
                        //get elements commit-by-commit
                        Elements all_commit = list_one_day.getElementsByClass("commit commits-list-item table-list-item js-navigation-item js-details-container Details js-socket-channel js-updatable-content");
                        for(Element one_commit : all_commit){
                            //get each commit history link, and connect
                            String commit_link = one_commit.getElementsByClass("message").first().attr("href");
                            Document commit_doc = Jsoup.connect("https://github.com"+commit_link).ignoreHttpErrors(true).get();

                            //get commit history title bar, which contain the number of added and deleted line
                            String add_del_log = commit_doc.getElementsByClass("toc-diff-stats").first().toString();


                            //get add line number
                            ArrayList<Character> number = new ArrayList<>();
                            if(add_del_log.contains(" addition")) {
                                for (int i = add_del_log.indexOf(" addition") - 1; add_del_log.charAt(i) != '>'; i--) {
                                    number.add(0,add_del_log.charAt(i));
                                }
                                commit_data[0]+=Integer.parseInt(GithubActivity.getNumber(number));
                            }
                            else{
                                //??
                            }
                            //get delete line number
                            if(add_del_log.contains(" deletion")) {
                                number = new ArrayList<>();
                                for (int i = add_del_log.indexOf(" deletion") - 1; add_del_log.charAt(i) != '>'; i--) {
                                    number.add(0, add_del_log.charAt(i));
                                }
                                commit_data[1]+=Integer.parseInt(GithubActivity.getNumber(number));
                            }
                            else{
                                //??
                            }
                            if(data.size()>=GithubActivity.getHistoryNum())
                                break;
                        }
                        unit_data[0]="+"+commit_data[0];
                        unit_data[1]="-"+commit_data[1];
                        Integer[] day = days.get(0);
                        days.remove(0);
                        unit_data[2]=day[1] + "-" + day[2];
                        data.add(unit_data);
                        graph_points.add(commit_data[0]);
                        if(data.size()>=GithubActivity.getHistoryNum())
                            break;
                    }

                    //str.add(doc.title());
                    //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout listView = findViewById(R.id.github_history_layout);
                        GithubActivity.setHorizontalScrollView(listView,data,getApplicationContext());
                    }
                });
            }
        });
        thread.start();
        LinearLayout layout = findViewById(R.id.github_history_layout);
        if (layout.getChildCount() >= GithubActivity.getHistoryNum())
            thread.interrupt();
        try {
            thread.join();

            //그래프에 들어갈 점 배열
            GraphView graphview = (GraphView) findViewById(R.id.github_graph);
            Toast.makeText(getApplicationContext(), graph_points.toString(), Toast.LENGTH_LONG).show();
            graphview.setPoints(graph_points, 1, 0, 1000);
            graphview.drawForBeforeDrawView();
        } catch (InterruptedException e) {
            Log.d("graph error","?");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
