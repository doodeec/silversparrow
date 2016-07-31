package com.doodeec.silversparrow.ui.contact.add;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.BaseActivity;
import com.doodeec.silversparrow.base.Layout;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.activity_add_contact)
public class AddContactActivity extends BaseActivity {

    @Override protected void onResume() {
        super.onResume();
        enableToolbarBack();
    }
}
