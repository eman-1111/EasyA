package eman.app.android.easya.utils;

import eman.app.android.easya.BuildConfig;

/**
 * Created by eman_ashour on 7/10/2016.
 */
public class Constants {

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";

    public static final String FIREBASE_COURSE_NAME= "course_NAME";
    public static final String FIREBASE_COURSE_LIST = "lesson_LIST";
    public static final String FIREBASE_LOCATION_Subject_LISTS = "subjectLists";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_USER_LISTS = "userLists";
    public static final String FIREBASE_LOCATION_USER_FRIENDS = "userFriends";
    public static final String FIREBASE_LOCATION_LISTS_SHARED_WITH = "sharedWith";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String PREF_USER_ACCOUNT_KEY = "accountKey";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_USER_LISTS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_LISTS;
    public static final String FIREBASE_URL_USER_FRIENDS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_FRIENDS;
    public static final String FIREBASE_URL_LISTS_SHARED_WITH = FIREBASE_URL + "/" + FIREBASE_LOCATION_LISTS_SHARED_WITH;

    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String KEY_LIST_NAME = "LIST_NAME";
    public static final String KEY_LAYOUT_RESOURCE = "LAYOUT_RESOURCE";
    public static final String KEY_LIST_ID = "LIST_ID";
    public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String KEY_LIST_ITEM_NAME = "ITEM_NAME";
    public static final String KEY_LIST_ITEM_ID = "LIST_ITEM_ID";
    public static final String KEY_PROVIDER = "PROVIDER";
    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_LIST_OWNER = "LIST_OWNER";
    public static final String KEY_GOOGLE_EMAIL = "GOOGLE_EMAIL";
    public static final String KEY_PREF_SORT_ORDER_LISTS = "PERF_SORT_ORDER_LISTS";
    public static final String KEY_SHARED_WITH_USERS = "SHARED_WITH_USERS";


    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String GOOGLE_PROVIDER = "google";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";


}
