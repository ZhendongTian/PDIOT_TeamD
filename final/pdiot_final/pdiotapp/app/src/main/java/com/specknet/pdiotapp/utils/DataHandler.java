package com.specknet.pdiotapp.utils;

import java.util.ArrayList;

public class DataHandler {

    private ArrayList<Float> respeck_acc_xs;
    private ArrayList<Float> respeck_acc_ys;
    private ArrayList<Float> respeck_acc_zs;
    private ArrayList<Float> respeck_gyro_xs;
    private ArrayList<Float> respeck_gyro_ys;
    private ArrayList<Float> respeck_gyro_zs;

    private ArrayList<Float> thingy_acc_xs;
    private ArrayList<Float> thingy_acc_ys;
    private ArrayList<Float> thingy_acc_zs;


    public DataHandler(int bufferSize){
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

    public void feed(RESpeckLiveData respecklivedata){
        float acc_x = respecklivedata.getAccelX();
        float acc_y = respecklivedata.getAccelY() ;
        float acc_z = respecklivedata.getAccelZ();
        GyroscopeReading gyro = respecklivedata.getGyro();

        float gyro_x = gyro.getX();
        float gyro_y = gyro.getY();
        float gyro_z = gyro.getZ();

        respeck_acc_xs.add(acc_x);
        respeck_acc_ys.add(acc_y);
        respeck_acc_zs.add(acc_z);

        respeck_gyro_xs.add(gyro_x);
        respeck_gyro_ys.add(gyro_y);
        respeck_gyro_zs.add(gyro_z);


    }

    public void feed(ThingyLiveData thingylivedata){
        float acc_x = thingylivedata.getAccelX();
        float acc_y = thingylivedata.getAccelY() ;
        float acc_z = thingylivedata.getAccelZ();

        thingy_acc_xs.add(acc_x);
        thingy_acc_ys.add(acc_y);
        thingy_acc_zs.add(acc_z);

    }







}
