package com.atif.bestmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.graphics.PorterDuff.Mode.SRC_IN;

public class PlayerActivity extends AppCompatActivity {
Button btnPause,btnNext,btnPrevious;
TextView songTextLabel,timer,duration;
SeekBar seekBar;
Equalizer Equilizer;
static MediaPlayer myMediaPlayer;
int Position;
ArrayList<File> mySongs;
Thread updateseekBar;
String sname;
Animation rotate;
ImageView music_icon;
private double finalTime, StartTime=0;

    @SuppressLint({"NewApi", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPause=findViewById(R.id.btnPause);
        btnNext=findViewById(R.id.btnNext);
        btnPrevious=findViewById(R.id.btnPrevious);
        songTextLabel=findViewById(R.id.songTextLabel);
        music_icon=findViewById(R.id.music_icon);
        seekBar=findViewById(R.id.seekBar);
       // timer=findViewById(R.id.timer);
        //duration=findViewById(R.id.duration);
        rotate= AnimationUtils.loadAnimation(getApplicationContext(), R.xml.rotate_music_icon);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateseekBar=new Thread(){
            @Override
            public void run() {
               int totalDuration = myMediaPlayer.getDuration();
               int currentPosition = 0;

               while (currentPosition<totalDuration){
                   try {
                       sleep(500);
                       currentPosition = myMediaPlayer.getCurrentPosition();
                       seekBar.setProgress(currentPosition);
                   }catch (InterruptedException e){
                       e.printStackTrace();
                   }
               }


            }
        };
       if (myMediaPlayer!=null){
           myMediaPlayer.stop();
           myMediaPlayer.release();
       }
        Intent i = getIntent();
       Bundle bundle = i.getExtras();

       mySongs=(ArrayList) bundle.getParcelableArrayList("song");

       sname=mySongs.get(Position).getName().toString();
       String songName = i.getStringExtra("songname");
       songTextLabel.setText(songName);
       songTextLabel.setSelected(true);

       Position = bundle.getInt("pos",0);
       Uri u =Uri.parse(mySongs.get(Position).toString());

       myMediaPlayer= MediaPlayer.create(getApplicationContext(),u);
       myMediaPlayer.start();
       seekBar.setMax(myMediaPlayer.getDuration());
       updateseekBar.start();
       seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorprivateRed), PorterDuff.Mode.MULTIPLY);
       seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorWhite), SRC_IN);


       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
               myMediaPlayer.seekTo(seekBar.getProgress());

           }
       });

       btnPause.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              // finalTime=myMediaPlayer.getDuration();
               seekBar.setMax(myMediaPlayer.getDuration());
               if (myMediaPlayer.isPlaying()){
                   btnPause.setBackgroundResource(R.drawable.play_icon);
                   myMediaPlayer.pause();
                   music_icon.setAnimation(null);
                   // timer.setText(String.format("%d min, %D sec", TimeUnit.MICROSECONDS.toMinutes((long) finalTime),
                                          //                                 TimeUnit.MILLISECONDS.toSeconds((long) finalTime)-
                                          //                                         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                   // );
                   // seekBar.setProgress((int)StartTime);

               }else {
                   btnPause.setBackgroundResource(R.drawable.pause_icon);
                   myMediaPlayer.start();
                   music_icon .setVisibility(View.VISIBLE);
                   music_icon.startAnimation(rotate);


               }
           }
       });

       btnNext.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               myMediaPlayer.stop();
               myMediaPlayer.release();
               Position = ((Position+1)%mySongs.size());
               Uri u =Uri.parse(mySongs.get(Position).toString());
               myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
               sname = mySongs.get(Position).getName().toString();
               songTextLabel.setText(sname);
               myMediaPlayer.start();
           }
       });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();

                Position = ((Position-1)<0)?(mySongs.size()-1):(Position-1);
                Uri u = Uri.parse(mySongs.get(Position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(Position).getName().toString();
                songTextLabel.setText(sname);
                myMediaPlayer.start();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
