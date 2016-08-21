package data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mohammad on 04/08/16.
 */

public class MovieContract {



    public static final String AUTHORITY = "com.shamdroid.popularmovies";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);


    public static class MovieEntry implements BaseColumns {

        public static final String FAVORITE_TABLE = "favorite";

        public static final String _ID = "_id";
        public static final String MOVIE_ID = "movie_id";
        public static final String TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";
        public static final String VOTE_RATE = "vote_rate";
        public static final String POSTER_URL = "poster_url";



        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(FAVORITE_TABLE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + FAVORITE_TABLE ;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + FAVORITE_TABLE ;


        public static Uri buildMovieByIdUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI , id);
        }
        public static int getIdFromUri (Uri uri){
            return (int) ContentUris.parseId(uri);
        }
    }

}
