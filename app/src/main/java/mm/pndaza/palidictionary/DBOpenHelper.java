package mm.pndaza.palidictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private  static DBOpenHelper sInstance;
    private static final String DATABASE_NAME = "dict.db";
    private static final int DATABASE_VERSION = 1;


    public static synchronized DBOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (sInstance == null) {
            sInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBOpenHelper(Context context) {
        super(context, context.getExternalFilesDir(null)+ "/databases/" + DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
