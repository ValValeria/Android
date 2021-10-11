package com.example.calls.ui.contact;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.calls.BR;
import com.example.calls.R;
import com.example.calls.models.Contact;
import org.jetbrains.annotations.NotNull;

public class ContactFragment extends Fragment {
    public static final String PHONE_ARG = "PHONE_ARG";
    private String phoneNumber;
    private ViewDataBinding contactFragmentBinding;
    private final Contact contact = new Contact();
    private NavController navController;
    private final String TAG = ContactFragment.class.getName();

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        phoneNumber = requireArguments().getString(PHONE_ARG);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        contactFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact, container, false);
        contactFragmentBinding.setVariable(BR.contact, contact);

        return contactFragmentBinding.getRoot();
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
                            contact.setName(name);
                            contact.setPhoneNumber(phoneNo);

                            contactFragmentBinding.setVariable(BR.contact, contact);
                            contactFragmentBinding.invalidateAll();
                        }
                    }

                    if(contact.getPhoneNumber().isEmpty()){
                        Toast.makeText(requireContext(), "The contact is not found", Toast.LENGTH_LONG).show();
                        navController.navigate(R.id.nav_contacts);
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
    }

    public void deleteContact(View view){
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getPhoneNumber()));

        try (Cursor cursor = contentResolver.query(contactUri, null, null, null, null)) {
            if (cursor.moveToNext()) {
                do {
                    if (cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(contact.getName())) {
                        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        contentResolver.delete(uri, null, null);
                        
                        Toast.makeText(requireContext(), "The contact is deleted", Toast.LENGTH_LONG).show();
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
          Log.e(TAG, e.getMessage());
        }
    }

    public void writeAMessage(View view){
        navController.navigate(R.id.nav_sms);
    }

    public void callContact(View view){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhoneNumber()));
        startActivity(intent);
    }
}
