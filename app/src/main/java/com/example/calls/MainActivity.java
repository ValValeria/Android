package com.example.calls;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableBoolean;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calls.adapters.SearchAdapter;
import com.example.calls.databinding.ActivityMainBinding;
import com.example.calls.models.Contact;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnCloseListener, SearchView.OnQueryTextListener {
    private NavController navController;
    public final ObservableBoolean showNav = new ObservableBoolean(true);
    public RecyclerView recyclerView;
    public SearchAdapter searchAdapter;
    public final List<Contact> searchResults = new ArrayList<>();
    public ActivityMainBinding activityMainBinding;
    public static PublishSubject<Boolean> clickEvent = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        activityMainBinding.setShowNav(showNav);
        setContentView(activityMainBinding.getRoot());

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        clickEvent.subscribe((v) -> this.onClose());

        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id = item.getGroupId();
        NavDestination navDestination = navController.getGraph().findNode(id);

        if(navDestination != null){
            navController.navigate(id);
            return true;
        }

        return false;
    }

    @Override
    public boolean onClose() {
        this.showNav.set(true);
        this.searchResults.clear();
        this.searchAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return handleChangeSearch(query);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return handleChangeSearch(newText);
    }

    private boolean handleChangeSearch(String txt){
        this.showNav.set(false);
        this.searchResults.clear();
        this.searchAdapter.notifyDataSetChanged();

        ContentResolver contentResolver = getContentResolver();
        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
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

                        if(phoneNo.contains(txt) || name.contains(txt)){
                            Contact contact = new Contact();
                            contact.setPhoneNumber(phoneNo);
                            contact.setName(name);

                            searchResults.add(contact);
                            Log.i(MainActivity.class.getName(), "The result is added");
                        }
                    }

                    pCur.close();
                    searchAdapter.notifyDataSetChanged();
                }
            }
        }

        return true;
    }

    private void setupUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = activityMainBinding.searchResults;
        searchAdapter = new SearchAdapter(this, searchResults, navController);
        recyclerView.setAdapter(searchAdapter);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}