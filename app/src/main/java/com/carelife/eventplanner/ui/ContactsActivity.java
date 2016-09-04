package com.carelife.eventplanner.ui;

/**
 * Created by carelife on 2016/8/11.
 */
import java.io.InputStream;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.carelife.eventplanner.R;

public class ContactsActivity extends ListActivity {

    Context mContext = null;

    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private static final int PHONES_NUMBER_INDEX = 1;
    private static final int PHONES_PHOTO_ID_INDEX = 2;
    private static final int PHONES_CONTACT_ID_INDEX = 3;


    private ArrayList<String> mContactsName = new ArrayList<>();
    private ArrayList<String> mContactsNumber = new ArrayList<>();
    private ArrayList<Bitmap> mContactsPhoto = new ArrayList<>();

    ListView mListView = null;
    MyListAdapter myAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = this;
        mListView = this.getListView();
        getPhoneContacts();

        myAdapter = new MyListAdapter(this);
        setListAdapter(myAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                Intent i = new Intent();
                i.putExtra("Name", mContactsName.get(position));
                i.putExtra("Phone", mContactsNumber.get(position));
                setResult(RESULT_OK, i);
                finish();
            }
        });

        super.onCreate(savedInstanceState);
    }

    private void getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();

        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);


        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                Bitmap contactPhoto;
                if(photoid > 0 ) {
                    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsPhoto.add(contactPhoto);
            }

            phoneCursor.close();
        }
    }

    class MyListAdapter extends BaseAdapter {
        public MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return mContactsName.size();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iamge = null;
            TextView title = null;
            TextView text = null;
            if (convertView == null || position < mContactsNumber.size()) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.activity_contactlist, null);
                iamge = (ImageView) convertView.findViewById(R.id.color_image);
                title = (TextView) convertView.findViewById(R.id.color_title);
                text = (TextView) convertView.findViewById(R.id.color_text);
            }
            title.setText(mContactsName.get(position));
            text.setText(mContactsNumber.get(position));
            iamge.setImageBitmap(mContactsPhoto.get(position));
            return convertView;
        }

    }
}