package com.doodeec.silversparrow.util.rx;

import com.doodeec.silversparrow.BuildConfig;

import rx.Observable;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public final class RxUtils {

    private static final boolean isDebug = BuildConfig.DEBUG;

    /**
     * Handles onError events in a stream
     * Does not emit onComplete after an error
     *
     * @param message message
     *
     * @return empty stream
     */
    public static <T> Observable.Transformer<T, T> handleErrorNever(String message) {
        return t -> t.onErrorResumeNext(e -> {
            if (isDebug) {
                Timber.e(e, message);
            } else {
                Timber.e("%s: %s", message, e.getMessage());
            }
            return Observable.never();
        });
    }

    /**
     * Handles onError events in a stream
     * Does emit onComplete right after an error
     *
     * @param message message
     *
     * @return empty stream
     */
    public static <T> Observable.Transformer<T, T> handleErrorEmpty(String message) {
        return t -> t.onErrorResumeNext(e -> {
            if (isDebug) {
                Timber.e(e, message);
            } else {
                Timber.e("%s: %s", message, e.getMessage());
            }
            return Observable.empty();
        });
    }
}
