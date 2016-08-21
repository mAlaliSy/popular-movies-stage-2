package data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mohammad on 04/08/16.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "popular_movies.db";
    public static final int VERSION = 1 ;

    public MoviesDBHelper(Context context) {
        super(context, DB_NAME ,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE_STATEMENT = "CREATE TABLE " +
                MovieContract.MovieEntry.FAVORITE_TABLE + " ( " +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                MovieContract.MovieEntry.MOVIE_ID + " INTEGER UNIQUE NOT NULL , " +
                MovieContract.MovieEntry.TITLE + " TEXT NOT NULL , " +
                MovieContract.MovieEntry.OVERVIEW + " TEXT NOT NULL , " +
                MovieContract.MovieEntry.VOTE_RATE + " REAL NOT NULL , " +
                MovieContract.MovieEntry.POSTER_URL + " TEXT NOT NULL , " +
                MovieContract.MovieEntry.RELEASE_DATE + " TEXT NOT NULL )" ;

        sqLiteDatabase.execSQL(CREATE_TABLE_STATEMENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }



}
