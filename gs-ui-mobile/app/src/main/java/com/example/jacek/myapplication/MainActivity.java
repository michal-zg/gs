package com.example.jacek.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jacek.myapplication.data.Event;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

//    @Bind({ R.id.title, R.id.subtitle, R.id.hello })
//    List<View> headerViews;

    ArrayAdapter adapter;

    @Bind(R.id.listViewEvents)
    ListView listViewEvents;

//    @OnItemClick(R.id.listViewEvents) void onItemClick(int position) {
//        Toast.makeText(this, "You clicked: " + adapter.getItem(position), LENGTH_SHORT).show();
//    }

//    @OnItemClick(R.id.fab)
//    public void onAddClick(int position) {
//        Intent intent = new Intent(MainActivity.this, AddActivity.class);
//
//        MainActivity.this.startActivity(intent);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        ArrayAdapter adapter = new ArrayAdapter<Event>(this, R.layout.list_events_row, new ArrayList<Event>()) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                EventItemLayout tli;

                if (null == convertView) {
                    tli = (EventItemLayout) View.inflate(getContext(), R.layout.list_events_row, null);
                } else {
                    tli = (EventItemLayout) convertView;
                }

//                tli.setData(mTasks.get(position));


                return tli;
            }
        };

        listViewEvents.setAdapter(adapter);
    }

    @Override
    protected void onResume() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ArrayAdapter) listViewEvents.getAdapter()).notifyDataSetChanged();
            }
        });

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
