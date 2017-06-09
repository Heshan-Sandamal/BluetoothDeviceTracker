package com.cs4492.cseuom.bluetoothdevicetracker.check_moving;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by win 8 on 6/8/2017.
 */

public class deviceMoving extends Activity implements SensorEventListener {

    private deviceMoving mov;
    TextView txt; int xval,yval,zval ;
    SensorManager sm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm=(SensorManager)this.getSystemService(Context.SENSOR_SERVICE);

        sm.registerListener((SensorEventListener) this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub
    }
    //This method is called when your mobile moves any direction
    @Override
    public void onSensorChanged(SensorEvent event)
    {


        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {


            //get x, y, z values
            float value[]=event.values;
            float x=value[0];
            float y=value[1];
            float z=value[2];
            //use the following formula
            //use gravity according to your place if you are on moon than use moon gravity
            String x1= String.valueOf(x);
            String y1= String.valueOf(y);
            String z1= String.valueOf(z);

            String[] x1part = x1.split("\\."); // String array, each element is text between dots
            String[] y1part = y1.split("\\."); // String array, each element is text between dots
            String[] z1part = z1.split("\\."); // String array, each element is text between dots

            xval= Integer.parseInt(x1part[0]);
            yval= Integer.parseInt(y1part[0]);
            zval= Integer.parseInt(z1part[0]);
            int res= Integer.parseInt(x1part[0])+Integer.parseInt(y1part[0])+Integer.parseInt(z1part[0]);
            String val= String.valueOf(res);
            txt.setText(val);
            //+ Log.d("MOV", x1part[0]+" "+y1part[0]+" "+z1part[0]) ;

            //Log.d("MOV",x+" "+y+" "+z) ;

//            float asr=(x*x+y*y+z*z)/(SensorManager.GRAVITY_EARTH*
//                    SensorManager.GRAVITY_EARTH);
            //If mobile move any direction then the following condition will become true
//            if(asr>=2)
//            {
//                //Generate random number every time and display on text view
//                Random r=new Random();
//                int i=r.nextInt(10);
//                Log.d("MOV", String.valueOf(i)) ;
//            }
        }
    }

}