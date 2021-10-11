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
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;


public class MessageFragment extends Fragment {
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.sms_btn);
        button.setOnClickListener(this::sendMessage);
    }

    private void sendMessage(View view){
        TextInputEditText number = requireView().findViewById(R.id.messageTxt);
        String numberText = number.getText().toString();

        TextInputEditText message = requireView().findViewById(R.id.phoneNumber);
        String messageText = message.getText().toString();

        SmsManager.getDefault()
                .sendTextMessage(numberText, null, messageText, null, null);

        Toast.makeText(requireContext(), "The message is sent", Toast.LENGTH_SHORT).show();
    }
}