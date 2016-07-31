package com.doodeec.silversparrow.data.provider;

import com.doodeec.silversparrow.data.storage.DBHelper;
import com.j256.ormlite.dao.Dao;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Dao manipulation wrapper
 *
 * @author Dusan Bartos
 */
public class StorageManager {

    private final DBHelper helper;

    public StorageManager(DBHelper dbHelper) {
        this.helper = dbHelper;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean add(T object) throws Exception {
        return helper.getCachedDao((Class<T>) object.getClass()).create(object) == 1;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean addAll(List<T> objects) throws Exception {
        final Dao<T, Object> dao = helper.getCachedDao((Class<T>) objects.get(0).getClass());
        dao.callBatchTasks(() -> {
            for (T obj : objects) {
                dao.createOrUpdate(obj);
                Timber.d("saved/updated: %s", obj.toString());
            }
            return objects;
        });
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean delete(List<T> objects) throws Exception {
        if (objects == null || objects.isEmpty()) return true;
        return helper.getCachedDao((Class<T>) objects.get(0).getClass()).delete(objects) > 0;
    }

    public <T> Observable<List<T>> observe(Class<T> cls) {
        final Dao<T, Object> dao = helper.getCachedDao(cls);
        //lazy final variable is safe because unsubscribe will come only after subscribe is called
        final DaoHolder dh = new DaoHolder();
        final Action1<Subscriber<List<T>>> queryAction = (subscriber) -> {
            try {
                subscriber.onNext(dao.queryForAll());
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };

        return Observable.<List<T>>create(subscriber -> {
            dh.observer = () -> queryAction.call((Subscriber<List<T>>) subscriber);
            dao.registerObserver(dh.observer);
            queryAction.call((Subscriber<List<T>>) subscriber);
        }).doOnUnsubscribe(() -> dao.unregisterObserver(dh.observer));
    }

    //helper to overcome final variable lazy initialization
    private class DaoHolder {
        Dao.DaoObserver observer;
    }
}
