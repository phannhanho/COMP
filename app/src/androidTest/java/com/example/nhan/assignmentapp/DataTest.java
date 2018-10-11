package com.example.nhan.assignmentapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.example.nhan.assignmentapp.helper.Image;
import com.example.nhan.assignmentapp.helper.ImageDatabase;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
public class DataTest {
    static Context appContext;
    static final String DATABASE_NAME = "DB";
    static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
    static Image IMAGE_1, IMAGE_2, IMAGE_3;
    static ImageDatabase db;

    @BeforeClass
    public static void Init() {
        appContext = InstrumentationRegistry.getTargetContext();
        db = new ImageDatabase();
        db.init(appContext, DATABASE_NAME);
        try {
            IMAGE_1 = new Image(1, sdf.parse("2018-01-01 08:00:00"), "hello", "file1.jpg");
            IMAGE_2 = new Image(2, sdf.parse("2018-01-02 08:00:00"), "goodbye", "file2.jpg");
            IMAGE_3 = new Image(3, sdf.parse("2018-01-03 08:00:00"), "test", "file3.jpg");
            db.addImage(IMAGE_1);
            db.addImage(IMAGE_2);
            db.addImage(IMAGE_3);
            db.saveState();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void save() {
        try {
            db.saveState();
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void load() {
        try {
            db.loadState();
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void filterDate() {
        try {
            List<Image> images = db.getImagesByDate(sdf.parse("2018-01-01 00:00:00"), sdf.parse("2018-01-02 23:59:00"));
            assertEquals(images.size(), 2);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void filterCaption() {
        try {
            List<Image> images = db.getImagesByCaption("test");
            assertEquals(images.size(), 1);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void filterDateAndCaption() {
        try {
            List<Image> images = db.getImagesByDateAndCaption(sdf.parse("2018-01-01 00:00:00"), sdf.parse("2018-01-03 23:59:00"), "hello");
            assertEquals(images.size(), 1);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}