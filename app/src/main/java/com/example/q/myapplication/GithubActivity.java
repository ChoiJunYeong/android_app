package com.example.q.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import java.util.HashSet;
import java.util.Set;

public class GithubActivity  {
    static int num_of_history = 5;

    public static int getHistoryNum(){
        return num_of_history;
    }
    public static void setHorizontalScrollView(LinearLayout view,ArrayList<String[]> data,Context context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        for(int position = 0;position<5;position++) {
            if(data.size()<=position)
                break;
            ViewGroup convertView = (ViewGroup)view.getChildAt(position+1);
            TextView add_txt = (TextView) convertView.getChildAt(0);
            TextView del_txt = (TextView) convertView.getChildAt(1);
            TextView date_txt = (TextView) convertView.getChildAt(2);
            if (data.get(position)[0] != null)
                add_txt.setText(data.get(position)[0]);
            else
                add_txt.setText('-');
            if (data.get(position)[1] != null)
                del_txt.setText(data.get(position)[1]);
            else
                del_txt.setText('-');
            if (data.get(position)[2] != null)
                date_txt.setText(data.get(position)[2]);
            else
                date_txt.setText('-');
        }
    }

    public static void setRepository(final Context context){
        final ArrayList<String> ret_value = new ArrayList<>();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        final EditText input = new EditText(context);
        builder.setTitle("set repository")
                .setMessage("please input repository link.\nex) ChoiJunYeong/android_app")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://github.com/"+input.getText().toString();
                        url+="/commits";
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("URL",url);
                        editor.apply();

                        SharedPreferences log_preference =  context.getSharedPreferences("git-logs",Context.MODE_PRIVATE);
                        SharedPreferences.Editor pref_editor = log_preference.edit();
                        pref_editor.clear();
                        pref_editor.apply();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(input)
                .show();
    }

    //get number
    //ex) adfvcxnm 31 acvt --> 31
    public static  String getNumber(ArrayList<Character> num_array){
        int num = 0;
        for(int i=0;i<num_array.size();i++){
            int temp = num_array.get(i);
            if(temp>'0' && temp<'9'){
                num = num*10+(temp-'0');
            }
        }
        return  Integer.toString(num);
    }

    public static Set<String> saveLog(Context context,Set<String> new_key_set){
        //get old key
        SharedPreferences log =  context.getSharedPreferences("git-logs",Context.MODE_PRIVATE);

        Set<String> old_key_set = log.getStringSet("git-keys",null);
        //update
        SharedPreferences.Editor editor = log.edit();
        editor.putStringSet("git-keys",new_key_set);
        editor.apply();
        //get difference
        if(old_key_set==null)
            return new_key_set;

        new_key_set.removeAll(old_key_set);
        return new_key_set;
    }

}
