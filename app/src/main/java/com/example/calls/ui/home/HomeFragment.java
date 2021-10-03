package com.example.calls.ui.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.calls.R;
import com.example.calls.models.Call;
import com.example.calls.models.Contact;
import com.example.calls.ui.contact.ContactFragment;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;
    private final ArrayList<Call> list = new ArrayList<>();
    private LinearLayout linearLayout;
    private final String[] projection = new String[] {
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE
    };
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayout = view.findViewById(R.id.calls);

        int hasReadContactPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS);

        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }else{
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }

        if (READ_CONTACTS_GRANTED){
            loadContacts();
        }
    }

    public void loadContacts(){
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, null);

        if(cursor != null){
            while(cursor.moveToNext()){
                String contact = cursor.getString(0);
                String number = cursor.getString(1);
                String date = cursor.getString(3);

                Call call = new Call(contact);
                call.setNumber(number);
                call.setDate(date);

                list.add(call);
                addView();
            }

            if(list.size() == 0){
                addNewContact();
            }

            atomicInteger.incrementAndGet();
            cursor.close();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void addView(){
        Call call = list.get(list.size() - 1);

        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
        View view = layoutInflater.inflate(R.layout.contact_card, linearLayout, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ContactFragment.PHONE_ARG, call.getNumber());

               // navController.navigate(R.id.nav_contact, bundle);
            }
        });

        TextView textView = requireView().findViewById(R.id.name);
        textView.setText(call.getName());

        TextView textView2 = requireView().findViewById(R.id.time);
        textView2.setText(call.getDate());

        linearLayout.addView(view);
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