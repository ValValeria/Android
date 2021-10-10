package com.example.calls.ui.add_contact;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.calls.R;
import com.example.calls.models.Contact;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;


public class AddContactFragment extends Fragment {
    private TextInputEditText email;
    private TextInputEditText phoneNumber;
    private TextInputEditText name;

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

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.emailInput);
        name = view.findViewById(R.id.nameInput);
        phoneNumber = view.findViewById(R.id.phoneInput);

        Button button = view.findViewById(R.id.add_contact_btn);
        button.setOnClickListener(this::addNewContact);
    }

    private void addNewContact(View view){
        Contact contact = new Contact();
        contact.setName(this.name.getText().toString());
        contact.setPhoneNumber(this.phoneNumber.getText().toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, 1);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getName());
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName());
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber());
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        requireActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);

        Snackbar snackbar = Snackbar.make(requireView(), "The contact is added", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}