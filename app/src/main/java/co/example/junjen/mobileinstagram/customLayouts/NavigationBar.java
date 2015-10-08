package co.example.junjen.mobileinstagram.customLayouts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import co.example.junjen.mobileinstagram.elements.Parameters;

/**
 * Created by junjen on 8/10/2015.
 */
public class NavigationBar extends RelativeLayout {

    public NavigationBar(Context context) {
        super(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int[] location = new int[2];
        this.getLocationOnScreen(location);
        Parameters.NavigationViewHeight = location[1];

    }
}
