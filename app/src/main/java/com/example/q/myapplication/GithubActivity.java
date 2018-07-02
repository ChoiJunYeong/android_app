package com.example.q.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GithubActivity extends AppCompatActivity {
    GithubAdapter githubAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github);

        //new tread for get html of github
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                //stored data
                final ArrayList<String[]> data = new ArrayList<String[]>();
                try  {

                    //access githun
                    Document github_doc = Jsoup.connect("https://github.com/ChoiJunYeong/android_app/commits").get();

                    //get elements day-by-day
                    Elements list_by_day = github_doc.getElementsByClass("commit-group table-list table-list-bordered");


                    for(Element list_one_day : list_by_day){
                        //get elements commit-by-commit
                        Elements all_commit = list_one_day.getElementsByClass("commit commits-list-item table-list-item js-navigation-item js-details-container Details js-socket-channel js-updatable-content");
                        for(Element one_commit : all_commit){
                            //get each commit history link, and connect
                            String commit_link = one_commit.getElementsByClass("message").first().attr("href");
                            Document commit_doc = Jsoup.connect("https://github.com"+commit_link).get();

                            //get commit history title bar, which contain the number of added and deleted line
                            String add_del_log = commit_doc.getElementsByClass("toc-diff-stats").first().toString();


                            //get add line number
                            ArrayList<Character> number = new ArrayList<>();
                            String[] unit_data = new String[2];
                            if(add_del_log.contains(" addition")) {
                                for (int i = add_del_log.indexOf(" addition") - 1; add_del_log.charAt(i) != '>'; i--) {
                                    number.add(0,add_del_log.charAt(i));
                                }
                                unit_data[0]=getNumber(number) + " addition";
                            }
                            else{
                                unit_data[0]=null;
                            }
                            //get delete line number
                            if(add_del_log.contains(" deletion")) {
                                number = new ArrayList<>();
                                for (int i = add_del_log.indexOf(" deletion") - 1; add_del_log.charAt(i) != '>'; i--) {
                                    number.add(0, add_del_log.charAt(i));
                                }
                                unit_data[1]=getNumber(number) + " deletion";
                            }
                            else{
                                unit_data[1]=null;
                            }
                            data.add(unit_data);
                        }
                    }

                    //str.add(doc.title());
                    //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        githubAdapter = new GithubAdapter(getApplicationContext(),data);
                        LinearLayout listView = findViewById(R.id.github_history_layout);
                        //listView.setAdapter(githubAdapter);
                        setHorizontalScrollView(listView,data);
                    }
                });
            }
        });


        thread.start();

    }
    public void setHorizontalScrollView(LinearLayout view,ArrayList<String[]> data){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for(int position = 0;position<data.size();position++) {
            View convertView = layoutInflater.inflate(R.layout.github_item, null);
            TextView add_txt = (TextView) convertView.findViewById(R.id.add_num);
            TextView del_txt = (TextView) convertView.findViewById(R.id.delete_num);
            if (data.get(position)[0] != null)
                add_txt.setText(data.get(position)[0]);
            if (data.get(position)[1] != null)
                del_txt.setText(data.get(position)[1]);
            view.addView(convertView);
        }
    }
    public String getNumber(ArrayList<Character> num_array){
        int num = 0;
        for(int i=0;i<num_array.size();i++){
            int temp = num_array.get(i);
            if(temp>'0' && temp<'9'){
                num = num*10+(temp-'0');
            }
        }
        return  Integer.toString(num);
    }


    public class GithubAdapter extends BaseAdapter{
        ArrayList<String[]> data;
        Context context;
        LayoutInflater layoutInflater;
        GithubAdapter(Context context, ArrayList<String[]> data){
            super();
            this.data = data;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return data.size();
        }
        public Object getItem(int position) {
            return data.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView= layoutInflater.inflate(R.layout.github_item, null);

            TextView add_txt=(TextView)convertView.findViewById(R.id.add_num);
            TextView del_txt=(TextView)convertView.findViewById(R.id.delete_num);
            if(data.get(position)[0]!=null)
                add_txt.setText(data.get(position)[0]);
            if(data.get(position)[1]!=null)
                del_txt.setText(data.get(position)[1]);



            return convertView;
        }
    }
}
