package it.uniba.dib.sms232413.object;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SensorHandler implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private boolean isSensorEnabled = false;
    private float thresholdHigh = 0.3f; // soglia per "molto tremore"
    private float thresholdMedium = 0.2f; // soglia per "poco tremore"
    private float thresholdLow = 0.1f; // soglia per "nessun tremore"
    private List<float[]> sensorDataList = new ArrayList<>();
    private static final int DATA_INTERVAL = 10000;
    private Context context;
    private SensorCallback callback;

    public SensorHandler(Context context, SensorCallback callback) {
        this.context = context;
        this.callback = callback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void startSensor() {
        if (!isSensorEnabled && gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

            // Attendere 10 secondi e poi arrestare il sensore
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sensorManager.unregisterListener(SensorHandler.this);
                    evaluateSensorData();
                }
            }, DATA_INTERVAL);
        } else {
            Toast.makeText(context, "Il sensore non è disponibile.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        sensorDataList.add(new float[]{x, y, z});
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    private void evaluateSensorData() {
        if (sensorDataList.isEmpty()) {
            callback.onResult("Nessun dato rilevato.");
            return;
        }

        float rmsValue = calculateRMS(sensorDataList);

        if (rmsValue > 0.3) {
            callback.onResult("Molto tremore!");
        } else if (rmsValue > 0.2) {
            callback.onResult("Abbastanza tremore!");
        } else if (rmsValue > 0.1) {
            callback.onResult("Poco tremore!");
        } else {
            callback.onResult("Nessun tremore!");
        }
    }

    private float calculateRMS(List<float[]> data) {
        float sum = 0;

        for (float[] values : data) {
            sum += Math.pow(values[0], 2) + Math.pow(values[1], 2) + Math.pow(values[2], 2);
        }

        float mean = sum / data.size();
        return (float) Math.sqrt(mean);
    }

    public interface SensorCallback {
        void onResult(String result);
    }
}
