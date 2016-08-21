package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by mohammad on 04/08/16.
 */

public class MoviesProvider extends ContentProvider {


    public static final int FAVORITE_CODE = 100 ;
    public static final int FAVORITE_WITH_ID_CODE = 101 ;

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(MovieContract.AUTHORITY , MovieContract.MovieEntry.FAVORITE_TABLE , FAVORITE_CODE );
        URI_MATCHER.addURI(MovieContract.AUTHORITY , MovieContract.MovieEntry.FAVORITE_TABLE + "/#" , FAVORITE_WITH_ID_CODE);
    }


    private MoviesDBHelper moviesDBHelper ;

    @Override
    public boolean onCreate() {
        moviesDBHelper = new MoviesDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (URI_MATCHER.match(uri)){
            case FAVORITE_CODE :
                return moviesDBHelper.getReadableDatabase().query(MovieContract.MovieEntry.FAVORITE_TABLE ,
                        projection ,
                        selection ,
                        selectionArgs ,
                        null , null ,
                        sortOrder );

            case FAVORITE_WITH_ID_CODE :
                return moviesDBHelper.getReadableDatabase().query(MovieContract.MovieEntry.FAVORITE_TABLE ,
                        projection ,
                        MovieContract.MovieEntry.MOVIE_ID + "=?" ,
                        new String []{String.valueOf(MovieContract.MovieEntry.getIdFromUri(uri))} ,
                        null , null , null );

            default:
                    throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int code = URI_MATCHER.match(uri);

        switch (code){
            case FAVORITE_CODE :
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE ;
            case FAVORITE_WITH_ID_CODE :
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = moviesDBHelper.getWritableDatabase();

        Uri returnedUri = null ;

        switch (URI_MATCHER.match(uri)){
            case FAVORITE_CODE :
                int id = (int) db.insert(MovieContract.MovieEntry.FAVORITE_TABLE , null , contentValues) ;
                if (id  > -1 ){
                    returnedUri =  MovieContract.MovieEntry.buildMovieByIdUri(id);
                }else{
                    throw new SQLiteException("Failed to insert into table : " + MovieContract.MovieEntry.FAVORITE_TABLE);
                }

                break;

                default:
                    throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return returnedUri ;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {


        SQLiteDatabase db = moviesDBHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)){
            case FAVORITE_CODE :

                return db.delete(MovieContract.MovieEntry.FAVORITE_TABLE , s , strings);

                default:
                    throw new UnsupportedOperationException("Unknown Uri :" + uri );
        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) { // In our case , we do not need update method .. because we will not change any movie data
        return 0;
    }
}
