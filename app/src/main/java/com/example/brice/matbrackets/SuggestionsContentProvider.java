package com.example.brice.matbrackets;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.util.ArrayList;

public class SuggestionsContentProvider extends ContentProvider {
    private static final String[] COLUMNS = {
            "_id", // must include this column
            SearchManager.SUGGEST_COLUMN_TEXT_1};
    public MatrixCursor cursor = new MatrixCursor(COLUMNS);

    public SuggestionsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        //throw new UnsupportedOperationException("Not yet implemented");

        String query = uri.getLastPathSegment().toLowerCase();
        System.out.println("Content provider query: "+query);
        cursor.addRow(new Object[] {1,"Your search: "+query});

//        ArrayList<String> temp = new ArrayList<String>();
//        temp.add("Item One");
//        temp.add("Item Two");
//        temp.add("Item Three");
//        temp.add("Random fourth item");
//
//        for(int i = 0; i < temp.size(); i++){
//            cursor.addRow(new Object[] {i,temp.get(i).toString()});
//        }

        MatrixCursor returnMatrix = cursor;
        cursor = new MatrixCursor(COLUMNS);
        return returnMatrix;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        return 0;
    }
}
