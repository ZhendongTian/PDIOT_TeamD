package com.specknet.pdiotapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class SettingsActivity extends AppCompatActivity {

    // creating variables for our edit text and buttons.
    private EditText phoneInput, phoneInput2;
    private Button contactButton;
    private TextView contactDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initializing our edit text  and buttons.
        phoneInput = findViewById(R.id.phone_input);
        phoneInput2 = findViewById(R.id.phone_input2);
        contactButton = findViewById(R.id.contact_button);
        contactDetails = findViewById(R.id.contact_list);

        try{
            ParseUser currentUser = ParseUser.getCurrentUser();
            String contact = currentUser.getString("emConString");
            contactDetails.setText(contact);
        } catch (Exception e) {
        }


        // adding on click listener for our button.
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // on below line we are getting data from our edit text.
                String number1 = phoneInput.getText().toString();
                String number2 = phoneInput2.getText().toString();

                // checking if the entered text is empty or not.
                if (TextUtils.isEmpty(number1) && TextUtils.isEmpty(number2)) {
                    Toast.makeText(SettingsActivity.this, "Please enter a valid UK phone number in the specified format", Toast.LENGTH_SHORT).show();
                }
                else if (number1.length() != 11 || number2.length() != 11 ){
                    Toast.makeText(SettingsActivity.this, "Please enter a valid UK phone number in the specified format", Toast.LENGTH_SHORT).show();
                }

                else if (!(number1.equals(number2))) {
                    Toast.makeText(SettingsActivity.this, "Phone numbers do not match. Please try again.", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        registerContact(number1);
                    } catch (ParseException e) {
                    }
                }

            }
        });
    }

    private void registerContact(String number1) throws ParseException {
        // convert string to int

        int number = Integer.parseInt(number1);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Other attributes than "email" will remain unchanged!
            currentUser.put("emergencyContact", number);
            currentUser.put("emConString", number1);

            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Toast.makeText(this, "Successfully registered new contact!", Toast.LENGTH_SHORT).show();
                }else{
                    // Something went wrong while saving
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        TextView contactDetails = findViewById(R.id.contact_list);

        contactDetails.setText(currentUser.getString("emConString"));

    }
}