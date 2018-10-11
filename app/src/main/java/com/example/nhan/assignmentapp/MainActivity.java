package com.example.nhan.assignmentapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import com.example.nhan.assignmentapp.helper.Image;
import com.example.nhan.assignmentapp.helper.ImageDatabase;

public class MainActivity extends AppCompatActivity {
    static final String DATABASE_NAME = "DB";
    static final String TAG = "MainActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_FILTER = 2;
    private ImageView imageView;
    private TextView textViewTimestamp;
    private EditText editTextCaption;
    private Button buttonLeft;
    private Button buttonRight;
    private String mCurrentPhotoPath;
    private Date mCurrentDateTimestamp;
    private int mCurrentIndex;
    private List<Image> mGallery;
    public ImageDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textViewTimestamp = findViewById(R.id.textViewTimestamp);
        editTextCaption = findViewById(R.id.editTextCaption);
        buttonLeft = findViewById(R.id.buttonLeft);
        buttonRight = findViewById(R.id.buttonRight);

        try {
            database = new ImageDatabase();
            database.init(getApplicationContext(), DATABASE_NAME);
            database.loadState();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        mGallery = new ArrayList<>();
        mCurrentIndex = 0;

        editTextCaption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        database.updateImageCaption(mGallery.get(mCurrentIndex).getId(), editTextCaption.getText().toString());
                        updateSuccess();
                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    Image image = new Image(mCurrentDateTimestamp, mCurrentPhotoPath);
                    database.addImage(image);
                    insertSuccess(image);
                } catch (Exception ex) {
                    Log.d(TAG, ex.getMessage());
                }
            }
        } else if (requestCode == SEARCH_FILTER) {
            if (resultCode == RESULT_OK) {
                mGallery.clear();
                mCurrentIndex = 0;
                Date startDate = null;
                Date endDate = null;
                String caption = null;
                startDate = (Date) data.getSerializableExtra(SearchActivity.START_DATE);
                endDate = (Date) data.getSerializableExtra(SearchActivity.END_DATE);
                caption = data.getStringExtra(SearchActivity.CAPTION_STRING);
                if (startDate != null && endDate != null && caption != null) {
                    mGallery = database.getImagesByDateAndCaption(startDate, endDate, caption);
                } else if (startDate != null && endDate != null && caption == null) {
                    mGallery = database.getImagesByDate(startDate, endDate);
                } else if (startDate == null && endDate == null && caption != null) {
                    mGallery = database.getImagesByCaption(caption);
                } else {
                    mGallery = database.getImages();
                }
                retrieveSuccess();
            }
        }
    }


    public void buttonClickSnap(View view) {
        dispatchTakePictureIntent();
    }


    public void buttonClickLeft(View view) {
        if (mCurrentIndex > 0) {
            mCurrentIndex--;
            setPic();
            if (!buttonRight.isEnabled()) {
                buttonRight.setEnabled(true);
            }
        }
        if (mCurrentIndex == 0) {
            buttonLeft.setEnabled(false);
        }
    }


    public void buttonClickRight(View view) {
        if (mCurrentIndex < (mGallery.size() - 1)) {
            mCurrentIndex++;
            setPic();
            if (!buttonLeft.isEnabled()) {
                buttonLeft.setEnabled(true);
            }
        }
        if (mCurrentIndex == (mGallery.size() - 1)) {
            buttonRight.setEnabled(false);
        }
    }


    public void buttonClickFilter(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_FILTER);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), R.string.error_image_file_creation, Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.nhan.assignmentapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        mCurrentDateTimestamp = new Date();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(mCurrentDateTimestamp);
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void setPic() {
        if (mGallery.size() == 0) return;
        if (mCurrentIndex > mGallery.size()) return;
        if (mCurrentIndex < 0) return;

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mGallery.get(mCurrentIndex).getFilePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mGallery.get(mCurrentIndex).getFilePath(), bmOptions);
        imageView.setImageBitmap(bitmap);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        textViewTimestamp.setText(dateFormat.format(mGallery.get(mCurrentIndex).getTimestamp()));
        editTextCaption.setText(mGallery.get(mCurrentIndex).getCaption());
        editTextCaption.setEnabled(true);
    }


    private void insertSuccess(Image image) {
        mGallery.clear();
        galleryAddPic();
        mGallery.add(image);
        setPic();
    }


    private void retrieveSuccess() {
        mCurrentIndex = 0;
        if (mGallery.size() > 0) {
            setPic();
            if (mGallery.size() > 1) {
                buttonRight.setEnabled(true);
            } else {
                buttonRight.setEnabled(false);
                buttonLeft.setEnabled(false);
            }
        } else {
            textViewTimestamp.setText(R.string.timestamp);
            editTextCaption.setText("");
            imageView.setImageResource(android.R.color.black);
            buttonLeft.setEnabled(false);
            buttonRight.setEnabled(false);
        }
    }


    private void updateSuccess() {
        mGallery.get(mCurrentIndex).setCaption(editTextCaption.getText().toString());
    }
}
