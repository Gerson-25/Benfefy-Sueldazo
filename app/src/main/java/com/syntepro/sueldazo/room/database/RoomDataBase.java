package com.syntepro.sueldazo.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.syntepro.sueldazo.room.dao.AccessDao;
import com.syntepro.sueldazo.room.entity.Access;
import com.syntepro.sueldazo.room.entity.CountryUser;
import com.syntepro.sueldazo.room.entity.FirebaseToken;
import com.syntepro.sueldazo.room.entity.LocationServiceCounter;
import com.syntepro.sueldazo.room.entity.NavigationCategoryUser;
import com.syntepro.sueldazo.room.entity.NavigationCouponUser;
import com.syntepro.sueldazo.room.entity.NotificationUser;
import com.syntepro.sueldazo.room.entity.RemoteConfig;
import com.syntepro.sueldazo.room.entity.UserConfiguration;

@Database(entities = {Access.class, NotificationUser.class, CountryUser.class, RemoteConfig.class, NavigationCategoryUser.class, NavigationCouponUser.class, FirebaseToken.class, UserConfiguration.class, LocationServiceCounter.class}, version = 11, exportSchema = false)
public abstract class RoomDataBase extends RoomDatabase {

    private static RoomDataBase INSTANCE;

    public abstract AccessDao accessDao();

    public static RoomDataBase getRoomDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RoomDataBase.class, "appdb")
                    // allow queries on the main thread.
                    // Don't do this on a real app! See PersistenceBasicSample for an example.
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
