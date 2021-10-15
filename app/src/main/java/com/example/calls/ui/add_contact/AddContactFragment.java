package com.example.calls.ui.add_contact;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
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
import androidx.navigation.NavController;
import java.util.ArrayList;


public class AddContactFragment extends Fragment {
    private TextInputEditText email;
    private TextInputEditText phoneNumber;
    private TextInputEditText name;
    private NavController navController;

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

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();

        Button button = view.findViewById(R.id.add_contact_btn);
        button.setOnClickListener(this::addNewContact);
    }

    private void addNewContact(View view){
        Contact contact = new Contact();
        contact.setName(this.name.getText().toString());
        contact.setPhoneNumber(this.phoneNumber.getText().toString());

        String message = "The contact is added";

        try{
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ContentResolver contentResolver = requireActivity().getContentResolver();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            contact.getName()).build());

            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());

            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);

            requireView().postDelayed(() -> {
                navController.navigate(R.id.nav_contacts);
            }, Snackbar.LENGTH_LONG + 300);
        }catch(Exception e){
            e.printStackTrace();

            message = "Please, try again";
        }

        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}