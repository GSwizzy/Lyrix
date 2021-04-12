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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gabriel.lyrixapplication.R;
import com.gabriel.lyrixapplication.model.Song;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HistoryController extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView historyRV;
    private FirestoreRecyclerAdapter adapter;
    private TextView lyricsView;
    private Toolbar myToolbar;
    private RelativeLayout layout1;
    private RelativeLayout layout2;
    private GradientDrawable gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_controller);
        instantiate();
        initRecyclerView("Song");

         historyRV.setHasFixedSize(true);
         historyRV.setLayoutManager(new LinearLayoutManager(this));
         historyRV.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.returnMenu:
                finish();
                break;


        }

        return true;
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
//                    Log.d("Click", "onClick: "+ getAdapterPosition());
                    Song song = (Song) adapter.getItem(getAdapterPosition());
                    lyricsView.setText(song.getLyrics());
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

void instantiate(){
    db = FirebaseFirestore.getInstance();
    lyricsView = findViewById(R.id.lyricsView);
    historyRV = findViewById(R.id.historyRecyclerView);
    lyricsView.setMovementMethod(new ScrollingMovementMethod());
    myToolbar = findViewById(R.id.myToolbar);
    setSupportActionBar(myToolbar);
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

void initRecyclerView(String collection){
    //Query
    Query query = db.collection(collection);

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
        }
    };
}
}