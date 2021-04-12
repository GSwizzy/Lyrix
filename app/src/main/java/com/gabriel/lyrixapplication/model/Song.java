package com.gabriel.lyrixapplication.model;

public class Song implements Comparable{
    private String name;
    private String artist;
    private String lyrics;

    public Song(){}



    public Song(String name, String artist, String lyrics) {
        this.name = name;
        this.artist = artist;
        this.lyrics = lyrics;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public int compareTo(Object o) {
        Song song = (Song) o;

        if (song.getName().equals(this.name)
                && song.getArtist().equals(this.artist)
                && song.getLyrics().equals(this.lyrics))
            return 0; //they are the same
        return 1; //they are not the same
    }


}
