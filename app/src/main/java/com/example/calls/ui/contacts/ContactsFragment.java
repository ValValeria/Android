package com.example.calls.ui.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.calls.R;
import com.example.calls.models.Contact;
import com.example.calls.ui.contact.ContactFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ContactsFragment extends Fragment {
    private ConcurrentLinkedDeque<Contact> contacts = new ConcurrentLinkedDeque<>();
    private LinearLayout linearLayout;
    private static final String TAG = ContactsFragment.class.getName();
    private NavController navController;

    public ContactsFragment(){
        super(R.layout.contacts);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayout = view.findViewById(R.id.contacts_result);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
    }

    @Override
    public void onStart() {
        super.onStart();

        loadContacts();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.contacts.clear();
        this.linearLayout.removeAllViews();
        this.linearLayout.invalidate();

        this.loadContacts();
    }

    public static List<Contact> getContacts(ContentResolver cr){
        final Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        final ArrayList<Contact> contacts = new ArrayList<>();

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);

                        Contact contact = new Contact();
                        contact.setName(name);
                        contact.setPhoneNumber(phoneNo);

                        contacts.add(contact);
                    }

                    pCur.close();
                }
            }
        }

        return contacts;
    }

    private void loadContacts(){
        final ContentResolver cr = requireActivity().getContentResolver();

        this.contacts.addAll(getContacts(cr));

        if(contacts.size() == 0){
            noResults();
        } else {
            for(Contact contact: contacts){
                addView(contact);
            }
        }
    }

    private void addView(Contact contact){
        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
        View view = layoutInflater.inflate(R.layout.contact_card, linearLayout, false);

        TextView textView = view.findViewById(R.id.name);
        textView.setText(contact.getName());

        TextView textView2 = view.findViewById(R.id.time);
        textView2.setText(contact.getPhoneNumber());

        Button button = view.findViewById(R.id.view_contact);
        button.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ContactFragment.PHONE_ARG, contact.getPhoneNumber());

            navController.navigate(R.id.nav_contact, bundle);
        });

        linearLayout.addView(view);
    }

    private void noResults() {
        LayoutInflater layoutInflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View noResultsView = layoutInflater.inflate(R.layout.no_results, linearLayout, false);

        Button button = noResultsView.findViewById(R.id.add_new_contact_btn);
        button.setOnClickListener(v -> {
            navController.navigate(R.id.nav_add_contact);
        });

        linearLayout.addView(noResultsView);
        linearLayout.invalidate();
        
        Log.i(TAG, "noResults method is called");
    }
}
