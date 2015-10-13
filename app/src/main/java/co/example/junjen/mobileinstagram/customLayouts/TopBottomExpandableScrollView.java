package co.example.junjen.mobileinstagram.customLayouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

/**
 * Created by junjen on 11/10/2015.
 */
public class TopBottomExpandableScrollView extends ExpandableScrollView {

    private int topLevel;
    private TopScrollViewListener topScrollViewListener = null;
    private ScrollViewListener scrollViewListener = null;
    private int lastScrollY;
    private int counter = 0;    // counter to prevent 2 consecutive cases of onScrollChanged

    public TopBottomExpandableScrollView(Context context) {
        super(context);
    }

    public TopBottomExpandableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopBottomExpandableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTopScrollViewListener(TopScrollViewListener topScrollViewListener) {
        this.topScrollViewListener = topScrollViewListener;
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public void setTopLevel(int top){
        this.topLevel = top;
    }

    // checks if top or bottom of a Scroll View has been reached
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        View view = getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));

        lastScrollY = y;

        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            if (scrollViewListener != null && counter == 0) {
                counter++;
                scrollViewListener.onScrollEnded(this, x, y, oldx, oldy);
            }
        } else {
            counter = 0;
        }

        // if refresh panel at top of scroll reached
        View view2 = getChildAt(getChildCount() - 1);
        if (view2.getTop() < topLevel) {
            topScrollViewListener.onScrollTop(this, x, y, oldx, oldy);
        }
        super.onScrollChanged(x, y, oldx, oldy);
    }

    public int getLastScrollY(){
        return this.lastScrollY;
    }
}
