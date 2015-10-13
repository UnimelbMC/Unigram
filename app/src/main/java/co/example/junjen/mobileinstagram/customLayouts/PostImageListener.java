package co.example.junjen.mobileinstagram.customLayouts;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;

/**
 * Created by junjen on 9/10/2015.
 *
 * Listener for post images to detect double tap likes.
 */

public class PostImageListener extends GestureDetector.SimpleOnGestureListener {

    private ImageView postImageView;
    private Post post;
    GestureDetector mGestureDetector;

    public PostImageListener(ImageView postImageView, Post post){
        this.postImageView = postImageView;
        this.post = post;
        setListener();
    }

    public void setListener(){
        postImageView.setClickable(true);
        mGestureDetector = new GestureDetector( postImageView.getContext(), this, null, true );
        postImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);

                postImageView.invalidate();
                return true; // indicate event was handled
            }
        });
    }

    @Override
    public boolean onDoubleTap( MotionEvent e ) {
        String postId = (String) postImageView.getContentDescription();

        Log.w("test", "double tap: " + postId);

        ImageView likeFeedback = (ImageView)
                ((RelativeLayout)postImageView.getParent()).getChildAt(1);

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

        // update post info
        post.likePost(true);

        return true;
    }

    // handle swipe motion for swipe to send
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(e1.getY() - e2.getY()) > Parameters.SWIPE_MAX_OFF_PATH)
            return false;
        if (Math.abs(e1.getX() - e2.getX()) > Parameters.SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > Parameters.SWIPE_THRESHOLD_VELOCITY) {

            Log.w("test", "swiped");

            // show toast feedback on swipe
            CharSequence text = Parameters.swipe_toast_message;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(Parameters.NavigationBarContext, text, duration);
            toast.show();

            // TODO: bluetooth popup

            if(Parameters.bluetoothSwipeFragment != null) {
                Toast.makeText(Parameters.NavigationBarActivity,"u r swipe", Toast.LENGTH_LONG).show();
                Parameters.bluetoothSwipeFragment.sendMessage("Hello world from Bluetooth!");
            }

        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }
}