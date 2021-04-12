package com.gabriel.lyrixapplication.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gabriel.lyrixapplication.EditNameDialog;
import com.gabriel.lyrixapplication.R;
import com.gabriel.lyrixapplication.model.SavedList;
import com.gabriel.lyrixapplication.model.Song;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedListController extends AppCompatActivity implements EditNameDialog.EditNameDialogListener {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    private RecyclerView savedRV;

    private String docId;
    private List<String> docIds;


    private FloatingActionButton editButtonFloat;
    private FloatingActionButton deleteButton;
    private FloatingActionButton doneButton;

    private LinearLayout editLayout;
    private LinearLayout deleteLayout;

    private boolean deleteMode = false;
    private Boolean addNew = false;
    private boolean editMode = false;
    private boolean optionsOpen=false;


    private int position;
    private MenuItem addButton;
    private Toolbar myToolbar;

    private Animation hideButton;
    private Animation showButton;
    private Animation animShake;
    private Animation showLayout;
    private Animation hideLayout;






    private RelativeLayout layout;
    private GradientDrawable gd;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_controller);
        instantiate();
        initRecyclerView("SavedList");



        if (getIntent().getStringExtra("song") != null) {
            deleteLayout.setVisibility(View.INVISIBLE);
            doneButton.setVisibility(View.INVISIBLE);
            editButtonFloat.setVisibility(View.INVISIBLE);
        }




        savedRV.setHasFixedSize(true);
        savedRV.setLayoutManager(new LinearLayoutManager(this));
        savedRV.setAdapter(adapter);


        editButtonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode = true;
                Toast.makeText(SavedListController.this, "Edit Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMode = true;
                Toast.makeText(SavedListController.this, "Delete Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(deleteLayout.getVisibility() == View.VISIBLE && editLayout.getVisibility() == View.VISIBLE){
                   deleteLayout.setVisibility(View.INVISIBLE);
                   editLayout.setVisibility(View.INVISIBLE);
                   deleteLayout.startAnimation(hideLayout);
                   editLayout.startAnimation(hideLayout);
                   doneButton.setAnimation(hideButton);
                   optionsOpen=false;
                   if(deleteMode)
                       Toast.makeText(SavedListController.this, "Delete Mode Disabled", Toast.LENGTH_SHORT).show();
                   deleteMode = false;
                   if(editMode)
                       Toast.makeText(SavedListController.this, "Edit Mode Disabled", Toast.LENGTH_SHORT).show();
                   editMode = false;
                   savedRV.clearAnimation();


               }else{
                   optionsOpen = true;
                   savedRV.startAnimation(animShake);
                   deleteLayout.setVisibility(View.VISIBLE);
                   editLayout.setVisibility(View.VISIBLE);
                   deleteLayout.startAnimation(showLayout);
                   editLayout.startAnimation(showLayout);
                   doneButton.setAnimation(showButton);
               }

            }
        });


    }


    //ViewHolder
    private class SavedListViewHolder extends RecyclerView.ViewHolder {

        private TextView list_name;
        private CardView parent;


        public SavedListViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.name_textView);
            parent = itemView.findViewById(R.id.parent_layout);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                position = getLayoutPosition();
                if(optionsOpen && !deleteMode  && !editMode)
                    Toast.makeText(SavedListController.this, "Please close options menu", Toast.LENGTH_SHORT).show();
                   else if (getIntent().getStringExtra("song") != null && !editMode) {

                        Gson gson = new Gson();
                        String songAsString = getIntent().getStringExtra("song");
                        Song song = gson.fromJson(songAsString, Song.class);

                        Map<String, Object> songObject = new HashMap<>();
                        songObject.put("name", song.getName());
                        songObject.put("artist", song.getArtist());
                        songObject.put("lyrics", song.getLyrics());


                        db.collection("SavedList").document(docIds.get(getAdapterPosition()))
                                .collection("songs")
                                .document()
                                .set(songObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SavedListController.this, "Song Saved Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SavedListController.this, "Song Failed to Save", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (!deleteMode && !editMode) {
                        Intent intent = new Intent(SavedListController.this, SavedSongsController.class);
                        intent.putExtra("listId", docIds.get(getAdapterPosition()));
                        startActivity(intent);

                    } else if (deleteMode) {
                        editMode = false;
                        db.collection("SavedList")
                                .document(docIds.get(getAdapterPosition())).delete();
                        docIds.remove(getAdapterPosition());
                    }
                    else if (editMode) {
                        deleteMode=false;
                        openEditDialog();
                    }
                    else if(deleteMode && editMode){
                        Toast.makeText(SavedListController.this, "Cannot perform action", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void applyText(String newName) {
        Map<String, Object> nameUpdate = new HashMap<>();
        nameUpdate.put("name", newName);
        if (editMode && !addNew) {
        updateName(nameUpdate);
        }else if (addNew && !editMode){
            addList(newName);
        }
        else{
            Toast.makeText(this, "Action could not be completed", Toast.LENGTH_SHORT).show();
        }
    }

    private void addList(String nameUpdate) {
        CollectionReference savedListRef = FirebaseFirestore.getInstance()
                .collection("SavedList");
        savedListRef.add(new SavedList(nameUpdate));
        finish();
        Toast.makeText(this, "List Added Successfully", Toast.LENGTH_SHORT).show();

    }

    private void updateName(Map<String, Object> nameUpdate) {
        db.collection("SavedList")
                .document(docIds.get(position)).update(nameUpdate);
    }




    public void openEditDialog() {
        EditNameDialog nameDialog = new EditNameDialog();
        nameDialog.show(getSupportFragmentManager(), "edit dialog");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.saved_lists_options, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.returnMenu:
                finish();
                break;

            case R.id.createNewListMenu:
                addNew = true;
                openEditDialog();

                break;

        }
        return true;
    }

    void instantiate(){
        db = FirebaseFirestore.getInstance();
        savedRV = findViewById(R.id.savedRecyclerView);
        docIds = new ArrayList<>();
        deleteButton = findViewById(R.id.deleteButtonFloat);
        editButtonFloat = findViewById(R.id.editFloat);
        doneButton = findViewById(R.id.doneButtonFloat);
        addButton = findViewById(R.id.createNewListMenu);
        myToolbar = findViewById(R.id.myToolbar);
        deleteLayout = findViewById(R.id.deleteLayout);
        editLayout = findViewById(R.id.editLayout);
        setSupportActionBar(myToolbar);


        animShake  = AnimationUtils.loadAnimation(this, R.anim.shake);
        showButton = AnimationUtils.loadAnimation(this, R.anim.show);
        hideButton = AnimationUtils.loadAnimation(this, R.anim.hide);
        showLayout = AnimationUtils.loadAnimation(this, R.anim.showlayout);
        hideLayout = AnimationUtils.loadAnimation(this, R.anim.hidelayout);

        layout = findViewById(R.id.recyclerHolder);
        gd = new GradientDrawable();
        gd.setColor(Color.RED);
        gd.setCornerRadius(50);
        gd.setStroke(5, Color.rgb(187, 134, 252));
        gd.setColor(303030);
        layout.setBackground(gd);
    }

    void initRecyclerView(String collection){
        //Query
        Query query = db.collection(collection);

        //RecyclerOptions
        FirestoreRecyclerOptions<SavedList> options = new FirestoreRecyclerOptions.Builder<SavedList>()
                .setQuery(query, SavedList.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<SavedList, SavedListViewHolder>(options) {
            @NonNull
            @Override
            public SavedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new SavedListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SavedListViewHolder savedListViewHolder, int i, @NonNull SavedList savedList) {
                savedListViewHolder.list_name.setText(savedList.getName());
                docId = getSnapshots().getSnapshot(i).getId();
                docIds.add(docId);
            }
        };
    }
}