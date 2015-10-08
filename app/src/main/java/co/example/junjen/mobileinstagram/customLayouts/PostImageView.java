package co.example.junjen.mobileinstagram.customLayouts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

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
            getResources();
            Drawable[] layers = new Drawable[2];
            layers[0] = getDrawable();
            layers[1] = Parameters.MainActivityContext.getResources().getDrawable(R.drawable.like_button_feedback);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            setImageDrawable(layerDrawable);


//            RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
//            anim.setInterpolator(new LinearInterpolator());
//            anim.setRepeatCount(Animation.INFINITE);
//            anim.setDuration(700);
//
//            // Start animating the image
//            final ImageView splash = (ImageView) findViewById(R.id.splash);
//            splash.startAnimation(anim);
//
//            // Later.. stop the animation
//            splash.setAnimation(null);


            return true;
        }
    }
}