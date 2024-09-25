package com.example.opticalsensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.renderscript.Sampler.Value;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

        private SensorManager sensor;
        private TextView text;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                sensor = (SensorManager) getSystemService(SENSOR_SERVICE);
                text = (TextView) findViewById(R.id.textView1);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present
                getMenuInflater().inflate(R.menu.activity_main, menu);
                return true;
        }

        @Override
        protected void onPause() {
// TODO Auto-generated method stub
                sensor.unregisterListener(this);
                super.onPause();
        }

        @Override
        protected void onResume() {
                // TODO Auto-generated method stub
                sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_LIGHT),
                        SensorManager.SENSOR_DELAY_GAME);
                super.onResume();
        }

        @Override
        protected void onStop() {
                // TODO Auto-generated method stub
                sensor.unregisterListener(this);
                super.onStop();
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub
                float[] values = event.values;
                int sensorType = event.sensor.TYPE_LIGHT;
                if (sensorType == Sensor.TYPE_LIGHT) {
                        String light_intensity = "LightIntensity: " + String.valueOf(values[0]) + '\n';
                        light_intensity += "SensorName: " + event.sensor.getName() + '\n';
                        light_intensity += "PowerDraw: " + event.sensor.getPower() + '\n';
                        light_intensity += "MaximumRange: " + event.sensor.getMaximumRange() + '\n';
                        text.setText(light_intensity);
                }
        }
}
