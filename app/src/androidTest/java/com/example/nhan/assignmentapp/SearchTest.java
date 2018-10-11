package com.example.nhan.assignmentapp;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.example.nhan.assignmentapp.helper.Image;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;



import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SearchTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    static Context appContext;
    static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
    static Image IMAGE_1, IMAGE_2, IMAGE_3;

    @Before
    public void Init() {
        appContext = InstrumentationRegistry.getTargetContext();

        try {
            IMAGE_1 = new Image(1, sdf.parse("2018-01-01 08:00:00"), "hello", "file1.jpg");
            IMAGE_2 = new Image(2, sdf.parse("2018-01-02 08:00:00"), "goodbye", "file2.jpg");
            IMAGE_3 = new Image(3, sdf.parse("2018-01-03 08:00:00"), "test", "file3.jpg");
            mActivityRule.getActivity().database.addImage(IMAGE_1);
            mActivityRule.getActivity().database.addImage(IMAGE_2);
            mActivityRule.getActivity().database.addImage(IMAGE_3);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void filter() {
        onView(withId(R.id.buttonFilter)).perform(click());
        onView(withId(R.id.buttonSearchFilter)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.textViewTimestamp)).check(matches(withText(sdf.format(IMAGE_1.getTimestamp()))));
        onView(withId(R.id.editTextCaption)).check(matches(withText(IMAGE_1.getCaption())));
        onView(withId(R.id.buttonRight)).perform(click());
        onView(withId(R.id.textViewTimestamp)).check(matches(withText(sdf.format(IMAGE_2.getTimestamp()))));
        onView(withId(R.id.editTextCaption)).check(matches(withText(IMAGE_2.getCaption())));
        onView(withId(R.id.buttonRight)).perform(click());
        onView(withId(R.id.textViewTimestamp)).check(matches(withText(sdf.format(IMAGE_3.getTimestamp()))));
        onView(withId(R.id.editTextCaption)).check(matches(withText(IMAGE_3.getCaption())));
    }


    @Test
    public void filterCaption() {
        onView(withId(R.id.buttonFilter)).perform(click());
        onView(withId(R.id.editTextSearchCaption)).perform(typeText(IMAGE_3.getCaption()), closeSoftKeyboard());
        onView(withId(R.id.buttonSearchFilter)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.textViewTimestamp)).check(matches(withText(sdf.format(IMAGE_3.getTimestamp()))));
        onView(withId(R.id.editTextCaption)).check(matches(withText(IMAGE_3.getCaption())));
    }


    @Test
    public void filterDate() {
        onView(withId(R.id.buttonFilter)).perform(click());
        onView(withId(R.id.editTextStartDate)).perform(typeText("2018-01-02 00:00:00"), closeSoftKeyboard());
        onView(withId(R.id.editTextEndDate)).perform(typeText("2018-01-02 23:59:59"), closeSoftKeyboard());
        onView(withId(R.id.buttonSearchFilter)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.textViewTimestamp)).check(matches(withText(sdf.format(IMAGE_2.getTimestamp()))));
        onView(withId(R.id.editTextCaption)).check(matches(withText(IMAGE_2.getCaption())));
    }


    @Test
    public void filterDateAndCaption() {
        onView(withId(R.id.buttonFilter)).perform(click());
        onView(withId(R.id.editTextStartDate)).perform(typeText("2018-01-01 00:00:00"), closeSoftKeyboard());
        onView(withId(R.id.editTextEndDate)).perform(typeText("2018-01-01 23:59:59"), closeSoftKeyboard());
        onView(withId(R.id.editTextSearchCaption)).perform(typeText(IMAGE_1.getCaption()), closeSoftKeyboard());
        onView(withId(R.id.buttonSearchFilter)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.textViewTimestamp)).check(matches(withText(sdf.format(IMAGE_1.getTimestamp()))));
        onView(withId(R.id.editTextCaption)).check(matches(withText(IMAGE_1.getCaption())));
    }
}
