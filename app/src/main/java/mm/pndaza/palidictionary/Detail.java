package mm.pndaza.palidictionary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class Detail extends AppCompatActivity {

    private Boolean isUnicode = null;
    private Boolean isAdded = null;
    private  int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);


        MDetect.init(this);
        isUnicode = MDetect.isUnicode();

        // change title to unicode
        if(isUnicode)
            setTitle(Rabbit.zg2uni(this.getTitle().toString()));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Intent intent = getIntent();

        id = intent.getIntExtra("id", 0);
        String word = intent.getStringExtra("word");
        String content = intent.getStringExtra("content");
        String book = intent.getStringExtra("book");
        String book_name = Book.getBookName(book, isUnicode);

        int index;

        index = content.indexOf("\n");
        if( index != -1 && !book.equals("4")){
            word = content.substring(0,index);
            content = content.substring(index);
        }

        index = content.indexOf("[");
        int index_end = content.indexOf("]");
        String viggaha = "";
        if(index != -1 && index_end != -1 && book.equals("1")){
            viggaha = content.substring(index-1,index_end);
            content = content.substring(index_end);
        }

        //
        index = content.indexOf("တိပိ၊");
        if (index != -1) {
            book_name = content.substring(index);
            content = content.substring(0, index-1);
        }


        // styling word
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString str1= new SpannableString(word + "\n");
        str1.setSpan(new ForegroundColorSpan(Color.argb(255, 85,0,0)), 0, str1.length(), 0);
        str1.setSpan(new RelativeSizeSpan(1.4f), 0,str1.length(), 0);
        str1.setSpan(new StyleSpan(Typeface.BOLD), 0, str1.length(), 0 );
        builder.append(str1);

        // Styling viggaha
        if (viggaha.length() > 0) {
            SpannableString vi = new SpannableString( viggaha );
            vi.setSpan(new ForegroundColorSpan(Color.GRAY), 0, vi.length(), 0);
            vi.setSpan(new RelativeSizeSpan(1.2f), 0, vi.length(), 0);
            builder.append(vi);
        }

        // styling content
        SpannableString str2= new SpannableString(content + "\n\n");
        str2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str2.length(), 0);
        str2.setSpan(new RelativeSizeSpan(1.3f), 0,str2.length(), 0);
        builder.append(str2);

        // Styling book
        SpannableString str3= new SpannableString(book_name + "\n\n\n\n");
        str3.setSpan(new ForegroundColorSpan(Color.GRAY), 0, str3.length(), 0);
        str3.setSpan(new RelativeSizeSpan(1.1f), 0,str3.length(), 0);
        str3.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),0,str3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(str3);


        TextView textView = findViewById(R.id.tv_detail);
        textView.setTextIsSelectable(true);
        textView.setText( builder, TextView.BufferType.SPANNABLE);


        FloatingActionButton fab = findViewById(R.id.fab);

        // check current work is exist in fav table
        // And set drawable icon for floating button on condition

        DBOpenHelper dbOpenHelper = DBOpenHelper.getInstance(this);
        final SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT sid FROM fav WHERE sid = %d",id),null);

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            isAdded = false;
            fab.setImageResource(R.drawable.favourite);

        } else  {
            isAdded = true ;
            fab.setImageResource(R.drawable.favourited);

        }
        cursor.close();

        // handle onClick Event for Floating Button

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isAdded == false){
                    sqLiteDatabase.execSQL("INSERT INTO fav (sid) VALUES (?)",new Object[] {id});


                    ((FloatingActionButton)view).setImageResource(R.drawable.favourited);

                    String info = "မွတ္သားလိုက္ပါၿပီ";
                    if (isUnicode == true)
                        info = Rabbit.zg2uni(info);

                    Snackbar.make(view, info, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    isAdded = true;

                }
                else {
                    sqLiteDatabase.execSQL("DELETE FROM fav WHERE sid = ?", new Object[] {id});


                    ((FloatingActionButton)view).setImageResource(R.drawable.favourite);

                    String info = "မွတ္သားထားသည္မွ ဖ်က္လိုက္ပါၿပီ";
                    if (isUnicode == true)
                        info = Rabbit.zg2uni(info);

                    Snackbar.make(view, info, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    isAdded = false;
                }

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}