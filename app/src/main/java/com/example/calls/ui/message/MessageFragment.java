package com.example.calls.ui.message;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.calls.R;
import com.example.calls.ui.contact.ContactFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;


public class MessageFragment extends Fragment {
    private String phoneNumber;
    private TextInputEditText numberField;
    
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        phoneNumber = requireArguments().getString(ContactFragment.PHONE_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        numberField = view.findViewById(R.id.phoneNumber);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        numberField.setText(phoneNumber); 
        Button button = view.findViewById(R.id.sms_btn);
        button.setOnClickListener(this::sendMessage);
    }

    private void sendMessage(View view){
        numberField = requireView().findViewById(R.id.messageTxt);
        String numberText = numberField.getText().toString();

        TextInputEditText messageField = requireView().findViewById(R.id.phoneNumber);
        String messageText = messageField.getText().toString();

        SmsManager.getDefault()
                .sendTextMessage(numberText, null, messageText, null, null);

        Toast.makeText(requireContext(), "The message is sent", Toast.LENGTH_SHORT).show();
    }
}