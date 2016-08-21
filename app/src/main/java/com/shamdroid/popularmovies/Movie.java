package com.shamdroid.popularmovies;

/**
 * Created by mohammad on 29/07/16.
 */


import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import data.MovieContract;

/**
 * Class for representing a movie
 *
 */


public class Movie implements Parcelable {

    private int id ;
    private String title ,posterURL , overview , releaseDate ;
    float voteAverage ;

    public Movie(int id, String title, String posterURL, String overview, String releaseDate, float voteAverage) {
        this.id = id;
        this.title = title;
        this.posterURL = posterURL;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    public Movie (Cursor cursor){
        id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID));
        title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE));
        posterURL = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.POSTER_URL));
        overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW));
        releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE));
        voteAverage = cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.VOTE_RATE));
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public float getVoteAverage() {
        return voteAverage;
    }


    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterURL = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(posterURL);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeFloat(voteAverage);
    }


}
