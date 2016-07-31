package com.doodeec.silversparrow.ui;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.ui.contact.add.AddContactActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.BaseTestRunner;

/**
 * @author Dusan Bartos
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddContactTest extends BaseTestRunner<AddContactActivity> {
    public AddContactTest() {
        super(AddContactActivity.class);
    }

    @Test public void addContact_elementsShown() {
        setupFailureHandler(activity);

        isViewDisplayed(R.id.new_contact_name);
        isViewDisplayed(R.id.new_contact_phone);
        isViewDisplayed(R.id.new_contact_add_btn);
    }
}
