package com.skytek.edgelighting;


import static android.telecom.CallAudioState.ROUTE_EARPIECE;
import static android.telecom.CallAudioState.ROUTE_SPEAKER;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;

import com.skytek.edgelighting.receiver.Callreceived;

import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallingService extends InCallService {
//    WindowOverlay window;

    Callreceived callReceived;

    MediaPlayer player;

    String getImgPath;

    Boolean checkMute = false;

    Boolean checkSpeaker = false;
    String getcallnumber;
    private LottieAnimationView animationView;
    private LottieAnimationView arrowAbove;
    private LottieAnimationView arrowDown;
    private float animationViewRight;
    private float arrowAboveLeft;
    private float arrowDownLeft;


    @Override
    public void onCreate() {
        Log.d("dhghfhj", "onCreate: incoming calling service");
        super.onCreate();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                player = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);
                player.setLooping(true);

            } else {
                player = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);
                player.setLooping(true);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.d("callcheck", "call removed");
        try {
//            window.close();
            try {
                if (callReceived != null) {
                    callReceived.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                player.stop();
            } catch (Exception e) {
                Log.d("checkException", "asdasdsad");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.d("dhghfhj", "onCallAdded: incoming call");
        getcallnumber = call.getDetails().getHandle().toString();
        Call.Details details = call.getDetails();
        getcallnumber = getcallnumber.substring(4);


        try {
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (getcallnumber.charAt(0) == '+') {
            getcallnumber = getcallnumber.replaceFirst("\\+92", "0");
        }
      /*  if (Resizing.userWallpapers == null) {
            Resizing.userWallpapers = PrefConfig.getHashMap(getApplicationContext());
        }
        if (Resizing.userWallpapers != null) {
            if (Resizing.userWallpapers.containsKey(getcallnumber)) {
                getImgPath = Resizing.userWallpapers.get(getcallnumber);
                window = new WindowOverlay(getApplicationContext(), Resizing.userWallpapers.get(getcallnumber));
                window.open();
            } else {
                window = new WindowOverlay(getApplicationContext(), "1");
                window.open();
            }
        } else {
            window = new WindowOverlay(getApplicationContext(), "1");
            window.open();
        }

            try {
                TextView firstName = WindowOverlay.mView.findViewById(R.id.firstName);
                TextView lastName = WindowOverlay.mView.findViewById(R.id.lastName);
                TextView phoneNumber = WindowOverlay.mView.findViewById(R.id.phoneNumber);
                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q && details.getContactDisplayName() != null && details.getContactDisplayName() != "") {
                    String fullName = details.getContactDisplayName();
                    String[] nameParts = fullName.split(" ");
                    String string1 = nameParts[0];  // "Jaffar"
                    String string2 = "";
                    try {
                        if (nameParts[1] != null && !nameParts[1].equals("")) {
                            string2 = nameParts[1];  // "Abbas"
                        } else {
                            string2 = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    firstName.setText(string1);
                    phoneNumber.setText(getcallnumber);

                    if (string2 != null && !string2.equals("")) {
                        lastName.setText(string2);
                    } else {
                        lastName.setVisibility(View.GONE);
                    }
                } else {
                    if (getcallnumber != null && !getcallnumber.equals("")) {
                        phoneNumber.setText(getcallnumber);
                    }
                    firstName.setText("UnKnown");
                    lastName.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        animationView = WindowOverlay.mView.findViewById(R.id.animationView);
        try {
            arrowAbove = WindowOverlay.mView.findViewById(R.id.arrowAbove);
            arrowDown = WindowOverlay.mView.findViewById(R.id.arrowBelow);
            animationView.setAnimation(R.raw.buttonanimation);
            animationView.playAnimation();
            arrowDown.setAnimation(R.raw.arrow_up);
            arrowDown.playAnimation();

            arrowAbove.setAnimation(R.raw.arrow_down);
            arrowAbove.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        animationView.setOnTouchListener(new View.OnTouchListener() {
            private float startX; // Change variable name to startX
            private float buttonX; // Change variable name to buttonX
            private float originalX; // Change variable name to originalX

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX(); // Use getRawX() to get X-coordinate
                        buttonX = v.getX(); // Use getX() to get current X position
                        originalX = buttonX;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        animationViewRight = animationView.getX() + animationView.getWidth();
                        arrowAboveLeft = arrowAbove.getX() + 400;
                        arrowDownLeft = arrowDown.getX();

                        float currentX = event.getRawX(); // Use getRawX() to get X-coordinate
                        float deltaX = currentX - startX; // Calculate delta in X direction
                        float newX = buttonX + deltaX; // Calculate new X position
                        v.setX(newX); // Set the new X position
                        if (deltaX < 0 && animationViewRight <= arrowAboveLeft) {
                            Log.d("afasfasf", "kaato");
                            arrowAbove.setVisibility(View.GONE);
                        } else if (deltaX > 0 && animationView.getX() + 200 >= arrowDownLeft) {
                            Log.d("afasfasf", "uthao");
                            arrowDown.setVisibility(View.GONE);
                        }
                        else {
                            arrowAbove.setVisibility(View.VISIBLE)  ;
                            arrowDown.setVisibility(View.VISIBLE);
                        }


                        return true;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX(); // Use getRawX() to get X-coordinate
//                        handleGesture(startX, endX, call);
                        v.setX(originalX); // Reset button position to original X position
                        return true;
                }
                return false;
            }
        });


    }
/*
    private void handleGesture(float startX, float endY, Call call) {
        float deltaX = endY - startX;
        Log.d("klfhksjdhf", "animationViewRight : " + animationViewRight);
        Log.d("klfhksjdhf", "arrowAboveLeft : " + arrowAboveLeft);
        Log.d("klfhksjdhf", "animationView.getX() : " + animationView.getX());
        Log.d("klfhksjdhf", "arrowDownLeft : " + arrowDownLeft);

        if (deltaX < 0 && animationViewRight <= arrowAboveLeft) {
            // User moved the button upwards (attend call)
            call.reject(false, "not a reason");
            Log.d("permission", "call rejected");
//            window.close();
            try {
                player.stop();
            } catch (Exception e) {
                Log.d("checkException", "asdasdsad");
            }
        } else if (deltaX > 0 && animationView.getX() + 200 >= arrowDownLeft) {
            // User moved the button downwards (reject call)


            call.answer(0);
            try {
                player.stop();
            } catch (Exception e) {
                Log.d("checkException", "asdasdsad");
            }

//            window.close();

            //check for sending wallpaper to the call received screen

            try {
                if (Resizing.userWallpapers != null) {
                    for (Map.Entry<String, String> entry : Resizing.userWallpapers.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                    }
                    if (Resizing.userWallpapers.containsKey(getcallnumber)) {
                        getImgPath = Resizing.userWallpapers.get(getcallnumber);

                        callReceived = new Callreceived(getApplicationContext(), getImgPath);

                        callReceived.open();
                    } else {

                        callReceived = new Callreceived(getApplicationContext(), "1");
                        callReceived.open();

                    }
                } else {

                    callReceived = new Callreceived(getApplicationContext(), "1");
                    callReceived.open();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            TextView callrecevedFirstName = callReceived.mReceviedView.findViewById(R.id.firstName);
            TextView callreceivedPhoneNumber = callReceived.mReceviedView.findViewById(R.id.phoneNumber);

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (call.getDetails().getContactDisplayName() != null && call.getDetails().getContactDisplayName() != "") {
                        try {
                            callrecevedFirstName.setText(call.getDetails().getContactDisplayName());
                            if (getcallnumber != null && !getcallnumber.equals("")) {
                                callreceivedPhoneNumber.setText(getcallnumber);
                            } else {
                                callreceivedPhoneNumber.setText("Unknown");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            callrecevedFirstName.setText("Unknown");
                            if (getcallnumber != null && !getcallnumber.equals("")) {
                                callreceivedPhoneNumber.setText(getcallnumber);
                            } else {
                                callreceivedPhoneNumber.setText("Unknown");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            } else {
                callrecevedFirstName.setText("Unknown");
                if (getcallnumber != null && !getcallnumber.equals("")) {
                    callreceivedPhoneNumber.setText(getcallnumber);
                } else {
                    callreceivedPhoneNumber.setText("Unknown");
                }
            }

            callReceived.mReceviedView.findViewById(R.id.frame_mute).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        if (checkMute) {
                            checkMute = false;
                            audioManager.setMicrophoneMute(false);
                            ImageView volumeIcon = callReceived.mReceviedView.findViewById(R.id.mute_icon);
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.mic_checking);
                            volumeIcon.setImageDrawable(drawable);
                        } else {
                            checkMute = true;
                            audioManager.setMicrophoneMute(true);
                            ImageView volumeIcon = callReceived.mReceviedView.findViewById(R.id.mute_icon);
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.mic_off_permanetly);
                            volumeIcon.setImageDrawable(drawable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            callReceived.mReceviedView.findViewById(R.id.frame_volume).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (checkSpeaker) {
                            checkSpeaker = false;
                            setAudioRoute(ROUTE_EARPIECE);
                            ImageView volumeIcon = callReceived.mReceviedView.findViewById(R.id.volume_icon);
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.volume);
                            volumeIcon.setImageDrawable(drawable);
                        } else {
                            checkSpeaker = true;
                            setAudioRoute(ROUTE_SPEAKER);
                            ImageView volumeIcon = callReceived.mReceviedView.findViewById(R.id.volume_icon);
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.volume_speaker);
                            volumeIcon.setImageDrawable(drawable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            callReceived.mReceviedView.findViewById(R.id.decline_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        call.disconnect();

                        callReceived.close();

                        call.reject(false, "mssage is here");

                        Log.d("permission123", "on call declined");
                        callReceived = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } else {
            // Reset button position to original
//            animationView.setX(originalX);
            // Rest of your code...
        }

    }*/
}
