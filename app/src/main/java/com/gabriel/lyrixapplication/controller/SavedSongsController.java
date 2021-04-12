package com.gabriel.lyrixapplication.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gabriel.lyrixapplication.R;
import com.gabriel.lyrixapplication.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SavedSongsController extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;


    private RecyclerView songsRV;


    private TextView lyrics;


    private String docId;

    private FloatingActionButton deleteButton;
    private FloatingActionButton doneButton;

    private LinearLayout deleteLayout;

    private String songId;
    private List<String> songIds;
    private Toolbar myToolbar;


    private Animation hideButton;
    private Animation showButton;
    private Animation animShake;
    private Animation showLayout;
    private Animation hideLayout;


    private RelativeLayout layout1;
    private RelativeLayout layout2;
    private GradientDrawable gd;

    private boolean deleteMode = false;
    private boolean optionsOpen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_songs_controller);
        instantiate();


        docId = getIntent().getStringExtra("listId");

        initRecyclerViewWithSubCollection("SavedList", "songs");

        songsRV.setHasFixedSize(true);
        songsRV.setLayoutManager(new LinearLayoutManager(this));
        songsRV.setAdapter(adapter);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMode = true;
                Toast.makeText(SavedSongsController.this, "Delete Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteLayout.getVisibility() == View.VISIBLE){
                    deleteLayout.setVisibility(View.INVISIBLE);

                    deleteLayout.startAnimation(hideLayout);

                    doneButton.setAnimation(hideButton);
                    if(deleteMode)
                        Toast.makeText(SavedSongsController.this, "Delete Mode Disabled", Toast.LENGTH_SHORT).show();
                    deleteMode = false;
                    songsRV.clearAnimation();
                    optionsOpen=false;

                }else{
                    songsRV.startAnimation(animShake);
                    deleteLayout.setVisibility(View.VISIBLE);
                    deleteLayout.startAnimation(showLayout);
                    doneButton.setAnimation(showButton);
                    optionsOpen=true;
                }
            }
        });
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        private TextView song;
        private CardView parent;

        public SongViewHolder(@NonNull View songView) {
            super(songView);
            song = songView.findViewById(R.id.name_textView);
            parent = songView.findViewById(R.id.parent_layout);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Song song = (Song) adapter.getItem(getAdapterPosition());
                    if(optionsOpen  && !deleteMode){
                        Toast.makeText(SavedSongsController.this, "Please close options menu", Toast.LENGTH_SHORT).show();
                    }
                    else if (!deleteMode) {
                        lyrics.setText(song.getLyrics());
                    } else {
                       db.collection("SavedList")
                                .document(docId).collection("songs").document(songIds.get(getAdapterPosition())).delete();
                                songIds.remove(getAdapterPosition());

                    }

                }
            });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    void instantiate() {
        db = FirebaseFirestore.getInstance();
        lyrics = findViewById(R.id.lyricsView);
        lyrics.setMovementMethod(new ScrollingMovementMethod());
        deleteButton = findViewById(R.id.deleteButtonFloat);
        doneButton = findViewById(R.id.doneButtonFloat);
        songIds = new ArrayList<>();
        myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        songsRV = findViewById(R.id.savedSongRecycler);


        deleteLayout = findViewById(R.id.deleteLayout);

        animShake  = AnimationUtils.loadAnimation(this, R.anim.shake);
        showButton = AnimationUtils.loadAnimation(this, R.anim.show);
        hideButton = AnimationUtils.loadAnimation(this, R.anim.hide);
        showLayout = AnimationUtils.loadAnimation(this, R.anim.showlayout);
        hideLayout = AnimationUtils.loadAnimation(this, R.anim.hidelayout);

        layout1 = findViewById(R.id.recyclerHolder);
        layout2 = findViewById(R.id.lyricsHolder);
        gd = new GradientDrawable();
        gd.setColor(Color.RED);
        gd.setCornerRadius(50);
        gd.setStroke(5, Color.rgb(187, 134, 252));
        gd.setColor(303030);
        layout1.setBackground(gd);
        layout2.setBackground(gd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.returnMenu:
                finish();
                break;

        }

        return true;
    }

    void initRecyclerViewWithSubCollection(String collection, String subCollection){
        //Query
        Query query = db.collection(collection).document(docId).collection(subCollection);

        //RecyclerOptions
        FirestoreRecyclerOptions<Song> options = new FirestoreRecyclerOptions.Builder<Song>()
                .setQuery(query, Song.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Song, SongViewHolder>(options) {
            @NonNull
            @Override
            public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new SongViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i, @NonNull Song song) {
                songViewHolder.song.setText(song.getArtist() + " - " + song.getName());
                songId = getSnapshots().getSnapshot(i).getId();
                songIds.add(songId);
            }
        };
    }
}
