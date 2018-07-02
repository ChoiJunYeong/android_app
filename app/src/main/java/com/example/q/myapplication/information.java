package com.example.q.myapplication;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class information extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);


        Intent intent = getIntent();
        final String name = intent.getStringExtra("Name");
        final String phone = intent.getStringExtra("Phone");

        TextView nameView = (TextView) findViewById(R.id.info_name);
        TextView phoneView = (TextView) findViewById(R.id.info_phone);

        nameView.setText(name);
        phoneView.setText(phone);

        Button btnDelete = (Button) findViewById(R.id.delete_info);
        Button btnoutofway = (Button) findViewById(R.id.out_info);
        Button btnChange = (Button) findViewById(R.id.change_info);
        btnoutofway.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent outIntent = new Intent(getApplicationContext(), phonebook.class);
                setResult(RESULT_OK, outIntent);
                finish();
            }
        });



        btnChange.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                Context context = information.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder add = new AlertDialog.Builder(information.this);

                add.setTitle("수정하기");
                //add.setMessage("이름");
                final TextView name2 = new TextView(context);
                name2.setText("이름");
                layout.addView(name2);
                final EditText name1 = new EditText(context);
                name1.setHint(name);
                name1.setText(name);
                layout.addView(name1);
                //add.setView(name1);
                final TextView phone2 = new TextView(context);
                phone2.setText("전화번호");
                layout.addView(phone2);
                final EditText phone1 = new EditText(context);
                phone1.setInputType(0x00000003);
                phone1.setHint(phone);
                phone1.setText(phone);
                layout.addView(phone1);
                //add.setView(phone1);

                add.setView(layout);

                add.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( name1.getText().toString().length() == 0 ) {
                            AlertDialog.Builder add2 = new AlertDialog.Builder(information.this);
                            add2.setTitle("공백입니다");
                            add2.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            add2.show();


                        } else {
                            final String value1 = name1.getText().toString();
                            final String value2 = phone1.getText().toString();
                            Thread thread2 = new Thread(){
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
                                                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, value1)   //이름

                                                        .build()
                                        );

                                        list.add(
                                                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, value2)           //전화번호
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
                            thread2.start();
                            try{thread2.join();}catch (InterruptedException e){}

                            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
                            Cursor cur = getContentResolver().query(contactUri, null, null, null, null);
                            try {
                                if (cur.moveToFirst()) {
                                    do {
                                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                                            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                                            getContentResolver().delete(uri, null, null);
                                        }

                                    } while (cur.moveToNext());
                                }
                            } catch (Exception e) {
                                System.out.println(e.getStackTrace());
                            } finally {
                                cur.close();
                            }

                            Intent outIntent = new Intent(getApplicationContext(), phonebook.class);
                            setResult(2, outIntent);
                            finish();

                        }
                    }
                });

                add.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                add.show();
                }
        });



        btnDelete.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder check = new AlertDialog.Builder(information.this);
                check.setTitle("연락처를 삭제하겠습니까?");
                check.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                check.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
                        Cursor cur = getContentResolver().query(contactUri, null, null, null, null);
                        try {
                            if (cur.moveToFirst()) {
                                do {
                                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                                        getContentResolver().delete(uri, null, null);
                                    }

                                } while (cur.moveToNext());
                            }
                        } catch (Exception e) {
                            System.out.println(e.getStackTrace());
                        } finally {
                            cur.close();
                        }

                        Intent outIntent = new Intent(getApplicationContext(), phonebook.class);
                        setResult(1, outIntent);
                        finish();
                    }
                });
                check.show();

            }
        });
    }
}