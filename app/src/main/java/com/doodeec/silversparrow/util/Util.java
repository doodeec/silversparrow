package com.doodeec.silversparrow.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.doodeec.silversparrow.base.Layout;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public final class Util {

    private static final Map<Class, Integer> PATH_LAYOUT_CACHE = new LinkedHashMap<>();

    //GC sometimes does not release image references soon enough before OOM exception,
    //so this is a little helper which eliminates OOM a bit
    private static final int GC_HANDLER_DEBOUNCE = 1000;
    private static Handler gcHandler = new Handler();
    private static Runnable gcHandlerTask = () -> {
        Timber.v("Trying to free unused references | calling GC");
        System.gc();
    };

    private static DisplayMetrics displayMetrics;

    private static int getAnnotationValue(Object o, final Map<Class, Integer> cache, Class<?> clazz) {
        final Class pathType = o.getClass();
        Integer res = cache.get(pathType);
        if (res == null) {
            final Annotation annotation = pathType.getAnnotation(clazz);
            if (annotation != null) {
                try {
                    final Method m = annotation.getClass().getDeclaredMethod("value", (Class[]) null);
                    res = (Integer) m.invoke(annotation, (Object[]) null);
                    cache.put(pathType, res);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return res != null ? res : -1;
    }

    /**
     * Reads {@link Layout} annotation value of given object
     * @return layout resource if found, else -1
     */
    public static int getLayoutRes(Object o) {
        return getAnnotationValue(o, PATH_LAYOUT_CACHE, Layout.class);
    }

    private static int maxWidth(Context context) {
        if (displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getMetrics(displayMetrics);
        }

        return displayMetrics.widthPixels;
    }

    /**
     * Shorthand for attaching image from url to target view
     *
     * @param target      target view
     * @param sourceUrl   image url
     * @param placeholder placeholder drawable
     */
    public static void setImage(ImageView target, String sourceUrl, @DrawableRes int placeholder) {
        if (target == null) {
            Timber.e("Cannot attach image to empty target");
            return;
        }

        Picasso.with(target.getContext()).cancelRequest(target);

        final int targetPlaceholder = placeholder == -1 ? android.R.color.transparent : placeholder;
        if (TextUtils.isEmpty(sourceUrl)) {
//            Timber.d("Image url is empty, setting placeholder");
            target.setImageResource(placeholder);
            return;
        }

        Timber.d("Starting image loading: %s", sourceUrl);
        final int w = target.getWidth();
        final int h = target.getHeight();
        if (w > 0 && h > 0) {
            // target has both sides calculated, use view directly to fit image
            Picasso.with(target.getContext())
                    .load(Uri.parse(sourceUrl))
                    .placeholder(targetPlaceholder)
                    .fit()
                    .centerCrop()
                    .into(target);
        } else {
            // target is not measured completely yet, use a little trick to resize
            final int maxWidth;
            if (w > 0) {
                // use calculated width if available
                maxWidth = w;
            } else {
                // use screen width if target width not calculated yet
                maxWidth = maxWidth(target.getContext());
            }
            Picasso.with(target.getContext())
                    .load(sourceUrl)
                    .placeholder(targetPlaceholder)
                    .resize(maxWidth, maxWidth)
                    .centerCrop()
                    .into(target);
        }
    }

    /**
     * Releases reference to the image/icon used in ImageView
     * Internally initializes debounced GC event and cancels all pending Picasso requests for this
     * target
     *
     * @param target view
     */
    public static void releaseImage(ImageView target) {
        if (target == null) return;
        target.setImageDrawable(null);
        target.setTag(null);
        Picasso.with(target.getContext()).cancelRequest(target);
        // try to free remaining bitmap references
        initGc();
    }

    private static void initGc() {
        gcHandler.removeCallbacks(gcHandlerTask);
        gcHandler.postDelayed(gcHandlerTask, GC_HANDLER_DEBOUNCE);
    }

    public static Intent dialIntent(String phone) {
        try {
            final Uri phoneUri = Uri.parse("tel:" + phone);
            return new Intent(Intent.ACTION_DIAL).setData(phoneUri);
        } catch (Exception e) {
            return new Intent(Intent.ACTION_DIAL);
        }
    }
}
