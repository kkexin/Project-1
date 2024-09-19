package com.example.myapplication;

import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.concurrent.Executors;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private List<Float> acc_x = new ArrayList<>(), acc_y = new ArrayList<>(), acc_z = new ArrayList<>();


    int k_bps = 0;
    DBHandler DBHandler;
    private boolean m_RecordingHRFlag = false;
    private String m_heartRate;
    private String m_cameraId;
    private static final int kREQUEST_CAMERA = 3891;
    private Sensor m_sensor;
    private SensorManager m_sensorManager;
    private boolean m_ReadingAccFlag = false;
    private ExecutorService m_execService;
    private CameraManager m_cameraManager;
    private static final long kDURATION_MS = 45000;

    private Handler m_accHandler = new Handler();


    private void work() {
        startSymptomsS();
        startButtonRespiRate();

        startButtonHeartRate();
        startSignsPush();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHandler = new DBHandler(MainActivity.this);
        m_execService = Executors.newCachedThreadPool();

        work();

        try {
            startCameraWork();
        } catch (CameraAccessException e) {
            Toast.makeText(MainActivity.this, "camera wrong", Toast.LENGTH_SHORT).show();
            Log.e("camera wrong", e.getMessage() + " " + e.getStackTrace());
        }
    }

    private void startSignsPush() {
        Button uploadSigns = findViewById(R.id.uploadSigns);

        uploadSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHandler.Heart_rate_SET_FUNC(m_heartRate);
                DBHandler.Respiratory_rate_SET_Func(String.valueOf(k_bps));
                DBHandler.addDBItems();

                Toast.makeText(MainActivity.this, "Rates pushed sqlite success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startButtonRespiRate() {
        Button respiRateBtn = findViewById(R.id.computeRespiRate);

        respiRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!m_ReadingAccFlag) {
                    runRespiratorySensorsFunc(respiRateBtn);
                }
                else {
                    shutdownRespiratorySensorsFunc(respiRateBtn);
                }
            }
        });
    }

    private void startButtonHeartRate() {
        Button heartRateBtn = findViewById(R.id.computeHeartRate);
        
        heartRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!m_RecordingHRFlag) {
                    runRecordingForHeartRateFunc(heartRateBtn);
                }
                else {
                    shutdownRecordingForHeartRateFunc(heartRateBtn);
                }
            }
        });
    }

    private void startCameraWork() throws CameraAccessException {
        m_cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        m_cameraId = m_cameraManager.getCameraIdList()[0];
    }

    private void runRecordingForHeartRateFunc(Button record) {
        m_RecordingHRFlag = true;
        record.setClickable(false);

        Intent item = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        item.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        startActivityForResult(item, kREQUEST_CAMERA);
    }

    private void shutdownRecordingForHeartRateFunc(Button record) {
        m_RecordingHRFlag = false;
        record.setClickable(true);
    }

    private void RespiRatoryRate_setFunc(int k_bps) {
        TextView temptv = findViewById(R.id.respiRateTextView);
        temptv.setText(String.valueOf(k_bps));
    }

    private void shutdownRespiratorySensorsFunc(Button button) {
        if(acc_x.size() > 0) {
            k_bps = calcRespiRateFunc();
            Log.d("stop resp rate", String.valueOf(k_bps));
            RespiRatoryRate_setFunc(k_bps);
        }

        m_ReadingAccFlag = false;
        button.setClickable(true);
        m_sensorManager.unregisterListener(this);
    }

    private void runRespiratorySensorsFunc(Button respiRateBtn) {
        Toast.makeText(this, "start breath for 45s", Toast.LENGTH_SHORT).show();

        m_ReadingAccFlag = true;
        respiRateBtn.setClickable(false);

        m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        m_sensor = m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        m_sensorManager.registerListener(this, m_sensor, SensorManager.SENSOR_DELAY_NORMAL);

        m_accHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this ,"respiratory rate stopped", Toast.LENGTH_SHORT).show();
                shutdownRespiratorySensorsFunc(respiRateBtn);
            }
        }, kDURATION_MS);
    }

    private void startSymptomsS() {
        Button button = findViewById(R.id.m_sympts);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "switch to sympts tab!", Toast.LENGTH_SHORT).show();

                Intent tmp = new Intent(MainActivity.this, SymptomActivity.class);
                MainActivity.this.startActivity(tmp);
            }
        });
    }

    private void initializeVideoViewWithVideoCapture(Uri kuri) {
        VideoView view = findViewById(R.id.videoViewXML);
        view.setVideoURI(kuri);

        view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.setVolume(0f,0f);
            }
        });
        view.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == kREQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                Uri vuri = data.getData();

                TextView htv = findViewById(R.id.heartRateTextView);
                htv.setText("is loaded ...");

                Toast.makeText(this, "Video saved to:\n" + vuri,
                        Toast.LENGTH_LONG).show();

                initializeVideoViewWithVideoCapture(vuri);
                Toast.makeText(this, "task in back_ground",
                        Toast.LENGTH_SHORT).show();

                calcHeartRateInBack(this, vuri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "video cancelled",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "record fail",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int calcRespiRateFunc() {
        float pre_value = 0f;
        float cur_value = 0f;
        pre_value = 10f;

        int cnt = 0;

        for (int i = 11; i <= 450; i++) {
            cur_value = (float) Math.sqrt(Math.pow(acc_x.get(i), 2.0) + Math.pow(acc_y.get(i), 2.0) + Math.pow(acc_z.get(i), 2.0));

            if (Math.abs(pre_value - cur_value) > 0.15) {
                cnt++;
            }

            pre_value = cur_value;
        }

        double tmp = (double) cnt / 45.0;
        return (int) (tmp * 30);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        acc_x.add(sensorEvent.values[0]);
        acc_y.add(sensorEvent.values[1]);
        acc_z.add(sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor m_sensor, int i) {
    }

    private void setHeartRateFunc(String rate) {
        TextView htv = findViewById(R.id.heartRateTextView);
        htv.setText(rate);

        Toast.makeText(MainActivity.this, "heart rate calc done",
                Toast.LENGTH_SHORT).show();
    }

    private void calcHeartRateInBack(Context context, Uri uri) {
        final String[] m_heartRate = new String[1];

        Handler tmp = new Handler(Looper.getMainLooper());

        Runnable uiUpdateRunnable = new Runnable() {
            @Override
            public void run() {

                setHeartRateFunc(m_heartRate[0]);
            }
        };

        Runnable backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                m_heartRate[0] = startTaskInBack(context, uri);

                tmp.post(uiUpdateRunnable);
            }
        };

        m_execService.submit(backgroundRunnable);
    }

    private List<Bitmap> retrieveFramesFromVideo(Context context, Uri uri) {
        Log.d("heart rate in back", "frme extract start");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        List<Bitmap> frameList = new ArrayList<>();

        try {
            retriever.setDataSource(context, uri);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
            int aduration = Integer.parseInt(duration);
            int i = 10;

            while (i < aduration) {
                Log.d("heart rate in back", "extracting frame at idx: " + i);
                Bitmap bitmap = retriever.getFrameAtIndex(i);
                frameList.add(bitmap);
                i += 5;
            }
        } catch (Exception e) {
            Log.e("frame Extract error", e.getMessage());
        } finally {
            retriever.release();
        }

        return frameList;
    }

    private List<Long> processFramesForColorAnalysis(List<Bitmap> frameList) {
        Log.d("heart rate in back", "frame color analysis start");
        List<Long> colorBuckets = new ArrayList<>();

        for (Bitmap frame : frameList) {
            long redBucket = 0;
            long pixelCount = 0;

            // Analyze pixels in the 100x100 area (from 550x550 to 650x650)
            for (int y = 550; y < 650; y++) {
                for (int x = 550; x < 650; x++) {
                    int pixel = frame.getPixel(x, y);
                    pixelCount++;
                    redBucket += Color.red(pixel) + Color.blue(pixel) + Color.green(pixel);
                }
            }

            colorBuckets.add(redBucket); // Add the redBucket sum for this frame
        }

        return colorBuckets;
    }

    private String calculateHeartRateFromAnalysis(List<Long> colorBuckets) {
        Log.d("heart rate in back", "heart rate calc start");

        List<Long> smoothedValues = new ArrayList<>();

        // Smooth the values over a sliding window of 5 frames
        for (int i = 0; i < colorBuckets.size() - 5; i++) {
            long smoothedValue = (colorBuckets.get(i) + colorBuckets.get(i + 1) +
                    colorBuckets.get(i + 2) + colorBuckets.get(i + 3) +
                    colorBuckets.get(i + 4)) / 4;
            smoothedValues.add(smoothedValue);
        }

        long previousValue = smoothedValues.get(0);
        int spikeCount = 0;

        // Detect spikes in the smoothed values
        for (int i = 1; i < smoothedValues.size(); i++) {
            long currentValue = smoothedValues.get(i);
            if ((currentValue - previousValue) > 3500) {
                spikeCount++;
            }
            previousValue = currentValue;
        }

        // Calculate heart rate from detected spikes
        int rate = (int) (((float) spikeCount / 45) * 60);
        String heartRate = String.valueOf(rate / 2);

        Log.d("heart rate in back", "is calc: " + heartRate);
        return heartRate;
    }

    // Main function to initiate the background task
    private String startTaskInBack(Context context, Uri uri) {
        Log.d("heart rate in back", "task start");

        List<Bitmap> frameList = retrieveFramesFromVideo(context, uri);

        List<Long> colorBuckets = processFramesForColorAnalysis(frameList);

        return calculateHeartRateFromAnalysis(colorBuckets);
    }

}