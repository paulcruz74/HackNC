package com.parse.starter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SendActivity extends ActionBarActivity {
    private static final String DEBUG_TAG = "Velocity";
    private VelocityTracker mVelocityTracker = null;
    TextView x;
    TextView y;
    private float lastXVel=0;
    private float lastYVel=0;
    private String m;
    private Uri fUri;
    private static ImageView bubz;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        x = (TextView) findViewById(R.id.velocityX);
        y = (TextView) findViewById(R.id.velocityY);
        bubz = (ImageView) findViewById(R.id.bubble);
        Intent intent = getIntent();
        fUri = Uri.parse(intent.getStringExtra("fileUri"));
        m = intent.getStringExtra("message");


    }
    public byte[] readBytes(InputStream inputStream) throws IOException{
        if(inputStream == null){
            return null;
        }
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while((len = inputStream.read(buffer)) != -1){
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        float xpos = event.getX();
        float ypos = event.getY();
        bubz.setX(xpos - bubz.getWidth() / 2);
        bubz.setY(ypos - bubz.getHeight() * 2 / 2);

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                bubz.setVisibility(View.VISIBLE);
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                Log.d("", "X velocity: " +
                        VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                pointerId));
                Log.d("", "Y velocity: " +
                        VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                                pointerId));


                lastXVel = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                lastYVel = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);
                x.setText("X Velocity = " + lastXVel + "\n X position: " + xpos);
                y.setText("Y Velocity = " + lastYVel + "\n Y position: " + ypos);

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                bubz.setVisibility(View.INVISIBLE);
                if(fUri == null){
                    byte[] inputData = null;
                    // upload m, lastXVel, lastYVel

                }
                else{
                    try {
                        InputStream iStream = getContentResolver().openInputStream(fUri);
                        byte[] inputData = readBytes(iStream);
                        //upload inputData, m, lastXVel, lastYVel

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.d("SendActivity", m);
                Log.d("SendActivity", fUri.toString());
                break;
        }
        return true;
    }
}