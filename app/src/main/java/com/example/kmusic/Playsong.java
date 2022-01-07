package com.example.kmusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Handler;

import java.io.File;
import java.util.ArrayList;


public class Playsong extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    String k;
    TextView textView,current,total;
    ArrayList<File> songs;
    static MediaPlayer mediaPlayer;
    ImageView previous,pause,next;
    String textContent;
    int position;
    SeekBar seekBar;
    double current_pos, total_duration;
    private static MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder stateBuilder;
    String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";


    //    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onStop() {
//        super.onStop();
//        notificationmanager();
//    }
    private String timerConversion(long total_duration) {
        String audioTime;
        int dur = (int) total_duration;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }



    public void setAudioProgress(){

        current_pos=mediaPlayer.getCurrentPosition();
        total_duration=mediaPlayer.getDuration();

        total.setText(timerConversion((long) total_duration));
        current.setText(timerConversion((long) current_pos));

        seekBar.setMax((int) total_duration);
        final Handler handler= new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    current_pos = mediaPlayer.getCurrentPosition();
                    current.setText(timerConversion((long) current_pos));
                    seekBar.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            if(position!=songs.size()-1){
                                position=position+1;
                            }
                            else{
                                position=0;
                            }
                            Uri uri=Uri.parse(songs.get(position).toString());
                            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                            mediaPlayer.start();
                            pause.setImageResource(R.drawable.pause);
                            seekBar.setMax(mediaPlayer.getDuration());
                            textContent=songs.get(position).getName().toString();
                            textView.setText(textContent);
                            setAudioProgress();
                            notificationmanager();
                        }
                    });
                } catch (IllegalStateException ed){
                    ed.printStackTrace();
                }

            }

        };
        handler.postDelayed(runnable, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void notificationmanager() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            NotificationManager manager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Bitmap largeicon = BitmapFactory.decodeResource(getResources(), R.drawable.largeicon);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        int icon;
        String playPause;
        int nexticon = R.drawable.next;
        String next = "next";
        int previousicon = R.drawable.previous;
        String previos = "previous";
        if (mediaPlayer.isPlaying()) {
            icon = R.drawable.pause;
            playPause = "pause";
        } else {
            icon = R.drawable.play;
            playPause = "Play";
        }
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, playPause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)
        );
        NotificationCompat.Action skipToNext = new NotificationCompat.Action(
                nexticon, next,
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        );
        NotificationCompat.Action skiptoprevious = new NotificationCompat.Action(
                previousicon, previos,
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        );
        Intent intent3=new Intent(this,Playsong.class);
        startActivity(intent3);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent3,0);
        if (mediaPlayer.isPlaying()) {
            notificationBuilder.setAutoCancel(false)
                    .setContentTitle(textContent)
                    .setContentText("K' Music")
                    .setContentInfo("Information")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.smallicon)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(largeicon)
                    .addAction(skiptoprevious)
                    .addAction(playPauseAction)
                    .addAction(skipToNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2))

                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_LOW);
            notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(3, notificationBuilder.build());

        } else {
            notificationBuilder.setAutoCancel(true)
                    .setContentTitle(textContent)
                    .setContentText("K' Music")
                    .setContentInfo("Information")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.smallicon)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(largeicon)
                    .addAction(skiptoprevious)
                    .addAction(playPauseAction)
                    .addAction(skipToNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2))

                    .setOngoing(false)
                    .setPriority(Notification.PRIORITY_LOW);
            notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(3, notificationBuilder.build());
        }
    }


    public void playbtn(){
        if (mediaPlayer.isPlaying()) {
            pause.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        } else {
            pause.setImageResource(R.drawable.pause);
            mediaPlayer.start();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void previousbtn(){
        mediaPlayer.stop();
        mediaPlayer.release();
        if(position!=0){
            position=position-1;
        }
        else{
            position=songs.size()-1;
        }
        Uri uri=Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        pause.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration());
        textContent= songs.get(position).getName();
        textView.setText(textContent);
        setAudioProgress();
        notificationmanager();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void nextbtn(){
        mediaPlayer.stop();
        mediaPlayer.release();
        if(position!=songs.size()-1){
            position=position+1;
        }
        else{
            position=0;
        }
        Uri uri=Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        pause.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration());
        textContent=songs.get(position).getName().toString();
        textView.setText(textContent);
        setAudioProgress();
        notificationmanager();
    }
    private void createMediasessioncompat(){
        mediaSessionCompat = new MediaSessionCompat(this,"simple player");
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mediaSessionCompat.setMediaButtonReceiver(null);
        mediaSessionCompat.setPlaybackState(stateBuilder.build());
        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPlay() {
                super.onPlay();
                playbtn();
                notificationmanager();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPause() {
                super.onPause();
                playbtn();
                notificationmanager();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                nextbtn();
                notificationmanager();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                previousbtn();
                notificationmanager();
            }
        });
        mediaSessionCompat.setActive(true);

    }



    public void mainactivityintent() {
        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        mediaPlayer = intent.getParcelableExtra("mediaplayer");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);

        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);

        textView=findViewById(R.id.textView);
        current=findViewById(R.id.current);
        total=findViewById(R.id.total);
        pause=findViewById(R.id.pause);
        previous=findViewById(R.id.previous);
        next=findViewById(R.id.next);
        seekBar=findViewById(R.id.seekBar);
        createMediasessioncompat();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mainactivityintent();
        }
        else {
            mainactivityintent();
        }


        notificationmanager();
        int mediaplayerduration=mediaPlayer.getDuration();
        setAudioProgress();
       seekBar.setMax(mediaplayerduration);

       pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playbtn();
                }
            });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousbtn();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextbtn();
            }
        });

    }

    public static class MyReceiver extends BroadcastReceiver{
        public MyReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSessionCompat,intent);
        }
    }

}