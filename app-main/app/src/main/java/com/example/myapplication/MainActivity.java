package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private EditText input;
    private TextView output;
    Interpreter tflite;
    int datapoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText) findViewById(R.id.input);
        output = (TextView) findViewById(R.id.output);
        Button button = (Button) findViewById(R.id.button);
        Button buttonDetect = (Button) findViewById(R.id.buttonDetect);
        Button buttonRead = (Button) findViewById(R.id.buttonRead);

        ArrayList<Respeck> respeckList = generateList();

        try {
            tflite = new Interpreter(loadModelFile());
        }catch (Exception e){
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = input.getText().toString();
                output.setText("Using file: "+ text);
                input.setText("");
            }
        });

        buttonDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                float prediction = inference(input.getText().toString());
                Object[] results = inference(respeckList);

                int class_index = (Integer) results[0];
                float confidence = (Float) results[1];
                Log.d("myTag", Integer.toString(class_index) + " , "+ Float.toString(confidence));

                String[] motion_list = {"Sitting",
                        "Sitting bent forward",
                        "Sitting bent backward",
                        "Standing",
                        "Lying down left",
                        "Lying down right",
                        "Lying down on stomach",
                        "Lying down on back",
                        "Walking at normal speed",
                        "Running",
                        "Climbing stairs",
                        "Descending stairs",
                        "Desk work",
                        "Movement",
                        "Falling on knees",
                        "Falling on the back",
                        "Falling on the left",
                        "Falling on the right"};
                String motion = motion_list[class_index];

                output.setText(motion+", with confidence: "+Float.toString(confidence)); // Integer.toString(prediction)
            }
        });


        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("datapoint "+ datapoint +"\n"+ respeckList.get(datapoint).getAcc_x() + " , "+respeckList.get(datapoint).getAcc_y() + " , "+
                        respeckList.get(datapoint).getAcc_z() + " , "+respeckList.get(datapoint).getGyro_x() + " , "+
                        respeckList.get(datapoint).getGyro_y() + " , "+respeckList.get(datapoint).getGyro_z() + " ! ");
                datapoint += 1;
                if(datapoint>= respeckList.size()){
                    datapoint=0;
                }

            }
        });

    }

    private ArrayList<Respeck> generateList() {
        ArrayList<Respeck> respeckList = new ArrayList();
        InputStream is = getResources().openRawResource(R.raw.lyingback);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");

                // Read the data and store it in the WellData POJO.
                Respeck respeck = new Respeck();
                respeck.setAcc_x(tokens[1]);
                respeck.setAcc_y(tokens[2]);
                respeck.setAcc_z(tokens[3]);
                respeck.setGyro_x(tokens[4]);
                respeck.setGyro_y(tokens[5]);
                respeck.setGyro_z(tokens[6]);
                respeckList.add(respeck);

                Log.d("MainActivity" ,"Just Created " + respeck);
                Log.d("myTag" ,"Size of respeck list " + respeckList.size());
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
        return respeckList;
    }

    public Object[] inference(ArrayList<Respeck> respeckList){    //String s
        float[][][] inputValue = new float[1][50][6];
        for(int i=0; i<50; i++){
            for(int j=0; j<6; j++){
                String respeckDataPoint = "";
//                Log.d("myTag", i+" , "+ j);
                switch (j){
                    case 0:
                        respeckDataPoint = respeckList.get(i+1).getAcc_x();
                        break;
                    case 1:
                        respeckDataPoint = respeckList.get(i+1).getAcc_y();
                        break;
                    case 2:
                        respeckDataPoint = respeckList.get(i+1).getAcc_z();
                        break;
                    case 3:
                        respeckDataPoint = respeckList.get(i+1).getGyro_x();
                        break;
                    case 4:
                        respeckDataPoint = respeckList.get(i+1).getGyro_y();
                        break;
                    case 5:
                        respeckDataPoint = respeckList.get(i+1).getGyro_z();
                        break;
                    default:
                        respeckDataPoint = "";
                        break;
                }

                inputValue[0][i][j] =  (float) Float.valueOf(respeckDataPoint)  ;//(float) 0;
            }
        }
//        inputValue[0] = Float.valueOf(s);
        Log.d("myTag", Float.toString(inputValue[0][49][5]));

        float[][] outputValue = new float[1][18];
        tflite.run(inputValue, outputValue);
        float inferedValue = outputValue[0][0];
        Log.d("myTag", Float.toString(outputValue[0][0]));
//        int index = argmax(outputValue[0]);
        //Obtained highest prediction
        Object[] results = argmax(outputValue[0]);

        return results;
    }

    public static Object[] argmax(float[] array){
        int best = -1;
        float best_confidence = 0.0f;
        for(int i = 0;i < array.length;i++){
            float value = array[i];
            if (value > best_confidence){
                best_confidence = value;
                best = i;
            }
        }
        return new Object[]{best,best_confidence};
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("cnn18.tflite");
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startoffSets = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffSets, declaredLength);
    }


}