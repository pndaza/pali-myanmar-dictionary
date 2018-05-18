package mm.pndaza.palidictionary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DefineCursorAdapter extends CursorAdapter {
    private static Boolean isUnicode = null;

    public DefineCursorAdapter(Context context, Cursor c){
        super(context, c);
        MDetect.init(context);
        isUnicode = MDetect.isUnicode();
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }


    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String column;
        if(isUnicode) {
            column = "uword";
        }
        else
            column = "zword";

        String word = cursor.getString(cursor.getColumnIndexOrThrow(column));
        String book_id = cursor.getString(cursor.getColumnIndexOrThrow("book"));

        String book_name = Book.getBookName(book_id,isUnicode);

        TextView textView_word = view.findViewById(R.id.tv_word);
        TextView textView_book = view.findViewById(R.id.tv_book);

        textView_word.setText(word);
        textView_book.setText(book_name);
        textView_word.setTextColor(Color.BLACK);
        textView_book.setTextColor(Color.GRAY);

        RelativeLayout rl = view.findViewById(R.id.background);

        // set background color
        int position = cursor.getPosition();
        if (position % 2 == 0) {
            rl.setBackgroundColor(Color.argb( 90,225, 245, 254));
        } else {
            rl.setBackgroundColor(Color.argb(90, 179, 229, 252));
        }
    }
}
