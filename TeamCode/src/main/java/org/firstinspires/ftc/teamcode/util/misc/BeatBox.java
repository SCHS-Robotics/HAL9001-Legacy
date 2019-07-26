package org.firstinspires.ftc.teamcode.util.misc;

import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;

import org.firstinspires.ftc.teamcode.system.source.Robot;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class BeatBox {
    private Robot robot;
    private Map<String, MediaPlayer> songs;

    public BeatBox(Robot robot){
        this.robot = robot;
    }

    public void addSong(String songName, MediaPlayer song){
        songs.put(songName, song);
    }

    public void removeSong(String songName){
        songs.remove(songName);
    }

    public void playSong(String songName){
        songs.get(songName).start();
    }

    public void stopSong(String songName){
        songs.get(songName).stop();
    }

    public void setSongLoop(String songName, boolean loop){
        songs.get(songName).setLooping(loop);
    }

    public void playRandomSong(){
        Collection songKeys = songs.values();
        MediaPlayer[] songs = (MediaPlayer[]) songKeys.toArray();
        Random random = new Random();
        songs[random.nextInt(songs.length)].start();
    }

    public void ultimateBeats(){
        for(MediaPlayer song : songs.values()) {
            song.setLooping(true);
            song.start();

        }
    }

    public void baseBoost(String name, int level) {

        level = level*1000;

        BassBoost bassBoost = new BassBoost(0,songs.get(name).getAudioSessionId());
        songs.get(name).attachAuxEffect(bassBoost.getId());
        songs.get(name).setAuxEffectSendLevel(level);
    }
}
