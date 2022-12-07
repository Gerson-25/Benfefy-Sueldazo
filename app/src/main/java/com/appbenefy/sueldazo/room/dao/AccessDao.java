package com.appbenefy.sueldazo.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.appbenefy.sueldazo.room.entity.Access;
import com.appbenefy.sueldazo.room.entity.CountryUser;
import com.appbenefy.sueldazo.room.entity.FirebaseToken;
import com.appbenefy.sueldazo.room.entity.LocationServiceCounter;
import com.appbenefy.sueldazo.room.entity.NavigationCategoryUser;
import com.appbenefy.sueldazo.room.entity.NavigationCouponUser;
import com.appbenefy.sueldazo.room.entity.NotificationUser;
import com.appbenefy.sueldazo.room.entity.RemoteConfig;
import com.appbenefy.sueldazo.room.entity.UserConfiguration;

import java.util.List;

@Dao
public interface AccessDao {

    //Accesos Directos
    @Insert
    void addAccess(Access access);

    @Query("SELECT * FROM access")
    List<Access> getAccess();

    @Query("DELETE FROM access WHERE idCategoria = :id")
    void deleteAccess(String id);

    @Query("DELETE FROM access")
    void dropAccess();

    @Query("SELECT * FROM access WHERE idCategoria = :id")
    Access getAccessWithId(String id);

    //Notificaciones
    @Insert
    void addNotificationUser(NotificationUser notificationUser);

    @Query("SELECT * FROM notificationUser")
    NotificationUser getNotification();

    @Update
    void updateNotificationUser(NotificationUser notificationUser);

    @Query("DELETE FROM notificationUser")
    void dropNotifications();

    //Paises
    @Insert
    void addCountryUser(CountryUser countryUser);

    @Query("SELECT * FROM countryUser")
    CountryUser getCountry();

    @Query("DELETE FROM countryUser")
    void dropCountry();

    //RemoteConfig
    @Insert
    void addRemoteConfig(RemoteConfig remoteConfig);

    @Query("SELECT * FROM remoteConfig")
    RemoteConfig getConfig();

    @Query("DELETE FROM remoteConfig")
    void dropConfig();

    //NavigationCategoryUser
    @Insert
    void addNavigationCategoryUser(NavigationCategoryUser navigationCategoryUser);

    @Query("SELECT * FROM navigationCategoryUser")
    List<NavigationCategoryUser> getCategory();

    @Query("DELETE FROM navigationCategoryUser")
    void dropCategory();

    //NavigationCouponUser
    @Insert
    void addNavigationCouponUser(NavigationCouponUser navigationCouponUser);

    @Query("SELECT * FROM navigationCouponUser")
    List<NavigationCouponUser> getCoupon();

    @Query("DELETE FROM navigationCouponUser")
    void dropCoupon();

    //Token
    @Insert
    void addFirebaseToken(FirebaseToken firebaseToken);

    @Query("SELECT * FROM firebasetoken")
    FirebaseToken getToken();

    @Query("DELETE FROM firebaseToken")
    void dropToken();

    //UserConfiguration
    @Insert
    void addUserConfiguration(UserConfiguration userConfiguration);

    @Query("SELECT * FROM userConfiguration")
    UserConfiguration getConfiguration();

    @Query("DELETE FROM userConfiguration")
    void dropConfiguration();

    // LocationServiceCounter
    @Insert
    void addLocationServiceCounter(LocationServiceCounter locationServiceCounter);

    @Query("SELECT * FROM locationServiceCounter")
    LocationServiceCounter getLocationServiceCounter();

    @Query("UPDATE locationServiceCounter SET locationServiceCounter = :locationServiceCounter, notificationWSCounter = :notificationWSCounter WHERE id =:id")
    void updateLocationServiceCounter(int locationServiceCounter, int notificationWSCounter, int id);

}
