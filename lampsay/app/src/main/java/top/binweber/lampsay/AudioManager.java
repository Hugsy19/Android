package top.binweber.lampsay;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wangb on 2017/12/6.
 */

public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static AudioManager mInstance;

    private boolean isPrepared;

    private AudioManager(String dir) {
        mDir = dir;
    }

    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if(mInstance == null)
                    mInstance = new AudioManager(dir);
            }
        }

        return mInstance;
    }

    public void prepareAudio() {

        try {
            isPrepared = false;

            File dir = new File(mDir);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = generateFileName();
            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(file.getAbsolutePath()); // output file
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // audio source is MIC
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB); // audio format
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // audio encoder

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;

            if(mListener != null)
                mListener.wellPrepared();

        }catch (IllegalStateException | IOException e){
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if(isPrepared) {
            try {
                return maxLevel * mMediaRecorder.getMaxAmplitude()/32768 + 1;
            } catch (Exception e) {

            }
        }

        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel() {
        release();
        if(mCurrentFilePath != null){
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

}


