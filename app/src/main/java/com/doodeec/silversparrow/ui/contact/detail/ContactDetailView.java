package com.doodeec.silversparrow.ui.contact.detail;

import com.doodeec.silversparrow.base.BaseMvpView;

/**
 * @author Dusan Bartos
 */
public interface ContactDetailView extends BaseMvpView {

    void setPhone(String name);

    void setName(String description);

    void showEmptyMessage(boolean show);

    void showContact(boolean show);
}
