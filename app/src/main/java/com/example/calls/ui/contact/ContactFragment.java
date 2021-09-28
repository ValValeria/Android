package com.example.calls.ui.contact;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.calls.R;
import com.example.calls.models.Contact;
import org.jetbrains.annotations.NotNull;

public class ContactFragment extends Fragment {
    public static final String PHONE_ARG = "PHONE_ARG";
    private String phoneNumber;

    public ContactFragment() {
        super(R.layout.fragment_contact);
    }

    @Override
    public void onStart() {
        super.onStart();

        ContentResolver contentResolver = requireActivity().getContentResolver();
        @SuppressLint("Recycle") Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if(phoneNo.equals(phoneNumber)){
                            Contact contact = new Contact();
                            contact.setName(name);
                            contact.setPhoneNumber(phoneNo);

                            setUpContact(contact);
                        }
                    }
                    pCur.close();
                }
            }
        }
    }

    @Override
    public void onViewCreated (@NonNull @NotNull View
                                       view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        phoneNumber = requireArguments().getString(PHONE_ARG);
    }

    private void setUpContact(Contact contact){

    }
}
