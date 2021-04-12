package com.gabriel.lyrixapplication.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gabriel.lyrixapplication.R;
import com.gabriel.lyrixapplication.data.MySingleton;
import com.gabriel.lyrixapplication.model.Song;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class HomeController extends AppCompatActivity {

    private GradientDrawable gd;

    private EditText nameText;
    private EditText artistText;

    private Button button;

    private String artist;
    private String songName;

    private TextView errorTV;

    private Toolbar myToolbar;

    private RelativeLayout layout;

    private ImageView image;

    private Random r;

    private Integer[] images = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instantiate();


        //API CALL TO RETRIEVE LYRICS
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!nameText.getText().toString().isEmpty() && !artistText.getText().toString().isEmpty()) {
                    errorTV.setText("");
                    songName = nameText.getText().toString().trim().toUpperCase();
                    artist = artistText.getText().toString().trim().toUpperCase();

                    findSong(songName, artist);

                } else {
                    errorTV.setText(R.string.error);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.savedMenu:
                Intent savedIntent = new Intent(HomeController.this, SavedListController.class);
                startActivity(savedIntent);
                break;
            case R.id.historyMenu:
                Intent historyIntent = new Intent(HomeController.this, HistoryController.class);
                startActivity(historyIntent);
                break;
        }
        return true;
    }

    void instantiate() {
        nameText = findViewById(R.id.nameText);
        artistText = findViewById(R.id.artistText);
        button = findViewById(R.id.searchButton);
        errorTV = findViewById(R.id.errorView);
        myToolbar = findViewById(R.id.myToolbar);
        layout = findViewById(R.id.form);
        setSupportActionBar(myToolbar);


        gd = new GradientDrawable();
        gd.setColor(Color.RED);
        gd.setCornerRadius(50);
        gd.setStroke(5, Color.rgb(187, 134, 252));
        gd.setColor(303030);


        layout.setBackground(gd);

        r = new Random();
        image = findViewById(R.id.album);
        image.setImageResource(images[r.nextInt(images.length)]);

    }

    public void findSong(String songName, String artist) {
        String url = "https://api.lyrics.ovh/v1/" + artist + "/" + songName;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(HomeController.this, SongController.class);
                        //SET VALUES TO MODEL

                        Song song = new Song();
                        song.setArtist(artist);
                        song.setName(songName);
                        try {
                            song.setLyrics(response.getString("lyrics"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //SEND SONG TO SONGCONTROLLER
                        Gson gson = new Gson();
                        String songAsString = gson.toJson(song);
                        intent.putExtra("song", songAsString);

                        startActivityForResult(intent, 1);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String empty = data.getStringExtra("clear");
                nameText.setText(empty);
                artistText.setText(empty);

            }
        }
    }
}