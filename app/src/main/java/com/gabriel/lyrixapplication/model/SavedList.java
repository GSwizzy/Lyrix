package com.gabriel.lyrixapplication.model;

import com.gabriel.lyrixapplication.model.Song;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SavedList {
    private String name;
    private List<Song> songs;

    public SavedList(String name, List<Song> songs) {
        this.name = name;
        this.songs = songs;
    }

    public SavedList(String name) {
        this.name=name;
    }

    public SavedList() {
    }

    public String getName() {
        return name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
