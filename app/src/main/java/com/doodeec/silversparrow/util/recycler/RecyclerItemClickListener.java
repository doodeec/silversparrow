package com.doodeec.silversparrow.util.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Helper class used to determine single tap and long tap events on RecyclerView's items
 *
 * @author Dusan Bartos
 */
public final class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private final IOnItemClickListener listener;
    private final GestureDetector gestureDetector;
    private final ExtendedGestureListener gestureListener;

    public RecyclerItemClickListener(Context context, IOnItemClickListener listener) {
        this.listener = listener;
        gestureListener = new ExtendedGestureListener();
        gestureDetector = new GestureDetector(context, gestureListener);
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && listener != null) {
            gestureListener.setHelpers(childView, view.getChildAdapterPosition(childView));
            gestureDetector.onTouchEvent(e);
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    /**
     * Extended Gesture listener react for both item clicks and item long clicks
     */
    private class ExtendedGestureListener extends GestureDetector.SimpleOnGestureListener {
        private View view;
        private int position;

        public void setHelpers(View view, int position) {
            this.view = view;
            this.position = position;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            listener.onItemClick(view, position);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (listener instanceof IOnItemLongClickListener) {
                ((IOnItemLongClickListener) listener).onItemLongClick(view, position);
            }
        }
    }

    public interface IOnItemClickListener {
        /**
         * Fires when recycler view receives a single tap event on any item
         *
         * @param view     tapped view
         * @param position item position in the list
         */
        void onItemClick(View view, int position);
    }

    public interface IOnItemLongClickListener extends IOnItemClickListener {
        /**
         * Fires when recycler view receives a long tap event on item
         *
         * @param view     long tapped view
         * @param position item position in the list
         */
        void onItemLongClick(View view, int position);
    }
}
