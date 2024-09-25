package com.example.accelerationsensors;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    TextView acceleration_x;//x方向的加速度
    TextView acceleration_y;//y方向的加速度
    TextView acceleration_z;//z方向的加速度
    TextView acceleration_total;//显示总加速度
    TextView stepCountTextView; // 声明用于显示步数的TextView对象
    //显示运动情况
    TextView ifmove;
    SensorManager mySensorManager;//SensorManager对象引用
    //SensorManagerSimulator mySensorManager;//声明SensorManagerSimulator对象,调试时用


    private int step;
    private double original_value;
    private double last_value;
    private double current_value;
    private boolean motionState=true; //是否处于运动状态
    private boolean processState=true;  //是否已经开始计步

    @Override
    public void onCreate(Bundle savedInstanceState) {//重写onCreate方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//设置当前的用户界面
        acceleration_x = (TextView) findViewById(R.id.acceleration_x);//得到acceleration_x的引用
        acceleration_y = (TextView) findViewById(R.id.acceleration_y);//得到acceleration_y的引用
        acceleration_z = (TextView) findViewById(R.id.acceleration_z);//得到acceleration_z的引用
        acceleration_total = (TextView) findViewById(R.id.acceleration_total);//得到acceleration_total的引用
        stepCountTextView =(TextView) findViewById(R.id.stepCountTextView);//计步器的引用

        step=0;
        original_value=0;
        last_value =0;
        current_value =0;

        //设置一个用于判断是否运动的控件
        ifmove = (TextView) findViewById(R.id.ifmove);//得到ifmove的引用
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);//获得SensorManager
    }
    private SensorEventListener mySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float total_acceleration;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] values = sensorEvent.values;
                //通过开平方和得到总加速度
                total_acceleration = (float) Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0]
                        + sensorEvent.values[1] * sensorEvent.values[1]
                        + sensorEvent.values[2] * sensorEvent.values[2]);

                //设置加速度的显示情况
                acceleration_x.setText("X方向上的加速度：" + sensorEvent.values[0]);
                acceleration_y.setText("Y方向上的加速度：" + sensorEvent.values[1]);
                acceleration_z.setText("Z方向上的加速度：" + sensorEvent.values[2]);
                acceleration_total.setText("和加速度：" + total_acceleration);

                //通过与本地9.8左右的加速度进行比较从而判断手机是否运动
                //因为实际本地加速度会在9.8-9.9之间浮动，通过物理知识可知小于9.8是在上升，大于9.9是在下降
                if (total_acceleration < 9.9 && total_acceleration > 9.8) {
                    ifmove.setText("静止中");
                } else if (total_acceleration >= 9.9) {
                    ifmove.setText("正在下降");
                } else if (total_acceleration <= 9.8) {
                    ifmove.setText("正在上升");
                }
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                double range = 5; //设置一个精度范围
                float[] value = sensorEvent.values;
                float current_value = (float) Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0]
                        + sensorEvent.values[1] * sensorEvent.values[1]
                        + sensorEvent.values[2] * sensorEvent.values[2]);
                //获取当前的总加速度

                //向上加速的状态
                if (motionState == true) {
                    if (current_value >= last_value)
                        last_value = current_value;
                    else {
                        //检测到一次峰值
                        if (Math.abs(current_value - last_value) > range) {
                            original_value = current_value;
                            motionState = false;
                        }
                    }
                }
                //向下加速的状态
                if (motionState == false) {
                    if (current_value <= last_value)
                        last_value = current_value;
                    else {
                        //检测到一次峰值
                        if (Math.abs(current_value - last_value) > range) {
                            original_value = current_value;
                            if (processState == true) {
                                step++; //检测到开始记录，步数加1
                                if (processState == true) {
                                    updateStepCount(); //更新读数
                                }
                            }
                            motionState = true;
                        }
                    }
                }
            }

        }
        // 更新步数显示
        private void updateStepCount() {
            // 可以将步数显示在一个TextView上
            // 这里假设你有一个名为stepCountTextView的TextView来显示步数
            stepCountTextView.setText("当前步数：" + step);
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    @Override
    protected void onResume() {//重写的onResume方法
        mySensorManager.registerListener(//注册监听
                mySensorListener, //监听器SensorListener对象
                mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),//传感器的类型为加速度
                SensorManager.SENSOR_DELAY_UI//传感器事件传递的频度
        );
        super.onResume();
    }
    @Override
    protected void onPause() {//重写onPause方法
        mySensorManager.unregisterListener(mySensorListener);//取消注册监听器
        super.onPause();
    }
}