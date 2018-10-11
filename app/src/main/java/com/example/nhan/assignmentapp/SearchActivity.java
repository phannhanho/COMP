package com.example.nhan.assignmentapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    static final String START_DATE = "START_DATE_STRING";
    static final String END_DATE = "END_DATE_STRING";
    static final String CAPTION_STRING = "CAPTION_STRING";
    EditText editTextStartDate;
    EditText editTextEndDate;
    EditText editTextCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextCaption = findViewById(R.id.editTextSearchCaption);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        super.onBackPressed();
    }


    public void buttonFilterClick(View view) {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        Intent data = new Intent();
        Date startDate;
        Date endDate;
        String caption;
        try {
            if (editTextStartDate.getText().length() > 0 && editTextEndDate.getText().length() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
                startDate = sdf.parse(editTextStartDate.getText().toString());
                endDate = sdf.parse(editTextEndDate.getText().toString());
                data.putExtra(START_DATE, startDate);
                data.putExtra(END_DATE, endDate);
            } else {
                if ((editTextStartDate.getText().length() > 0 && editTextEndDate.getText().length() == 0)
                        || (editTextStartDate.getText().length() == 0 && editTextEndDate.getText().length() > 0)) {
                    Toast.makeText(SearchActivity.this, "Start Date and End Date cannot both be empty.", Toast.LENGTH_LONG).show();
                }
            }

            if (editTextCaption.getText().length() > 0) {
                caption = editTextCaption.getText().toString();
                data.putExtra(CAPTION_STRING, caption);
            }
            setResult(RESULT_OK, data);
            finish();
        } catch (ParseException ex) {
            Toast.makeText(SearchActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
