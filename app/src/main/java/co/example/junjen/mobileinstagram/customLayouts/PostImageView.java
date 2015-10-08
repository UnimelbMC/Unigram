package co.example.junjen.mobileinstagram.customLayouts;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.animation.AnimatorSet;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.elements.Parameters;

/**
 * Created by junjen on 8/10/2015.
 *
 * Handles double tap to like for post images.
 */

public class PostImageView extends ImageView {

    private Context context;
    private GestureListener mGestureListener;
    private GestureDetector mGestureDetector;

    public PostImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public PostImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public PostImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructing(context);
    }


    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        mGestureListener = new GestureListener();
        mGestureDetector = new GestureDetector( context, mGestureListener, null, true );
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);

                invalidate();
                return true; // indicate event was handled
            }

        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap( MotionEvent e ) {
            String postId = (String) getContentDescription();

            Log.w("test","double tap: "+ postId);

            ImageView likeFeedback = (ImageView) ((RelativeLayout)getParent()).getChildAt(1);

            /** On post image double tap, animate like icon as feedback **/

            float scale = Parameters.animationStartEndScale;
            int offsetDuration = 0;

            // make like button appear
            ScaleAnimation animationAppear = new ScaleAnimation(scale, 1, scale, 1,
                    Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);
            animationAppear.setRepeatCount(0);
            animationAppear.setDuration(Parameters.likeAppearDuration);
            offsetDuration += animationAppear.getDuration();

            // make like button stay
            ScaleAnimation animationStay = new ScaleAnimation(1, 1, 1, 1,
                    Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);
            animationStay.setRepeatCount(0);
            animationStay.setDuration(Parameters.likeStayDuration);
            animationStay.setStartOffset(offsetDuration);
            offsetDuration += animationStay.getDuration();

            // make like button disappear
            ScaleAnimation animationDisappear = new ScaleAnimation(1, scale, 1, scale,
                    Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);
            animationDisappear.setRepeatCount(0);
            animationDisappear.setDuration(Parameters.likeDisappearDuration);
            animationDisappear.setStartOffset(offsetDuration);

            // start animation set
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animationAppear);
            animationSet.addAnimation(animationStay);
            animationSet.addAnimation(animationDisappear);
            likeFeedback.startAnimation(animationSet);

            return true;
        }
    }
}