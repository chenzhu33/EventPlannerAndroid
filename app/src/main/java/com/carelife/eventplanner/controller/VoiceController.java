package com.carelife.eventplanner.controller;

import java.io.File;
import java.io.IOException;

import com.carelife.eventplanner.utils.StringUtil;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

/**
 * Created by carelife on 2016/8/13.
 */
public class VoiceController {
    private final static String FILE_PATH = "/com.eventplanner/";

    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    private static VoiceController instance;

    public static VoiceController getInstance() {
        if(instance == null) {
            instance = new VoiceController();
        }
        return instance;
    }

    private VoiceController() {

    }

    public String startRecord(Context context) {
        String filePath = generateFilePath(context);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
        return filePath;
    }

    public void stopRecord() {
        if(mRecorder != null) {
            mRecorder.stop();
            //mRecorder.release();
            mRecorder = null;
        }
    }

    public void startPlay(String filePath, MediaPlayer.OnCompletionListener listener) {
        if(filePath == null || filePath.isEmpty()) {
            return;
        }
        mPlayer = new MediaPlayer();
        try{
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(listener);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if(mPlayer != null) {
            mPlayer.stop();
            //mPlayer.release();
            mPlayer = null;
        }
    }

    public String generateFilePath(Context context) {
        String fileName = StringUtil.getRandomString(10)+".3gp";
        String path = context.getExternalCacheDir().getAbsolutePath() + FILE_PATH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path + fileName;
    }

}
