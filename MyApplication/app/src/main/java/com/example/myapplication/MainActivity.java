package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private EditText input;
    private TextView output;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText) findViewById(R.id.input);
        output = (TextView) findViewById(R.id.output);
        Button button = (Button) findViewById(R.id.button);
        Button buttonDetect = (Button) findViewById(R.id.buttonDetect);

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
                Object[] results = inference();

                int class_index = (Integer) results[0];
                float confidence = (Float) results[1];
                Log.d("myTag", Integer.toString(class_index) + " , "+ Float.toString(confidence));

                String[] motion_list = {"Standing",
                        "Sitting bent forward",
                        "Walking at normal speed"};
                String motion = motion_list[class_index];

                output.setText(motion+", with confidence: "+Float.toString(confidence)); // Integer.toString(prediction)
            }
        });

    }

    public Object[] inference(){    //String s
        float[][][] inputValue = new float[1][50][6];
        for(int i=0; i<50; i++){
            for(int j=0; j<6; j++){
                inputValue[0][i][j] = (float) 0;
            }
        }
//        inputValue[0] = Float.valueOf(s);
        Log.d("myTag", Float.toString(inputValue[0][49][5]));

        float[][] outputValue = new float[1][3];
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
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("cnn.tflite");
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startoffSets = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffSets, declaredLength);
    }


}