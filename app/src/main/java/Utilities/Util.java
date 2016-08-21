package Utilities;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import data.MovieContract;

/**
 * Created by mohammad on 04/08/16.
 */

public class Util {

    public static boolean isConnected (Context context) { // Check if the device is connected to the Internet
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    public static boolean isFavorite (Context context , int id ){ // Check if movie with passed id is favorite
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI , null , MovieContract.MovieEntry.MOVIE_ID + "=?" , new String []{String.valueOf(id)} , null );

        int found = cursor.getCount();

        cursor.close();

        return found != 0;
    }






    public static void setListViewHeightBasedOnChildren(ListView listView , Context context) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int listViewWidth = listView.getWidth() ;
        int widthSpec = View.MeasureSpec.makeMeasureSpec(listViewWidth, View.MeasureSpec.AT_MOST);


        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(widthSpec, 0);

            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }




    public static void saveImageToImagesDir (Context context , Bitmap bitmap ,String fileName ){

        File imagesDir = context.getCacheDir() ;

        if (!imagesDir.exists())
            imagesDir.mkdirs();

        File saveTo = new File(imagesDir , fileName);

        FileOutputStream fos = null ;

        try {
             fos = new FileOutputStream(saveTo);

             bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }


    public static Bitmap loadImageFromImagesDir (Context context , String fileName ){
        File imagesDir = context.getCacheDir() ;
        File imageFile = new File(imagesDir , fileName);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile));

            return bitmap ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null ;
    }



    public static Drawable getDrawable(Context context , int resId){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resId , context.getTheme());
        }else {
            return context.getResources().getDrawable(resId);
        }

    }

}
