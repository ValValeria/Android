package com.example.calls.ui.add_contact;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.calls.R;
import com.example.calls.models.Contact;


public class AddContactFragment extends Fragment {

    public AddContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    private void addNewContact(){
        Contact contact = new Contact();
        contact.setName("calls bot");
        contact.setPhoneNumber("+3800998736224");

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, 1);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getName());
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName());
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber());
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        requireActivity().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
    }
}