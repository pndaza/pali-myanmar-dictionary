package mm.pndaza.palidictionary;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;

public class Favourite extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<FavData> item_list = new ArrayList<>();
    private FavDataAdapter mAdapter;

    private DBOpenHelper dbOpenHelper = null;
    private SQLiteDatabase sqLiteDatabase = null;

    Boolean menu_show = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite);

        // change title to unicode
        MDetect.init(this);
        if(MDetect.isUnicode())
            setTitle(Rabbit.zg2uni(this.getTitle().toString()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initControls();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initControls();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void initControls() {

        recyclerView = findViewById(R.id.recycler_view);

        item_list.clear();

        dbOpenHelper = DBOpenHelper.getInstance(this);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select define._id, zword, book from define inner join fav on fav.sid = define._id", null);

        if (cursor.getCount() != 0 ){
            FavData.isCheckboxShow = false;

            while (cursor.moveToNext()){
                item_list.add(new FavData(cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("zword")),
                        cursor.getString(cursor.getColumnIndexOrThrow("book"))));
            }
        }
        cursor.close();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FavDataAdapter(getApplicationContext(),item_list);
        recyclerView.setAdapter(mAdapter);


    }

    public void removeFromDB(final int _id){

        dbOpenHelper = DBOpenHelper.getInstance(this);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM fav WHERE sid = ?", new Object[] {_id});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_fav, menu);

        if (menu != null) {
            MenuItem sel = menu.findItem(R.id.select_all);
            MenuItem del = menu.findItem(R.id.del);
            MenuItem edit = menu.findItem(R.id.edit);
            if(menu_show == true) {
                edit.setIcon(R.drawable.cancel);
                sel.setVisible(true);
                del.setVisible(true);
            } else {
                edit.setIcon(R.drawable.edit);
                sel.setVisible(false);
                del.setVisible(false);
            } }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit:

                if (menu_show == false) {
                    menu_show = true;
                    FavData.isCheckboxShow = true;
                } else {
                    item.setIcon(R.drawable.edit);
                    menu_show = false;
                    FavData.isCheckboxShow = false;
                }


                mAdapter.notifyDataSetChanged();;
                invalidateOptionsMenu();

                return true;

            case  R.id.select_all:

                for (FavData favData: item_list) {
                    favData.setSelected(true);
                }
                mAdapter.notifyDataSetChanged();

                return true;

            case R.id.del:

                ArrayList<FavData> temp = new ArrayList<>(item_list);

                for ( FavData data : temp ) {
                    if ( data.isSelected() == true) {
                        item_list.remove(data);

                        removeFromDB(data.getId());
                    }
                }

                mAdapter.notifyDataSetChanged();

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }
}

