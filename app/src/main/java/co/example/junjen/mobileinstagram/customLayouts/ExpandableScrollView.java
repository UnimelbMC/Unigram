package co.example.junjen.mobileinstagram.customLayouts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import co.example.junjen.mobileinstagram.elements.Parameters;

/**
 * Created by junjen on 1/10/2015.
 *
 * Custom scroll view to be able to detect when scrolled to the bottom
 */

public class ExpandableScrollView extends ScrollView{

    private ScrollViewListener scrollViewListener = null;
    private int counter = 0;    // counter to prevent 2 consecutive cases of onScrollChanged

    public ExpandableScrollView(Context context) {
        super(context);
    }

    public ExpandableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    // checks if bottom of a Scroll View has been reached
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        View view = getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));
        if (diff == 0) { // if diff is zero, then the bottom has been reached
            if (scrollViewListener != null && counter == 0) {
                counter++;
                scrollViewListener.onScrollEnded(this, x, y, oldx, oldy);
            }
        } else {
            counter = 0;
        }
        super.onScrollChanged(x, y, oldx, oldy);
    }
}
