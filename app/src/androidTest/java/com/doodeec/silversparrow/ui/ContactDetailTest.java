package com.doodeec.silversparrow.ui;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.ui.contact.detail.ContactDetailActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.BaseTestRunner;

/**
 * @author Dusan Bartos
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContactDetailTest extends BaseTestRunner<ContactDetailActivity> {
    public ContactDetailTest() {
        super(ContactDetailActivity.class);
    }

    @Test public void contactDetail_elementsShown() {
        setupFailureHandler(activity);

        isViewDisplayed(R.id.contact_phone);
        isViewDisplayed(R.id.contact_order_list);
    }
}
