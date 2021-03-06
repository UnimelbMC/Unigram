package co.example.junjen.mobileinstagram;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


import co.example.junjen.mobileinstagram.elements.Parameters;


public class CameraFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    //Camera parameters variables
    private Camera mCamera;
    private CameraPreview mPreview;
    private static final String TAG = "MyCamera";
    public static final int MEDIA_TYPE_IMAGE = 1;

    //variables to move ViewSwitcher
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;
    private int currentState;
    ViewSwitcher vLowerPanel,vUpperPanel;

    //Camera view in layout
    ImageView cameraView;
    FrameLayout preview;

    //For filters
    Bitmap bitmapImage,originalbmp,originalbmpfiltered;

    //Buttoms
    ImageButton flashButton;
    ImageButton backButton;

    //Grid and layer
    boolean grid,layer;

    //Variable startActivityForResult
    private static final int SELECT_PHOTO = 100;
    private static final int PIC_CROP = 1;
    private String intent;

    //Layouts
    LinearLayout seekLayout;
    LinearLayout filterLayout;
    LinearLayout confirmationLayout;
    SeekBar cbSeekbar;
    TextView numberSeekTextView;

    private String optionCamera;
    private int brightness,contrast;

    //accept
    private File photoPath;


    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public CameraFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //OnCreateView with initialize variables
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Parameters.NavigationBarActivity.activityFeedBar(false);

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getActivity(), mCamera);
        preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        //Button and Listener declarations
        Button takePhotoButton = (Button) v.findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(takePhotoButtonListener);

        ImageButton closeButton = (ImageButton) v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(closeButtonListener);

        ImageButton loadImageButton = (ImageButton) v.findViewById(R.id.loadImage);
        loadImageButton.setOnClickListener(loadImageButtonListener);

        flashButton = (ImageButton) v.findViewById(R.id.flashButton);
        flashButton.setOnClickListener(flashButtonListener);

        backButton=(ImageButton) v.findViewById(R.id.btnBack);
        backButton.setOnClickListener(backButtonListener);

        ImageButton noFilterButton=(ImageButton) v.findViewById(R.id.NormalImagebutton);
        ImageButton filter1Button=(ImageButton) v.findViewById(R.id.Filter1);
        ImageButton filter2Button=(ImageButton) v.findViewById(R.id.Filter2);
        ImageButton filter3Button=(ImageButton) v.findViewById(R.id.Filter3);

        noFilterButton.setOnClickListener(noFilterButtonListener);
        filter1Button.setOnClickListener(filter1ButtonListener);
        filter2Button.setOnClickListener(filter2ButtonListener);
        filter3Button.setOnClickListener(filter3ButtonListener);

        ImageButton gridButton=(ImageButton) v.findViewById(R.id.grid);
        gridButton.setOnClickListener(gridButtonListener);

        cameraView=(ImageView) v.findViewById(R.id.cameraView);
        vLowerPanel=(ViewSwitcher) v.findViewById(R.id.vLowerPanel);
        vUpperPanel=(ViewSwitcher) v.findViewById(R.id.vUpperPanel);

        //Grid
        grid=false;
        layer=true;

        ImageButton cropButton=(ImageButton) v.findViewById(R.id.cropButton);
        cropButton.setOnClickListener(cropButtonListener);

        //Brightness and contrast
        ImageButton brightnessButton=(ImageButton) v.findViewById(R.id.brightnessButton);
        brightnessButton.setOnClickListener(brightnessButtonListener);
        ImageButton contrastButton=(ImageButton) v.findViewById(R.id.contrastButton);
        contrastButton.setOnClickListener(contrastButtonListener);
        seekLayout=(LinearLayout) v.findViewById(R.id.seekLayout);
        cbSeekbar= (SeekBar) v.findViewById(R.id.cbseekBar);
        cbSeekbar.setOnSeekBarChangeListener(cbSeekbarListener);
        numberSeekTextView=(TextView) v.findViewById(R.id.numberSeekTextView);

        //Layout declaration
        filterLayout=(LinearLayout) v.findViewById(R.id.filterLayout);
        confirmationLayout=(LinearLayout) v.findViewById(R.id.confirmationLayout);

        optionCamera="";
        intent="";

        //accept and close button
        ImageButton acceptButton=(ImageButton) v.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(acceptButtonListener);
        ImageButton xButton=(ImageButton) v.findViewById(R.id.xButton);
        xButton.setOnClickListener(xButtonListener);
        ImageButton btnAccept= (ImageButton) v.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(btnAcceptListener);

        //Disable button if the user does not have camera
        if(!hasCamera())
            takePhotoButton.setEnabled(false);

        // Inflate the layout for this fragment
        return v;
    }


    // A safe way to get an instance of the Camera object.
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    //Callback for takePicture
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            try {
                File miDirs = new File(
                        Environment.getExternalStorageDirectory() + "/myphotos");
                if (!miDirs.exists())
                    miDirs.mkdirs();

                final Calendar c = Calendar.getInstance();
                String new_Date = c.get(Calendar.DAY_OF_MONTH) + "-"
                        + ((c.get(Calendar.MONTH)) + 1) + "-"
                        + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR)
                        + "-" + c.get(Calendar.MINUTE) + "-"
                        + c.get(Calendar.SECOND);

                String imageFilePath = String.format(
                        Environment.getExternalStorageDirectory() + "/myphotos"
                                + "/%s.jpg", "te1t(" + new_Date + ")");

                Uri selectedImage = Uri.parse(imageFilePath);

                File file = new File(imageFilePath);
                photoPath=file;
                String path = file.getAbsolutePath();
                Bitmap bitmap = null;
                outStream = new FileOutputStream(file);
                outStream.write(data);
                outStream.close();

                if (path != null) {
                    if (path.startsWith("content")) {
                        bitmap = decodeStrem(file, selectedImage,
                                getActivity());
                    } else {
                        bitmap = decodeFile(file, 10);
                    }
                }
                if (bitmap != null) {
                    originalbmp=bitmap;
                    showTakenPicture(bitmap);
                    //showImageView.setImageBitmap(bitmap);
                    Toast.makeText(getActivity(),
                            "Picture Captured Successfully", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getActivity(),
                            "Failed to Capture the picture. kindly Try Again:",
                            Toast.LENGTH_LONG).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }

    };

     // Decode strem.
    public static Bitmap decodeStrem(File fil, Uri selectedImage,
                                     Context mContext) {

        Bitmap bitmap = null;
        try {

            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver()
                    .openInputStream(selectedImage));

            final int THUMBNAIL_SIZE = getThumbSize(bitmap);

            bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE,
                    THUMBNAIL_SIZE, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos
                    .toByteArray()));

            return bitmap = rotateImage(bitmap, fil.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

   //Decode file.
    public static Bitmap decodeFile(File f, int sampling) {
        try {
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(
                    new FileInputStream(f.getAbsolutePath()), null, o2);

            o2.inSampleSize = sampling;
            o2.inTempStorage = new byte[48 * 1024];

            o2.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(
                    new FileInputStream(f.getAbsolutePath()), null, o2);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bitmap = rotateImage(bitmap, f.getAbsolutePath());
            return bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }


     // Rotate image function
    public static Bitmap rotateImage(Bitmap bmp, String imageUrl) {
        if (bmp != null) {
            ExifInterface ei;
            int orientation = 0;
            try {
                ei = new ExifInterface(imageUrl);
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

            } catch (IOException e) {
                e.printStackTrace();
            }
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_UNDEFINED:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }
            Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
                    bmpHeight, matrix, true);
            return resizedBitmap;
        } else {
            return bmp;
        }
    }



     //Gets the thumb size
    public static int getThumbSize(Bitmap bitmap) {

        int THUMBNAIL_SIZE = 250;
        if (bitmap.getWidth() < 300) {
            THUMBNAIL_SIZE = 250;
        } else if (bitmap.getWidth() < 600) {
            THUMBNAIL_SIZE = 500;
        } else if (bitmap.getWidth() < 1000) {
            THUMBNAIL_SIZE = 750;
        } else if (bitmap.getWidth() < 2000) {
            THUMBNAIL_SIZE = 1500;
        } else if (bitmap.getWidth() < 4000) {
            THUMBNAIL_SIZE = 2000;
        } else if (bitmap.getWidth() > 4000) {
            THUMBNAIL_SIZE = 2000;
        }
        return THUMBNAIL_SIZE;
    }

    //Set Brightness function
   public Bitmap SetBrightness(Bitmap src, int value) {
        // original image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if(R > 255) { R = 255; }
                else if(R < 0) { R = 0; }

                G += value;
                if(G > 255) { G = 255; }
                else if(G < 0) { G = 0; }

                B += value;
                if(B > 255) { B = 255; }
                else if(B < 0) { B = 0; }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    //Set Contrast function
    private Bitmap adjustedContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    //override onResume method to recover camera view
    @Override
    public void onResume() {
        super.onResume();

        if (mCamera == null) {
            mCamera=getCameraInstance();
            mPreview = new CameraPreview(this.getActivity(), mCamera);
            preview.addView(mPreview);
        }
    }

    //override onPause method to release camera
    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null){
            //              mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;

        }
    }

    //Listener methods for Button and Image Buttons


    private View.OnClickListener takePhotoButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Get an image from the camera
            mCamera.takePicture(null, null, mPicture);
            layer=false;

        }
    };

    private View.OnClickListener closeButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // go back to previous fragment
            ((NavigationBar) getActivity()).goBack();
        }
    };


    private View.OnClickListener cropButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Intent Crop Listener
            Bitmap bitmap=null;
            //Retireve image from ImageView
            if(layer==true){
                bitmap =((BitmapDrawable)((LayerDrawable)cameraView.getDrawable()).getDrawable(0)).getBitmap();

            }else{
                bitmap=((BitmapDrawable) cameraView.getDrawable()).getBitmap();
            }
            //Passing URL path to perform Crop
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
            performCrop(Uri.parse(path));

        }
    };

    //Crop function
    private void performCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            intent = "cropPhoto";
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private View.OnClickListener brightnessButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Brightness Button selected
            seekLayout.setVisibility(View.VISIBLE);
            confirmationLayout.setVisibility(View.VISIBLE);
            filterLayout.setVisibility(View.INVISIBLE);


            cbSeekbar.setProgress(brightness + 50);
            numberSeekTextView.setText(String.valueOf(brightness));
            originalbmpfiltered = ((BitmapDrawable)cameraView.getDrawable()).getBitmap();


            optionCamera="brightness";
        }
    };
    private View.OnClickListener contrastButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Contrast Button selected
            seekLayout.setVisibility(View.VISIBLE);
            confirmationLayout.setVisibility(View.VISIBLE);
            filterLayout.setVisibility(View.INVISIBLE);

            cbSeekbar.setProgress(contrast+50);
            numberSeekTextView.setText(String.valueOf(contrast));
            originalbmpfiltered = ((BitmapDrawable)cameraView.getDrawable()).getBitmap();

            optionCamera="contrast";
        }
    };

    //Post photo in Instagram
    private View.OnClickListener btnAcceptListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {

                //verified if Instagram is installed
                if (verificaInstagram()) {

                    //Save photo in phone memory to get URL and use share Intend

                    File miDirs = new File(
                            Environment.getExternalStorageDirectory() + "/myphotos");
                    if (!miDirs.exists())
                        miDirs.mkdirs();

                    final Calendar c = Calendar.getInstance();
                    String new_Date = c.get(Calendar.DAY_OF_MONTH) + "-"
                            + ((c.get(Calendar.MONTH)) + 1) + "-"
                            + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR)
                            + "-" + c.get(Calendar.MINUTE) + "-"
                            + c.get(Calendar.SECOND);

                    String imageFilePath = String.format(
                            Environment.getExternalStorageDirectory() + "/myphotos"
                                    + "/%s.jpg", "te1t(" + new_Date + ")");

                    Uri selectedImage = Uri.parse(imageFilePath);

                    Bitmap bitmap = null;
                    if(layer==true){
                        bitmap =((BitmapDrawable)((LayerDrawable)cameraView.getDrawable()).getDrawable(0)).getBitmap();

                    }else{
                        bitmap=((BitmapDrawable) cameraView.getDrawable()).getBitmap();
                    }

                    //create a file to write bitmap data
                    File f = new File(imageFilePath);
                    f.createNewFile();

                    //Convert bitmap to byte array
                    //Bitmap bitmap = your bitmap;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();


                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    final ContentResolver cr = getActivity().getContentResolver();
                    final String[] p1 = new String[] {
                            MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.TITLE, MediaStore.Images.ImageColumns.DATE_TAKEN
                    };
                    Cursor c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, p1, null, null, p1[1] + " DESC");

                    if (c1.moveToFirst() ) {
                        Log.i("Teste", "last picture (" + c1.getString(1) + ") taken on: " + new Date(c1.getLong(2)));
                    }

                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    shareIntent.setPackage("com.instagram.android");

                    startActivity(Intent.createChooser(shareIntent, "Share to"));

                    c1.close();
                }else{
                    Toast.makeText(getActivity(),
                            "Please Install Instagram before publish", Toast.LENGTH_LONG);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    //Function to verified Instagram
    private boolean verificaInstagram(){
        boolean installed = false;

        try {
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.instagram.android", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    //SeekBar change Listener to change brightness and constrast
    private SeekBar.OnSeekBarChangeListener cbSeekbarListener= new SeekBar.OnSeekBarChangeListener() {


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    try {
                        final int trueProgress=progress-50;
                        numberSeekTextView.setText(String.valueOf(trueProgress));


                        final int p=trueProgress;
                        final Bitmap[] temporalBitmap = new Bitmap[1];
                        final int[] bri = new int[1];
                        final int[] cont = new int[1];

                        Thread tGetPic = new Thread( new Runnable() {
                        int trueProgress1=p;

                        public void run() {

                            switch (optionCamera){


                                case "brightness":

                                    temporalBitmap[0]=SetBrightness(originalbmpfiltered,trueProgress1);
                                    bri[0] =trueProgress1;


                                    break;
                                case "contrast":

                                    temporalBitmap[0] =adjustedContrast(originalbmpfiltered,trueProgress1);
                                    cont[0] =trueProgress1;

                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    tGetPic.start();
                        tGetPic.join();
                        brightness=bri[0];
                        contrast=cont[0];
                        cameraView.setImageBitmap(temporalBitmap[0]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
    };

    //Accept Button for Brightness and Contrast result
    private View.OnClickListener acceptButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            seekLayout.setVisibility(View.INVISIBLE);
            confirmationLayout.setVisibility(View.INVISIBLE);
            filterLayout.setVisibility(View.VISIBLE);

            switch (optionCamera){

                case "brightness":

                    originalbmp = ((BitmapDrawable)cameraView.getDrawable()).getBitmap();
                    originalbmpfiltered=originalbmp;
                    brightness=0;

                    cbSeekbar.setProgress(brightness+50);
                    numberSeekTextView.setText(String.valueOf(brightness));

                    break;
                case "contrast":

                    originalbmp = ((BitmapDrawable)cameraView.getDrawable()).getBitmap();
                    originalbmpfiltered=originalbmp;
                    contrast=0;

                    cbSeekbar.setProgress(contrast+50);
                    numberSeekTextView.setText(String.valueOf(contrast));

                    break;
                default:


                    break;
            }
        }
    };

    //Negate Brightness and Contrast results
    private View.OnClickListener xButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            seekLayout.setVisibility(View.INVISIBLE);
            confirmationLayout.setVisibility(View.INVISIBLE);
            filterLayout.setVisibility(View.VISIBLE);

            switch (optionCamera){

                case "brightness":
                    cameraView.setImageBitmap(originalbmpfiltered);
                    break;
                case "contrast":
                    cameraView.setImageBitmap(originalbmpfiltered);
                    break;
                default:


                    break;
            }
        }
    };

    //Grid on Camera view
    private View.OnClickListener gridButtonListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            grid=!grid;
            mPreview.postInvalidate();
        }
    };

    //No FilterButton
    private View.OnClickListener noFilterButtonListener= new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            cameraView.setImageBitmap(originalbmp);
            layer=false;
        }
    };

    //Invert Filter Button
    private View.OnClickListener filter1ButtonListener= new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            bitmapImage=originalbmp;
            Bitmap newPhoto=invertImage(bitmapImage);
            cameraView.setImageBitmap(newPhoto);
            layer=false;
        }
    };

    //Dirty Filter Button
    private View.OnClickListener filter2ButtonListener= new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            bitmapImage=originalbmp;
            LayerDrawable newPhoto2=dirtyImage(bitmapImage);
            cameraView.setImageDrawable(newPhoto2);
            layer=true;
        }
    };

    //Black and White Filter Button
    private View.OnClickListener filter3ButtonListener= new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            bitmapImage=originalbmp;
            Bitmap newPhoto=BlackWhiteImage(bitmapImage);
            cameraView.setImageBitmap(newPhoto);
            layer=false;
        }
    };

    // Load Image with Intent Object
    private View.OnClickListener loadImageButtonListener= new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            layer=false;
            intent="selectPhoto";
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);

        }
    };

    // OnActivityResult for Load Photo and Crop Photo Intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        //Select Intent Result
        if (resultCode == getActivity().RESULT_OK && intent.equals("selectPhoto")) {
            // Let's read picked image data - its URI
            Uri pickedImage = imageReturnedIntent.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            //Transform Image into Bitmap using BitmapFactory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            originalbmp=bitmap;
            showTakenPicture(bitmap);

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
        //Crop Intent Result
        if (intent.equals("cropPhoto")) {

            if (imageReturnedIntent != null) {
                // get the returned data
                Bundle extras = imageReturnedIntent.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");
                originalbmp=selectedBitmap;
                cameraView.setImageBitmap(selectedBitmap);
            }
        }
    }

    //Flash Option Listener
    private View.OnClickListener flashButtonListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Camera.Parameters params=mCamera.getParameters();
            if(!params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_ON) ){
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                mCamera.setParameters(params);
                flashButton.setImageResource(R.drawable.ic_flash_on);

            }else{
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                mCamera.startPreview();
                flashButton.setImageResource(R.drawable.ic_flash_off2);
            }

        }
    };

    //Back Button to go back to CameraView
    private View.OnClickListener backButtonListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            onBackPressedButton();
            mCamera.startPreview();

        }
    };


    //Check if the user has a camera
    private boolean hasCamera() {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //Filters Functions
    //Invert Function method.
    private Bitmap invertImage(Bitmap originalImage) {
        //creating Blank Bitmap with same dimensions as original Image
        Bitmap modifiedImage= Bitmap.createBitmap(originalImage.getWidth(), originalImage.getHeight(), originalImage.getConfig());
        int A,R,G,B;
        int pixelColor;
        int height=originalImage.getHeight();
        int width = originalImage.getWidth();

        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                pixelColor = originalImage.getPixel(x, y);
                A = Color.alpha(pixelColor);
                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);
                modifiedImage.setPixel(x, y, Color.argb(A, R, G, B));

            }
        }
        return modifiedImage;
    }
    //BlackWhite method.
    private Bitmap BlackWhiteImage(Bitmap originalImage) {
        //creating Blank Bitmap with same dimensions as original Image
        Bitmap modifiedImage= Bitmap.createBitmap(originalImage.getWidth(), originalImage.getHeight(), originalImage.getConfig());
        int A,R,G,B;
        int pixelColor;
        int gray;
        int height=originalImage.getHeight();
        int width = originalImage.getWidth();

        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                pixelColor = originalImage.getPixel(x, y);
                A = Color.alpha(pixelColor);
                gray=(Color.red(pixelColor)+Color.green(pixelColor)+Color.blue(pixelColor))/3;
                R = gray;
                G = gray;
                B = gray;
                modifiedImage.setPixel(x, y, Color.argb(A, R, G, B));

            }
        }
        return modifiedImage;
    }


    //Dirty Filter method.
    private LayerDrawable dirtyImage(Bitmap originalImage){
        Bitmap modifiedImage= Bitmap.createBitmap(originalImage.getWidth(),originalImage.getHeight(),originalImage.getConfig());
        Drawable[] layers=new Drawable[2];

        layers[0]=new BitmapDrawable(getResources(), originalImage);
        layers[1]=getResources().getDrawable(R.drawable.noise);
        LayerDrawable layerDrawable=new LayerDrawable(layers);
        return layerDrawable;
    }

    //Method called when picture were taken
    private void showTakenPicture(Bitmap bitmap) {
        brightness=0;
        contrast=0;
        vUpperPanel.showNext();
        vLowerPanel.showNext();
        cameraView.setImageBitmap(bitmap);
        updateState(STATE_SETUP_PHOTO);
    }

    //Method called when back Button is pressed and return to CameraView
    public void onBackPressedButton() {
        if (currentState == STATE_SETUP_PHOTO) {
            //btnTakePhoto.setEnabled(true);
            vUpperPanel.showPrevious();
            vLowerPanel.showPrevious();
            updateState(STATE_TAKE_PHOTO);
        }
    }

    // Method to generate animation between CameraView and ImageView
    private void updateState(int state) {

        Animation slide_in_left, slide_out_right;
        slide_in_left = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_out_right);


        currentState = state;
        if (currentState == STATE_TAKE_PHOTO) {

            vUpperPanel.setInAnimation(slide_in_left);
            vUpperPanel.setOutAnimation(slide_out_right);
            vLowerPanel.setInAnimation(slide_in_left);
            vLowerPanel.setOutAnimation(slide_out_right);
            cameraView.setVisibility(View.INVISIBLE);

        } else if (currentState == STATE_SETUP_PHOTO) {

            vUpperPanel.setInAnimation(slide_out_right);
            vUpperPanel.setOutAnimation(slide_in_left);
            vLowerPanel.setInAnimation(slide_out_right);
            vLowerPanel.setOutAnimation(slide_in_left);
            cameraView.setVisibility(View.VISIBLE);

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
     // sets the action bar title when in a user feed fragment
     public void onAttach(Context context) {
        super.onAttach(context);

        ActionBar actionBar = ((AppCompatActivity)
                this.getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Parameters.NavigationBarActivity.activityFeedBar(false);
    }

    @Override
    // sets the action bar title when in a user feed fragment
    public void onDetach() {
        super.onDetach();

        ActionBar actionBar = ((AppCompatActivity)
                this.getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }




    /** A basic Camera preview class */
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera1;
        private Camera.Parameters mParameters;
        private static final String TAG = "MyCamera";
        private Paint paint = new Paint();


        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera1 = camera;
            mParameters = mCamera1.getParameters();

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            //paintgrid
            this.setWillNotDraw(false);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {

                final Camera.Parameters p = mCamera1.getParameters();
                final Camera.Size size = getOptimalSize();
                //p.setPictureSize(size.height,size.width);

                p.set("jpeg-quality", 100);
                p.set("rotation", 90);
                p.setPictureFormat(PixelFormat.JPEG);
                p.setPreviewSize(size.width, size.height);
               // p.setPreviewSize(preview.getHeight(), preview.getWidth());// here w h are reversed
                mCamera1.setParameters(p);
                mCamera1.setDisplayOrientation(90);
                mCamera1.setPreviewDisplay(holder);
                mCamera1.startPreview();


            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        //Get Optimeze size picture for different camera specifications
        private Camera.Size getOptimalSize() {
            final double PREVIEW_SIZE_FACTOR = 1.30;
            Camera.Size result = null;
            final Camera.Parameters parameters = mCamera1.getParameters();

            for (final Camera.Size size : parameters.getSupportedPreviewSizes()) {
                if (size.width <= getWidth() * PREVIEW_SIZE_FACTOR && size.height <= getHeight() * PREVIEW_SIZE_FACTOR) {
                    if (result == null) {
                        result = size;
                    } else {
                        final int resultArea = result.width * result.height;
                        final int newArea = size.width * size.height;

                        if (newArea > resultArea) {
                            result = size;
                        }
                    }
                }
            }
            if (result == null) {
                result = parameters.getSupportedPreviewSizes().get(0);
            }
            return result;
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }
        public Camera.Parameters getCameraParameters(){
            return mCamera1.getParameters();
        }

        //Overrride OnDraw method to draw Grid on Camera View
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if(grid){
            //  Find Screen size first
            int screenWidth = preview.getWidth();
            int screenHeight = (int) (preview.getHeight());

            //  Set paint options
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.argb(255, 255, 255, 255));

            canvas.drawLine((screenWidth/3)*2,0,(screenWidth/3)*2,screenHeight,paint);
            canvas.drawLine((screenWidth/3),0,(screenWidth/3),screenHeight,paint);
            canvas.drawLine(0,(screenHeight/3)*2,screenWidth,(screenHeight/3)*2,paint);
            canvas.drawLine(0,(screenHeight/3),screenWidth,(screenHeight/3),paint);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            CameraFragment camera=new CameraFragment();

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera1.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera1.setPreviewDisplay(mHolder);
                mCamera1.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }

}








