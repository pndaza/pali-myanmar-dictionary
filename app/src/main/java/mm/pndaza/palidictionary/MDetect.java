package mm.pndaza.palidictionary;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

public class MDetect {

    private static Boolean cacheUnicode = null;

    public static void init(Context context){

        if (cacheUnicode != null) {
            Log.i("MDetect", "MDetect was already initialized.");
            }

            TextView textView = new TextView(context,null);
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

            textView.setText("\u1000");
            textView.measure(0,0);
            int length1 = textView.getMeasuredWidth();

            textView.setText("\u1000\u1039\u1000");
            textView.measure(0,0);
            int length2 = textView.getMeasuredWidth();

            cacheUnicode = (length1 == length2) ;

    }

    public static boolean isUnicode(){

        if (null == cacheUnicode)
            throw new UnsupportedOperationException("MDetect was not initialized.");

        return cacheUnicode;
    }



}
