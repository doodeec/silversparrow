package com.doodeec.silversparrow.data.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.doodeec.silversparrow.data.model.Contact;
import com.doodeec.silversparrow.data.model.Order;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "com.doodeec.silversparrow.db";

    private final Map<Class, Dao<?, Object>> daoMap = new HashMap<>();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Timber.d("Creating Contacts table");
            TableUtils.createTable(connectionSource, Contact.class);
            Timber.d("Creating Orders table");
            TableUtils.createTable(connectionSource, Order.class);
        } catch (java.sql.SQLException e) {
            Timber.e(e, "Error creating DB");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    //TODO add migration when upgrading DB schema
                    break;

                default:
                    throw new IllegalStateException("onUpgrade() with unknown newVersion" + newVersion);
            }
            upgradeTo++;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Dao<T, Object> getCachedDao(Class<T> clazz) {
        if (!daoMap.containsKey(clazz)) {
            try {
                final Dao<T, Object> dao = getDao(clazz);
                daoMap.put(clazz, dao);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return (Dao<T, Object>) daoMap.get(clazz);
    }
}
