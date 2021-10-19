package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
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
                float prediction = inference();
                output.setText(Float.toString(prediction));
            }
        });

    }

    public float inference(){    //String s
        Float[][][] inputValue = new Float[1][50][6];
        for(int i=0; i<50; i++){
            for(int j=0; j<6; j++){
                inputValue[0][i][j] = (float) 0;
            }
        }
//        inputValue[0] = Float.valueOf(s);

        Float[] outputValue = new Float[1];
        tflite.run(inputValue, outputValue);
        float inferedValue = outputValue[0];
        return inferedValue;
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