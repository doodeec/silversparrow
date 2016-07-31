package com.doodeec.silversparrow.ui.contact.list;

import android.view.Menu;
import android.view.MenuItem;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.BaseActivity;
import com.doodeec.silversparrow.base.Layout;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.activity_contact_list)
public class ContactListActivity extends BaseActivity {

    private ContactListFragment fragment;

    @Override protected void onResume() {
        super.onResume();
        fragment = (ContactListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contact_list);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        if (fragment != null) {
            handled = fragment.onMenuItemClick(item.getItemId());
        }
        return handled || super.onOptionsItemSelected(item);
    }
}
