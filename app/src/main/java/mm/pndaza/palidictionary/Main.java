package mm.pndaza.palidictionary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private static Boolean isUnicode = null;
    private DBOpenHelper db = null;
    private SQLiteDatabase sqLiteDatabase = null;
    private Cursor cursor = null;
    private DefineCursorAdapter adapter = null;
    private ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = this;
        // check user phone use zawgyi or unicode
        if(isUnicode == null) {
            MDetect.init(this);
            isUnicode = MDetect.isUnicode();
        }

        initTextView();

        // initailize Database
        db =DBOpenHelper.getInstance(this);
        sqLiteDatabase = db.getReadableDatabase();

        // Create empty adapter
        adapter = new DefineCursorAdapter(this, null);
        listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);

        LinearLayout linearLayout = findViewById(R.id.empty);
        listView.setEmptyView(linearLayout);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                db = DBOpenHelper.getInstance(context);
                sqLiteDatabase = db.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("select * from define where _id = " + ((Cursor)adapterView.getItemAtPosition(position)).getString(0), null);
                cursor.moveToFirst();

                Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String word = cursor.getString(cursor.getColumnIndexOrThrow("zword"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String book = cursor.getString(cursor.getColumnIndexOrThrow("book"));
                cursor.close();

                if(isUnicode){
                    word = Rabbit.zg2uni(word);
                    content = Rabbit.zg2uni(content);
                }

                Intent result = new Intent(Main.this.getApplicationContext(),Detail.class);
                result.putExtra("id",id);
                result.putExtra("word", word);
                result.putExtra("content", content);
                result.putExtra("book", book);

                Main.this.startActivity(result);


            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem nav_fav =  menu.findItem(R.id.nav_fav);

        String fav = "မွတ္သားခ်က္မ်ား";
        if (isUnicode)
            fav = Rabbit.zg2uni(fav);
        nav_fav.setTitle(fav);
    }


    void initTextView(){

        String info = getString(R.string.info);
        if(isUnicode) {
            TextView tv = findViewById(R.id.tv_info);
            tv.setText(Rabbit.zg2uni(info));
        }
        String dev = getString(R.string.dev);
        SpannableString spannableString = new SpannableString(dev);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),dev.length()-7,dev.length(),0);
        TextView tv_dev = findViewById(R.id.tv_dev);
        tv_dev.setText(spannableString,TextView.BufferType.SPANNABLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem search_item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        String hint = "စာရွာရန္";

        if (isUnicode == true) {
            hint = Rabbit.zg2uni(hint);
        }
        searchView.setQueryHint(hint);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                String str_query = s.trim();
                String col;

                // In Unicode device, uword column will use
                // In Zawgyi, zword will use
                if (isUnicode)
                    col = "uword";
                else
                    col = "zword";

                if( str_query.length() == 0){
                     cursor = null;
                     adapter.changeCursor(cursor);
                     return false;
                }


                String sql_time = String.format(" EXPLAIN QUERY PLAN SELECT _id, %s FROM define where %s like '%s%%' LIMIT 500", col,col,str_query);
                cursor = sqLiteDatabase.rawQuery(sql_time, null);
                cursor.moveToFirst();
                Log.i("Query plan", cursor.getString(cursor.getColumnIndexOrThrow("detail")));


                String sql = String.format("SELECT _id, %s, book FROM define where %s like '%s%%' LIMIT 500", col,col,str_query);
                cursor  = sqLiteDatabase.rawQuery(sql, null);
                Log.i ("row count", String.valueOf(cursor.getCount()));
                adapter.changeCursor(cursor);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fav) {
            Intent intent = new Intent( Main.this,Favourite.class);
            Main.this.startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
