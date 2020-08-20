package com.anthonyh.recordshow;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anthonyh.recordshow.audio.agent.BaseAudioCustomAgent;
import com.anthonyh.recordshow.audio.agent.MonoAgent;
import com.anthonyh.recordshow.audio.agent.StroAgent;
import com.anthonyh.recordshow.audio.record.DefaultRecord;
import com.anthonyh.recordshow.audio.record.RecordConfig;
import com.anthonyh.recordshow.audio.record.RecordException;
import com.anthonyh.recordshow.audio.record.RecordManger;
import com.anthonyh.recordshow.ui.ShowActivity;
import com.anthonyh.recordshow.widget.VadView;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
///
public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    Button button, startLocFileBt;
    VadView vadViewTop, vadViewBottom;
    EditText editText;
    RadioGroup radioGroup;
    String registId;
    RecordConfig recordConfig;
    BaseAudioCustomAgent audioCustomAgent;
    ExecutorService executorService;
    Future customFuture;
    private static final String TAG = "MainActivity";

    public static   String AudioPath = "/sdcard/recordFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vadViewTop = findViewById(R.id.wav1);
        editText = findViewById(R.id.fileNameEd);
        vadViewBottom = findViewById(R.id.wav2);
        radioGroup = findViewById(R.id.channel);
        radioGroup.setOnCheckedChangeListener(this);
        button = findViewById(R.id.recordButton);
        startLocFileBt = findViewById(R.id.startLoc);
        refreshButton();
        executorService = Executors.newFixedThreadPool(1);
//        AudioPath = Environment.getDownloadCacheDirectory().getPath()+"/recordFile";
    }

    private void refreshButton() {
        switch (RecordManger.getInstance().getRecordState()) {
            case RECORDING:
                button.setText("点击停止");
                break;
            case NONE:
                button.setText("点击录音");
                break;
        }

    }


    public void recordClick(View view) {
        RecordManger.RecordState recordState = RecordManger.getInstance().getRecordState();
        Log.e(TAG, "recordClick: " + recordState);
        if (recordState == RecordManger.RecordState.RECORDING) {
            stopRecord();
        } else {
            startRecord();
        }
    }


    /**
     *   int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
     *                 AudioFormat.ENCODING_PCM_16BIT);
     *         mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRate, AudioFormat.CHANNEL_IN_MONO,
     *                 AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 10);
     *         this.readSize=readSize;
     */
    private void startRecord() {
        try {
            String fileName = getFileName();
            if (TextUtils.isEmpty(fileName)) {
                Toast.makeText(getApplicationContext(), "请输入正确的文件名", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "开始录音", Toast.LENGTH_SHORT).show();
            recordConfig = getRecordConfig();
            switch (recordConfig.getChannelConfig()) {
                case AudioFormat.CHANNEL_IN_MONO:
                    vadViewTop.setVisibility(View.VISIBLE);
                    vadViewBottom.setVisibility(View.GONE);
                    audioCustomAgent = new MonoAgent(vadViewTop);
                    vadViewTop.startShow();
                    break;
                case AudioFormat.CHANNEL_IN_STEREO:
                    vadViewTop.setVisibility(View.VISIBLE);
                    vadViewBottom.setVisibility(View.VISIBLE);
                    audioCustomAgent = new StroAgent(vadViewTop, vadViewBottom);
                    vadViewTop.startShow();
                    vadViewBottom.startShow();
                    break;
            }
            audioCustomAgent.setFilePath(AudioPath);
            audioCustomAgent.setFileName(fileName);
            registId = UUID.randomUUID().toString();
            RecordManger.getInstance().registAudioCustom(registId, audioCustomAgent);
            customFuture = executorService.submit(audioCustomAgent);
            RecordManger.getInstance().startRecord(recordConfig, new DefaultRecord());
            refreshButton();
            disableView();
        } catch (RecordException e) {
            e.printStackTrace();
        }
    }

    private String getFileName() {
        String fileName = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(fileName))
            fileName += ".pcm";
        return fileName;
    }

    private void stopRecord() {
        Toast.makeText(getApplicationContext(), "结束录音", Toast.LENGTH_SHORT).show();
        editText.setText("");
        RecordManger.getInstance().unRegistAudioCustom(registId);
        RecordManger.getInstance().stopRecord();
        refreshButton();
        if (customFuture != null) {
            customFuture.cancel(true);
        }
        enableView();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
    }

    private RecordConfig getRecordConfig() {
        int sampleRate = getSam();
        int channel = getChannel();
        int audioformat = AudioFormat.ENCODING_PCM_16BIT;
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channel,
                audioformat);
        RecordConfig recordConfig = new RecordConfig.Builder()
                .audioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .sampleRateInHz(sampleRate)
                .channelConfig(channel)
                .audioFormat(audioformat)
                .bufferSizeInBytes(10 * minBufferSize)
                .readBufferSize(30 * sampleRate / 1000 * 10)// 30ms
                .build();

        return recordConfig;
    }

    private int getSam() {
        return 16000;
    }

    private int getChannel() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.mono:
                return AudioFormat.CHANNEL_IN_MONO;
            case R.id.sreo:
                return AudioFormat.CHANNEL_IN_STEREO;
        }
        return AudioFormat.CHANNEL_IN_MONO;
    }

    public void disableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(false);
        }
    }

    public void enableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(true);
        }
    }

    private void enableView() {
        enableRadioGroup(radioGroup);
        editText.setEnabled(true);
        startLocFileBt.setEnabled(true);
    }

    private void disableView() {
        disableRadioGroup(radioGroup);
        editText.setEnabled(false);
        startLocFileBt.setEnabled(false);
    }

    public void startLocClick(View view) {
        startActivity(new Intent(this, ShowActivity.class));
    }


}
