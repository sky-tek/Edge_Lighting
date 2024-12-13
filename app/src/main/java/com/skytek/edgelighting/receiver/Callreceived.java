package com.skytek.edgelighting.receiver;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.skytek.edgelighting.R;

public class Callreceived {

    // declaring required variables
    private Context context;
    static public View mReceviedView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;

    public Callreceived(Context context , String imgPath ){
        this.context=context;

        Log.d("dhghfhj", "Callreceived:method ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Set the layout parameters of the window
                mParams = new WindowManager.LayoutParams(
                        // Shrink the window to wrap the content rather than filling the screen
                        WindowManager.LayoutParams.MATCH_PARENT,

                        WindowManager.LayoutParams.MATCH_PARENT,
                        // Display it on top of other application windows
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        // Add the FLAG_LAYOUT_NO_LIMITS flag to extend into the area behind the status bar
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        // Make the underlying application window visible through any transparent parts
                        PixelFormat.TRANSLUCENT
                );
            }
        }
        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
//        mReceviedView = layoutInflater.inflate(R.layout.callreceived, null);


        if (imgPath.equals("1")) {
            //  mView.findViewById(R.id.backgroundImg).setBackgroundResource(R.drawable.defaultimgwallpapereagle);
//            ConstraintLayout constraintLayout = mReceviedView.findViewById(R.id.callreceived);

            // Create a black transparent color
            int blackColor = Color.argb(210, 0, 0, 0);

// Set the background resource on the ConstraintLayout
//            constraintLayout.setBackgroundResource(R.drawable.splash007);

// Create a View for the black transparent overlay
            View blackOverlay = new View(context);
            blackOverlay.setBackgroundColor(blackColor);

// Set the dimensions of the black overlay
            ConstraintLayout.LayoutParams overlayLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
            );
            blackOverlay.setLayoutParams(overlayLayoutParams);

// Add the black overlay to the ConstraintLayout
//            constraintLayout.addView(blackOverlay);

// Adjust the order of views to make sure the black overlay is on top
            blackOverlay.bringToFront();
            //            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.splash007);
//            float blurRadius = 25f; // Adjust the blur radius as needed
//
//            Bitmap blurredBitmap = applyBlur(context, bitmap, blurRadius);
//            Drawable blurredDrawable = new BitmapDrawable(context.getResources(), blurredBitmap);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                constraintLayout.setBackground(blurredDrawable);
//            } else {
//                constraintLayout.setBackgroundDrawable(blurredDrawable);
//            }
        } else {
            String imagePath = imgPath.toString(); // Replace with the actual image file path

            int blackColor = Color.argb(210, 0, 0, 0);
            // Load the image file into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            // Apply blur effect to the Bitmap
            float blurRadius = 25.0f; // Adjust the blur radius as needed
            Bitmap blurredBitmap = applyBlur(context, bitmap, blurRadius);

            // Set the blurred Bitmap as the background
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);

    /*      ConstraintLayout constraintLayout = mReceviedView.findViewById(R.id.callreceived);
            constraintLayout.setBackground(drawable);*/

            // Create a View for the black transparent overlay
            View blackOverlay = new View(context);
            blackOverlay.setBackgroundColor(blackColor);

// Set the dimensions of the black overlay
            ConstraintLayout.LayoutParams overlayLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
            );
            blackOverlay.setLayoutParams(overlayLayoutParams);

// Add the black overlay to the ConstraintLayout
//            constraintLayout.addView(blackOverlay);

// Adjust the order of views to make sure the black overlay is on top
            blackOverlay.bringToFront();

        }


       /* mReceviedView.findViewById(R.id.decline_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        mParams.gravity = Gravity.CENTER;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);*/

    }
    private Bitmap applyBlur(Context context, Bitmap bitmap, float radius) {
        Bitmap inputBitmap = bitmap.copy(bitmap.getConfig(), true);

        RenderScript renderScript = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);

        output.copyTo(inputBitmap);

        renderScript.destroy();

        return inputBitmap;
    }
    public void open() {

        try {
            if(mReceviedView.getWindowToken()==null) {
                if(mReceviedView.getParent()==null) {
                    mWindowManager.addView(mReceviedView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1",e.toString());
        }
    }

    public void close() {

        try {

            // remove the view from the window
            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mReceviedView);
            // invalidate the view
            mReceviedView.invalidate();
            // remove all views
            ((ViewGroup)mReceviedView.getParent()).removeAllViews();

        } catch (Exception e) {
            Log.d("Error2",e.toString());
        }
    }
}