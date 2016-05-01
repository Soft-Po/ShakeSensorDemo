package com.softpo.shakesensordemo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager manager;
    private Sensor accelerometerSensor;
    private ImageView image_up;
    private ImageView image_down;
    //声明一个播放音乐的类
    private SoundPool soundPool;
    //声明震动类
    private Vibrator vibrator;
    private int soundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSensor();

        initView();

        initSoundPool();

        initVibrator();
    }


    private void initVibrator() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void initSoundPool() {
        if(Build.VERSION.SDK_INT>21){

            SoundPool.Builder builder = new SoundPool.Builder();

            //设置builder
            builder.setMaxStreams(5);

            AudioAttributes.Builder attributesBuilder =
                    new AudioAttributes.Builder();
            //设置attributesBuilder
            attributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);

            AudioAttributes audioAttributes = attributesBuilder.build();

            builder.setAudioAttributes(audioAttributes);

            soundPool = builder.build();
        }else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }
        //现在soundPool音乐池中加载了一个音乐资源
        soundID = soundPool.load(this, R.raw.awe, 1);
    }

    private void initView() {
        image_up = ((ImageView) findViewById(R.id.image_up));

        image_down = ((ImageView) findViewById(R.id.image_down));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册传感器
        manager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //解除注册，释放资源
        manager.unregisterListener(this, accelerometerSensor);
    }

    private void initSensor() {
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //获取传感器传回来的数据
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];
        Log.d("flag", "----------------->x: "+x);

        //摇一摇是必须有第一定幅度，才能触发该功能
        //返回的值是加速度
        if(Math.abs(x)>15||Math.abs(y)>15||Math.abs(z)>15){
//            showSensorInfo.setText("摇到了");
            //启动震动
            //启动声音
            //启动动画
            /**
             * 参数一：an array of longs of
             * times for which to turn the vibrator on or off.
             *
             * 参数二：震动重复，如果是-1不进行重复，指定一个int类型
             * 数代表着重复几次
             */
            vibrator.vibrate(new long[]{500,200,500,200,300},-1);

            //启动声音

            /**
             * @param soundID a soundID returned by the load() function
             * @param leftVolume left volume value (range = 0.0 to 1.0)
             * @param rightVolume right volume value (range = 0.0 to 1.0)
             * @param priority stream priority (0 = lowest priority)
             * @param loop loop mode (0 = no loop, -1 = loop forever)
             * @param rate playback rate (1.0 = normal playback, range 0.5 to 2.0)
             * @return non-zero streamID if successful, zero if failed
             */
            soundPool.play(soundID,1,1,0,1,1);

            //启动动画
            startAnimation();

        }
    }

    private void startAnimation() {
        //设置image_up的动画
        //true代表着，该set中所有的动画动画插值器分享同一个
        //动画插值器，让动画效果更完美
        AnimationSet set1 = new AnimationSet(true);

        TranslateAnimation up1 = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                -1//控件向上移动它自身的高度
        );
        up1.setDuration(1000);

        TranslateAnimation up2 = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                -1,
                TranslateAnimation.RELATIVE_TO_SELF,
                0
        );

        up2.setDuration(1000);

        //设置up1延迟500毫秒执行，颤动效果
        up1.setStartOffset(500);
        set1.addAnimation(up1);
        set1.addAnimation(up2);

        image_up.startAnimation(set1);


        //对image_down设置动画
        AnimationSet set2 = new AnimationSet(true);

        TranslateAnimation down1 = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                1//控件向下移动它自身的高度
        );
        down1.setDuration(1000);

        TranslateAnimation down2 = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                0,
                TranslateAnimation.RELATIVE_TO_SELF,
                1,
                TranslateAnimation.RELATIVE_TO_SELF,
                0
        );

        down2.setDuration(1000);

        //设置down1延迟500毫秒执行，颤动效果
        down1.setStartOffset(500);
        set2.addAnimation(down1);
        set2.addAnimation(down2);
        image_down.startAnimation(set2);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
