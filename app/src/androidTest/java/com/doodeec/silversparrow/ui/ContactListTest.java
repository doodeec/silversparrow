package com.doodeec.silversparrow.ui;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.ui.contact.list.ContactListActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.BaseTestRunner;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * @author Dusan Bartos
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContactListTest extends BaseTestRunner<ContactListActivity> {
    public ContactListTest() {
        super(ContactListActivity.class);
    }

    @Test public void contactList_elementsShown() {
        setupFailureHandler(activity);

        isViewDisplayed(R.id.contact_list);

        if (!isPortrait(activity)) {
            isViewDisplayed(R.id.contact_phone);
            isViewDisplayed(R.id.contact_order_list);
        }
    }

    @Test public void contactList_detailClick() {
        setupFailureHandler(activity);

        onView(withId(R.id.contact_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        if (isPortrait(activity)) {
            isViewDisplayed(R.id.contact_phone);
            isViewDisplayed(R.id.contact_order_list);
        }
    }
}
