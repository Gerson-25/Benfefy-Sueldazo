package com.syntepro.appbeneficiosbolivia.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.syntepro.appbeneficiosbolivia.room.dao.AccessDao;
import com.syntepro.appbeneficiosbolivia.room.entity.Access;
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser;
import com.syntepro.appbeneficiosbolivia.room.entity.FirebaseToken;
import com.syntepro.appbeneficiosbolivia.room.entity.LocationServiceCounter;
import com.syntepro.appbeneficiosbolivia.room.entity.NavigationCategoryUser;
import com.syntepro.appbeneficiosbolivia.room.entity.NavigationCouponUser;
import com.syntepro.appbeneficiosbolivia.room.entity.NotificationUser;
import com.syntepro.appbeneficiosbolivia.room.entity.RemoteConfig;
import com.syntepro.appbeneficiosbolivia.room.entity.UserConfiguration;

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
