package co.example.junjen.mobileinstagram.customLayouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import co.example.junjen.mobileinstagram.elements.Parameters;

/**
 * Created by junjen on 11/10/2015.
 */
public class TopBottomExpandableScrollView extends ExpandableScrollView {

    private TopScrollViewListener topScrollViewListener = null;
    private ScrollViewListener scrollViewListener = null;
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

    public void setScrollViewListener(TopScrollViewListener topScrollViewListener) {
        this.topScrollViewListener = topScrollViewListener;
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

        View view2 = getChildAt(getChildCount() - 1);
        if (view2.getTop() < Parameters.userFeedFragmentTop) {
            topScrollViewListener.onScrollTop(this, x, y, oldx, oldy);
        }
        super.onScrollChanged(x, y, oldx, oldy);
    }

}
