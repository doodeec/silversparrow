package com.doodeec.silversparrow.ui.contact.detail;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.BaseActivity;
import com.doodeec.silversparrow.base.Layout;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.activity_contact_detail)
public class ContactDetailActivity extends BaseActivity {

    @Override protected void onResume() {
        super.onResume();
        enableToolbarBack();
    }
}
