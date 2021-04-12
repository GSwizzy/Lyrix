package com.gabriel.lyrixapplication.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gabriel.lyrixapplication.R;
import com.gabriel.lyrixapplication.model.Song;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SongController extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView lyricsView;
    private TextView songNameView;
    private TextView artistNameView;
    private Toolbar myToolbar;
    private RelativeLayout layout;
    private GradientDrawable gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_controller);
        instantiate();

        Gson gson = new Gson();
        String songAsString = getIntent().getStringExtra("song");
        Song song = gson.fromJson(songAsString, Song.class);



            if (!song.getLyrics().isEmpty()  && !song.getName().isEmpty() && !song.getArtist().isEmpty()) {
                viewLyrics(song);

                Map<String, String> songObject = new HashMap<>();
                songObject.put("name", song.getName());
                songObject.put("artist", song.getArtist());
                songObject.put("lyrics", song.getLyrics());

                db.collection("Song").document()
                        .set(songObject);
            } else

                songNameView.setText(R.string.not_found);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lyrics_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.saveSongMenu:

                Gson gson = new Gson();
                String songAsString = getIntent().getStringExtra("song");
                Intent intent = new Intent(this, SavedListController.class);
                Song song = gson.fromJson(songAsString, Song.class);

                if(!song.getLyrics().isEmpty()){
                    intent.putExtra("song", songAsString);
                    startActivityForResult(intent, RESULT_OK);
                }
                else {
                    Toast.makeText(this, "Song could not be saved", Toast.LENGTH_SHORT).show();
                }



                break;
            case R.id.returnMenu:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("clear", "");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                break;





        }

        return true;
    }


    public void viewLyrics(Song song){
        lyricsView.setText(song.getLyrics());
        songNameView.setText(song.getName());
        artistNameView.setText(song.getArtist());
    }
    void instantiate(){
        db = FirebaseFirestore.getInstance();
        lyricsView = findViewById(R.id.lyricsView);
        lyricsView.setMovementMethod(new ScrollingMovementMethod());
        songNameView = findViewById(R.id.songTitleView);
        artistNameView = findViewById(R.id.songArtistView);
        myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        layout = findViewById(R.id.recyclerHolder);
        gd = new GradientDrawable();
        gd.setColor(Color.RED);
        gd.setCornerRadius(50);
        gd.setStroke(5, Color.rgb(187, 134, 252));
        gd.setColor(303030);
        layout.setBackground(gd);
    }


}