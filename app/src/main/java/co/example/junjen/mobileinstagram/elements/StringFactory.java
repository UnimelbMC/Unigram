package co.example.junjen.mobileinstagram.elements;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by junjen on 1/10/2015.
 *
 * Handles links in strings
 */

public class StringFactory {

    // builds a string that contains text links to make those links clickable
    public static void stringBuilder(TextView tv, ArrayList<CharSequence> args) {

        // Append all string portions
        for (CharSequence arg : args){
            tv.append(arg);
        }

        // This line makes the link clickable!
        makeLinksFocusable(tv);

        args.clear();   // clear
    }

    public static SpannableString createLink (CharSequence link_string, View.OnClickListener onClickListener) {
        SpannableString link = makeLinkSpan(link_string, onClickListener);
        return link;
    }

    /*
     * Methods used above.
     */

    private static SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return link;
    }

    private static void makeLinksFocusable(TextView tv) {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    /*
     * ClickableString class
     */

    private static class ClickableString extends ClickableSpan {
        private View.OnClickListener mListener;

        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }

        @Override
        public void updateDrawState(TextPaint ds) {// override updateDrawState
            ds.setUnderlineText(false); // set to false to remove underline
        }
    }
}
