package com.example.calls.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calls.MainActivity;
import com.example.calls.R;
import com.example.calls.models.Contact;
import com.example.calls.ui.contact.ContactFragment;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    LayoutInflater layoutInflater;
    List<Contact> contacts;
    NavController navController;

    public SearchAdapter(Context context, List<Contact> contacts, NavController navController){
        this.contacts = contacts;
        this.navController = navController;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.search_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.textView.setText(contact.getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ContactFragment.PHONE_ARG, contact.getPhoneNumber());

                navController.navigate(R.id.nav_contact, bundle);
                MainActivity.clickEvent.onNext(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public Button button;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.name);
            button = itemView.findViewById(R.id.view_contact);
        }
    }
}
