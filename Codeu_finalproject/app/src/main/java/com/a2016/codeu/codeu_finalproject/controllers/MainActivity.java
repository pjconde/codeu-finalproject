package com.a2016.codeu.codeu_finalproject.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.a2016.codeu.codeu_finalproject.R;
import com.a2016.codeu.codeu_finalproject.models.JedisIndex;
import com.a2016.codeu.codeu_finalproject.models.JedisMaker;
import com.a2016.codeu.codeu_finalproject.models.WikiSearch;

import java.io.IOException;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onClick(View v) throws IOException {
        // This gets the search box and allows us to check if it is empty
        EditText searchBox = (EditText) findViewById(R.id.search_input);
        boolean searchEmpty = TextUtils.isEmpty(searchBox.getText().toString());
        if (!searchEmpty) {
            // If the search box is not empty we retrieve the string
            String searchWord = searchBox.getText().toString();

            Jedis jedis = JedisMaker.make();
            JedisIndex index = new JedisIndex(jedis);

            Map<String, Integer> tempMap = null;
            WikiSearch temp = new WikiSearch(tempMap);
            WikiSearch search = temp.search(searchWord, index);

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("WS_results", search);
            intent.putExtra("searched", searchWord);
            startActivity(intent);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
