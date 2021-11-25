package com.specknet.pdiotapp.utils;

import android.util.Log;

import java.util.ArrayList;

public class ModelManager {
    private DataBuffer<Float> respeck_acc_xs;
    private DataBuffer<Float> respeck_acc_ys;
    private DataBuffer<Float> respeck_acc_zs;
    private DataBuffer<Float> respeck_gyro_xs;
    private DataBuffer<Float> respeck_gyro_ys;
    private DataBuffer<Float> respeck_gyro_zs;

    private DataBuffer<Float> thingy_acc_xs;
    private DataBuffer<Float> thingy_acc_ys;
    private DataBuffer<Float> thingy_acc_zs;


    public ModelManager(int bufferSize){
        respeck_acc_xs = new DataBuffer<Float>(bufferSize);
        respeck_acc_ys = new DataBuffer<Float>(bufferSize);
        respeck_acc_zs = new DataBuffer<Float>(bufferSize);
        respeck_gyro_xs = new DataBuffer<Float>(bufferSize);
        respeck_gyro_ys = new DataBuffer<Float>(bufferSize);
        respeck_gyro_zs = new DataBuffer<Float>(bufferSize);

        thingy_acc_xs = new DataBuffer<Float>(bufferSize);
        thingy_acc_ys = new DataBuffer<Float>(bufferSize);
        thingy_acc_zs = new DataBuffer<Float>(bufferSize);
    }

    public void addData(String name, Float data){
        if(name == "respeck_acc_x"){
            respeck_acc_xs.add(data);
        }else if(name == "respeck_acc_y"){
            respeck_acc_ys.add(data);
        }else if(name == "respeck_acc_z"){
            respeck_acc_zs.add(data);
        }else if(name == "respeck_gyro_x"){
            respeck_gyro_xs.add(data);
        }else if(name == "respeck_gyro_y"){
            respeck_gyro_ys.add(data);
        }else if(name == "respeck_gyro_z"){
            respeck_gyro_zs.add(data);
        }else if(name == "thingy_acc_x"){
            thingy_acc_xs.add(data);
        }else if(name == "thingy_acc_y"){
            thingy_acc_ys.add(data);
        }else if(name == "thingy_acc_z"){
            thingy_acc_zs.add(data);
        }else{
            Log.e("Add Data","Add data error: name doesn't exsit");
        }


    }

}
