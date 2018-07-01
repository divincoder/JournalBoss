package com.ofoegbuvgmail.journalboss.ui;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ofoegbuvgmail.journalboss.R;
import com.ofoegbuvgmail.journalboss.activity.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    @Test
    public void recyclerViewItemClickTest() {


        onView(withId(R.id.recipe_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }


    //Test that the recipe name test are displayed properly specifically item 0
    @Test
    public void itemContentDescriptionTest() {

        onView(withId(R.id.recipe_rv))
                .check(matches(atPosition(0, hasDescendant(withText("Nutella Pie")))));
    }
}
